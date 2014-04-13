$(document).ready(function(){
    $('div[name="remarkheader"]').parent('div').find('.panel-body').hide();

    //toggle chat window and remark form
    $(document).on("click",'div[name="comm_header"]',function(){
        $(this).parent('div').find('.panel-body').toggle();
    });
    $(document).on("click",'div[name="remarkheader"]',function(){
        $(this).parent('div').find('.panel-body').toggle();
    });

    //update message and image
    function updateMessageImage(examRecordId,chatList,imageId){
        if(chatList!=null){
            $.each(chatList, function(index,chat){
                var messagehistory = $('#'+examRecordId).find(".history");
                if(chat.fromStudent){
                    $('<p class="student">' + chat.message + '</p>').appendTo(messagehistory);
                }else{
                    $('<p class="invigilator">' + chat.message + '</p>').appendTo(messagehistory);
                }
                messagehistory[0].scrollTop = messagehistory[0].scrollHeight;
            });
        }
        if(imageId!=null){
            var _img = $('#'+examRecordId).find(".video").find("img").get(0);
            var newImg = new Image;
            newImg.src = '/getimage/'+imageId;
            newImg.onload = function() {
                _img.src = this.src;
            }
        }
    }

    //poll message and image
    function pollMessageImage(examRecordIds,lastChatId,lastImageId){
        return function(){
            $.ajax({
                url:"/pollmessageimage",
                type:"POST",
                data:{examRecordIds: examRecordIds, lastChatId: lastChatId, lastImageId: lastImageId},
                dataType:"json",
                success:function(json){
                    if(json.error!=0){
                        $('span[name="pollerror"]').text(json.error).show();
                    }else{
                        $('span[name="pollerror"]').hide();
                        lastChatId = json.lastChatId;
                        lastImageId = json.lastImageId;
                        $.each( json, function( key, value ) {
                            if(/^[0-9]+$/.test(key)){
                                updateMessageImage(key,value.chatList,value.imageId);
                            }
                        });
                        setTimeout(pollMessageImage(examRecordIds,lastChatId,lastImageId),1000);
                    }
                },
                error:function(xhr,status){
                    $('span[name="pollerror"]').text("Message and image polling error.").show();
                }
            });
        }
    }

    var examRecordIdsInfo = document.getElementsByName("forjs");
    var examRecordIdsArray = new Array(examRecordIdsInfo.length);
    for(var i=0;i<examRecordIdsInfo.length;i++){
        examRecordIdsArray[i] = examRecordIdsInfo[i].id;
        setTimeout(checkExamStatus(examRecordIdsArray[i]),5000);
    }
    var examRecordIds = examRecordIdsArray.join(",");
    console.log(examRecordIds);
    setTimeout(pollMessageImage(examRecordIds,0,0),1000);


    //verify student identity
    $(document).on('click','input[name="verify"]',function(){
        var $form = $(this).parent('form');
        var $div = $form.parent('div');

        var options={
            url:"/verifystudent",
            type:"POST",
            dataType:"json",
            success: function(json){
                if(json.error!=0){
                    $('<p class="text-danger">' + json.error + '</p>').appendTo($div);
                }else{
                    $form.hide();
                    $('<p class="text-success">Verification result has been sent!</p>').appendTo($div);
                }
            },
            error: function(xhr,status){
                $('<p class="text-danger">Sorry... the form submission failed.</p>').appendTo($div);
            }
        };
        $form.ajaxSubmit(options);
    });

    //send message
    $(document).on('click','input[name="sendmessage"]',function(){
        var $form = $(this).parent('form');

        var options={
            url:"/invigilatormessage",
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
                    $('.history').find('span[name="messageerror"]').text(json.error).show();
                }else{
                    $('.history').find('span[name="messageerror"]').hide();
                    $form.find('input[name="message"]').clearFields();
                }
            },
            error: function(xhr,status){
                $('.history').find('span[name="messageerror"]').text("Sorry... the form submission failed.").show();
            }
        };
        $form.ajaxSubmit(options);
    });

    //submit remark
    $(document).on('click','input[name="submitremark"]',function(){
        var $form = $(this).parent('form');

        var options={
            url:"/submitremark",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                if(form.remark.value==""){
                    $form.find('span[name="remarkerror"]').text("Please fill the remark form").show();
                    return false;
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $form.find('span[name="remarkerror"]').text(json.error).show();
                }else{
                    $form.find('span[name="remarkerror"]').text("");
                    $form.find('span[name="remarksuccess"]').text("Remark sent!").show();
                    $form.find('input[name="submitremark"]').attr("disabled","disabled");
                }
            },
            error: function(xhr,status){
                $form.find('span[name="remarkerror"]').text("Sorry... the form submission failed.").show();
            }
        };
        $form.ajaxSubmit(options);
    });

    //finish invigilation
    $('button[name="finish"]').popover({
        html:true,
        content: function(){
            return $('#confirmation').html();
        }
    });

    $(document).on('click','button[name="confirmfinish"]',function(){
        $('#invigilate').text("");
        $('#schedule').show();
    });
    //b. click "cancel"
    $(document).on('click','button[name="back"]',function(){
        $('button[name="finish"]').popover('hide');
    });

    //check whether student has finished exam
    function checkExamStatus(examRecordId){
        return function(){
            $.ajax({
                url:"/checkexamstatus",
                type:"POST",
                data:{examRecordId: examRecordId},
                dataType:"json",
                success:function(json){
                    if(json.error!=0){
                        $('<p class="text-danger">' + json.error + '</p>').appendTo($('#info'+examRecordId));
                    }else{
                        if(json.examStatus == 4){
                            $('<p class="text-danger">Student has left exam</p>').appendTo($('#info'+examRecordId));
                        }else{
                            setTimeout(checkExamStatus(examRecordId),1000);
                        }
                    }
                },
                error:function(xhr,status){
                    $('<p class="text-danger">Internal server error.</p>').appendTo($('#info'+examRecordId));
                }
            });
        }
    }
});