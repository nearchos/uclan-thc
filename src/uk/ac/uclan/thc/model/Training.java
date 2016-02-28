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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Nearchos Paspallis
 * Date: 11/09/13
 * Time: 10:06
 */
public class Training implements Serializable
{
    private final String uuid;
    private final String createdBy;
    private final String locationUUID;
    private final String floorUUID;
    private final long timestamp;
    private final String contextAsJSON;
    private final double lat;
    private final double lng;

    public Training(final String uuid, final String createdBy, final String locationUUID, final String floorUUID, final long timestamp, final String contextAsJSON, final double lat, final double lng)
    {
        this.uuid = uuid;
        this.createdBy = createdBy;
        this.locationUUID = locationUUID;
        this.floorUUID = floorUUID;
        this.timestamp = timestamp;
        this.contextAsJSON = contextAsJSON;
        this.lat = lat;
        this.lng = lng;
    }

    public String getUUID() { return uuid; }

    public String getCreatedBy() { return createdBy; }

    public String getLocationUUID() { return locationUUID; }

    public String getFloorUUID() { return floorUUID; }

    public long getTimestamp() { return timestamp; }

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public String getTimestampAsString() { return SIMPLE_DATE_FORMAT.format(new Date(timestamp)); }

    public String getContextAsJSON() { return  contextAsJSON; }

    public double getLat() { return lat; }

    public double getLng() { return lng; }

    @Override public String toString()
    {
        return uuid + " created by '" + createdBy + "' on " + new Date(timestamp);
    }
}