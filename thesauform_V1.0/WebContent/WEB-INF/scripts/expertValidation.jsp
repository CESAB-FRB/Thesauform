<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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
		
		function addVote(id, name, prop, value){
			var json = $.ajax({ url: "vote?action=add&trait_name="+name+"&property="+prop+"&value="+value, async: false }).responseText;
			var obj = JSON.parse(json);
			if(!obj.error) {
				$("#"+id).html(obj.nb);				
			}
		}
	
		function delVote(id, name, prop, value){
			var json = $.ajax({ url: "vote?action=del&trait_name="+name+"&property="+prop+"&&value="+value, async: false }).responseText;
			var obj = JSON.parse(json);
			if(!obj.error) {
				$("#"+id).html(obj.nb);				
			}
		}
	</script>
</content>
<div id="content">
	<h2
		class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">Vote
		for the trait: ${myTraitVote.uri}</h2>
	<table id="modif1">
		<c:if test="${myTraitVote.isInserted}">
			<tr>
				<td colspan="2">
					<h3>Validation of trait insertion :</h3>
				</td>
				<td>
					<div id="ajout">${myTraitVote.nbInsertVote}</div>
				</td>
				<td><span title="ajout" class="ui-icon ui-icon-circle-plus"
					onclick="addVote(this.title, '${myTraitVote.uri}', 'insert',  'insert');"></span>
				</td>
				<td><span title="ajout" class="ui-icon ui-icon-circle-minus"
					onclick="delVote(this.title, '${myTraitVote.uri}', 'insert',  'insert');"></span>
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
						<h3>Validation of trait deletetion :</h3>
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
						</c:forEach> propose to delete this trait</td>
					<td>
						<div id="delete">${myTraitVote.nbDeleteVote}</div>
					</td>
					<td><span title="delete" class="ui-icon ui-icon-circle-plus"
						onclick="addVote(this.title, '${myTraitVote.uri}', 'delete',  'delete');"></span>
					</td>
					<td><span title="delete" class="ui-icon ui-icon-circle-minus"
						onclick="delVote(this.title, '${myTraitVote.uri}', 'delete',  'delete');"></span>
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
		<c:if
			test="${!(empty myTraitVote.nameList)||!(empty myTraitVote.definitionList)||!(empty myTraitVote.referenceList)||!(empty myTraitVote.abbreviationList)||!(empty myTraitVote.referenceList)||!(empty myTraitVote.synonymList)||!(empty myTraitVote.relatedList)||!(empty myTraitVote.categoryList)||!(empty myTraitVote.unitList)||!(empty myTraitVote.commentList)}">
			<tr>
				<td colspan="4">
					<h3>Validation of trait update :</h3>
				</td>
			</tr>
			<c:choose>
				<c:when test="${empty myTraitVote.nameList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="4">Pref name</th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.nameList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current name</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="prefLabel${count}">${prop.value}</div>
										</td>
										<td><span title="prefLabel${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'prefLabel', '${prop.key}');"></span>
										</td>
										<td><span title="prefLabel${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'prefLabel', '${prop.key}');"></span>
										</td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="prefLabel${count}">${prop.value}</div>
										</td>
										<td><span title="prefLabel${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'prefLabel', '${prop.key}');"></span>
										</td>
										<td><span title="prefLabel${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'prefLabel', '${prop.key}');"></span>
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
						<th colspan="4">Definition</th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.definitionList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current definition</td>
										<td>
											<c:out value="${prop.key}" />
											<c:if test="${!empty myTraitVote.referenceList}">
												<c:forEach items="${myTraitVote.referenceList}" var="myTypeList2">
													<c:forEach items="${myTypeList.value}" var="prop2">
														<c:if test="${myTypeList2.key=='current'}">
															(ref: <c:out value="${prop2.key}" />)
														</c:if>
													</c:forEach>
												</c:forEach>
											</c:if>
										</td>			
										<td>
											<div id="def${count}">${prop.value}</div>
										</td>
										<td><span title="def${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'definition', '${prop.key}');"></span>
										</td>
										<td><span title="def${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'definition', '${prop.key}');"></span>
										</td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td>
											<c:choose>
												<c:when test="${fn:contains(prop.key,'__')}">
													<c:set var="my_def_ref" value="${fn:replace(prop.key, '__', '|')}"/>
													<c:set var="my_def_ref"
														value="${fn:split(my_def_ref, '|')}" />												
													<c:out value="${my_def_ref[0]}"/> (ref: <c:out value="${my_def_ref[1]}"/>)
												</c:when>
												<c:otherwise>
													<c:out value="${prop.key}" />
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<div id="def${count}">${prop.value}</div>
										</td>
										<td><span title="def${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'definition', '${prop.key}');"></span>
										</td>
										<td><span title="def${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'definition', '${prop.key}');"></span>
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
						<th colspan="4">Abbreviation</th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.abbreviationList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="abbrev${count}">${prop.value}</div>
										</td>
										<td><span title="abbrev${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'abbreviation', '${prop.key}');"></span>
										</td>
										<td><span title="abbrev${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'abbreviation', '${prop.key}');"></span>
										</td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="abbrev${count}">${prop.value}</div>
										</td>
										<td><span title="abbrev${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'abbreviation', '${prop.key}');"></span>
										</td>
										<td><span title="abbrev${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'abbreviation', '${prop.key}');"></span>
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
						<th colspan="4"><b>Synonym</b></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.synonymList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="syn${count}">${prop.value}</div>
										</td>
										<td><span title="syn${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'altLabel', '${prop.key}');"></span>
										</td>
										<td><span title="syn${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'altLabel', '${prop.key}');"></span>
										</td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="syn${count}">${prop.value}</div>
										</td>
										<td><span title="syn${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'altLabel', '${prop.key}');"></span>
										</td>
										<td><span title="syn${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'altLabel', '${prop.key}');"></span>
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
				<c:when test="${empty myTraitVote.relatedList}">
				</c:when>
				<c:otherwise>
					<tr>
						<th colspan="4"><b>Related</b></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.relatedList}" var="prop">
						<tr>
							<td>Proposition ${count}</td>
							<td><c:out value="${prop.key}" /></td>
							<td>
								<div id="rel${count}">{prop.value}</div>
							</td>
							<td><span title="rel${count}"
								class="ui-icon ui-icon-circle-plus"
								onclick="addVote(this.title, '${myTraitVote.uri}', 'related', '${prop.key}');"></span>
							</td>
							<td><span title="rel${count}"
								class="ui-icon ui-icon-circle-minus"
								onclick="delVote(this.title, '${myTraitVote.uri}', 'related', '${prop.key}');"></span>
							</td>
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
						<th colspan="4"><b>Pref unit</b></th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.unitList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="unit${count}">${prop.value}</div>
										</td>
										<td><span title="unit${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'prefUnit', '${prop.key}');"></span>
										</td>
										<td><span title="unit${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'prefUnit', '${prop.key}');"></span>
										</td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="unit${count}">${prop.value}</div>
										</td>
										<td><span title="unit${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'prefUnit', '${prop.key}');"></span>
										</td>
										<td><span title="unit${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'prefUnit', '${prop.key}');"></span>
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
						<th colspan="4">Category</th>
					</tr>
					<c:set var="count" value="1" scope="page" />
					<c:forEach items="${myTraitVote.categoryList}" var="myTypeList">
						<c:forEach items="${myTypeList.value}" var="prop">
							<c:choose>
								<c:when test="${myTypeList.key=='current'}">
									<tr>
										<td>Current abbreviation</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="category${count}">${prop.value}</div>
										</td>
										<td><span title="category${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'broaderTransitive', '${prop.key}');"></span>
										</td>
										<td><span title="category${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'broaderTransitive', '${prop.key}');"></span>
										</td>
									</tr>
									<c:set var="count" value="${count + 1}" scope="page" />
								</c:when>
								<c:otherwise>
									<tr>
										<td>Proposition ${count}</td>
										<td><c:out value="${prop.key}" /></td>
										<td>
											<div id="category${count}">${prop.value}</div>
										</td>
										<td><span title="category${count}"
											class="ui-icon ui-icon-circle-plus"
											onclick="addVote(this.title, '${myTraitVote.uri}', 'broaderTransitive', '${prop.key}');"></span>
										</td>
										<td><span title="category${count}"
											class="ui-icon ui-icon-circle-minus"
											onclick="delVote(this.title, '${myTraitVote.uri}', 'broaderTransitive', '${prop.key}');"></span>
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