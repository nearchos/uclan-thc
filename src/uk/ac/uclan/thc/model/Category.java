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
public class Category implements Serializable
{
    private String uuid;
    private String name;
    private String createdBy;
    private long validFrom;
    private long validUntil;
    final String code;
    final String locationUUID;

    public Category(final String uuid, final String name, final String createdBy, final long validFrom, final long validUntil, final String code, final String locationUUID)
    {
        this.uuid = uuid;
        this.name = name;
        this.createdBy = createdBy;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.code = code;
        this.locationUUID = locationUUID;
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

    public long getValidFrom()
    {
        return validFrom;
    }

    public boolean hasFinished()
    {
        final long now = System.currentTimeMillis();
        return now > validUntil;
    }

    public long getValidUntil() // todo investigate if this is used or not
    {
        return validUntil;
    }

    public String getCode()
    {
        return code == null ? "" : code;
    }

    public String getLocationUUID() { return locationUUID == null ? "" : locationUUID; }

    public boolean isActiveNow()
    {
        final long now = System.currentTimeMillis();
        return now >= validFrom && now <= validUntil;
    }

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public String getValidFromAsString()
    {
        return SIMPLE_DATE_FORMAT.format(new Date(validFrom));
    }

    public String getValidUntilAsString()
    {
        return SIMPLE_DATE_FORMAT.format(new Date(validUntil));
    }

}