<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Menu</title>
</head>
<body>
<h1>Hi! This is all the menu.</h1>
<ol>
    <c:forEach var="dish" items="${dishes}">
        <li>
            ${dish.name}
        </li>
    </c:forEach>
</ol>
</body>
</html>