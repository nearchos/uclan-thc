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

import io.ably.lib.rest.AblyRest;
import io.ably.lib.rest.Channel;
import io.ably.lib.types.AblyException;
import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.data.ParameterFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.Category;
import uk.ac.uclan.thc.model.Parameter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Initiates a new quiz session for the specified category. The session is automatically created if no session is
 * already available for the given parameters combination.
 *
 * User: Nearchos Paspallis
 * Date: 17/12/13
 * Time: 21:49
 */
public class GetStartQuiz extends HttpServlet
{
    private static final Logger log = Logger.getLogger(GetStartQuiz.class.getCanonicalName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*"); // todo disable this (in all JSON requests)?
        final PrintWriter printWriter = response.getWriter();

        final String code = request.getParameter("code"); // undocumented parameter; used only for testing a category before it becomes active

        final String playerName = request.getParameter("playerName");
        final String appID = request.getParameter("appID");
        final String categoryUUID = request.getParameter("categoryUUID");
        final String teamEmail = request.getParameter("teamEmail");
        final String name1 = request.getParameter("name1");
        final String email1 = request.getParameter("email1");
        final String name2 = request.getParameter("name2");
        final String installationID = request.getParameter("installationID");
        final String genderS = request.getParameter("gender");
        final String gender = "m".equalsIgnoreCase(genderS) ? "male" : "f".equalsIgnoreCase(genderS) ? "female" : "unknown";

        if(playerName == null || playerName.isEmpty() || appID == null || appID.isEmpty() || categoryUUID == null || categoryUUID.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Invalid or missing parameters", "Valid parameter list 'startQuiz?playerName=...&appID=...&categoryUUID=...'"));
        }
        else
        {
            final Category category = CategoryFactory.getCategory(categoryUUID);
            if(category == null)
            {
                // ignore reply builder, and output the error status/message and terminate
                printWriter.println(Protocol.getJsonStatus("Unknown category ID", "The specified category ID could not be found"));
            }
            else
            {
                final boolean showInactive = code != null && code.equals(category.getCode());
                if(!category.isActiveNow() && !showInactive)
                {
                    // ignore reply builder, and output the error status/message and terminate
                    printWriter.println(Protocol.getJsonStatus("Inactive category", "The specified category is not active right now"));
                }
                else
                {
                    final String sessionUUID = SessionFactory.getOrCreateSession(
                            playerName,
                            appID,
                            categoryUUID,
                            name1,
                            teamEmail == null || teamEmail.isEmpty() ? email1 : teamEmail,
                            gender);

                    if(sessionUUID == null)
                    {
                        // report that the given playerName was already used
                        printWriter.println(Protocol.getJsonStatus("Invalid Team Name", "The specified Team Name is already in use (try a different one)"));
                    }
                    else
                    {
                        // everything ok

                        // first send an email for book-keeping
                        if(name1 != null && !name1.isEmpty()) {
//                            sendEmail(category.getCreatedBy(), category.getName(), playerName, teamEmail, appID, categoryUUID, name1, name2, request.getRemoteAddr(), installationID);
                        }

                        // next send an email to the player
                        final String secret = sessionUUID.substring(sessionUUID.length() - 4);
//                        sendEmail(playerName, name1, email1, category, secret);

                        // ably push
                        try {
                            double lat = 0d;
                            double lng = 0d;

                            final Parameter parameter = ParameterFactory.getParameter("ABLY_PRIVATE_KEY");
                            if(parameter != null) {
                                final String ablyKey = parameter.getValue();
                                final AblyRest ably = new AblyRest(ablyKey);
                                final Channel channel = ably.channels.get("category-" + categoryUUID);
                                final String json = "  {" + EOL +
                                        "    \"uuid\": \"" + sessionUUID + "\"," + EOL +
                                        "    \"appID\": \"" + appID + "\"," + EOL +
                                        "    \"playerName\": \"" + playerName + "\"," + EOL +
                                        "    \"score\": " + 0 + "," + EOL +
                                        "    \"finishTime\": " + 0 + "," + EOL +
                                        "    \"lat\": " + lat + "," + EOL +
                                        "    \"lng\": " + lng + "" + EOL +
                                        "  }" + EOL;
                                io.ably.lib.types.Message[] messages = new io.ably.lib.types.Message[]{new io.ably.lib.types.Message("new_session", json)};
                                channel.publish(messages);
                            }
                        } catch (AblyException ae) {
                            log.severe("Ably error: " + ae.errorInfo);
                        }

                        // finally prepare and send the reply
                        String reply = "{" + EOL +
                                "  \"status\": \"OK\"" + "," + EOL + // OK status
                                "  \"sessionUUID\": \"" + sessionUUID + "\"" + EOL + // OK status
                                "}" + EOL;

                        printWriter.println(reply); // normal JSON output
                    }
                }
            }
        }
    }

//    private void sendEmail(final String categoryCreatedByEmail, final String categoryName,
//                           final String teamName, final String appID, final String categoryUUID, final String name1,
//                           final String email1, final String name2, final String email2,
//                           final String senderIP, final String installationID)
//    {
//        final Session session = Session.getDefaultInstance(new Properties(), null);
//        try
//        {
//            final Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(categoryCreatedByEmail, name1, "utf-8"));
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(categoryCreatedByEmail, categoryName));
//            message.setSubject("Treasure Hunt - " + appID + " from: " + email1);
//            message.setText(
//                    "Competition entry: "
//                            + "\n----------------------------------------"
//                            + "\n  Team name: " + teamName
//                            + "\n  Name1: " + name1
//                            + "\n  Email1: " + email1
//                            + "\n  Name2: " + name2
//                            + "\n  Email2: " + email2
//                            + "\n----------------------------------------"
//                            + "\n  App id: " + appID
//                            + "\n  Category UUID: " + categoryUUID
//                            + "\n----------------------------------------"
//                            + "\n  Submitted (UTC): " + new Date()
//                            + "\n  Installation ID: " + installationID
//                            + "\n  Sender IP: " + senderIP
//                            + "\n----------------------------------------"
//            );
//            Transport.send(message);
//        }
//        catch (AddressException ae)
//        {
//            log.severe(ae.getMessage());
//        }
//        catch (MessagingException me)
//        {
//            log.severe(me.getMessage());
//        }
//        catch (UnsupportedEncodingException uee)
//        {
//            log.severe(uee.getMessage());
//        }
//    }

