<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Tipo File Oggetto" >
        <script type='text/javascript'>

            $(document).ready(function () {
                var fl_vers_sacer_asinc = $("div[id='Fl_vers_sacer_asinc']").text();
                if (fl_vers_sacer_asinc === "0")
                    $("div[id='Fl_vers_sacer_asinc']").text("NO");
                if (fl_vers_sacer_asinc === "1")
                    $("div[id='Fl_vers_sacer_asinc']").text("SI");
                var id = $("#Fl_ver_firma_fmt_sacer").val();
                if (id === "0") {
                    //$("#Nm_fmt_file_vers_sacer").prop("disabled",false);
                    $("#Nm_fmt_file_calc_sacer").prop("disabled", false);
                    $("#Ds_fmt_rappr_calc_sacer").prop("disabled", false);
                    $("#Ds_fmt_rappr_esteso_calc_sacer").prop("disabled", false);
                } else if (id === "1") {
                    //$("#Nm_fmt_file_vers_sacer").prop("disabled",true);
                    $("#Nm_fmt_file_calc_sacer").prop("disabled", true);
                    $("#Ds_fmt_rappr_calc_sacer").prop("disabled", true);
                    $("#Ds_fmt_rappr_esteso_calc_sacer").prop("disabled", true);

                }

                $("#Fl_ver_firma_fmt_sacer").change(function ()
                {
                    var id = $(this).val();
                    if (id === "0") {
                        //$("#Nm_fmt_file_vers_sacer").prop("disabled",false);
                        $("#Nm_fmt_file_calc_sacer").prop("disabled", false);
                        $("#Ds_fmt_rappr_calc_sacer").prop("disabled", false);
                        $("#Ds_fmt_rappr_esteso_calc_sacer").prop("disabled", false);
                    } else if (id === "1") {
                        //$("#Nm_fmt_file_vers_sacer").prop("disabled",true);
                        $("#Nm_fmt_file_calc_sacer").prop("disabled", true);
                        $("#Ds_fmt_rappr_calc_sacer").prop("disabled", true);
                        $("#Ds_fmt_rappr_esteso_calc_sacer").prop("disabled", true);

                    }

                });
                var fl_calc_hash_sacer = $("#Fl_calc_hash_sacer").val();
                var ti_vers_file = $("#Ti_vers_file").val();
                // Recupero il valore di "Controllo Hash" nel dettaglio Tipo Oggetto
                var ti_contr_hash = $("#Fl_contr_hash").text();
                // Se il tipo versamento non è NO_ZIP la combo Tipo Calcolo Hash
                // deve essere visibile ma non editabile. Quindi il codice si comporta 
                // in modo analogo alla situazione con flag Calcolo Hash in Sacer impostato
                // a SI.
//                if (ti_vers_file === "ZIP_NO_XML_SACER" || ti_vers_file === "ZIP_CON_XML_SACER" || ti_vers_file === "DA_TRASFORMARE") {
//                    idFl_calc_hash_sacer = "1";
//
//                }
                // Se sono NO_ZIP
                if (ti_vers_file === "NO_ZIP") {
                    if (fl_calc_hash_sacer === "0" || ti_contr_hash === "SI") {
                        $("#Ti_calc_hash_sacer").prop("disabled", false);
                    } else {
                        $("#Ti_calc_hash_sacer").find('option:selected').removeAttr("selected");
                        $("#Ti_calc_hash_sacer").prop("disabled", true);
                    }
                }

                $("#Fl_calc_hash_sacer").change(function ()
                {
                    var id = $(this).val();
                    if (id === "0" || ti_contr_hash === "SI") {
                        $("#Ti_calc_hash_sacer").prop("disabled", false);
                    } else {
                        $("#Ti_calc_hash_sacer").find('option:selected').removeAttr("selected");
                        $("#Ti_calc_hash_sacer").prop("disabled", true);
                    }
                });

            });

        </script> 
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio Tipo File Oggetto "/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['tipoFileObjectList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.TipoFileObject.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoFileObjectList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.TipoFileObjectList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.VersatoriPing.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.ID_TIPO_FILE_OBJECT%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.NM_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.TipoObjPing.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.NM_TIPO_OBJECT%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoObject.FL_CONTR_HASH%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.TI_VERS_FILE%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.TipoFileObjPing.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.NM_TIPO_FILE_OBJECT%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.DS_TIPO_FILE_OBJECT%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.FL_VERS_SACER_ASINC%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.FL_CALC_HASH_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.TI_CALC_HASH_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.FL_VER_FIRMA_FMT_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ConfigurazioneInSacer.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.NM_TIPO_DOC_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.TI_DOC_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.NM_TIPO_STRUT_DOC_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.NM_TIPO_COMP_DOC_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.NM_FMT_FILE_VERS_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.NM_FMT_FILE_CALC_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.DS_FMT_RAPPR_CALC_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.TipoFileObject.DS_FMT_RAPPR_ESTESO_CALC_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoFileObject'].status eq 'view') }">
                <slf:tab  name="<%=AmministrazioneForm.TipoFileObjTab.NAME%>" tabElement="XsdAssociatiTab">
                    <slf:list  name="<%= AmministrazioneForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.XsdDatiSpecList.NAME%>" />
                </slf:tab>
            </c:if>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>