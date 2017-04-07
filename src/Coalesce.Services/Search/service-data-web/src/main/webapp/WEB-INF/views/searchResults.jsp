<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Expert Search Results</title>

<link rel="stylesheet" href="/search/resources/jquery-ui.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="/search/resources/style.css">
<link rel="stylesheet" href="/search/resources/ui.theme.css">

<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

</head>
<body>

    <nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <img class="navbar-icon" src="/search/resources/images/InCadence_Logo_Small_200_x_53.png" /> <a class="navbar-brand" href="#">Results</a>
        </div>
        <div class="collapse navbar-collapse" id="myNavbar">
            <ul class="nav navbar-nav navbar-right">
                <li><a id="template-selection" href="#">Edit</a></li>
                <li><a id="template-selection" href="#">Refresh</a></li>
            </ul>
        </div>
    </div>
    </nav>

    <div class="container-fluid text-center templatecontainer">
        <table>
            <thead>
                <tr>
                    <c:forEach items="${rows[0]}" var="column">
                        <td><c:out value="${column.key}" /></td>
                    </c:forEach>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${rows}" var="columns">
                    <tr>
                        <c:forEach items="${columns}" var="column">
                            <td><c:out value="${column.value}" /></td>
                        </c:forEach>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="container-fluid navbar-fixed-bottom">

        <!-- Footer -->
        <footer class="container-fluid bg-4 text-center">
        <p>
            Powered By <a href="#">Coalesce</a>
        </p>
        </footer>
    </div>

</body>
</html>