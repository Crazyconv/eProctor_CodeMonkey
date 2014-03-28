$(document).ready(function(){
    $(document).on('click','input[name="saveanswer"]',function(){
        var $button = $(this);
        var $form = $button.parent('form');
        var $div = $form.parent('div');

        var options={
            url:"/saveanswer",
            type:"POST",
            dataType:"json",
            beforeSubmit: function(formData, jqForm, option){
                var form = jqForm[0];
                if(form.answer.value==""){
                    $div.find('span[name="answererror"]').text("The answer field is empty.").slideDown();
                    return false;
                }
                return true;
            },
            success: function(json){
                alert("success");
                if(json.error!=0){
                    $div.find('span[name="answererror"]').text(json.error).show();
                }else{
                    $div.find('span[name="answererror"]').hide();
                    $button.attr("disabled","disabled");
                    $div.find('span[name="answersuccess"]').text("Saved!").slideDown();
                    setTimeout(function(){$div.find('span[name="answersuccess"]').hide();},2000);
                }
            },
            error: function(xhr,status){
                $div.find('span[name="answererror"]').text("Sorry... the form submission failed.").show();
            }
        };
        $form.ajaxSubmit(options);
    });

    $(document).on('keypress','textarea',function(){
        var button = this.form.saveanswer;
        button.disabled = false;
    });
});
