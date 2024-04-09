<%@page import="it.eng.sacerasi.slite.gen.form.TrasformazioniForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="<%= TrasformazioniForm.FiltriRicercaTrasformazioni.DESCRIPTION %>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=TrasformazioniForm.RicercaTrasformazioneSection.DESCRIPTION %>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.CD_TRASF_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.DS_TRANSF_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.CD_VERSIONE_COR_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.DS_VERSIONE_COR_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.FL_ATTIVA_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.DT_ISTITUZ_SEARCH %>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.DT_SOPPRES_SEARCH %>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
            </slf:fieldSet>                
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.RICERCA_TRASFORMAZIONE %>"  width="w50" />
            </sl:pulsantiera> 
            <sl:newLine skipLine="true"/>
            
            <!--  lista con i risultati -->
            <slf:listNavBar name="<%= TrasformazioniForm.TrasformazioniList.NAME %>" pageSizeRelated="true"/>
            <slf:list name="<%= TrasformazioniForm.TrasformazioniList.NAME%>" />
            <slf:listNavBar  name="<%= TrasformazioniForm.TrasformazioniList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>