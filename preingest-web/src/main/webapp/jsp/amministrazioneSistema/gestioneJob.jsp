<%@ page import="it.eng.sacerasi.slite.gen.form.GestioneJobForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Gestione Job" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />

            <sl:contentTitle title="GESTIONE JOB"/>

            <slf:fieldSet legend="Producer coda verifica hash" >
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaVerificaH.ATTIVO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaVerificaH.DT_REG_LOG_JOB_INI%>" colSpan="2"/>
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaVerificaH.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="2"/>

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaVerificaH.START_PRODUCER_CODA_VERIFICA_H%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaVerificaH.STOP_PRODUCER_CODA_VERIFICA_H%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaVerificaH.START_ONCE_PRODUCER_CODA_VERIFICA_H%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ProducerCodaVerificaH.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <slf:fieldSet legend="Prepara XML SACER" >
                <slf:lblField name="<%=GestioneJobForm.PreparaXMLSACER.ATTIVO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.PreparaXMLSACER.DT_REG_LOG_JOB_INI%>" colSpan="2"/>
                <slf:lblField name="<%=GestioneJobForm.PreparaXMLSACER.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="2"/>
                <sl:newLine />

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.PreparaXMLSACER.START_PREPARA_XMLSACER%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparaXMLSACER.STOP_PREPARA_XMLSACER%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.PreparaXMLSACER.START_ONCE_PREPARA_XMLSACER%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.PreparaXMLSACER.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <slf:fieldSet legend="Producer coda versamento" >
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaVersamento.ATTIVO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaVersamento.DT_REG_LOG_JOB_INI%>" colSpan="2"/>
                <slf:lblField name="<%=GestioneJobForm.ProducerCodaVersamento.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="2"/>

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaVersamento.START_PRODUCER_CODA_VERSAMENTO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaVersamento.STOP_PRODUCER_CODA_VERSAMENTO%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.ProducerCodaVersamento.START_ONCE_PRODUCER_CODA_VERSAMENTO%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.ProducerCodaVersamento.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <slf:fieldSet legend="Recupera errori in coda" >
                <slf:lblField name="<%=GestioneJobForm.RecuperaErroriCoda.ATTIVO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.RecuperaErroriCoda.DT_REG_LOG_JOB_INI%>" colSpan="2"/>
                <slf:lblField name="<%=GestioneJobForm.RecuperaErroriCoda.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="2"/>

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.RecuperaErroriCoda.START_RECUPERA_ERRORI_CODA%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.RecuperaErroriCoda.STOP_RECUPERA_ERRORI_CODA%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.RecuperaErroriCoda.START_ONCE_RECUPERA_ERRORI_CODA%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.RecuperaErroriCoda.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <slf:fieldSet legend="Recupera versamenti in errore e in timeout" >
                <slf:lblField name="<%=GestioneJobForm.RecuperaVersErr.ATTIVO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.RecuperaVersErr.DT_REG_LOG_JOB_INI%>" colSpan="2"/>
                <slf:lblField name="<%=GestioneJobForm.RecuperaVersErr.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="2"/>

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.RecuperaVersErr.START_RECUPERA_VERS_ERR%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.RecuperaVersErr.STOP_RECUPERA_VERS_ERR%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.RecuperaVersErr.START_ONCE_RECUPERA_VERS_ERR%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.RecuperaVersErr.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <slf:fieldSet legend="Recupero da SACER" >
                <slf:lblField name="<%=GestioneJobForm.RecuperoSACER.ATTIVO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.RecuperoSACER.DT_REG_LOG_JOB_INI%>" colSpan="2"/>
                <slf:lblField name="<%=GestioneJobForm.RecuperoSACER.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="2"/>

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.RecuperoSACER.START_RECUPERO_SACER%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.RecuperoSACER.STOP_RECUPERO_SACER%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.RecuperoSACER.START_ONCE_RECUPERO_SACER%>" colSpan="2" position="right" />                    
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.RecuperoSACER.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <slf:fieldSet legend="Allineamento organizzazioni" >
                <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.ATTIVO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.DT_REG_LOG_JOB_INI%>" colSpan="2" />
                <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="2" />

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.START_ALLINEAMENTO_ORGANIZZAZIONI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.STOP_ALLINEAMENTO_ORGANIZZAZIONI%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoOrganizzazioni.START_ONCE_ALLINEAMENTO_ORGANIZZAZIONI%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.AllineamentoOrganizzazioni.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="<%=GestioneJobForm.InizializzazioneLog.DESCRIPTION%>" >
                <slf:lblField name="<%=GestioneJobForm.InizializzazioneLog.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.InizializzazioneLog.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.InizializzazioneLog.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.InizializzazioneLog.START_ONCE_INIZIALIZZAZIONE_LOG%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.InizializzazioneLog.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="<%=GestioneJobForm.AllineamentoLog.DESCRIPTION%>" >
                <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.START_ALLINEAMENTO_LOG%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.STOP_ALLINEAMENTO_LOG%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineamentoLog.START_ONCE_ALLINEAMENTO_LOG%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.AllineamentoLog.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            
            <slf:fieldSet legend="Esegui trasformazione" >
                <slf:lblField name="<%=GestioneJobForm.EseguiTrasformazione.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.EseguiTrasformazione.VT_REG_LOG_JOB_INI%>" colSpan="3"/>
                <slf:lblField name="<%=GestioneJobForm.EseguiTrasformazione.VT_PROSSIMA_ATTIVAZIONE%>" colSpan="3"/>

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.EseguiTrasformazione.START_ESEGUI_TRASFORMAZIONE %>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.EseguiTrasformazione.STOP_ESEGUI_TRASFORMAZIONE%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.EseguiTrasformazione.START_ONCE_ESEGUI_TRASFORMAZIONE%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.EseguiTrasformazione.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            
            <slf:fieldSet legend="Invia oggetti generati a Ping" >
                <slf:lblField name="<%=GestioneJobForm.InviaOggettiGeneratiAPing.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.InviaOggettiGeneratiAPing.VT_REG_LOG_JOB_INI%>" colSpan="3"/>
                <slf:lblField name="<%=GestioneJobForm.InviaOggettiGeneratiAPing.VT_PROSSIMA_ATTIVAZIONE%>" colSpan="3"/>

                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.InviaOggettiGeneratiAPing.START_INVIA_OGGETTI_GENERATI_APING %>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.InviaOggettiGeneratiAPing.STOP_INVIA_OGGETTI_GENERATI_APING%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.InviaOggettiGeneratiAPing.START_ONCE_INVIA_OGGETTI_GENERATI_APING %>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.InviaOggettiGeneratiAPing.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
                
            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="<%=GestioneJobForm.InvioSU.DESCRIPTION%>" >
                <slf:lblField name="<%=GestioneJobForm.InvioSU.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.InvioSU.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.InvioSU.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.InvioSU.START_INVIO_SU%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.InvioSU.STOP_INVIO_SU%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.InvioSU.START_ONCE_INVIO_SU%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.InvioSU.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="<%=GestioneJobForm.InvioSisma.DESCRIPTION%>" >
                <slf:lblField name="<%=GestioneJobForm.InvioSisma.ATTIVO%>" colSpan="3" controlWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.InvioSisma.DT_REG_LOG_JOB_INI%>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.InvioSisma.DT_PROSSIMA_ATTIVAZIONE%>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.InvioSisma.START_INVIO_SISMA%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.InvioSisma.STOP_INVIO_SISMA%>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.InvioSisma.START_ONCE_INVIO_SISMA%>" colSpan="2" position="right" />
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.InvioSisma.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
            
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
