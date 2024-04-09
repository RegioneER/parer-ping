<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Controlli cartella ftp</title>
        <style>
            table {
                border-color: black;
                border-width: 1px;
                background-color: white;
            }
        </style>
    </head>
    <body>
        <table border="1">
            <thead>
                <tr>
                    <th>Versatore</th>
                    <th>Directory di Input</th>
                    <th>Check</th>
                    <th>Directory di Output</th>
                    <th>Check</th>
                    <th>Error</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${not empty requestScope.tablerows}" >
                    ${fn:escapeXml(requestScope.tablerows)}
                </c:if>
            </tbody>
        </table>
    </body>
</html>
