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

<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="Download" >
        <meta http-equiv="refresh" content="5;url=${fn:escapeXml(requestScope.action)}?operation=download">
        <!--        <script type="text/javascript">
                    function startDownload() {
                        document.getElementById("download").src = "${requestScope.action}?operation=download";
                    }
                    setTimeout(startDownload, 5 * 1000);
                </script>-->
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <slf:fieldSet>
                <h2>Il download partirà tra 5 secondi. In caso di mancato funzionamento,
                    utilizza questo link per scaricare il <a href="${fn:escapeXml(requestScope.action)}?operation=download">file</a>
                </h2>
                <div class="pulsantiera">
                    <div class="containerLeft w25">
                        <input class=" pulsante " type="submit" value="Indietro" name="operation__elencoOnClick">
                    </div>
                </div>

                <!--<iframe id="download" width="1" height="1" style="display:none"></iframe>--> 
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>
