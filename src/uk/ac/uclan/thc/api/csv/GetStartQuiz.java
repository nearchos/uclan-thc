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
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.data.QuestionFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Initiates a new quiz session for the specified category. The session is automatically created if no session is
 * already available for the given parameters combination.
 *
 * <p>Parameters</p>
 * <ul>
 * <li>URL: '/api/csv/startQuiz'.</li>
 * <li>playerName: so it can appear on the leader board</li>
 * <li>appID: typically, the name of your team</li>
 * <li>categoryUUID: the UUID of a valid, active category (you can get this by using '/api/csv/categories', see {@link GetCategories}).</li>
 * </ul>
 *
 * Possible outcomes:
 * <ul>
 *     <li>
 *         When: Everything OK
 *         Status: "OK", Message: ""
 *         Second line: the 'session' ID to be used in subsequent calls (e.g. in {@link GetCurrentQuestion} or
 *         {@link GetUpdateLocation}
 *         Example:
 *         <code>
 *         <br/>OK,
 *         <br/>agtzfnVjbGFuLXRoY3IVCxIIQ2F0ZWdvcnkYgICAgIC6hwkM
 *         </code>
 *     </li>
 *     <li>
 *         When one or more parameters are missing
 *         Status: "Invalid or missing parameters", Message: "Valid parameter list 'startQuiz?playerName=...&appID=...&categoryUUID=...'"
 *         Example:
 *         <code>
 *         <br/>Invalid or missing parameters,Valid parameter list 'startQuiz?playerName=...&appID=...&categoryUUID=...'
 *         </code>
 *     </li>
 *     <li>
 *         When the category is unknown
 *         Status: "Unknown category ID", Message: "The specified category ID could not be found"
 *         Example:
 *         <code>
 *         <br/>Unknown category ID,The specified category ID could not be found
 *         </code>
 *     </li>
 *     <li>
 *         When the category is not active
 *         Status: "Inactive category", Message: "The specified category is not active currently"
 *         Example:
 *         <code>
 *         <br/>Inactive category,The specified category is not active right now
 *         </code>
 *     </li>
 *     <li>
 *         When the playerName is already in use
 *         Status: "Invalid playerName", Message: "The specified playerName is already in use (try a different one)"
 *         Example:
 *         <code>
 *         <br/>Invalid playerName,The specified playerName is already in use (try a different one)
 *         </code>
 *     </li>
 * </ul>
 *
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 09:51
 */
public class GetStartQuiz extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String code = request.getParameter("code"); // undocumented parameter; used only for testing a category before it becomes active

        final String playerName = request.getParameter("playerName");
        final String appID      = request.getParameter("appID");
        final String categoryUUID = request.getParameter("categoryUUID");

        if(playerName == null || playerName.isEmpty() || appID == null || appID.isEmpty() || categoryUUID == null || categoryUUID.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getCsvStatus("Invalid or missing parameters", "Valid parameter list 'startQuiz?playerName=...&appID=...&categoryUUID=...'"));
        }
        else
        {
            final Category category = CategoryFactory.getCategory(categoryUUID);
            if(category == null)
            {
                // ignore reply builder, and output the error status/message and terminate
                printWriter.println(Protocol.getCsvStatus("Unknown category ID", "The specified category ID could not be found"));
            }
            else
            {
                final boolean showInactive = code != null && code.equals(category.getCode());
                if(!category.isActiveNow() && !showInactive)
                {
                    // ignore reply builder, and output the error status/message and terminate
                    printWriter.println(Protocol.getCsvStatus("Inactive category", "The specified category is not active right now"));
                }
                else
                {
                    final String name1 = "csv_client"; // default placeholders (used only in the JSON variant of the API)
                    final String email1 = "csv_email@codecyprus.org";
                    final String sessionUUID = SessionFactory.getOrCreateSession(playerName, appID, categoryUUID, name1, email1);

                    if(sessionUUID == null)
                    {
                        // report that the given playerName was already used
                        printWriter.println(Protocol.getCsvStatus("Invalid playerName", "The specified playerName is already in use (try a different one)"));
                    }
                    else
                    {
                        final StringBuilder reply = new StringBuilder();
                        reply.append(Protocol.getCsvStatus("OK", "")).append(EOL); // OK status

                        int numOfQuestions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(categoryUUID).size();
                        reply.append(sessionUUID).append(EOL);
                        reply.append(Integer.toString(numOfQuestions)).append(EOL);

                        printWriter.println(reply.toString()); // normal CSV output
                    }
                }
            }
        }
    }
}