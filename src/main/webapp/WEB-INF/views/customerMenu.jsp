<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Menu</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/main.css" />"/>
</head>
<body>
<div id="menu_body">
    <h1>Hi! This is all the menu we suggest you. ${customername}</h1>
    <h2>Age: ${customerAge}</h2>
    <h2>Age Category: ${ageCategory}</h2>

    <h2>
        <a href="/">Home</a>
    </h2>

    <h3>
        <a href="/customers/${customername}/questions">Next Question</a>
    </h3>

    <c:forEach var="personalisedMenu" items="${personalisedMenus}">
        <div id="menu_food" class="shadow">
            <div id="dish_pic">
                <c:choose>
                    <c:when test="${personalisedMenu.name == 'tuna salad'}">
                        <img src="<c:url value="/resources/images/tuna_salad.png" />"/>
                    </c:when>
                    <c:when test="${personalisedMenu.name == 'pasta salad'}">
                        <img src="<c:url value="/resources/images/pasta_salad.png"/>"/>
                    </c:when>
                    <c:when test="${personalisedMenu.name == 'nut salad'}">
                        <img src="<c:url value="/resources/images/nut_salad.png"/>"/>
                    </c:when>
                    <c:when test="${personalisedMenu.name == 'grilled chicken potsu'}">
                        <img src="<c:url value="/resources/images/grilled_chicken_potsu.png"/>"/>
                    </c:when>
                    <c:when test="${personalisedMenu.name == 'fried rice'}">
                        <img src="<c:url value="/resources/images/fried_rice.png"/>"/>
                    </c:when>
                    <c:when test="${personalisedMenu.name == 'sandwiches'}">
                        <img src="<c:url value="/resources/images/sandwiches.png"/>"/>
                    </c:when>
                </c:choose>
            </div>
            <div id="dish_name">${personalisedMenu.name}</div>
            <div id="">Regular Price:&#163;
            <c:choose>
                <c:when test="${ageCategory == 'Child'}">${personalisedMenu.child}</c:when>
                <c:when test="${ageCategory == 'Adult'}">${personalisedMenu.regular}</c:when>
                <c:when test="${ageCategory == 'Pensioner'}">${personalisedMenu.pensioner}</c:when>
                <c:otherwise>${personalisedMenu.regular}</c:otherwise>
            </c:choose>

            </div>
        </div>
    </c:forEach>
</div>
</body>
</html>