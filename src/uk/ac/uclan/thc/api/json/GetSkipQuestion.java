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
import uk.ac.uclan.thc.data.QuestionFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.Category;
import uk.ac.uclan.thc.model.Question;
import uk.ac.uclan.thc.model.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Skips the current question in the current session. This action costs -5 points.
 *
 * User: Nearchos Paspallis
 * Date: 17/Dec/13
 * Time: 22:15
 */
public class GetSkipQuestion extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*"); // todo disable this (in all JSON requests)?
        final PrintWriter printWriter = response.getWriter();

        final String code = request.getParameter("code");
        final String sessionUUID = request.getParameter("session");

        if(sessionUUID == null || sessionUUID.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Invalid or missing parameters", "Valid parameter list 'skipQuestion?session=...'"));
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
                    if(correctAnswer.equals("teamChallenge"))
                    {
                        // ignore reply builder, and output the error status/message and terminate
                        printWriter.println(Protocol.getJsonStatus("Mandatory question", "This specific question cannot be skipped"));
                    }
                    else
                    {
                        final boolean hasMoreQuestions = SessionFactory.updateScoreAndSkipSessionToNextQuestion(sessionUUID);
                        final Session updatedSession = SessionFactory.getSession(sessionUUID);
                        assert updatedSession != null;
                        final long score = updatedSession.getScore();

                        String reply = "{" + EOL +
                                "  \"status\": \"OK\"" + "," + EOL + // OK status
                                "  \"hasMoreQuestions\": " + hasMoreQuestions + "," + EOL + // has more questions?
                                "  \"score\": " + score + EOL + // score
                                "}" + EOL;

                        printWriter.println(reply); // normal JSON output
                    }
                }
            }
        }
    }
}