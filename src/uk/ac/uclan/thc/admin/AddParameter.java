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
import uk.ac.uclan.thc.data.ParameterFactory;
import uk.ac.uclan.thc.data.UserEntity;
import uk.ac.uclan.thc.model.Parameter;

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
public class AddParameter extends HttpServlet
{
    public static final Logger log = Logger.getLogger(AddParameter.class.getCanonicalName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        response.setContentType("text/html");

        if(user == null) {
            response.getWriter().print("You must sign in first");
        } else {
            final UserEntity userEntity = UserEntity.getUserEntity(user.getEmail());
            if(userEntity == null || !userEntity.isAdmin()) {
                response.getWriter().print(Protocol.getJsonStatus("Error", "User '" + user.getEmail() + "' is not an admin"));
            } else {
                final String keyS = request.getParameter(ParameterFactory.PROPERTY_KEY);
                final String valueS = request.getParameter(ParameterFactory.PROPERTY_VALUE);

                final Parameter existingParameter = ParameterFactory.getParameter(keyS);
                if(existingParameter != null) {
                    log.warning("Parameter with key: " + keyS + " already exists! Overwriting.");
                    ParameterFactory.editParameter(existingParameter.getUuid(), keyS, valueS);
                } else {
                    log.info("Creating new parameter: " + keyS + " -> " + valueS);
                    ParameterFactory.addParameter(keyS, valueS);
                }

                response.sendRedirect("/admin/parameters");
            }
        }
    }
}
