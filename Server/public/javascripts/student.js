// Note:
//         $(a) is used to address an element in the HTML
//         there are various kinds of addressing methods, so there are various kinds of a
// ----------------------------------------------------------------------
// Description:
//         run the supplied function when $(document) is ready(fully loaded)
//         then supplied function defines a bunch of events js is monitoring
// ----------------------------------------------------------------------
$(document).ready(function(){

    // Note:
    //         $('input[type=button][name="edit"]') is addressing this element in HTML:
    //             <input type=button name="edit">xxxx</input>
    // ----------------------------------------------------------------------
    // Description:
    //         when such addressed element is clicked, start the function passed in
    //         what this function does:
    //             find the form containing this button, and then find the courseID by it
    //             and using this courseID to locate an HTML in server and load that HTML
    //             in the current HTML(embed in an element)
    // ----------------------------------------------------------------------
    $('input[type=button][name="edit"]').click(function(){
        var $form = $(this).parent().get(0);
        var courseId = $form.courseId.value;
        $('#slot' + courseId).load("/course/"+courseId);
    });

    // Description:
    //         when any form of $(document) is submitted, the function passed in is activated
    //         what this function does:
    //             create an object variable, send this object variable out to send a request to the server
    // ----------------------------------------------------------------------
    $(document).on('submit','form',function(){
        var options = {
            url:"/selectslot",          // the url this object should be sent to
            type:"POST",                // the HTTP method to send this object
            dataType:"json",            // expected data type of the response from server 
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

            // Description:
            //         when server respond with a ok(xx) result(?), a function should be executed,
            //         and this function will examine the its json parameter, to alert user accordingly
            // ----------------------------------------------------------------------
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
            // similar to success clause
            error: function(xhr,status){
//                $("#questionerror").text("Sorry... the form submission failed.").slideDown();
                alert("error!");
            }
        };

        $(this).ajaxSubmit(options);        // submit the constructed request object to server
        return false;                       // somehow can avoid some problems
    });

});
