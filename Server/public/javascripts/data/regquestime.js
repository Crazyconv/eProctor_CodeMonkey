$(document).ready(function(){
    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "h+": this.getHours(),
            "m+": this.getMinutes()
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    $("#regForm").submit(function(){
        var options = {
            url:"/addregistration",
            type:"POST",
            dataType:"json",
            success: function(json){
                if(json.error!=0){
                    $("#registrationerror").text(json.error).slideDown();
                }else{
                    $('<li/>').text(json.courseCode).appendTo("#student" + json.studentId);
                    $("#registrationerror").hide();
                }
            },
            error: function(xhr,status){
                $("#registrationerror").text("Sorry... the form submission failed.").slideDown();
            }
        };
        $(this).ajaxSubmit(options);
        return false;
    });

    $("#questionForm").submit(function(){
        var options = {
            url:"/addquestion",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                if(form.content.value==""){
                    $("#questionerror").text("Please enter the question.").slideDown();
                    return false;
                }
                if(form.content.value.length > 5000){
                    $("#questionerror").text("The question exceeds 5000 characters.").slideDown();
                    return false;
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $("#questionerror").text(json.error).slideDown();
                }else{
                    $("#questionerror").hide();
                    $("#questionForm textarea").clearFields();
                    $('<li/>').text(json.content).appendTo("#course" + json.courseId);
                }
            },
            error: function(xhr,status){
                $("#questionerror").text("Sorry... the form submission failed.").slideDown();
            }
        };
        $(this).ajaxSubmit(options);
        return false;
    });

    $("#slotForm").submit(function(){
        var options = {
            url:"/allocateslot",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                if(form.capacity.value==""){
                    $("#sloterror").text("Please enter the capacity.").slideDown();
                    form.capacity.focus();
                    return false;
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $("#sloterror").text(json.error).slideDown();
                }else{
                    $("#sloterror").hide()
                    var date = new Date(json.start).Format("dd/MM/yyyy");
                    var start = new Date(json.start).Format("hh:mm");
                    var end = new Date(json.end).Format("hh:mm");
                    $('<li>' + date + ' ' + start + '-' + end + ' capacity:' + json.capacity +'</li>').appendTo("#slot" + json.courseId);
                    $("#slotForm input[type='text']").clearFields();
                }
            },
            error: function(xhr,status){
                $("#sloterror").text("Sorry... the form submission failed.").slideDown();
            }
        };
        $(this).ajaxSubmit(options);
        return false;
    });
});
