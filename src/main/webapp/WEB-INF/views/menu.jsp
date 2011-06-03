<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Menu</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/main.css" />"/>
</head>
<body>


<div id="menu_body">
    <h1>Hi! This is all the menu.</h1>

    <h2>
        <a href="/">Home</a>
    </h2>
    <c:forEach var="dish" items="${dishes}">
        <div id="menu_food" class="shadow">
            <div id="dish_pic">
                <c:choose>
                    <c:when test="${dish.name == 'tuna salad'}">
                        <img src="<c:url value="/resources/images/tuna_salad.png" />"/>
                    </c:when>
                    <c:when test="${dish.name == 'pasta salad'}">
                        <img src="<c:url value="/resources/images/pasta_salad.png"/>"/>
                    </c:when>
                    <c:when test="${dish.name == 'nut salad'}">
                        <img src="<c:url value="/resources/images/nut_salad.png"/>"/>
                    </c:when>
                    <c:when test="${dish.name == 'grilled chicken potsu'}">
                        <img src="<c:url value="/resources/images/grilled_chicken_potsu.png"/>"/>
                    </c:when>
                    <c:when test="${dish.name == 'fried rice'}">
                        <img src="<c:url value="/resources/images/fried_rice.png"/>"/>
                    </c:when>
                    <c:when test="${dish.name == 'sandwiches'}">
                        <img src="<c:url value="/resources/images/sandwiches.png"/>"/>
                    </c:when>
                </c:choose>
            </div>
            <div id="dish_name">${dish.name}</div>
            <div id="">Regular Price:&#163;${dish.regular}</div>
        </div>
    </c:forEach>
</div>
</body>
</html>