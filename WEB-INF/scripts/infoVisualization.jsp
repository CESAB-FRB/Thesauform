<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<content tag="local_script">
	<script src="//code.jquery.com/jquery-1.10.2.js"></script>
	<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
	<script type="text/javascript" src="JS/jquery.jstree.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$(function () {
				$("#jtree").jstree({ 
					"html_data" : {
						"data" : "<ul> <li> <a href='annotationInfo?viz=1&trait=<c:out value='${my_trait.parent.name}'/>' > <c:out value='${my_trait.parent.name}'/> </a> <ul> <li> <a href='annotationInfo?viz=1&trait=<c:out value='${my_trait.name}'/>' > <c:out value='${my_trait.name}'/> </a> <c:choose> <c:when test='${empty my_trait.sonsList}'> <c:choose> <c:when test='${empty my_trait.categoriesList}'> </c:when><c:otherwise><ul><c:forEach items='${my_trait.categoriesList}' var='category'> <li> <a href='annotationInfo?viz=1&trait=<c:out value='${category.realName}'/>' > <c:out value='${category.realName}'/> </a> </li> </c:forEach></ul></c:otherwise> </c:choose> </c:when> <c:otherwise> <ul> <c:forEach items='${my_trait.sonsList}' var='son'> <li> <a href='annotationInfo?viz=1&trait=<c:out value='${son.name}'/>' > <c:out value='${son.name}'/> </a> </li> </c:forEach> </ul> </c:otherwise> </c:choose> </li> </ul> </li> </ul>"						},
					"themes" : {
						"theme" : "classic",
						"dots" : true,
						"icons" : false
					},
					"plugins" : [ "themes", "html_data", "sort" ]
				});
				$('#jtree').jstree('open_all');
			});
		});
	</script>
</content>
		
<div id="content" style="width: 900px;">
	<div id="up" >
	</div>
	<div id="down">
		<h2 class="row" style="">
		</h2>
	  			<div id="aside" style="width: 30%;">
		    <div id="jtree"></div>
		</div>
		<h3><c:out value="${fn:replace(my_trait.name, '_', ' ')}"/></h3>
		<c:choose>
			<c:when test="${empty my_trait.realName}">
			</c:when>
			<c:otherwise>
				<div id="toto">
					<b>Formal name:&nbsp;</b>	
					<c:out value="${my_trait.realName}"/>						
				</div>   
			</c:otherwise>
		</c:choose>	
		<c:choose>
			<c:when test="${empty my_trait.abbreviation}">
			</c:when>
			<c:otherwise>
				<div>
					<b>Abbreviation:&nbsp;</b>
					<c:out value="${my_trait.abbreviation}"/>
				</div>    
			</c:otherwise>
		</c:choose>	
		<div>
			<b>Unique identifier:&nbsp;</b>
		</div>
		<c:choose>
			<c:when test="${empty my_trait.synonymsList}">
			</c:when>
			<c:otherwise>
				<c:forEach items="${my_trait.synonymsList}" var="synonym">
					<div id="toto">
						<b>Synonym Term:&nbsp;</b>
						<c:out value="${synonym.realName}"/>							
					</div>
				</c:forEach>        
			</c:otherwise>
		</c:choose> 
		<c:choose>
			<c:when test="${empty my_trait.definition}">
			</c:when>
			<c:otherwise>
				<div>
					<b>Definition:&nbsp;</b>
					<c:out value="${my_trait.definition}"/>
				</div>      
			</c:otherwise>
		</c:choose>
		<c:forTokens items="${_trait_display_}" delims="," var="my_display">
			<c:if test="${my_display eq 'unit'}">
				<c:choose>
					<c:when test="${empty my_trait.unit}">
					</c:when>
					<c:otherwise>
						<div>
							<b>Formal Unit:&nbsp;</b>
							<c:out value="${my_trait.unit}"/>
						</div>   
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forTokens>
		<br>
		<c:choose>
			<c:when test="${empty my_trait.commentsList}">
			</c:when>
			<c:otherwise>
				<c:forEach items="${my_trait.commentsList}" var="comment">
					<div id="toto">
						<b>Comment:&nbsp;</b>
						<c:out value="${comment.value}"/>							
					</div>
				</c:forEach>        
			</c:otherwise>
		</c:choose> 
		<c:choose>
			<c:when test="${empty my_trait.reference}">
			</c:when>
			<c:otherwise>
				<div>
					<b>Bibliographic Reference:&nbsp;</b>
					<c:out value="${my_trait.reference}"/>
				</div>     
			</c:otherwise>
		</c:choose>
		
		<br>
		<br>
		<div>------------------------------------------------</div>		
		<br>
		<c:choose>
			<c:when test="${empty my_trait.relatedsList}">
			</c:when>
			<c:otherwise>
				<c:forEach items="${my_trait.relatedsList}" var="related">
					<div id="relclone">
						<b>Related to:&nbsp;</b>
						<a href="annotationInfo?viz=1&trait=<c:out value='${related.name}'/>" ><c:out value="${related.name}"/></a>
					</div>
				</c:forEach>        
			</c:otherwise>
		</c:choose>
	</div>
</div>
