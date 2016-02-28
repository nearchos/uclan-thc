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
import uk.ac.uclan.thc.data.TimedQuestionFactory;
import uk.ac.uclan.thc.data.UserEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 11:55
 */
public class AddOrEditTimedQuestion extends HttpServlet
{
    private static final Logger log = Logger.getLogger(AddOrEditTimedQuestion.class.getName());

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
                response.getWriter().print("User '" + user.getEmail() + "' is not an admin");
            }
            else
            {
                String uuid = request.getParameter(TimedQuestionFactory.PROPERTY_UUID);
                final String title = request.getParameter(TimedQuestionFactory.PROPERTY_TITLE);
                final String createdBy = request.getParameter(TimedQuestionFactory.PROPERTY_CREATED_BY);
                final String categoryUuid = request.getParameter(TimedQuestionFactory.PROPERTY_CATEGORY_UUID);
                final String body = request.getParameter(TimedQuestionFactory.PROPERTY_BODY);
                final String imageUrl = request.getParameter(TimedQuestionFactory.PROPERTY_IMAGE_URL);

                if(uuid != null && !uuid.isEmpty()) // editing existing category
                {
                    TimedQuestionFactory.editQuestion(uuid, title, createdBy, categoryUuid, body, imageUrl);
                }
                else // adding a new category
                {
                    final Key key = TimedQuestionFactory.addTimedQuestion(title, createdBy, categoryUuid, body, imageUrl);
                    uuid = KeyFactory.keyToString(key);
                }

                response.sendRedirect("/admin/timed-question?uuid=" + uuid);
            }
        }
    }
}
