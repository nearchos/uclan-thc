<%@ page import="uk.ac.uclan.thc.data.CategoryFactory" %>
<%@ page import="uk.ac.uclan.thc.data.TimedQuestionFactory" %>
<%@ page import="uk.ac.uclan.thc.model.Category" %>
<%@ page import="uk.ac.uclan.thc.model.TimedQuestion" %>
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
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 26/10/15
  Time: 08:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <title>UCLan Treasure Hunt Challenge - All Timed Questions</title>
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
        final Vector<TimedQuestion> timedQuestions = TimedQuestionFactory.getAllTimedQuestions();
%>

<h1>Timed Questions</h1>

<p>Number of questions: <%=timedQuestions.size()%></p>

<table border="1">
    <tr>
        <th>UUID</th>
        <th>TITLE</th>
        <th>CREATED BY</th>
        <th>CATEGORY</th>
        <th>BODY</th>
        <th>IMAGE URL</th>
    </tr>
    <%
        if(timedQuestions != null)
        {
            for(final TimedQuestion timedQuestion : timedQuestions)
            {
    %>
    <tr>
        <td><a href="/admin/timed-question?uuid=<%= timedQuestion.getUUID() %>"><%= timedQuestion.getUUID() %></a></td>
        <td><%= timedQuestion.getTitle() %></td>
        <td><%= timedQuestion.getCreatedBy() %></td>
        <td><%= timedQuestion.getCategoryUUID() %> (<%= CategoryFactory.getCategory(timedQuestion.getCategoryUUID()).getName() %>)</td>
        <td><%= timedQuestion.getBody() %></td>
        <td><%= timedQuestion.getImageUrl() %></td>
    </tr>
    <%
            }
        }
    %>
</table>

<hr/>

<form action="/admin/edit-timed-question" method="post" onsubmit="submitButton.disabled = true; return true;">
    <table>
        <tr>
            <td>TITLE</td>
            <td><input type="text" name="<%= TimedQuestionFactory.PROPERTY_TITLE%>" required/></td>
        </tr>
        <tr>
            <td>CREATED BY</td>
            <td><%= userEmail %></td>
        </tr>
        <tr>
            <td>CATEGORY</td>
            <td>
                <select name="<%= TimedQuestionFactory.PROPERTY_CATEGORY_UUID%>">
                    <%
                        for(final Category category : CategoryFactory.getAllCategories())
                        {
                    %>
                    <option value="<%=category.getUUID()%>" ><%=category.getName()%></option>
                    <%
                        }
                    %>
                </select>
            </td>
        </tr>
        <tr>
            <td>BODY</td>
            <td><input type="text" name="<%= TimedQuestionFactory.PROPERTY_BODY%>" required/></td>
        </tr>
        <tr>
            <td>IMAGE URL</td>
            <td><input type="text" name="<%= TimedQuestionFactory.PROPERTY_IMAGE_URL%>" /></td>
        </tr>
    </table>
    <div><input type="submit" name="submitButton" value="Add timed question" /></div>
</form>

<%
    }
%>

</body>
</html>