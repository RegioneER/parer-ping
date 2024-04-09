var idTimerPoll = 0;
function poll() {
    $('.verificaDocumentiMessageBox').empty();
    var idStrumentoUrbanistico = 0;
    // Funzione JavaScript normale
    return setInterval(function () {
        // Chiamata AJAX: chiamare http verso il server senza rigenerare di nuovo la pagina (come funziomna in http classico)
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'StrumentiUrbanistici.html',
            data: {
                operation: "checkDocumentiSU"
            },
            success:
                    function (data) {
                        var msg = '<div class="messages verificaDocumentiWarningBox"><ul><li class="message warning ">';
                        var div = '';
                        var array = data.map[0].array;
                        var oggetto = '';
                        var stopPoll = 'NO';
                        var listaDocumentiConErrori = '';
                        var documentiConErrori ='NO';

                        array.forEach(function (entry) {
                            if (entry.ID_STRUMENTO_URBANISTICO !== null) {
                                idStrumentoUrbanistico = entry.ID_STRUMENTO_URBANISTICO;
                            }
                            if (entry.DOCUMENTI_CON_ERRORI === 'SI') {
                                listaDocumentiConErrori = entry.LISTA_DOCUMENTI_CON_ERRORI;
                                documentiConErrori = 'SI';
                            }
                            if (entry.OGGETTO !== null) {
                               oggetto = entry.OGGETTO;
                            }
                            if (entry.STOP_POLL === 'SI') {
                                stopPoll = 'SI';
                            }
                        });

                        // Verifico se stoppare il polling
                        if (stopPoll === 'SI') {
                            if (documentiConErrori === 'NO') {
                                msg += 'Le operazioni di verifica documenti sullo strumento urbanistico <br/>' + oggetto + '<br/><br/>sono state eseguite con successo';
                                msg += '</li>' + div + '</ul></div>';
                                $('.verificaDocumentiMessageBox').append(msg);
                            } else {
                                msg += 'Le operazioni di verifica documenti sullo strumento urbanistico <br/>' + oggetto + '<br/><br/>non sono andate a buon fine per i seguenti documenti: ' + listaDocumentiConErrori;
                                msg += '</li>' + div + '</ul></div>';
                                $('.verificaDocumentiMessageBox').append(msg);                              
                            }
                            //clearInterval(idTimerPoll);
                        }
                    },
            complete: function () {
                if ($('.verificaDocumentiMessageBox').html().trim() && !$('.verificaDocumentiMessageBox').data('POPUP')) {
                    $('.verificaDocumentiMessageBox').data('POPUP', true);
                    $('.verificaDocumentiMessageBox').dialog({
                        autoOpen: true,
                        width: 600,
                        modal: true,
                        closeOnEscape: true,
                        resizable: false,
                        dialogClass: "alertBox",
                        buttons: {
                            "Ok": function () {
                                $(this).dialog("close");
                                $('.verificaDocumentiMessageBox').removeData('POPUP');
                                $('.verificaDocumentiMessageBox').empty();
                                 window.location = "StrumentiUrbanistici.html?operation=reloadThirdStepStrumentoUrbanistico&idStrumentoUrbanistico="+idStrumentoUrbanistico;
                            }
                        }
                    });
                }
            }
        });
    }, 5000);
};