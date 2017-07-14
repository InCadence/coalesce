<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
<title>Template editor</title>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" type="text/css" media="all"
	href="<c:url value="/css/folding.css" />" />

<link rel="stylesheet" type="text/css" media="all"
	href="<c:url value="/js/jstree/themes/default/style.css" />" />
	
	<link rel="stylesheet" type="text/css" media="all"
	href="<c:url value="/js/bootstrap/css/bootstrap.css" />" />

<script src="<c:url value="/js/jquery/jquery-3.1.1.js" />"></script>
<script src="<c:url value="/js/jstree/jstree.js" />"></script>
<script src="<c:url value="/js/bootstrap/js/bootstrap.js" />"></script>
<script src="<c:url value="/js/bootbox/bootbox.js" />"></script>

<!-- Sets the basepath for the library if not in same directory -->
<script type="text/javascript">
	mxBasePath = "<c:url value="/" />";
</script>

<script type="text/javascript" src="<c:url value="/js/mxClient.js" />"></script>

<!-- Loads and initializes the library -->
<script type="text/javascript" src="<c:url value="/resources/angularjs/1.6.1/angular.js" />"></script>

<spring:url value="/images/grid.gif" var="grid" />

<script src="<c:url value="/js/scripts/enums/CoalesceFieldType.js" />"></script>
<script src="<c:url value="/js/scripts/enums/CoalesceObjectType.js" />"></script>
<script src="<c:url value="/js/scripts/model/CoalesceEntityTemplate.js" />"></script>
<script src="<c:url value="/js/scripts/model/CoalesceField.js" />"></script>
<script src="<c:url value="/js/scripts/model/CoalesceRecordSet.js" />"></script>
<script src="<c:url value="/js/scripts/model/CoalesceSection.js" />"></script>
<script src="<c:url value="/js/scripts/model/CoalesceCell.js" />"></script>
<script src="<c:url value="/js/scripts/model/CoalesceEditCell.js" />"></script>
<script src="<c:url value="/js/scripts/dragdropcontroller.js" />"></script>

</head>

<!-- Page passes the container for the graph to the program -->
<body ng-app="myApp">

<div id="graphbox" ng-controller="draganddrop" style="overflow:hidden;width:100%;height:100%;background:url('${grid}');cursor:default;"></div>

    <!-- Select Template Form -->
    <div id="dialog-template-form" title="Select Template">
        <form>
            <fieldset>
                <select id="templates" class="form-control"></select>
                <!-- Allow form submission with keyboard without duplicating the dialog button -->
                <input type="submit" tabindex="-1" style="position: absolute; top: -1000px">
            </fieldset>
        </form>
    </div>

</body>
</html>