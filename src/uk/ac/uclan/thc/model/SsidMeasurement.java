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
 * Date: 11/09/13
 * Time: 10:06
 */
public class SsidMeasurement implements Serializable
{
    private final String uuid;
    private final String trainingUUID;
    private final String macAddress;
    private final double ssid;

    public SsidMeasurement(final String uuid, final String trainingUUID, final String macAddress, final double ssid)
    {
        this.uuid = uuid;
        this.trainingUUID = trainingUUID;
        this.macAddress = macAddress;
        this.ssid = ssid;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getTrainingUUID() { return trainingUUID; }

    public String getMacAddress() { return macAddress; }

    public double getSsid() { return ssid; }

    @Override public String toString()
    {
        return macAddress + " -> " + ssid;
    }
}