<%@ page import="it.eng.sacerasi.slite.gen.form.GestioneJobForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="RICERCA JOB">        
        <script type="text/javascript">
            $(document).ready(function () {
                
                $(document).bind('keypress', function(e) {
                    if(e.keyCode==13){
                        e.preventDefault();
                        $("input[name='operation__ricercaGestioneJob']").trigger('click');
                    }
                });

                $('#Ni_tot_job_nuovi2').css({"text-decoration": "underline"});
                $('#Ni_tot_job_solo_foto').css({"text-decoration": "underline"});
                
                $('#Ni_tot_job_nuovi2').hover(function() {
                    $(this).css('cursor','pointer');
                });
                
                $('#Ni_tot_job_solo_foto').hover(function() {
                    $(this).css('cursor','pointer');
                });

                $('#Ni_tot_job_nuovi2').click(function () {
                    $.post("GestioneJob.html?operation=getNuoviJob").done(function (jsonData) {
                        var jobArray = jsonData.map[0];
                        var arrayNuovi = jobArray['nm_job_array_nuovi'];
                        var gestioneJobListPerAmm = $('#GestioneJobListPerAmm');

                        $.each(arrayNuovi, function (index, value) {
                            gestioneJobListPerAmm.find("td").filter(function () {
                                return $(this).text() == value;
                            }).css({"background-color": "rgb(254, 245, 0)"});
                            gestioneJobListPerAmm.find("td").filter(function () {
                                return $(this).text() == value
                            }).next().css({"background-color": "rgb(254, 245, 0)"});
                        });
                    });
                });

                $('#Ni_tot_job_solo_foto').click(function () {
                    $.post("GestioneJob.html?operation=getNuoviJob").done(function (jsonData) {
                        var jobArray = jsonData.map[0];
                        var arraySoloFoto = jobArray['nm_job_array_solo_foto'];
                        var gestioneJobFotoListPerAmm = $('#GestioneJobFotoListPerAmm');

                        $.each(arraySoloFoto, function (index, value) {
                            gestioneJobFotoListPerAmm.find("td").filter(function () {
                                return $(this).text() == value;
                            }).css({"background-color": "rgb(254, 245, 0)"});
                            gestioneJobFotoListPerAmm.find("td").filter(function () {
                                return $(this).text() == value
                            }).next().css({"background-color": "rgb(254, 245, 0)"});
                        });
                    });
                });


                $('#GestioneJobRicercaList tr').each(
                        function (index) {
                            var elemento = $(this).find('td:eq(5)');
                            if (elemento.text() === 'ATTIVO') {
                                elemento.css({"color": "#0f6e25", "text-align": "center", "height": "30"});
                            } else if (elemento.text() === 'DISATTIVO') {
                                elemento.css({"color": "#FF0000", "text-align": "center", "height": "30"});
                            } else {
                                elemento.val('IN_ESECUZIONE');
                                elemento.css({"color": "#003399", "text-align": "center", "height": "30"});
                            }

                            var colonnaStart = $(this).find('td:eq(9)').css({"text-align": "center"});
                            var colonnaStop = $(this).find('td:eq(10)').css({"text-align": "center"});
                            var colonnaSingle = $(this).find('td:eq(11)').css({"text-align": "center"});
                        }
                );


                $('#GestioneJobListPerAmm tr').click(function () {
                    // Ricavo le celle delle 2 colonne della lista GestioneJobListPerAmm
                    // della riga che ho cliccato
                    var cellaDellaPrimaColonna = $(this).find('td:eq(0)');
                    var cellaDellaSecondaColonna = $(this).find('td:eq(1)');
                    // Ricavo il nome del JOB da cercare
                    var testo = cellaDellaPrimaColonna.text();

                    // Mi tengo l'informazione del fatto se la riga cliccata (formata dai 2 td) 
                    // era già gialla
                    var giaGialla = false;
                    // Se infatti la cella è già gialla dovrò deselezionarla dopo
                    // in quanto se cliccassi sulla stessa già gialla, dopo l'azzeramento, tornerebbe gialla!
                    // E cosi avrei sempre una riga selezionata...
                    if (cellaDellaPrimaColonna.css('background-color') == 'rgb(254, 245, 194)') {
                        giaGialla = true;
                    }

                    // "Azzero" il colore di evidenziazione di tutte e 2 le liste
                    $('#GestioneJobListPerAmm tr').each(function (index) {
                        $(this).find('td').css({"background-color": ""});
                    })
                    $('#GestioneJobFotoListPerAmm tr').each(function (index) {
                        $(this).find('td').css({"background-color": ""});
                    })
                    
                    var gestioneJobFotoListPerAmm = $('#GestioneJobFotoListPerAmm');

                    // Coloro di giallo la riga cliccata sulla prima tabella
                    cellaDellaPrimaColonna.css({"background-color": "rgb(254, 245, 194)"});
                    cellaDellaSecondaColonna.css({"background-color": "rgb(254, 245, 194)"});
                    // Quindi vado a colorare di giallo la riga corrispondente lo stesso JOB
                    // sulla seconda tabella
                    gestioneJobFotoListPerAmm.find("td").filter(function () {
                        return $(this).text() == testo;
                    }).css({"background-color": "rgb(254, 245, 194)"});
                    gestioneJobFotoListPerAmm.find("td").filter(function () {
                        return $(this).text() == testo;
                    }).next().css({"background-color": "rgb(254, 245, 194)"});

                    // A questo punto, se avevo una riga gialla "precedente", la decoloro
                    // da entrambe le tabelle
                    if (giaGialla) {
                        cellaDellaPrimaColonna.css({"background-color": ""});
                        cellaDellaSecondaColonna.css({"background-color": ""});
                        gestioneJobFotoListPerAmm.find("td").filter(function () {
                            return $(this).text() == testo;
                        }).css({"background-color": ""});
                        gestioneJobFotoListPerAmm.find("td").filter(function () {
                            return $(this).text() == testo;
                        }).next().css({"background-color": ""});
                    }
                });

                $('#GestioneJobListPerAmm tr').each(
                        function (index) {
                            var elemento = $(this).find('td:eq(1)');
                            if (elemento.text() === 'ATTIVO') {
                                elemento.css({"color": "#0f6e25", "text-align": "center"});
                            } else if (elemento.text() === 'DISATTIVO') {
                                elemento.css({"color": "#FF0000", "text-align": "center"});
                            } else {
                                elemento.val('IN_ESECUZIONE');
                                elemento.css({"color": "#003399", "text-align": "center"});
                            }
                        });

                $('#GestioneJobFotoListPerAmm tr').each(
                        function (index) {
                            var elemento = $(this).find('td:eq(1)');
                            if (elemento.text() === 'ATTIVO') {
                                elemento.css({"color": "#0f6e25", "text-align": "center"});
                            } else if (elemento.text() === 'DISATTIVO') {
                                elemento.css({"color": "#FF0000", "text-align": "center"});
                            } else {
                                elemento.val('IN_ESECUZIONE');
                                elemento.css({"color": "#003399", "text-align": "center"});
                            }
                        });
                        
                        
                        $('#GestioneJobFotoListPerAmm tr').click(function () {
                    // Ricavo le celle delle 2 colonne della lista GestioneJobListPerAmm
                    // della riga che ho cliccato
                    var cellaDellaPrimaColonna = $(this).find('td:eq(0)');
                    var cellaDellaSecondaColonna = $(this).find('td:eq(1)');
                    // Ricavo il nome del JOB da cercare
                    var testo = cellaDellaPrimaColonna.text();

                    // Mi tengo l'informazione del fatto se la riga cliccata (formata dai 2 td) 
                    // era già gialla
                    var giaGialla = false;
                    // Se infatti la cella è già gialla dovrò deselezionarla dopo
                    // in quanto se cliccassi sulla stessa già gialla, dopo l'azzeramento, tornerebbe gialla!
                    // E cosi avrei sempre una riga selezionata...
                    if (cellaDellaPrimaColonna.css('background-color') == 'rgb(254, 245, 194)') {
                        giaGialla = true;
                    }

                    // "Azzero" il colore di evidenziazione di tutte e 2 le liste
                    $('#GestioneJobListPerAmm tr').each(function (index) {
                        $(this).find('td').css({"background-color": ""});
                    })
                    $('#GestioneJobFotoListPerAmm tr').each(function (index) {
                        $(this).find('td').css({"background-color": ""});
                    })
                    
                    var gestioneJobListPerAmm = $('#GestioneJobListPerAmm');

                    // Coloro di giallo la riga cliccata sulla prima tabella
                    cellaDellaPrimaColonna.css({"background-color": "rgb(254, 245, 194)"});
                    cellaDellaSecondaColonna.css({"background-color": "rgb(254, 245, 194)"});
                    // Quindi vado a colorare di giallo la riga corrispondente lo stesso JOB
                    // sulla seconda tabella
                    gestioneJobListPerAmm.find("td").filter(function () {
                        return $(this).text() == testo;
                    }).css({"background-color": "rgb(254, 245, 194)"});
                    gestioneJobListPerAmm.find("td").filter(function () {
                        return $(this).text() == testo;
                    }).next().css({"background-color": "rgb(254, 245, 194)"});

                    // A questo punto, se avevo una riga gialla "precedente", la decoloro
                    // da entrambe le tabelle
                    if (giaGialla) {
                        cellaDellaPrimaColonna.css({"background-color": ""});
                        cellaDellaSecondaColonna.css({"background-color": ""});
                        gestioneJobListPerAmm.find("td").filter(function () {
                            return $(this).text() == testo;
                        }).css({"background-color": ""});
                        gestioneJobListPerAmm.find("td").filter(function () {
                            return $(this).text() == testo;
                        }).next().css({"background-color": ""});
                    }
                });
                        
            });
        </script>

        <style>

            a.StartGestioneJob { background-image: url("img/job/start3.png");
                                 background-repeat: no-repeat;
                                 padding-right : 1.5em;
                                 text-decoration: none;
                                 width: 22px;
                                 height: 24px;	
                                 align-content: center;
            }

            a.StopGestioneJob { background-image: url("img/job/stop3.png");
                                background-repeat: no-repeat;
                                padding-right : 1.5em;
                                text-decoration: none;
                                width: 22px;
                                height: 24px;	
                                align-content: center;
            }

            a.EsecuzioneSingolaGestioneJob { background-image: url("img/job/single3.png");
                                             background-repeat: no-repeat;
                                             padding-right : 1.5em;
                                             text-decoration: none;
                                             width: 22px;
                                             height: 24px;		
                                             align-content: center;
            }
        </style>

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="RICERCA JOB"/>

            <slf:tab  name="<%= GestioneJobForm.GestioneJobTabs.NAME%>" tabElement="RicercaJobTab">
                <slf:fieldSet borderHidden="true">
                    <slf:section name="<%=GestioneJobForm.InfoRiassuntiveJobSection.NAME%>" styleClass="importantContainer">                        
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaInfo.NI_TOT_JOB_PRESENTI%>"  colSpan="3" />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaInfo.NI_TOT_JOB_ATTIVI%>"  colSpan="3" />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaInfo.NI_TOT_JOB_DISATTIVI%>"  colSpan="3" /><sl:newLine />
                    </slf:section>                    
                    <slf:section name="<%=GestioneJobForm.FiltriRicercaJobSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaFiltri.NM_AMBITO%>" colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaFiltri.DS_JOB%>"  colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaFiltri.TI_STATO_JOB%>"  colSpan="3" controlWidth="w100"/>
                        <sl:newLine />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaFiltri.RICERCA_GESTIONE_JOB%>"  colSpan="3" controlWidth="w100"/>
                    </slf:section>
                </slf:fieldSet>                
                <slf:listNavBar name="<%= GestioneJobForm.GestioneJobRicercaList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= GestioneJobForm.GestioneJobRicercaList.NAME%>" />
                <slf:listNavBar  name="<%= GestioneJobForm.GestioneJobRicercaList.NAME%>" />
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaFiltri.START_MASSIVO_GESTIONE_JOB%>"  colSpan="3" controlWidth="w100"/>
                <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaFiltri.STOP_MASSIVO_GESTIONE_JOB%>"  colSpan="3" controlWidth="w100"/>
                <slf:lblField name="<%=GestioneJobForm.GestioneJobRicercaFiltri.ESECUZIONE_SINGOLA_MASSIVA_GESTIONE_JOB%>"  colSpan="3" controlWidth="w100"/>
            </slf:tab>

            <slf:tab  name="<%= GestioneJobForm.GestioneJobTabs.NAME%>" tabElement="AmmJobTab">
                <slf:container width="w40">
                    <slf:section name="<%=GestioneJobForm.InfoJobSection.NAME%>" styleClass="importantContainer">                        
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo.NI_TOT_JOB_PRESENTI%>"  width="w100" labelWidth="w50" controlWidth="w10" labelPosition="left" controlPosition="right" /><sl:newLine />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo.NI_TOT_JOB_ATTIVI%>"  width="w100" labelWidth="w50" controlWidth="w10" labelPosition="left" controlPosition="right" /><sl:newLine />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo.NI_TOT_JOB_DISATTIVI%>"  width="w100" labelWidth="w50" controlWidth="w10" labelPosition="left" controlPosition="right" /><sl:newLine />
                        <sl:newLine skipLine="true"/>
                        <sl:newLine skipLine="true"/>
                        <sl:newLine skipLine="true"/>
                        <sl:newLine skipLine="true"/>
                        <slf:container width="w50">                                                    
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo.RICARICA_GESTIONE_JOB%>"  position="right" />
                        </slf:container>
                        <slf:container width="w50">                            
                            <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo.SALVA_FOTO_GESTIONE_JOB%>"  position="left" />
                            <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo.DISABILITA_ALL_JOBS%>"  position="right" />
                        </slf:container>                        
                        <sl:newLine skipLine="true"/>
                        <slf:listNavBar name="<%= GestioneJobForm.GestioneJobListPerAmm.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= GestioneJobForm.GestioneJobListPerAmm.NAME%>" />
                        <slf:listNavBar  name="<%= GestioneJobForm.GestioneJobListPerAmm.NAME%>" />
                    </slf:section>    
                </slf:container>
                <slf:container width="w5" ></slf:container>
                <slf:container width="w10" >
                    <sl:newLine skipLine="true"/>
                    <sl:newLine skipLine="true"/>
                    <sl:newLine skipLine="true"/>                    
                    <sl:newLine skipLine="true"/>
                    <sl:newLine skipLine="true"/>
                </slf:container>
                <slf:container width="w5" ></slf:container>
                <slf:container width="w40">
                    <slf:section name="<%=GestioneJobForm.InfoJob2Section.NAME%>" styleClass="importantContainer">                        
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo2.NI_TOT_JOB_PRESENTI2%>"  width="w100" labelWidth="w60" controlWidth="w10" labelPosition="left" controlPosition="right" /><sl:newLine />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo2.NI_TOT_JOB_ATTIVI2%>"  width="w100" labelWidth="w60" controlWidth="w10" labelPosition="left" controlPosition="right" /><sl:newLine />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo2.NI_TOT_JOB_DISATTIVI2%>"  width="w100" labelWidth="w60" controlWidth="w10" labelPosition="left" controlPosition="right" /><sl:newLine />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo2.NI_TOT_JOB_NUOVI2%>"  width="w100" labelWidth="w60" controlWidth="w10" labelPosition="left" controlPosition="right" /><sl:newLine />
                        <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo2.NI_TOT_JOB_SOLO_FOTO%>"  width="w100" labelWidth="w60" controlWidth="w10" labelPosition="left" controlPosition="right" /><sl:newLine />
                        <sl:newLine skipLine="true"/>
                        <slf:container width="w50">                            
                        </slf:container>                        
                        <slf:container width="w50">
                            <slf:lblField name="<%=GestioneJobForm.GestioneJobInfo2.RIPRISTINA_FOTO_GESTIONE_JOB%>" position="left" />
                        </slf:container>
                        <sl:newLine skipLine="true"/>                
                        <c:if test="${sessionScope.visualizzaRipristinaFoto == null}" >
                        <sl:newLine skipLine="true"/>                                                    
                        </c:if>
                        <slf:listNavBar name="<%= GestioneJobForm.GestioneJobFotoListPerAmm.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= GestioneJobForm.GestioneJobFotoListPerAmm.NAME%>" />
                        <slf:listNavBar  name="<%= GestioneJobForm.GestioneJobFotoListPerAmm.NAME%>" />
                    </slf:section>  
                </slf:container>                   
            </slf:tab>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>