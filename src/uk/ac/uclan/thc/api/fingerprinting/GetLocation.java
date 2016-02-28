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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.FloorFactory;
import uk.ac.uclan.thc.data.LocationFactory;
import uk.ac.uclan.thc.data.UserEntity;
import uk.ac.uclan.thc.model.Floor;
import uk.ac.uclan.thc.model.Location;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * @author Nearchos Paspallis
 * 09/02/14 / 15:37.
 */
public class GetLocation extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        final String locationUUID = request.getParameter("uuid");
        if(locationUUID == null)
        {
            printWriter.print(Protocol.getJsonStatus("Error", "You must provide a 'uuid' parameter"));
        }
        else
        {
            final Location location = LocationFactory.getLocation(locationUUID);
            if(location == null)
            {
                printWriter.print(Protocol.getJsonStatus("Error", "No location for the specified UUID: " + locationUUID));
            }
            else
            {
                final boolean isOwner = user != null && location.getCreatedBy().equalsIgnoreCase(user.getEmail());
                if(!location.isAdminOrOwnerAccessOnly() || UserEntity.isTrainer(user) || UserEntity.isAdmin(user) || isOwner)
                {
                    final Vector<Floor> floors = FloorFactory.getFloors(locationUUID);

                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("{\n");
                    stringBuilder.append("   \"status\": \"OK\",\n");
                    stringBuilder.append("   \"message\": \"\",\n");

                    stringBuilder.append("   \"location\": {\n");
                    stringBuilder.append("      \"uuid\": \"").append(location.getUUID()).append("\",\n");
                    stringBuilder.append("      \"name\": \"").append(location.getName()).append("\",\n");
                    stringBuilder.append("      \"createdBy\": \"").append(location.getCreatedBy()).append("\",\n");
                    stringBuilder.append("      \"timestamp\": ").append(location.getTimestamp()).append("\n");
                    stringBuilder.append("    }, \n");

                    stringBuilder.append("   \"floors\": [\n");
                    for(int i = 0; i < floors.size(); i++)
                    {
                        final Floor floor = floors.elementAt(i);
                        stringBuilder.append("        {\n");
                        stringBuilder.append("          \"uuid\": \"").append(floor.getUUID()).append("\",\n");
                        stringBuilder.append("          \"name\": \"").append(floor.getName()).append("\",\n");
                        stringBuilder.append("          \"order\": ").append(floor.getOrder()).append(",\n");
                        stringBuilder.append("          \"imageURL\": \"").append(floor.getImageURL()).append("\",\n");
                        stringBuilder.append("          \"topLeftLat\": ").append(floor.getTopLeftLat()).append(",\n");
                        stringBuilder.append("          \"topLeftLng\": ").append(floor.getTopLeftLng()).append(",\n");
                        stringBuilder.append("          \"bottomRightLat\": ").append(floor.getBottomRightLat()).append(",\n");
                        stringBuilder.append("          \"bottomRightLng\": ").append(floor.getBottomRightLng()).append("\n");
                        stringBuilder.append("        }").append(i < floors.size() - 1 ? ",\n" : "\n");
                    }
                    stringBuilder.append("   ]\n");
                    stringBuilder.append("}\n");

                    printWriter.println(stringBuilder.toString());
                }
                else
                {
                    final String message = user == null ? "Undefined user" : "User not authorized: " + user.getEmail();

                    printWriter.println(
                                    "{\n" +
                                    "   \"status\": \"Error\",\n" +
                                    "   \"message\": \"" + message + "\"\n" +
                                    "}\n");
                }
            }
        }
    }

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
//    {
//        response.setContentType("text/plain; charset=utf-8");
//        final PrintWriter printWriter = response.getWriter();
//
//        final UserService userService = UserServiceFactory.getUserService();
//        final User user = userService.getCurrentUser();
//
//        if(UserEntity.isTrainer(user))
//        {
//            final String locationUUID = request.getParameter("uuid");
//            if(locationUUID == null)
//            {
//                printWriter.print(Protocol.getJsonStatus("Error", "You must provide a 'uuid' parameter"));
//            }
//            else
//            {
//                final Location location = LocationFactory.getLocation(locationUUID);
//                if(location == null)
//                {
//                    printWriter.print(Protocol.getJsonStatus("Error", "No location for the specified UUID: " + locationUUID));
//                }
//                else
//                {
//                    final boolean isOwner = location.getCreatedBy().equalsIgnoreCase(user.getEmail());
//                    if(!(UserEntity.isAdmin(user) || isOwner || !location.isAdminOrOwnerAccessOnly()))
//                    {
//                        printWriter.print(Protocol.getJsonStatus("Error", "The given location requires admin or owner access"));
//                    }
//                    else
//                    {
//                        final Vector<Floor> floors = FloorFactory.getFloors(locationUUID);
//
//                        final StringBuilder stringBuilder = new StringBuilder();
//                        stringBuilder.append("{\n");
//                        stringBuilder.append("   \"status\": \"OK\",\n");
//                        stringBuilder.append("   \"message\": \"\",\n");
//
//                        stringBuilder.append("   \"location\": {\n");
//                        stringBuilder.append("      \"uuid\": \"").append(location.getUUID()).append("\",\n");
//                        stringBuilder.append("      \"name\": \"").append(location.getName()).append("\",\n");
//                        stringBuilder.append("      \"createdBy\": \"").append(location.getCreatedBy()).append("\",\n");
//                        stringBuilder.append("      \"timestamp\": ").append(location.getTimestamp()).append("\n");
//                        stringBuilder.append("    }, \n");
//
//                        stringBuilder.append("   \"floors\": [\n");
//                        for(int i = 0; i < floors.size(); i++)
//                        {
//                            final Floor floor = floors.elementAt(i);
//                            stringBuilder.append("        {\n");
//                            stringBuilder.append("          \"uuid\": \"").append(floor.getUUID()).append("\",\n");
//                            stringBuilder.append("          \"name\": \"").append(floor.getName()).append("\",\n");
//                            stringBuilder.append("          \"order\": ").append(floor.getOrder()).append(",\n");
//                            stringBuilder.append("          \"imageURL\": \"").append(floor.getImageURL()).append("\",\n");
//                            stringBuilder.append("          \"topLeftLat\": ").append(floor.getTopLeftLat()).append(",\n");
//                            stringBuilder.append("          \"topLeftLng\": ").append(floor.getTopLeftLng()).append(",\n");
//                            stringBuilder.append("          \"bottomRightLat\": ").append(floor.getBottomRightLat()).append(",\n");
//                            stringBuilder.append("          \"bottomRightLng\": ").append(floor.getBottomRightLng()).append("\n");
//                            stringBuilder.append("        }").append(i < floors.size() - 1 ? ",\n" : "\n");
//                        }
//                        stringBuilder.append("   ]\n");
//                        stringBuilder.append("}\n");
//
//                        printWriter.println(stringBuilder.toString());
//                    }
//                }
//            }
//        }
//        else
//        {
//            final String message = user == null? "Undefined user" : "User not authorized as trainer: " + user.getEmail();
//
//            printWriter.println(
//                    "{\n" +
//                            "   \"status\": \"Error\",\n" +
//                            "   \"message\": \"" + message + "\"\n" +
//                            "}\n");
//        }
//    }
}