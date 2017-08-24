<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/api.css" type="text/css" />
</content>

<h3>API to interact with validated data</h3>
<div id="content" style="width: 900px;">
	<ul>
		<li>Get all terms: <a href="searchApi?type=list&format=json&concept=All">export</a></li>
		<li>List of term: <a href="apiList">list</a></li>
	</ul>
</div>
