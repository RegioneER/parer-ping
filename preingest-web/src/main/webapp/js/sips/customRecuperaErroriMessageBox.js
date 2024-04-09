/* 
 * Override di CMessagesAlertBox() in classes.js per mostrare la finestrella di creazione strutture template
 * dipendenti dall'ambiente selezionato
 */
function CMessagesAlertBox() {
    $('.infoBox').dialog({
        autoOpen: true,
        width: 600,
        modal: false,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Ok": function () {
                $(this).dialog("close");
            }
        }
    });

    $('.pulsantieraRecuperoErrori').hide();    
    $('.customBoxRecuperoErrori').dialog({
        autoOpen: true,
        width: 600,
        modal: true,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Salva": function () {            
                $(this).dialog("close");                
                //$.post('../../struttura/strutturaRicerca.jsp', {idAmbienteStruttureTemplate:"2"});                
                window.location = "StrumentiUrbanistici.html?operation=confermaRecuperoErrore&isFromJavaScript=true&ti_nuovo_stato=" + $('#Ti_nuovo_stato').find('option:selected').val();
            },
            "Annulla": function () {
                $(this).dialog("close");
            }
        }
    })

    $('.warnBox').dialog({
        autoOpen: true,
        width: 600,
        modal: true,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Ok": function () {
                $(this).dialog("close");
            }
        }
    });

    $('.errorBox').dialog({
        autoOpen: true,
        width: 600,
        modal: true,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Ok": function () {
                $(this).dialog("close");
            }
        }
    });
}