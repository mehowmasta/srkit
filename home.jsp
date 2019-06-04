<%@include  file="dochdr.jspf"%>
<script src="home.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container flex' style='padding-top:1rem;'>
    <%=_bean.get("Buttons") %>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
