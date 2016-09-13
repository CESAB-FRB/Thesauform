<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<content tag="logout"> <jsp:include page="logout.jsp" /> </content>

<content tag="local_script"> <script
	src="//code.jquery.com/jquery-1.10.2.js"></script> <script
	src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<link rel="stylesheet" href="../CSS/jquery.qtip.css" />
<script type="text/javascript" src="../JS/jquery.qtip.js"></script> <script
	type="text/javascript">
	$(document).ready(function() {
		$(function() {
			$("#tabs").tabs();
		});
		$('a[title]').qtip({
			position : {
				my : "bottom left",
				at : "top right",
			},
			show : {
				event : 'click'
			},
			hide : {
				event : 'click'
			},
			style : {
				classes : 'helpClasse'
			}
		});
	});

	//add note
	function addVote(id, name, prop, value, vote) {
		delVote(id, name, prop, value)
		if (vote != 0) {
			var json = $.ajax({
				url : "vote?action=add&trait_name=" + name + "&property="
						+ prop + "&value=" + value + "&vote_value=" + vote,
				async : false
			}).responseText;
			var obj = JSON.parse(json);
			if (!obj.error) {
				$("#" + id).html(obj.nb);
			}
		}
	}

	function delVote(id, name, prop, value) {
		var json = $.ajax({
			url : "vote?action=del&trait_name=" + name + "&property=" + prop
					+ "&value=" + value + "&vote_value=" + 0,
			async : false
		}).responseText;
		var obj = JSON.parse(json);
		if (!obj.error) {
			$("#" + id).html(obj.nb);
		}
	}
