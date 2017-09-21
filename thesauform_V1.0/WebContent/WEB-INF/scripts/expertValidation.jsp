<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />

<content tag="logout"> <jsp:include page="logout.jsp" /> </content>

<content tag="local_script"> 
	<script src="//code.jquery.com/jquery-1.10.2.js"></script> 
	<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
	<link rel="stylesheet" href="../CSS/expertValidation.css" />
	<link rel="stylesheet" href="../CSS/jquery.qtip.css" />
	<script type="text/javascript" src="../JS/jquery.qtip.js"></script> 
	<script
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
	
		//add vote
		function addVote(id, name, prop, value, vote) {
			value = value.replace("&", ";and;");
			delVote(id, name, prop, value)
			if (vote != 0) {
				var json = $.ajax({
					url : "vote?action=add&trait_name=" + encodeURIComponent(name) + "&property="
							+ prop + "&value=" + encodeURIComponent(value) + "&vote_value=" + vote,
					error: function(xhr, status, error) {
						alert(xhr.responseText);
					},
					success: function(results) { 
						if(results.error) {
							alert("Vote failed. Please contact administrator.")	
						}
					},
					async : false
				}).responseText;
				var obj = JSON.parse(json);
				if (!obj.error) {
					$("#" + id).html(obj.nb);
					updateCountVote(name);
				}
			}
		}
	
		function delVote(id, name, prop, value) {
			var json = $.ajax({
				url : "vote?action=del&trait_name=" + encodeURIComponent(name) + "&property=" + prop
						+ "&value=" + encodeURIComponent(value) + "&vote_value=" + 0,
				async : false
			}).responseText;
			var obj = JSON.parse(json);
			if (!obj.error) {
				$("#" + id).html(obj.nb);
				updateCountVote(name);
			}
		}
		
		//add comment to vote
		function changeVote(name, prop, value, vote, comment, id) {
			value = value.replace("&", ";and;");
			comment = comment.replace("&", ";and;");
			if (vote != 0) {
				var json = $.ajax({
					url : "vote?action=change&trait_name=" + encodeURIComponent(name) + "&property="
							+ prop + "&value=" + encodeURIComponent(value) + "&vote_value=" + vote + "&comment=" + encodeURIComponent(comment),
					error: function(xhr, status, error) {
						alert(xhr.responseText);
					},
					success: function(results) { 
						if(results.error) {
							alert("Comment failed. Please contact administrator.")	
						}
						else {
							document.getElementById(id).style.display='none';
						}
					},
					async : false
				}).responseText;
			}
			else {
				alert("You need to respond to the vote before to comment.")
			}
		}

		function updateCountVote(name) {
			var json = $.ajax({
				url : "count?concept=" + encodeURIComponent(name),
				async : false
			}).responseText;
			var obj = JSON.parse(json);
			if (!obj.error) {
				$("#vote_count").html(obj.nb);
			}
		}

		//hide/display comment
		function displayComment(id, concept, prop, value) {
			if(document.getElementById(id).style.display=='block') {
				document.getElementById(id).style.display='none';
			}
			else {
				//get comment value
				var json = $.ajax({
					url : "get_comment?concept=" + encodeURIComponent(concept) + "&prop="+ prop + "&value=" + encodeURIComponent(value),
					async : false
				}).responseText;
				var obj = JSON.parse(json);
				if (!obj.error && obj.comment!=='null') {
					$("#" + id.replace('comment','comment-text')).html(obj.comment);
				}
				document.getElementById(id).style.display='block';
			}
		}
