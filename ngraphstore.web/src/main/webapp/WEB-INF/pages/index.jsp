<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>N-graphStore - Home</title>
<link rel="stylesheet"
	href="/ngraphstore/webResources/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css"
	href="/ngraphstore/webResources/css/simple.css">
</head>
<body>
	<div id="wrapper">
		<div id="header" class="abs">
			<div class="nav">
				<ul class="nav">
					<li><a href="https://github.com/TortugaAttack/N-graphStore">
							<span class="navlogo">N-graphStore</span>
					</a></li>
					<li><a class="active" href="/ngraphstore/index"><i
							class="fa fa-home"></i> <span>Home</span></a></li>
					<li><a href="/ngraphstore/sparql-view"><i
							class="fa fa-search"></i> <span>Query</span></a></li>
					<li><a href="/ngraphstore/auth/update"><i
							class="fa fa-pencil"></i> <span>Update</span></a></li>
					<li><a href="/ngraphstore/auth/upload"><i
							class="fa fa-upload"></i> <span>Upload</span></a></li>
					<li><c:if test="${!authenticated}">
							<a href="/ngraphstore/login"><i class="fa fa-sign-in"></i><span>Login</span></a>
						</c:if> <c:if test="${authMethod != 'none'}">
							<c:if test="${authenticated}">
								<c:url value="/logout" var="logoutUrl" />
								<form action="${logoutUrl}" method="post">
									<c:if test="${isAdmin}">
										<a href="/ngraphstore/auth/admin"><i
											class="fa fa-address-book"></i> <span>Admin</span></a>
									</c:if>
									<a href="/ngraphstore/auth/settings"><i class="fa fa-gear"></i>
										<span>Settings</span></a> <a href="javascript:;"
										onclick="parentNode.submit();"><i class="fa fa-sign-out"></i><span>Logout</span></a>
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}" />
								</form>
							</c:if>
						</c:if></li>
				</ul>
			</div>
		</div>

		<div id="content" class="content">
			<img class="logo" src="/ngraphstore/webResources/images/logo.png" />
			<div class="divider"></div>
		</div>
		<div id="footer" class="footer">
			<div class="copyright">Copyright (c) Public Domain - 2017</div>
			<a href="https://github.com/TortugaAttack/N-graphStore"><img
				width="60px" src="/ngraphstore/webResources/images/GitHub_Logo.png"></a>
		</div>
	</div>
</body>
</html>