</script> </content>
<div id="content">
	<h2
		class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">Vote
		for the term: ${myTraitVote.uri}</h2>
	<table id="modif1">
		<c:if test="${myTraitVote.isInserted}">
			<tr>
				<td colspan="2">
					<h3>How important is this term for inclusion?</h3>
				</td>
				<td><select title="ajout" class="notation"
					onchange="addVote(this.title, '${myTraitVote.uri}', 'insert',  'insert', this.options[this.selectedIndex].value);">
						<option
							<c:if test="${myTraitVote.nbInsertVote==0}">selected="selected"</c:if>
							value="0">no response</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==1}">selected="selected"</c:if>
							value="1">don't like at all</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==2}">selected="selected"</c:if>
							value="2">like a bit</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==3}">selected="selected"</c:if>
							value="3">ambivalent</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==4}">selected="selected"</c:if>
							value="4">like</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==5}">selected="selected"</c:if>
							value="5">like a lot</option>
				</select></td>
			</tr>
		</c:if>
		<c:choose>
			<c:when test="${empty myTraitVote.deleteList}">
			</c:when>
			<c:otherwise>
				<c:set var="count" value="1" scope="page" />
				<tr>
					<td colspan="4">
						<h3>Do you think the term should be removed?</h3>
					</td>
				</tr>
				<tr>
					<td colspan="2"><c:forEach items="${myTraitVote.deleteList}"
							var="contributor">
							<c:choose>
								<c:when test="${count == 1}">
									<c:out value="${contributor}" />
								</c:when>
								<c:otherwise>
										, <c:out value="${contributor}" />
								</c:otherwise>
							</c:choose>
							<c:set var="count" value="${count + 1}" scope="page" />
						</c:forEach> propose to delete this term</td>
					<td><select title="delete" class="notation"
						onchange="addVote(this.title, '${myTraitVote.uri}', 'delete',  'delete', this.options[this.selectedIndex].value);">
							<option
								<c:if test="${myTraitVote.nbDeleteVote==0}">selected="selected"</c:if>
								value="0">no response</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==1}">selected="selected"</c:if>
								value="1">don't like at all</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==2}">selected="selected"</c:if>
								value="2">like a bit</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==3}">selected="selected"</c:if>
								value="3">ambivalent</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==4}">selected="selected"</c:if>
								value="4">like</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==5}">selected="selected"</c:if>
								value="5">like a lot</option>
					</select></td>
				</tr>
			</c:otherwise>
		</c:choose>
		<c:if
			test="${!(empty myTraitVote.nameList)||!(empty myTraitVote.definitionList)||!(empty myTraitVote.referenceList)||!(empty myTraitVote.abbreviationList)||!(empty myTraitVote.referenceList)||!(empty myTraitVote.synonymList)||!(empty myTraitVote.relatedList)||!(empty myTraitVote.categoryList)||!(empty myTraitVote.unitList)||!(empty myTraitVote.commentList)}">
			<tr>
				<td colspan="4">
					<h3>Please rate the following proposals linked to the term :</h3>
				</td>
			</tr>
			<c:choose>
				<c:when test="${empty myTraitVote.nameList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="1">Name
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one abbreviation has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</th>
						<th colspan="3"></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.nameList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current name</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="prefLabel${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'prefLabel', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="prefLabel${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'prefLabel', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.definitionList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="1">Definition
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference for all proposals listed using the picklist.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</th>
						<th colspan="3"></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.definitionList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current definition</td>
										<td><c:out value="${prop.key}" /> <c:if
												test="${!empty myTraitVote.referenceList}">
												<c:forEach items="${myTraitVote.referenceList}"
													var="myTypeList2">
													<c:forEach items="${myTypeList.value}" var="prop2">
														<c:if test="${myTypeList2.key=='current'}">
															(ref: <c:out value="${prop2.key}" />)
														</c:if>
													</c:forEach>
												</c:forEach>
											</c:if></td>
										<td><select title="def${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'definition', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:choose>
												<c:when test="${fn:contains(prop.key,'__')}">
													<c:set var="my_def_ref"
														value="${fn:replace(prop.key, '__', '|')}" />
													<c:set var="my_def_ref"
														value="${fn:split(my_def_ref, '|')}" />
													<c:out value="${my_def_ref[0]}" /> (ref: <c:out
														value="${my_def_ref[1]}" />)
												</c:when>
												<c:otherwise>
													<c:out value="${prop.key}" />
												</c:otherwise>
											</c:choose></td>
										<td><select title="def${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'definition', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.abbreviationList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="1">Abbreviation
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one abbreviation has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</th>
						<th colspan="3"></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.abbreviationList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="abbrev${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'abbreviation', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="abbrev${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'abbreviation', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.synonymList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="1"><b>Synonym</b>
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one abbreviation has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div></th>
						<th colspan="3"></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.synonymList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="syn${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'altLabel', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="syn${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'altLabel', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.relatedList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="1"><b>Related</b>
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one abbreviation has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div></th>
						<th colspan="3"></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.relatedList}" var="prop">
						<tr>
							<td>Proposition ${count}</td>
							<td><c:out value="${prop.key}" /></td>
							<td><select title="rel${count}" class="notation"
								onchange="addVote(this.title, '${myTraitVote.uri}', 'related', '${prop.key}', this.options[this.selectedIndex].value);">
									<option
										<c:if test="${prop.value==0}">selected="selected"</c:if>
										value="0">no response</option>
									<option
										<c:if test="${prop.value==1}">selected="selected"</c:if>
										value="1">don't like at all</option>
									<option
										<c:if test="${prop.value==2}">selected="selected"</c:if>
										value="2">like a bit</option>
									<option
										<c:if test="${prop.value==3}">selected="selected"</c:if>
										value="3">ambivalent</option>
									<option
										<c:if test="${prop.value==4}">selected="selected"</c:if>
										value="4">like</option>
									<option
										<c:if test="${prop.value==5}">selected="selected"</c:if>
										value="5">like a lot</option>
							</select></td>
						</tr>
						<c:set var="count" value="${count + 1}" scope="page" />
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.unitList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="1"><b>Unit</b>
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one abbreviation has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div></th>
						<th colspan="3"></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.unitList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="unit${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'prefUnit', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="unit${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'prefUnit', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.categoryList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="1">Category
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one abbreviation has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</th>
						<th colspan="3"></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.categoryList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="category${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'broaderTransitive', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td><select title="category${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'broaderTransitive', '${prop.key}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value==1}">selected="selected"</c:if>
													value="1">don't like at all</option>
												<option
													<c:if test="${prop.value==2}">selected="selected"</c:if>
													value="2">like a bit</option>
												<option
													<c:if test="${prop.value==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value==4}">selected="selected"</c:if>
													value="4">like</option>
												<option
													<c:if test="${prop.value==5}">selected="selected"</c:if>
													value="5">like a lot</option>
										</select></td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.commentList}">
				</c:when>
				<c:otherwise>
					<tr>
						<td>
							<h3>Comments</h3>
						</td>
					</tr>
					<tr>
						<c:forEach items="${myTraitVote.commentList}" var="myAnnotation">
							<tr>
								<td><c:out value="${myAnnotation.property}" /></td>
								<td><c:out
										value="${myAnnotation.value} by ${myAnnotation.creator}" /></td>
							</tr>
						</c:forEach>
					</tr>
				</c:otherwise>
			</c:choose>
		</c:if>
	</table>
</div>