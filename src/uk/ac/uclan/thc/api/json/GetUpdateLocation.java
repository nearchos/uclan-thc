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

import com.google.appengine.api.datastore.Key;
import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.LocationFingerprintFactory;
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
 * Updates the current position for the specified session. No data is returned.
 *
 * User: Nearchos Paspallis
 * Date: 17/12/13
 * Time: 22:34
 */
public class GetUpdateLocation extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String latS = request.getParameter("lat");
        final String lngS = request.getParameter("lng");
        final String sessionUUID = request.getParameter("session");

        if(latS == null || latS.isEmpty() || lngS == null || lngS.isEmpty() || sessionUUID == null || sessionUUID.isEmpty())
        {
            // ignore reply builder, and output the error status/message and terminate
            printWriter.println(Protocol.getJsonStatus("Invalid or missing parameters", "Valid parameter list 'updateLocation?lat=...&lng=...&session=...'"));
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
                try
                {
                    final double lat = Double.parseDouble(latS);
                    final double lng = Double.parseDouble(lngS);

                    // adding the fingerprint
                    final Key key = LocationFingerprintFactory.addLocationFingerprint(lat, lng, sessionUUID);

                    final StringBuilder reply = new StringBuilder("{").append(EOL);
                    reply.append("  \"status\": \"OK\"").append(EOL); // OK status
                    reply.append("}").append(EOL); // OK status

                    printWriter.println(reply.toString()); // normal JSON output
                }
                catch (NumberFormatException nfe)
                {
                    // ignore reply builder, and output the error status/message and terminate
                    printWriter.println(Protocol.getJsonStatus("Invalid parameters", "The parameters 'lat' and 'lng' must be real numbers (e.g. 33.15)"));
                }
            }
        }
    }
}