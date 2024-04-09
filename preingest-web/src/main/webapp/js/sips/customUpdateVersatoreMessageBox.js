/* 
 * Override di CMessagesAlertBox() in classes.js per mostrare il warning relativo all'action 
 * AmministrazioneAction costituito da 3 bottoni
 */
function CMessagesAlertBox() {
    $('.infoBox').dialog({
        autoOpen : true,
        width : 600,
        modal : false,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "Ok" : function() {
                $(this).dialog("close");
            }
        }
    });
    
    $('.pulsantieraMB').hide();
    $('.customBox').dialog({
        autoOpen : true,
        width : 600,
        modal : true,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "Si" : function() {
                $(this).dialog("close");                
                window.location = "Amministrazione.html?operation=confermaModificheVersatore&table="+$("input[name='table']").attr("value")+"&back="+$("input[name='back']").attr("value");
            },
            "No" : function() {
                $(this).dialog("close");                
                window.location = "Amministrazione.html?operation=annullaModificheVersatore&table="+$("input[name='table']").attr("value")+"&back="+$("input[name='back']").attr("value");
            }
        }
    });
       
    $('.pulsantieraSalvataggioVers').hide();
    $('.customBoxSalvataggioVers').dialog({
        autoOpen: true,
        width: 600,
        modal: true,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Si": function () {
                $(this).dialog("close");
                window.location = "Amministrazione.html?operation=confermaEseguiModificaSalvataggioVersatore";
            },
            "No": function () {
                $(this).dialog("close");
                window.location = "Amministrazione.html?operation=annullaEseguiModificaSalvataggioVersatore";
            }
        }
    });
    
    $('.warnBox').dialog({
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