<%@include file="dochdr.jspf"%>
<script src="vehiclelist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.vehicles = <%=_bean.get("Vehicles")%>;
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
#vehicleList .section{
	max-width:40rem;
}
#vehicleList .subtitle{
	text-align:right;
	border:0 none;
}
#vehicleModTable.show{
	display:inline-table;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>
<div class='popup info' id='infoPopup'>	
	<%@include file="tables/vehicleMod.jspf"%>
</div>
<div class='container' id='vehiclesDiv'>
	<div id='vehicleList'></div>
</div>
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>