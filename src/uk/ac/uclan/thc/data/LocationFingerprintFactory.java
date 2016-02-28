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
import uk.ac.uclan.thc.model.LocationFingerprint;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class LocationFingerprintFactory
{
    public static final Logger log = Logger.getLogger(LocationFingerprintFactory.class.getCanonicalName());

    public static final String KIND = "LocationFingerprint";

    public static final String PROPERTY_TIMESTAMP = "timestamp";
    public static final String PROPERTY_LATITUDE = "lat";
    public static final String PROPERTY_LONGITUDE = "lng";
    public static final String PROPERTY_SESSION_UUID = "session_uuid";

    /**
     * It returns the most updated {@link LocationFingerprint} available. Could be null if no fingerprint is
     * available for the given session.
     *
     * @param sessionUUID
     * @return the most recent {@link LocationFingerprint} if it exists, otherwise NULL
     */
    static public LocationFingerprint getLastLocationFingerprintBySessionUUID(final String sessionUUID)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains("locationOf-" + sessionUUID)) // first check in the cache
        {
            return (LocationFingerprint) memcacheService.get("locationOf-" + sessionUUID);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            final Query.Filter filterCategory = new Query.FilterPredicate(
                    PROPERTY_SESSION_UUID,
                    Query.FilterOperator.EQUAL,
                    sessionUUID);
            final Query query = new Query(KIND);
            query.setFilter(filterCategory).addSort(PROPERTY_TIMESTAMP, Query.SortDirection.DESCENDING);

            final PreparedQuery preparedQuery = datastoreService.prepare(query);

            final Iterator<Entity> iterator = preparedQuery.asIterable().iterator();
            if(iterator.hasNext())
            {
                final LocationFingerprint locationFingerprint = getFromEntity(iterator.next());
                memcacheService.put("locationOf-" + sessionUUID, locationFingerprint); // update the cache

                return locationFingerprint;
            }
            else
            {
                return null;
            }
        }
    }

    static public Key addLocationFingerprint(final double lat, final double lng, final String sessionUUID)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        memcacheService.delete("locationOf-" + sessionUUID); // invalidate old cache entry, if any

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity locationFingerprintEntity = new Entity(KIND);
        locationFingerprintEntity.setProperty(PROPERTY_TIMESTAMP, System.currentTimeMillis());
        locationFingerprintEntity.setProperty(PROPERTY_LATITUDE, lat);
        locationFingerprintEntity.setProperty(PROPERTY_LONGITUDE, lng);
        locationFingerprintEntity.setProperty(PROPERTY_SESSION_UUID, sessionUUID);

        return datastoreService.put(locationFingerprintEntity);
    }

    static public LocationFingerprint getFromEntity(final Entity entity)
    {
        return new LocationFingerprint(
                KeyFactory.keyToString(entity.getKey()),
                (Long) entity.getProperty(PROPERTY_TIMESTAMP),
                (Double) entity.getProperty(PROPERTY_LATITUDE),
                (Double) entity.getProperty(PROPERTY_LONGITUDE),
                (String) entity.getProperty(PROPERTY_SESSION_UUID));
    }
}