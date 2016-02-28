<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="uk.ac.uclan.thc.model.Location" %>
<%@ page import="uk.ac.uclan.thc.data.LocationFactory" %>
<%@ page import="java.util.Date" %>
<%@ page import="uk.ac.uclan.thc.model.Floor" %>
<%@ page import="uk.ac.uclan.thc.data.FloorFactory" %>
<%@ page import="java.util.Vector" %>
<%--
  ~ This file is part of UCLan-THC server.
  ~
  ~     UCLan-THC server is free software: you can redistribute it and/or
  ~     modify it under the terms of the GNU General Public License as
  ~     published by the Free Software Foundation, either version 3 of
  ~     the License, or (at your option) any later version.
  ~
  ~     UCLan-THC server is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<%--
  User: Nearchos Paspallis
  Date: 11/09/13
  Time: 11:59
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <title>UCLan Treasure Hunt Challenge - Location</title>
    </head>

    <body>

<%@ include file="Authenticate.jsp" %>

<%
    if(userEntity == null)
    {
%>
        You are not logged in!
<%
    }
    else if(!userEntity.isAdmin())
    {
%>
        You are not admin!
<%
    }
    else
    {
        String key = request.getParameter("uuid");
        final Location location = LocationFactory.getLocation(key);
        final long now = System.currentTimeMillis();

        final Vector<Floor> floors = FloorFactory.getFloors(location.getUUID());
%>
        <p><a href="/admin/locations">Back to all locations</a></p>

        <hr/>
        <h1>Loation: <%= location.getName() %></h1>
        <p><b>UUID</b>: <%= location.getUUID() %></p>
        <p><b>Name</b>: <%= location.getName() %></p>
        <p><b>Created by</b>: <%= user.getEmail() %></p>
        <p><b>Admin or owner access only</b>: <%= location.isAdminOrOwnerAccessOnly() %></p>
        <p><b>Timestamp</b>: <%= location.getTimestampAsString() %></p>

        <hr/>
        <form action="/admin/edit-location" method="post" onsubmit="editButton.disabled = true; return true;">
            <table>
                <tr>
                    <td>NAME</td>
                    <td><input type="text" name="<%= LocationFactory.PROPERTY_NAME %>" value="<%= location.getName() %>"/></td>
                </tr>
                <tr>
                    <td>NUM OF FLOORS</td>
                    <td><%= floors.size() %></td>
                </tr>
                <tr>
                    <td>CREATED BY</td>
                    <td><%= location.getCreatedBy() %></td>
                </tr>
                <tr>
                    <td>ADMIN OR OWNER ACCESS ONLY</td>
                    <td><input type="checkbox" name="<%=LocationFactory.PROPERTY_ADMIN_OR_OWNER_ACCESS_ONLY%>" value="true" <%=location.isAdminOrOwnerAccessOnly() ? "checked" : ""%>/></td>
                </tr>
                <tr>
                    <td>TIMESTAMP</td>
                    <td><input type="datetime-local" name="<%= LocationFactory.PROPERTY_TIMESTAMP%>" value="<%= Location.SIMPLE_DATE_FORMAT.format(new Date(now)) %>"/></td>
                </tr>
            </table>
            <div><input type="submit" value="Edit location" name="editButton" /></div>
            <input type="hidden" name="<%= LocationFactory.PROPERTY_UUID %>" value="<%= location.getUUID() %>"/>
            <input type="hidden" name="<%= LocationFactory.PROPERTY_CREATED_BY %>" value="<% location.getCreatedBy(); %>" />
        </form>

        <hr/>
        <form action="/admin/delete-entity" onsubmit="deleteButton.disabled = true; return true;">
            <div><input type="submit" value="Delete location" name="deleteButton"/></div>
            <input type="hidden" name="<%= LocationFactory.PROPERTY_UUID %>" value="<%= location.getUUID() %>"/>
            <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/locations", "UTF-8") %>"/>
        </form>

        <hr/>
        <h2>Floors</h2>
        <table border="1">
            <tr>
                <th>UUID</th>
                <th>NAME</th>
                <th>ORDER</th>
                <th>IMAGE URL</th>
                <th>TOP LEFT</th>
                <th>BOTTOM RIGHT</th>
                <th></th>
                <th></th>
            </tr>
            <%
                for(final Floor floor : floors)
                {
            %>
            <tr>
                <td><a href="/admin/floor?uuid=<%=floor.getUUID()%>"><%= floor.getUUID() %></a></td>
                <td><%= floor.getName() %></td>
                <td><%= floor.getOrder() %></td>
                <td><%= floor.getImageURL() %></td>
                <td>(<%= floor.getTopLeftLat() %>, <%= floor.getTopLeftLng() %>)</td>
                <td>(<%= floor.getBottomRightLat() %>, <%= floor.getBottomRightLng() %>)</td>
                <td>
                    <form action="/admin/floor">
                        <div><input type="submit" value="Edit" /></div>
                        <input type="hidden" name="<%= FloorFactory.PROPERTY_UUID %>" value="<%= floor.getUUID() %>"/>
                    </form>
                </td>
                <td>
                    <form action="/admin/delete-entity">
                        <div><input type="submit" value="Delete" /></div>
                        <input type="hidden" name="<%= FloorFactory.PROPERTY_UUID %>" value="<%= floor.getUUID() %>"/>
                        <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/location?uuid=" + location.getUUID(), "UTF-8") %>"/>
                    </form>
                </td>
            </tr>
            <%
                }
            %>
        </table>

        <hr/>

        <form action="/admin/edit-floor" method="post" onsubmit="editFloorButton.disabled = true; return true;">
            <table>
                <tr>
                    <td>NAME</td>
                    <td><input type="text" name="<%= FloorFactory.PROPERTY_FLOOR_NAME %>" /></td>
                </tr>
                <tr>
                    <td>ORDER</td>
                    <td><input type="number" name="<%= FloorFactory.PROPERTY_FLOOR_ORDER%>" /></td>
                </tr>
                <tr>
                    <td>IMAGE URL</td>
                    <td><input type="text" name="<%= FloorFactory.PROPERTY_FLOOR_IMAGE_URL%>" /></td>
                </tr>
                <tr>
                    <td>TOP LEFT LAT</td>
                    <td><input type="number" step="0.000001" name="<%= FloorFactory.PROPERTY_FLOOR_TOP_LEFT_LAT%>" /></td>
                </tr>
                <tr>
                    <td>TOP LEFT LNG</td>
                    <td><input type="number" step="0.000001" name="<%= FloorFactory.PROPERTY_FLOOR_TOP_LEFT_LNG %>" /></td>
                </tr>
                <tr>
                    <td>BOTTOM RIGHT LAT</td>
                    <td><input type="number" step="0.000001" name="<%= FloorFactory.PROPERTY_FLOOR_BOTTOM_RIGHT_LAT %>" /></td>
                </tr>
                <tr>
                    <td>BOTTOM RIGHT LNG</td>
                    <td><input type="number" step="0.000001" name="<%= FloorFactory.PROPERTY_FLOOR_BOTTOM_RIGHT_LNG %>" /></td>
                </tr>
            </table>
            <div><input type="submit" value="Add floor" name="editFloorButton"/></div>
            <input type="hidden" name="<%= FloorFactory.PROPERTY_FLOOR_LOCATION_UUID %>" value="<%= location.getUUID() %>" />
        </form>

        <%
    }
%>
    </body>

</html>