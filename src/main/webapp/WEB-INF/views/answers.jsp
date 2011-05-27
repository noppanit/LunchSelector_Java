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

<h3>
    ${questionText}
</h3>

<form name="customerForm" id="customerForm" action="/customers/answer" method="POST">
    <input type="hidden" name="customername" id="customername" value="${customername}"/>
    <input type="hidden" name="questionId" id="questionId" value="${questionId}"/>
    <ol>
        <c:choose>
            <c:when test="${questionType == 'single'}">
                <c:forEach var="answer" items="${answers}">
                    <li>
                        <input type="radio" value="${answer.id}" name="answerId" id="answerId">${answer.name}</input>
                    </li>
                </c:forEach>
            </c:when>
            <c:when test="${questionType == 'multiple'}">
                <c:forEach var="answer" items="${answers}">
                    <li>
                        <input type="checkbox" value="${answer.id}" name="answerId" id="answerId">${answer.name}</input>
                    </li>
                </c:forEach>
            </c:when>
        </c:choose>
    </ol>
    <input type="submit"/>
</form>
</body>
</html>