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

package uk.ac.uclan.thc.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;

import java.util.List;
import java.util.logging.Logger;

/**
 * Date: 8/16/12
 * Time: 12:28 PM
 */
public class UserEntity
{
    public static final Logger log = Logger.getLogger(UserEntity.class.getCanonicalName());

    public static final String KIND = "User";

    public static final String PROPERTY_EMAIL       = "email";
    public static final String PROPERTY_NICKNAME    = "nickname";
    public static final String PROPERTY_IS_ADMIN    = "is_admin";
    public static final String PROPERTY_IS_TRAINER  = "is_trainer";

    private final String email;
    private final String nickname;
    private final boolean isAdmin;
    private final boolean isTrainer;

    public UserEntity(final String email, final String nickname, final boolean isAdmin, final boolean isTrainer)
    {
        this.email      = email;
        this.nickname   = nickname;
        this.isAdmin    = isAdmin;
        this.isTrainer  = isTrainer;
    }

    public UserEntity(final Entity entity)
    {
        if(!KIND.equals(entity.getKind()))
        {
            throw new IllegalArgumentException("Entity must be of kind: " + KIND + " (found: " + entity.getKind() + ")");
        }

        this.email      = (String) entity.getProperty(PROPERTY_EMAIL);
        this.nickname   = (String) entity.getProperty(PROPERTY_NICKNAME);
        this.isAdmin    = (Boolean) entity.getProperty(PROPERTY_IS_ADMIN);
        this.isTrainer  = (Boolean) entity.getProperty(PROPERTY_IS_TRAINER);
    }

    public String getEmail()
    {
        return email;
    }

    public String getNickname()
    {
        return nickname;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public boolean isTrainer() { return isTrainer; }

    static public boolean isAdmin(final User user)
    {
        return user != null && getUserEntity(user.getEmail()).isAdmin();
    }

    static public boolean isTrainer(final User user)
    {
        return user != null && getUserEntity(user.getEmail()).isTrainer();
    }

    static public UserEntity getUserEntity(final String email)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).setFilter(new Query.FilterPredicate(UserEntity.PROPERTY_EMAIL, Query.FilterOperator.EQUAL, email));
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final List<Entity> userEntities = preparedQuery.asList(FetchOptions.Builder.withDefaults());
        if(userEntities.size() == 0)
        {
            log.info("Could not find user with email: " + email);
            return null;
        }
        else if(userEntities.size() == 1)
        {
            return new UserEntity(userEntities.get(0));
        }
        else
        {
            log.severe("More than 1 entities for email: " + email);
            return new UserEntity(userEntities.get(0));
        }
    }

    static public UserEntity setUserEntity(final String email, final String nickname, final boolean isAdmin, final boolean isTrainer)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity userEntity = new Entity(KIND);
        userEntity.setProperty(PROPERTY_EMAIL, email);
        userEntity.setProperty(PROPERTY_NICKNAME, nickname);
        userEntity.setProperty(PROPERTY_IS_ADMIN, isAdmin);
        userEntity.setProperty(PROPERTY_IS_TRAINER, isTrainer);

        datastoreService.put(userEntity);

        return new UserEntity(email, nickname, isAdmin, isTrainer);
    }

    static public UserEntity updateUserEntity(final String email, final String nickname, final boolean isAdmin, final boolean isTrainer)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).setFilter(new Query.FilterPredicate(UserEntity.PROPERTY_EMAIL, Query.FilterOperator.EQUAL, email));
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final List<Entity> userEntities = preparedQuery.asList(FetchOptions.Builder.withDefaults());
        if(userEntities.size() == 1)
        {
            final Entity entity = userEntities.get(0);
            entity.setProperty(PROPERTY_NICKNAME, nickname);
            entity.setProperty(PROPERTY_IS_ADMIN, isAdmin);
            entity.setProperty(PROPERTY_IS_TRAINER, isTrainer);
            datastoreService.put(entity);
            return new UserEntity(entity);
        }
        else
        {
            log.severe("More than 1 entities for email: " + email);
            return new UserEntity(userEntities.get(0));
        }
    }
}