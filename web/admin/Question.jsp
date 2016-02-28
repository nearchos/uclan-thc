<%@ page import="uk.ac.uclan.thc.model.Question" %>
<%@ page import="uk.ac.uclan.thc.data.QuestionFactory" %>
<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="java.net.URLEncoder" %>
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
        <title>UCLan Treasure Hunt Challenge - Question</title>
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
        final Question question = QuestionFactory.getQuestion(key);
%>

<p>Got question: <%=question.toString()%></p>
        <p><a href="/admin/category?uuid=<%=question.getCategoryUUID()%>">Back to category <%=question.getCategoryUUID()%></a></p>

        <form action="/admin/edit-question" method="post" onsubmit="editQuestionButton.disabled = true; return true;">
            <table>
                <tr>
                    <th>SEQ NUMBER</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_SEQ_NUMBER%>" value="<%=question.getSeqNumber()%>" /></td>
                </tr>
                <tr>
                    <th>TEXT</th>
                    <td><textarea name="<%= QuestionFactory.PROPERTY_TEXT %>" cols="80" rows="10"><%=question.getText()%></textarea></td>
                    <%--<td><input type="text" style='word-break: break-word;' name="<%= QuestionFactory.PROPERTY_TEXT %>" value="<%=question.getText()%>"/></td>--%>
                </tr>
                <tr>
                    <th>CORRECT ANSWER</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_CORRECT_ANSWER %>" value="<%=question.getCorrectAnswer()%>"/></td>
                </tr>
                <tr>
                    <th>CORRECT SCORE</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_CORRECT_SCORE%>" value="<%=question.getCorrectScore()%>"/></td>
                </tr>
                <tr>
                    <th>WRONG SCORE</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_WRONG_SCORE%>" value="<%=question.getWrongScore()%>"/></td>
                </tr>
                <tr>
                    <th>SKIP SCORE</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_SKIP_SCORE%>" value="<%=question.getSkipScore()%>"/></td>
                </tr>
                <tr>
                    <th>LATITUDE</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_LATITUDE %>" value="<%=question.getLatitude()%>"/></td>
                </tr>
                <tr>
                    <th>LONGITUDE</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_LONGITUDE %>" value="<%=question.getLongitude()%>"/></td>
                </tr>
                <tr>
                    <th>DISTANCE THRESHOLD</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_DISTANCE_THRESHOLD%>" value="<%=question.getDistanceThreshold()%>"/></td>
                </tr>
            </table>
            <div><input type="submit" value="Edit question" name="editQuestionButton"/></div>
            <input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID%>" value="<%= question.getUUID() %>" />
            <input type="hidden" name="<%= QuestionFactory.PROPERTY_CATEGORY_UUID %>" value="<%= question.getCategoryUUID() %>" />
        </form>

        <hr/>

        <form action="/admin/delete-entity" onsubmit="deleteButton.disabled = true; return true;">
            <div><input type="submit" value="Delete question" name="deleteButton"/></div>
            <input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID %>" value="<%= question.getUUID() %>"/>
            <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/category?uuid=" + question.getCategoryUUID(), "UTF-8") %>"/>
        </form>

        <%
    }
%>
    </body>

</html>