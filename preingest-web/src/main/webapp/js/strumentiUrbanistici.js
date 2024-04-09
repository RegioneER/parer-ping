function initWizardStep1() {
    // Attiva il sensore di modifica per alcuni campi della form 
    $('#Data, #Ds_descrizione, #IdentificativoCollegato1, #IdentificativoCollegato2, #AnnoCollegato1, #AnnoCollegato2').change(settaFlagModificato);

    $("#AnnoCollegato1").change(function () {
        // Pulisce i campi precedentemente ed eventualmente valorizzati, anche quelli nascosti
        $("#IdentificativoCollegato1").prop('selectedIndex', 0);
    });

    $("#AnnoCollegato2").change(function () {
        // Pulisce i campi precedentemente ed eventualmente valorizzati, anche quelli nascosti
        $("#IdentificativoCollegato2").prop('selectedIndex', 0);
    });

    // All'inizio in modifica/inserimento il pulsante salva bozza viene spento
    if ($('#Modificato').val() != 'S') {
        $('input[name="operation__salvaBozza"]').css('display', 'none');
    }
}

function initWizardStep1InsertMode() {
    // Negli altri casi attiva il trigger per la selezione del tipo SU
    $("#Nm_tipo_strumento_urbanistico").change(function () {
        selezioneStrumentoUrbanistico($(this));
        settaFlagModificato();
    });
    $("#Ti_fase_strumento").change(function () {
        attivazioneCollegamentiPerFase();
        settaFlagModificato();
    });
    $("#Ti_atto").change(function () {
        let tiAttoOut = $("#Ti_atto option:selected").text() ? $("#Ti_atto option:selected").text() : '';
        $("#Ti_atto_out").html(tiAttoOut);
        settaFlagModificato();
    });

    $('#Anno').change(() => {
        settaFlagModificato();
        $('#Anno_out').html($('#Anno').val());
    });

    $('#Numero').change(settaFlagModificato);
}

function initWizardStep1UpdateMode() {
    var prevValTipo;
    var prevValFase;
    var prevValAtto;
    var prevAnno;
    var prevNumero;
    var alertCambioDatiGiaDato = false;
    var nmTipoStrumentoOld = $("#Nm_tipo_strumento_urbanistico").val(); // Acquisisce il vecchio valore che era su db
    $("#Nm_tipo_strumento_urbanistico").focus(function () {
        prevValTipo = $(this).val();
    }).change(function () {
        if (alertCambioDatiGiaDato === false && nmTipoStrumentoOld !== $(this).val()) {
            if (confirm("E' stata richiesta una modifica di tipologia di Strumento Urbanistico, confermare? \n Modificando il tipo strumento i tipi documento caricati saranno eliminati.")) {
                alertCambioDatiGiaDato = true; // Così la prossima volta non lo chiede più!
                pulisceCollegamenti();
                selezioneStrumentoUrbanistico($(this));
                settaFlagModificato();
                return true;
            } else {
                $(this).val(prevValTipo);
                return false;
            }
        } else {
            pulisceCollegamenti();
            selezioneStrumentoUrbanistico($(this));
        }
    });

    var nmTipoFaseOld = $("#Ti_fase_strumento").val(); // Acquisisce il vecchio valore che era su db
    $("#Ti_fase_strumento").focus(function () {
        prevValFase = $(this).val();
    }).change(function () {
        if (alertCambioDatiGiaDato === false && nmTipoFaseOld !== $(this).val()) {
            if (confirm("E' stata richiesta una modifica della Fase elaborazione strumento, confermare? \n Modificando la Fase elaborazione strumento i tipi documento caricati saranno eliminati.")) {
                alertCambioDatiGiaDato = true; // Così la prossima volta non lo chiede più!
                pulisceCollegamenti();
                attivazioneCollegamentiPerFase();
                settaFlagModificato();
                return true;
            } else {
                $(this).val(prevValFase);
                return false;
            }
        } else {
            pulisceCollegamenti();
            attivazioneCollegamentiPerFase();
        }
    });

    var nmTipoAttoOld = $("#Ti_atto").val(); // Acquisisce il vecchio valore che era su db
    $("#Ti_atto").focus(function () {
        prevValAtto = $(this).val();
    }).change(function () {
        if (nmTipoAttoOld !== $(this).val()) {
            pulisceCollegamenti();
            attivazioneCollegamentiPerFase();
            settaFlagModificato();
            let tiAttoOut = $( "#Ti_atto option:selected" ).text()? $( "#Ti_atto option:selected" ).text() : '';
            $("#Ti_atto_out").html(tiAttoOut);
            return true;
        } else {
            pulisceCollegamenti();
            attivazioneCollegamentiPerFase();
            let tiAttoOut = $("#Ti_atto option:selected").text() ? $("#Ti_atto option:selected").text() : '';
            $("#Ti_atto_out").html(tiAttoOut);
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
            return true;
        }

    });
}

