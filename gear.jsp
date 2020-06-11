<%@include file="dochdr.jspf"%>
<script src="gear.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>
	<div id='infoPopup' class='popup info'>
		<div class='container flex'>
			<%@include file="tables/concealMod.jspf"%>
			<%@include file="tables/gearGlossary.jspf"%>
		</div>
	</div>
	<div class='container flex' style='padding-top:1rem;'>	
		<%=_bean.get("Buttons") %>
	</div>

	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>