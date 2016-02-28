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
 *     along with SessionFactory.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.uclan.thc.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import uk.ac.uclan.thc.model.*;
import uk.ac.uclan.thc.model.Category;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class SessionFactory
{
    public static final Logger log = Logger.getLogger(SessionFactory.class.getCanonicalName());

    public static final String KIND = "Session";

    public static final String PROPERTY_UUID = "uuid";
    public static final String PROPERTY_PLAYER_NAME = "player_name";
    public static final String PROPERTY_APP_ID = "app_id";
    public static final String PROPERTY_CATEGORY_UUID = "category_uuid";
    public static final String PROPERTY_CURRENT_QUESTION_UUID = "current_question_uuid";
    public static final String PROPERTY_SCORE = "score";
    public static final String PROPERTY_FINISH_TIME = "finish_time";
    public static final String PROPERTY_NAME1 = "name1";
    public static final String PROPERTY_EMAIL1 = "email1";

    static public Session getSession(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString)) // first check the cache
        {
            final Object uncheckedSession = memcacheService.get(keyAsString);
            return uncheckedSession instanceof Session ? (Session) uncheckedSession : null;
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try
            {
                final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));
                if(!SessionFactory.KIND.equals(sessionEntity.getKind())) return null;

                final Session session = getFromEntity(sessionEntity);

                memcacheService.put(keyAsString, session); // update cache

                return session;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
            catch (IllegalArgumentException iae)
            {
                log.warning("Invalid argument " + iae.getMessage());

                return null;
            }
        }
    }

    /**
     * Returns the UUID of the created (or retrieved) {@link Session}.
     *
     * @param playerName the name of the player
     * @param appID the ID of the app
     * @param categoryUUID the UUID of the category
     * @return the UUID of the created {@link Session} or null if it could not be created (i.e. because the playerName,
     * categoryUUID combination was already used).
     */
    static public String getOrCreateSession(final String playerName, final String appID, final String categoryUUID, final String name1, final String email1)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        final Transaction transaction = datastoreService.beginTransaction(); // transaction used to check if the playerName is not used already
        try
        {
            // first check if a session exists for this playerName/categoryUUID
            final Query.Filter filterPlayerName = new Query.FilterPredicate(
                    PROPERTY_PLAYER_NAME,
                    Query.FilterOperator.EQUAL,
                    playerName);
            final Query.Filter filterCategoryUUID = new Query.FilterPredicate(
                    PROPERTY_CATEGORY_UUID,
                    Query.FilterOperator.EQUAL,
                    categoryUUID);

            final Query query = new Query(KIND);
            final Query.Filter compositeFilter =
                    Query.CompositeFilterOperator.and(filterPlayerName, filterCategoryUUID);
            query.setFilter(compositeFilter);

            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final Iterable<Entity> iterable = preparedQuery.asIterable();
            final Iterator<Entity> iterator = iterable.iterator();

            // there should be exactly 0 or 1 sessions available
            if(!iterator.hasNext()) // no existing session with given playerName/categoryUUID; create new session
            {
                final Vector<Question> questions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(categoryUUID);

                // if questions.isEmpty, set text to empty String "" implying finished session
                final String firstQuestionUUID = questions.isEmpty() ? "" : questions.elementAt(0).getUUID();
                final Key key = addSession(playerName, appID, categoryUUID, firstQuestionUUID, name1, email1);

                transaction.commit();

                return KeyFactory.keyToString(key);
            }
            else // there must be exactly 1 session available
            {
                transaction.rollback();

                return null;
            }
        }
        finally
        {
            if(transaction.isActive())
            {
                transaction.rollback();
            }
        }
    }

    public static final int MAX_NUM_OF_SESSIONS = 10;

    static public Vector<Session> getSessionsByCategoryUUID(final String categoryUUID)
    {
        return getSessionsByCategoryUUID(categoryUUID, MAX_NUM_OF_SESSIONS, false);
    }

    static public Vector<Session> getAllSessionsByCategoryUUID(final String categoryUUID)
    {
        return getSessionsByCategoryUUID(categoryUUID, Integer.MAX_VALUE, false);
    }

    static public Vector<Session> getSessionsByCategoryUUID(final String categoryUUID, final int numOfSessions)
    {
        return getSessionsByCategoryUUID(categoryUUID, numOfSessions, false);
    }

    static public Vector<Session> getSessionsByCategoryUUID(final String categoryUUID, final boolean sorted)
    {
        return getSessionsByCategoryUUID(categoryUUID, MAX_NUM_OF_SESSIONS, sorted);
    }

    static public Vector<Session> getSessionsByCategoryUUID(final String categoryUUID, final int numOfSessions, final boolean sorted)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterCategory = new Query.FilterPredicate(
                PROPERTY_CATEGORY_UUID,
                Query.FilterOperator.EQUAL,
                categoryUUID);
        final Query query = new Query(KIND);
        query.setFilter(filterCategory);
        query.addSort(PROPERTY_SCORE, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Session> sessions = new Vector<Session>();
        int count = 0;
        for(final Entity entity : preparedQuery.asIterable())
        {
            sessions.add(getFromEntity(entity));
            if(++count >= numOfSessions) break;
        }

        if (!sorted)
        {
            Collections.shuffle(sessions);
        }

        return sessions;
    }

    public static final long DEFAULT_CORRECT_SCORE = 10L;

    /**
     * Updates the specified session by progressing to the next question.
     *
     * @param sessionUUID the ID of the session to be progressed
     * @return true of there is indeed a next question, false otherwise (i.e. if that was the last question)
     */
    static public boolean updateScoreAndProgressSessionToNextQuestion(final String sessionUUID)
    {
        return updateScoreAndProgressSessionToNextQuestion(sessionUUID, DEFAULT_CORRECT_SCORE);
    }

    /**
     * Updates the specified session by progressing to the next question.
     *
     * @param sessionUUID the ID of the session to be progressed
     * @param correctScore the score increment to be applied
     * @return true of there is indeed a next question, false otherwise (i.e. if that was the last question)
     */
    static public boolean updateScoreAndProgressSessionToNextQuestion(final String sessionUUID, final long correctScore)
    {
        final Session session = getSession(sessionUUID);
        final long newScore = session.getScore() + correctScore;

        final String currentQuestionUUID = session.getCurrentQuestionUUID();
        if("".equals(currentQuestionUUID)) // already finished the questions sequence in this session
        {
            return false;
        }
        else
        {
            MemcacheServiceFactory.getMemcacheService().delete(sessionUUID); // invalidate cache entry

            final String categoryUUID = session.getCategoryUUID();
            final Vector<Question> questions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(categoryUUID);
            final int numOfQuestions = questions.size();
            for(int i = 0; i < numOfQuestions; i++)
            {
                final Question question = questions.elementAt(i);
                if(question.getUUID().equals(currentQuestionUUID))
                {
                    if(i == numOfQuestions-1) // finished the questions sequence in this session
                    {
                        final Category category = CategoryFactory.getCategory(categoryUUID);
                        final long finishTime = System.currentTimeMillis() - category.getValidFrom();
                        updateSessionWithNextQuestionUUIDScoreAndFinishTime(sessionUUID, "", newScore, finishTime);
                        return false;
                    }
                    else
                    {
                        final String nextQuestionUUID = questions.elementAt(i+1).getUUID();
                        updateSessionWithNextQuestionUUIDScoreAndFinishTime(sessionUUID, nextQuestionUUID, newScore, 0L);
                        return true;
                    }
                }
            }

            // normally, this line would never execute
            log.severe("Error while progressing session with UUID: " + sessionUUID + " to the next question (currentQuestionUUID: " + currentQuestionUUID + ")");
            return false;
        }
    }

    public static final long DEFAULT_WRONG_SCORE = -3L;

    /**
     * Updates the specified session by staying at the same question while decreasing the score.
     *
     * @param sessionUUID the ID of the session to be progressed
     * @param wrongScore the score increment to be applied (must be negative)
     */
    static public void updateScoreAndKeepSessionToSameQuestion(final String sessionUUID, final long wrongScore)
    {
        final Session session = getSession(sessionUUID);
        final long newScore = session.getScore() + wrongScore;

        MemcacheServiceFactory.getMemcacheService().delete(sessionUUID); // invalidate cache entry
        updateSessionWithScore(sessionUUID, newScore);
    }

    /**
     * Skips the current question in this session by progressing to the next question.
     *
     * @param sessionUUID the ID of the session to be progressed
     * @return true of there is indeed a next question, false otherwise (i.e. if that was the last question)
     */
    static public boolean updateScoreAndSkipSessionToNextQuestion(final String sessionUUID)
    {
        final Session session = getSession(sessionUUID);

        final String currentQuestionUUID = session.getCurrentQuestionUUID();
        if("".equals(currentQuestionUUID)) // already finished the questions sequence in this session
        {
            return false;
        }
        else
        {
            MemcacheServiceFactory.getMemcacheService().delete(sessionUUID); // invalidate cache entry

            final long newScore = session.getScore() - 5L;

            final String categoryUUID = session.getCategoryUUID();
            final Vector<Question> questions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(categoryUUID);
            final int numOfQuestions = questions.size();
            for(int i = 0; i < numOfQuestions; i++)
            {
                final Question question = questions.elementAt(i);
                if(question.getUUID().equals(currentQuestionUUID))
                {
                    if(i == numOfQuestions-1) // finished the questions sequence in this session
                    {
                        final Category category = CategoryFactory.getCategory(categoryUUID);
                        final long finishTime = System.currentTimeMillis() - category.getValidFrom();
                        updateSessionWithNextQuestionUUIDScoreAndFinishTime(sessionUUID, "", newScore, finishTime);
                        return false;
                    }
                    else
                    {
                        final String nextQuestionUUID = questions.elementAt(i+1).getUUID();
                        updateSessionWithNextQuestionUUIDScoreAndFinishTime(sessionUUID, nextQuestionUUID, newScore, 0L);
                        return true;
                    }
                }
            }

            // normally, this line would never execute
            log.severe("Error while skipping question in session with UUID: " + sessionUUID + " to the next question (currentQuestionUUID: " + currentQuestionUUID + ")");
            return false;
        }
    }

    static private void updateSessionWithNextQuestionUUIDScoreAndFinishTime(final String sessionUUID,
                                                                  final String nextQuestionUUID,
                                                                  final long score,
                                                                  final long finishTime)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(sessionUUID));
            sessionEntity.setProperty(PROPERTY_CURRENT_QUESTION_UUID, nextQuestionUUID);
            sessionEntity.setProperty(PROPERTY_SCORE, score);
            sessionEntity.setProperty(PROPERTY_FINISH_TIME, finishTime);

            datastoreService.put(sessionEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + sessionUUID);
        }
        catch (IllegalArgumentException iae)
        {
            log.warning("Invalid argument " + iae.getMessage());
        }
    }

    static private void updateSessionWithScore(final String sessionUUID, final long score)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity sessionEntity = datastoreService.get(KeyFactory.stringToKey(sessionUUID));
            sessionEntity.setProperty(PROPERTY_SCORE, score);

            datastoreService.put(sessionEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + sessionUUID);
        }
        catch (IllegalArgumentException iae)
        {
            log.warning("Invalid argument " + iae.getMessage());
        }
    }

    static public Key addSession(final String playerName,
                                 final String appID,
                                 final String categoryUUID,
                                 final String currentQuestionUUID,
                                 final String name1,
                                 final String email1)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity sessionEntity = new Entity(KIND);
        sessionEntity.setProperty(PROPERTY_PLAYER_NAME, playerName);
        sessionEntity.setProperty(PROPERTY_APP_ID, appID);
        sessionEntity.setProperty(PROPERTY_CATEGORY_UUID, categoryUUID);
        sessionEntity.setProperty(PROPERTY_CURRENT_QUESTION_UUID, currentQuestionUUID);
        sessionEntity.setProperty(PROPERTY_SCORE, 0);
        sessionEntity.setProperty(PROPERTY_FINISH_TIME, 0);
        sessionEntity.setProperty(PROPERTY_NAME1, name1);
        sessionEntity.setProperty(PROPERTY_EMAIL1, email1);

        return datastoreService.put(sessionEntity);
    }

    static public Session getFromEntity(final Entity entity)
    {
        return new Session(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_PLAYER_NAME),
                (String) entity.getProperty(PROPERTY_APP_ID),
                (String) entity.getProperty(PROPERTY_CATEGORY_UUID),
                (String) entity.getProperty(PROPERTY_CURRENT_QUESTION_UUID),
                (Long) entity.getProperty(PROPERTY_SCORE),
                (Long) entity.getProperty(PROPERTY_FINISH_TIME),
                entity.hasProperty(PROPERTY_NAME1) ? (String) entity.getProperty(PROPERTY_NAME1) : "",
                entity.hasProperty(PROPERTY_EMAIL1) ? (String) entity.getProperty(PROPERTY_EMAIL1) : "");
    }
}