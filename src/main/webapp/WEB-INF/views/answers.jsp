<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Menu</title>
</head>
<body>
<h1>Hi! Please answer this question</h1>
<h2>
    <a href="/">
        Home
    </a>
</h2>
<h3>
    <a href="/customers/menu/${customername}">
        Personalised Menu
    </a>
</h3>
<ol>
    <c:forEach var="answer" items="${answers}">
        <li>
            <a href="">
                ${answer.name}
            <a/>
        </li>
    </c:forEach>
</ol>
</body>
</html>