<%@include file="dochdr.jspf"%>
<script src="medicallist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.gear = <%=_bean.get("Gear")%>;
</script>
<style>
.feat{
	padding-left:1rem;
}
.table{
	width:100%;
}
.table.show {
    display: inline-table;
}
.table td.hide{
	display:none;
}
.tableWrap {
	min-width: calc(100% - 1rem);
}
.subtitle{
	text-align:right;
	border:0 none;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>

<div class='container' id='gearDiv'>
	<div id='gearList'></div>
</div>
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>