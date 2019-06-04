<%
	String url = "index.jsp";
	if (request.getSession(false) != null)
		url = (String) request.getSession(false).getAttribute("RedirUrl");
    response.sendRedirect(url == null || url.trim().equals("") ? "index.jsp" : url); 
 %>