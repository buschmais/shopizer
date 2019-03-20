<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/shopizer-tags.tld" prefix="sm" %> 

<%@ page session="false" %>

<script>

function getContextPath() {
   return "http://${pageContext.request.getHeader('X-Forwarded-Host')}";
}

function getItemLabel(quantity) {
	var labelItem = '<s:message code="label.generic.item" text="item" />';
	if (quantity > 1) {
		labelItem = '<s:message code="label.generic.items" text="items" />';
	}
	return labelItem;
}

//assign rating on a list of products
function setProductRating(productList) {
	jQuery.each( productList, function( i, val ) {
		var pId = '#productRating_' + val.id; 
	    $(pId).raty({ 
			readOnly: true,
			half: true,
			path : '<c:url value="/resources/img/stars/"/>',
			score: val.ratingCount
		});
	 });
}

</script>