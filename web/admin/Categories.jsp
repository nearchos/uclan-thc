<%@ page import="java.util.Vector" %>
<%@ page import="uk.ac.uclan.thc.model.Category" %>
<%@ page import="uk.ac.uclan.thc.data.CategoryFactory" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="java.util.Date" %>
<%@ page import="uk.ac.uclan.thc.data.QuestionFactory" %>
<%@ page import="uk.ac.uclan.thc.data.SessionFactory" %>
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
        <title>UCLan Treasure Hunt Challenge - Categories</title>
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
<p><%=userEntity.getEmail()%>, you are not admin!</p>
<%
    }
    else
    {
        final Vector<Category> categories = CategoryFactory.getAllCategories();
%>

<h1>Categories</h1>

<p>Number of categories: <%=categories.size()%></p>

<table border="1">
    <tr>
        <th>UUID</th>
        <th>NAME</th>
        <th>NUM OF QUESTIONS</th>
        <th>CREATED BY</th>
        <th>VALID FROM</th>
        <th>VALID UNTIL</th>
        <th>CODE</th>
        <th>LOCATION UUID</th>
        <th>IS ACTIVE NOW</th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
    </tr>
    <%
        if(categories != null)
        {
            for(final Category category : categories)
            {
                int numOfQuestions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(category.getUUID()).size();
                final String categoryColor = category.isActiveNow() ? "green" : category.hasFinished() ? "red" : "blue";
    %>
    <tr>
        <td><a href="/admin/category?uuid=<%= category.getUUID() %>"><%= category.getUUID() %></a></td>
        <td><%= category.getName() %></td>
        <td><%= numOfQuestions %></td>
        <td><%= category.getCreatedBy() %></td>
        <td style="color:<%=categoryColor%>"><%= category.getValidFromAsString() %></td>
        <td style="color:<%=categoryColor%>"><%= category.getValidUntilAsString() %></td>
        <td><%= category.getCode() %></td>
        <td><%= category.getLocationUUID() %></td>
        <td><%= category.isActiveNow() %></td>
        <td>
                <div>
                    <input type="button" value="Sessions" onclick="window.open('/admin/sessions?<%=SessionFactory.PROPERTY_CATEGORY_UUID%>=<%=category.getUUID()%>')" />
                </div>
        </td>
        <td>
                <div>
                    <input type="button" value="Live map" onclick="window.open('/live/map.html?<%=CategoryFactory.PROPERTY_UUID%>=<%=category.getUUID()%>')" />
                </div>
        </td>
        <td>
            <form action="/admin/category">
                <div><input type="submit" value="Edit" /></div>
                <input type="hidden" name="<%= CategoryFactory.PROPERTY_UUID %>" value="<%= category.getUUID() %>"/>
            </form>
        </td>
        <td>
            <form action="/admin/delete-entity">
                <div><input type="submit" value="Delete" /></div>
                <input type="hidden" name="<%= CategoryFactory.PROPERTY_UUID %>" value="<%= category.getUUID() %>"/>
                <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/categories", "UTF-8") %>"/>
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

<form action="/admin/edit-category" method="post" onsubmit="submitButton.disabled = true; return true;">
    <table>
        <tr>
            <td>NAME</td>
            <td><input type="text" name="<%= CategoryFactory.PROPERTY_NAME%>" required/></td>
        </tr>
        <tr>
            <td>CREATED BY</td>
            <td><%= userEmail %></td>
        </tr>
        <tr>
            <td>VALID FROM</td>
            <td><input type="datetime-local" name="<%= CategoryFactory.PROPERTY_VALID_FROM%>" value="<%= Category.SIMPLE_DATE_FORMAT.format(new Date(now)) %>"/></td>
        </tr>
        <tr>
            <td>VALID UNTIL</td>
            <td><input type="datetime-local" name="<%= CategoryFactory.PROPERTY_VALID_UNTIL%>" value="<%= Category.SIMPLE_DATE_FORMAT.format(new Date(now+SEVEN_DAYS)) %>"/></td>
        </tr>
        <tr>
            <td>CODE</td>
            <td><input type="text" name="<%= CategoryFactory.PROPERTY_CODE%>"/></td>
        </tr>
        <tr>
            <td>LOCATION UUID</td>
            <td><input type="text" name="<%= CategoryFactory.PROPERTY_LOCATION_UUID%>"/></td>
        </tr>
        <tr><td colspan="2"><i>Please note that all times are in <a href="http://en.wikipedia.org/wiki/UTC">UTC (Coordinated Universal Time)</a></i></td></tr>
    </table>
    <div><input type="submit" name="submitButton" value="Add category" /></div>
</form>

<%
    }
%>

</body>
</html>