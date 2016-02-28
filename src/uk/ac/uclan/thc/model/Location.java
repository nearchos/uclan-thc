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
 * Date: 16/Jun/14
 * Time: 16:27
 */
public class Location implements Serializable
{
    private String uuid;
    private String name;
    private String createdBy;
    private boolean adminOrOwnerAccessOnly;
    private long timestamp;

    public Location(final String uuid, final String name, final String createdBy, final boolean adminOrOwnerAccessOnly, final long timestamp)
    {
        this.uuid = uuid;
        this.name = name;
        this.createdBy = createdBy;
        this.adminOrOwnerAccessOnly = adminOrOwnerAccessOnly;
        this.timestamp = timestamp;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public boolean isAdminOrOwnerAccessOnly()
    {
        return adminOrOwnerAccessOnly;
    }

    public long getTimestamp() { return timestamp; }

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public String getTimestampAsString() { return SIMPLE_DATE_FORMAT.format(new Date(timestamp)); }
}