<%@ page import="it.eng.sacerasi.slite.gen.form.TrasformazioniForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="<%= TrasformazioniForm.InserimentoSetParametri.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content multipartForm="true" >
            <slf:messageBox />

            <slf:fieldBarDetailTag name="<%=TrasformazioniForm.InserimentoSetParametri.NAME%>" hideBackButton="false" />
            <sl:contentTitle title="<%= TrasformazioniForm.InserimentoSetParametri.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.InserimentoSetParametri.NM_SET_PARAM_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserimentoSetParametri.DS_SET_PARAM_TRASF%>" colSpan="8"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserimentoSetParametri.FL_SET_PARAM_ARK%>" colSpan="4"/>
                <sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
