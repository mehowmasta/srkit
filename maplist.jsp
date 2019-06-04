<%@include  file="dochdr.jspf"%>
<script src="maplist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.maps =<%=_bean.get("Maps")%>;
</script>
<style>
.childBtnContainer{
	position:absolute;
}
.color{
	width:2.2rem;
	height:2.2rem;
	margin:0.2rem;
	border:0.1rem solid black;
	align-items:center;
}
#mapsDiv .subtitle{
	font-size:1.2rem;
}
#mapsDiv .section{
	width: 100%;
	max-width: 100%;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container' id='mapsDiv'>
		<div class='flex' id='mapList'></div>
	</div>
<div class='container flex' style='padding:1rem;'>
	<%=_bean.get("Buttons") %>
	<div class='childBtnContainer flex' id='btnAddSelector' style='align-items:center;'>
		<div class='x' style='top:-2rem;' onclick='view.toggleUploadButton()'>X</div>
		<div class='fileWrap childBtn'>
			<input id='mockMapFile' type='text' class='file' onfocus='this.blur()' onclick='ir.get("mapFile").click()'><label class='inputLabel'>File</label>
			<input id='mapFile' name='mapFile' type='file'  accept='image/*' size='10' onchange='view.setName(this)' style='display:none;'>
		</div>
		<div class='inputWrap childBtn'>
			<input type='text' value='' id='mapName' name='mapName'><label class='inputLabel'>Name</label>
		</div>
		<button type='submit' class='childBtn' style='max-height:3.75rem'>Upload</button>
	</div>	
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
