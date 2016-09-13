<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<content tag="local_script">
	<script type="text/javascript">
		$(document).ready(function() {
			$("#tabs").tabs();
		});
	</script>
</content>

<div id="content">
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Thesaurus annotation</a></li>
			<li><a href="#tabs-2">Thesaurus vote</a></li>
			<li><a href="#tabs-3">Thesaurus administration</a></li>
		</ul>
		<div id="tabs-1">
			<form id="attachform1" name="" method="post"
				action="authentication">
				<c:if test="${not empty errors['name']}">
					<div id="errorloginName" class="ui-state-error ui-corner-all">
						<span class="ui-icon ui-icon-alert"
							style="float: left; margin-right: 0.3em;"></span> <strong>${errors['name']}</strong>
					</div>
				</c:if>
				<c:if test="${not empty errors['mail']}">
					<div id="errorlogin" class="ui-state-error ui-corner-all">
						<span class="ui-icon ui-icon-alert"
							style="float: left; margin-right: 0.3em;"></span> <strong>${errors['mail']}</strong>
					</div>
				</c:if>
				<c:if test="${not empty errors['status']}">
					<div id="errorloginLogin" class="ui-state-error ui-corner-all">
						<span class="ui-icon ui-icon-alert"
							style="float: left; margin-right: 0.3em;"></span> <strong>${errors['status']}</strong>
					</div>
				</c:if>

				<div class="row">

					<label id="l_name" for="name">Name:&nbsp;</label> <input
						type="text" name="name" id="name"
						value="<c:out value="${param.name}"/>" style="width: 200px;" />
				</div>
				<div class="row">
					<label id="l_pw" for="pw">Password:&nbsp;</label> <input
						type="password" name="pw" id="pw" style="width: 200px;" />
				</div>
				<input type="hidden" name="action" id="action" value="annotation" />
				<div style="clear: both"></div>
				<input type="submit" id="envoi" value="Login" class="button" />

				<div id="filediv"></div>
			</form>
		</div>
		<div id="tabs-2">
			<form id="attachform2" name="" method="post"
				action="authentication">
				<div id="errorlogin2" class="ui-state-error ui-corner-all"
					style="display: none;">
					<span class="ui-icon ui-icon-alert"
						style="float: left; margin-right: 0.3em;"></span> <strong>Alert:</strong>
					Identification failed
				</div>
				<div class="row">
					<label id="l_name1" for="name1">Name:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
					<input type="text" name="name" id="name1" style="width: 200px;" />
				</div>
				<div class="row">
					<label id="l_pw" for="pw">Password:&nbsp;</label> <input
						type="password" name="pw" id="pw" style="width: 200px;" />
				</div>
				<input type="hidden" name="action" id="action" value="expert" />
				<div style="clear: both"></div>
				<input type="submit" id="envoi" value="Login" class="button" />
			</form>
		</div>
		<div id="tabs-3">
			<form id="attachform3" name="" method="post"
				action="authentication">
				<div id="errorlogin2" class="ui-state-error ui-corner-all"
					style="display: none;">
					<span class="ui-icon ui-icon-alert"
						style="float: left; margin-right: 0.3em;"></span> <strong>Alert:</strong>
					Identification failed
				</div>
				<div class="row">
					<label id="l_name1" for="name1">Name:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
					<input type="text" name="name" id="name2" style="width: 200px;" />
				</div>
				<div class="row">
					<label id="l_pw" for="pw">Password:&nbsp;</label> <input
						type="password" name="pw" id="pw" style="width: 200px;" />
				</div>
				<input type="hidden" name="action" id="action"
					value="administration" />
				<div style="clear: both"></div>
				<input type="submit" id="envoi" value="Login" class="button" />
			</form>
		</div>
	</div>
</div>
<div id="aside">
	<h3>Information :</h3>
	<p>To read and/or to annotate (make comments on) the thesaurus, click on the "Thesaurus annotation" tab</p>
	<p>To vote on the proposals suggested in the thesaurus, sign in the "Thesaurus vote" tab</p>
	<p>If you are an administrator, sign in using the "Thesaurus administration" tab (restricted access)</p>
</div>

