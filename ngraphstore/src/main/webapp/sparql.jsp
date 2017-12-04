<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
	app.controller("TableController", function($scope, $http) {
		$scope.data = [];
		$scope.time = 0.0;
		$scope.sparqlForm = {
			query : 'SELECT * WHERE {?s ?p ?o}'
		}
		$scope.sparqlSubmit = function() {
			console.log($scope.sparqlForm.query);
			var startDate = new Date();
			var startTime = startDate.getTime();
			$http(
					{
						method : 'GET',
						url : 'http://localhost:9098/ngraphstore/sparql?query='
								+ $scope.sparqlForm.query
					}).then(function successCallback(response) {
				var endDate = new Date();
				var endTime = endDate.getTime();
				$scope.data = response.data;
				$scope.time = (endTime - startTime) / 1000.0;
			}, function errorCallback(response) {
				console.log(response.statusText);
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
					<li><a href="/ngraphstore/index.jsp"><i class="fa fa-home"></i>
							<span>Home</span></a></li>
					<li><a class="active" href="/ngraphstore/sparql.jsp"><i
							class="fa fa-search"></i> <span>Query</span></a></li>
				</ul>
			</div>
			<div class="divider"></div>

		</div>
		<div id="content" class="content" ng-app="table" ng-controller="TableController">

			<form id="myform" ng-submit="sparqlSubmit()">
				<div class=".col-md-12">
					<textarea class="itxt" rows="15" ng-model="sparqlForm.query"></textarea>
				</div>
				<div class=".col-md-12">
					<input type="submit" class="btn" value="Submit">
				</div>
			</form>
			<div class="divider"></div>
			<div class=".col-md-12 itxt time">Query took {{time}} seconds</div>
			<div class="divider"></div>
			<div style="overflow-x: auto;">

				<table class="itxt fancytable">
					<tr>

						<th ng-repeat="var in data.head.vars">{{ var }}</th>

					</tr>

					<tr ng-repeat="binding in data.results.bindings">

						<td ng-repeat="var in data.head.vars">{{ binding[var].value
							}}</td>
					</tr>

				</table>
			</div>
			<div class="divider"></div>
		</div>
		<div id="footer" class="footer">
			<a href="https://github.com/TortugaAttack/N-graphStore">GitHub</a>
		</div>
	</div>
</body>
</html>