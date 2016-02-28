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
import uk.ac.uclan.thc.model.Floor;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class FloorFactory
{
    public static final Logger log = Logger.getLogger(FloorFactory.class.getCanonicalName());

    public static final String KIND = "Floor";

    public static final String PROPERTY_UUID                    = "uuid";
    public static final String PROPERTY_FLOOR_NAME              = "floor_name";
    public static final String PROPERTY_FLOOR_ORDER             = "floor_order";
    public static final String PROPERTY_FLOOR_LOCATION_UUID     = "floor_location_uuid";
    public static final String PROPERTY_FLOOR_IMAGE_URL         = "floor_image_url";
    public static final String PROPERTY_FLOOR_TOP_LEFT_LAT      = "floor_top_left_lat";
    public static final String PROPERTY_FLOOR_TOP_LEFT_LNG      = "floor_top_left_lng";
    public static final String PROPERTY_FLOOR_BOTTOM_RIGHT_LAT  = "floor_bottom_right_lat";
    public static final String PROPERTY_FLOOR_BOTTOM_RIGHT_LNG  = "floor_bottom_right_lng";

    static public Floor getFloor(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString))
        {
            return (Floor) memcacheService.get(keyAsString);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try
            {
                final Entity floorEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                final Floor floor = getFromEntity(floorEntity);

                memcacheService.put(keyAsString, floor); // add cache entry

                return floor;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
        }
    }

    static public Vector<Floor> getFloors(final String locationUUID)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterFloors = new Query.FilterPredicate(
                PROPERTY_FLOOR_LOCATION_UUID,
                Query.FilterOperator.EQUAL,
                locationUUID);
        final Query query = new Query(KIND).addSort(PROPERTY_FLOOR_ORDER, Query.SortDirection.ASCENDING);
        query.setFilter(filterFloors);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Floor> floors = new Vector<Floor>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            floors.add(getFromEntity(entity));
        }

        return floors;
    }

    static public Key addFloor(final String name, final long order, final String locationUUID, final String imageURL,
                               final double topLeftLat, final double topLeftLng, final double bottomRightLat, final double bottomRightLng)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity floorEntity = new Entity(KIND);
        floorEntity.setProperty(PROPERTY_FLOOR_NAME, name);
        floorEntity.setProperty(PROPERTY_FLOOR_ORDER, order);
        floorEntity.setProperty(PROPERTY_FLOOR_LOCATION_UUID, locationUUID);
        floorEntity.setProperty(PROPERTY_FLOOR_IMAGE_URL, imageURL);
        floorEntity.setProperty(PROPERTY_FLOOR_TOP_LEFT_LAT, topLeftLat);
        floorEntity.setProperty(PROPERTY_FLOOR_TOP_LEFT_LNG, topLeftLng);
        floorEntity.setProperty(PROPERTY_FLOOR_BOTTOM_RIGHT_LAT, bottomRightLat);
        floorEntity.setProperty(PROPERTY_FLOOR_BOTTOM_RIGHT_LNG, bottomRightLng);

        return datastoreService.put(floorEntity);
    }

    static public void editFloor(final String uuid, final String name, final long order, final String locationUUID, final String imageURL,
                                 final double topLeftLat, final double topLeftLng, final double bottomRightLat, final double bottomRightLng)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity floorEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            floorEntity.setProperty(PROPERTY_FLOOR_NAME, name);
            floorEntity.setProperty(PROPERTY_FLOOR_ORDER, order);
            floorEntity.setProperty(PROPERTY_FLOOR_LOCATION_UUID, locationUUID);
            floorEntity.setProperty(PROPERTY_FLOOR_IMAGE_URL, imageURL);
            floorEntity.setProperty(PROPERTY_FLOOR_TOP_LEFT_LAT, topLeftLat);
            floorEntity.setProperty(PROPERTY_FLOOR_TOP_LEFT_LNG, topLeftLng);
            floorEntity.setProperty(PROPERTY_FLOOR_BOTTOM_RIGHT_LAT, bottomRightLat);
            floorEntity.setProperty(PROPERTY_FLOOR_BOTTOM_RIGHT_LNG, bottomRightLng);
            datastoreService.put(floorEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cache entry
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Floor getFromEntity(final Entity entity)
    {
        return new Floor(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_FLOOR_NAME),
                (Long) entity.getProperty(PROPERTY_FLOOR_ORDER),
                (String) entity.getProperty(PROPERTY_FLOOR_LOCATION_UUID),
                (String) entity.getProperty(PROPERTY_FLOOR_IMAGE_URL),
                (Double) entity.getProperty(PROPERTY_FLOOR_TOP_LEFT_LAT),
                (Double) entity.getProperty(PROPERTY_FLOOR_TOP_LEFT_LNG),
                (Double) entity.getProperty(PROPERTY_FLOOR_BOTTOM_RIGHT_LAT),
                (Double) entity.getProperty(PROPERTY_FLOOR_BOTTOM_RIGHT_LNG));
    }
}