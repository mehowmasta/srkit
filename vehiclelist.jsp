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
	<div class='tableWrap'>
		<div class='tableTitle'>Vehicle Modifications</div>
		<table class='table' id='vehicleModTable'>
			<thead>
				<tr>
					<td class='tdl'>Modification</td><td class='tdc'>Avail</td><td class='tdc'>Cost</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Rigger interface</td>
					<td class='tdc'>4</td>
					<td class='tdc'>1,000¥</td>
				</tr>
				<tr>
					<td class='tdl'>Standard weapon mount</td>
					<td class='tdc'>8F</td>
					<td class='tdc'>2,500¥</td>
				</tr>
				<tr>
					<td class='tdl'>Heavy weapon mount</td>
					<td class='tdc'>14F</td>
					<td class='tdc'>5,000¥</td>
				</tr>
				<tr>
					<td class='tdl'>Manual operation</td>
					<td class='tdc'>+1</td>
					<td class='tdc'>+500¥</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<div class='container' id='vehiclesDiv'>
	<div id='vehicleList'></div>
</div>
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>