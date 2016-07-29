<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<script src="//code.jquery.com/jquery-1.10.2.js"></script>
	<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$(function() {
				$("#tabs").tabs();
			});
		});
	</script>
</content>


<div id="content">
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Modified terms</a></li>
			<li><a href="#tabs-2">Suggested terms</a></li>
			<li><a href="#tabs-3">Deleted terms</a></li>
		</ul>
		<div id="tabs-1">
			<c:choose>
				<c:when test="${empty my_updated_list}">
					No concept modified
				</c:when>
				<c:otherwise>
					<c:forEach items="${my_updated_list}" var="name">
						<div>
							<a
								href="expert/validation?change=Update&&trait=<c:out value="${name}"/>"><c:out
									value="${name}" /></a>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
		<div id="tabs-2">
			<c:choose>
				<c:when test="${empty my_inserted_list}">
					No concept modified
				</c:when>
				<c:otherwise>
					<c:forEach items="${my_inserted_list}" var="name">
						<div>
							<a
								href="expert/validation?change=Insert&&trait=<c:out value="${name}"/>"><c:out
									value="${name}" /></a>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
		<div id="tabs-3">
			<c:choose>
				<c:when test="${empty my_deleted_list}">
					No concept modified
				</c:when>
				<c:otherwise>
					<c:forEach items="${my_deleted_list}" var="name">
						<div>
							<a
								href="expert/validation?change=Delete&&trait=<c:out value="${name}"/>"><c:out
									value="${name}" /></a>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>

