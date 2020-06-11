<%@include  file="dochdr.jspf"%>
<script src="biowarelist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.bioware = <%=_bean.get("Bioware") %>;
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
<div id='infoPopup' class='popup info'>	
	<%@include  file="tables/wareGrades.jspf"%>
	<%@include  file="tables/basicBioware.jspf"%>
	<%@include  file="tables/culturedBioware.jspf"%>
	
	
</div>
<div class='container' id='biowareDiv'>	
	<div id='biowareList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
