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
 




			<c:if test="${page!=null}">
			<div class="row-fluid">
          		    <div class="span12">
          			    <span id="homeText"><c:out value="${page.description}" escapeXml="false"/></span>
          		    </div>
			</div>
			</c:if>
			
			<br/>

	<c:import url="${pageContext.request.contextPath}/shop/product/featured"/>

			
		