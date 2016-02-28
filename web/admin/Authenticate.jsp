<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="uk.ac.uclan.thc.data.UserEntity" %>
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
  Date: 8/17/12
  Time: 10:41 AM
--%>
    <p> <h1> UCLan Treasure Hunt Challenge </h1> </p>
<%
    final UserService userService = UserServiceFactory.getUserService();
    final User user = userService.getCurrentUser();
    final String userEmail = user == null ? "Unknown" : user.getEmail();
    UserEntity userEntity = null;
    if (user == null)
    {
%>
    <p>You need to <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> to use this service.</p>
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
    <p><img src="../favicon.png" alt="UCLan"/> Logged in as: <%= user.getNickname() %> <b> <%= userEntity.isAdmin() ? "(admin)" : "" %> </b> [<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>]</p>
<%
    }
%>