<%@include  file="dochdr.jspf"%>
<script src="positivequalitieslist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.qualities = <%=_bean.get("Qualities") %>;
</script>
<style>
#qualitiesDiv .subtitle {
	border-bottom:0 none;
	text-align:right;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container' id='qualitiesDiv'>	
	<div class='divider shadow'>Positive Qualities</div>
	<div class='flex' style='flex-wrap:wrap;' id='qualityList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
