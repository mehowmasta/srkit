<%@include  file="dochdr.jspf"%>
<script src="grouplist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.groups =<%=_bean.get("Groups")%>;
</script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container' id='groupsDiv'>
		<div class='flex' id='groupList'></div>
	</div>
<div class='container flex'>
	<%=_bean.get("Buttons") %>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
