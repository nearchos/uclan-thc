<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="uk.ac.uclan.thc.data.CategoryFactory" %>
<%@ page import="uk.ac.uclan.thc.data.TimedQuestionFactory" %>
<%@ page import="uk.ac.uclan.thc.model.Category" %>
<%@ page import="uk.ac.uclan.thc.model.TimedQuestion" %>
<%@ page import="java.net.URLEncoder" %>
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
        <title>UCLan Treasure Hunt Challenge - Timed Question</title>
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
        final TimedQuestion timedQuestion = TimedQuestionFactory.getTimedQuestion(key);
%>

<p>Got question: <%=timedQuestion.getTitle()%></p>
        <p>Back to <a href="/admin/all-timed-questions">all timed questions</a></p>

        <form action="/admin/edit-timed-question" method="post" onsubmit="editTimedQuestionButton.disabled = true; return true;">
            <table>
                <tr>
                    <th>UUID</th>
                    <td><%=timedQuestion.getUUID()%></td>
                </tr>
                <tr>
                    <th>TITLE</th>
                    <td><input type="text" name="<%= TimedQuestionFactory.PROPERTY_TITLE%>" value="<%=timedQuestion.getTitle()%>"/></td>
                </tr>
                <tr>
                    <th>CREATED BY</th>
                    <td><%= timedQuestion.getCreatedBy() %><input type="hidden" name="<%=TimedQuestionFactory.PROPERTY_CREATED_BY%>" value="<%= timedQuestion.getCreatedBy() %>"></td>
                </tr>
                <tr>
                    <th>CATEGORY</th>
                    <td>
                        <select name="<%= TimedQuestionFactory.PROPERTY_CATEGORY_UUID%>">
                            <%
                                for(final Category category : CategoryFactory.getAllCategories())
                                {
                            %>
                            <option value="<%=category.getUUID()%>" <%= timedQuestion.getCategoryUUID().equals(category.getUUID()) ? "selected" : "" %>><%=category.getName()%></option>
                            <%
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>BODY</th>
                    <td><input type="text" name="<%= TimedQuestionFactory.PROPERTY_BODY%>" value="<%=timedQuestion.getBody()%>"/></td>
                </tr>
                <tr>
                    <th>IMAGE URL</th>
                    <td><input type="text" name="<%= TimedQuestionFactory.PROPERTY_IMAGE_URL%>" value="<%=timedQuestion.getImageUrl()%>"/></td>
                </tr>
            </table>
            <div><input type="submit" value="Edit question" name="editTimedQuestionButton"/></div>
            <input type="hidden" name="<%= TimedQuestionFactory.PROPERTY_UUID%>" value="<%= timedQuestion.getUUID() %>" />
        </form>

        <hr/>

        <form action="/admin/delete-entity" onsubmit="deleteButton.disabled = true; return true;">
            <div><input type="submit" value="Delete timed question" name="deleteButton"/></div>
            <input type="hidden" name="<%= TimedQuestionFactory.PROPERTY_UUID %>" value="<%= timedQuestion.getUUID() %>"/>
            <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/all-timed-questions", "UTF-8") %>"/>
        </form>

        <%
    }
%>
    </body>

</html>