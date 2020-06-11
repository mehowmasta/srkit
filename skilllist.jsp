<%@include  file="dochdr.jspf"%>
<script src="skilllist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.skills = <%=_bean.get("Skills") %>;
model.sortType = <%=_bean.get("SortType") %>;
</script>
<style>
.properties {
	width:98%;
}
.properties  td {
	width:50%;
	white-space:nowrap;
	padding-right:1rem;
}

</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div id='infoPopup' class='popup info'>		
	<%@include  file="tables/perceptionMod.jspf"%>
</div>
<div class='container' id='skillsDiv'>	
	<div id='skillList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
