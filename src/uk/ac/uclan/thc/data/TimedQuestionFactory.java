package uk.ac.uclan.thc.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import uk.ac.uclan.thc.model.TimedQuestion;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis on 26/10/2015 / 08:11.
 */
public class TimedQuestionFactory
{
    public static final Logger log = Logger.getLogger(TimedQuestionFactory.class.getCanonicalName());

    public static final String KIND = "TimedQuestion";

    public static final String PROPERTY_UUID                = "uuid";
    public static final String PROPERTY_TITLE               = "timed_question_title";
    public static final String PROPERTY_CREATED_BY          = "timed_question_created_by";
    public static final String PROPERTY_CATEGORY_UUID       = "timed_question_category_uuid";
    public static final String PROPERTY_BODY                = "timed_question_body";
    public static final String PROPERTY_IMAGE_URL           = "timed_question_image_url";

    static public TimedQuestion getTimedQuestion(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString))
        {
            return (TimedQuestion) memcacheService.get(keyAsString);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try
            {
                final Entity timedQuestionEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                final TimedQuestion timedQuestion = getFromEntity(timedQuestionEntity);

                memcacheService.put(keyAsString, timedQuestion); // add cache entry

                return timedQuestion;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
        }
    }

    static public Vector<TimedQuestion> getAllTimedQuestions()
    {
        final Vector<TimedQuestion> timedQuestions = new Vector<TimedQuestion>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();
        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            timedQuestions.add(getFromEntity(entity));
        }

        return timedQuestions;
    }

    static public Vector<TimedQuestion> getAllTimedQuestionsForCategory(final String categoryUUID)
    {
        final Vector<TimedQuestion> timedQuestions = new Vector<TimedQuestion>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterCategory = new Query.FilterPredicate(
                PROPERTY_CATEGORY_UUID,
                Query.FilterOperator.EQUAL,
                categoryUUID);
        final Query query = new Query(KIND);
        query.setFilter(filterCategory);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();
        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            timedQuestions.add(getFromEntity(entity));
        }

        return timedQuestions;
    }

    static public Key addTimedQuestion(
            final String title,
            final String createdBy,
            final String categoryUUID,
            final String body,
            final String imageUrl)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity timedQuestionEntity = new Entity(KIND);
        timedQuestionEntity.setProperty(PROPERTY_TITLE, title);
        timedQuestionEntity.setProperty(PROPERTY_CREATED_BY, createdBy);
        timedQuestionEntity.setProperty(PROPERTY_CATEGORY_UUID, categoryUUID);
        timedQuestionEntity.setProperty(PROPERTY_BODY, body);
        timedQuestionEntity.setProperty(PROPERTY_IMAGE_URL, imageUrl);

        return datastoreService.put(timedQuestionEntity);
    }

    static public void editQuestion(
            final String uuid,
            final String title,
            final String createdBy,
            final String categoryUUID,
            final String body,
            final String imageUrl)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity timedQuestionEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            timedQuestionEntity.setProperty(PROPERTY_TITLE, title);
            timedQuestionEntity.setProperty(PROPERTY_CREATED_BY, createdBy);
            timedQuestionEntity.setProperty(PROPERTY_CATEGORY_UUID, categoryUUID);
            timedQuestionEntity.setProperty(PROPERTY_BODY, body);
            timedQuestionEntity.setProperty(PROPERTY_IMAGE_URL, imageUrl);
            datastoreService.put(timedQuestionEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cache entry
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public TimedQuestion getFromEntity(final Entity entity)
    {
        return new TimedQuestion(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_TITLE),
                (String) entity.getProperty(PROPERTY_CREATED_BY),
                (String) entity.getProperty(PROPERTY_CATEGORY_UUID),
                (String) entity.getProperty(PROPERTY_BODY),
                (String) entity.getProperty(PROPERTY_IMAGE_URL));
    }
}