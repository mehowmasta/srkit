<%@ page language="java" import="sr.web.*,sr.web.page.*"  autoFlush="true" contentType="text/html; charset=UTF-8"  %>
<%@include  file="dochdrstamp.jspf"%><%
	response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0
	response.setDateHeader ("Expires", -1); //prevents caching at the proxy server
	AppBasePage _bean = (AppBasePage) sr.web.page.PageMapper.getInstance().getPage(pageContext);
	if (_bean == null) 
    {
    	return;
	}
	%>
<!DOCTYPE html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
<meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
<meta HTTP-EQUIV="Expires" CONTENT="-1">
<meta name="google" content="notranslate">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<link rel="icon" href="favicon.ico">
<link href="style.jsp?p=<%=_bean.isMobile() %>&a=false&<%=_bean.getThemeParameters()%>" rel="stylesheet" charset="UTF-8" type="text/css" />
<link href='https://fonts.googleapis.com/css?family=Jura' rel='stylesheet'>
<link href='https://fonts.googleapis.com/css?family=Electrolize' rel='stylesheet'>
<link href='https://fonts.googleapis.com/css?family=Ceviche One' rel='stylesheet'>
<title><%=AppBasePage.PRODUCT_NAME%></title>
<script src="statusconsole.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script src="index.js?s=<%=stamp %>" TYPE="text/javascript"></script>
<script>view.errorText = "<%=_bean.get("ErrorText")%>";
var sr5 = {isMobile: <%= _bean.get("MobileDevice")%>};
</script>
</head>
<style>
body{
	background-image:url(images/logo.png);
	background-size:120vw;
	background-position: 50% 50%;
	background-repeat: no-repeat;
	transistion:5s ease-out;
	-webkit-transition:5s ease-out;
	
}
.container {
	display:flex;
	flex-direction:column;
	align-items:center;
	max-width:500px;
	position:relative;
	top:50vh;
	margin:0 auto;
	transistion:200ms ease-out;
	-webkit-transition:200ms ease-out;
}
#social img, #license img{
	transistion:200ms ease-in-out;
	-webkit-transition:200ms ease-in-out;
}
#social img:hover, #license img:hover{
	transform:scale(1.5);
	-webkit-transform:scale(1.5);
}
</style>
<body onload='view.onload(event)'>
<div style='height:100%;'>
	<form action='index.jsp' method='POST' style='height:100%;'>		
		<div id='container' class='container'>
			<div class='inputWrap'>
				<input type='text' id='_u' name='_u' size='15' tabindex='1' style='background-color:#444 !important;'>
				<label class='inputLabel'>userName</label>
			</div>
			<div class='inputWrap'>
				<input type='password' id='_p' name='_p' size='15' tabindex='1' style='background-color:#444 !important;'>
				<label class='inputLabel'>password</label>
			</div>
			<button type='submit' style='max-width:12em;margin:1rem 0;' tabindex='1'>CONNECT</button>
			<br>
			<button type='button' onclick='view.guest();' style='font-size:80%;max-width:8rem;margin:1rem 0;padding:0.5rem;min-width:5rem;' tabindex='1'>GUEST</button>
		</div>
	</form>
</div>
<div id='social' style='position:absolute;right:0;top:2rem;padding:0.25rem;transition:all 200ms;'>
	<a style='cursor:pointer;' href='https://www.patreon.com/srkit' ><img src="icons/patreon.svg" style='height:2.5rem;width:2.5rem;' class='hover' data-hover='Support development on Patreon'></a>
	<br>
	<br>
	<a style='cursor:pointer;' href='https://www.reddit.com/r/ShadowrunKit/' ><img src="icons/reddit.svg" style='height:2.5rem;width:2.5rem;' class='hover' data-hover='Follow development on Reddit: r/ShadowrunKit'></a>
	<br>
	<br>
	<a style='cursor:pointer;' href='https://github.com/mehowmasta/srkit' ><img src="icons/github.svg" style='height:2.5rem;width:2.5rem;' class='hover' data-hover='Contribute to development on GitHub'></a>
</div>
<div id='license' style='position:absolute;left:0;bottom:2rem;padding:0.25rem;transition:all 200ms;'>
	<a style='cursor:pointer;' href='license.jsp' ><img src="icons/gpl3.svg" style='height:2.5rem;width:2.5rem;' class='hover' data-hover='Free Software License'></a>
</div>
</script>
<%  
	if (_bean != null)
	{
		_bean.release();
		_bean = null;
	}
%>
<%@include  file="hoverpop.jspf"%>
</body>
</html>
