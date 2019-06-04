<%@include  file="dochdr.jspf"%>
<script src="grouplist.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.groups =<%=_bean.get("Groups")%>;
</script>
<style>
#groupsDiv .subtitle{
	font-size:1.2rem;
	border-bottom:0 none;
	color:white;
}
#groupsDiv .section{
	width: 100%;
	max-width: 100%;
}
#groupsDiv .thumb{
	margin-left:0.5rem;
}
#groupsDiv .thumbWrap{
	position:relative;
}
#groupsDiv .thumbLabel{
	position:absolute;left:0.1rem;bottom:0.1rem;
}
</style>
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
