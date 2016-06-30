<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/user.css" type="text/css" />
</content>

<c:choose>
	<c:when test="${not empty errors['user_error']}">
		<div id="errorloginName" class="ui-state-error ui-corner-all">
			<span class="ui-icon ui-icon-alert"></span> <strong>${errors['user_error']}</strong>
		</div>
	</c:when>
	<c:otherwise>
		<c:if test="${not empty success}">
			<div id="success">
				<strong><c:out value="${success}" /></strong>
			</div>
		</c:if>
		<form id="userform" method="post" action="${pageContext.request.contextPath}/administration/userModification">
			<div class="user_row">
				<div id="name">Name:</div>
				<div id="user_name">
					<c:out value="${my_user.name}" />
				</div>
			</div>
			<input type="hidden" name="user_name"
				value="<c:out value="${my_user.name}"/>" />
			<div class="user_row">
				<div id="mail">Email:</div>
				<div id="user_mail">
					<input type="text" name="user_mail"
						value="<c:out value="${my_user.mail}"/>" />
				</div>
			</div>
			<div class="user_row">
				<div id="right">Right:&nbsp;</div>
				<div id="user_right">
					<select name="user_right">
						<c:choose>
							<c:when test="${my_user.right eq 'public'}">
								<option value="public" selected>User</option>
							</c:when>
							<c:otherwise>
								<option value="public">User</option>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${my_user.right eq 'expert'}">
								<option value="expert" selected>Expert</option>
							</c:when>
							<c:otherwise>
								<option value="expert">Expert</option>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${my_user.right eq 'admin'}">
								<option value="admin" selected>Administrator</option>
							</c:when>
							<c:otherwise>
								<option value="admin">Administrator</option>
							</c:otherwise>
						</c:choose>
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
				<div id="delete">Delete user:</div>
				<div id="user_delete">
					<input type="checkbox" name="del" value="1" />
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
