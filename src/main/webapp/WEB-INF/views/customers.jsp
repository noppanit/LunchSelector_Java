<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Menu</title>
</head>
<body>
<h1>Hi! This is all the menu.</h1>
<h2>
    <a href="/">
        Home
    </a>
</h2>
<ol>
    <c:forEach var="customer" items="${customers}">
        <li>
            <a href="customers/menu/${customer.name}">${customer.name}</a>
        </li>
    </c:forEach>
</ol>
</body>
</html>