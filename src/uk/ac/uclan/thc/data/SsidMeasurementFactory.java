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
import uk.ac.uclan.thc.model.SsidMeasurement;
import uk.ac.uclan.thc.model.Training;

import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class SsidMeasurementFactory
{
    public static final Logger log = Logger.getLogger(SsidMeasurementFactory.class.getCanonicalName());

    public static final String KIND = "SsidMeasurement";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_TRAINING_UUID = "ssid-measurement_training-uuid";
    public static final String PROPERTY_MAC_ADDRESS = "ssid-measurement_mac-address";
    public static final String PROPERTY_SSID = "ssid-measurement_ssid";

    static public SsidMeasurement getSsidMeasurement(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString))
        {
            return (SsidMeasurement) memcacheService.get(keyAsString);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try
            {
                final Entity ssidMeasurementEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                final SsidMeasurement ssidMeasurement = getFromEntity(ssidMeasurementEntity);

                memcacheService.put(keyAsString, ssidMeasurement); // add cache entry

                return ssidMeasurement;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
        }
    }

    static public Vector<SsidMeasurement> getSsidMeasurementsByTraining(final String trainingUUID)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        final Query.Filter filterCode = new Query.FilterPredicate(
                PROPERTY_TRAINING_UUID,
                Query.FilterOperator.EQUAL,
                trainingUUID);
        query.setFilter(filterCode);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<SsidMeasurement> ssidMeasurements = new Vector<SsidMeasurement>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            ssidMeasurements.add(getFromEntity(entity));
        }

        return ssidMeasurements;
    }

    static public Key addSsidMeasurement(final String trainingUUID, final String macAddress, final double ssid)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity trainingEntity = new Entity(KIND);
        trainingEntity.setProperty(PROPERTY_TRAINING_UUID, trainingUUID);
        trainingEntity.setProperty(PROPERTY_MAC_ADDRESS, macAddress);
        trainingEntity.setProperty(PROPERTY_SSID, ssid);
        // todo cleanup memcache?

        return datastoreService.put(trainingEntity);
    }

    static public Vector<Key> addAllSsidMeasurement(final String trainingUUID, final Map<String,Double> macAddressToSsids)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Vector<Key> keys = new Vector<Key>();

        for(final String macAddress : macAddressToSsids.keySet())
        {
            final Entity trainingEntity = new Entity(KIND);
            trainingEntity.setProperty(PROPERTY_TRAINING_UUID, trainingUUID);
            trainingEntity.setProperty(PROPERTY_MAC_ADDRESS, macAddress);
            trainingEntity.setProperty(PROPERTY_SSID, macAddressToSsids.get(macAddress));

            keys.add(datastoreService.put(trainingEntity));
        }

        // todo cleanup memcache?
        return keys;
    }

    static public SsidMeasurement getFromEntity(final Entity entity)
    {
        return new SsidMeasurement(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_TRAINING_UUID),
                (String) entity.getProperty(PROPERTY_MAC_ADDRESS),
                (Double) entity.getProperty(PROPERTY_SSID));
    }
}