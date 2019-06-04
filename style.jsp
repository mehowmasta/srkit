<%@ page language="java" import="sr.data.UserRec,sr.web.SessionKeys,ir.util.Coerce" 
	autoFlush="true" contentType="text/css;"
%><% 
	boolean isMobile = Coerce.toBool(request.getParameter("p"));
    boolean isSysAdmin = Coerce.toBool(request.getParameter("a"));
	String b1 = "#" + Coerce.toString(request.getParameter("B1"));
	String b2 = "#" + Coerce.toString(request.getParameter("B2"));
	String b3 = "#" + Coerce.toString(request.getParameter("B3"));
	String b4 = "#" + Coerce.toString(request.getParameter("B4"));
	String f1 = "#" + Coerce.toString(request.getParameter("F1"));
	String f2 = "#" + Coerce.toString(request.getParameter("F2"));
	String f3 = "#" + Coerce.toString(request.getParameter("F3"));
	String f4 = "#" + Coerce.toString(request.getParameter("F4"));
	String t1 = "#" + Coerce.toString(request.getParameter("T1"));
	String t2 = "#" + Coerce.toString(request.getParameter("T2"));
	String t3 = "#" + Coerce.toString(request.getParameter("T3"));
	String t4 = "#" + Coerce.toString(request.getParameter("T4"));
	String t5 = "#" + Coerce.toString(request.getParameter("T5"));
	String s1 = "#" + Coerce.toString(request.getParameter("S1"));
	String s2 = "#" + Coerce.toString(request.getParameter("S2"));
	String s3 = "#" + Coerce.toString(request.getParameter("S3"));
%>
<%@include  file="style.css"%>