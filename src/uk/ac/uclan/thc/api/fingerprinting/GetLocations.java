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
import uk.ac.uclan.thc.data.LocationFactory;
import uk.ac.uclan.thc.data.UserEntity;
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
public class GetLocations extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        if(UserEntity.isTrainer(user))
        {
            final Vector<Location> locations = LocationFactory.getAllLocations(UserEntity.isAdmin(user), user.getEmail());

            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{\n");
            stringBuilder.append("   \"status\": \"OK\",\n");
            stringBuilder.append("   \"message\": \"\",\n");
            stringBuilder.append("   \"locations\": [\n");
            int counter = 0;
            for(final Location location : locations)
            {
                stringBuilder.append("    {\n");
                stringBuilder.append("      \"uuid\": \"").append(location.getUUID()).append("\",\n");
                stringBuilder.append("      \"name\": \"").append(location.getName()).append("\",\n");
                stringBuilder.append("      \"createdBy\": \"").append(location.getCreatedBy()).append("\",\n");
                stringBuilder.append("      \"timestamp\": ").append(location.getTimestamp()).append("\n");
                stringBuilder.append("    }").append(++counter < locations.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  ]\n");
            stringBuilder.append("}\n");

            printWriter.println(stringBuilder.toString());
        }
        else
        {
            // add user in the DB without access rights though
            if(user != null) UserEntity.setUserEntity(user.getEmail(), user.getNickname(), false, false);

            final String message = user == null? "Undefined user" : "User not authorized as trainer: " + user.getEmail();

            printWriter.println(
                    "{\n" +
                    "   \"status\": \"Error\",\n" +
                    "   \"message\": \"" + message + "\"\n" +
                    "}\n");
        }
    }
}