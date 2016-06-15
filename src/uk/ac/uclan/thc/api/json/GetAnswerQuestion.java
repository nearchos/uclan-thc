/*
 * This file is part of UCLan-THC server.
 *
 *     UCLan-THC server is free software: you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     UCLan-THC server is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.uclan.thc.api.json;

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.data.LocationFingerprintFactory;
import uk.ac.uclan.thc.data.QuestionFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.Category;
import uk.ac.uclan.thc.model.LocationFingerprint;
import uk.ac.uclan.thc.model.Question;
import uk.ac.uclan.thc.model.Session;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Submits a proposed answer for the current question. A correct answer is rewarded with +10 points.
 *
 * User: Nearchos Paspallis
 * Date: 17/12/13
 * Time: 22:21
 */
public class GetAnswerQuestion extends HttpServlet
{
    public static final Logger log = Logger.getLogger(GetAnswerQuestion.class.getCanonicalName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*"); // todo disable this (in all JSON requests)?
        final PrintWriter printWriter = response.getWriter();

        // if magic is specified and equals {@link MAGIC}, then show inactive categories too - used by the examiner
        final String code = request.getParameter("code");
        final String sessionUUID = request.getParameter("session");
        final String answer = request.getParameter("answer");

        if(sessionUUID == null || sessionUUID.isEmpty() || answer == null || answer.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Invalid or missing parameters", "Valid parameter list 'answerQuestion?answer=...&session=...'"));
        }
        else
        {
            final Session session = SessionFactory.getSession(sessionUUID);
            if(session == null)
            {
                // ignore reply builder, and output the error status/message and terminate
                printWriter.println(Protocol.getJsonStatus("Unknown session", "The specified session ID could not be found"));
            }
            else
            {
                final Category category = CategoryFactory.getCategory(session.getCategoryUUID());
                if(category != null && !category.isActiveNow() && code != null && code.equals(category.getCode()))
                {
                    // ignore reply builder, and output the error status/message and terminate
                    printWriter.println(Protocol.getJsonStatus("Inactive category", "The specified category is not active"));
                }
                else
                {
                    final String currentQuestionUUID = session.getCurrentQuestionUUID();
                    final Question currentQuestion = QuestionFactory.getQuestion(currentQuestionUUID);
                    assert currentQuestion != null;
                    final String correctAnswer = currentQuestion.getCorrectAnswer().trim();

                    final StringBuilder reply = new StringBuilder("{").append(EOL);
                    reply.append("  \"status\": \"OK\"").append(",").append(EOL); // OK status

                    final String feedback;

                    // first check if the answer is correct
                    if(!checkAnswer(answer, correctAnswer, session))
                    {
                        SessionFactory.updateScoreAndKeepSessionToSameQuestion(sessionUUID, currentQuestion.getWrongScore());
                        feedback = "incorrect";
                    }
                    else // answer is correct
                    {
                        // check the location
                        final LocationFingerprint lastLocationFingerprint = LocationFingerprintFactory.getLastLocationFingerprintBySessionUUID(sessionUUID);
                        if(!currentQuestion.isCorrectLocation(lastLocationFingerprint))
                        {
                            SessionFactory.updateScoreAndKeepSessionToSameQuestion(sessionUUID, currentQuestion.getWrongScore());
                            feedback = "unknown or incorrect location";
                        }
                        else
                        {
                            // correct answer and location. check if this was the last question or not
                            final boolean hasMoreQuestions = SessionFactory.updateScoreAndProgressSessionToNextQuestion(sessionUUID, currentQuestion.getCorrectScore());
                            if(hasMoreQuestions) // unfinished
                            {
                                feedback = "correct,unfinished";
                            }
                            else // finished
                            {
                                feedback = "correct,finished";
                                final Session finishedSession = SessionFactory.getSession(sessionUUID);
                                if(category != null && finishedSession != null) {
                                    sendEmail(finishedSession.getPlayerName(), finishedSession.getName1(), finishedSession.getEmail1(), category.getName(), finishedSession.getScore(), finishedSession.getFinishTime());
                                }
                            }
                        }
                    }

                    reply.append("  \"feedback\": \"").append(feedback).append("\"").append(EOL); // OK status
                    reply.append("}").append(EOL);

                    printWriter.println(reply.toString()); // normal JSON output
                }
            }
        }
    }

    private boolean checkAnswer(final String givenAnswer, final String correctAnswer, final Session session)
    {
        switch (correctAnswer) {
            case "parseEmail":
                final String sessionUUID = session.getUUID();
                return givenAnswer.equals(sessionUUID.substring(sessionUUID.length() - 4));
            case "teamChallenge":
                final String playerName = session.getPlayerName();
                final Category category = CategoryFactory.getCategory(session.getCategoryUUID());
                if(category != null) {
                    String code = Integer.toHexString((playerName + category.getCode()).hashCode());
                    code = code.substring(code.length() > 4 ? code.length() - 4 : 0);
                    return givenAnswer.equals(code);
                }
            default:
                return givenAnswer.equalsIgnoreCase(correctAnswer);
        }
    }

    public static final String FROM_EMAIL_ADDRESS = "robot@codecyprus.org";
    public static final String FROM_NAME = "The robot at Code Cyprus";

    private void sendEmail(final String teamName, final String name1, final String email1, final String categoryName, final long score, final long finishTime)
    {
        if(name1 == null || name1.isEmpty() || email1 == null || email1.isEmpty()) {
            log.warning("Could not send email. Name and/or Email were empty. Name1: " + name1 + ", Email1: " + email1);
            return;
        }

        int ftHours         = (int) (finishTime / (60 * 60 * 1000));
        int ftMinutes       = (int) ((finishTime - ftHours * 60 * 60 * 1000) / (60 * 1000));
        int ftSeconds       = (int) ((finishTime - ftHours * 60 * 60 * 1000 - ftMinutes * 60 * 1000) / 1000);
        int ftMilliseconds  = (int) ((finishTime - ftHours * 60 * 60 * 1000 - ftMinutes * 60 * 1000) / 1000);
        final javax.mail.Session session = javax.mail.Session.getDefaultInstance(new Properties(), null);
        try
        {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL_ADDRESS, FROM_NAME, "utf-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email1, name1));
            message.setSubject(categoryName + " - Congratulations on finishing the treasure hunt!");
            message.setText("Well done! We hope you enjoyed the Treasure Hunt: "  + categoryName + "."
                            + "\nHere are some details about your performance:"
                            + "\n"
                            + "\n  Team name: " + teamName
                            + "\n  Name1: " + name1
                            + "\n  Email1: " + email1
                            + "\n----------------------------------------------------"
                            + "\n  Competition: " + categoryName
                            + "\n----------------------------------------------------"
                            + "\n  Score: " + score
                            + "\n  Finish time: " + ftHours + "h" + ftMinutes + "m" + ftSeconds + "s:" + ftMilliseconds + "ms"
                            + "\n----------------------------------------------------"
                            + "\n"
                            + "\nHope you enjoyed " + categoryName
                            + "\n"
                            + "\nYour friend,"
                            + "\n"
                            + "\nThe robot at Code Cyprus"
            );
            Transport.send(message);
        }
        catch (MessagingException | UnsupportedEncodingException e)
        {
            log.severe(e.getMessage());
        }
    }
}