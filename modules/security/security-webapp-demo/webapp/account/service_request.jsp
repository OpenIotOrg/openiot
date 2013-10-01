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
		<!--<p>
		<strong>The service request URL:</strong> <span class="label label-important">https://localhost:7443/servicerequest?access_token=<c:out value="${requestScope.access_token}"/>&client_id=<c:out value="${requestScope.client_id}"/></span>
		</p> -->
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

		<% if(request.getAttribute("hasPermission").equals(true))  
		 	out.println("<h2 style=\"color: #006B00\">You have the permission \"stream:query:s1\" for service [" + request.getAttribute("client_id") + "]!</h2>");
		 else
			out.println("<h2 style=\"color:#8F0047\">You DON'T have the permission \"stream:query:s1\" for service [" + request.getAttribute("client_id") + "]!</h2>");
		%>

		
	</div>

	<%@ include file="../bootstrap_footer.jsp"%>
</body>
</html>
