$(document).ready(function(){
    //load the schedule for the current date
    //for demo purpose, the date is set to be 2014/5/1
    //var nowDate = new Date();
    var nowDate = new Date(2014,4,1);
    $.ajax({
        url:"/displayschedule",
        type:"POST",
        data:{date: nowDate.getTime()},
        dataType:"html",
        success:function(html){
            $('#scheduleerror').html('<br/>');
            $('#timetable').html(html);
        },
        error:function(xhr,status){
            $('#scheduleerror').text("Message and image polling error.").show();
        }
    });

    $(document).on('click','button[name="signin"]',function(){
        var $form = $(this).parent('form');
        var options={
            url:"/signin",
            type:"POST",
            dataType:"html",
            success: function(html){
                $('#scheduleerror').html('<br/>');
                $('#schedule').hide();
                $('#invigilate').html(html);
            },
            error: function(xhr,status){
                $('#scheduleerror').html("Sorry... the form submission failed.").show();
            }
        };
        $form.ajaxSubmit(options);
    });
});
