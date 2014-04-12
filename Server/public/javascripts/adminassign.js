$('document').ready(function(){
    $('button[name="assign"]').click(function(){
        var $form = $(this).parent('form');
        var options={
            url:"/toggleassign",
            type:"POST",
            dataType:"html",
            success: function(html){
                $('#content').html(html);
                $('#myModal').modal(
                    'toggle'
                );
            },
            error: function(xhr,status){
                $('#content').html("Internal server error");
                $('#myModal').modal(
                    'toggle'
                );
            }
        };
        $form.ajaxSubmit(options);
    });
});
