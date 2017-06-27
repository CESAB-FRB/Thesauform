<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
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
	$("#cat").autocomplete(
			{
				source : function(requete, reponse) {
					$.ajax({
						url : 'annotationSearch',
						dataType : 'json',
						data : {
							trait : $('#cat').val(),
							maxRows : 15
						},
						success : function(data) {
							reponse($.map($.parseJSON(JSON.stringify(data)),
									function(objet) {
										return objet; // on retourne cette forme de suggestion
									}));
						}
					});
				}
			});
</script>

<c:if test="${not empty my_errors['parent']}">
	<div id="errorloginName" class="ui-state-error ui-corner-all">
		<span class="ui-icon ui-icon-alert"></span> <strong>${my_errors['parent']}</strong>
	</div>
</c:if>
<div class="row">
	<label id="l_name" for="name">Name: <span
		style='float: right; margin-right: 10px;'><a
			title='a word or set of words by which an entity is known, addressed, or referred to.'><img
				src="IMG/red_help.png"
				style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
	</label> <input type="text" name="nameAnn" id="nameAnn"
		value="<c:out value="${my_trait.name}"/>">
</div>
<div class="row">
	<label id="l_def" for="def">Definition: <span
		style='float: right; margin-right: 10px;'><a
			title='This should unambiguously specify the criteria for identification of the entity.'><img
				src="IMG/red_help.png"
				style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
	</label>
	<textarea name="def" id="def"><c:out
			value="${my_trait.definition}" /></textarea>
</div>
<div class="row">
	<label id="l_ref" for="ref">Source: <span
		style='float: right; margin-right: 10px;'><a
			title='A reference to an article or book, or to a url for a web site.'><img
				src="IMG/red_help.png"
				style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
	</label>
	<textarea rows="1" name="ref" id="ref"><c:out value="${my_trait.reference}"/></textarea>
</div>
<div class="row">
	<label id="l_abbr" for="abbr">Abbreviation: <span
		style='float: right; margin-right: 10px;'><a
			title="A common shortening of a name, such as 'dbh' for 'diameter at breast height'."><img
				src="IMG/red_help.png"
				style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
	</label> <input type="text" name="abbr" id="abbr"
		value="<c:out value="${my_trait.abbreviation}"/>">
</div>
<c:choose>
	<c:when test="${empty my_trait.synonymsList}">
		<div class="row" id="toto">
			<label id="l_syn" for="syn">Synonym: <span
				style='float: right; margin-right: 10px;'><a
					title='Another name that may be commonly used for this term.'><img
						src="IMG/red_help.png"
						style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
			</label> <input type="text" name="syn" id="syn" value=""> <span
				class="ui-icon ui-icon-circle-plus"
				style="float: left; margin-right: 0.3em;"
				onclick=" $('#toto').clone().insertAfter('#toto'); "></span>
		</div>
	</c:when>
	<c:otherwise>
		<c:forEach items="${my_trait.synonymsList}" var="synonym">
			<div class="row" id="toto">
				<label id="l_syn" for="syn">Synonym:&nbsp;</label> <input
					type="text" name="syn" id="syn"
					value="<c:out value="${synonym.realName}"/>"> <span
					class="ui-icon ui-icon-circle-plus"
					style="float: left; margin-right: 0.3em;"
					onclick=" $('#toto').clone().insertAfter('#toto'); "></span>
			</div>
		</c:forEach>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${empty my_trait.relatedsList}">
		<div class="row" id="relclone">
			<label id="l_rel" for="related">Related: <span
				style='float: right; margin-right: 10px;'><a title='A term that you feel is closely allied to this one.'><img
						src="IMG/red_help.png"
						style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
			</label> <input type="text" name="related" id="related" value=""> <span
				class="ui-icon ui-icon-circle-plus"
				style="float: left; margin-right: 0.3em;"
				onclick=" $('#relclone').clone().insertAfter('#relclone'); "></span>
		</div>
	</c:when>
	<c:otherwise>
		<c:forEach items="${my_trait.relatedsList}" var="related">
			<div class="row" id="relclone">
				<label id="l_rel" for="related">Related :&nbsp;</label> <input
					type="text" name="related" id="related "
					value="<c:out value="${related.realName}"/>"> <span
					class="ui-icon ui-icon-circle-plus"
					style="float: left; margin-right: 0.3em;"
					onclick=" $('#relclone').clone().insertAfter('#relclone'); "></span>
			</div>
		</c:forEach>
	</c:otherwise>
