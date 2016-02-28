<%@ page import="java.util.Vector" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="uk.ac.uclan.thc.data.*" %>
<%@ page import="uk.ac.uclan.thc.model.Training" %>
<%@ page import="uk.ac.uclan.thc.model.Location" %>
<%@ page import="uk.ac.uclan.thc.model.Floor" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
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
        final String locationUUID = request.getParameter(TrainingFactory.PROPERTY_LOCATION_UUID);
        final Location location = LocationFactory.getLocation(locationUUID);
        final Vector<Training> trainings = TrainingFactory.geTrainingsForLocation(locationUUID);
        final Vector<Floor> floors =  FloorFactory.getFloors(locationUUID);
        final Map<String,String> floorUUIDToName = new HashMap<String, String>();
        for(final Floor floor : floors)
        {
            floorUUIDToName.put(floor.getUUID(), floor.getName());
        }
%>

<h1>Trainings</h1>

<p>Location: <%=location.getName()%> (created by: <%=location.getCreatedBy()%> on <%=location.getTimestampAsString()%>)</p>
<p>Number of trainings: <%=trainings.size()%></p>

<%
    final Map<String,Integer> creatorToTrainingsSize = new HashMap<String, Integer>();
    final Map<String,Integer> floorUUIDToTrainingsSize = new HashMap<String, Integer>();
    for(final Training training : trainings)
    {
        if(!creatorToTrainingsSize.containsKey(training.getCreatedBy()))
        {
            creatorToTrainingsSize.put(training.getCreatedBy(), 1);
        }
        else {
            creatorToTrainingsSize.put(training.getCreatedBy(), creatorToTrainingsSize.get(training.getCreatedBy()) + 1);
        }

        if(!floorUUIDToTrainingsSize.containsKey(training.getFloorUUID()))
        {
            floorUUIDToTrainingsSize.put(training.getFloorUUID(), 1);
        }
        else
        {
            floorUUIDToTrainingsSize.put(training.getFloorUUID(), floorUUIDToTrainingsSize.get(training.getFloorUUID()) + 1);
        }
    }
%>

<h3>Statistics</h3>
<b>CREATED BY</b>
<ul>
<%
    for(final String createdBy : creatorToTrainingsSize.keySet())
    {
%>
        <li><b><%=createdBy%></b>: <%=creatorToTrainingsSize.get(createdBy)%></li>
<%
    }
%>
</ul>

<b>FLOORS</b>
<ul>
<%
    for(final String floorUUID : floorUUIDToTrainingsSize.keySet())
    {
%>
        <li><b><%=floorUUIDToName.get(floorUUID)%></b>: <%=floorUUIDToTrainingsSize.get(floorUUID)%></li>
<%
    }
%>
</ul>

<p/>

<table border="1">
    <tr>
        <th>UUID</th>
        <th>COORDINATES</th>
        <th>FLOOR</th>
        <th>CREATED BY</th>
        <th>TIMESTAMP</th>
    </tr>
    <%
        for(final Training training : trainings)
        {
    %>
    <tr>
        <td><%= training.getUUID() %></td>
        <td>(<a href="http://maps.google.com/maps?q=<%=training.getLat()%>,<%=training.getLng()%>"><%=training.getLat()%>,<%=training.getLng()%></a>)</td>
        <td><%=floorUUIDToName.get(training.getFloorUUID())%></td>
        <td><%= training.getCreatedBy() %></td>
        <td><%= training.getTimestampAsString() %></td>
        <td>
            <form action="/admin/training">
                <div><input type="submit" value="Edit" /></div>
                <input type="hidden" name="<%= TrainingFactory.PROPERTY_UUID %>" value="<%= training.getUUID() %>"/>
            </form>
        </td>
        <td>
            <form action="/admin/delete-entity">
                <div><input type="submit" value="Delete" /></div>
                <input type="hidden" name="<%= TrainingFactory.PROPERTY_UUID %>" value="<%= training.getUUID() %>"/>
                <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/trainings?training_location_uuid=" + training.getLocationUUID(), "UTF-8") %>"/>
            </form>
        </td>
    </tr>
    <%
        }

//        final long now = System.currentTimeMillis();
//        final long SEVEN_DAYS = 7L * 24L * 60L * 60L * 1000L;
    %>
</table>

<hr/>

<%--<form action="/admin/edit-location" method="post" onsubmit="submitButton.disabled = true; return true;">--%>
    <%--<table>--%>
        <%--<tr>--%>
            <%--<td>NAME</td>--%>
            <%--<td><input type="text" name="<%= LocationFactory.PROPERTY_NAME%>" required/></td>--%>
        <%--</tr>--%>
        <%--<tr>--%>
            <%--<td>CREATED BY</td>--%>
            <%--<td><%= userEmail %></td>--%>
        <%--</tr>--%>
        <%--<tr>--%>
            <%--<td>TIMESTAMP</td>--%>
            <%--<td><input type="datetime-local" name="<%= LocationFactory.PROPERTY_TIMESTAMP%>" value="<%= Location.SIMPLE_DATE_FORMAT.format(new Date(now)) %>"/></td>--%>
        <%--</tr>--%>
        <%--<tr><td colspan="2"><i>Please note that all times are in <a href="http://en.wikipedia.org/wiki/UTC">UTC (Coordinated Universal Time)</a></i></td></tr>--%>
    <%--</table>--%>
    <%--<div><input type="submit" name="submitButton" value="Add location" /></div>--%>
<%--</form>--%>

<%
    }
%>

</body>
</html>