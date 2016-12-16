<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>


<h3>Data validation</h3>
<div id="content">
<c:choose>
	<c:when test="${empty vote}">
	</c:when>
	<c:otherwise>
	<table>
		<tr>
			<th style="width: 700px;">Definition</th>
			<th style="width: 200px;">Vote</th>
		</tr>
		<c:set var="count" value="1" scope="page" />
		<c:forEach items="${vote}" var="myVoteList">
			<tr>
				<td>		
					<c:out value="${myVoteList.key}" />
				</td>		
				<td>
			<c:forEach items="${myVoteList.value}" var="myVoteValue">
				<c:if test="${myVoteValue==1}">don't like at all</c:if>
				<c:if test="${myVoteValue==2}">like a bit</c:if>
				<c:if test="${myVoteValue==3}">ambivalent</c:if>
				<c:if test="${myVoteValue==4}">like</c:if>
				<c:if test="${myVoteValue==5}">like a lot</c:if>
				<br />
			</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</table>
	</c:otherwise>
</c:choose>

</div>