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
<%@page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>Login</title>
<%@ include file="bootstrap_header.jsp"%>
</head>
<body>

	<div class="container">

		<form class="form-signin" method="post" action="">
			<img alt="OpenIoT" src="OpenIoTLogo_0_transparent.png">

			<shiro:guest>
				<small>
					<p class="text-info">Here are a few sample accounts to play
						with.</p>


					<table class="table table-hover table-condensed table-bordered">
						<thead>
							<tr>
								<th>Username</th>
								<th>Password</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>admin</td>
								<td>secret</td>
							</tr>
							<tr>
								<td>presidentskroob</td>
								<td>12345</td>
							</tr>
							<tr>
								<td>darkhelmet</td>
								<td>darkhelmetpass</td>
							</tr>
							<tr>
								<td>lonestarr</td>
								<td>lonestarrpass</td>
							</tr>
						</tbody>
					</table>
				</small>
			</shiro:guest>
			<%
				if (request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME) != null) {
			%>
			<p class="text-error">Login failed. Please try again</p>
			<%
				}
			%>
			<h2 class="form-signin-heading">Please log in</h2>
			<input type="text" name="username" class="input-block-level"
				placeholder="Username"> <input type="password"
				name="password" class="input-block-level" placeholder="Password">
			<label class="checkbox"> <input type="checkbox"
				value="remember-me" name="rememberMe"> Remember me
			</label>
			<button class="btn btn-large btn-primary" type="submit">Log
				in</button>
		</form>

	</div>
	<!-- /container -->


	<%@ include file="bootstrap_footer.jsp"%>
</body>
</html>
