<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/users.css" type="text/css" />
</content>

<c:choose>
	<c:when test="${empty my_user_list}">
		No user
	</c:when>
	<c:otherwise>
		<div class="users">
			<div class="huser">
			    <div class="hname">Name</div>
			    <div class="hmail">Mail</div>
			    <div class="hright">Right</div>
			    <div class="hbutton"></div>
			</div>
		<c:forEach items="${my_user_list}" var="user">
			<div id="<c:out value="${user.name}"/>" class="user">
			    <div class="name"><c:out value="${user.name}"/></div>
			    <div class="mail"><c:out value="${user.mail}"/></div>
			    <div class="right"><c:out value="${user.right}"/></div>
			    <div class="button"><a href="${pageContext.request.contextPath}/administration/userModification?user_name=<c:out value="${user.name}"/>"><img src="${pageContext.request.contextPath}/IMG/edit.jpg" alt="Edit"></a></div>
			</div>
		</c:forEach>
		</div>
	</c:otherwise>
</c:choose>
<div id="add_user">
	<a href="${pageContext.request.contextPath}/administration/userAdd"><img src="${pageContext.request.contextPath}/IMG/add_user.jpg" alt="Add user"></a>
</div>
