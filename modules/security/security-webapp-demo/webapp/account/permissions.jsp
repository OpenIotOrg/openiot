<%@page import="java.util.Map"%>
<%@page import="org.openiot.security.client.AccessControlUtil"%>
<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>OpenIoT Authentication and Authorization Demo</title>
<%@ include file="../bootstrap_header.jsp"%>
</head>
<body>

	<div class="container">

		<h1>OpenIoT Authentication and Authorization Demo</h1>

		<p>
			Hi
			<shiro:guest>Guest</shiro:guest>
			<shiro:user>
				<shiro:principal />
			</shiro:user>
			! (
			<shiro:user>
				<a href="<c:url value="/logout"/>">Log out</a>
			</shiro:user>
			<shiro:guest>
				<a href="<c:url value="/login.jsp"/>">Log in</a>
			</shiro:guest>
			)
		</p>
		

		<p>Welcome to the OpenIoT Authentication and Authorization Demo.</p>
		<shiro:user>
		<div class="well well-large">
		<p>
		<strong>Your token is:</strong> <span class="label label-info"><c:out value="${requestScope.access_token}"/></span>
		</p>
		<p>
		<strong>The clientId is:</strong> <span class="label label-warning"><c:out value="${requestScope.client_id}"/></span>
		</p>
		<p>
		<strong>The service request URL:</strong> <span class="label label-important">https://localhost:7443/servicerequest?access_token=<c:out value="${requestScope.access_token}"/>&client_id=<c:out value="${requestScope.client_id}"/></span>
		</p>
		</div>
		</shiro:user>

		<shiro:user>
			<p>
				Go to <a href="<c:url value="/home.jsp"/>">home page</a>.
			</p>
		</shiro:user>
		<shiro:guest>
			<p>
				If you want to access the user-only <a
					href="<c:url value="/account"/>">account page</a>, you will need to
				log-in first.
			</p>
		</shiro:guest>

		<h2>Permissions</h2>

		<p>Here are the permissions you have and don't have. Log out and
			log back in under different user accounts to see different
			permissions.</p>

		<%-- 
		<jsp:scriptlet>String[] permissions = new String[] { "stream:view:s1", "stream:query:s1", "stream:view:s2", "stream:query:s2", "admin:create_user", "admin:delete_user", "admin:delete_stream:s1",
					"admin:delete_stream:s2,s3" };
			pageContext.setAttribute("permissions", permissions);</jsp:scriptlet>
			--%>
		<h3>Permissions you have</h3>

		<table class="table table-hover table-condensed table-bordered table-striped">
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
				</tr>
			</thead>
			<tbody style="color: #006B00">
				<%
					for (Map.Entry<String, String> perm : ((Map<String, String>) request.getAttribute("permissions")).entrySet())
						if (AccessControlUtil.getInstance().hasPermission(perm.getKey()))
							out.println("<tr><td>" + perm.getKey() + "</td><td>" + perm.getValue() + "</td></tr>");
				%>
			</tbody>
		</table>

		<h3>Permissions you DON'T have</h3>

		<table class="table table-hover table-condensed table-bordered table-striped">
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
				</tr>
			</thead>
			<tbody style="color:#8F0047">
				<%
					for (Map.Entry<String, String> perm : ((Map<String, String>) request.getAttribute("permissions")).entrySet())
						if (!AccessControlUtil.getInstance().hasPermission(perm.getKey()))
							out.println("<tr><td>" + perm.getKey() + "</td><td>" + perm.getValue() + "</td></tr>");
				%>
			</tbody>
		</table>

	</div>

	<%@ include file="../bootstrap_footer.jsp"%>
</body>
</html>
