<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@include file="dochdr.jspf"%>
<script src="error.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<style>
body{
	background-size:80vh;
	background-position: 50% 50%;
	background-repeat: no-repeat;
	transistion:5s ease-out;
	-webkit-transition:5s ease-out;	
}
.divider{
	width:120%;transform:translateX(-10%);
	position:relative;
	top:calc(50vh - 8rem);
	text-align:center;
	padding:1rem;
}

</style>
<body onload='view.onload(event)'>
	<form action='error.jsp' method='POST' style='height:100%;'>	
	<button type="button" class="navButton back hover" data-hover="Go back a page" tabindex="1" onclick="window.history.back()">&#8678;</button>	
		<div class='divider shadow'>
			<i>404</i>: Wrong turn chummer!
		</div>
	</form>	
<%@include  file="docftr.jspf"%>
