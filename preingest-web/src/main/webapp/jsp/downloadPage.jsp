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
