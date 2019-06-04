<%@include file="dochdr.jspf"%>
<script src="critterpowerlist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.critterPowers =
<%=_bean.get("Powers")%>
	;
	model.sortType =
<%=_bean.get("SortType")%>
	;
</script>
<style>
.properties {
	width: 98%;
}

.properties  td {
	width: 33%;
	white-space: nowrap;
	padding-right: 1rem;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>
<div id='infoPopup' class='popup info'>		
	<div class='tableWrap'>
		<div class='tableTitle'>Search Modifiers Table</div>
		<table class='table' id='searchModTable'>
			<thead>
				<tr>
					<td class='tdl'>Situation</td><td class='tdc'>Threshold Modifiers</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Target is more than a kilometer away</td>
					<td class='tdc'>+kilometers</td>
				</tr>
				<tr>
					<td class='tdl'>Target is a nonliving object or place</td>
					<td class='tdc'>+5</td>
				</tr>
			</tbody>
		</table>
		<table class='table' style='width:100%;'>
			<thead>
				<tr>
					<td class='tdl'>Situation</td><td class='tdc'>Dice Pool Modifiers</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Target hidden by Concealment power</td>
					<td class='tdc'>-concealer’s Magic</td>
				</tr>
				<tr>
					<td class='tdl'>Target hidden behind mana barrier</td>
					<td class='tdc'>-barrier Force</td>
				</tr>				
			</tbody>
		</table>
	</div>
</div>
	<div class='container' id='critterPowersDiv'>
		<div id='critterPowerList'></div>
	</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>