<%@include file="dochdr.jspf"%>
<script src="mentorspiritlist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.spirits =
<%=_bean.get("Spirits")%>
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
#spiritsDiv .subtitle{
	font-size:1.2rem;
}
</style>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>
	<div class='container' id='spiritsDiv'>
		<div class='flex' id='spiritList'></div>
	</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>