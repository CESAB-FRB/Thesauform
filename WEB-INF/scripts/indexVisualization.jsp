<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<content tag="local_script">
	<link rel="stylesheet" type="text/css" href="CSS/listnav.css"/>
	<script src="JS/jquery-1.9.0.js"></script>
</content>
				
<div id="down">
	<ul id="myList" class="index" style="list-style-type: none;">
		<c:forEach items="${my_trait.sonsList}" var="son" >
			<li>
				<a href="annotationInfo?viz=1&&trait=<c:out value='${son.name}'/>">
					<c:out value="${fn:replace(son.name, '_', ' ')}"/>
				</a>
			</li>
		</c:forEach>       
	</ul>	
</div>
<script src="JS/jquery-listnav.min.js"></script>
<script>
 	 $("#myList").listnav();
</script>
