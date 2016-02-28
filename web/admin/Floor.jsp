<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="uk.ac.uclan.thc.model.Floor" %>
<%@ page import="uk.ac.uclan.thc.data.FloorFactory" %>
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
  Date: 17/06/14
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <title>UCLan Treasure Hunt Challenge - Floor</title>
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
        final Floor floor = FloorFactory.getFloor(key);
%>

        <p><a href="/admin/location?uuid=<%=floor.getLocationUUID()%>">Back to location <%=floor.getLocationUUID()%></a></p>

        <form action="/admin/edit-floor" method="post" onsubmit="editFloorButton.disabled = true; return true;">
            <table>
                <tr>
                    <th>NAME</th>
                    <td><input type="text" name="<%= FloorFactory.PROPERTY_FLOOR_NAME %>" value="<%=floor.getName()%>"/></td>
                </tr>
                <tr>
                    <th>ORDER</th>
                    <td><input type="number" name="<%= FloorFactory.PROPERTY_FLOOR_ORDER %>" value="<%=floor.getOrder()%>"/></td>
                </tr>
                <tr>
                    <th>IMAGE URL</th>
                    <td><input type="text" name="<%= FloorFactory.PROPERTY_FLOOR_IMAGE_URL %>" value="<%=floor.getImageURL()%>"/></td>
                </tr>
                <tr>
                    <th>TOP LEFT LAT</th>
                    <td><input type="number" name="<%= FloorFactory.PROPERTY_FLOOR_TOP_LEFT_LAT %>" value="<%=floor.getTopLeftLat() %>"/></td>
                </tr>
                <tr>
                    <th>TOP LEFT LNG</th>
                    <td><input type="number" name="<%= FloorFactory.PROPERTY_FLOOR_TOP_LEFT_LNG %>" value="<%=floor.getTopLeftLng() %>"/></td>
                </tr>
                <tr>
                    <th>BOTTOM RIGHT LAT</th>
                    <td><input type="number" name="<%= FloorFactory.PROPERTY_FLOOR_BOTTOM_RIGHT_LAT%>" value="<%=floor.getBottomRightLat()%>"/></td>
                </tr>
                <tr>
                    <th>BOTTOM RIGHT LNG</th>
                    <td><input type="number" name="<%= FloorFactory.PROPERTY_FLOOR_BOTTOM_RIGHT_LNG%>" value="<%=floor.getBottomRightLng()%>"/></td>
                </tr>
            </table>
            <div><input type="submit" value="Edit floor" name="editFloorButton"/></div>
            <input type="hidden" name="<%= FloorFactory.PROPERTY_UUID%>" value="<%= floor.getUUID() %>" />
            <input type="hidden" name="<%= FloorFactory.PROPERTY_FLOOR_LOCATION_UUID%>" value="<%= floor.getLocationUUID() %>" />
        </form>

        <hr/>

        <form action="/admin/delete-entity" onsubmit="deleteButton.disabled = true; return true;">
            <div><input type="submit" value="Delete question" name="deleteButton"/></div>
            <input type="hidden" name="<%= FloorFactory.PROPERTY_UUID %>" value="<%= floor.getUUID() %>"/>
            <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/location?uuid=" + floor.getLocationUUID(), "UTF-8") %>"/>
        </form>

        <%
    }
%>
    </body>

</html>