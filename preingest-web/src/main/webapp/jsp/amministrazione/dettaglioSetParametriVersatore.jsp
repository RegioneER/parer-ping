<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AmministrazioneForm.SetParametriVersatoreDetail.DESCRIPTION%>" >
    </sl:head>
    <script type="text/javascript" src="<c:url value='/js/sips/customUpdateParametroVersatoreMessageBox.js'/>" ></script>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <div id="content">
            <slf:messageBox /> 

            <c:if test="${!empty sessionScope.customBoxUpdateParametroVersatore}">
                <div class="messages customBoxUpdateParametroVersatore ">
                    <ul>
                        <li class="message info ">Parametro da modificare:</li>
                        <br>
                        <div class="containerLeft w4ctr">
                            <slf:lblField name="<%=AmministrazioneForm.ValoreParametroVersatoreDetail.NM_PARAM_TRASF%>" labelWidth="w30" />
                            <slf:lblField name="<%=AmministrazioneForm.ValoreParametroVersatoreDetail.DS_VALORE_PARAM%>" labelWidth="w30" controlWidth="w60" />
                        </div> 
                        <div class="pulsantieraUpdateParametroVersatore">
                            <slf:doubleLblField name="<%=AmministrazioneForm.ValoreParametroVersatoreDetail.CONFERMA_MODIFICA_VALORE_PARAMETRO_VERSATORE%>" name2="<%=AmministrazioneForm.ValoreParametroVersatoreDetail.ANNULLA_MODIFICA_VALORE_PARAMETRO_VERSATORE%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1" />
                        </div> 
                    </ul>
                </div> 
            </c:if>

            <sl:contentTitle title="<%=AmministrazioneForm.SetParametriVersatoreDetail.DESCRIPTION%>" />
            <sl:form id="multipartForm" multipartForm="true"> 
            
            <c:if test="${sessionScope['###_FORM_CONTAINER']['setParametriVersatoreList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.SetParametriVersatoreDetail.NAME%>" /> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['setParametriVersatoreList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.SetParametriVersatoreList.NAME%>" />  
            </c:if>
                
            <slf:lblField name="<%=AmministrazioneForm.SetParametriVersatoreDetail.BUTTON_ACTION %>"/>

            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.TipoOggettoDaTrasfSection.NAME%>" styleClass="importantContainer">
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.VERSATORE_TRASF%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.NM_TIPO_OBJECT_DA_TRASF%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.CD_TRASF%>" colSpan="2" controlWidth="w40" />
                </slf:fieldSet>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.ID_VERS_GEN%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.ID_TIPO_OBJECT_GEN%>" colSpan="2" controlWidth="w40" />
                </slf:fieldSet>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.ParametriSection.NAME%>" styleClass="importantContainer">
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.SetParametriVersatoreDetail.NM_SET_PARAM_TRASF%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.SetParametriVersatoreDetail.DS_SET_PARAM_TRASF%>" colSpan="2" controlWidth="w40" />
                </slf:fieldSet>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.CaricaParametriSection.NAME%>" styleClass="importantContainer">
                <slf:fieldSet>
                    <input id="Ds_file_csv_parameters" name="Ds_file_csv_parameters" class="slText w40" type="file">
                    <sl:newLine />
                    <sl:pulsantiera>
                        <slf:lblField name="<%=AmministrazioneForm.SetParametriVersatoreDetail.CARICA_PARAMETRI_DA_CSV %>" colSpan="2" controlWidth="w40" />
                    </sl:pulsantiera>
                </slf:fieldSet>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:lblField name="<%=AmministrazioneForm.SetParametriVersatoreDetail.ELIMINA_SET_PARAMETRI_VERSATORE%>" colSpan="2" controlWidth="w40" />
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.ValoreParametriSection.NAME%>" styleClass="importantContainer">
                <slf:list  name="<%= AmministrazioneForm.ValoreParametriVersatoreList.NAME%>"  />
                <slf:listNavBar  name="<%= AmministrazioneForm.ValoreParametriVersatoreList.NAME%>" />
            </slf:section>
            </sl:form>
        </div>
        <sl:footer />

        <script type='text/javascript'>
            $(document).ready(function () {
                $('input[name ="operation__caricaParametriDaCSV"]').click(function () {
                    $('input[name ="Button_action"]').val('caricaParametriDaCSV');
                });

                $('input[name ="operation__eliminaSetParametriVersatore"]').click(function () {
                    $('input[name="Button_action"]').val('eliminaSetParametriVersatore');
                });
            });
        </script>

    </sl:body>
</sl:html>
