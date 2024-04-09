<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <c:if test="${sessionScope['duplicaVersDetail'] eq 'importa'}">
        <sl:head title="Importazione Versatore" />
    </c:if>
    <c:if test="${sessionScope['duplicaVersDetail'] eq 'duplica'}">
        <sl:head title="Duplicazione Versatore" />
    </c:if>
    <script type='text/javascript'>
        $(document).ready(function () {
            // Al caricamento della pagina, eseguo gestisciRifTemp() e inizializzo il change sul campo
            gestisciTipologia();
            initChangeEvents();
        });

        function initChangeEvents() {
            $('#Tipologia').change(function () {
                gestisciTipologia();
            });
        }
        ;

        function gestisciTipologia() {
            var tipologia = $('[name=Tipologia]');
            if (tipologia.val() === 'PRODUTTORE') {
                $('#EnteConvenzionatoSection').show();
                $('#FornitoreEsternoSection').hide();
                $('#DateEntiSection').show();
                $('#CorrispondenzaSacerSection').show();
            } else if (tipologia.val() === 'FORNITORE_ESTERNO') {
                $('#EnteConvenzionatoSection').hide();
                $('#FornitoreEsternoSection').show();
                $('#DateEntiSection').show();
                $('#CorrispondenzaSacerSection').hide();
            }else if (tipologia.val() === 'SOGGETTO_ATTUATORE') {
                $('#EnteConvenzionatoSection').hide();
                $('#FornitoreEsternoSection').show();
                $('#DateEntiSection').show();
                $('#CorrispondenzaSacerSection').hide();
            } else {
                $('#EnteConvenzionatoSection').hide();
                $('#FornitoreEsternoSection').hide();
                $('#DateEntiSection').hide();
            }
        }
        ;
    </script>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content multipartForm="${sessionScope['duplicaVersDetail'] eq 'importa'}">
            <slf:messageBox /> 

            <c:if test="${sessionScope['duplicaVersDetail'] eq 'importa'}">
                <sl:contentTitle title="Importazione Versatore "/>
            </c:if>
            <c:if test="${sessionScope['duplicaVersDetail'] eq 'duplica'}">
                <sl:contentTitle title="Duplicazione Versatore "/>
            </c:if>


            <slf:fieldBarDetailTag name="<%=AmministrazioneForm.Vers.NAME%>" hideBackButton="false"/> 
            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.VersatoriPing.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <c:if test="${(sessionScope['###_FORM_CONTAINER']['vers'].status eq 'insert') }">
                        <sl:newLine />                    
                        <slf:lblField name="<%=AmministrazioneForm.Vers.TIPOLOGIA%>" colSpan="4" controlWidth="w40" />
                    </c:if>
                    <slf:lblField name="<%=AmministrazioneForm.Vers.NM_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DS_VERS%>" colSpan="4" controlWidth="w40" />                    
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DT_INI_VAL_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DT_FINE_VAL_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.FL_ARCHIVIO_RESTITUITO%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.FL_CESSATO%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_AMBIENTE_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DT_INI_VAL_APPART_AMBIENTE%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DT_FIN_VAL_APPART_AMBIENTE%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />                   
                    <c:if test="${sessionScope['duplicaVersDetail'] eq 'importa'}">
                        <slf:lblField name="<%=AmministrazioneForm.Vers.DS_FILE_XML_VERSATORE%>" colSpan="4" controlWidth="w40" />
                        <sl:newLine />
                    </c:if>
                </slf:section> 
                <sl:newLine />
                <slf:section name="<%=AmministrazioneForm.PathFtpSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DS_PATH_INPUT_FTP%>" width="w100" controlWidth="w60" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DS_PATH_OUTPUT_FTP%>" width="w100" controlWidth="w60" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DS_PATH_TRASF%>" width="w100" controlWidth="w60" labelWidth="w20" />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=AmministrazioneForm.CorrispondenzaSacerSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=AmministrazioneForm.Vers.TI_DICH_VERS%>" width="w100" controlWidth="w20" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_ORGANIZ_IAM%>" width="w100" controlWidth="w60" labelWidth="w20" />                
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['vers'].status eq 'insert') }">
                <slf:section name="<%=AmministrazioneForm.EnteConvenzionatoSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_AMBIENTE_ENTE_CONVENZ_EC%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_ENTE_CONVENZ_EC%>" colSpan="4" controlWidth="w40" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=AmministrazioneForm.FornitoreEsternoSection.NAME%>"  styleClass="importantContainer">
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_ENTE_CONVENZ_FE%>" colSpan="4" controlWidth="w40" />
                </slf:section>
                <slf:lblField name="<%=AmministrazioneForm.Vers.DT_INI_VAL_APPART_ENTE_SIAM%>" colSpan="4" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.Vers.DT_FINE_VAL_APPART_ENTE_SIAM%>" colSpan="4" controlWidth="w40" />
            </c:if>          

            <sl:pulsantiera>
                <c:if test="${sessionScope['duplicaVersDetail'] eq 'importa'}">
                    <slf:lblField  name="<%=AmministrazioneForm.Vers.IMPORTA_VERSATORE%>" colSpan="1"/>
                </c:if>
                <c:if test="${sessionScope['duplicaVersDetail'] eq 'duplica'}">
                    <slf:lblField  name="<%=AmministrazioneForm.Vers.DUPLICA_VERSATORE%>" colSpan="1"/>
                </c:if>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>