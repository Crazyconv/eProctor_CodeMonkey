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
                    $("#error").text("MatricNo should be the format of A1234567B!").slideDown();
                    form.matricNo.value="";
                    form.matricNo.focus();
                    return false;
                }

                //verify the type of the file uploaded
                var extend = form.photo.value.substring(form.photo.value.lastIndexOf('.')+1);
                if(extend==""){
                    $("#error").text("Please upload a photo!").slideDown();
                    return false;
                }else{
                    extend = extend.toLowerCase();
                    if(extend!="jpg" && extend!="png" && extend!="jpeg"){
                        $("#error").text("The photo should be in jpg/png format").slideDown();
                        return false;
                    }
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $("#error").text(json.error).slideDown();
                }else{
                    $('<li/>').text(json.matricNo).appendTo("#existingstudent");
//                    $content = $('<li>' +  + ' <img src="/getphoto/' + json.studentId + '"/></li>');
//                    $content.appendTo("#existingstudent");
                    $("#error").hide();
                }
            },
            error: function(xhr,status){
                $("#error").text("Sorry... the form submission failed.").slideDown();
            },
            clearForm: true
        };
        $(this).ajaxSubmit(options);
        return false;
    });
    $("#courseForm").submit(function(){
        alert("success");
        var options = {
            url:"addcourse",
            type:"POST",
            dataType:"json",
            success:function(json){
                if(json.error!=0){
                    alert(json.error);
                }else{
                    $("<li></li>").text(json.matricNo).appendTo("#existingstudent");
                    $('<img src="'+ json.photoPath +'"/>');
                }
            },
            error: function(xhr,status){
                alert("Sorry... there is a problem.");
            },
            cleanForm: true,
        };
        $(this).attr("action","addstudent");
        $(this).ajaxSubmit(options);
        return false;
    });


});