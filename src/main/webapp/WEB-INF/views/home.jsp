<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Home</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/main.css" />"/>
</head>
<body>
<h1>Hunger Strikes!</h1>

<h2>
    <a href="menu">
        List of menu
    </a>
</h2>

<h2>
    <a href="customers">
        List of customer
    </a>
</h2>
<h2>
    <a href="customers/add">
        Start a process
    </a>
</h2>
<h2>
    <a href="rules">
        Set Rule
    </a>
</h2>
</body>
</html>