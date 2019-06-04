<%@include  file="dochdr.jspf"%>
<script src="mapdetail.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.imageMap = <%= _bean.get("ImageMap")%>;
model.mapData = new KeyedArray("Row",<%= _bean.get("MapData")%>);
</script>
<style>
.pageSubtitle{
	margin-top:2rem;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<%=_bean.get("ctlRow") %>
<%=_bean.get("ctlData") %>
<div class='pageSubtitle'><span>Map</span></div>
<div class='container flex' style='align-items:center;'>	
	<%=_bean.get("ctlName") %>
	<div class='inputWrap'>
		<input id='ctlDivision' name='ctlDivision' type='number' min=4 max=60 oninput='view.updateDivision()' onfocus='this.select()' style='width:5rem;text-align:right;' value='<%=_bean.get("DivisionValue")%>'>
		<label class='inputLabel'>Division</label>
	</div>
	<div class='inputWrap'>
		<input id='ctlZoom' name='ctlZoom' type='number' min=0.2 max=3 step=0.1 oninput='view.updateZoom()' onfocus='this.select()' style='width:5rem;text-align:right;' value='<%=_bean.get("ZoomValue")%>'>
		<label class='inputLabel'>Zoom</label>
	</div>
	<%=_bean.get("ctlInactive") %>
</div>
<div class='container flex mapColorPalette' id='colorDiv'></div>
<div class='container flex mapContainer' id='mapDiv'></div>
<div class='container flex footer' >
	<%=_bean.get("Buttons") %>
</div>
<%=_bean.endForm() %>
<%@include  file="pickarmorpop.jspf" %>
<%@include  file="pickcharacterpop.jspf" %>
<%@include  file="pickcyberdeckpop.jspf"%>
<%@include  file="pickcyberwarepop.jspf"%>
<%@include  file="pickdronepop.jspf"%>
<%@include  file="pickgearpop.jspf"%>
<%@include  file="pickmapobjectpop.jspf"%>
<%@include  file="picknpcpop.jspf"%>
<%@include  file="pickvehiclepop.jspf"%>
<%@include  file="pickweaponpop.jspf"%>
<%@include  file="detailpop.jspf"%>
<%@include  file="detailmodpop.jspf"%>
<%@include  file="docftr.jspf"%>