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

package uk.ac.uclan.thc.model;

import java.io.Serializable;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 22:17
 */
public class Session implements Serializable
{
    private final String uuid;
    private final String playerName;
    private final String appID;
    private final String categoryUUID;
    private final String currentQuestionUUID;
    private final long score;
    private final long finishTime; // in milliseconds
    private final String name1;
    private final String email1;
    private final String gender;

    public Session(final String uuid,
                   final String playerName,
                   final String appID,
                   final String categoryUUID,
                   final String currentQuestionUUID,
                   final long score,
                   final long finishTime,
                   final String name1,
                   final String email1,
                   final String gender)
    {
        this.uuid = uuid;
        this.playerName = playerName;
        this.appID = appID;
        this.categoryUUID = categoryUUID;
        this.currentQuestionUUID = currentQuestionUUID;
        this.score = score;
        this.finishTime = finishTime;
        this.name1 = name1;
        this.email1 = email1;
        this.gender = gender;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public String getAppID()
    {
        return appID;
    }

    public String getCategoryUUID()
    {
        return categoryUUID;
    }

    public String getCurrentQuestionUUID()
    {
        return currentQuestionUUID;
    }

    public long getScore()
    {
        return score;
    }

    public long getFinishTime()
    {
        return finishTime;
    }

    public String getName1()
    {
        return name1;
    }

    public String getEmail1()
    {
        return email1;
    }

    public String getGender() {
        return gender;
    }
}