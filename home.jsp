<%@include  file="dochdr.jspf"%>
<script src="home.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>
model.hasFriendRequest = <%= _bean.get("FriendRequest") %>;
model.hasTransferRequest = <%= _bean.get("TransferRequest") %>;
</script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container flex' style='padding-top:1rem;'>
    <%=_bean.get("Buttons") %>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
