$('document').ready(function(){
    $('#invigilators').load("/listinvigilators");
    $('#inv').load("/assignInvigilator");

    //for css
    $('tr').mouseenter(function(){
        $(this).addClass("info");
    });
    $('tr').mouseleave(function(){
        $(this).removeClass("info");
    });

    $('#createbutton').click(function(){
        $form = $('#createForm');
        var options = {
            url:"/createinvigilator",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                if($form.get(0).name.value=="" || $form.get(0).account.value=="" || $form.get(0).password.value==""){
                    $('#createerror').text("Please fill in all fields");
                    return false;
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $('#createerror').text(json.error);
                }else{
                    $('#createerror').html('<br/>');
                    $('<tr><td>' + json.email + '</td><td>' + json.name + '</td></tr>').appendTo('#table');
                }
            },
            error: function(xhr,status){
                $('#createerror').text("Internal server error.");
            },
            clearForm: true
        };
        $form.ajaxSubmit(options);
    });

    $('#resetbutton').click(function(){
        $form = $('#resetForm');
        var options = {
            url:"/resetpassword",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                if($form.get(0).email.value=="" || $form.get(0).password.value==""){
                    $('#reseterror').text("Please fill in all fields");
                    return false;
                }
                return true;
            },
            success: function(json){
                if(json.error!=0){
                    $('#reseterror').text(json.error);
                }else{
                    $('#reseterror').html('<br/>');
                    $('#resetsuccess').text("Password has been reset.");
                }
            },
            error: function(xhr,status){
                $('#reseterror').text("Internal server error.");
            },
            clearForm: true
        };
        $form.ajaxSubmit(options);
    });
});