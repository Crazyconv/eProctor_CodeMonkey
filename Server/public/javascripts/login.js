$(document).ready(function(){

    $('#loginForm').submit(
    function()
    {
        // communicate with controller for /enter at route table
        var options = {
            url:"/enter",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                if(form.username.value==""){
                    $("#loginerror").text("Please enter the username.").slideDown();
                    form.username.focus();
                    return false;
                }
                if(form.password.value==""){
                    $("#loginerror").text("Please enter the password.").slideDown();
                    form.password.focus();
                    return false;
                }
                return true;
            },
            success: function(json){

                // if authentication failed, display error msg 
                if(json.error != 0){
                    $("#loginerror").text(json.error).slideDown();
                }else{
                // if authentication succeed, redirect to the /index page 
                    $("#loginerror").hide(); // Is this line necessary?
                    window.location.href="/index";
                }
            },
            error: function(xhr,status){
                $("#loginerror").text("Sorry... the form submission failed.").slideDown();
            }
        };


        $(this).ajaxSubmit(options);
        return false;
    });
});
