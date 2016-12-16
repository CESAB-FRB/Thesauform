<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/data_administration.css" type="text/css" />
</content>

<h3>Data management</h3>
<div id="content">
	<ul>
		<li><a href="exportFile?file=person">Export person file</a></li>
		<li><a href="exportFile?file=annotation">Export annotation concept file</a></li>
		<li><a href="exportFile?file=public">Export public concept file</a></li>
	</ul>
</div>