//    private void sendEmail(final String teamName, final String name1, final String email1, final Category category, final String secret)
//    {
//        final Session session = Session.getDefaultInstance(new Properties(), null);
//        try
//        {
//            final Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(GetAnswerQuestion.FROM_EMAIL_ADDRESS, GetAnswerQuestion.FROM_NAME, "utf-8"));
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email1, name1));
//            message.setSubject("Welcome to the Code Cyprus treasure hunt!");
//            message.setText(
//                    "Ahoy " + name1 + ","
//                            + "\n"
//                            + "\nWelcome to the treasure hunt for '" + category.getName() + "'."
//                            + "\n"
//                            + "\nYour code is: " + secret
//                            + "\n"
//                            + "\nNow do not waste any more time, go find the treasure! I will email you again when it is time to split the loot."
//                            + "\n"
//                            + "\nYour friend,"
//                            + "\n"
//                            + "\nThe robot at Code Cyprus"
//            );
//            Transport.send(message);
//        }
//        catch (AddressException ae)
//        {
//            log.severe(ae.getMessage());
//        }
//        catch (MessagingException me)
//        {
//            log.severe(me.getMessage());
//        }
//        catch (UnsupportedEncodingException uee)
//        {
//            log.severe(uee.getMessage());
//        }
//    }
}