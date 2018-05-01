//get friends feed
var albums;

$(document).ready(function () {
   albums = getAlbumsForPage();
});


function loadFriendsFeed() {

    document.getElementById("container").style.display="none";
    $("#newpost").html("");
    $(".page-header").html("Friends Feed");
    $.ajax({
        url: "/feed/friends",
        success: function (data) {
            // console.log(JSON.stringify(data));
            // console.log("FRIENDS POST ID: "+data[i].id);
            insertPosts(data);
            document.getElementById('subscribeButton').style.display="none";
            document.getElementById('showSubscriptions').style.display="none";
            // for(i=0;i<data.length;i++){
            //     console.log(document.getElementById("deletePost"+data[i].id));
            //     document.getElementById("deletePost"+data[i].id).display="none";
            // }
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });

}

//get feed with user's posts
function loadUserPosts(id) {

    document.getElementById("container").style.display="none";

    $("#newpost").html("");
    $.ajax({
        url: "feed/user",
        data: {id : id},
        success: function (data) {
            $(".page-header").html(data.user.username +"'s page");
            if(!data.owner) {
                document.getElementById('subscribeButton').style.display = "block";
                document.getElementById('ownerID').innerHTML = data.user.id;
                document.getElementById('showSubscriptions').style.display="none";
                insertPosts(data.posts);
                for(i=0;i<data.posts.length;i++){
                    document.getElementById("deletePost"+data.posts[i].id).style.display="none";
                }

            }
            else {
                document.getElementById("container").style.display="none";
                document.getElementById('subscribeButton').style.display = "none";
                document.getElementById('showSubscriptions').style.display="block";
                insertPosts(data.posts);
                for(i=0;i<data.posts.length;i++){
                    document.getElementById("deletePost"+data.posts[i].id).style.display="block";
                }
            }
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function loadTagFeed(id){

    $("#newpost").html("");
    $.ajax({
        url: "feed/tag",
        data: {id : id},
        success: function (data) {
            console.log(data);
            $(".page-header").html(data.tagname);
            insertPosts(data.posts);
            document.getElementById("deletePost"+data.posts[i].id).display="none";
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

//get trending feed
function loadTrendingFeed() {
    document.getElementById("container").style.display="none";
    $("#newpost").html("");
    $(".page-header").html("Trending Feed");

    $.ajax({
        url: "feed/trending",
        success: function (data) {
            // console.log(JSON.stringify(data));
            // console.log("TRENDING POST ID: "+data[i].id);
            insertPosts(data);
            document.getElementById('subscribeButton').style.display="none";
            document.getElementById('showSubscriptions').style.display="none";
            // for(var i=0;i<data.length;i++){
            //     console.log(document.getElementById("deletePost"+data[i].id));
            //     document.getElementById("deletePost"+data[i].id).display="none";
            // }
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

//Inserting posts in body of html
function insertPosts(json) {
    for (var i = 0; i < json.length; i++) {
        var post = json[i];
        var postPoster = post.poster;
        var postComments = post.comments;
        var postUrl = post.url;
        var postTags = post.tags;
        var postDate = timeStamp(post.date);
        var postID = post.id;
        var postLikes = post.likes;
        var postDislikes = post.dislikes;
        insertNewPost(post, postPoster, postComments, postUrl, postTags, postDate, postID, postLikes, postDislikes);

    }

}
//used for post visualising
function insertNewPost(post, postPoster, postComments, postUrl, postTags, postDate, postID, postLikes, postDislikes) {
    var postPosterUsername = postPoster.username;
    //the place where the post will be visualized
    var parent = document.getElementById('newpost');
    var imageID = "image" + postID;
    //adding image
    var newChild = "<div id =\"post" + postID + "\" class=\"card\">" +
        "    <!-- post picture -->" +
        "    <div class=\"fill\">" +
        "    <img class=\"card-img-top\" id="+imageID+" src=\"\" alt=\"Image\">" +
        "    </div>" +
        "        <!-- post body -->" +
        "        <div class=\"card-body\">";

    //adding tags
    for (i = 0; i < postTags.length; i++) {
        newChild += "<a href='#' ><span class=\"label label-primary\" style='font-size: medium'>" + postTags[i] + "</span></a>  ";  //TODO Tag inserting to search
    }

    //adding albums dropdown
    newChild += addAlbumDiv(postID);
    newChild += deleteAlbumPostDiv(postID);
    //likes dislikes
    newChild +=
        "<br><div class=\"btn-group\">" +
        "            <button class=\"btn btn-success\" onclick=\"like("+postID+")\" ><i\n" +
        "                    class=\"fa fa-thumbs-up\"></i>\n" +
        "                <div id=\"post"+postID+"likes\" style=\"display: inline\">" + postLikes + "</div>\n" +
        "            </button>\n" +
        "            <button class=\"btn btn-danger\" onclick=\"dislike("+postID+")\" ><i\n" +
        "                    class=\"fa fa-thumbs-down\"></i>\n" +
        "                <div id=\"post"+postID+"dislikes\" style=\"display: inline\">" + postDislikes + "</div>\n" +
        "            </button>\n" +
        "        </div>";

    newChild += insertModal(post);
    //comments
    newChild += "<p id=\"commentsSection" + postID + "\"><span class=\"badge\"></span> <b>Comments:</b></p><br>";
    for (var i = 0; i < postComments.length; i++) {
        newChild += insertNewComment(postComments[i]);
    }
    if (postComments[postComments.length-1] != undefined) {
        var lastCommentID = postComments[postComments.length - 1].id;
    }

    var commentSectionID = "commentsSection"+postID;


    newChild += "                     <h4>Leave a Comment:</h4>\n" +
        "                        <div class=\"form-group\">\n" +
        "                            <textarea id=\"commentContext"+postID+"\" class=\"form-control\" rows=\"3\" required></textarea>\n" +
        "                        </div>\n" +
        "                        <button onclick=\"addComment("+lastCommentID+", "+commentSectionID+","+postID+" )\" class=\"btn btn-success\">Submit</button>\n" +
        "                    </div>\n" +
        "                    <div class=\"card-footer text-muted\">\n" +
        "                        Posted on " + postDate + " by\n" +
        "                        <a href=\"#\" onclick='loadUserPosts("+postPoster.id+")'>" + postPosterUsername + "</a><a href='#' id=\"deletePost" + postID +"\" onclick='deletePost("+postID+")' style='float:right; display: none'>delete</a>\n" +
        "                    </div>\n" +
        "                </div><br>";
    parent.insertAdjacentHTML('beforeend', newChild);
    //load post's picture
    addImage(imageID, postUrl);
}

function deletePost(postID){
    $.ajax({
        url: "feed/deletePost",
        type:"POST",
        data: {postID : postID},
        success: function (data) {
            alert('You have successfully deleted the post.');
            document.getElementById("post"+postID).innerHTML="";
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}
//function for disliking post
//TODO
function dislike(postID){
    $.ajax({
        url: "user/addDislike",
        type: "POST",
        data:{
            postID: postID
        }
    }).then(function (data) {
        if(data==='success'){
            // alert('Successfully disliked post');
            var oldValue = parseInt(document.getElementById("post"+postID+"dislikes").innerHTML);
            var newValue = Number(oldValue) + Number(1);
            document.getElementById("post"+postID+"dislikes").innerHTML=String(newValue);
        }
        if(data==='removedLikeAddDislike'){
            var oldDislikeValue = parseInt(document.getElementById("post"+postID+"dislikes").innerHTML);
            var newDislikeValue = Number(oldDislikeValue) + Number(1);
            var oldLikeValue = parseInt(document.getElementById("post"+postID+"likes").innerHTML);
            var newLikeValue = Number(oldLikeValue) - Number(1);
            document.getElementById("post"+postID+"dislikes").innerHTML=String(newDislikeValue);
            document.getElementById("post"+postID+"likes").innerHTML=String(newLikeValue);
        }
        if(data==='You have already disliked this post.'){
            // alert('You have already disliked this post.');
        }
    });
}
//function for liking post
function like(postID){
    $.ajax({
        url: "user/addLike",
        type: "POST",
        data:{
            postID: postID
        },
        success: function (data) {
            console.log(data);
            if(data==='success'){
                alert('Successfully liked post');
                var oldValue = parseInt(document.getElementById("post"+postID+"likes").innerHTML);
                var newValue = Number(oldValue) + Number(1);
                document.getElementById("post"+postID+"likes").innerHTML=String(newValue);
            }
            if(data==='removedDislikeAddLike'){
                var oldDislikeValue = parseInt(document.getElementById("post"+postID+"dislikes").innerHTML);
                var newDislikeValue = Number(oldDislikeValue) - Number(1);
                var oldLikeValue = parseInt(document.getElementById("post"+postID+"likes").innerHTML);
                var newLikeValue = Number(oldLikeValue) + Number(1);

                document.getElementById("post"+postID+"dislikes").innerHTML=String(newDislikeValue);
                document.getElementById("post"+postID+"likes").innerHTML=String(newLikeValue);
            }
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });


}
//funcion used to post comment -> put in db -> return result
function addComment(lastCommentID, commentSectionID, postID){
    var posterID = $('#username').val();
    var commentContextValue = String(document.getElementById("commentContext"+postID+"").value);

    $.ajax({
        url: "addComment",
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
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
    document.getElementById("commentContext"+postID+"").value = "";
}

function addAlbumDiv(postID){
    var div =
        "<div class='dropdown album-dropdown' style='float:right'>\n" +
        "    <button class='btn btn-primary dropdown-toggle' type='button' data-toggle='dropdown'>Add to album</button>\n" +
        "    <ul class='dropdown-menu'>\n";
    for(i = 0; i < albums.length; i++){
        var albumID = albums[i].id;
        var albumName = albums[i].albumName;
        div += "<li><a onclick='addToAlbum("+albumID+", "+postID+")'>"+albumName+"</a></li>"
    }
    div += "</ul></div>";
    return div;
}

function deleteAlbumPostDiv(postID){
    var div = "<div class='post-album-delete' style='float:right; display: none'  onclick='deleteAlbumPost("+postID+")'>" +
        "<button class='btn btn-primary' type='button'>Delete from album</button></div>"
    return div;
}

function insertModal(post) {
    //TODO modal insertion
// <div class=\"box\"><a data-target=\"#myModal\" data-toggle=\"modal\"\n" +
//     "                                            href=\"#myModal\">" + postLikes + "</a></div>\n" +
//     "\n" +
//     "                        <!-- The Modal -->\n" +
//     "                        <div id=\"myModal\" class=\"modal\">\n" +
//     "\n" +
//     "                            <!-- Modal content -->\n" +
//     "                            <div class=\"modal-content\">\n" +
//     "                                <div class=\"modal-header\">\n" +
//     "                                    <span class=\"close\">&times;</span>\n" +
//     "\n" +
//     "                                </div>\n" +
//     "                                <div class=\"modal-body\">\n" +
//     "                                    <h1>Likes:</h1>\n" +
//     "                                    <p>GET LIKERS FROM DB</p>\n" +
//     "                                </div>\n" +
//     "                                <div class=\"modal-footer\">\n" +
//     "                                </div>\n" +
//     "                            </div>\n" +
//     "\n" +
//     "                        </div>\n" +
//     "\n" +
//     "                        <br>\n" +
    return "<div></div>";
}

//function for deleting comment
function deleteComment(commentID){
    $.ajax({
        url: "deleteComment",
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

/**
 * Return a timestamp with the format "m/d/yy h:MM:ss TT"
 * @type {Date}
 */

function timeStamp(dbDate) {
// Create a date object with the current time
    var now = new Date(dbDate);

// Create an array with the current month, day and time
    var date = [ now.getDate(), now.getMonth() + 1, now.getFullYear() ];

// Create an array with the current hour, minute and second
    var time = [ now.getHours(), now.getMinutes(), now.getSeconds() ];

// Determine AM or PM suffix based on the hour
    var suffix = ( time[0] < 12 ) ? "AM" : "PM";

// Convert hour from military time
    time[0] = ( time[0] < 12 ) ? time[0] : time[0] - 12;

// If hour is 0, set it to 12
    time[0] = time[0] || 12;

// If seconds and minutes are less than 10, add a zero
    for ( var i = 1; i < 3; i++ ) {
        if ( time[i] < 10 ) {
            time[i] = "0" + time[i];
        }
    }

// Return the formatted string
    return date.join("/") + " " + time.join(":") + " " + suffix;
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
        "                                <small>" + commentDate + "</small><small>, Likers: </small><small id=\"comment"+commentID+"likes\"></small>\n" +
        "                            </h4><button class='btn btn-primary' onclick='likeComment("+commentID+")' style='float:right'>LIKE</button><button class='btn btn-primary' style='float: right;' onclick='deleteComment("+commentID+")'>DELETE</button>" +

        "                            <p>" + commentContent + "</p>\n" +
        "                            <br>\n" +
        "                        </div>\n" +
        "                    </div>";

    var imageID = "commentPic"+commentID;
    addImage(imageID, commentPoster.profilePicUrl);
    return newChild;
}

function getCommentsLikesCount(commentID){
    $.ajax({
        url: "getCommentLikes",
        type: "POST",
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
        url: "addCommentLike",
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

function subsrcibe(subscribedToID){
    $.ajax({
        url: "user/subscribe",
        type: "POST",
        data: {
            subscribedToID: subscribedToID
        },
        success: function (data) {
            alert("Successfully subscribed to the user.");
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });

        // .then(function(data) {
        //     console.log(data);
        //     // if(data.message != undefined){
        //     //     alert('Subscription successfully performed.');
        //     // }
        //     // else {
        //     //     alert(data.message);
        //     // }
        // })
        // .catch(function(data) {
        //     console.log(data.message);
        // });
}
