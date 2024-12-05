<%@ page import="it.eng.sacerasi.slite.gen.form.StrumentiUrbanisticiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sl:html>
    <sl:head title="<%=StrumentiUrbanisticiForm.InserimentoWizard.DESCRIPTION%>" >
        <link rel="stylesheet" type="text/css" href="/sacerping/css/strumentiUrbanistici.css" type="text/css" media="screen" />
        <script type="text/javascript">
            $(function () {
                $('#DocumentiCaricatiList tbody tr').each(function (idx, elemento) {
                    var nomeOriginale = $($(elemento).find('td')[0]).html();
                    var nomeFile = $($(elemento).find('td')[1]).html();
                    var link = '<a href="StrumentiUrbanisticiDownloadServlet?chiave=' + nomeFile + '" download id="download" class="DownloadSU"></a>';
                    $($(elemento).find('td')[0]).html(link + '&nbsp;' + nomeOriginale);
                });
                var pulsante = $("input[name='operation__riportaInBozza']");
                pulsante.unbind();
                pulsante.click(function (event) {
                    var c = confirm("E' stato richiesto di riportare lo strumento urbanistico in stato BOZZA, confermare?");
                    if (c == false) {
                        event.preventDefault();
                        event.stopPropagation();
                        return c;
                    }
                });
            });
        </script>
        <script type="text/javascript" src="<c:url value='/js/sips/customRecuperaErroriMessageBox.js'/>" ></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore" description="Dettaglio strumento urbanistico"/>
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <%@ include file="mascheraRecuperoErrori.jspf"%>
            <sl:contentTitle title="Dettaglio strumento urbanistico" />            
            <c:choose>
                <c:when test="${requestScope.nascondiUpdate}">
                    <slf:listNavBarDetail name="<%=StrumentiUrbanisticiForm.StrumentiUrbanisticiList.NAME%>" hideOperationButton="true"/>  
                </c:when>
                <c:otherwise>
                    <slf:listNavBarDetail name="<%=StrumentiUrbanisticiForm.StrumentiUrbanisticiList.NAME%>" hideOperationButton="false"/>  
                </c:otherwise>
            </c:choose>
            <sl:newLine />
            <slf:fieldSet borderHidden="false">
                <slf:section name="<%=StrumentiUrbanisticiForm.DatiGeneraliSUSection.NAME%>" styleClass="importantContainer containerLeft w60">  
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_ENTE_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_PROVINCIA_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_UNIONE_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_TIPO_STRUMENTO_URBANISTICO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_FASE_STRUMENTO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />                    
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.OGGETTO_OUT%>" width="w100" labelWidth="w30" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DS_DESCRIZIONE_OUT%>" width="w100" labelWidth="w30" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_ATTO_OUT%>" colSpan="3"  labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ANNO_OUT%>" colSpan="3"  labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NUMERO_OUT%>" colSpan="3"  labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.CD_KEY_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.DATA%>" colSpan="3" labelWidth="w30" controlWidth="w100" />
                    <sl:newLine />
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DT_CREAZIONE_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_STATO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>                  
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DT_STATO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ID_STRUMENTI_URBANISTICI_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100" />
                    <sl:newLine />
                </slf:section> 
                <slf:section name="<%=StrumentiUrbanisticiForm.AltreFasiSection.NAME%>" styleClass="importantContainer containerRight w40">  
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.FASE_COLLEGATA1_OUT%>" width="w100" labelWidth="w30" controlWidth="w70"  />
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.IDENTIFICATIVO_COLLEGATO1_OUT%>" width="w100" labelWidth="w30" controlWidth="w70"  />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ANNO_COLLEGATO1_OUT%>" width="w100" labelWidth="w30" controlWidth="w70"  />
                    <sl:newLine />
                    <sl:newLine />
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.FASE_COLLEGATA2_OUT%>" width="w100" labelWidth="w30" controlWidth="w70"  />
                    <sl:newLine />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.IDENTIFICATIVO_COLLEGATO2_OUT%>" width="w100" labelWidth="w30" controlWidth="w70"  />
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ANNO_COLLEGATO2_OUT%>" width="w100" labelWidth="w30" controlWidth="w70"  />
                </slf:section> 
                <sl:newLine />
                <sl:newLine />
                <slf:section name="<%=StrumentiUrbanisticiForm.DocumentiCaricatiSection.NAME%>" styleClass="importantContainer">  
                    <slf:list  name="<%= StrumentiUrbanisticiForm.DocumentiCaricatiList.NAME%>"  />
                </slf:section>
                <!-- Se non ci sono segnalazioni si fa sparire proprio la sezione -->
                <c:if test="${fn:length(requestScope['alObbNonCaricati']) gt 0 || fn:length(requestScope['alDocInErrore']) gt 0}">
                    <slf:section name="<%=StrumentiUrbanisticiForm.ErroriRiepilogoSection.NAME%>" styleClass="importantContainer">
                        <c:forEach var="elem" items="${requestScope['alObbNonCaricati']}">
                            &nbsp;<c:out value="${elem}" /><BR/>
                        </c:forEach>
                        <c:forEach var="elem" items="${requestScope['alDocInErrore']}">
                            &nbsp;<c:out value="${elem}" /><BR/>
                        </c:forEach>
                    </slf:section>
                </c:if>
                <!-- Se non ci sono segnalazioni oppure se il SU è IN_ELABORAZIONE non si mostra proprio la sezione -->
                <c:if test="${(requestScope['statoSU'] eq 'BOZZA') and not(strDocFacolativi eq '')}">
                    <slf:section name="<%=StrumentiUrbanisticiForm.AggiuntaDocumentiFacoltativiSection.NAME%>" styleClass="importantContainer"> 
                        <c:set value="${requestScope['strDocFacolativi']}" var="strDocFacolativi" />
                        <c:if test="${ not(strDocFacolativi eq '') }">
                            &nbsp;E' ancora possibile caricare nello Strumento urbanistico corrente i seguenti documenti facoltativi: <STRONG><c:out value="${strDocFacolativi}" /></STRONG>.
                            <BR/>
                            <BR/>
                            &nbsp;Per versare altri file zip tornare alla schermata precedente tramite il tasto indietro.
                        </c:if>
                    </slf:section>
                </c:if>                        
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrumentiUrbanisticiForm.RiepilogoButtonList.VERSA_SU%>" />
                    <slf:buttonList name="<%=StrumentiUrbanisticiForm.DettaglioButtonList.NAME%>" />
                </sl:pulsantiera>
                
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrumentiUrbanisticiForm.SUDetailStatiSection.NAME%>">  
                    <slf:list   name="<%= StrumentiUrbanisticiForm.SUDetailStatiList.NAME%>" />
                    <slf:listNavBar  name="<%= StrumentiUrbanisticiForm.SUDetailStatiList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
