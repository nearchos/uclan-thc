<%@ page import="java.util.Date" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="uk.ac.uclan.thc.model.TimedQuestion" %>
<%@ page import="uk.ac.uclan.thc.data.TimedQuestionFactory" %>
<%@ page import="uk.ac.uclan.thc.model.Category" %>
<%@ page import="uk.ac.uclan.thc.data.CategoryFactory" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 26/10/2015
  Time: 07:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

  <head>
    <title>UCLan Treasure Hunt Challenge - Timed Question</title>
  </head>

  <body>

  <%
        final String key = request.getParameter("uuid");
        final TimedQuestion timedQuestion = key != null ? TimedQuestionFactory.getTimedQuestion(key) : null;
        final Category category = timedQuestion == null ? null : CategoryFactory.getCategory(timedQuestion.getCategoryUUID());

        if(timedQuestion == null || category == null)
        {
  %>

  <p><b>Unknown or invalid path/uuid</b></p>

  <%
        }
        else if(System.currentTimeMillis() < category.getValidFrom())
        {
  %>

  <p><b>The selected question is not active yet.</b></p>
  <p>It will become active at: <%= category.getValidFromAsString() %> [UTC (Coordinated Universal Time)]</p>

  <%
        }
        else if(System.currentTimeMillis() > category.getValidUntil())
        {
  %>

  <p><b>The selected question is not active anymore.</b></p>
  <p>It was active until: <%= category.getValidUntilAsString() %> [UTC (Coordinated Universal Time)]</p>

  <%
        }
        else // active
        {
          final String imageUrl = timedQuestion != null ? timedQuestion.getImageUrl() : null;
  %>

  <p><b><%= timedQuestion.getTitle() %></b></p>
  <p><%= timedQuestion.getBody() %></p>

  <%
          if(imageUrl != null && imageUrl.length() > 0)
          {
  %>
  <img src="<%= imageUrl %>"/>
  <%
          }
        }
  %>

  </body>
</html>
