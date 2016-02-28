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
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.model.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import static uk.ac.uclan.thc.api.Protocol.EOL;

/**
 * Retrieves the current score for the specified session (i.e. the same user/app).
 *
 * User: Nearchos Paspallis
 * Date: 17/12/13
 * Time: 22:10
 */
public class GetScore extends HttpServlet
{
    public static final Logger log = Logger.getLogger(GetScore.class.getCanonicalName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String sessionUUID = request.getParameter("session");

        if(sessionUUID == null)
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Invalid or missing parameters", "Valid parameter list 'score?session=...'"));
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
                final long score = session.getScore();

                final StringBuilder reply = new StringBuilder("{").append(EOL);
                reply.append("  \"status\": \"OK\"").append(",").append(EOL); // OK status
                reply.append("  \"score\": ").append(score).append(EOL); // OK status
                reply.append("}").append(EOL);

                printWriter.println(reply.toString()); // normal JSON output
            }
        }
    }
}