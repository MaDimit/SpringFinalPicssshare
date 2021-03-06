$(document).ready(function () {

    $("#btnSubmit").click(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();

        fire_ajax_submit();
        $("#uploadedPost").css('display','block');
        $("#fileUploadForm").hide();
    });

});

function fire_ajax_submit() {

    // Get form
    var form = $('#fileUploadForm')[0];

    var data = new FormData(form);

    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/img/upload",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
            var image = data.base64Image;
            var post = data.post;
            $("#image").attr("src", getImageSrc(image));
            $("#postID").val(post.id);
            // $("#btnSubmit").prop("disabled", false);

        },
        error: function (e) {

            $("#result").text(e.responseText);
            console.log("ERROR : ", e);
            $("#btnSubmit").prop("disabled", false);

        }
    });

}

function addTags(){
    var input = $("#tagsinput").val();
    var postID = $("#postID").val();
    console.log(input);
    console.log(postID);
    $.ajax({
        type:"POST",
        url: "/util/addTag",
        data: {input: input, postID : postID},
        success: function (data) {
            for(var i = 0; i < data.length ; i++){
                $("#tags").append("<a href='#' ><span class='label label-primary'>" + data[i] + "&nbsp;</span></a>");
            }
            $("#tagsinput").val("");
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function cancelPostUpload(){
    var postID = $("#postID").val();

    $.ajax({
        type: "POST",
        url:"img/delete",
        data:{postID : postID},
        success: function (data) {
            window.location.replace("index.html#my");
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}
