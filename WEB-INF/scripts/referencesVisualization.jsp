<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="content" style="width: 900px;">
	<c:forEach items="${my_ref}" var="ref" >
		<p>
			<c:out value="${ref}"/>
		</p>
	</c:forEach> 
</div>