</script>
</content>
<div id="content">
	<div id="voted_concept_number">
		You have already voted for <c:out value="${countVoted}"/> terms
	</div>
	<div id="concept_vote_title">
		<div id="title" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">Vote for the term: ${myTraitVote.uri}</div>
		<div id="title_vote_count">your tally for this term: <span id="vote_count"><c:out value="${count}" /> </span></div>
	</div>
	<div style="clear: both;"></div>
	<div id="explanation">
		Vote for the options using the drop-down list. Each of your votes will be recorded in the tally score above.<br /> 
		Please click the + icon if you want to leave a comment. When you have finished, click OK.
	</div>
	<table id="modif1">
		<thead>
			<tr>
				<th id="col1"></th>
				<th id="col2"></th>
				<th id="col3"></th>
				<th id="col4"></th>
			</tr>
		</thead>
		<c:if test="${myTraitVote.isInserted}">
			<tr>
				<td colspan="2">
					<h3>How important is this term for inclusion?</h3>
				</td>
				<td>
					<select name="ajout" title="ajout" class="notation"
					onchange="addVote(this.title, '${myTraitVote.uri}', 'insert',  'insert', this.options[this.selectedIndex].value);">
						<option
							<c:if test="${myTraitVote.nbInsertVote==0}">selected="selected"</c:if>
							value="0">no response</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==1}">selected="selected"</c:if>
							value="1">strongly disagree</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==2}">selected="selected"</c:if>
							value="2">disagree</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==3}">selected="selected"</c:if>
							value="3">ambivalent</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==4}">selected="selected"</c:if>
							value="4">agree</option>
						<option
							<c:if test="${myTraitVote.nbInsertVote==5}">selected="selected"</c:if>
							value="5">strongly agree</option>
					</select>
				</td>
				<td class="comment-block">
					Comment: 
					<span onclick="displayComment('comment-insert','${myTraitVote.uri}', 'insert', 'insert')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
					<span id="comment-insert" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
						<textarea id="comment-text-insert" rows="2"><c:out value="${prop.value.comment}" /></textarea>
						<button onclick="changeVote('${myTraitVote.uri}', 'insert', 'insert', document.getElementsByName('ajout')[0].selectedIndex, document.getElementById('comment-text-insert').value,'comment-insert');">
								OK
						</button>
					</span>
				</td>						
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
					<td><select name="delete" title="delete" class="notation"
						onchange="addVote(this.title, '${myTraitVote.uri}', 'delete',  'delete', this.options[this.selectedIndex].value);">
							<option
								<c:if test="${myTraitVote.nbDeleteVote==0}">selected="selected"</c:if>
								value="0">no response</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==1}">selected="selected"</c:if>
								value="1">strongly disagree</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==2}">selected="selected"</c:if>
								value="2">disagree</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==3}">selected="selected"</c:if>
								value="3">ambivalent</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==4}">selected="selected"</c:if>
								value="4">agree</option>
							<option
								<c:if test="${myTraitVote.nbDeleteVote==5}">selected="selected"</c:if>
								value="5">strongly agree</option>
					</select></td>
					<td class="comment-block">
						Comment: 
						<span onclick="displayComment('comment-delete', '${myTraitVote.uri}', 'delete', 'delete')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
						<span id="comment-delete" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
							<textarea id="comment-text-delete" rows="2"><c:out value="${prop.value.comment}" /></textarea>
							<button onclick="changeVote('${myTraitVote.uri}', 'delete', 'delete', document.getElementsByName('delete')[0].selectedIndex, document.getElementById('comment-text-delete').value,'comment-delete');">
								OK
							</button>
						</span>
					</td>	
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
						<td colspan="1" style="font-weight: bold;">Name
							<div style='float: right; padding-right: 20px;'>
								<a title='Please indicate your preference using the combo-box. If more than one abbreviation has been proposed, you will be asked to comment on each.'>
									<img src="../IMG/red_help.png" style="width: 10px; height: 10px; margin-bottom: 3px;" />
								</a>
							</div>
						</td>
						<td colspan="3"></td>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.nameList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current name: </td>
										<td><c:out value="${prop.key}" /></td>
										<td><select name="prefLabel${count}" title="prefLabel${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'prefLabel', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select>
										</td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-name${count}', '${myTraitVote.uri}', 'prefLabel', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-name${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-name${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'prefLabel', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('prefLabel${count}')[0].selectedIndex, document.getElementById('comment-text-name${count}').value,'comment-name${count}');">
													OK
												</button>
											</span>
										</td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}: </td>
										<td><c:out value="${prop.key}" /></td>
										<td><select name="prefLabel${count}" title="prefLabel${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'prefLabel', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-name${count}', '${myTraitVote.uri}', 'prefLabel', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-name${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-name${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'prefLabel', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('prefLabel${count}')[0].selectedIndex, document.getElementById('comment-text-name${count}').value,'comment-name${count}');">
													OK
												</button>
											</span>
										</td>
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
						<td colspan="1" style="font-weight: bold;">Definition
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference for all proposals listed using the picklist.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</td>
						<td colspan="3"></td>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.definitionList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current definition: </td>
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
										<td><select name="def${count}" title="def${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'definition', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-def${count}', '${myTraitVote.uri}', 'definition', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-def${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-def${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'definition', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('def${count}')[0].selectedIndex, document.getElementById('comment-text-def${count}').value,'comment-def${count}');">
													OK
												</button>
											</span>
										</td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}: </td>
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
										<td><select name="def${count}" title="def${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'definition', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-def${count}', '${myTraitVote.uri}', 'definition', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-def${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-def${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'definition', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('def${count}')[0].selectedIndex, document.getElementById('comment-text-def${count}').value,'comment-def${count}');">
													OK
												</button>											</span>
										</td>										
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
						<td colspan="1" style="font-weight: bold;">Abbreviation
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one abbreviation has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</td>
						<td colspan="3"></td>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.abbreviationList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation: </td>
										<td><c:out value="${prop.key}" /></td>
										<td><select name="abbrev${count}" title="abbrev${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'abbreviation', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-abb${count}', '${myTraitVote.uri}', 'abbreviation', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-abb${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-abb${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'abbreviation', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('abbrev${count}')[0].selectedIndex, document.getElementById('comment-text-abb${count}').value,'comment-abb${count}');">
													OK
												</button>											</span>
										</td>										
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}: </td>
										<td><c:out value="${prop.key}" /></td>
										<td><select name="abbrev${count}" title="abbrev${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'abbreviation', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-abb${count}', '${myTraitVote.uri}', 'abbreviation', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-abb${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-abb${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'abbreviation', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('abbrev${count}')[0].selectedIndex, document.getElementById('comment-text-abb${count}').value,'comment-abb${count}');">
													OK
												</button>											</span>
										</td>										
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
						<td colspan="1" style="font-weight: bold;"><b>Synonym</b>
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one synonym has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</td>
						<td colspan="3"></td>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:if test="${not empty myTraitVote.synonymList['current']}">
						<c:forEach items="${myTraitVote.synonymList['current']}" var="prop" varStatus="cptSyn">
							<tr>
								<td>Current synonym: ${cptSyn.index + 1}</td>
								<td><c:out value="${prop.key}" /></td>
								<td><select name="syn${count}" title="syn${count}" class="notation"
									onchange="addVote(this.title, '${myTraitVote.uri}', 'validatedAltLabel', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
										<option
											<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
											value="0">no response</option>
										<option
											<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
											value="1">strongly disagree</option>
										<option
											<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
											value="2">disagree</option>
										<option
											<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
											value="3">ambivalent</option>
										<option
											<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
											value="4">agree</option>
										<option
											<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
											value="5">strongly agree</option>
								</select></td>
								<td class="comment-block">
									Comment: 
									<span onclick="displayComment('comment-syn${count}', '${myTraitVote.uri}', 'validatedAltLabel', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
									<span id="comment-syn${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
										<textarea id="comment-text-syn${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
										<button onclick="changeVote('${myTraitVote.uri}', 'validatedAltLabel', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('syn${count}')[0].selectedIndex, document.getElementById('comment-text-syn${count}').value,'comment-syn${count}');">
											OK
										</button>
									</span>
								</td>										
							</tr>
							<c:set var="count" value="${count + 1}" scope="page" />
						</c:forEach>
					</c:if>
					<c:if test="${not empty myTraitVote.synonymList['proposed']}">
						<c:forEach items="${myTraitVote.synonymList['proposed']}" var="prop" varStatus="cptSyn">
							<tr>
								<td>Proposition ${cptSyn.index + 1 }: </td>
								<td><c:out value="${prop.key}" /></td>
								<td><select name="syn${count}" title="syn${count}" class="notation"
									onchange="addVote(this.title, '${myTraitVote.uri}', 'altLabel', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
										<option
											<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
											value="0">no response</option>
										<option
											<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
											value="1">strongly disagree</option>
										<option
											<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
											value="2">disagree</option>
										<option
											<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
											value="3">ambivalent</option>
										<option
											<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
											value="4">agree</option>
										<option
											<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
											value="5">strongly agree</option>
								</select></td>
								<td class="comment-block">
									Comment: 
									<span onclick="displayComment('comment-syn${count}', '${myTraitVote.uri}', 'altLabel', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
									<span id="comment-syn${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
										<textarea id="comment-text-syn${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
										<button onclick="changeVote('${myTraitVote.uri}', 'altLabel', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('syn${count}')[0].selectedIndex, document.getElementById('comment-text-syn${count}').value,'comment-syn${count}');">
											OK
										</button>											</span>
								</td>										
							</tr>
							<c:set var="count" value="${count + 1}" scope="page" />
						</c:forEach>
					</c:if>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.relatedList}">
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="1" style="font-weight: bold;"><b>Related</b>
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one related has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</td>
						<td colspan="3"></td>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:if test="${not empty myTraitVote.relatedList['current']}">
						<c:forEach items="${myTraitVote.relatedList['current']}" var="prop" varStatus="cptRel">
							<tr>
								<td>Current related ${cptRel.index + 1}: </td>
								<td><c:out value="${prop.key}" /></td>
								<td><select name="rel${count}" title="rel${count}" class="notation"
									onchange="addVote(this.title, '${myTraitVote.uri}', 'related', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
										<option
											<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
											value="0">no response</option>
										<option
											<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
											value="1">strongly disagree</option>
										<option
											<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
											value="2">disagree</option>
										<option
											<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
											value="3">ambivalent</option>
										<option
											<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
											value="4">agree</option>
										<option
											<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
											value="5">strongly agree</option>
								</select></td>
								<td class="comment-block">
									Comment: 
									<span onclick="displayComment('comment-rel${count}', '${myTraitVote.uri}', 'related', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
									<span id="comment-rel${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
										<textarea id="comment-text-rel${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
										<button onclick="changeVote('${myTraitVote.uri}', 'related', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('rel${count}')[0].selectedIndex, document.getElementById('comment-text-rel${count}').value,'comment-rel${count}');">
											OK
										</button>								</span>
								</td>										
							</tr>
							<c:set var="count" value="${count + 1}" scope="page" />
					</c:forEach>
					</c:if>
					<c:if test="${not empty myTraitVote.relatedList['proposed']}">
						<c:forEach items="${myTraitVote.relatedList['proposed']}" var="prop" varStatus="cptRel">
						<tr>
							<td>Proposition ${cptRel.index + 1}: </td>
							<td><c:out value="${prop.key}" /></td>
							<td><select name="rel${count}" title="rel${count}" class="notation"
								onchange="addVote(this.title, '${myTraitVote.uri}', 'related', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
									<option
										<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
										value="0">no response</option>
									<option
										<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
										value="1">strongly disagree</option>
									<option
										<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
										value="2">disagree</option>
									<option
										<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
										value="3">ambivalent</option>
									<option
										<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
										value="4">agree</option>
									<option
										<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
										value="5">strongly agree</option>
							</select></td>
							<td class="comment-block">
								Comment: 
								<span onclick="displayComment('comment-rel${count}', '${myTraitVote.uri}', 'related', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
								<span id="comment-rel${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
									<textarea id="comment-text-rel${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
									<button onclick="changeVote('${myTraitVote.uri}', 'related', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('rel${count}')[0].selectedIndex, document.getElementById('comment-text-rel${count}').value,'comment-rel${count}');">
										OK
									</button>								</span>
							</td>										
						</tr>
						<c:set var="count" value="${count + 1}" scope="page" />
						</c:forEach>
					</c:if>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty myTraitVote.unitList}">
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="1" style="font-weight: bold;"><b>Unit</b>
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one unit has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</td>
						<td colspan="3"></td>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.unitList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current unit: </td>
										<td><c:out value="${prop.key}" /></td>
										<td><select name="unit${count}" title="unit${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'prefUnit', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-unit${count}', '${myTraitVote.uri}', 'prefUnit', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-unit${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-unit${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'prefUnit', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('unit${count}')[0].selectedIndex, document.getElementById('comment-text-unit${count}').value,'comment-unit${count}');">
													OK
												</button>
											</span>
										</td>										
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}: </td>
										<td><c:out value="${prop.key}" /></td>
										<td><select name="unit${count}" title="unit${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'prefUnit', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-unit${count}', '${myTraitVote.uri}', 'prefUnit', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-unit${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-unit${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'prefUnit', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('unit${count}')[0].selectedIndex, document.getElementById('comment-text-unit${count}').value,'comment-unit${count}');">
													OK
												</button>
											</span>
										</td>										
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
						<td colspan="1" style="font-weight: bold;">Category
							<div style='float: right; padding-right: 20px;'>
								<a
									title='Please indicate your preference using the combo-box. If more than one category has been proposed, you will be asked to comment on each.'><img
									src="../IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a>
							</div>
						</td>
						<td colspan="3"></td>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.categoryList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current category: </td>
										<td><c:out value="${prop.key}" /></td>
										<td><select name="category${count}" title="category${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'broaderTransitive', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-cat${count}', '${myTraitVote.uri}', 'broaderTransitive', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											<span id="comment-cat${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-cat${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'broaderTransitive', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('category${count}')[0].selectedIndex, document.getElementById('comment-text-cat${count}').value,'comment-cat${count}');">
													OK
												</button>
											</span>
										</td>										
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}: </td>
										<td><c:out value="${prop.key}" /></td>
										<td><select name="category${count}" title="category${count}" class="notation"
											onchange="addVote(this.title, '${myTraitVote.uri}', 'broaderTransitive', '${fn:replace(prop.key,search,replace)}', this.options[this.selectedIndex].value);">
												<option
													<c:if test="${prop.value.voteValue==0}">selected="selected"</c:if>
													value="0">no response</option>
												<option
													<c:if test="${prop.value.voteValue==1}">selected="selected"</c:if>
													value="1">strongly disagree</option>
												<option
													<c:if test="${prop.value.voteValue==2}">selected="selected"</c:if>
													value="2">disagree</option>
												<option
													<c:if test="${prop.value.voteValue==3}">selected="selected"</c:if>
													value="3">ambivalent</option>
												<option
													<c:if test="${prop.value.voteValue==4}">selected="selected"</c:if>
													value="4">agree</option>
												<option
													<c:if test="${prop.value.voteValue==5}">selected="selected"</c:if>
													value="5">strongly agree</option>
										</select></td>
										<td class="comment-block">
											Comment: 
											<span onclick="displayComment('comment-cat${count}', '${myTraitVote.uri}', 'broaderTransitive', '${fn:replace(prop.key,search,replace)}')" class="ui-icon ui-icon-circle-plus" style="float: right;margin-right: 50%;" ></span>
											
											<span id="comment-cat${count}" <c:if test="${empty prop.value.comment}">style="display: none;"</c:if>>
												<textarea id="comment-text-cat${count}" rows="2"><c:out value="${prop.value.comment}" /></textarea>
												<button onclick="changeVote('${myTraitVote.uri}', 'broaderTransitive', '${fn:replace(prop.key,search,replace)}', document.getElementsByName('category${count}')[0].selectedIndex, document.getElementById('comment-text-cat${count}').value,'comment-cat${count}');">
													OK
												</button>
											</span>
										</td>										
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