$(document).ready(function(){
    $('input[type=button][name="edit"]').click(function(){
        var $form = $(this).parent('form');
        var courseId = $form.get(0).courseId.value;
        var options={
            url:"/showslot",
            type:"POST",
            dataType:"html",
            success: function(html){
                $('#select' + courseId).html(html).show();
                $('#slot'+courseId).hide();
            },
            error: function(xhr,status){
                $("#selecterror").text("Sorry... the form submission failed.").show();
            }
        };
        $form.ajaxSubmit(options);
    });

    $(document).on('submit','form',function(){
        var courseId = $(this).get(0).courseId.value;
        var options = {
            url:"/selectslot",
            type:"POST",
            dataType:"json",
            success: function(json){
                if(json.error!=0){
                    $("#selecterror").text(json.error).show();
                }else{
                    $("#selecterror").hide();
                    $('#select'+courseId).hide();
                    $('#slot'+courseId).text(json.date + ' ' + json.start + ' ' + json.end).show();
                    $('#remove'+courseId).attr("disabled",false);
                }
            },
            error: function(xhr,status){
                $("#selecterror").text("Sorry... the form submission failed.").show();
            }
        };
        $(this).ajaxSubmit(options);
        return false;
    });

    $(document).on('click','input[name="cancel"]',function(){
        var courseId = $(this).parent('form').get(0).courseId.value;
        $('#select'+courseId).hide();
        $('#slot'+courseId).show();
    });

    $(document).on('click','input[name="remove"]',function(){
        var $form = $(this).parent('form');
        var courseId = $form.get(0).courseId.value;
        var options={
            url:"/deleteslot",
            type:"POST",
            dataType:"json",
            success: function(json){
                if(json.error!=0){
                    $("#selecterror").text(json.error).show();
                }else{
                    $("#selecterror").hide();
                    $('#slot'+courseId).text("");
                    $('#remove'+courseId).attr("disabled",true);
                }
            },
            error: function(xhr,status){
                $("#selecterror").text("Sorry... the form submission failed.").show();
            }
        };
        $form.ajaxSubmit(options);
    });
});
