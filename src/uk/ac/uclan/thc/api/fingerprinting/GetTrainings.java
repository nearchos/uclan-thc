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

import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.SsidMeasurementFactory;
import uk.ac.uclan.thc.data.TrainingFactory;
import uk.ac.uclan.thc.model.SsidMeasurement;
import uk.ac.uclan.thc.model.Training;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Nearchos Paspallis
 * 09/02/14 / 15:37.
 */
public class GetTrainings extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        // todo utilize memcache

        final String locationUUID = request.getParameter("locationUUID");
        if(locationUUID == null)
        {
            printWriter.print(Protocol.getJsonStatus("Error", "You need to specify a location UUID"));
        }
        else
        {
            // search DB ...
            final Vector<Training> trainings = TrainingFactory.geTrainingsForLocation(locationUUID);
            final Map<Training,Vector<SsidMeasurement>> trainingsToSsidMeasurements = new HashMap<Training, Vector<SsidMeasurement>>();
            for(final Training training : trainings)
            {
                trainingsToSsidMeasurements.put(training, SsidMeasurementFactory.getSsidMeasurementsByTraining(training.getUUID()));
            }

            printWriter.println(createReply(locationUUID, trainingsToSsidMeasurements));
        }
    }

    static private String createReply(final String locationUUID, final Map<Training,Vector<SsidMeasurement>> trainingsToSsidMeasurements)
    {
        final StringBuilder stringBuilder = new StringBuilder("{").append("\n");
        stringBuilder.append("  \"status\": \"OK\", ").append("\n");
        stringBuilder.append("  \"locationUUID\": \"").append(locationUUID).append("\", ").append("\n");
        stringBuilder.append("  \"trainings\": [ ").append("\n");
        final Set<Training> trainings = trainingsToSsidMeasurements.keySet();
        final int numOfTrainings = trainings.size();
        int i = 0;
        for(final Iterator<Training> iterator = trainings.iterator(); iterator.hasNext(); i++)
        {
            final Training training = iterator.next();
            final Vector<SsidMeasurement> ssidMeasurements = trainingsToSsidMeasurements.get(training);
            stringBuilder.append("    {").append("\n");
            stringBuilder.append("      \"uuid\": \"").append(training.getUUID()).append("\",\n");
            stringBuilder.append("      \"floorUUID\": \"").append(training.getFloorUUID()).append("\", ").append("\n");
            stringBuilder.append("      \"createdBy\": \"").append(training.getCreatedBy()).append("\",\n");
            stringBuilder.append("      \"timestamp\": ").append(training.getTimestamp()).append(",\n");
            stringBuilder.append("      \"lat\": ").append(training.getLat()).append(",\n");
            stringBuilder.append("      \"lng\": ").append(training.getLng()).append(",\n");
            final String contextAsJSON = training.getContextAsJSON();
            stringBuilder.append("      \"context\": ").append(contextAsJSON == null ? "{}" : contextAsJSON).append(",\n");
            stringBuilder.append("      \"measurements\": [").append("\n");
            final int numOfSsidMeasurements = ssidMeasurements.size();
            for(int j = 0; j < numOfSsidMeasurements; j++)
            {
                final SsidMeasurement ssidMeasurement = ssidMeasurements.elementAt(j);
                stringBuilder.append("        { \"macAddress\": \"").append(ssidMeasurement.getMacAddress()).append("\", \"ssid\": ")
                        .append(ssidMeasurement.getSsid()).append(" }").append(j < numOfSsidMeasurements - 1 ? "," : "").append("\n");
            }
            stringBuilder.append("      ]").append("\n");
            stringBuilder.append("    }").append(i < numOfTrainings - 1 ? "," : "").append("\n");
        }
        stringBuilder.append("  ]").append("\n");
        stringBuilder.append("}").append("\n");

        return stringBuilder.toString();
    }
}