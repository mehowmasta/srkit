<%@include  file="dochdr.jspf"%>
<script src="negativequalitieslist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.qualities = <%=_bean.get("Qualities") %>;
</script>
<style>
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='popup info' id='infoPopup'>	
	<div class='popupContent'>
		<%@include  file="tables/allergy.jspf"%>
		<%@include  file="tables/asthma.jspf"%>
		<%@include  file="tables/dayJob.jspf"%>
		<%@include  file="tables/phobia.jspf"%>
		<%@include  file="tables/prejudice.jspf"%>
		<%@include  file="tables/scorched.jspf"%>
	</div>
</div>
<div class='container' id='qualitiesDiv'>	
	<div class='divider'>Negative Qualities</div>
	<div class='flex' style='flex-wrap:wrap;' id='qualityList'></div>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
