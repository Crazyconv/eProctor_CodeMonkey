$(document).ready(function(){
    var start = false;
    var timerEvent;
    var toSent = false;
    function timerTask(){
        if(toSent){
            if(start==false){
                if(grab.start()){
                    start = true;
                    //$('<li></li>').text("start").appendTo('ul');
                }
            }else{
                var imageString = grab.grab();
                if(imageString!=""){
                    $('div').html('<img src="data:image/jpeg;base64,' + imageString + '" />');
                }
            }
            timerEvent = setTimeout(timerTask,500);
        }
    }
    function startTimer(){
        toSent = true;
        timerEvent = setTimeout(timerTask,500);
    }
    function stopTimer(){
        toSent = false;
        clearTimeout(timerEvent);
        grab.stop();
    }

    $('#button1').click(function(){
        startTimer();
    });

    $('#button2').click(function(){
        stopTimer();
    });
});