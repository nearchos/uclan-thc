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

package uk.ac.uclan.thc.api.fingerprinting;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.SsidMeasurementFactory;
import uk.ac.uclan.thc.data.TrainingFactory;
import uk.ac.uclan.thc.data.UserEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis
 * 09/02/14 / 15:37.
 */
public class SubmitTraining extends HttpServlet
{
    public static final Logger log = Logger.getLogger(SubmitTraining.class.getCanonicalName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        if(UserEntity.isTrainer(user))
        {
            final long timestamp = System.currentTimeMillis();

            double lat = 0d;
            double lng = 0d;
            String context = "{}"; // by default, context is empty

            String locationUUID = "";
            String floorUUID = "";

            final Map parameterMap = request.getParameterMap();
            final Set<Map.Entry> mapEntries = parameterMap.entrySet();
            final Map<String,Double> macAddressToSsid = new HashMap<String, Double>();
            for(final Map.Entry ssidNameToSignal : mapEntries)
            {
                final String name = ssidNameToSignal.getKey().toString();

                if("locationUUID".equals(name))
                {
                    locationUUID = request.getParameter("locationUUID");
                }
                else if("floorUUID".equals(name))
                {
                    floorUUID = request.getParameter("floorUUID");
                }
                else if("lat".equals(name))
                {
                    final String latS = request.getParameter("lat");
                    lat = latS != null ? Double.parseDouble(latS) : 0d;
                }
                else if("lng".equals(name))
                {
                    final String lngS = request.getParameter("lng");
                    lng = lngS != null ? Double.parseDouble(lngS) : 0d;
                }
                else if("context".equals(name))
                {
                    context = request.getParameter("context");
                }
                else
                {
                    final double value = Double.parseDouble(((String []) ssidNameToSignal.getValue())[0]);
                    macAddressToSsid.put(name, value);
                }
            }
            // store to DB
            final Key key = TrainingFactory.addTraining(user.getEmail(), locationUUID, floorUUID, timestamp, context, lat, lng);
            final String trainingUUID = KeyFactory.keyToString(key);
            SsidMeasurementFactory.addAllSsidMeasurement(trainingUUID, macAddressToSsid);

            printWriter.println(Protocol.getJsonStatus("OK", "")); // normal JSON output
        }
        else
        {
            final String message = user == null? "Undefined user" : "User not authorized as trainer: " + user.getEmail();

            printWriter.println(Protocol.getJsonStatus("Error", message));
        }
    }
}