function selezioneStrumentoUrbanistico(dropDown) {
    var appo = $(dropDown).val();
    $.post("StrumentiUrbanistici.html", {operation: "tipoStrumentoModificatoJson", nmTipoStrumento: appo}).done(function (data) {
        spegniCollegamenti(true); // Cancella e spegne i collegamenti
        var arrayValori = Object.values(data.map[0]);
        var dropDownTiFaseStrumento = $("#Ti_fase_strumento");
        dropDownTiFaseStrumento.empty();
        dropDownTiFaseStrumento.append(new Option('', ''));
        arrayValori.forEach(function (item, index) {
            dropDownTiFaseStrumento.append(new Option(item, item));
        });
        $("#Nm_tipo_strumento_urbanistico_out").html(appo);
        $("#Nm_tipo_strumento_urbanistico_out_hidden").val(appo);
        var tiFaseStrumento = $("#Ti_fase_strumento").val();
        $("#Ti_fase_strumento_out").html(tiFaseStrumento);
        $("#Ti_fase_strumento_out_hidden").val(tiFaseStrumento);
        $("#Cd_key_out").html('');
        $("#Cd_key_out_hidden").html('');
        $("#FaseCollegata1_fake").html('');
        $("#FaseCollegata2_fake").html('');
        $('#FaseCollegata1, #FaseCollegata2').val('');
        pulisceCollegamenti();
    });
}

function pulisceCollegamenti() {
    $('#AnnoCollegato1, #AnnoCollegato2').each(function () {
        $(this).val('');
    });
    $('#IdentificativoCollegato1, #IdentificativoCollegato2').empty();
}

function attivazioneCollegamentiPerFase() {
    let tiFaseStrumento = $("#Ti_fase_strumento").val();
    $("#Ti_fase_strumento_out").html(tiFaseStrumento);
    $("#Ti_fase_strumento_out_hidden").val(tiFaseStrumento);
    if (!tiFaseStrumento) {
        spegniCollegamenti(true);
    } else {
        $("#FaseCollegata1_fake").html('');
        $("#FaseCollegata2_fake").html('');
        $('#FaseCollegata1, #FaseCollegata2').val('');

        let fasi = $("#Ti_fase_strumento").find('option').filter((index, element) =>
            $(element).val() != "" && $(element).val() != tiFaseStrumento
        );
        if (fasi[0]) {
            $("#FaseCollegata1_fake").html($(fasi[0]).val());
            $("#FaseCollegata1").val($(fasi[0]).val());

            getAnniForFaseCollegata($(fasi[0]).val(), "#AnnoCollegato1");
        }
        if (fasi[1]) {
            $("#FaseCollegata2_fake").html($(fasi[1]).val());
            $("#FaseCollegata2").val($(fasi[1]).val());

            getAnniForFaseCollegata($(fasi[1]).val(), "#AnnoCollegato2");
        }
        spegniCollegamenti(false); // accende collegamenti
    }
    pulisceCollegamenti();
}

