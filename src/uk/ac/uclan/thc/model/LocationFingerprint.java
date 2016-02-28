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
 * Date: 25/09/13
 * Time: 21:46
 */
public class LocationFingerprint implements Serializable
{
    private final String uuid;
    private final long timestamp;
    private final double lat;
    private final double lng;
    private final String sessionUUID;

    public LocationFingerprint(final String uuid, final long timestamp, final double lat, final double lng, final String sessionUUID)
    {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;
        this.sessionUUID = sessionUUID;
    }

    public String getUUID()
    {
        return uuid;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }

    public String getSessionUUID()
    {
        return sessionUUID;
    }
}