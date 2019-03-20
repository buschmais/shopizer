<%
response.setCharacterEncoding("UTF-8");
response.setHeader("Cache-Control","no-cache");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", -1);
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/shopizer-tags.tld" prefix="sm" %> 
 
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


 <div id="shop" class="">
 
       <div style="margin-top: 0px;" class="banner center-block">

	       	<c:if test="${requestScope.CONTENT['banner']!=null}">
				<sm:pageContent contentCode="banner"/>
			</c:if>

       </div>


</div>
				
			    <c:if test="${requestScope.CONTENT['homeMessage']!=null}">
			    <sm:pageContent contentCode="homeMessage"/>
		        </c:if>

        							

<div class="main">
			<c:if test="${page!=null}">
				<div id="" class="container">
	          			    <c:out value="${page.description}" escapeXml="false"/>
				</div>
			</c:if>

			
			<br/>

	<%
		String host = request.getHeader("X-Forwarded-Host");
		String sessionId = request.getSession().getId();
		String url = "http://" + host + "/catalog/shop/product/featured?jsessionid=" + sessionId;
	%>

	<c:import url='<%=url%>'/>

	<div class="container">
				
			</div>
		
		
</div>
</div>