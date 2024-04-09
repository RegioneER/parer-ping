<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista oggetti" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA OGGETTI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.FiltriRiepilogoVersamenti.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.ID_AMBIENTE_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.ID_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.ID_TIPO_OBJECT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.ID_OBJECT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.PERIODO_VERS%>" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.GIORNO_VERS_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriOggetti.ORE_VERS_DA%>" name2="<%=MonitoraggioForm.FiltriOggetti.MINUTI_VERS_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.GIORNO_VERS_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriOggetti.ORE_VERS_A%>" name2="<%=MonitoraggioForm.FiltriOggetti.MINUTI_VERS_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.TI_STATO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.TI_VERS_FILE%>" colSpan="2" />
                <sl:newLine />
                <slf:section name="<%=MonitoraggioForm.ChiaveUDListaOggettiSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.CD_REGISTRO_UNITA_DOC_SACER%>" colSpan="3" />                
                    <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.AA_UNITA_DOC_SACER%>" colSpan="3" />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.CD_KEY_UNITA_DOC_SACER%>" colSpan="3" />
                </slf:section>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.FILTRI_OGGETTI_CD_KEY_OBJECT%>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggetti.RICERCA_OGGETTI%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioForm.OggettiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.OggettiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>