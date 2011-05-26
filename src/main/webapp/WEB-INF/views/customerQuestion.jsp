<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Menu</title>
</head>
<body>
<h1>Hi! This is all the menu we suggest you.</h1>
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
    <c:forEach var="nextQuestion" items="${nextQuestions}">
        <li>
            <a href="/customers/${customername}/questions/${nextQuestion.id}">
                ${nextQuestion.name}
            </a>
        </li>
    </c:forEach>
</ol>
</body>
</html>