<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>


<html>
<head>
<style>
body {
	font-family: calibri;
	font-size: 10pt;
}

table {
	border: solid black 2px;
	border-spacing: 1px; border-collapse : separate;
	margin: 0%;
	border-collapse: separate;
}

tr {
	background-color: #C4F5EA;
}

td {
	font-family: arial;
	font-size: 8pt; background-color : #C4F5EA;
	padding: 0px;
	border-bottom: solid black 1px;
	background-color: #C4F5EA;
}

.button {
	display: inline-block;
	min-width: 50px;
	width: 180px;
	padding: 5px 10px;
	height: 30px;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Coalesce Data Service - Version 1</title>
</head>
<body>
	<script type="text/javascript">
		{
			// 1. wait for the page to be fully loaded.
			window.onload = function() {
				drawPageHeader();
				//alert(document.URL);
			}

			function drawPageHeader() {
				var canvas = document.getElementById("svgcanvas");
				var context = canvas.getContext("2d");
				context.shadowOffsetX = 10;
				context.shadowOffsetY = 10;
				context.shadowBlur = 4;
				context.shadowColor = "#666666"; //or use rgb(red, green, blue)

				//context.fillStyle = "#000000";
				//context.fillRect(10,10, 50, 50);

				context.fillStyle = "#000066";
				context.font = "30px Arial";
				context.fillText("IREMS Coalesce Data Service", 10, 40);
			}
		}
		function showWSDL() {
			location.href = document.URL + 'CoalesceDataService?wsdl';
		}
		function changeServiceStatus() {
			if (document.getElementById('wsEnableService').checked) {
				alert("Service Enabled")
			} else if (document.getElementById('wsDisableService').checked) {
				alert("Service Disabled")
			}
		}
		function setUIStatus(serviceState){
			if(serviceState)
				alert("Service is Up")
				else
					alert("Service is Down")
		}
	</script>
	<canvas id="svgcanvas" width="500" height="70"
		style="border: 1px solid #cccccc;">
    HTML5/SVG Canvas not supported..Too Bad For You!!!
</canvas>

	<!-- 	<br /> -->
	<%-- 	<br /> Data from Controller (Expression Language): ${name} --%>
	<%-- 	<br /> Data from Controller (Expression Language): ${name1} --%>
	<!-- 	<br /> Now using JSTL: -->
	<%-- 	<c:out value="${name}"></c:out> --%>
	<!-- 	<br /> -->
	<!-- 	<br /> -->
	<!-- 	<h2>DB Test Connection Results</h2> -->
	<br />
	<br />
	<br />
<sql:query var="rs" dataSource="jdbc/irems">
select count(*) from coalesceentity;
</sql:query>

	<c:choose>
		<c:when test="${rs.rowCount <= 0}">
			<b>IREMS Coalesce Database is not available.</b>
		</c:when>
		<c:otherwise>
			<b>IREMS Coalesce Database is available.</b>
			<br />
			<b>IREMS Coalesce Service Status is ${ServiceStatus}</b>
		</c:otherwise>
	</c:choose>
	
	<br />
	<br />jdbc/devsys
	<table style="border-collapse: collapse;0px; padding: 0px">
		<tr>
			<td>View Service WSDL URL</td>
			<td><button class="button" type="submit" onclick="showWSDL()">Show
					WSDL</button></td>
		</tr>
		<tr>
			<td><input type="radio" name="wsEnable" id="wsEnableService"
				value="enabled">Enable<input type="radio" name="wsEnable"
				id="wsDisableService" value="disabled">Disable</td>
			<td>
				<button class="button" type="submit" onclick="changeServiceStatus()">Set
					Service Status</button>
			</td>
		</tr>
	</table>
</body>
</html>