
var FF = {
    init: function(page) {
        $('.dropdown-toggle').dropdown();
        $('.modal').modal({
            backdrop: true,
            keyboard: true,
            show: false
        });

        $('select').each(function() {
            var v = $(this).attr("selectedvalue");
            $(this).val(v);
        });

        $('.modal-trigger').click(function(){
           var modalId = $(this).attr('href').substr(1);
            $('#' + modalId).modal('show');
        });
    }
};