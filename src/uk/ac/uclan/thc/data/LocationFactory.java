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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import uk.ac.uclan.thc.model.Location;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class LocationFactory
{
    public static final Logger log = Logger.getLogger(LocationFactory.class.getCanonicalName());

    public static final String KIND = "Location";

    public static final String PROPERTY_UUID                        = "uuid";
    public static final String PROPERTY_NAME                        = "location_name";
    public static final String PROPERTY_CREATED_BY                  = "location_created_by";
    public static final String PROPERTY_ADMIN_OR_OWNER_ACCESS_ONLY  = "location_admin_access_only";
    public static final String PROPERTY_TIMESTAMP                   = "location_timestamp";

    static public Location getLocation(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString))
        {
            return (Location) memcacheService.get(keyAsString);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try
            {
                final Entity locationEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                final Location location = getFromEntity(locationEntity);

                memcacheService.put(keyAsString, location); // add cache entry

                return location;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
        }
    }

    public static final String ALL_LOCATIONS = "all-locations";

    static public Vector<Location> getAllLocations()
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_NAME);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Location> locations = new Vector<Location>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            locations.add(getFromEntity(entity));
        }

        return locations;
    }

    static public Vector<Location> getAllLocations(final boolean isUserAdmin, final String userEmail)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_NAME);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Location> locations = new Vector<Location>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            final Location location = getFromEntity(entity);
            final boolean isUserOwner = location.getCreatedBy().equalsIgnoreCase(userEmail);
            if(isUserAdmin || isUserOwner || !location.isAdminOrOwnerAccessOnly())
                locations.add(location);
        }

        return locations;
    }

    static public Key addLocation(final String name, final String createdBy, final boolean adminAccessOnly, final long timestamp)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity categoryEntity = new Entity(KIND);
        categoryEntity.setProperty(PROPERTY_NAME, name);
        categoryEntity.setProperty(PROPERTY_CREATED_BY, createdBy);
        categoryEntity.setProperty(PROPERTY_ADMIN_OR_OWNER_ACCESS_ONLY, adminAccessOnly);
        categoryEntity.setProperty(PROPERTY_TIMESTAMP, timestamp);

        return datastoreService.put(categoryEntity);
    }

    static public void editLocation(final String uuid, final String name, final String createdBy, final boolean adminAccessOnly, final long timestamp)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity categoryEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            categoryEntity.setProperty(PROPERTY_NAME, name);
            categoryEntity.setProperty(PROPERTY_CREATED_BY, createdBy);
            categoryEntity.setProperty(PROPERTY_ADMIN_OR_OWNER_ACCESS_ONLY, adminAccessOnly);
            categoryEntity.setProperty(PROPERTY_TIMESTAMP, timestamp);
            datastoreService.put(categoryEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cache entry
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Location getFromEntity(final Entity entity)
    {
        return new Location(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_NAME),
                (String) entity.getProperty(PROPERTY_CREATED_BY),
                entity.hasProperty(PROPERTY_ADMIN_OR_OWNER_ACCESS_ONLY) ? (Boolean) entity.getProperty(PROPERTY_ADMIN_OR_OWNER_ACCESS_ONLY) : false,
                (Long) entity.getProperty(PROPERTY_TIMESTAMP));
    }
}