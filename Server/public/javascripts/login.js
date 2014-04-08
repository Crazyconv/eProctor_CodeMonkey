$(document).ready(function(){

    $('#loginForm').submit(function(){
        $button = $(this).find('[type="submit"]');
        // communicate with controller for /enter at route table
        var options = {
            url:"/enter",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                //check the field content before submission
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
                $button.button('loading');
                return true;
            },
            success: function(json){

                // if authentication failed, display error msg 
                if(json.error != 0){
                    $button.button('reset');
                    $("#loginerror").text(json.error).slideDown();
                }else{
                    $("#loginerror").html('<br/>');
                    //if authentication succeed,redirect to the /index page
                    window.location.href="/index";
                }
            },
            error: function(xhr,status){
                $button.button('reset');
                $("#loginerror").text("Sorry... the form submission failed.").slideDown();
            }
        };


        $(this).ajaxSubmit(options);
        return false;
    });

    $('#exit').click(function(){
        exit.exitApp();
    });

});
