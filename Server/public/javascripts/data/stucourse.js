$(document).ready(function(){
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
                if(form.questionNo.value=="" || form.title.value==""){
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

});