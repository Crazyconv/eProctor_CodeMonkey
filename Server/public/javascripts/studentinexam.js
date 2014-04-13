$(document).ready(function(){

    //poll examStatus, namely, whether the student has been verified
    function checkVerification(examRecordId){
        return function(){
            $.ajax({
                url:"/checkexamstatus",
                type:"POST",
                data:{examRecordId: examRecordId},
                dataType:"json",
                success:function(json){
                    if(json.error!=0){
                        $('#wait').text(json.error);
                    }else{
                        if(json.examStatus == 2){
                            $('<p>You identity has been verified.</p>').appendTo($('#ready'));
                            $('#wait').text("");
                            $('#hiddenquestion').removeClass('hidden');
                            var period = $('#period').html();
                            if(period>0 && period<2*24*60*60*1000){
                                setTimeout(finishExam,period);
                            }
                        }else if(json.examStatus == 3){
                            stopTimer(examRecordId);
                            $('<p>Identity verification failed, please appeal to your school.</p>').appendTo($('#ready'));
                            $('#wait').text("");
                        }else{
                            setTimeout(checkVerification(examRecordId),5000);
                        }
                    }
                },
                error:function(xhr,status){
                    $('#wait').text("Internal server error");
                }
            });
        }
    }

    //poll message
    var toPoll = false;
    var pollEvent;
    function pollMessage(examRecordId,lastChatId){
        return function(){
            if(toPoll){
                $.ajax({
                    url:"/pollmessage",
                    type:"POST",
                    data:{examRecordId: examRecordId, lastChatId: lastChatId},
                    dataType:"json",
                    success:function(json){
                        if(json.error!=0){
                            $('#wait').text(json.error);
                        }else{
                            lastChatId = json.lastChatId;
                            $.each(json.chatList, function( i, chat ){
                                if(chat.fromStudent){
                                    $('<p class="student">' + chat.message + '</p>').appendTo($('#history'));
                                }else{
                                    $('<p class="invigilator">' + chat.message + '</p>').appendTo($('#history'));
                                }
                                $('#history').get(0).scrollTop = $('#history').get(0).scrollHeight;
                            });
                            pollEvent = setTimeout(pollMessage(examRecordId,lastChatId),1000);
                        }
                    },
                    error:function(xhr,status){
                        $('#wait').text("Internal server error");
                    }
                });
            }
        }
    }
    function startPoll(examRecordId,lastChatId){
        toPoll = true;
        pollEvent = setTimeout(pollMessage(examRecordId,lastChatId),1000);
    }
    function stopPoll(){
        toPoll = false;
        clearTimeout(pollEvent);
    }

    //sign in -> start grabbing image
    var startCamera = false;
    var timerEvent;
    var toSent = false;
    function timerTask(examRecordId){
        return function(){
            if(toSent){
                if(startCamera==false){
                    if(grab.start()){
                        startCamera = true;
                        $('<p>Camera is ready!</p>').appendTo($('#ready'));
                        $('#wait').text("Please wait for the invigilator to verify your identity");
                    }
                }else{
                    var imageString = grab.grab();
                    if(imageString!=""){
                        $('#video').html('<img src="data:image/jpeg;base64,' + imageString + '" />');
                        $.ajax({
                            url:"/storeimage",
                            type:"POST",
                            data:{image: imageString, examRecordId: examRecordId},
                            dataType:"json",
                            success:function(json){
                                if(json.error!=0){
                                    $('#wait').text("Video connection error.");
                                }
                            },
                            error:function(xhr,status){
                                $('#wait').text("Internal server error");
                            }
                        });
                    }
                }
                timerEvent = setTimeout(timerTask(examRecordId),500);
            }
        }
    }
    function startTimer(examRecordId){
        toSent = true;
        timerEvent = setTimeout(timerTask(examRecordId),500);
    }
    function stopTimer(){
        if(startCamera==true){
            toSent = false;
            clearTimeout(timerEvent);
            startCamera = false;
            grab.stop();
        }
    }

    //sign in
    $(document).on('click','button[name="signin"]',function(){
        var $form = $(this).parent('form');
        var examRecordId = $form.get(0).examRecordId.value;
        var options={
            url:"/takeexam",
            type:"POST",
            dataType:"html",
            success: function(html){
                $('#beforeexam').toggle();
                $('#inexam').html(html);
                setTimeout(checkVerification(examRecordId),5000);
                startTimer(examRecordId);
                startPoll(examRecordId,0);
                app.lockApp();
            },
            error: function(xhr,status){
                $("#selecterror").text("Internal server error").show();
            }
        };
        $form.ajaxSubmit(options);
    });

    //save answer
    $(document).on('click','button[name="saveanswer"]',function(){
        var $button = $(this);
        var $form = $button.parent('form');
        var $div = $form.parent('a');

        var options={
            url:"/saveanswer",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                if(form.answer.value==""){
                    $form.find('span[name="answererror"]').text("The answer field is empty.").slideDown();
                    return false;
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $form.find('span[name="answererror"]').text(json.error).show();
                }else{
                    $form.find('span[name="answererror"]').hide();
                    $button.attr("disabled","disabled");
                    $form.find('span[name="answersuccess"]').text("Saved!").slideDown();
                    setTimeout(function(){$form.find('span[name="answersuccess"]').hide();},2000);
                }
            },
            error: function(xhr,status){
                $form.find('span[name="answererror"]').text("Internal server error").show();
            }
        };
        $form.ajaxSubmit(options);
    });

    $(document).on('keypress','textarea',function(){
        var button = this.form.saveanswer;
        if(button!=null){
            button.disabled = false;
        }
    });

    //send message
    $(document).on('click','input[name="sendmessage"]',function(){
        var $form = $(this).parent('form');

        var options={
            url:"/sendmessage",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                if(form.message.value==""){
                    return false;
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $('#wait').text(json.error);
                }else{
                    $form.find('textarea').clearFields();
                }
            },
            error: function(xhr,status){
                $('#wait').text("Internal server error");
            }
        };
        $form.ajaxSubmit(options);
    });

    //click "finish exam" to finish exam in advance
    //action for "finish" button is written in html
    //a. click "confirm"
    $(document).on('click','button[name="confirmfinish"]',function(){
        var $form = $(this).parent('form');
        var examRecordId = $form.get(0).examRecordId.value;
        var options={
            url:"/finishexam",
            type:"POST",
            dataType:"json",
            success: function(json){
                if(json.error!=0){
                    $('#finisherror').text(json.error).show();
                    $('button[name="finish"]').popover('hide');
                }else{
                    $('#inexam').text("");
                    $('#beforeexam').show();
                    app.unlockApp();
                    stopTimer();
                    stopPoll();
                }
            },
            error: function(xhr,status){
                $('#finisherror').text('Internal server error').show();
            }
        };
        $form.ajaxSubmit(options);
    });
    //b. click "cancel"
    $(document).on('click','button[name="back"]',function(){
        $('button[name="finish"]').popover('hide');
    });

    function finishExam(){
        $('button[name="confirmfinish"]').trigger('click');
    }
});
