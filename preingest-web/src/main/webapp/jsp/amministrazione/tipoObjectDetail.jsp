<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Tipo Oggetto" >

        <script type='text/javascript' >
            function disable(value) {

                if ((value === "ZIP_CON_XML_SACER" || value === "DA_TRASFORMARE")) {
                    $("#Cd_registro_unita_doc_sacer").prop("disabled", true);
                    $("#Nm_tipo_unita_doc_sacer").prop("disabled", true);
                    $("#Fl_forza_accettazione_sacer").prop("disabled", true);
                    $("#Fl_forza_conservazione").prop("disabled", true);
                    $("#Fl_forza_collegamento").prop("disabled", true);
                    $("#Ti_conservazione").prop("disabled", true);
                    /*$*/
                    $("#Nm_tipo_unita_doc_sacer").val(null);
                    $("#Fl_forza_accettazione_sacer").prop("disabled", true);
                    $("#Fl_forza_conservazione").prop("disabled", true);
                    $("#Fl_forza_collegamento").prop("disabled", true);
                    $("#Ti_conservazione").prop("disabled", true);
                    $("#Cd_registro_unita_doc_sacer").val(null);
                    if (value === "DA_TRASFORMARE") {
                        $("#Id_trasf").prop("disabled", false);
                        $("#Prio_trasf").prop("disabled", false);
                        $("#Id_set_valori_param").prop("disabled", false);
                        $("#Ds_reg_exp_cd_vers").prop("disabled", false);
                    }
                } else {
                    if ((value === "ZIP_NO_XML_SACER")) {
                        $("#Cd_registro_unita_doc_sacer").prop("disabled", true);
                        $("#Cd_registro_unita_doc_sacer").val(null);
                    } else {
                        $("#Cd_registro_unita_doc_sacer").prop("disabled", false);
                    }
                    $("#Nm_tipo_unita_doc_sacer").prop("disabled", false);
                    $("#Fl_forza_accettazione_sacer").prop("disabled", false);
                    $("#Fl_forza_conservazione").prop("disabled", false);
                    $("#Fl_forza_collegamento").prop("disabled", false);
                    $("#Ti_conservazione").prop("disabled", false);
                    $("#Fl_contr_hash").prop("disabled", false);
                    $("#Id_trasf").prop("disabled", true);
                    $("#Id_trasf").val(null);
                    $("#Prio_trasf").prop("disabled", false);
                    $("#Prio_trasf").val(null);
                    $("#Ds_reg_exp_cd_vers").prop("disabled", true);
                    $("#Ds_reg_exp_cd_vers").val(null);
                    $("#Id_set_valori_param").prop("disabled", true);
                    $("#Id_set_valori_param").val(null);
                }

                if ((value === "DA_TRASFORMARE")) {
                    $("#Ti_calc_key_unita_doc").prop("disabled", true);
                    $("#Ti_calc_key_unita_doc").val(null);
                } else {
                    $("#Ti_calc_key_unita_doc").prop("disabled", false);
                }
                // MEV#27321 - Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e NO_ZIP
                if ((value === "ZIP_CON_XML_SACER") || (value === "NO_ZIP") ) {
                    $("#Ti_priorita_versamento").prop("disabled", false);
 //                   var tipoImpostato = $("#Ti_priorita_versamento").val();
                } else {
                    $("#Ti_priorita_versamento").prop("disabled", true);
                    $("#Ti_priorita_versamento").val('');
                }

            }

            $(document).ready(function () {
                disable($("#Ti_vers_file").val());

                $("#Ti_vers_file").change(function ()
                {
                    var id = $(this).val();
                    disable(id);
                    return false;
                });
                
            } );


        </script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio Tipo Oggetto "/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['tipoObjectList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.TipoObject.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoObjectList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.TipoObjectList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.Versatore.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.Vers.NM_VERS%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_VERS%>" colSpan="4" controlWidth="w40" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.TipoObjPing.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.ID_TIPO_OBJECT%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.NM_TIPO_OBJECT%>" colSpan="2" controlWidth="w60" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.DS_TIPO_OBJECT%>" colSpan="2" controlWidth="w80" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.TI_VERS_FILE%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.TI_CALC_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.CD_REGISTRO_UNITA_DOC_SACER%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.NM_TIPO_UNITA_DOC_SACER%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.TI_CONSERVAZIONE%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.FL_CONTR_HASH%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.FL_FORZA_ACCETTAZIONE_SACER%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.FL_FORZA_CONSERVAZIONE%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.FL_FORZA_COLLEGAMENTO%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.ID_TRASF%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.TI_PRIORITA%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.TI_PRIORITA_VERSAMENTO%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.DS_REG_EXP_CD_VERS%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.FL_NO_VISIB_VERS_OGG%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.FL_CREA_TIPOFILE%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
                 <sl:newLine />
                <slf:section name="<%=AmministrazioneForm.CorrispondenzaSacerTipoObjSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.TI_DICH_VERS%>" width="w100" controlWidth="w20" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.ID_ORGANIZ_IAM%>" width="w100" controlWidth="w60" labelWidth="w20" />                
                </slf:section>
                <sl:newLine skipLine="true"/>

            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=AmministrazioneForm.TipoObject.LOG_EVENTI_TIPO_OGGETTO%>" colSpan="1"/>
            </sl:pulsantiera>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoObject'].status eq 'view') }">
                <sl:newLine skipLine="true"/>
                <slf:tab  name="<%=AmministrazioneForm.TipoObjTab.NAME%>" tabElement="PigTipoFileObject">
                    <slf:list  name="<%= AmministrazioneForm.TipoFileObjectList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.TipoFileObjectList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%=AmministrazioneForm.TipoObjTab.NAME%>" tabElement="PigXsdDatiSpec">
                    <slf:list  name="<%= AmministrazioneForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.XsdDatiSpecList.NAME%>" />
                </slf:tab>
                <%--<slf:tab  name="<%=AmministrazioneForm.TipoObjTab.NAME%>" tabElement="CorrispondenzeSacerTipoOggettoTab">
                    <slf:list  name="<%= AmministrazioneForm.CorrispondenzeSacerTipoOggettoList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.CorrispondenzeSacerTipoOggettoList.NAME%>" />
                </slf:tab>--%>
                <slf:tab  name="<%=AmministrazioneForm.TipoObjTab.NAME%>" tabElement="VersatoriGenerazioneOggettiTab">
                    <slf:list  name="<%= AmministrazioneForm.VersatoriGenerazioneOggettiList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.VersatoriGenerazioneOggettiList.NAME%>" />
                </slf:tab>
            </c:if>

            <slf:section name="<%=AmministrazioneForm.ParametriAmministrazioneTipoOggettoSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriTipoOggettoButtonList.PARAMETRI_AMMINISTRAZIONE_TIPO_OGGETTO_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriAmministrazioneTipoOggettoList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriAmministrazioneTipoOggettoList.NAME%>" />
            </slf:section>
            <slf:section name="<%=AmministrazioneForm.ParametriConservazioneTipoOggettoSection.NAME%>" styleClass="noborder w100">
                 <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriTipoOggettoButtonList.PARAMETRI_CONSERVAZIONE_TIPO_OGGETTO_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriConservazioneTipoOggettoList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriConservazioneTipoOggettoList.NAME%>" />
            </slf:section>
            <slf:section name="<%=AmministrazioneForm.ParametriGestioneTipoOggettoSection.NAME%>" styleClass="noborder w100">
                 <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriTipoOggettoButtonList.PARAMETRI_GESTIONE_TIPO_OGGETTO_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriGestioneTipoOggettoList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriGestioneTipoOggettoList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
