<%@include file="dochdr.jspf"%>
<script src="dronelist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.drones = <%=_bean.get("Drones")%>;
	model.sortType = <%=_bean.get("SortType") %>;
</script>
<style>
.feat{
	padding-left:1rem;
}
.table{
	width:100%;
}
.table td.hide{
	display:none;
}
.tableWrap {
	min-width: calc(100% - 1rem);
}
#droneList .section{
	max-width:31rem;
}
#droneList .subtitle{
	text-align:right;
	border:0 none;
}
#droneList .table.show{
	display:inline-table;
}
@media (max-width: 640px) {
#droneList .table.show{
	display:block;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>

<div class='container' id='dronesDiv'>
	<div id='droneList'></div>
</div>
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>