<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio stato versamento oggetto" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox /> 
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="Dettaglio stato versamento oggetto"/>
            <slf:fieldBarDetailTag name="<%=AmministrazioneForm.StatoVersamentoObjectDetail.NAME%>" hideBackButton="true"/> 
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.StatoVersamentoOggettoSection.NAME%>" styleClass="importantContainer">  
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.StatoVersamentoObjectDetail.TI_STATO_OBJECT%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.StatoVersamentoObjectDetail.DS_TI_STATO_OBJECT%>" colSpan="4" />
                </slf:fieldSet>
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>