<%@include  file="dochdr.jspf"%>
<script src="metatype.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.races = <%=_bean.get("Races") %>;
</script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container' id='racesDiv'>	
	<div class='tableWrap' id='metaTypeWrap' style='display:none;'>
		<div class='tableTitle'>Metatype Attribute Table</div>
		<table class='table' id='metaTypeTable'>		
		</table>
	</div>
	<div class='tableWrap' id='metaSapientWrap' style='display:none;'>
		<div class='tableTitle'>MetaSapient Attribute Table</div>
		<table class='table' id='metaSapientTable'>		
		</table>
	</div>
	<div class='tableWrap' id='metaSapient2Wrap' style='display:none;'>
		<div class='tableTitle'>MetaSapient Attribute Table</div>
		<table class='table' id='metaSapient2Table'>		
		</table>
	</div>
	<div class='tableWrap' id='shapeShifterWrap' style='display:none;'>
		<div class='tableTitle'>Shapeshifter Attribute Table</div>
		<table class='table' id='shapeShifterTable'>		
		</table>
	</div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
