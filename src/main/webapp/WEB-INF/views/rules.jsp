<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Rules</title>
</head>
<body>
<h1>Hi! All.</h1>
<h2>
    <a href="/">
        Home
    </a>
</h2>
<ol>
    <c:forEach var="rule" items="${rules}">
        <li>
           ${rule.name}
        </li>
    </c:forEach>
</ol>
</body>
</html>