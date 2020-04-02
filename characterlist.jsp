<%@include file="dochdr.jspf"%>
<script src="characterlist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.characters =<%=_bean.get("Characters")%>;
	model.metatypes = new KeyedArray("Row",<%=_bean.get("Metatypes")%>);
	model.sex = new KeyedArray("Row",<%=_bean.get("Sex")%>);
	model.types = new KeyedArray("Name",<%=_bean.get("Types")%>);
</script>
<style>

#listWrap {
	max-height: calc(100vh - 11rem);
	overflow-y: auto;
}
#charactersDiv .subtitle{
	font-size:1.2rem;
}
#charactersDiv .section{
	width: 100%;
	max-width: 100%;
	margin-left:2.3rem;
}
#charactersDiv .section > *{
	pointer-events:none;
}
#charactersDiv .tableExtra td{
 	padding:0.1rem 0.4rem;
 	white-space:nowrap;
}
#charactersDiv .tableExtra{
    display: block;
    overflow-x: auto;
}
#charactersDiv .thumb{
    margin-right:0.2rem;
}
#transferDiv .section{
	width:100%;
	max-width: 100%;
	margin-left:2.3rem;
}
</style>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>
	<div id='listWrap'>
		<div class='container' id='transferDiv' style='display:none;'>	
				<div class="pageSubtitle">Transfer Request</div>
			<div class='flex' id='transferList'></div>
		</div>
		<div class='container' id='charactersDiv'>
				<div id='characterListTitle' style='display:none;' class="pageSubtitle"></div>
			<div class='flex' id='characterList'></div>
		</div>
	</div>
	
<div class='container flex'>
	<%=_bean.get("Buttons") %>
	<div class='childBtnContainer' id='btnAddSelector'></div>	
	<div class='childBtnContainer flex' id='btnImportSelector' style='align-items:center;'>
		<div class='x' style='top:-2rem;' onclick='view.toggleImportButton()'>X</div>
		<div class='fileWrap childBtn'>
			<input id='mockImportFile' type='text' class='file' onfocus='this.blur()' onclick='ir.get("importFile").click()'><label class='inputLabel'>File</label>
			<input id='importFile' name='importFile' type='file'  accept='.json' onchange='view.setImportName(this)' maxlength='80000000'  size='10' style='display:none;'>
		</div>
		<button type='submit' id='submitImport' name='submitImport' class='childBtn' style='max-height:3.75rem'>Upload</button>
	</div>	
	<label id='characterCount' style='position:absolute;right:0.75rem'></label>
</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>