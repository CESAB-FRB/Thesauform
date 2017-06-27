<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<content tag="logout"> <jsp:include page="logout.jsp" /> </content>

<content tag="local_script">
	<link rel="stylesheet" href="CSS/annotation.css" type="text/css" />
	<link rel="stylesheet" href="CSS/jquery.qtip.css" />
	<script src="//code.jquery.com/jquery-1.10.2.js"></script> <script
		src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script> <script
		type="text/javascript" src="JS/jquery.jstree.js"></script> <script
		type="text/javascript" src="JS/jquery.elastic.js"></script> <script
		type="text/javascript" src="JS/jquery.qtip.js"></script> <script
		type="text/javascript">
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
				minLength : 2 
			});
		});
	</script> 
	<script type="text/javascript">
		$(document).ready(function() {
			$('a[title]').qtip({
				position: {
					my: "bottom left",
					at: "top right",
				},
				show: {
					event: 'click'
				},
				hide: {
					event: 'click'
				},
				style: {
					classes: 'helpClasse'
				}
			});
			$("#tabs").tabs();
			$('#treeview').show();
			$(function () {
				$("#jtree").jstree({ 
					"html_data" : {
						"correct_state": false,
						//changer ici 
						"data": "<c:forTokens items="${_term_root_}" delims="," var="root"><li class='jstree-closed' id='${fn:replace(root, ' ', '_')}'><a href='#' onclick='getInfo(\"<c:out value="${_term_uri_}"/>#${root}\")'><b>${fn:replace(root, '_', ' ')}</b></a></li></c:forTokens>",
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
		
		function checkSpecialChar(form) { 	
			var fieldValuePairs = $('#'+form).serializeArray();
			$.each(fieldValuePairs, function(index, fieldValuePair) {
				if(fieldValuePair.value.indexOf('#') !== -1) {
					if(fieldValuePair.name=='def') {
					    alert("definition should not contain the character #");
					}
					else {
					    alert(fieldValuePair.name + " should not contain the character #");	
					}
				    $('<input/>', { id: 'fail', name: 'fail', value: '1', type: 'hidden' }).appendTo('#'+form);
				}
			});
		} 
		
		function validate(form) {
			checkSpecialChar(form);
			if($('#hpere').val() == '') {
				alert('Please choose a category in the tree to the right');
		        	return false;
		    	}
		    	else {
		    		return(confirm('Do you really want to insert term ' + $('#nameAdd').val() + ' with parent ' + $('#hpere').val() + '?'));
		    	}
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
			<div id="description" class="resourcebox ui-widget-content"
				data-jowl="owl:Class">
				<h4 class="ui-dialog-titlebar  ui-state-default propertybox"
					data-jowl="rdfs:label">Description</h4>
				<form id="annotation" Method="POST" Action="annotationModification" onsubmit="checkSpecialChar('annotation')">
					<b id="pere"></b> <input type="hidden" id="inputAnn"
						name="inputAnn" />
					<div id="bodygrid"></div>
				</form>
			</div>
		</div>
		<div id="tabs-2">
			<div align="left" class="row">Select the new term's category in
				the tree to the right</div>
			<div id="description1" class="resourcebox ui-widget-content"
				data-jowl="owl:Class">
				<h4 class="ui-dialog-titlebar  ui-state-default propertybox"
					data-jowl="rdfs:label">Description</h4>
				<form id="validation" Method="POST" Action="annotationModification" onsubmit="return(validate('validation'));">
					<div class="row">
						<div class="gras">
							Category: <span id="Apere"></span>
						</div>
						<input type="hidden" id="hpere" name="hpere" />
					</div>
					<div class="row">
						<label id="l_name" for="name">Name:&nbsp;<span
							style='float: right; margin-right: 10px;'><a
								title='a word or set of words by which an entity is known, addressed, or referred to.'><img
									src="IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span></label> <input
							type="text" name="nameAdd" id="nameAdd" />
					</div>
					<div class="row">
						<label id="l_def" for="def">Definition: <span
							style='float: right; margin-right: 10px;'><a
								title='This should unambiguously specify the criteria for identification of the entity.'><img
									src="IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
						</label>
						<textarea name="def" id="def"></textarea>
					</div>
					<div class="row">
						<label id="l_ref" for="ref">Source: <span
							style='float: right; margin-right: 10px;'><a
								title='A reference to an article or book, or to a url for a web site.'><img
									src="IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
						</label>
						<textarea name="ref" id="ref" rows="1"></textarea>
					</div>
					<div class="row">
						<label id="l_abbr" for="abbr">Abbreviation: <span
							style='float: right; margin-right: 10px;'><a
								title="A common shortening of a name, such as 'dbh'for 'diameter at breast height'."><img
									src="IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
						</label> <input type="text" name="abbr" id="abbr" />
					</div>
					<div class="row" id="synclone2">
						<label id="l_syn" for="syn">Synonym: <span
							style='float: right; margin-right: 10px;'><a
								title='Another name that may be commonly used for this term.'><img
									src="IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
						</label> <input type="text" name="syn" id="syn" /> <span
							class="ui-icon ui-icon-circle-plus"
							style="float: left; margin-right: 0.3em;"
							onclick="  $('#synclone2').clone().insertAfter('#synclone2');"></span>
					</div>
					<div class="row" id="relclone2">
						<label id="l_rel" for="related">Related: <span
							style='float: right; margin-right: 10px;'><a title='A term that you feel is closely allied to this one.'><img
									src="IMG/red_help.png"
									style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
						</label> <input type="text" name="related" id="related" /> <span
							class="ui-icon ui-icon-circle-plus"
							style="float: left; margin-right: 0.3em;"
							onclick="  $('#relclone2').clone().insertAfter('#relclone2');"></span>
					</div>
					<c:forTokens items="${_trait_display_}" delims="," var="my_display">
						<c:if test="${my_display eq 'unit'}">
							<div class="row">
								<label id="l_unit" for="unit">Unit: <span
									style='float: right; margin-right: 10px;'><a
										title='Units should comply with the International System of Units.'><img
											src="IMG/red_help.png"
											style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
								</label> <input type="text" name="unit" id="unit" />
							</div>
						</c:if>
						<c:if test="${my_display eq 'realName'}">
							<div class="row">
								<label id="l_real_name" for="realName">Real Name: <span
									style='float: right; margin-right: 10px;'><a title=''><img
											src="IMG/red_help.png"
											style="width: 10px; height: 10px; margin-bottom: 3px;" /></a></span>&nbsp;
								</label> <input type="text" name="realNameAdd" id="nameAdd" />
							</div>
						</c:if>
					</c:forTokens>
					<div style="clear: both"></div>
					<input type="submit" id="" value="Submit" class="button"/>
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
	<input id="owlauto" type="text" size="40"
		style="display: block; width: 99%; margin: 5px 0px;"
		title="enter a search term" />
	<div class="info" style="color: rgb(195, 195, 195);">Enter Search
		Terms here</div>
	<hr>
</div>
