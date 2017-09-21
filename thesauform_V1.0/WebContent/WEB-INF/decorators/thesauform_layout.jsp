<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html>

<html>
	<head>
		<title>${_tab_title_}</title>
		<link rel="stylesheet" type="text/css"
			href="${pageContext.request.contextPath}/CSS/start/jquery-ui-1.7.3.custom.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/layout.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/main.css" />
		<link rel="icon" href="${pageContext.request.contextPath}/IMG/favicon.ico" />		
		<script type="text/javascript" src="${pageContext.request.contextPath}/JS/jquery.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/JS/jquery-ui-1.7.2.custom.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/JS/authentificationAnnotation.js"></script>

		<decorator:getProperty property="page.local_script"></decorator:getProperty>
	</head>
	
	<body>
		<div id="head-container">
			<div id="header">
				<c:set var="my_logo_header_prop"
					value="${fn:split(_logo_header_, ';')}" />
				<c:set var="my_header_dimension"
					value="${fn:split(my_logo_header_prop[2], 'x')}" />
				<img style="float: left; display: inline;"
					width="${my_header_dimension[0]}" height="${my_header_dimension[1]}"
					border="0" src="${pageContext.request.contextPath}/IMG/${my_logo_header_prop[0]}"
					name="${my_logo_header_prop[1]}" />
				<h1
					style="float: left; display: inline; width: 800px; margin-left: 1.2em;">${_software_title_}</h1>
				<h3 style="float: left; display: inline; width: 800px; margin-left: 1.2em;">${_explanation_title_}</h3>
			</div>
		</div>
		<div id="navigation-container">
			<div id="navigation">
				<c:set var="menu_hidden_display_list"
					value="${fn:split(_menu_hidden_display_, ',')}" />
				<c:forEach var="menu_hidden" items="${menu_hidden_display_list}">
					<c:set target="${_menu_hidden_display_map_}" property="${menu_hidden}" value="1"/> 
				</c:forEach>
				<ul id="menu">
					<li><a href="${pageContext.request.contextPath}/home">Home</a></li>
					<c:if test="${!_menu_hidden_display_map_.containsKey('facet')}">
						<c:if test="${not empty _facet_list_}">
							<li><a href="${pageContext.request.contextPath}/visualizationFacet">Faceted search</a></li>
						</c:if>
					</c:if>
					<c:if test="${not empty _public_data_file_}">
						<li><a href="${pageContext.request.contextPath}/visualizationHierarchy">Hierarchy search</a></li>
						<c:if test="${!_menu_hidden_display_map_.containsKey('index')}">
								<li><a href="${pageContext.request.contextPath}/index">Index</a></li>
						</c:if>
						<c:if test="${!_menu_hidden_display_map_.containsKey('reference')}">
							<li><a href="${pageContext.request.contextPath}/references">References</a></li>
						</c:if>
						<c:if test="${!_menu_hidden_display_map_.containsKey('api')}">
							<li><a href="${pageContext.request.contextPath}/api">API</a></li>
						</c:if>
					</c:if>
					<c:if test="${!_menu_hidden_display_map_.containsKey('annotation')}">
						<li><a href="${pageContext.request.contextPath}/annotationModification">Annotation</a></li>
					</c:if>
					<c:if test="${!_menu_hidden_display_map_.containsKey('vote')}">
						<li><a href="${pageContext.request.contextPath}/expert">Vote</a></li>
					</c:if>
					<li><a href="${pageContext.request.contextPath}/DOC/thesauform_manual.pdf" target="_blank">Manual</a></li>
					<li><a href="${pageContext.request.contextPath}/administration">Administration</a></li>
				</ul>
			</div>
		</div>
		<div id="content-container">
			<div id="content-container2">
				<div id="content-container3">
					<decorator:getProperty property="page.logout"></decorator:getProperty>
					<decorator:body />
				</div>
			</div>
		</div>
	
		<div id="footer-container">
			<div id="footer">
				<a href="mailto:${_contact_mail_}" style="color: white">Contact
					${_contact_information_}</a>
			</div>
		</div>
		<div id="logo" align="center">
			<c:forTokens items="${_logos_}" delims="," var="my_logo">
				<c:set var="my_logo_prop" value="${fn:split(my_logo, ';')}" />
				<c:set var="my_dimension" value="${fn:split(my_logo_prop[2], 'x')}" />
				<a href="${pageContext.request.contextPath}/${my_logo_prop[3]}"> <img width="${my_dimension[0]}"
					height="${my_dimension[1]}" border="0" src="${pageContext.request.contextPath}/IMG/${my_logo_prop[0]}"
					name="${my_logo_prop[1]}" />
				</a>
			</c:forTokens>
		</div>
	</body>
</html>
