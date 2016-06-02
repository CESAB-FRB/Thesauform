<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/add_user.css" type="text/css" />
</content>

<c:choose>
	<c:when test="${not empty errors['user_error']}">
		<div id="errorloginName" class="ui-state-error ui-corner-all">
			<span class="ui-icon ui-icon-alert"></span> 
			<strong>${errors['user_error']}</strong>
		</div>
	</c:when>
	<c:otherwise>
		<c:if test="${not empty success}">
			<div id="success">
				<strong><c:out value="${success}"/></strong>
			</div>
		</c:if>
		<form id="userform" method="post" action="/administration/userAdd" >
			<div class="user_row">
				<div id="name">Name:</div>
				<div id="user_name">
					<input type="text" name="user_name" />
				</div>
			</div>
			<div class="user_row">
				<div id="mail">Email:</div>
				<div id="user_mail">
					<input type="text" name="user_mail" />
				</div>
			</div>
			<div class="user_row">
				<div id="right">Right:&nbsp;</div>
				<div id="user_right">
					<select name="user_right">
						<option value="public" selected>User</option>
						<option value="expert" selected>Expert</option>
						<option value="admin">Administrator</option>
					</select>
				</div>
			</div>
			<div class="user_row">
				<div id="password">Password:</div>
				<div id="user_password">
					<input type="password" name="user_password" />
				</div>
			</div>
			<div class="user_row">
				<div id="submit">&nbsp;</div>
				<div id="user_submit">
					<input type="submit" id="envoi" value="OK" class="button" />
				</div>
			</div>
		</form>
	</c:otherwise>
</c:choose>
