<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
</content>
<c:if test="${not empty my_errors['type']}">
	<div id="errorloginName" class="ui-state-error ui-corner-all">
		<span class="ui-icon ui-icon-alert"></span> 
		<strong>${my_errors['type']}</strong>
	</div>
</c:if>