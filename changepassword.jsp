<%@include file="dochdr.jspf"%>
<body onload='sr5.doneLoading()'>
<%= _bean.startForm() %>
<div class='container flex'>
<%= _bean.get("Old") %><div class='flexSpacer'></div>
<%= _bean.get("New") %><div class='flexSpacer'></div>
<%= _bean.get("Confirm") %>
</div>
<div class='container flex'>
	<button type='submit'>Update</button>
</div> 
<%=_bean.endForm() %>
<%@include file="docftr.jspf"%>