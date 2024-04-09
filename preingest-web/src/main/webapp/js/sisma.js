function initWizardStep1() {
    $('#Data, #Ds_descrizione, #Classifica, #Id_fascicolo, #Oggetto_fascicolo, #Id_sottofascicolo, #Oggetto_sottofascicolo, #Fl_intervento_soggetto_a_tutela').change(settaFlagModificato);

    // All'inizio in modifica/inserimento il pulsante salva bozza viene spento
    if ($('#Modificato').val() !== 'S') {
        $('input[name="operation__salvaBozza"]').css('display', 'none');
    }
}

function initWizardStep1InsertMode() {
    // Negli altri casi attiva il trigger per la selezione del finanziamento, tipo sisma e fase
    $("#Id_sisma_finanziamento").change(function () {
        selezioneFinanziamento($(this));
        settaFlagModificato();
    });
    // Interventi
    $("#Id_sisma_progetti_ag").change(function () {
        selezioneIntervento($(this));
        settaFlagModificato();
    });
    // Fasi progettuali
    $("#Id_sisma_fase_progetto").change(function () {
        selezioneFase($(this));
        settaFlagModificato();
    });
    // Stato progetto
    $("#Id_sisma_stato_progetto").change(function () {
        selezioneStato($(this));
        settaFlagModificato();
    });
    // Atti
    $("#Id_sisma_val_atto").change(function () {
        $('#Ti_atto_out').html($('#Id_sisma_val_atto option:selected').text());
        settaFlagModificato();
    });

    $('#Numero, #Anno').change(function () {
        $('#Numero_out').html($('#Numero').val());
        $('#Anno_out').html($('#Anno').val());
        settaFlagModificato();
    });
}

function initWizardStep1UpdateMode() {
    var prevValFinanziamento;
    var prevValIntervento;
    var prevValFase;
    var prevValAtto;
    var prevAnno;
    var prevNumero;
    var alertCambioDatiGiaDato = false;

    var nmTipoFinanziamentoOld = $("#Id_sisma_finanziamento").val(); // Acquisisce il vecchio valore che era su db
    $("#Id_sisma_finanziamento").focus(function () {
        prevValFinanziamento = $(this).val();
    }).change(function () {
        if (alertCambioDatiGiaDato === false && nmTipoFinanziamentoOld !== $(this).val()) {
            if (confirm("E' stata richiesta una modifica della linea di finanziamento, confermare? Nel caso di modifica tutti i documenti caricati sino a questo momento verranno cancellati.")) {
                alertCambioDatiGiaDato = true; // Così la prossima volta non lo chiede più!
                selezioneFinanziamento($(this));
                settaFlagModificato();
                return true;
            } else {
                $(this).val(prevValFinanziamento);
                return false;
            }
        } else {
            selezioneFinanziamento($(this));
        }
    });

    var interventoOld = $("#Id_sisma_progetti_ag").val(); // Acquisisce il vecchio valore che era su db
    $("#Id_sisma_progetti_ag").focus(function () {
        prevValIntervento = $(this).val();
    }).change(function () {
        if (alertCambioDatiGiaDato === false && interventoOld !== $(this).val()) {
            if (confirm("E' stata richiesta una modifica dell'Intervento, confermare? Nel caso di modifica tutti i documenti caricati sino a questo momento verranno cancellati.")) {
                alertCambioDatiGiaDato = true; // Così la prossima volta non lo chiede più!
                selezioneIntervento($(this));
                settaFlagModificato();
                return true;
            } else {
                $(this).val(prevValIntervento);
                return false;
            }
        } else {
            selezioneIntervento($(this));
        }
    });

    var faseOld = $("#Id_sisma_fase_progetto").val(); // Acquisisce il vecchio valore che era su db
    $("#Id_sisma_fase_progetto").focus(function () {
        prevValFase = $(this).val();
    }).change(function () {
        if (alertCambioDatiGiaDato === false && faseOld !== $(this).val()) {
            if (confirm("E' stata richiesta una modifica della fase progettuale, confermare? Nel caso di modifica tutti i documenti caricati sino a questo momento verranno cancellati.")) {
                alertCambioDatiGiaDato = true; // Così la prossima volta non lo chiede più!
                selezioneFase($(this));
                settaFlagModificato();
                return true;
            } else {
                $(this).val(prevValFase);
                return false;
            }
        } else {
            selezioneFase($(this));
        }
    });

    // Stato progetto
    $("#Id_sisma_stato_progetto").change(function () {
        selezioneStato($(this));
        settaFlagModificato();
    });

    var attoOld = $("#Id_sisma_val_atto").val(); // Acquisisce il vecchio valore che era su db
    $("#Id_sisma_val_atto").focus(function () {
        prevValAtto = $(this).val();
    }).change(function () {
        if (attoOld !== $(this).val()) {
            settaFlagModificato();
            $('#Ti_atto_out').html($('#Id_sisma_val_atto option:selected').text());
            return true;
        }
    });

    var nmAnnoOld = $('#Anno').val();
    $('#Anno').focus(function () {
        prevAnno = $(this).val();
    }).change(function () {
        if (nmAnnoOld !== $(this).val()) {
            settaFlagModificato();
            $('#Anno_out').html($('#Anno').val());
            return true;
        }
    });

    var nmNumeroOld = $('#Numero').val();
    $('#Numero').focus(function () {
        prevNumero = $(this).val();
    }).change(function () {
        if (nmNumeroOld !== $(this).val()) {
            settaFlagModificato();
            $('#Numero_out').html($('#Numero').val());
            return true;
        }
    });

}

