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
import uk.ac.uclan.thc.model.Question;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 8/16/12
 * Time: 11:40 AM
 */
public class QuestionFactory
{
    public static final Logger log = Logger.getLogger(QuestionFactory.class.getCanonicalName());

    public static final String KIND = "Question";

    public static final String PROPERTY_UUID                = "uuid";
    public static final String PROPERTY_CATEGORY_UUID       = "question_category_uuid";
    public static final String PROPERTY_SEQ_NUMBER          = "question_seq_number";
    public static final String PROPERTY_TEXT                = "question_text";
    public static final String PROPERTY_CORRECT_ANSWER      = "question_correct_answer";
    public static final String PROPERTY_CORRECT_SCORE       = "question_correct_score";
    public static final String PROPERTY_WRONG_SCORE         = "question_wrong_score";
    public static final String PROPERTY_SKIP_SCORE          = "question_skip_score";
    public static final String PROPERTY_LATITUDE            = "question_latitude";
    public static final String PROPERTY_LONGITUDE           = "question_longitude";
    public static final String PROPERTY_DISTANCE_THRESHOLD  = "question_distance_threshold";

    public static final long DEFAULT_CORRECT_SCORE   = 10L;
    public static final long DEFAULT_WRONG_SCORE     = 0L;
    public static final long DEFAULT_SKIP_SCORE     = -5L;

    static public Question getQuestion(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString))
        {
            return (Question) memcacheService.get(keyAsString);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try
            {
                final Entity questionEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                final Question question = getFromEntity(questionEntity);

                memcacheService.put(keyAsString, question); // add cache entry

                return question;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
        }
    }

    static public Vector<Question> getAllQuestionsForCategoryOrderedBySeqNumber(final String categoryUUID)
    {
        final Vector<Question> questions = new Vector<Question>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterCategory = new Query.FilterPredicate(
                PROPERTY_CATEGORY_UUID,
                Query.FilterOperator.EQUAL,
                categoryUUID);
        final Query query = new Query(KIND).addSort(PROPERTY_SEQ_NUMBER, Query.SortDirection.ASCENDING);
        query.setFilter(filterCategory);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();
        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            questions.add(getFromEntity(entity));
        }

        return questions;
    }

    static public Key addQuestion(
            final String categoryUuid,
            final int seqNumber,
            final String text,
            final String correctAnswer,
            final int correctScore,
            final int wrongScore,
            final int skipScore,
            final double latitude,
            final double longitude,
            final double distanceThreshold)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity questionEntity = new Entity(KIND);
        questionEntity.setProperty(PROPERTY_CATEGORY_UUID, categoryUuid);
        questionEntity.setProperty(PROPERTY_TEXT, text);
        questionEntity.setProperty(PROPERTY_SEQ_NUMBER, seqNumber);
        questionEntity.setProperty(PROPERTY_CORRECT_ANSWER, correctAnswer);
        questionEntity.setProperty(PROPERTY_CORRECT_SCORE, correctScore);
        questionEntity.setProperty(PROPERTY_WRONG_SCORE, wrongScore);
        questionEntity.setProperty(PROPERTY_SKIP_SCORE, skipScore);
        questionEntity.setProperty(PROPERTY_LATITUDE, latitude);
        questionEntity.setProperty(PROPERTY_LONGITUDE, longitude);
        questionEntity.setProperty(PROPERTY_DISTANCE_THRESHOLD, distanceThreshold);

        return datastoreService.put(questionEntity);
    }

    static public void editQuestion(
            final String uuid,
            final String categoryUUID,
            final int seqNumber,
            final String text,
            final String correctAnswer,
            final int correctScore,
            final int wrongScore,
            final int skipScore,
            final double latitude,
            final double longitude,
            final double distanceThreshold)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity questionEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            questionEntity.setProperty(PROPERTY_CATEGORY_UUID, categoryUUID);
            questionEntity.setProperty(PROPERTY_SEQ_NUMBER, seqNumber);
            questionEntity.setProperty(PROPERTY_TEXT, text);
            questionEntity.setProperty(PROPERTY_CORRECT_ANSWER, correctAnswer);
            questionEntity.setProperty(PROPERTY_CORRECT_SCORE, correctScore);
            questionEntity.setProperty(PROPERTY_WRONG_SCORE, wrongScore);
            questionEntity.setProperty(PROPERTY_SKIP_SCORE, skipScore);
            questionEntity.setProperty(PROPERTY_LATITUDE, latitude);
            questionEntity.setProperty(PROPERTY_LONGITUDE, longitude);
            questionEntity.setProperty(PROPERTY_DISTANCE_THRESHOLD, distanceThreshold);
            datastoreService.put(questionEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cache entry
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Question getFromEntity(final Entity entity)
    {
        long correctScore   = (Long) (entity.hasProperty(PROPERTY_CORRECT_SCORE) ? entity.getProperty(PROPERTY_CORRECT_SCORE) : DEFAULT_CORRECT_SCORE);
        long wrongScore     = (Long) (entity.hasProperty(PROPERTY_WRONG_SCORE) ? entity.getProperty(PROPERTY_WRONG_SCORE) : DEFAULT_WRONG_SCORE);
        long skipScore      = (Long) (entity.hasProperty(PROPERTY_SKIP_SCORE) ? entity.getProperty(PROPERTY_SKIP_SCORE) : DEFAULT_SKIP_SCORE);
        double distanceThreshold = (Double) (entity.hasProperty(PROPERTY_DISTANCE_THRESHOLD) ? entity.getProperty(PROPERTY_DISTANCE_THRESHOLD) : Question.DEFAULT_DISTANCE_THRESHOLD);
        return new Question(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_CATEGORY_UUID),
                (Long) entity.getProperty(PROPERTY_SEQ_NUMBER),
                (String) entity.getProperty(PROPERTY_TEXT),
                (String) entity.getProperty(PROPERTY_CORRECT_ANSWER),
                correctScore,
                wrongScore,
                skipScore,
                (Double) entity.getProperty(PROPERTY_LATITUDE),
                (Double) entity.getProperty(PROPERTY_LONGITUDE),
                distanceThreshold);
    }
}