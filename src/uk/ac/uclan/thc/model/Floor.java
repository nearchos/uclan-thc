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
 * Date: 16/Jun/14
 * Time: 16:27
 */
public class Floor implements Serializable
{
    private final String uuid;
    private final String name;
    private final long order;
    private final String locationUUID;
    private final String imageURL;
    private final double topLeftLat;
    private final double topLeftLng;
    private final double bottomRightLat;
    private final double bottomRightLng;

    public Floor(final String uuid, final String name, final long order, final String locationUUID, final String imageURL,
                 final double topLeftLat, final double topLeftLng, final double bottomRightLat, final double bottomRightLng)
    {
        this.uuid = uuid;
        this.name = name;
        this.order = order;
        this.locationUUID = locationUUID;
        this.imageURL = imageURL;
        this.topLeftLat = topLeftLat;
        this.topLeftLng = topLeftLng;
        this.bottomRightLat = bottomRightLat;
        this.bottomRightLng = bottomRightLng;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public long getOrder() { return order; }

    public String getLocationUUID() { return locationUUID; }

    public String getImageURL() { return imageURL; }

    public double getTopLeftLat() { return topLeftLat; }

    public double getTopLeftLng() { return topLeftLng; }

    public double getBottomRightLat() { return bottomRightLat; }

    public double getBottomRightLng() { return bottomRightLng; }
}