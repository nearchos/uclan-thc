<%@ page import="java.util.Vector" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="java.util.Date" %>
<%@ page import="uk.ac.uclan.thc.model.Location" %>
<%@ page import="uk.ac.uclan.thc.data.*" %>
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
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 11/09/13
  Time: 09:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <title>UCLan Treasure Hunt Challenge - Locations</title>
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
        final Vector<Location> locations = LocationFactory.getAllLocations();
%>

<h1>Locations</h1>

<p>Number of locations: <%=locations.size()%></p>

<table border="1">
    <tr>
        <th>UUID</th>
        <th>NAME</th>
        <th>CREATED BY</th>
        <th>ADMIN OR OWNER ACCESS ONLY</th>
        <th>TIMESTAMP</th>
    </tr>
    <%
        if(locations != null)
        {
            for(final Location location : locations)
            {
    %>
    <tr>
        <td><a href="/admin/location?uuid=<%= location.getUUID() %>"><%= location.getUUID() %></a></td>
        <td><%= location.getName() %></td>
        <td><%= location.getCreatedBy() %></td>
        <td><%= location.isAdminOrOwnerAccessOnly() %></td>
        <td><%= location.getTimestampAsString() %></td>
        <td>
            <form action="/admin/trainings">
                <div><input type="submit" value="Trainings" /></div>
                <input type="hidden" name="<%= TrainingFactory.PROPERTY_LOCATION_UUID %>" value="<%= location.getUUID() %>"/>
            </form>
        </td>
        <td>
            <form action="/admin/location">
                <div><input type="submit" value="Edit" /></div>
                <input type="hidden" name="<%= LocationFactory.PROPERTY_UUID %>" value="<%= location.getUUID() %>"/>
            </form>
        </td>
        <td>
            <form action="/admin/delete-entity">
                <div><input type="submit" value="Delete" /></div>
                <input type="hidden" name="<%= LocationFactory.PROPERTY_UUID %>" value="<%= location.getUUID() %>"/>
                <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/locations", "UTF-8") %>"/>
            </form>
        </td>
    </tr>
    <%
            }
        }

        final long now = System.currentTimeMillis();
        final long SEVEN_DAYS = 7L * 24L * 60L * 60L * 1000L;
    %>
</table>

<hr/>

<form action="/admin/edit-location" method="post" onsubmit="submitButton.disabled = true; return true;">
    <table>
        <tr>
            <td>NAME</td>
            <td><input type="text" name="<%= LocationFactory.PROPERTY_NAME%>" required/></td>
        </tr>
        <tr>
            <td>CREATED BY</td>
            <td><%= userEmail %></td>
        </tr>
        <tr>
            <td>ADMIN OR OWNER ACCESS ONLY</td>
            <td><input type="checkbox" name="<%=LocationFactory.PROPERTY_ADMIN_OR_OWNER_ACCESS_ONLY%>" value="true"/></td>
        </tr>
        <tr>
            <td>TIMESTAMP</td>
            <td><input type="datetime-local" name="<%= LocationFactory.PROPERTY_TIMESTAMP%>" value="<%= Location.SIMPLE_DATE_FORMAT.format(new Date(now)) %>"/></td>
        </tr>
        <tr><td colspan="2"><i>Please note that all times are in <a href="http://en.wikipedia.org/wiki/UTC">UTC (Coordinated Universal Time)</a></i></td></tr>
    </table>
    <div><input type="submit" name="submitButton" value="Add location" /></div>
</form>

<%
    }
%>

</body>
</html>