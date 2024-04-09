<%-- 
    Document   : index
    Created on : 12-set-2012, 16.59.23
    Author     : Quaranta_M
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page session="false" %>
<%
    String redirectURL = response.encodeRedirectURL("Login.html");
    response.setStatus(303);
    response.addHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "no-cache");    
    response.addHeader("Cache-Control", "no-store");
    response.addHeader("Cache-Control", "must-revalidate");
    response.setHeader("Location", redirectURL);
%>