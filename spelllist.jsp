<%@include file="dochdr.jspf"%>
<script src="spelllist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.spells =
<%=_bean.get("Spells")%>
	;
	model.sortType =
<%=_bean.get("SortType")%>
	;
</script>
<style>
.properties {
	width: 98%;
}
.properties  td {
	width: 33%;
	white-space: nowrap;
	padding-right: 1rem;
}
</style>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>
	<div id='infoPopup' class='popup info'>
		<%@include file="tables/detectionSpellResults.jspf"%>		
		<%@include file="tables/mindProbe.jspf"%>
	</div>
	<div class='container' id='spellsDiv'>
		<div id='spellList'></div>
	</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>