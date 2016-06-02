<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach items="${my_trait.sonsList}" var="son">
	<c:choose>
		<c:when test="${!empty son.sonsList}">
			<li class="jstree-closed" id="<c:out value="${son.realName}"/>"  title="<c:out value="${son.uri}"/>">
				<c:choose>
					<c:when test="${viz eq '1'}">
						<a href="annotationInfo?viz=1&&trait=<c:out value='${son.realName}'/>" onclick="getInfo('<c:out value="${son.uri}"/>')"><c:out value="${son.realName}"/></a>
					</c:when>
					<c:otherwise>
						<a href="#" onclick="getInfo('<c:out value="${son.uri}"/>')"><c:out value="${son.realName}"/></a>
					</c:otherwise>
				</c:choose>
			</li>
		</c:when>
		<c:otherwise>
			<li id="<c:out value="${son.realName}"/>"  title="<c:out value="${son.uri}"/>">
			<c:choose>
				<c:when test="${viz eq '1'}">
					<a href="annotationInfo?viz=1&&trait=<c:out value='${son.realName}'/>" onclick="getInfo('<c:out value="${son.uri}"/>')"><c:out value="${son.realName}"/></a>
				</c:when>
				<c:otherwise>
					<a href="#" onclick="getInfo('<c:out value="${son.uri}"/>')"><c:out value="${son.realName}"/></a>
				</c:otherwise>
			</c:choose>
			</li>
		</c:otherwise>
	</c:choose>
</c:forEach>

