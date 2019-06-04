<%@include file="dochdr.jspf"%>
<script src="cyberdecklist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.cyberdecks = <%=_bean.get("Cyberdecks")%>;
</script>
<style>
.table{
	width:100%;
}
.table.show {
    display: inline-table;
}
.tableWrap {
    min-width: calc(100% - 1rem);
}
#cyberdeckList .section{
	max-width:25rem;
	min-width:25rem;
}
#cyberdeckList .subtitle{
	text-align:right;
	border:0 none;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>	
<div class='container' id='cyberdecksDiv'>
	<div id='cyberdeckList' class='flex'></div>
</div>
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>