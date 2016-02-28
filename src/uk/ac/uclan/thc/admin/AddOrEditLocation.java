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
import uk.ac.uclan.thc.data.LocationFactory;
import uk.ac.uclan.thc.data.UserEntity;
import uk.ac.uclan.thc.model.Location;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 16/Jun/13
 * Time: 16:53
 */
public class AddOrEditLocation extends HttpServlet
{
    public static final Logger log = Logger.getLogger(AddOrEditLocation.class.getCanonicalName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        response.setContentType("text/html");

        if(user == null)
        {
            response.getWriter().print(Protocol.getJsonStatus("Error", "You must sign in first"));
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
                String uuid = request.getParameter(LocationFactory.PROPERTY_UUID);
                final String name = request.getParameter(LocationFactory.PROPERTY_NAME);
                final boolean adminAccessOnly = "true".equalsIgnoreCase(request.getParameter(LocationFactory.PROPERTY_ADMIN_OR_OWNER_ACCESS_ONLY));
                final String createdBy = user.getEmail();
                long timestamp;
                try
                {
                    timestamp = Location.SIMPLE_DATE_FORMAT.parse(request.getParameter(LocationFactory.PROPERTY_TIMESTAMP)).getTime();
                }
                catch (ParseException pe)
                {
                    timestamp = 0L;
                    log.warning(pe.getMessage());
                }

                if(uuid != null && !uuid.isEmpty()) // editing existing location
                {
                    LocationFactory.editLocation(uuid, name, createdBy, adminAccessOnly, timestamp);
                }
                else // adding a new location
                {
                    final Key key = LocationFactory.addLocation(name, createdBy, adminAccessOnly, timestamp);
                    uuid = KeyFactory.keyToString(key);
                }

                response.sendRedirect("/admin/location?uuid=" + uuid);
            }
        }
    }
}
