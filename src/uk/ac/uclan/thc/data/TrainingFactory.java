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
import uk.ac.uclan.thc.model.Training;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class TrainingFactory
{
    public static final Logger log = Logger.getLogger(TrainingFactory.class.getCanonicalName());

    public static final String KIND = "Training";

    public static final String PROPERTY_UUID            = "uuid";
    public static final String PROPERTY_TIMESTAMP       = "training_timestamp";
    public static final String PROPERTY_CONTEXT         = "training_context";
    public static final String PROPERTY_CREATED_BY      = "training_created_by";
    public static final String PROPERTY_LOCATION_UUID   = "training_location_uuid";
    public static final String PROPERTY_FLOOR_UUID      = "training_floor_uuid";
    public static final String PROPERTY_LAT             = "training_lat";
    public static final String PROPERTY_LNG             = "training_lng";

    static public Training getTraining(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString))
        {
            return (Training) memcacheService.get(keyAsString);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try
            {
                final Entity trainingEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                final Training training = getFromEntity(trainingEntity);

                memcacheService.put(keyAsString, training); // add cache entry

                return training;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
        }
    }

    static public Vector<Training> getAllTrainings()
    {
        // todo update memcache?
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Training> trainings = new Vector<Training>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            trainings.add(getFromEntity(entity));
        }

        return trainings;
    }

    static public Vector<Training> geTrainingsForLocation(final String locationUUID)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains("trainings@" + locationUUID))
        {
            return (Vector<Training>) memcacheService.get("trainings@" + locationUUID);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            final Query.Filter filterLocations = new Query.FilterPredicate(
                    PROPERTY_LOCATION_UUID,
                    Query.FilterOperator.EQUAL,
                    locationUUID);
            final Query query = new Query(KIND);//.addSort(PROPERTY_TIMESTAMP, Query.SortDirection.ASCENDING);
            query.setFilter(filterLocations);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final Vector<Training> trainings = new Vector<Training>();
            for(final Entity entity : preparedQuery.asIterable())
            {
                trainings.add(getFromEntity(entity));
            }

            // update memcache
            memcacheService.put("trainings@" + locationUUID, trainings);

            return trainings;
        }
    }

    static public Key addTraining(final String createdBy, final String locationUUID, final String floorUUID, final long timestamp, final String contextAsJSON, final double lat, final double lng)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity trainingEntity = new Entity(KIND);
        trainingEntity.setProperty(PROPERTY_CREATED_BY, createdBy);
        trainingEntity.setProperty(PROPERTY_LOCATION_UUID, locationUUID);
        trainingEntity.setProperty(PROPERTY_FLOOR_UUID, floorUUID);
        trainingEntity.setProperty(PROPERTY_TIMESTAMP, timestamp);
        trainingEntity.setProperty(PROPERTY_CONTEXT, contextAsJSON);
        trainingEntity.setProperty(PROPERTY_LAT, lat);
        trainingEntity.setProperty(PROPERTY_LNG, lng);

        // cleanup memcache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        memcacheService.delete("trainings@" + locationUUID);

        return datastoreService.put(trainingEntity);
    }

    static public Training getFromEntity(final Entity entity)
    {
        return new Training(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_CREATED_BY),
                (String) entity.getProperty(PROPERTY_LOCATION_UUID),
                (String) entity.getProperty(PROPERTY_FLOOR_UUID),
                (Long) entity.getProperty(PROPERTY_TIMESTAMP),
                (String) entity.getProperty(PROPERTY_CONTEXT),
                (Double) entity.getProperty(PROPERTY_LAT),
                (Double) entity.getProperty(PROPERTY_LNG));
    }
}