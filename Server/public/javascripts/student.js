$(document).ready(function(){
    $('input[type=button][name="edit"]').click(function(){
        var $form = $(this).parent().get(0);
        var courseId = $form.courseId.value;
        $('#slot' + courseId).load("/course/"+courseId);
    });
    $(document).on('submit','form',function(){
        var options = {
            url:"/selectslot",
            type:"POST",
            dataType:"json",
//            beforeSubmit: function(formData, jqForm, option){
//                var form = jqForm[0];
//                if(form.content.value==""){
//                    $("#questionerror").text("Please enter the question.").slideDown();
//                    return false;
//                }
//                if(form.content.value.length > 5000){
//                    $("#questionerror").text("The question exceeds 5000 characters.").slideDown();
//                    return false;
//                }
//                return true;
//            },
            success: function(json){
                if(json.error!=0){
                    alert(json.error);
//                    $("#questionerror").text(json.error).slideDown();
                }else{
//                    $("#questionerror").hide();
//                    $("#questionForm textarea").clearFields();
//                    $('<li/>').text(json.content).appendTo("#course" + json.courseId);
                    alert("success!");
                }
            },
            error: function(xhr,status){
//                $("#questionerror").text("Sorry... the form submission failed.").slideDown();
                alert("error!");
            }
        };
        $(this).ajaxSubmit(options);
        return false;
    });

});
