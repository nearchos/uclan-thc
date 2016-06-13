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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import uk.ac.uclan.thc.data.CategoryFactory;
import uk.ac.uclan.thc.data.SessionFactory;
import uk.ac.uclan.thc.data.UserEntity;
import uk.ac.uclan.thc.model.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 12:09
 */
public class DeleteAllSessions extends HttpServlet
{
    private Logger log = Logger.getLogger(DeleteAllSessions.class.toString());

    public static final String REDIRECT_URL = "redirect-url";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
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
                response.getWriter().print("User '" + user.getEmail() + "' is not an admin");
            }
            else
            {
                final String key = request.getParameter(CategoryFactory.PROPERTY_UUID);
                final String redirectUrl = request.getParameter(REDIRECT_URL);

                log.warning("Deleting entity with UUID: " + key);

                deleteAllSessions(key);

                response.sendRedirect(URLDecoder.decode(redirectUrl, "UTF-8"));
            }
        }
    }

    private void deleteAllSessions(final String categoryUuid)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        final Vector<Session> sessions = SessionFactory.getAllSessionsByCategoryUUID(categoryUuid);
        for(final Session session : sessions) {
            final String sessionUuid = session.getUUID();
            datastoreService.delete(KeyFactory.stringToKey(sessionUuid));
            MemcacheServiceFactory.getMemcacheService().delete(sessionUuid); // invalidate cache entry
        }
        log.info(sessions.size() + " sessions deleted");
    }
}