<%@ page import="java.util.Vector" %>
<%@ page import="uk.ac.uclan.thc.admin.DeleteEntity" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="uk.ac.uclan.thc.model.Parameter" %>
<%@ page import="uk.ac.uclan.thc.data.ParameterFactory" %>
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
  Date: 22/02/18
  Time: 9:30
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

    <head>
        <title>UCLan Treasure Hunt Challenge - Parameters</title>
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
        final Vector<Parameter> parameters = ParameterFactory.getAllParameters();
%>
        <p><a href="/admin/categories">Back to all categories</a></p>

        <p>At minimum you need to set the following parameters:
            <ul>
                <li>ABLY_PRIVATE_KEY</li>
                <li>ABLY_PUBLIC_KEY</li>
                <li>GOOGLE_MAPS_KEY</li>
            </ul>
        </p>
        <br/>
        <table>
            <tr><td><b>Key</b></td><td><b>Value</b></td></tr>
<%
        for(final Parameter parameter : parameters) {
%>
            <tr><td><b><%=parameter.getKey()%></b></td><td><b><%=parameter.getValue()%></b></td></tr>
<%
        }
%>
        </table>

        <hr/>

        <hr/>
        <h2>Parameters</h2>
        <table border="1">
            <tr>
                <th>UUID</th>
                <th>KEY</th>
                <th>VALUE</th>
                <th></th>
            </tr>
            <%
                for(final Parameter parameter : parameters) {
            %>
            <tr>
                <td><%= parameter.getUuid() %></td>
                <td><%= parameter.getKey() %></td>
                <td><%= parameter.getValue() %></td>
                <td>
                    <form action="/admin/delete-entity">
                        <div><input type="submit" value="Delete" /></div>
                        <input type="hidden" name="<%= ParameterFactory.PROPERTY_UUID %>" value="<%= parameter.getUuid() %>"/>
                        <input type="hidden" name="<%= DeleteEntity.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/parameters", "UTF-8") %>"/>
                    </form>
                </td>
            </tr>
            <%
                }
            %>
        </table>

        <hr/>

        <form action="/admin/add-parameter" method="post" onsubmit="addParameterButton.disabled = true; return true;">
            <table>
                <tr>
                    <th>KEY</th>
                    <td><input type="text" name="<%= ParameterFactory.PROPERTY_KEY %>" /></td>
                </tr>
                <tr>
                    <th>VALUE</th>
                    <td><input type="text" name="<%= ParameterFactory.PROPERTY_VALUE %>" /></td>
                </tr>
            </table>
            <div><input type="submit" value="Add parameter" name="addParameterButton"/></div>
        </form>

        <%
    }
%>
    </body>

</html>