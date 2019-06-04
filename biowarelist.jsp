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
	<div class='pWrap'>
		<div class='pTitle'>Basic Bioware</div>
		<p id='basicBiowareP'>&emsp;Bioware is subtler, more holistic, and less invasive than
		cyberware, at the cost of being substantially pricier. Instead
		of replacing body parts with machines, bioware
		augments the body`s own functions and integrates transplanted
		organs that function as natural features. The
		application of biotechnology is a tricky business, as the
		fine balance of homeostasis between the body`s organic
		systems must be maintained. In the last decade, bionics
		and bio-engineering techniques have taken bioware from
		cutting edge to commonplace. Bioware is more expensive
		monetarily, costs less Essence, and is much harder to
		spot. Also, and we`d like to think this goes without saying,
		bioware has no wireless capability at all.</p>
	</div>
	<div class='pWrap'>
		<div class='pTitle'>Cultured Bioware</div>
		<p id='culturedBiowareP'>&emsp;Cultured bioware must be tailor-made for the body in
		which it will eventually find a home. This means it is
		more expensive and takes longer to acquire than the
		off-the-shelf kind.</p>
	</div>
</div>
<div class='container' id='biowareDiv'>	
	<div id='biowareList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
