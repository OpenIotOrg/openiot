<%--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>
<%@ include file="include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>OpenIoT Authentication and Authorization Demo</title>
<%@ include file="bootstrap_header.jsp"%>
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
			! 
			<shiro:user>
				(
				<a href="<c:url value="/logout"/>">Log out</a>
				)
			</shiro:user>
			<!--
			<shiro:guest>
				<a href="<c:url value="/login.jsp"/>">Log in</a>
			</shiro:guest>
			-->
		</p>

		<p>Welcome to the OpenIoT Authentication and Authorization Demo.</p>

		<shiro:user>
			<ul class="inline">
				<li>Visit your <a href="<c:url value="/account"/>">account page</a></li>
				<li>Visit your <a href="<c:url value="/perm"/>">permissions page</a></li>
			</ul>
		</shiro:user>
		<shiro:guest>
			<p>
				If you want to access the user-only <a
					href="<c:url value="/account"/>">account page</a>, you will need to
				log-in first.
			</p>
			<p>
				If you want to access the user-only <a
					href="<c:url value="/perm"/>">permissions page</a>, you will need to
				log-in first.
			</p>
		</shiro:guest>

		<h2>Roles</h2>

		<p>Here are the roles you have and don't
			have. Log out and log back in under different user accounts to see
			different roles.</p>

		<h3>Roles you have</h3>

		<p style="color: #006B00">
			<shiro:hasRole name="admin">admin<br />
			</shiro:hasRole>
			<shiro:hasRole name="president">president<br />
			</shiro:hasRole>
			<shiro:hasRole name="darklord">darklord<br />
			</shiro:hasRole>
			<shiro:hasRole name="goodguy">goodguy<br />
			</shiro:hasRole>
			<shiro:hasRole name="schwartz">schwartz<br />
			</shiro:hasRole>
		</p>

		<h3>Roles you DON'T have</h3>

		<p style="color:#8F0047">
			<shiro:lacksRole name="admin">admin<br />
			</shiro:lacksRole>
			<shiro:lacksRole name="president">president<br />
			</shiro:lacksRole>
			<shiro:lacksRole name="darklord">darklord<br />
			</shiro:lacksRole>
			<shiro:lacksRole name="goodguy">goodguy<br />
			</shiro:lacksRole>
			<shiro:lacksRole name="schwartz">schwartz<br />
			</shiro:lacksRole>
		</p>
	</div>

	<%@ include file="bootstrap_footer.jsp"%>
</body>
</html>
