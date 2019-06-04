<%@include file="dochdr.jspf"%>
<script src="programs.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
model.programs = <%=_bean.get("Programs") %>;
</script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm()%>
<div class='container' id='programsDiv'>	
	<div id='programList'></div>
</div>
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>