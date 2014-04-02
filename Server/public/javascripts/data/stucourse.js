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

    $("#studentForm").submit(function(){
        var options = {
            url:"/addstudent",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];

                //verify the format of the matricNo
                if(!(/^[a-zA-Z][0-9]{7}[a-zA-Z]$/.test(form.matricNo.value))){
                    $("#studenterror").text("MatricNo should be the format of A1234567B!").slideDown();
                    form.matricNo.value="";
                    form.matricNo.focus();
                    return false;
                }

                //verify the type of the file uploaded
                var extend = form.photo.value.substring(form.photo.value.lastIndexOf('.')+1);
                if(extend==""){
                    $("#studenterror").text("Please upload a photo!").slideDown();
                    return false;
                }else{
                    extend = extend.toLowerCase();
                    if(extend!="jpg" && extend!="png" && extend!="jpeg"){
                        $("#studenterror").text("The photo should be in jpg/png format").slideDown();
                        return false;
                    }
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $("#studenterror").text(json.error).slideDown();
                }else{
                    $('<li/>').text(json.matricNo).appendTo("#existingstudents");
//                    $content = $('<li>' +  + ' <img src="/getphoto/' + json.studentId + '"/></li>');
//                    $content.appendTo("#existingstudent");
                    $("#studenterror").hide();
                }
            },
            error: function(xhr,status){
                $("#studenterror").text("Sorry... the form submission failed.").slideDown();
            },
            clearForm: true
        };
        $(this).ajaxSubmit(options);
        return false;
    });
    $("#courseForm").submit(function(){
        var options = {
            url:"/addcourse",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                if(form.questionNo.value=="" || form.title.value=="" || form.instruction.value==""){
                    $("#courseerror").text("Please fill the form.").slideDown();
                    return false;
                }
                if(!(/^[a-zA-Z0-9]{5,7}$/.test(form.courseCode.value))){
                    $("#courseerror").text("CourseCode should be 5-7 alphanumeric.").slideDown();
                    form.courseCode.value="";
                    form.courseCode.focus();
                    return false;
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $("#courseerror").text(json.error).slideDown();
                }else{
                    $('<li/>').text(json.courseCode + ' : ' + json.title).appendTo("#existingcourses");
                    $("#courseerror").hide();
                }
            },
            error: function(xhr,status){
                $("#courseerror").text("Sorry... the form submission failed.").slideDown();
            },
            clearForm: true
        };
        $(this).ajaxSubmit(options);
        return false;
    });

    $("#slotForm").submit(function(){
        var options = {
            url:"/addslot",
            type:"POST",
            dataType:"json",
            success: function(json){
                if(json.error!=0){
                    $("#sloterror").text(json.error).slideDown();
                }else{
                    var date = new Date(json.start).Format("dd/MM/yyyy");
                    var start = new Date(json.start).Format("hh:mm");
                    var end = new Date(json.end).Format("hh:mm");
                    $('<li/>').text(date + ' ' + start + '-' + end).appendTo("#existingslots");
                    $("#sloterror").hide();
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