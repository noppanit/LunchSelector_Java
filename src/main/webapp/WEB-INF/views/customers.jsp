<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Menu</title>
</head>
<body>
<h1>Hi! This is all the menu.</h1>
<ol>
    <c:forEach var="customer" items="${customers}">
        <li>
            ${customer.name}
        </li>
    </c:forEach>
</ol>
</body>
</html>