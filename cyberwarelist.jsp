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
	<div class='tableWrap'>
		<div class='tableTitle'>Ware Grades</div>
		<table class='table' id='wareGradesTable'>
			<thead>
				<tr>
					<td class='tdl'>Grades</td><td class='tdc'>Ess Cost Multiplier</td><td class='tdc'>Avail Mod</td><td class='tdc'>Cost Multiplier</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Standard</td>
					<td class='tdc'>*1.0</td>
					<td class='tdc'>-</td>
					<td class='tdc'>*1</td>
				</tr>
				<tr>
					<td class='tdl'>Alphaware</td>
					<td class='tdc'>*0.8</td>
					<td class='tdc'>+2</td>
					<td class='tdc'>*1.2</td>
				</tr>
				<tr>
					<td class='tdl'>Betaware</td>
					<td class='tdc'>*0.7</td>
					<td class='tdc'>+4</td>
					<td class='tdc'>*1.5</td>
				</tr>
				<tr>
					<td class='tdl'>Deltaware</td>
					<td class='tdc'>*0.5</td>
					<td class='tdc'>+8</td>
					<td class='tdc'>*2.5</td>
				</tr>
				<tr>
					<td class='tdl'>Used</td>
					<td class='tdc'>*1.25</td>
					<td class='tdc'>-4</td>
					<td class='tdc'>*0.75</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class='tableWrap'>
		<div class='tableTitle'>Bone Lacing</div>
		<table class='table' id='boneLacingTable'>
			<thead>
				<tr>
					<td class='tdl'>Material</td><td class='tdc'>Body Boost</td><td class='tdc'>Armor</td><td class='tdc'>Unarmed Damage</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class='tdl'>Plastic</td>
					<td class='tdc'>+1</td>
					<td class='tdc'>+1</td>
					<td class='tdc'>(STR + 1)P</td>
				</tr>
				<tr>
					<td class='tdl'>Aluminum</td>
					<td class='tdc'>+2</td>
					<td class='tdc'>+2</td>
					<td class='tdc'>(STR + 2)P</td>
				</tr>
				<tr>
					<td class='tdl'>Titanium</td>
					<td class='tdc'>+3</td>
					<td class='tdc'>+3</td>
					<td class='tdc'>(STR + 3)P</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<div class='container' id='cyberwareDiv'>	
	<div id='cyberwareList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
