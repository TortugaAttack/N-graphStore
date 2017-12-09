<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="/ngraphstore/webResources/font-awesome/css/font-awesome.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
<title>N-graphStore - SPARQL</title>
<script src="https://code.jquery.com/jquery-1.11.3.js"></script>
<script type="text/javascript">
	var app = angular.module('table', []);
	app
			.controller(
					"TableController",
					function($scope, $http) {
						$scope.data = [];
						$scope.old = [];
						$scope.time = 0.0;
						$scope.error = false;
						$scope.errormsg = '';
						$scope.info = false;
						$scope.infomsg = '';
						$scope.time = 0.0;
						$scope.csrf = "${_csrf.token}";
						$scope.updateJson = [];
						$scope.exploreForm = {
							uri : '<http://ngraphstore/res/test>'
						}
						$scope.errorClear = function() {
							$scope.error = false;
							$scope.errormsg = '';
						};
						$scope.infoClear = function() {
							$scope.info = false;
							$scope.infomsg = '';
						};
						$scope.explore = function() {
							$scope.error = false;
							$scope.info = false;
							var startDate = new Date();
							var startTime = startDate.getTime();
							$http(
									{
										method : 'GET',
										url : 'http://localhost:9098/ngraphstore/api/explore'
												+ "?uri="
												+ encodeURIComponent($scope.exploreForm.uri)
									})
									.then(
											function successCallback(response) {
												$scope.info = false;
												$scope.infomsg = "update was successfull";
												$scope.data = response.data;
												var endDate = new Date();
												var endTime = endDate.getTime();
												$scope.time = (endTime - startTime) / 1000.0;
												$scope.old = angular
														.copy(response.data);
											},
											function errorCallback(response) {
												$scope.error = true;
												$scope.errormsg = response.status
														+ ": "
														+ response.statusText;
											});
						};
						$scope.update = function() {
							$scope.error = false;
							$scope.info = false;
							$http(
									{
										method : 'POST',
										url : 'http://localhost:9098/ngraphstore/api/auth/exchange',
										data : $
												.param({
													old : $scope.old.toString(),
													newTriples : $scope.data
															.toString(),
													_csrf : $scope.csrf
												}),
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
										}
									}).then(
									function successCallback(response) {

										$scope.data = response.data;
										$scope.info = true;
									},
									function errorCallback(response) {

										$scope.error = true;
										$scope.errormsg = response.status
												+ ": " + response.statusText;
									});
						};
						$scope.update2 = function() {
							$scope.error = false;
							var startDate = new Date();
							var oldStr = JSON.stringify($scope.old);
							var dataTriples = JSON.stringify($scope.data);
							var startTime = startDate.getTime();
							$http(
									{
										method : 'POST',
										url : 'http://localhost:9098/ngraphstore/api/auth/exchange',
										data : $.param({
											old : oldStr,
											newTriples : dataTriples,
											_csrf : $scope.csrf
										}),
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
										}
									})
									.then(
											function successCallback(response) {
												var endDate = new Date();
												var endTime = endDate.getTime();
												$scope.data = response.data;
												$scope.time = (endTime - startTime) / 1000.0;
												$scope.info = true;
											},
											function errorCallback(response) {
												console.log(response.data);
												$scope.error = true;
												$scope.errormsg = response.status
														+ ": "
														+ response.statusText;
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
					<!-- 				<li><img class="navlogo" -->
					<!-- 					src="/ngraphstore/webResources/images/logo.png" /></li> -->
					<li><a href="https://github.com/TortugaAttack/N-graphStore">
							<span class="navlogo">N-graphStore</span>
					</a></li>
					<li><a href="/ngraphstore/index"><i class="fa fa-home"></i>
							<span>Home</span></a></li>
					<li><a href="/ngraphstore/sparql-view"><i
							class="fa fa-search"></i> <span>Query</span></a></li>
					<li><a  href="/ngraphstore/auth/update"><i
							class="fa fa-pencil"></i> <span>Update</span></a></li>
					<li><a href="/ngraphstore/auth/upload"><i
							class="fa fa-upload"></i> <span>Upload</span></a></li>
					<li><a  class="active" href="/ngraphstore/explore"><i class="fa fa-eye"></i>
							<span>Explore</span></a></li>
					<li><c:if test="${authMethod != 'none'}">
							<c:if test="${!authenticated}">
								<a href="/ngraphstore/login"><i class="fa fa-sign-in"></i><span>Login</span></a>
							</c:if>
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
			<div class="divider"></div>

		</div>
		<div id="content" class="content" ng-app="table"
			ng-controller="TableController">
			<div class="itxt error" ng-show="error">
				{{errormsg}}<a ng-href='#' ng-click='errorClear()'><i
					class="error-icon fa fa-window-close"></i></a>
			</div>
			<div class="itxt info" ng-show="info">
				{{infomsg}}<a ng-href='#' ng-click='infoClear()'><i
					class="error-icon fa fa-window-close"></i></a>
			</div>
			<form id="myform">
				<div class=" itxt">
					<div class="itxt">
						<div>
							<input class="fullinput" ng-model="exploreForm.uri" type="text" />
						</div>
					</div>
					<button ng-click="explore()" class="btn">Explore</button>

				</div>
				<div class="divider" />
				<div class=".col-md-12">
					<!-- FOR each quad in explore request -->
					<div style="overflow-x: auto;">

						<table class="itxt fancytable">
							<tr>

								<th>subject</th>
								<th>predicate</th>
								<th>object</th>
								<th>graph</th>

							</tr>

							<tr ng-repeat="binding in data.graph">

								<td class="inputtd"><input data-ng-model="binding.subject"
									class="inputintd" type="text" value="{{binding.subject}}" /></td>
								<td class="inputtd"><input
									data-ng-model="binding.predicate" class="inputintd" type="text"
									value="{{binding.predicate}}" /></td>
								<td class="inputtd"><input data-ng-model="binding.object"
									class="inputintd" type="text" value="{{binding.object}}" /></td>
								<td class="inputtd"><input data-ng-model="binding.graph"
									class="inputintd" type="text" value="{{binding.graph}}" /></td>
							</tr>

						</table>
					</div>
				</div>

				<div class=".col-md-12">
					<c:if test="${authenticated}">
						<button ng-click="update2()" class="tablebtn btn">Submit</button>
					</c:if>
				</div>
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" />
			</form>
			<div class="divider"></div>
			<div class=".col-md-12 itxt time">Query took {{time}} seconds</div>
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