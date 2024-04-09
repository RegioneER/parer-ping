<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Versatore" >
        <script type="text/javascript" src="<c:url value='/js/sips/customUpdateVersatoreMessageBox.js'/>"></script>

        <script type='text/javascript'>
            $(document).ready(function () {
                // Al caricamento della pagina, eseguo gestisciRifTemp() e inizializzo il change sul campo
                gestisciTipologia();
                initChangeEvents();
            })


            function initChangeEvents() {
                $('#Tipologia').change(function () {
                    gestisciTipologia();
                });
            }

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
                } else if (tipologia.val() === 'SOGGETTO_ATTUATORE') {
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

        </script>

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 

            <c:if test="${!empty requestScope.customBoxSalvataggioVers}">
                <div class="messages customBoxSalvataggioVers ">
                    <ul>
                        <li class="message warning "><c:out value="${requestScope.customMessageSalvataggioVers}"/> </li>
                    </ul>
                </div>
            </c:if>

            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">Le modifiche apportate potrebbero provocare il fallimento dei versamenti in corso a Sacer: si desidera procedere?</li>
                    </ul>
                </div>
            </c:if>

            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= AmministrazioneForm.VersatoreCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <sl:contentTitle title="Dettaglio Versatore "/>

            <c:if test="${sessionScope['loadVersDaMenu'] == true}">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.VersList.NAME%>"/>
                <script>
                    $('a[title="Indietro"]').hide();
                </script>
            </c:if>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['versList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.Vers.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['versList'].table['empty']) && sessionScope['loadVersDaMenu'] != true }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.VersList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.VersatoriPingNoTitle.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_VERS%>" colSpan="4" controlWidth="w40" />
                    <%--<c:if test="${(sessionScope['###_FORM_CONTAINER']['vers'].status eq 'insert') }">                    --%>
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.TIPOLOGIA%>" colSpan="4" controlWidth="w40" />
                    <%--</c:if>--%>
                    <sl:newLine />
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
                </slf:section>     
                <sl:newLine />
                <slf:section name="<%=AmministrazioneForm.AmbienteSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_AMBIENTE_VERS%>" width="w100" controlWidth="w75" labelWidth="w25" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DT_INI_VAL_APPART_AMBIENTE%>" width="w40" controlWidth="w40" labelWidth="450"/>
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DT_FIN_VAL_APPART_AMBIENTE%>" width="w40" controlWidth="w40" labelWidth="450"/>
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=AmministrazioneForm.PathFtpSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=AmministrazioneForm.Vers.TI_STATO_CARTELLE%>" width="w100" controlWidth="w60" labelWidth="w20" />
                    <sl:newLine />
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
            <sl:pulsantiera>
                <slf:lblField  name="<%=AmministrazioneForm.Vers.ASSOCIA_SOP_CLASS_BUTTON%>" colSpan="1" />                
                <slf:lblField  name="<%=AmministrazioneForm.Vers.ESPORTA_VERSATORE%>" colSpan="1"/>
                <slf:lblField  name="<%=AmministrazioneForm.Vers.LOG_EVENTI%>" colSpan="1"/>
                <slf:lblField  name="<%=AmministrazioneForm.Vers.CESSA_VERSATORE%>" colSpan="1" />     
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['vers'].status eq 'view') }">
                <slf:section name="<%=AmministrazioneForm.EnteSiamSection.NAME%>" styleClass="importantContainer">
                    <slf:list  name="<%= AmministrazioneForm.EnteConvenzOrgList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.EnteConvenzOrgList.NAME%>" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=AmministrazioneForm.PrecedentiAppartenenzeAmbientiSection.NAME%>" styleClass="importantContainer">
                    <slf:list  name="<%= AmministrazioneForm.PrecedentiAppartenenzeAmbientiList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.PrecedentiAppartenenzeAmbientiList.NAME%>" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:tab  name="<%=AmministrazioneForm.VersTab.NAME%>" tabElement="<%=AmministrazioneForm.VersTab.pig_tipo_object%>">                        
                    <!--  piazzo la lista con i risultati -->
                    <slf:list  name="<%= AmministrazioneForm.TipoObjectList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.TipoObjectList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%=AmministrazioneForm.VersTab.NAME%>" tabElement="<%=AmministrazioneForm.VersTab.pig_sop_class_dicom_vers%>">                        
                    <!--  piazzo la lista con i risultati -->
                    <slf:list  name="<%= AmministrazioneForm.SopClassList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.SopClassList.NAME%>" />
                </slf:tab>
                <%--<slf:tab  name="<%=AmministrazioneForm.VersTab.NAME%>" tabElement="<%=AmministrazioneForm.VersTab.corrispondenze_sacer_vers_tab%>">                        
                    <slf:list  name="<%= AmministrazioneForm.CorrispondenzeSacerVersatoreList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.CorrispondenzeSacerVersatoreList.NAME%>" />
                </slf:tab>--%>
            </c:if>

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
                <slf:section name="<%=AmministrazioneForm.DateEntiSection.NAME%>"  styleClass="importantContainer">
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DT_INI_VAL_APPART_ENTE_SIAM%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DT_FINE_VAL_APPART_ENTE_SIAM%>" colSpan="4" controlWidth="w40" />
                </slf:section>
            </c:if>          

            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.ParametriAmministrazioneVersatoreSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriVersatoreButtonList.PARAMETRI_AMMINISTRAZIONE_VERSATORE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriAmministrazioneVersatoreList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriAmministrazioneVersatoreList.NAME%>" />
            </slf:section>
            <slf:section name="<%=AmministrazioneForm.ParametriConservazioneVersatoreSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriVersatoreButtonList.PARAMETRI_CONSERVAZIONE_VERSATORE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriConservazioneVersatoreList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriConservazioneVersatoreList.NAME%>" />
            </slf:section>
            <slf:section name="<%=AmministrazioneForm.ParametriGestioneVersatoreSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriVersatoreButtonList.PARAMETRI_GESTIONE_VERSATORE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriGestioneVersatoreList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriGestioneVersatoreList.NAME%>" />
            </slf:section>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
