/* 
 * Override di CMessagesAlertBox() in classes.js
 */
function CMessagesAlertBox() {
    ////////////////////////
    // MessageBox di INFO //
    ////////////////////////
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

    //////////////////////////////
    // MessageBox AMBIENTE VERS //
    //////////////////////////////
    $('.pulsantieraSalvataggioAmbienteVers').hide();
    $('.customBoxSalvataggioAmbienteVersControllo1').dialog({
        autoOpen: true,
        width: 600,
        modal: true,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Si": function () {
                $(this).dialog("close");
                window.location = "Amministrazione.html?operation=secondCheckChangeStatusAndSaveAmbienteVers";
            },
            "No": function () {
                $(this).dialog("close");
                window.location = "Amministrazione.html?operation=annullaEseguiModificaSalvataggioAmbienteVersatore";
            }
        }
    });
    $('.customBoxSalvataggioAmbienteVersControllo2').dialog({
        autoOpen: true,
        width: 600,
        modal: true,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Si": function () {
                $(this).dialog("close");
                window.location = "Amministrazione.html?operation=confermaEseguiModificaSalvataggioAmbienteVersatore";
            },
            "No": function () {
                $(this).dialog("close");
                window.location = "Amministrazione.html?operation=annullaEseguiModificaSalvataggioAmbienteVersatore";
            }
        }
    });
   

    ///////////////////////
    // WARNING ED ERRORE //
    ///////////////////////
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
