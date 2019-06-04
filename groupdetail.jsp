<%@include  file="dochdr.jspf"%>
<script src="groupdetail.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.groupCharacters = new KeyedArray("GroupCharacterRow", <%= _bean.get("GroupCharacters") %> );
model.groupRow = <%=_bean.get("GroupRow")%>;
model.types = new KeyedArray("Name",<%=_bean.get("Types")%>);
model.shareKey = '<%=_bean.get("ShareKey")%>';
</script>
<style>
.pageSubtitle{
	margin-top:2rem;
}
#charactersDiv .subtitle{
	font-size:1.2rem;
}
#charactersDiv .section{
	width: 100%;
	max-width: 100%;
	margin-left:2.3rem;
}
#charactersDiv .thumb {
    margin-right: 0.5rem;
}
#charactersWrap{
	max-width:100%;
}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<%=_bean.get("ctlRow") %>
<%=_bean.get("ctlCharacters") %>
<div class='pageSubtitle'><span>Team</span><span class='subtitle' style='color:white;border:0 none;right:0;position:absolute;padding-right:1rem;' id='groupKey'></span></div>
<div class='container flex' style='align-items:center;'>	
	<%=_bean.get("ctlName") %>
	<%=_bean.get("ctlPrivate") %>
	<%=_bean.get("ctlInactive") %>
	
</div>
<div id='charactersWrap' class='show' style='display:none;'>
	<div class='pageSubtitle'><span>Characters</span></div>
	<div class='container' id='charactersDiv'>
		<div class='flex' id='characterList'></div>
	</div>
	<div class='flex' style='justify-content:center;padding:1rem;'>
		<button type='button' class='hover' data-hover='Runners'  onclick='return view.addCharacters()'>Runners</button>
		<button type='button' class='hover' data-hover='Select from SIN Registry'  onclick='return view.addRegistry()'>Registry</button>
	</div>
	
</div>
<div class='container flex footer'>
	<%=_bean.get("Buttons") %>
</div>
<%=_bean.endForm() %>
<%@include  file="pickcharacterpop.jspf"%>
<%@include  file="picknpcpop.jspf"%>
<%@include  file="docftr.jspf"%>
<script>
 pickCharacterPop.groupList = model.groupCharacters;
</script>