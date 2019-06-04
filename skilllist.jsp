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
	<div class='tableWrap'>
			<div class='tableTitle'>Perception Test Modifiers</div>
			<table class='table' id='perceptionModTable'>
				<thead>
					<tr>
						<td class='tdl'>Situation</td>
						<td class='tdc'>Dice Pool Modifier</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class='tdl'>Perceiver is distracted</td>
						<td class='tdc'>-2</td>
					</tr>
					<tr>
						<td class='tdl'>Perceiver is specifically looking/listening for it</td>
						<td class='tdc'>+3</td>
					</tr>
					<tr>
						<td class='tdl'>Object/sound not in immediate vicinity</td>
						<td class='tdc'>-2</td>
					</tr>
					<tr>
						<td class='tdl'>Object/sound far away</td>
						<td class='tdc'>-3</td>
					</tr>
					<tr>
						<td class='tdl'>Object/sound stands out in some way</td>
						<td class='tdc'>+2</td>
					</tr>
					<tr>
						<td class='tdl'>Interfering sight/odor/sound</td>
						<td class='tdc'>-2</td>
					</tr>
					<tr>
						<td class='tdl'>Perceiver has active enchancements</td>
						<td class='tdc'>+ (Rating)</td>
					</tr>
					<tr>
						<td class='tdl'>Visibility and Light</td>
						<td class='tdc'>Environmental Factors, p. 173</td>
					</tr>
				</tbody>
			</table>
		</div>



	
</div>
<div class='container' id='skillsDiv'>	
	<div id='skillList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
