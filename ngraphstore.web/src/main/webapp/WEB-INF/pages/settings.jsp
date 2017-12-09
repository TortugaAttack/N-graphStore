<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>N-graphStore - Settings</title>
<link rel="stylesheet"
	href="/ngraphstore/webResources/font-awesome/css/font-awesome.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
<script src="https://code.jquery.com/jquery-1.11.3.js"></script>
<script type="text/javascript">
	var app = angular.module('passwordform', []);
	app
			.controller(
					"UserController",
					function($scope, $http) {
						$scope.error = false;
						$scope.csrf = "${_csrf.token}";
						$scope.errormsg = '';
						$scope.info = false;
						$scope.infomsg = 'Update was successfull';
						$scope.updateForm = {
							opassword : '',
							password : '',
							cpassword : ''
						}
						$scope.errorClear = function() {
							$scope.error = false;
							$scope.errormsg = '';
						};
						$scope.infoClear = function() {
							$scope.info = false;
						}
						$scope.update = function() {
							$scope.error = false;
							if ($scope.updateForm.password != $scope.updateForm.cpassword) {
								$scope.error = true;
								$scope.errormsg = "Passwords does not match";
								return;
							}
							$http(
									{
										method : 'POST',
										url : 'http://localhost:9098/ngraphstore/auth/admin/api/updatepwd',
										data : $
												.param({
													oldpassword : $scope.updateForm.opassword,
													password : $scope.updateForm.password,
													_csrf : $scope.csrf
												}),
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
										}
									})
									.then(
											function successCallback(response) {

												$scope.data = response.data;

												$scope.info = true;
											},
											function errorCallback(response) {

												$scope.error = true;
												$scope.errormsg = "Password could not be verified.";
											});
						};
					});
</script>
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
					<li><a href="/ngraphstore/index"><i class="fa fa-home"></i>
							<span>Home</span></a></li>
					<li><a href="/ngraphstore/sparql-view"><i
							class="fa fa-search"></i> <span>Query</span></a></li>
					<li><a href="/ngraphstore/auth/update"><i
							class="fa fa-pencil"></i> <span>Update</span></a></li>
					<li><a href="/ngraphstore/auth/upload"><i
							class="fa fa-upload"></i> <span>Upload</span></a></li>
					<li><a href="/ngraphstore/explore"><i class="fa fa-eye"></i>
							<span>Explore</span></a></li>
					<li><c:if test="${!authenticated}">
							<a href="/ngraphstore/login"><i class="fa fa-sign-in"></i><span>Login</span></a>
						</c:if> <c:if test="${authenticated}">
							<c:url value="/logout" var="logoutUrl" />
							<form action="${logoutUrl}" method="post">
								<c:if test="${isAdmin}">
									<a href="/ngraphstore/auth/admin"><i
										class="fa fa-address-book"></i> <span>Admin</span></a>
								</c:if>
								<a class="active" href="/ngraphstore/auth/settings"><i
									class="fa fa-gear"></i> <span>Settings</span></a> <a
									href="javascript:;" onclick="parentNode.submit();"><i
									class="fa fa-sign-out"></i><span>Logout</span></a> <input
									type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
							</form>
						</c:if></li>
				</ul>
			</div>
		</div>

		<div id="content" class="content" ng-app="passwordform"
			ng-controller="UserController">

			<form id="passwordform" ng-submit="update()">

				<div class="itxt error" ng-show="error">
					{{errormsg}}<a ng-href='#' ng-click='errorClear()'><i
						class="error-icon fa fa-window-close"></i></a>
				</div>
				<div class="itxt info" ng-show="info">
					{{infomsg}}<a ng-href='#' ng-click='infoClear()'><i
						class="error-icon fa fa-window-close"></i></a>
				</div>
				<div class=" itxt">
					<div class="itxt">
						<div>
							<label for="oldpassword">Old Password: </label>
						</div>
						<input class="fullinput" type="password" id="oldpassword"
							ng-model="updateForm.opassword" required />
					</div>
					<div class="itxt">
						<div>
							<label for="password">Password: </label>
						</div>
						<input class="fullinput" type="password" id="password"
							ng-model="updateForm.password" required />
					</div>
					<div class="itxt">
						<div>
							<label for="cpassword">Password: </label>
						</div>
						<input class="fullinput" type="password" id="cpassword"
							ng-model="updateForm.cpassword" required />
					</div>
					<input type="hidden" name="_csrf" value="${_csrf.token}" />
					<button type="submit" class="btn">Update</button>
				</div>

			</form>


		</div>
		<div id="footer" class="footer">
			<div class="copyright">Copyright (c) Public Domain - 2017</div>
			<a href="https://github.com/TortugaAttack/N-graphStore"><img
				width="60px" src="/ngraphstore/webResources/images/GitHub_Logo.png"></a>
		</div>
	</div>
</body>
</html>