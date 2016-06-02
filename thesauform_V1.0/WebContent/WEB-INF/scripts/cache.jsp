<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/annotation.css"/>	

	<script src="//code.jquery.com/jquery-1.10.2.js"></script>
	<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
</content>
<c:choose>
	<c:when test="${not empty errors['cache_error']}">
		<div id="errorloginName" class="ui-state-error ui-corner-all">
			<span class="ui-icon ui-icon-alert"></span> 
			<strong>${errors['cache_error']}</strong>
		</div>
	</c:when>
	<c:otherwise>
		<h3>Cache cleared</h3>
	</c:otherwise>
</c:choose>