<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<content tag="local_script">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/annotation.css"/>

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
							viz : "1", 
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
						"data": "<c:forTokens items="${_term_root_}" delims="," var="root"><li class='jstree-closed' id='${root}'><a href='annotationInfo?viz=1&&trait=<c:out value='${root}'/>' onclick='getInfo(\"<c:out value="${_term_uri_}"/>#${root}\")'><b>${root}</b></a></li></c:forTokens>",
						"ajax" : {
							"url" : "annotationArbre",
							"data" : function (n) { 
									return {
										trait : n.attr ? n.attr("id") : "${_super_root_}",
										viz : "1" 
									};
								}
						},
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
								return { 
									trait : n,
									viz : "1"
								};
							}
						}
					},
					"plugins" : [ "themes", "html_data", "search", "sort" ]
				})
			});
		});

		function arbre(id) {
			var id = id;
			var html = $.ajax({ url: "annotationArbre?trait="+id+"&viz=1" , async: false }).responseText; 
			$('#'+id).append(html);
			$('#'+id).removeAttr('onClick');
			return html;
		}

		function getInfo(item) {

		}
	</script>
</content>

<div id="content" style="width: 900px;">
	<div id="up" >
	</div>
	<div id="down">
		<h4>Browse the Hierarchy</h4>
		<div id="" class="ui-widget-content" style="text-align: left;">
			<h4 class="ui-dialog-titlebar ui-state-default">Treeview</h4>
			<div id="jtree" class="jstree-classic">
			</div>
		</div>
		<input id="owlauto" type="text" size="40" style="display:block;width:99%;margin:5px 0px;" title="enter a search term"/>
		<div class="info" style="color: rgb(195, 195 ,195);">Enter Search Terms here</div>
		<hr>
	</div>
</div>