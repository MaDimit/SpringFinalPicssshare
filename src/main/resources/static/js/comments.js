
//funcion used to post comment -> put in db -> return result
function addComment(lastCommentID, commentSectionID, postID){
    var posterID = $('#username').val();
    var commentContextValue = String(document.getElementById("commentContext"+postID+"").value);

    $.ajax({
        url: "comment",
        type: "POST",
        data:{
            postID: postID,
            commentText:commentContextValue},

        success: function (data) {
            var newcommentdiv = insertNewComment(data);
            if(lastCommentID != undefined) {
                $(newcommentdiv).insertAfter("#comment" + lastCommentID + "");
            }
            else{
                $(newcommentdiv).insertAfter("#" + commentSectionID.id + "");
            }
            $(".deleteComment").show();
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
    document.getElementById("commentContext"+postID+"").value = "";
}


//function for deleting comment
function deleteComment(commentID){
    $.ajax({
        url: "comment/delete",
        type: "POST",
        data: {
            commentID: commentID
        },

        success: function (data) {
            document.getElementById("comment"+commentID).innerHTML="";
            alert('You have successfully deleted your comment.')
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}



//function used for displaying comments
function insertNewComment(comment) {
    var commentID = comment.id;
    var commentContent = comment.content;
    var commentDate = timeStamp(comment.date);
    var commentPoster = comment.user;
    var likers = getCommentsLikesCount(commentID);

    var newChild = "<div id=\"comment" + commentID + "\" class=\"row\">\n" +
        "                        <div class=\"col-sm-1 text-center\">\n" +
        "                            <img id='commentPic"+commentID+"' onclick='loadUserPosts("+commentPoster.id+")' src=\"\" \n" +
        "                                 class=\"img-circle\" height=\"65\" width=\"65\" alt=\"Avatar\">\n" +
        "                        </div>\n" +
        "                        <div class=\"col-sm-8\">\n" +
        "                            <h4><a href=\"#\" onclick='loadUserPosts("+commentPoster.id+")'>" + commentPoster.username + "</a>\n" +
        "                                <small>" + commentDate + "</small><small>, Likers: </small><small onclick='showCommentLikers("+commentID+")' id=\"comment"+commentID+"likes\"  data-toggle=\"modal\" data-target=\"#myModal\" style='cursor: pointer;'></small>\n" +
        "                            </h4><button class='btn btn-primary' onclick='likeComment("+commentID+")' style='float:right'>LIKE</button><button class='btn btn-primary deleteComment' style='float: right; display: none' onclick='deleteComment("+commentID+")'>DELETE</button>" +

        "                            <p>" + commentContent + "</p>\n" +
        "                            <br>\n" +
        "                        </div>\n" +
        "                    </div>";

    var imageID = "commentPic"+commentID;
    addImage(imageID, commentPoster.profilePicUrl);
    return newChild;

}

function showCommentLikers(commentID){
    $.ajax({
        url: "comment/likes",
        data:{
            commentID: commentID
        },
        success: function (data) {
            var parent = document.getElementById("modalBody");
            var likers="";
            parent.innerHTML = "";
            for(i=0;i<data.length;i++) {
                likers += "<div class=\"row\" data-dismiss=\"modal\"><img id='liker"+data[i].username+"' onclick='loadUserPosts("+data[i].id+")' src=\"\" class=\"img-circle\" height=\"65\" width=\"65\" alt=\"Avatar\" style='margin-left: 9px;cursor: pointer;' ><h4 style='text-align: left; margin-left: 10px'><b onclick='loadUserPosts("+data[i].id+")' style='cursor: pointer;'>" + data[i].username + "</b></h4></div><hr>";
                var likerImageID = "liker"+data[i].username;
                console.log(likerImageID);
                console.log(data[i].profilePicUrl);
                addImage(likerImageID, data[i].profilePicUrl);
            }
            parent.insertAdjacentHTML('beforeend', likers);
          //  document.getElementById("modalBody").innerHTML = likers.value;
            console.log(data);
            console.table(data);
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function getCommentsLikesCount(commentID){
    $.ajax({
        url: "comment/likes",
        data:{
            commentID: commentID
        },
        success: function (data) {
            var likers = data;
            var numberOfLikers = data.length;
            document.getElementById("comment"+commentID+"likes").innerText = numberOfLikers;
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function likeComment(commentID){
    $.ajax({
        url: "comment/like",
        type: "POST",
        data:{
            commentID: commentID
        },
        success: function (data) {
            alert('Successfully liked comment');
            var likes = Number(document.getElementById("comment"+commentID+"likes").innerText );
            document.getElementById("comment"+commentID+"likes").innerText = String(Number(likes+1));
            // var oldValue = parseInt(document.getElementById("post"+postID+"likes").innerHTML);
            // var newValue = Number(oldValue) + Number(1);
            // document.getElementById("post"+postID+"likes").innerHTML=String(newValue);
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}
