$(document).ready(function(){
    //sort the table by exam time
    $("table").tablesorter( {sortList: [[1,0]]} );

    //date format for display
    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "h+": this.getHours(),
            "m+": this.getMinutes(),
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    $('input[type=button][name="edit"]').click(function(){
        var $form = $(this).parent('form');
        var courseId = $form.get(0).courseId.value;
        var options={
            url:"/showslot",
            type:"POST",
            dataType:"html",
            success: function(html){
                //hide the exam time and display the exam time selection form
                $('#select' + courseId).html(html).show();
                $('#slot'+courseId).hide();
            },
            error: function(xhr,status){
                $("#selecterror").text("Sorry... the form submission failed.").show();
            }
        };
        $form.ajaxSubmit(options);
    });

    $(document).on('submit','form',function(){
        var courseId = $(this).get(0).courseId.value;
        var options = {
            url:"/selectslot",
            type:"POST",
            dataType:"json",
            success: function(json){
                if(json.error!=0){
                    $("#selecterror").text(json.error).show();
                }else{
                    //hide the previous error message and the exam slot selection form
                    $("#selecterror").hide();
                    $('#select'+courseId).hide();
                    //update the exam time in html
                    var date = new Date(json.start).Format("dd/MM/yyyy");
                    var start = new Date(json.start).Format("hh:mm");
                    var end = new Date(json.end).Format("hh:mm");
                    $('#hidden'+courseId).text(json.start);
                    $('#slot'+courseId).text(date + ' ' + start + '-' + end).show();
                    //enable the remove button and sort the table again
                    $('#remove'+courseId).attr("disabled",false);
                    $("table").tablesorter( {sortList: [[1,0]]} );
                }
            },
            error: function(xhr,status){
                $("#selecterror").text("Sorry... the form submission failed.").show();
            }
        };
        $(this).ajaxSubmit(options);
        return false;
    });

    $(document).on('click','input[name="cancel"]',function(){
        var courseId = $(this).parent('form').get(0).courseId.value;
        //hide the exam slot selection form and display the exam time
        $('#select'+courseId).hide();
        $('#slot'+courseId).show();
    });

    $(document).on('click','input[name="remove"]',function(){
        var $form = $(this).parent('form');
        var courseId = $form.get(0).courseId.value;
        var options={
            url:"/deleteslot",
            type:"POST",
            dataType:"json",
            success: function(json){
                if(json.error!=0){
                    $("#selecterror").text(json.error).show();
                }else{
                    //hide the previous error message
                    $("#selecterror").hide();
                    //clear the original exam time
                    $('#slot'+courseId).text("");
                    $('#hidden'+courseId).text("");
                    //disable the remove button and sort the table again
                    $('#remove'+courseId).attr("disabled",true);
                    $("table").tablesorter( {sortList: [[1,0]]} );
                }
            },
            error: function(xhr,status){
                $("#selecterror").text("Sorry... the form submission failed.").show();
            }
        };
        $form.ajaxSubmit(options);
    });
});
