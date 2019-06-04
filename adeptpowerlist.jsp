<%@include file="dochdr.jspf"%>
<script src="adeptpowerlist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.adeptPowers =
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
#improvedReflexesTable.table.show,  #threeDMemoryTable.table.show{
display: inline-table;
width: 100%;
}

</style>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>	
	<div id='infoPopup' class='popup info'>		
	<div class='tableWrap'>
		<div class='tableTitle'>Improved Reflexes Table</div>
		<table class='table' id='improvedReflexesTable'>
			<thead>
				<tr>
					<td class='tdl'>Level</td><td class='tdc'>PP Cost</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Level 1</td>
					<td class='tdc'>1.5</td>
				</tr>
				<tr>
					<td class='tdl'>Level 2</td>
					<td class='tdc'>2.5</td>
				</tr>
				<tr>
					<td class='tdl'>Level 3</td>
					<td class='tdc'>3.5</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class='tableWrap'>
		<div class='tableTitle'>LINGUISTICS TABLE</div>
		<table class='table' id='langTable'>
			<thead>
				<tr>
					<td class='tdl'>Language</td><td class='tdc'>Threshold</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Common (English, Japanese, Spanish)</td>
					<td class='tdc'>1</td>
				</tr>
				<tr>
					<td class='tdl'>Uncommon (Latin, Or`zet, Sperethiel)</td>
					<td class='tdc'>2</td>
				</tr>
				<tr>
					<td class='tdl'>Obscure (Aramaic, Lapp, Berber)</td>
					<td class='tdc'>3</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class='tableWrap'>
		<div class='tableTitle'>MOTION SENSE TABLE</div>
		<table class='table' id='motionSenseTable'>
			<thead>
				<tr>
					<td class='tdl'>Moving this is</td><td class='tdc'>Threshold</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Smaller than dog/cat</td>
					<td class='tdc'>3</td>
				</tr>
				<tr>
					<td class='tdl'>Smaller than average metahuman (dwarf)</td>
					<td class='tdc'>2</td>
				</tr>
				<tr>
					<td class='tdl'>Average metahuman (human, ork, elf)</td>
					<td class='tdc'>1</td>
				</tr>
				<tr>
					<td class='tdl'>Larger than average (troll)</td>
					<td class='tdc'>Automatic success</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class='tableWrap'>
		<div class='tableTitle'>3D Memory TABLE</div>
		<table class='table' id='threeDMemoryTable'>
			<thead>
				<tr>
					<td class='tdc'>Time passed</td><td class='tdc'>Threshold</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdc'>24 hour</td>
					<td class='tdc'>1</td>
				</tr>
				<tr>
					<td class='tdc'>1 week</td>
					<td class='tdc'>2</td>
				</tr>
				<tr>
					<td class='tdc'>1 month</td>
					<td class='tdc'>3</td>
				</tr>
				<tr>
					<td class='tdc'>1 year</td>
					<td class='tdc'>4</td>
				</tr>
				<tr>
					<td class='tdc'>Over a year</td>
					<td class='tdc'>5</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
	<div class='container' id='adeptPowersDiv'>
		<div id='adeptPowerList'></div>
	</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>