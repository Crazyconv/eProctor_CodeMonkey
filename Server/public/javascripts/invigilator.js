$(document).ready(function(){
    //for css
//    $('tr').mouseenter(function(){
//        $(this).addClass("info");
//    });
//    $('tr').mouseleave(function(){
//        $(this).removeClass("info");
//    });

    //load the schedule for the current date
    //for demo purpose, the date is set to be 2014/5/1
    //var nowDate = new Date();
    var nowDate = new Date(2014,4,1);
    displayschedule(nowDate);

    function displayschedule(date){
        $.ajax({
            url:"/displayschedule",
            type:"POST",
            data:{date: date.getTime()},
            dataType:"html",
            success:function(html){
                $('#scheduleerror').html('<br/>');
                $('#timetable').html(html);
            },
            error:function(xhr,status){
                $('#scheduleerror').text("Message and image polling error.").show();
            }
        });
    }


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

    $(document).on('click','#pick',function(){
        var dateString = $('#date').get(0).date.value;
        var date = new Date(dateString);
        displayschedule(date);
    });
});
