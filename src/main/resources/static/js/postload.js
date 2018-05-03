//get friends feed
var albumsDropdown;

$(document).ready(function () {
   albumsDropdown = getAlbumsForPage();
});


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

    //adding albumsDropdown dropdown
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
        url: "feed/post/delete",
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

function addAlbumDiv(postID){
    var div =
        "<div class='dropdown album-dropdown' style='float:right'>\n" +
        "    <button class='btn btn-primary dropdown-toggle' type='button' data-toggle='dropdown'>Add to album</button>\n" +
        "    <ul class='dropdown-menu'>\n";
    for(i = 0; i < albumsDropdown.length; i++){
        var albumID = albumsDropdown[i].id;
        var albumName = albumsDropdown[i].albumName;
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
