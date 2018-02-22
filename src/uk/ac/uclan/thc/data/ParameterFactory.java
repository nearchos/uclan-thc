package uk.ac.uclan.thc.data;

import com.google.appengine.api.datastore.*;
import uk.ac.uclan.thc.model.Parameter;

import java.util.Vector;
import java.util.logging.Logger;

public class ParameterFactory {

    public static final Logger log = Logger.getLogger(ParameterFactory.class.getCanonicalName());

    public static final String KIND = "Parameter";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_KEY = "parameter_key";
    public static final String PROPERTY_VALUE = "parameter_value";

    static public Parameter getParameter(final String keyAsString) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity parameterEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));
            return getFromEntity(parameterEntity);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warning("Could not find " + KIND + " with key: " + keyAsString);
            return null;
        }
    }

    static public Vector<Parameter> getAllParameters() {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_KEY);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Parameter> parameters = new Vector<Parameter>();
        for(final Entity entity : preparedQuery.asIterable()) {
            parameters.add(getFromEntity(entity));
        }

        return parameters;
    }

    static public Key addParameter(final String key, final String value) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity parameterEntity = new Entity(KIND);
        parameterEntity.setProperty(PROPERTY_KEY, key);
        parameterEntity.setProperty(PROPERTY_VALUE, value);

        return datastoreService.put(parameterEntity);
    }

    static public void editParameter(final String uuid, final String key, final String value) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity parameterEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            parameterEntity.setProperty(PROPERTY_KEY, key);
            parameterEntity.setProperty(PROPERTY_VALUE, value);
            datastoreService.put(parameterEntity);
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Parameter getFromEntity(final Entity entity) {
        return new Parameter(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_KEY),
                (String) entity.getProperty(PROPERTY_VALUE));
    }
}
