<%@include  file="dochdr.jspf"%>
<script src="cyberwarelist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.cyberware = <%=_bean.get("Cyberware") %>;
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
	width:28rem;
}
.subtitle{
	text-align:right;
	border:0 none;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='popup info' id='infoPopup'>	
	<%@include  file="tables/wareGrades.jspf"%>
	<%@include  file="tables/boneLacing.jspf"%>	
</div>
<div class='container' id='cyberwareDiv'>	
	<div id='cyberwareList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
