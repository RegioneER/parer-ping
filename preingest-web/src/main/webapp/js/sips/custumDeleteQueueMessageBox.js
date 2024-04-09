/* 
 * Override di CLinkHandlerLoad() in classes.js per mostrare il messaggio relativo
 * alla cancellazione di un messaggio in coda
 */
/*
 * Link Handler
 */
function CLinkHandlerLoad() {
    $('table.list td  > a[href*="navigationEvent=delete"]').click(function() {
        var res = true;
        res = confirm('Sei sicuro di voler eliminare il messaggio in coda?');
        if (!res) {
            return false;
        }
    });
}
