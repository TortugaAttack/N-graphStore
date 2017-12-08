<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>N-graphStore - Admin</title>
<link rel="stylesheet"
	href="/ngraphstore/webResources/font-awesome/css/font-awesome.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
<script src="https://code.jquery.com/jquery-1.11.3.js"></script>
<link rel="stylesheet" type="text/css"
	href="/ngraphstore/webResources/css/simple.css">
<script type="text/javascript">
	var app = angular.module('create', []);
	app
			.controller(
					"CreateController",
					function($scope, $http) {
						$scope.errorCreate = false;
						$scope.errorUpdate = false;
						$scope.csrf = "${_csrf.token}";
						$scope.errormsg = '';
						$scope.infoCreate = false;
						$scope.infoUpdate = false;
						$scope.infomsg = 'Update was successfull';
						$scope.names = ${names};
						
						$scope.updateForm = {
							username : '',
							isAdmin : false
						};
						$scope.createForm = {
							username : '',
							password : '',
							cpassword : '',
							isAdmin : false
						};
						$scope.errorClear = function() {
							$scope.errorUpdate = false;
							$scope.errorCreate =false;
							$scope.errormsg = '';
						};
						$scope.infoClear = function() {
							$scope.infoUpdate = false;
							$scope.infoCreate = false;
						};
						$scope.create = function() {
							$scope.error = false;
							if ($scope.createForm.password != $scope.createForm.cpassword) {
								$scope.errorCreate = true;
								$scope.errormsg = "Passwords does not match";
								return;
							}

							$http(
									{
										method : 'POST',
										url : 'http://localhost:9098/ngraphstore/auth/admin/api/createuser',
										data : $
												.param({
													username : $scope.createForm.username,
													password : $scope.createForm.password,
													isAdmin : $scope.createForm.isAdmin,
													_csrf : $scope.csrf
												}),
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
										}
									}).then(
									function successCallback(response) {

										$scope.data = response.data;

										$scope.infoCreate = true;
									},
									function errorCallback(response) {

										$scope.errorCreate = true;
										$scope.errormsg = response.status
												+ ": " + response.statusText;
									});
						};

						$scope.remove = function() {
							$scope.errorUpdate = false;
							$http(
									{
										method : 'POST',
										url : 'http://localhost:9098/ngraphstore/auth/admin/api/deleteuser',
										data : $
												.param({
													username : $scope.updateForm.username,
													_csrf : $scope.csrf
												}),
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
										}
									}).then(
									function successCallback(response) {

										$scope.data = response.data;

										$scope.infoUpdate = true;
									},
									function errorCallback(response) {
										$scope.errorUpdate = true;
										$scope.errormsg = response.status
												+ ": " + response.statusText;
									});
						};
						$scope.update = function() {
							$scope.errorUpdate = false;
							$http(
									{
										method : 'POST',
										url : 'http://localhost:9098/ngraphstore/auth/admin/api/setuserrole',
										data : $
												.param({
													username : $scope.updateForm.username,
													isAdmin : true,
													_csrf : $scope.csrf
												}),
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
										}
									}).then(
									function successCallback(response) {
										$scope.data = response.data;
										$scope.infoUpdate = true;
									},
									function errorCallback(response) {
										$scope.errorUpdate=true;
										$scope.errormsg = response.status
												+ ": " + response.statusText;
									});
						};
					});
</script>
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
					<li><c:if test="${!authenticated}">
							<a href="/ngraphstore/login"><i class="fa fa-sign-in"></i><span>Login</span></a>
						</c:if> <c:if test="${authenticated}">
							<c:url value="/logout" var="logoutUrl" />
							<form action="${logoutUrl}" method="post">
								<c:if test="${isAdmin}">
									<a class="active" href="/ngraphstore/auth/admin"><i
										class="fa fa-address-book"></i> <span>Admin</span></a>
								</c:if>

								<a href="/ngraphstore/auth/settings"><i class="fa fa-gear"></i>
									<span>Settings</span></a> <a href="javascript:;"
									onclick="parentNode.submit();"><i class="fa fa-sign-out"></i><span>Logout</span></a>
								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
							</form>
						</c:if></li>
				</ul>
			</div>
		</div>

		<div id="content" class="content" ng-app="create"
			ng-controller="CreateController">

			<form id="create" ng-submit="create()">

				<div class="itxt error" ng-show="errorCreate">
					{{errormsg}}<a ng-href='#' ng-click='errorClear()'><i
						class="error-icon fa fa-window-close"></i></a>
				</div>
				<div class="itxt info" ng-show="infoCreate">
					{{infomsg}}<a ng-href='#' ng-click='infoClear()'><i
						class="error-icon fa fa-window-close"></i></a>
				</div>

				<div class=" itxt">
					<label>Create a new User:</label>
					<div class="itxt">
						<div>
							<label for="username">Username: </label>
						</div>
						<input class="fullinput" type="text" id="username"
							ng-model="createForm.username" required />
					</div>
					<div class="itxt">
						<div>
							<label for="password">Password: </label>
						</div>
						<input class="fullinput" type="password" id="password"
							ng-model="createForm.password" required />
					</div>
					<div class="itxt">
						<div>
							<label for="cpassword">Password: </label>
						</div>
						<input class="fullinput" type="password" id="cpassword"
							ng-model="createForm.cpassword" required />
					</div>
					<div class="itxt fullinput">

						<label for="isAdmin">Is Admin?: </label> <input class=""
							type="checkbox" id="isAdmin" ng-model="createForm.isAdmin" />
					</div>
					<input type="hidden" name="_csrf" value="${_csrf.token}" />
					<button type="submit" class="btn">Update</button>
				</div>

			</form>

			<div class="divider"></div>

			<form id="update" ng-submit="update()">

				<div class="itxt error" ng-show="errorUpdate">
					{{errormsg}}<a ng-href='#' ng-click='errorClear()'><i
						class="error-icon fa fa-window-close"></i></a>
				</div>
				<div class="itxt info" ng-show="infoUpdate">
					{{infomsg}}<a ng-href='#' ng-click='infoClear()'><i
						class="error-icon fa fa-window-close"></i></a>
				</div>
				<div class=" itxt">
					<label>Update/Delete User:</label>
					<div class="itxt">
						<div>
							<label for="select">Select User: </label>
						</div>
						<select class="fancyselect"  ng-options="x for x in names"
							ng-model="updateForm.username">
						</select>
					</div>
					<div class="itxt">
						<button type="submit" class="btn doublebtn">Set as Admin</button>
						<button ng-click="remove()" class="btn doublebtn">Delete</button>
					</div>
					<input type="hidden" name="_csrf" value="${_csrf.token}" />

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