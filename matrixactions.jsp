<%@include file="dochdr.jspf"%>
<script src="matrixactions.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
model.actions = <%=_bean.get("Actions") %>;
model.sortType = <%=_bean.get("SortType") %>;
</script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>
<div id='infoPopup' class='popup info'>	
		<%@include file="tables/userModes.jspf"%>
		<%@include file="tables/matrixPerception.jspf"%>
		<%@include file="tables/matrixSearch.jspf"%>
		<%@include file="tables/matrixSpotting.jspf"%>	
</div>

<div class='container' id='actionsDiv'>	
	<div id='actionList'></div>
</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>