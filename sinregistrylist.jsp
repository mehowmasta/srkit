<%@include file="dochdr.jspf"%>
<script src="sinregistrylist.js?s=<%=stamp%>" TYPE="text/javascript"></script>
<script>
	model.characters =<%=_bean.get("Characters")%>;
</script>
<style>
#charactersDiv {
	max-height: calc(100vh - 6rem);
	overflow-y: auto;
}
#charactersDiv .subtitle{
	font-size:1.2rem;
}
#charactersDiv .section{
	width: 100%;
	margin-left:2.3rem;
	max-width:initial;
}
#charactersDiv .section > *{
	pointer-events:none;
}
#charactersDiv .tableExtra td{
 	padding:0.1rem 0.4rem;
 	white-space:nowrap;
}
#charactersDiv .tableExtra{
    display: block;
    overflow-x: auto;
}
#charactersDiv .thumb{
    margin-right:0.2rem;
}
#characterList {
	overflow-x:hidden;
}
#characterList .long {
	display:none !important;
}
#characterList .divider {
	min-width: calc(100vw + 8rem);
	white-space: normal;
	padding-right: 6rem;
	padding-left: 4rem;
	-ms-transform: translateX(2rem);
	-webkit-transform: translateX(2rem);
	-moz-transform: translateX(2rem);
	-o-transform: translateX(2rem);
	transform: translateX(2rem);
}
</style>
<body onload='view.aaOnLoad()'>
	<%=_bean.startForm()%>
	<div class='container' id='charactersDiv'>
		<div class='flex' id='characterList'></div>
	</div>
	
<div class='container flex'>	
	<label id='characterCount' style='position:absolute;right:0.75rem'></label>
</div>
	<%=_bean.endForm() %>
	<%@include file="docftr.jspf"%>