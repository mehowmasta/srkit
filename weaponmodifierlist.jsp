<%@include file="dochdr.jspf"%>
<script src="weaponmodifierlist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.weaponModifiers = <%=_bean.get("WeaponModifiers")%>;
	model.sortType = <%=_bean.get("SortType") %>;
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
.section{
	max-width:28rem;
}
.subtitle{
	text-align:right;
	border:0 none;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>

<div class='container' id='weaponModifiersDiv'>
	<div id='weaponModifierList'></div>
</div>
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>