function selezioneFinanziamento(dropDown) {
    var valore = $(dropDown).val();
    var testo = $(dropDown).find('option:selected').text();
    $.post("Sisma.html", {operation: "tipoFinanziamentoModificatoJson", idSismaFinanziamento: valore, dsTipoFinanziamento: testo}).done(function (data) {
        var arrayValori = Object.entries(data.map[0]);
        var dropDownIdSismaProgettiAg = $("#Id_sisma_progetti_ag");
        dropDownIdSismaProgettiAg.empty().append(new Option('', ''));
        arrayValori.forEach(function (item) {
            dropDownIdSismaProgettiAg.append(new Option(item[1], item[0].slice(1)));
        });
        $("#Ds_tipo_finanziamento_out").html(testo);
        $("#Ds_tipo_finanziamento_out_hidden").val(testo);
        $("#Codice_intervento_out").html('');
        $("#Denominazione_intervento_out").html('');
        $("#Ds_fase_sisma_out").html('');
        $("#Ds_stato_progetto_out").html('');
        $("#Id_sisma_fase_progetto").empty();
        $("#Id_sisma_stato_progetto").empty();
        $("#Cd_key_out").html('');
        $("#Cd_key_out_hidden").html('');
        $("#Oggetto_out").html('');
        $("#Oggetto").html('');
    });
}

function selezioneIntervento(dropDown) {
    var idSismaProgettiAg = $(dropDown).val();
    var idSismaFinanziamento = $('#Id_sisma_finanziamento').val();
    var testo = $(dropDown).find('option:selected').text();
    $.post("Sisma.html", {operation: "tipoInterventoModificatoJson", idSismaProgettiAg: idSismaProgettiAg, idSismaFinanziamento: idSismaFinanziamento}).done(function (data) {
        var oggetto = data.map[0];
        var arrayValori = Object.entries(oggetto.array);
        var dropDownIdSismaFase = $("#Id_sisma_fase_progetto");
        dropDownIdSismaFase.empty().append(new Option('', ''));
        arrayValori.forEach(function (val) {
            dropDownIdSismaFase.append(new Option(val[1], val[0].slice(1)));
        });
        $("#Codice_intervento_out").html(testo);
        $("#Denominazione_intervento_out").html(oggetto.denominazioneIntervento);

        // MAC#25468: Progetti ricostruzione: correzione modalità di valorizzazione dei dati dell'intervento
        $("#Ubicazione_comune_out").html(oggetto.ubicazioneComune);
        $("#Ubicazione_provincia_out").html(oggetto.ubicazioneProvincia);
        $("#Ente_proprietario_out").html(oggetto.enteProprietario);
        $("#Natura_ente_proprietario_out").html(oggetto.naturaEnteProprietario);
        //

        // Imposta il nuovo valore per il soggetto a tutela
        $("#Fl_intervento_soggetto_a_tutela").val(oggetto.flInterventoSoggettoATutela);
        $("#Id_sisma_stato_progetto").empty();
        $("#Ti_fase_sisma_out").html('');
        $("#Ti_fase_sisma_out_hidden").val('');
        $("#Ds_fase_sisma_out").html('');
        $("#Ds_fase_sisma_out_hidden").val('');
        $("#Ds_stato_progetto_out").html('');
        $("#Cd_key_out").html('');
        $("#Cd_key_out_hidden").html('');
        $("#Oggetto_out").html('');
        $("#Oggetto").html('');
    });
}

function selezioneFase(dropDown) {
    var idSismaFaseProgetto = $(dropDown).val();
    var testo = $(dropDown).find('option:selected').text();
    // Carica gli STATI
    $.post("Sisma.html", {operation: "tipoFaseModificataJson", idSismaFaseProgetto: idSismaFaseProgetto, descrizione: testo}).done(function (data) {
        var arrayValori = Object.entries(data.map[0]);
        var dropDownIdSismaStato = $("#Id_sisma_stato_progetto");
        dropDownIdSismaStato.empty().append(new Option('', ''));
        arrayValori.forEach(function (val) {
            dropDownIdSismaStato.append(new Option(val[1], val[0].slice(1)));
        });
        $("#Ti_fase_sisma_out").html(idSismaFaseProgetto);
        $("#Ti_fase_sisma_out_hidden").val(idSismaFaseProgetto);
        $("#Ds_fase_sisma_out").html(testo);
        $("#Ds_fase_sisma_out_hidden").val(testo);
        $("#Ds_stato_progetto_out").html('');
        $("#Cd_key_out").html('');
        $("#Cd_key_out_hidden").html('');
        $("#Oggetto_out").html('');
        $("#Oggetto").html('');
    });
}

function selezioneStato(dropDown) {
    var testo = $(dropDown).find('option:selected').text();
    $("#Ds_stato_progetto_out").html(testo);
}

function settaFlagModificato() {
    $('#Modificato').val('S');
    $('input[name="operation__salvaBozza"]').css('display', 'block');
}
