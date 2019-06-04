<%@include file="dochdr.jspf"%>
<script src="spelllist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.spells =
<%=_bean.get("Spells")%>
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
			<div class='tableTitle'>DETECTION SPELL RESULTS</div>
			<table class='table' id='detectionSpellTable'>
				<thead>
					<tr>
						<td class='tdc'>Net Hits</td>
						<td class='tdl'>Results</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class='tdc'>1</td>
						<td class='tdl'>Only general knowledge, no details</td>
					</tr>
					<tr>
						<td class='tdl' colspan='2'><i  style='font-size:1rem;'>Detect Life example: A
								group of metahumans.</i></td>
					</tr>
					<tr>
						<td class='tdc'>2</td>
						<td class='tdl'>Major details only, no minor details</td>
					</tr>
					<tr>
						<td class='tdl' colspan='2'><i  style='font-size:1rem;'>Detect Life example: A
								dwarf, a troll, and an ork walk into a bar. Stop me if you heard
								this one.</i></td>
					</tr>
					<tr>
						<td class='tdc'>3</td>
						<td class='tdl'>Major and minor details, with some minor
							details obscured or missing</td>
					</tr>
					<tr>
						<td class='tdl' colspan='2'><i  style='font-size:1rem;'>Detect Life example: The
								three individuals listed above are all armed, and their weapons
								are out. The troll is leading.</i></td>
					</tr>
					<tr>
						<td class='tdc'>4</td>
						<td class='tdl'>Detailed information</td>
					</tr>
					<tr>
						<td class='tdl' colspan='2'><i  style='font-size:1rem;'>Detect Life example: The
								troll is your contact, Moira; you don`t believe you`ve ever met
								the other two.</i></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class='tableWrap'>
			<div class='tableTitle'>Mind Probe Table</div>
			<table class='table' id='mindProbeTable'>
				<thead>
					<tr>
						<td class='tdc'>Net Hits</td>
						<td class='tdl'>Results</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class='tdc'>1-2</td>
						<td class='tdl'>The subject can read the target`s surface
							thoughts only.</td>
					</tr>
					<tr>
						<td class='tdc'>3-4</td>
						<td class='tdl'>The subject can find out anything the target
							consciously knows and view the target`s recent memories (up to 72
							hours).</td>
					</tr>
					<tr>
						<td class='tdc'>5+</td>
						<td class='tdl'>The subject can probe the target`s
							subconscious, gaining information the target may not even be
							consciously aware of, like psychological quirks, deep fears, or
							hidden memories.</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<div class='container' id='spellsDiv'>
		<div id='spellList'></div>
	</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>