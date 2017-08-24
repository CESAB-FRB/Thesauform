<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/administration.css" type="text/css" />
</content>

<h3>Administration panel</h3>
<div id="content">
	<ul>
		<li><a href="administration/usersList">Users management</a></li>
		<li><a href="administration/validateData">Validation</a></li>
		<li><a href="administration/manageData">Data management</a></li>
		<li><a href="administration/clearCache">Clear Cache</a></li>
	</ul>
</div>