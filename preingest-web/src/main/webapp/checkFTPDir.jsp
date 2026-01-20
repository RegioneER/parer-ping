<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

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
