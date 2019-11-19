<%@include  file="dochdr.jspf"%>
<script src="preferences.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<style>
	.title{
		padding:1rem;
		width:100%;
	}
</style>
<body onload='view.aaOnLoad()'>
<%=_bean.startForm() %>
<div class='container flex'>	
<div class='pageSubtitle'>Profile</div>
	<%=_bean.get("ctlEName") %>
	<%=_bean.get("ctlName") %>
	<%=_bean.get("ctlShortName") %>
	<%=_bean.get("ctlEMail") %>
</div>
<div class='container flex'>	
<div class='pageSubtitle'>Source Books</div>
	<%=_bean.get("SourceBooks") %>
</div>
<div class='container flex'>
<div class='pageSubtitle'>Default Character</div>
	<%=_bean.get("ctlCharacter") %>
	<%=_bean.get("ctlAutoRoll") %>
</div>
<div class='container flex'>
<div class='pageSubtitle'>Theme</div>
	<%=_bean.get("ctlTheme") %>
</div>
<div class='container flex'>
	<%=_bean.get("Buttons") %>
</div>
<%=_bean.endForm() %>
<%@include  file="docftr.jspf"%>
