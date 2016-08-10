<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="logout">
	<jsp:include page="logout.jsp" />
</content>

<content tag="local_script">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/annotation.css" type="text/css" />
	<script src="//code.jquery.com/jquery-1.10.2.js"></script>
	<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
	<script type="text/javascript" src="JS/jquery.jstree.js"></script>
	<script type="text/javascript" src="JS/jquery.elastic.js"></script>
	<script type="text/javascript">
		$(function() {
			$("#tabs").tabs();
			$('#def').elastic();
		});
	</script>
	<script>
		$(function() {
			$( "#owlauto" ).autocomplete({
				source : function(requete, reponse){ 
					$.ajax({
						url : 'annotationSearch', 
						dataType : 'json', 
						data : {
							trait : $('#owlauto').val(), 
							maxRows : 15
						},
						success : function(data){
							reponse($.map($.parseJSON(JSON.stringify(data)),
							function(objet){
								return objet; // on retourne cette forme de suggestion
							}));
							}
						});
					}, 
				select: function(event, ui) {
					$("#jtree").jstree("search",ui.item.label);
				}, 
				minLength : 3 
			});
		});
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#tabs").tabs();
			$('#treeview').show();
			$(function () {
				$("#jtree").jstree({ 
					"html_data" : {
						"correct_state": false,
						//changer ici 
						"data": "<c:forTokens items="${_term_root_}" delims="," var="root"><li class='jstree-closed' id='${root}'><a href='#' onclick='getInfo(\"<c:out value="${_term_uri_}"/>#${root}\")'><b>${root}</b></a></li></c:forTokens>",
						"ajax" : {
							"url" : "annotationArbre",
							"data" : function (n) { 
								return { trait : n.attr ? n.attr("id") : "${_super_root_}" 
							}; 
						}},
					},
					"core" : { 
						"initially_open" : [<c:forTokens items="${_term_root_}" delims="," var="root" varStatus="loop">"<c:out value="${root}"/>"<c:if test="${!loop.last}">,</c:if></c:forTokens>] 
					},
					"themes" : {
						"theme" : "classic",
						"dots" : true,
						"icons" : false
					},
					"search" : {
						"case_insensitive" : true,
						"ajax" : {
							"url" : "annotationFather",
							"data" : function (n) { 
								return { trait : n }; 
							}
						}
					},
					"plugins" : [ "themes", "html_data", "search", "sort" ]
				})
			});
		});
	
		function arbre(id) {
			var id = id;
			var html = $.ajax({ url: "annotationArbre?trait="+id , async: false }).responseText; 
			$('#'+id).append(html);
			$('#'+id).removeAttr('onClick');
			return html;
		}
	
		function getInfo(item) {
			var item = item;
			item = item.split("#")[1];
			$("#pere").html(item);
			$("#Apere").html(item);
			$("#pereadd").html(item);
			var html = $.ajax({ url: "annotationInfo?trait="+item, async: false }).responseText;
			$("#hpere").val(item);
			$("#inputAdd").val(item);
			$("#inputAnn").val(item);
			$("#bodygrid").html(html);
			$("#jtree").jstree("toggle_node","#"+item);
		}
	</script>
</content>

<div id="content">
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">To modify a term</a></li>
			<li><a href="#tabs-2">To add a term</a></li>
		</ul>
		<div id="tabs-1">
			<div id="description" class="resourcebox ui-widget-content" data-jowl="owl:Class" >
				<h4 class="ui-dialog-titlebar  ui-state-default propertybox" data-jowl="rdfs:label">Description </h4>
					<form id="annotation" Method="POST" Action="annotationModification" >
						<b id="pere"></b>	
						<input type="hidden" id="inputAnn" name="inputAnn" />
						<div id="bodygrid">
						</div>
					</form>
			</div>
		</div>
		<div id="tabs-2">
			<div align="left" class="row">
				Select the new term's category in the tree above
			</div>
			<div id="description1" class="resourcebox ui-widget-content" data-jowl="owl:Class">
				<h4 class="ui-dialog-titlebar  ui-state-default propertybox" data-jowl="rdfs:label">Description </h4>
				<form id="validation" Method="POST" Action="annotationModification" >
					<div class="row">
						<div class="gras">Category : <span id="Apere"></span></div>
						<input type="hidden" id="hpere" name="hpere" />
					</div>
					<div class="row">
						<label id="l_name" for="name">Pref Name:&nbsp;</label>
						<input type="text" name="nameAdd" id="nameAdd" />
					</div>
					<div class="row">
						<label id="l_def" for="def">Definition:&nbsp;</label>
						<textarea name="def" id="def" ></textarea>
					</div>
					<div class="row">
						<label id="l_ref" for="ref">Reference:&nbsp;</label>
						<input type="text"  name="ref" id="ref" />
					</div>
					<div class="row">
						<label id="l_abbr" for="abbr">Abbreviation:&nbsp;</label>
						<input type="text"  name="abbr" id="abbr" />
					</div>
					<div class="row" id="synclone2">
						<label id="l_syn" for="syn">Synonym:&nbsp;</label>
						<input type="text"  name="syn" id="syn" />
						<span class="ui-icon ui-icon-circle-plus" style="float: left; margin-right: 0.3em;" onclick="  $('#synclone2').clone().insertAfter('#synclone2');"></span>
					</div>
					<div class="row" id="relclone2">
						<label id="l_rel" for="related">Related :&nbsp;</label>
						<input type="text"  name="related" id="related" />
						<span class="ui-icon ui-icon-circle-plus" style="float: left; margin-right: 0.3em;" onclick="  $('#relclone2').clone().insertAfter('#relclone2');"></span>
					</div>
					<c:forTokens items="${_trait_display_}" delims="," var="my_display">
						<c:if test="${my_display eq 'unit'}">
							<div class="row">
								<label id="l_unit" for="unit">Pref Unit:&nbsp;</label>
								<input type="text" name="unit" id="unit" />
							</div>
						</c:if>
						<c:if test="${my_display eq 'realName'}">
							<div class="row">
								<label id="l_real_name" for="realName">Real Name:&nbsp;</label>
								<input type="text" name="realNameAdd" id="nameAdd" />
							</div>
						</c:if>
					</c:forTokens>
					<div style="clear: both"></div>
					<input type="submit" id="" value="Submit" class="button" onclick="if($('#hpere').val() == '') return false;"/>
				</form>
			</div>
		</div>
	</div>
</div>
<div id="aside">
		<div id="" class="ui-widget-content">
			<h4 class="ui-dialog-titlebar ui-state-default">Treeview</h4>
			<div id="jtree" class="jstree-classic"></div>
		</div>
	<input id="owlauto" type="text" size="40" style="display:block;width:99%;margin:5px 0px;" title="enter a search term"/>
	<div class="info" style="color: rgb(195, 195 ,195);">Enter Search Terms here</div>
	<hr>
</div>
