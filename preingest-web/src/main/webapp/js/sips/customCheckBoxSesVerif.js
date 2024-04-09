/* Sovrascrivo la funzione CCheckBoxHandlerLoad
 per ridefinirne il comportamento in base alle mie esigenze:
 se checko la check "madre", checcka tutto
 se dececco la check "madre", riporta la situazione come era
 */
function CCheckBoxHandlerLoad() {

    $('table.list th.cbth').html(
            function(index, html) {
                return '<span style="display: block;"><input name="' + this.id + '" type="checkbox" />' + html + '</span>';
            });

    $('table.list th input[type="checkbox"][name="Fl_verif"]').click(
            function() {
                if (this.checked) {
                    $('table.list td > input[name="' + this.name + '"]').attr('checked', true);
                } else {
                    $('table.list td > input[name="' + this.name + '"]').removeAttr('checked');
                    $('table.list td > input[name="' + this.name + '"]').filter(checkVerificati).attr('checked', true);
                }
            });

    $('table.list th input[type="checkbox"][name="Fl_non_risolub"]').click(
            function() {
                if (this.checked) {
                    $('table.list td > input[name="' + this.name + '"]').attr('checked', true);
                } else {
                    $('table.list td > input[name="' + this.name + '"]').removeAttr('checked');
                    $('table.list td > input[name="' + this.name + '"]').filter(checkNonRisolubili).attr('checked', true);
                }
            });

    $('table.list th input[type="checkbox"][name="Select_msg"]').click(
            function() {
                if (this.checked) {
                    $('table.list td > input[name="' + this.name + '"]').attr('checked', true);
                } else {
                    $('table.list td > input[name="' + this.name + '"]').removeAttr('checked');
                    $('table.list td > input[name="' + this.name + '"]').filter(checkSelected).attr('checked', true);
                }
            });
}

