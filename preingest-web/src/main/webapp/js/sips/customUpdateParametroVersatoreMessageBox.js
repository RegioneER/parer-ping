function CMessagesAlertBox() {
    $('.infoBox').dialog({
        autoOpen : true,
        width : 600,
        modal : false,
        resizable: false,
        dialogClass: "alertBox",
        closeOnEscape : true,
        buttons : {
            "Ok" : function() {
                $(this).dialog("close");
            }
        }
    });
    
    $('.pulsantieraUpdateParametroVersatore').hide();
    $('.customBoxUpdateParametroVersatore').dialog({
        autoOpen : true,
        width : 700,
        modal : true,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "OK" : function() {
                $(this).dialog("close");
                window.location = "Amministrazione.html?operation=confermaModificaValoreParametroVersatore&dsValoreParamMod=" + encodeURIComponent($('#Ds_valore_param').val());
            },
            "Annulla" : function() {
                $(this).dialog("close");
                window.location = "Amministrazione.html?operation=annullaModificaValoreParametroVersatore";
            }
        }
    });
    
    $('.errorBox').dialog({
        autoOpen : true,
        width : 600,
        modal : true,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "Ok" : function() {
                $(this).dialog("close");
            }
        }
    });
}