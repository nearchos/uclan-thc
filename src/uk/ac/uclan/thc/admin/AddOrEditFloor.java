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

package uk.ac.uclan.thc.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import uk.ac.uclan.thc.api.Protocol;
import uk.ac.uclan.thc.data.FloorFactory;
import uk.ac.uclan.thc.data.UserEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 16/Jun/13
 * Time: 16:53
 */
public class AddOrEditFloor extends HttpServlet
{
    public static final Logger log = Logger.getLogger(AddOrEditFloor.class.getCanonicalName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        response.setContentType("text/html");

        if(user == null)
        {
            response.getWriter().print("You must sign in first");
        }
        else
        {
            final UserEntity userEntity = UserEntity.getUserEntity(user.getEmail());
            if(userEntity == null || !userEntity.isAdmin())
            {
                response.getWriter().print(Protocol.getJsonStatus("Error", "User '" + user.getEmail() + "' is not an admin"));
            }
            else
            {
                String uuid = request.getParameter(FloorFactory.PROPERTY_UUID);
                final String name = request.getParameter(FloorFactory.PROPERTY_FLOOR_NAME);
                final String orderS = request.getParameter(FloorFactory.PROPERTY_FLOOR_ORDER);
                long order = 0;
                final String locationUUID = request.getParameter(FloorFactory.PROPERTY_FLOOR_LOCATION_UUID);
                final String imageURL = request.getParameter(FloorFactory.PROPERTY_FLOOR_IMAGE_URL);
                final String topLeftLatS = request.getParameter(FloorFactory.PROPERTY_FLOOR_TOP_LEFT_LAT);
                final String topLeftLngS = request.getParameter(FloorFactory.PROPERTY_FLOOR_TOP_LEFT_LNG);
                final String bottomRightLatS = request.getParameter(FloorFactory.PROPERTY_FLOOR_BOTTOM_RIGHT_LAT);
                final String bottomRightLngS = request.getParameter(FloorFactory.PROPERTY_FLOOR_BOTTOM_RIGHT_LNG);
                double topLeftLat = 0d;
                double topLeftLng = 0d;
                double bottomRightLat = 0d;
                double bottomRightLng = 0d;
                try
                {
                    order = Long.parseLong(orderS);
                    topLeftLat = Double.parseDouble(topLeftLatS);
                    topLeftLng = Double.parseDouble(topLeftLngS);
                    bottomRightLat = Double.parseDouble(bottomRightLatS);
                    bottomRightLng = Double.parseDouble(bottomRightLngS);
                }
                catch (NumberFormatException nfe)
                {
                    log.warning(nfe.getMessage());
                }

                if(uuid != null && !uuid.isEmpty()) // editing existing location
                {
                    FloorFactory.editFloor(uuid, name, order, locationUUID, imageURL, topLeftLat, topLeftLng, bottomRightLat, bottomRightLng);
                }
                else // adding a new location
                {
                    final Key key = FloorFactory.addFloor(name, order, locationUUID, imageURL, topLeftLat, topLeftLng, bottomRightLat, bottomRightLng);
                    uuid = KeyFactory.keyToString(key);
                }

                response.sendRedirect("/admin/floor?uuid=" + uuid);
            }
        }
    }
}
