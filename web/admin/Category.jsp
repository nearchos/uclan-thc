<%@ page import="uk.ac.uclan.thc.model.Category" %>
<%@ page import="uk.ac.uclan.thc.data.CategoryFactory" %>
<%@ page import="uk.ac.uclan.thc.model.Question" %>
<%@ page import="java.util.Vector" %>
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
        <title>UCLan Treasure Hunt Challenge - Category</title>
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
        final Category category = CategoryFactory.getCategory(key);
        final Vector<Question> questions = QuestionFactory.getAllQuestionsForCategoryOrderedBySeqNumber(category.getUUID());
%>
        <p><a href="/admin/categories">Back to all categories</a></p>

        <hr/>
        <h1>Category: <%= category.getName() %></h1>
        <p><b>UUID</b>: <%= category.getUUID() %></p>
        <p><b>Name</b>: <%= category.getName() %></p>
        <p><b>Created by</b>: <%= user.getEmail() %></p>
        <p><b>Valid from</b>: <%= category.getValidFromAsString() %></p>
        <p><b>Valid until</b>: <%= category.getValidUntilAsString() %></p>
        <p><b>Code</b>: <%= category.getCode() %></p>
        <p><b>Location UUID</b>: <%= category.getLocationUUID() %></p>
        <p><b>Is active now</b>: <%= category.isActiveNow() %></p>

        <hr/>
        <form action="/admin/edit-category" method="post" onsubmit="editButton.disabled = true; return true;">
            <table>
                <tr>
                    <td>NAME</td>
                    <td><input type="text" name="<%= CategoryFactory.PROPERTY_NAME %>" value="<%= category.getName() %>"/></td>
                </tr>
                <tr>
                    <td>NUM OF QUESTIONS</td>
                    <td><%= questions.size() %></td>
                </tr>
                <tr>
                    <td>CREATED BY</td>
                    <td><%= category.getCreatedBy() %></td>
                </tr>
                <tr>
                    <td>VALID FROM</td>
                    <td><input type="datetime-local" name="<%= CategoryFactory.PROPERTY_VALID_FROM%>" value="<%= category.getValidFromAsString() %>"/></td>
                </tr>
                <tr>
                    <td>VALID UNTIL</td>
                    <td><input type="datetime-local" name="<%= CategoryFactory.PROPERTY_VALID_UNTIL%>" value="<%= category.getValidUntilAsString() %>"/></td>
                </tr>
                <tr>
                    <td>CODE</td>
                    <td><input type="text" name="<%= CategoryFactory.PROPERTY_CODE%>" value="<%= category.getCode() %>"/></td>
                </tr>
                <tr>
                    <td>LOCATION UUID</td>
                    <td><input type="text" name="<%= CategoryFactory.PROPERTY_LOCATION_UUID%>" value="<%= category.getLocationUUID() %>"/></td>
                </tr>
                <tr>
                    <td>IS ACTIVE NOW</td>
                    <td><%= category.isActiveNow() %></td>
                </tr>
            </table>
            <div><input type="submit" value="Edit category" name="editButton" /></div>
            <input type="hidden" name="<%= CategoryFactory.PROPERTY_UUID %>" value="<%= category.getUUID() %>"/>
            <input type="hidden" name="<%= CategoryFactory.PROPERTY_CREATED_BY %>" value="<% category.getCreatedBy(); %>" />
        </form>

        <hr/>
        <form action="/admin/delete-entity" onsubmit="deleteButton.disabled = true; return true;">
            <div><input type="submit" value="Delete category" name="deleteButton"/></div>
            <input type="hidden" name="<%= CategoryFactory.PROPERTY_UUID %>" value="<%= category.getUUID() %>"/>
            <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/categories", "UTF-8") %>"/>
        </form>

        <hr/>
        <h2>Questions</h2>
        <table border="1">
            <tr>
                <th>UUID</th>
                <th>SEQ NUMBER</th>
                <th>TEXT</th>
                <th>CORRECT ANSWER</th>
                <th>CORRECT SCORE</th>
                <th>WRONG SCORE</th>
                <th>SKIP SCORE</th>
                <th>LATITUDE</th>
                <th>LONGITUDE</th>
                <th>DISTANCE THRESHOLD</th>
                <th></th>
                <th></th>
            </tr>
            <%
                for(final Question question : questions)
                {
            %>
            <tr>
                <td><a href="/admin/question?uuid=<%=question.getUUID()%>"><%= question.getUUID() %></a></td>
                <td><%= question.getSeqNumber() %></td>
                <td><%= question.getText() %></td>
                <td><%= question.getCorrectAnswer() %></td>
                <td><%= question.getCorrectScore() %></td>
                <td><%= question.getWrongScore() %></td>
                <td><%= question.getSkipScore() %></td>
                <td><%= question.getLatitude() %></td>
                <td><%= question.getLongitude() %></td>
                <td><%= question.getDistanceThreshold() %></td>
                <td>
                    <form action="/admin/question">
                        <div><input type="submit" value="Edit" /></div>
                        <input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID %>" value="<%= question.getUUID() %>"/>
                    </form>
                </td>
                <td>
                    <form action="/admin/delete-entity">
                        <div><input type="submit" value="Delete" /></div>
                        <input type="hidden" name="<%= QuestionFactory.PROPERTY_UUID %>" value="<%= question.getUUID() %>"/>
                        <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/category?uuid=" + category.getUUID(), "UTF-8") %>"/>
                    </form>
                </td>
            </tr>
            <%
                }
            %>
        </table>

        <hr/>

        <form action="/admin/edit-question" method="post" onsubmit="editQuestionButton.disabled = true; return true;">
            <table>
                <tr>
                    <th>SEQ NUMBER</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_SEQ_NUMBER%>" /></td>
                </tr>
                <tr>
                    <th>TEXT</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_TEXT %>" /></td>
                </tr>
                <tr>
                    <th>CORRECT ANSWER</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_CORRECT_ANSWER %>" /></td>
                </tr>
                <tr>
                    <th>CORRECT SCORE</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_CORRECT_SCORE %>" /></td>
                </tr>
                <tr>
                    <th>WRONG SCORE</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_WRONG_SCORE %>" /></td>
                </tr>
                <tr>
                    <th>SKIP SCORE</th>
                    <td><input type="number" name="<%= QuestionFactory.PROPERTY_SKIP_SCORE %>" /></td>
                </tr>
                <tr>
                    <th>LATITUDE</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_LATITUDE %>" /></td>
                </tr>
                <tr>
                    <th>LONGITUDE</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_LONGITUDE %>" /></td>
                </tr>
                <tr>
                    <th>DISTANCE THRESHOLD</th>
                    <td><input type="text" name="<%= QuestionFactory.PROPERTY_DISTANCE_THRESHOLD%>" /></td>
                </tr>
            </table>
            <div><input type="submit" value="Add question" name="editQuestionButton"/></div>
            <input type="hidden" name="<%= QuestionFactory.PROPERTY_CATEGORY_UUID %>" value="<%= category.getUUID() %>" />
        </form>

        <%
    }
%>
    </body>

</html>