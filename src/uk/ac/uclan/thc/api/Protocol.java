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

package uk.ac.uclan.thc.api;

import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 24/09/13
 * Time: 19:27
 */
public class Protocol
{
    public static final Logger log = Logger.getLogger(Protocol.class.getCanonicalName());

    public static final String EOL = System.getProperty("line.separator");

    public static final String OUTPUT_TYPE              = "type";
    public static final String OUTPUT_TYPE_CSV          = "csv";
    public static final String OUTPUT_TYPE_JSON         = "json";

    static public String getCsvStatus(final String status, final String message)
    {
        return status + "," + message;
    }

    static public String getJsonStatus(final String status, final String message)
    {
        return "{ \"status\": \"" + status + "\", \"message\": \"" + message + "\" }";
    }
}