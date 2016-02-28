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
import uk.ac.uclan.thc.data.SessionFactory;
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
 * <p>Parameters</p>
 * <ul>
 * <li>URL: '/api/csv/answerQuestion'.</li>
 * <li>answer: the proposed answer</li>
 * <li>session: the session ID, typically retrieved after calling {@link GetStartQuiz}</li>
 * </ul>
 *
 * Possible outcomes:
 * <ul>
 *     <li>
 *         When: Skipped & More questions available
 *         Status: "OK", Message: ""
 *         Followed by: "skipped,unfinished"
 *         Example:
 *         <code>
 *         <br/>OK,
 *         <br/>skipped,unfinished
 *         </code>
 *     </li>
 *     <li>
 *         When: Skipped & No more questions available (finished all questions in category)
 *         Status: "OK", Message: ""
 *         Followed by: "skipped,finished"
 *         Example:
 *         <code>
 *         <br/>OK,
 *         <br/>skipped,finished
 *         </code>
 *     </li>
 *     <li>
 *         When one or more parameters are missing
 *         Status: "Invalid or missing parameters", Message: "Valid parameter list 'skipQuestion?session=...'"
 *         Example:
 *         <code>
 *         <br/>Invalid or missing parameters,Valid parameter list 'skipQuestion?session=...'
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
 * Date: 6/Oct/13
 * Time: 13:05
 */
public class GetSkipQuestion extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        // if magic is specified and equals {@link MAGIC}, then show inactive categories too - used by the examiner
        final String sessionUUID = request.getParameter("session");

        if(sessionUUID == null || sessionUUID.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getCsvStatus("Invalid or missing parameters", "Valid parameter list 'skipQuestion?session=...'"));
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
                final StringBuilder reply = new StringBuilder();
                reply.append(Protocol.getCsvStatus("OK", "")).append(EOL); // OK status

                final boolean hasMoreQuestions = SessionFactory.updateScoreAndSkipSessionToNextQuestion(sessionUUID);
                if(hasMoreQuestions) // unfinished
                {
                    reply.append("skipped,unfinished");
                }
                else // finished
                {
                    reply.append("skipped,finished");
                }

                printWriter.println(reply.toString()); // normal CSV output
            }
        }
    }
}