</c:choose>
<c:forTokens items="${_trait_display_}" delims="," var="my_display">
	<c:if test="${my_display eq 'unit'}">
		<div class="row">
			<label id="l_unit" for="unit">Unit: <span
				style='float: right; margin-right: 10px;'><a
					title='Units should comply with the International System of Units.'><img
						src="IMG/red_help.png"
						style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
			</label> <input type="text" name="unit" id="unit"
				value="<c:out value="${my_trait.unit}"/>">
		</div>
	</c:if>
	<c:if test="${my_display eq 'realName'}">
		<div class="row">
			<label id="l_name" for="name">Real Name: <span
				style='float: right; margin-right: 10px;'><a title=''><img
						src="IMG/red_help.png"
						style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
			</label> <input type="text" name="nameAnn" id="nameAnn"
				value="<c:out value="${my_trait.realName}"/>">
		</div>
	</c:if>
</c:forTokens>
<c:forEach items="${my_trait.parent}" var="parent"
	varStatus="parent_cpt">
	<div class="row" id="parent<c:out value="${parent_cpt.index}" />">
		<label id="l_cat<c:out value="${parent_cpt.index}" />"
			for="cat<c:out value="${parent_cpt.index}" />">Category: <span
			style='float: right; margin-right: 10px;'><a
				title='The category is the next highest level of the hierarchy. A term may be in more than one category.'><img
					src="IMG/red_help.png"
					style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
		</label> <input style="color: black;" disabled="disabled" type="text"
			name="cat<c:out value="${parent_cpt.index}" />"
			id="cat<c:out value="${parent_cpt.index}" />"
			value="<c:out value="${parent.name}"/>">
		<c:if test="${parent_cpt.index==0}">
			<span class="ui-icon ui-icon-circle-plus"
				style="float: left; margin-right: 0.3em;"
				onclick="document.getElementById('parent').style.display='block'; "></span>
		</c:if>
	</div>
</c:forEach>
<div class="row" id="parent" style="display: none;">
	<label id="l_cat" for="cat">Proposed new category:&nbsp;</label> <input
		type="text" name="cat" id="cat">
</div>
<div class="row">
	<label id="l_comment" for="commment">Comment: <span
		style='float: right; margin-right: 10px;'><a
			title='Use this cell to explain any choices or add additional thoughts.'><img
				src="IMG/red_help.png"
				style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
	</label>
	<textarea name="comment" id="comment"></textarea>
</div>
<div class="row">
	<label id="delete" for="commment">Delete:&nbsp;</label> <input
		id="delete_input" type="checkbox" name="del" value="1"
		style="text-align: left;" />
</div>
<div style="clear: both"></div>
<input type="submit" id="" value="Submit" class="button" />
<div style="clear: both"></div>
<c:if test="${!empty my_trait.annotationsList}">
	<div id="plus"
		onclick="$('#modif').toggle();$('#plus').toggle();$('#moins').toggle();">
		<span class="ui-icon ui-icon-circle-plus"
			style="float: left; margin-right: 0.3em;"></span>
		<h3 align="left">Proposed edits: click to see</h3>
	</div>
	<div id="moins"
		onclick="$('#modif').toggle();$('#plus').toggle();$('#moins').toggle();"
		style="display: none;">
		<span class="ui-icon ui-icon-circle-minus"
			style="float: left; margin-right: 0.3em;"></span>
		<h3 align="left">Proposed edits: click to hide</h3>
	</div>
</c:if>
<table id="modif" style="display: none;">
	<c:forEach items="${my_trait.annotationsList}" var="annotationTmp">
		<tr>
			<td>Annotation<c:out value="${annotationTmp.key}" /></td>
		</tr>
		<c:forEach items="${annotationTmp.value}" var="prop">
			<tr>
				<td></td>
				<td>${prop.getProperty()}</td>
				<td>${prop.getValue()}</td>
			</tr>
		</c:forEach>
	</c:forEach>
</table>
<div style="clear: both"></div>
<c:if test="${!empty my_trait.commentsList}">
	<div id="plus2"
		onclick="$('#com').toggle();$('#plus2').toggle();$('#moins2').toggle();">
		<span class="ui-icon ui-icon-circle-plus"
			style="float: left; margin-right: 0.3em;"></span>
		<h3 align="left">Comments : click to see</h3>
	</div>
	<div id="moins2"
		onclick="$('#com').toggle();$('#plus2').toggle();$('#moins2').toggle();"
		style="display: none;">
		<span class="ui-icon ui-icon-circle-minus"
			style="float: left; margin-right: 0.3em;"></span>
		<h3 align="left">Comments : click to hide</h3>
	</div>
</c:if>
<table id="com" style="display: none;">
	<c:set var="cptComment" value="0" scope="page" />
	<c:forEach items="${my_trait.commentsList}" var="comment">
		<c:set var="cptComment" value="${cptComment + 1}" scope="page" />
		<tr>
			<td>Comment <c:out value="${cptComment}" /></td>
			<td>by <c:out value="${comment.creator}" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><c:out value="${comment.value}" /></td>
		</tr>
	</c:forEach>
</table>


