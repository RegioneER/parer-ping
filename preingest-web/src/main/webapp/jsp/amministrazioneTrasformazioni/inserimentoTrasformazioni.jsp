<%@ page import="it.eng.sacerasi.slite.gen.form.TrasformazioniForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="<%= TrasformazioniForm.InserimentoTrasformazioniSection.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content multipartForm="true" >
            <slf:messageBox />

            <slf:fieldBarDetailTag name="<%=TrasformazioniForm.InserisciTrasformazione.NAME%>" hideBackButton="true" />
            <sl:contentTitle title="<%= TrasformazioniForm.InserimentoTrasformazioniSection.DESCRIPTION%>"/>

            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_NAME%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_DESCRIPTION%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_VERSION%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_VERSION_DESCRIPTION%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.DT_ISTITUZ %>" colSpan="1" />
                <slf:doubleLblField name="<%=TrasformazioniForm.InserisciTrasformazione.ORE_DT_IST %>" name2="<%=TrasformazioniForm.InserisciTrasformazione.MINUTI_DT_IST %>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.DT_SOPPRES %>" colSpan="1"/>
                <slf:doubleLblField name="<%=TrasformazioniForm.InserisciTrasformazione.ORE_DT_SOP %>" name2="<%=TrasformazioniForm.InserisciTrasformazione.MINUTI_DT_SOP %>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_ENABLED%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_BLOB%>" colSpan="2"/><sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
