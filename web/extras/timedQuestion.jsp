<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="uk.ac.uclan.thc.data.UserEntity" %>
<%@ page import="uk.ac.uclan.thc.data.TimedQuestionFactory" %>
<%@ page import="uk.ac.uclan.thc.model.Category" %>
<%@ page import="java.util.Date" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 26/10/2015
  Time: 07:46
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

  <head>
    <title>UCLan Treasure Hunt Challenge - Timed Question</title>
  </head>

  <body>

  <%
    final UserService userService = UserServiceFactory.getUserService();
    final User user = userService.getCurrentUser();
    final String userEmail = user == null ? "Unknown" : user.getEmail();
    UserEntity userEntity = null;
    if (user == null)
    {
  %>
  <p>You can <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> to administrate this service.</p>
  <%
    }
    else
    {
      userEntity = UserEntity.getUserEntity(user.getEmail());
      if(userEntity == null)
      {
        userEntity = UserEntity.setUserEntity(user.getEmail(), user.getNickname(), false, false);
      }
  %>
  <span><img src="../favicon.png" alt="UCLan"/> Logged in as: <%= user.getNickname() %> <b> <%= userEntity.isAdmin() ? "(admin)" : "" %> </b> [<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>]</span>
  <%
      if(userEntity.isAdmin())
      {
          final long now = System.currentTimeMillis();
  %>
  <form action="/extras/submit-question" method="post" onsubmit="submitButton.disabled = true; return true;">
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
              <td><input type="datetime-local" name="<%= TimedQuestionFactory.PROPERTY_CATEGORY_UUID%>" value="<%= Category.SIMPLE_DATE_FORMAT.format(new Date(now)) %>"/></td>
          </tr>
          <tr>
              <td>BODY</td>
              <td><input type="datetime-local" name="<%= TimedQuestionFactory.PROPERTY_BODY%>" required/></td>
          </tr>
          <tr>
              <td>IMAGE_URL</td>
              <td><input type="text" name="<%= TimedQuestionFactory.PROPERTY_IMAGE_URL%>"/></td>
          </tr>
      </table>
      <div><input type="submit" name="submitButton" value="Add question" /></div>
  </form>
  <%
      }
    }
  %>

  </body>
</html>
