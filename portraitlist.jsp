<%@include  file="dochdr.jspf"%>
<script src="portraitlist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.portraits = new KeyedArray("Row",<%=_bean.get("Portraits")%>);
</script>
<style>
.childBtnContainer{
	position:absolute;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container'>
	<div id='portraitList'class='flex' style='overflow-x:auto;justify-content:flex-start;max-height:calc(99vh - 10rem);'>
	</div>
	<div class='container flex'>
		<%=_bean.get("AddPortrait") %>
		<div class='childBtnContainer flex' id='btnAddPortraitSelector' style='align-items:center;'>
			<div class='x' style='top:-2rem;' onclick='view.toggleUploadButton()'>X</div>
			<div class='fileWrap childBtn'>
				<input id='mockPortraitFile' type='text' class='file' onfocus='this.blur()' onclick='ir.get("portraitFile").click()'><label class='inputLabel'>Portrait</label>
				<input id='portraitFile' name='mapFile' type='file'  accept='image/*' size='10' onchange='view.setName(this)' style='display:none;'>
			</div>
			<div class='inputWrap childBtn'>
				<input type='text' value='' id='portraitName' name='portraitName'><label class='inputLabel'>Name</label>
			</div>
			<button type='submit' name='portraitSubmit' class='childBtn' style='max-height:3.75rem'>Upload</button>
		</div>	
	</div>	
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
