<%@include file="dochdr.jspf"%>
<script src="armorlist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.armor = <%=_bean.get("Armor")%>;
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

<div class='container' id='armorDiv'>
	<div id='armorList'></div>
</div>
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>