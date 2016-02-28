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

package uk.ac.uclan.thc.api.csv;

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.QuestionFactory;
import uk.ac.uclan.thc.data.SessionFactory;
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
 * Retrieves the current question for the specified session. The returned data include the ID of the question, the
 * text of the question, and whether the question requires a lat/lng pair to be answered.
 *
 * <p>Parameters</p>
 * <ul>
 * <li>URL: '/api/csv/currentQuestion'.</li>
 * <li>session: the session ID, typically retrieved after calling {@link GetStartQuiz}</li>
 * </ul>
 *
 * Possible outcomes:
 * <ul>
 *     <li>
 *         When: Everything OK
 *         Status: "OK", Message: ""
 *         Second line: the question text for the current question (possibly HTML formatted)
 *         Third line: Whether the question is location-dependent (true|false)
 *         Example:
 *         <code>
 *         <br/>OK,
 *         <br/>What is the value of &lt;b&gt;1+1&lt;/b&gt;?
 *         <br/>false
 *         </code>
 *     </li>
 *     <li>
 *         When: There are no more questions to be answered (could be a new session, but an empty category)
 *         Status: "Finished session", Message: "The specified session has no more questions"
 *         Example:
 *         <code>
 *         <br/>Finished session,The specified session has no more questions
 *         </code>
 *     </li>
 *     <li>
 *         When one or more parameters are missing
 *         Status: "Invalid or missing parameters", Message: "Valid parameter list 'currentQuestion?session=...'"
 *         Example:
 *         <code>
 *         <br/>Invalid or missing parameters,Valid parameter list 'currentQuestion?session=...'
 *         </code>
 *     </li>
 *     <li>
 *         When the session is unknown
 *         Status: "Unknown session", Message: "The specified session ID could not be found"
 *         Example:
 *         <code>
 *         <br/>Unknown session,The specified session ID could not be found
 *         </code>
 *     </li>
 * </ul>
 *
 * User: Nearchos Paspallis
 * Date: 26/09/13
 * Time: 22:37
 */
public class GetCurrentQuestion extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String sessionUUID = request.getParameter("session");

        if(sessionUUID == null)
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getCsvStatus("Invalid or missing parameters", "Valid parameter list 'currentQuestion?session=...'"));
        }
        else
        {
            final Session session = SessionFactory.getSession(sessionUUID);

            if(session == null)
            {
                // ignore reply builder, and output the error status/message and terminate
                printWriter.println(Protocol.getCsvStatus("Unknown session", "The specified session ID could not be found"));
            }
            else
            {
                final String currentQuestionUUID = session.getCurrentQuestionUUID();
                if(currentQuestionUUID.isEmpty())
                {
                    // ignore reply builder, and output the error status/message and terminate
                    printWriter.println(Protocol.getCsvStatus("Finished session", "The specified session has no more questions"));
                }
                else
                {
                    final Question question = QuestionFactory.getQuestion(currentQuestionUUID);

                    final StringBuilder reply = new StringBuilder();
                    reply.append(Protocol.getCsvStatus("OK", "")).append(EOL); // OK status
                    reply.append(question.getText()).append(EOL).append(question.isLocationRelevant()).append(EOL);

                    printWriter.println(reply.toString()); // normal CSV output
                }
            }
        }
    }
}