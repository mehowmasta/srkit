<%@include  file="dochdr.jspf"%>
<script src="qualities.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container flex'>
	<button type='button' onclick='sr5.go("positivequalitieslist.jsp")'>Positive Qualities List</button>
	<button type='button' onclick='sr5.go("negativequalitieslist.jsp")'>Negative Qualities List</button>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