// Funzione per valorizzare le fasi collegate in base ai dati già presenti
function attivazioneCollegamentiPerFaseModifica() {
    let fase = $("#Ti_fase_strumento").val();
    if (!fase) {
        spegniCollegamenti(true);
    } else {
        let faseCollegata1 = $("#FaseCollegata1").val();
        let faseCollegata2 = $("#FaseCollegata2").val();
        $("#FaseCollegata1_fake").html(faseCollegata1);
        $("#FaseCollegata2_fake").html(faseCollegata2);
        let fasi = $("#Ti_fase_strumento").find('option').filter((index, element) => !['', fase, faseCollegata1, faseCollegata2].includes($(element).val()));
        if (fasi.length === 1) {
            if (!faseCollegata1) {
                $("#FaseCollegata1_fake").html($(fasi[0]).val());
                $("#FaseCollegata1").val($(fasi[0]).val());
            } else {
                $("#FaseCollegata2_fake").html($(fasi[0]).val());
                $("#FaseCollegata2").val($(fasi[0]).val());
            }
        } else if (fasi.length === 2) {
            $("#FaseCollegata1_fake").html($(fasi[0]).val());
            $("#FaseCollegata1").val($(fasi[0]).val());
            $("#FaseCollegata2_fake").html($(fasi[1]).val());
            $("#FaseCollegata2").val($(fasi[1]).val());
        }

        //MEV 29495 - riempi le combo degli anni delle fasi solo con gli anni realmmìente disponibili.
        if ($("#FaseCollegata1").val()) {
            getAnniForFaseCollegata($("#FaseCollegata1").val(), "#AnnoCollegato1");
        }
        if ($("#FaseCollegata2").val()) {
            getAnniForFaseCollegata($("#FaseCollegata2").val(), "#AnnoCollegato2");
        }
        
        spegniCollegamenti(false); // accende collegamenti
    }
}

function spegniCollegamenti(flag) {
    if (flag) {
        $("#FaseCollegata1_fake, #FaseCollegata2_fake, #AnnoCollegato1, #AnnoCollegato2, #IdentificativoCollegato1, #IdentificativoCollegato2").css('display', 'none'); // Disattiva tutto
    } else {
        // Se c'è solo una fase collegata tiene sempre spenta la fase e collegata!
        var faseCollegata2 = $("#FaseCollegata2").val();
        if (faseCollegata2) {
            $("#FaseCollegata2_fake, #AnnoCollegato2, #IdentificativoCollegato2").css('display', 'block'); // Riattiva tutto link 2
        } else {
            $("#FaseCollegata2_fake, #AnnoCollegato2, #IdentificativoCollegato2").css('display', 'none'); // Disattiva tutto della fase 2
        }

        $("#FaseCollegata1_fake, #AnnoCollegato1, #IdentificativoCollegato1").css('display', 'block'); // Riattiva link 1  
    }
}

function settaFlagModificato() {
    $('#Modificato').val('S');
    $('input[name="operation__salvaBozza"]').css('display', 'block');
}

function getAnniForFaseCollegata(fase, idComboAnno) {
    $.get("StrumentiUrbanistici.html", {operation: "faseCollagataModificataJson", nmTipoStrumento: $("#Nm_tipo_strumento_urbanistico_out_hidden").val(), nmFaseCollegata: fase}).done(function (data) {
        var arrayValori = Object.values(data.map[0]);
        var dropDownAnnoCollegato = $(idComboAnno);
        var oldSelectedValue = dropDownAnnoCollegato.find(":selected").val();
        dropDownAnnoCollegato.empty();
        dropDownAnnoCollegato.append(new Option('', '', true));
        arrayValori.forEach(function (item, index) {
            if (item === oldSelectedValue) {
                dropDownAnnoCollegato.append(new Option(item, item, false, true));
            }
            else {
                dropDownAnnoCollegato.append(new Option(item, item, false, false));  
            }
        });
    });
}

