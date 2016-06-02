<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@tag description="test template" pageEncoding="UTF-8"%>

<%@attribute name="tab_title"%>
<%@attribute name="software_title"%>
<%@attribute name="explanation_title"%>
<%@attribute name="data_file"%>
<%@attribute name="person_file"%>
<%@attribute name="term_uri"%>
<%@attribute name="term_root"%>
<%@attribute name="contact_information"%>
<%@attribute name="contact_mail"%>
<%@attribute name="logos"%>
<%@attribute name="logo_header"%>

<%@attribute name="content" fragment="true"%>

<html>
<head>
<title>${tab_title}</title>

<link rel="stylesheet" type="text/css"
	href="CSS/start/jquery-ui-1.7.3.custom.css" />
<link rel="stylesheet" type="text/css" href="CSS/layout.css" />
<link rel="stylesheet" type="text/css" href="CSS/main.css" />
<link rel="stylesheet" type="text/css" href="CSS/facet.css" />

<script type="text/javascript" src="JS/jquery.js"></script>
<script type="text/javascript" src="JS/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" src="JS/authentificationAnnotation.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$('#propertyPanel').remove();
		$('#tab2').remove();
		$('#thingwidget').appendTo("body").hide();
		$('#individualPanel').remove();
		$('#tab3').remove();
		$('#sparqldlPanel').remove();
		$('#tab4').remove();
		$("#tabs").tabs();
	});
</script>
</head>

<body>
	<div id="head-container">
		<div id="header">
			<c:set var="my_logo_header_prop"
				value="${fn:split(logo_header, ';')}" />
			<c:set var="my_header_dimension"
				value="${fn:split(my_logo_header_prop[2], 'x')}" />
			<img style="float: left; display: inline;"
				width="${my_header_dimension[0]}" height="${my_header_dimension[1]}"
				border="0" src="IMG/${my_logo_header_prop[0]}"
				name="${my_logo_header_prop[1]}" />
			<h1
				style="float: left; display: inline; width: 800px; margin-left: 1.2em;">${software_title}</h1>
			<h3>${explanation_title}</h3>
		</div>
	</div>
	<div id="navigation-container">
		<div id="navigation">
			<ul id="menu">
				<li><a href="home">Home</a></li>
				<li><a href="visualizationFacet">Faceted search</a></li>
				<li><a href="visualizationHierarchy">Hierarchy search</a></li>
				<li><a href="index">Index</a></li>
				<li><a href="references">References</a></li>
				<li><a href="api">API</a></li>
				<li><a href="administration">Administration</a></li>
			</ul>
		</div>
	</div>
	<div id="content-container">
		<div id="content-container2">
			<div id="content-container3">


				<jsp:invoke fragment="content" />

			</div>
		</div>
	</div>

	<div id="footer-container">
		<div id="footer">
			<a href="mailto:${contact_mail}" style="color: white">Contact
				${contact_information}</a>
		</div>
	</div>
	<div id="logo" align="center">
		<c:forTokens items="${_logos_}" delims="," var="my_logo">
			<c:set var="my_logo_prop" value="${fn:split(my_logo, ';')}" />
			<c:set var="my_dimension" value="${fn:split(my_logo_prop[2], 'x')}" />
			<a href="${my_logo_prop[3]}"> <img width="${my_dimension[0]}"
				height="${my_dimension[1]}" border="0" src="IMG/${my_logo_prop[0]}"
				name="${my_logo_prop[1]}" />
			</a>
		</c:forTokens>
	</div>
</body>
</html>
