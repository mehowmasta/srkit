<%@ page language="java" contentType="application/json" import="sr.web.page.*"%><%	
	response.setHeader("Cache-Control","no-cache,no-store"); //HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0
	response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
	AppBasePage _bean = (AppBasePage) sr.web.page.PageMapper.getInstance().getPage(pageContext);
    if (_bean == null)
		return; 
	%><%= _bean.get("JSON") 
	%>