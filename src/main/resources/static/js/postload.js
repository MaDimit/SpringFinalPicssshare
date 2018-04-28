function loadFriendsFeed() {

    document.getElementById("container").style.display="none";
    $("#newpost").html("");
    $(".page-header").html("Friends Feed");
    $.ajax({
        url: "/feed/friends"
    }).then(function (data) {
        console.log(data);
        insertPosts(data);

        document.getElementById('subscribeButton').style.visibility="hidden";
    });
}


function loadUserPosts(id) {
    $("#newpost").html("");
    $.ajax({
        url: "feed/user",
        data: {id : id},
    }).then(function (data) {
        console.log("USERNAME: "+data.user.username);
        $(".page-header").html(data.user.username +"'s page");
        console.log("DATA:"+JSON.stringify(data));
        console.log("OWNER: "+ data.owner);
        if(String(data.owner) ==="false") {
            document.getElementById('subscribeButton').style.visibility = "visible";
            document.getElementById('ownerID').innerHTML = data.user.id;
        }
        else if(String(data.owner) ==="true"){
            document.getElementById("container").style.display="none";
            document.getElementById('subscribeButton').style.visibility = "hidden";
        }
        insertPosts(data.posts);

    });
}

function loadTrendingFeed() {
    document.getElementById("container").style.display="none";
    $("#newpost").html("");
    $(".page-header").html("Trending Feed");

    $.ajax({
        url: "feed/trending"
    }).then(function (data) {
        console.log(data);
        insertPosts(data);
        document.getElementById('subscribeButton').style.visibility="hidden";
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
        var postDate = post.date;
        var postID = post.id;
        var postLikes = post.likes;
        var postDislikes = post.dislikes;
        insertNewPost(post, postPoster, postComments, postUrl, postTags, postDate, postID, postLikes, postDislikes);
    }

}

function insertNewPost(post, postPoster, postComments, postUrl, postTags, postDate, postID, postLikes, postDislikes) {
    var postPosterUsername = postPoster.username;

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
        newChild += "<a href='#'><span class=\"label label-primary\">" + postTags[i] + "</span></a> ";  //TODO Tag inserting to search
    }

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
        "                        <a href=\"#\" onclick='loadUserPosts("+postPoster.id+")'>" + postPosterUsername + "</a>\n" +
        "                    </div>\n" +
        "                </div>";
    parent.insertAdjacentHTML('beforeend', newChild);
    addImage(imageID, postUrl);
}

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

function like(postID){
    $.ajax({
        url: "user/addLike",
        type: "POST",
        data:{
            postID: postID
        }
    }).then(function (data) {
        if(data==='success'){
            // alert('Successfully liked post');
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
        if(data==='You have already liked this post.'){
            // alert('You have already liked this post.');
        }
    });

}

function addComment(lastCommentID, commentSectionID, postID){
    console.log(lastCommentID);
    console.log(commentSectionID.id);
    console.log(postID);
    console.log($('#username').val());
    var posterID = $('#username').val();
    var commentContextValue = String(document.getElementById("commentContext"+postID+"").value);
    console.log(commentContextValue);

    $.ajax({
        url: "addComment",
        type: "POST",
        data:{
                postID: postID,
                commentText:commentContextValue}
    }).then(function (data) {

        console.log(data);
        var newcommentdiv = insertNewComment(data);
        if(lastCommentID != undefined) {
            $(newcommentdiv).insertAfter("#comment" + lastCommentID + "");
        }
        else{
            $(newcommentdiv).insertAfter("#" + commentSectionID.id + "");
        }

    });
    document.getElementById("commentContext"+postID+"").value = "";
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

function deleteComment(commentID){
    $.ajax({
        url: "deleteComment",
        type: "POST",
        data: {
            commentID: commentID
        }
    }).then(function (data) {
        if(data==='success'){
            document.getElementById("comment"+commentID).innerHTML="";
            alert('You have successfully deleted your comment.')
        }
        else {
            alert('Sorry! Problems occured.');
        }
    });
}

function insertNewComment(comment) {
    var commentID = comment.id;
    var commentContent = comment.content;
    var commentDate = comment.date;
    var commentPoster = comment.user;
    var likers = getCommentsLikesCount(commentID);


    var newChild = "<div id=\"comment" + commentID + "\" class=\"row\">\n" +
        "                        <div class=\"col-sm-1 text-center\">\n" +
        "                            <img src=\"data:image/png;base64,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\"\n" +
        "                                 class=\"img-circle\" height=\"65\" width=\"65\" alt=\"Avatar\">\n" +
        "                        </div>\n" +
        "                        <div class=\"col-sm-8\">\n" +
        "                            <h4><a href=\"#\">" + commentPoster.username + "</a>\n" +
        "                                <small>" + commentDate + "</small><small>, Likers: </small><small id=\"comment"+commentID+"likes\"></small>\n" +
        "                            </h4><button onclick='likeComment("+commentID+")' style='float:right'>LIKE</button><button style='float: right;' onclick='deleteComment("+commentID+")'>DELETE</button>" +

        "                            <p>" + commentContent + "</p>\n" +
        "                            <br>\n" +
        "                        </div>\n" +
        "                    </div>";
    return newChild;
}

function getCommentsLikesCount(commentID){
    $.ajax({
        url: "getCommentLikes",
        type: "POST",
        data:{
            commentID: commentID
        }
    }).then(function (data) {
        var likers = data;
        var numberOfLikers = data.length;
        document.getElementById("comment"+commentID+"likes").innerText = numberOfLikers;
    });
}

function likeComment(commentID){
    $.ajax({
        url: "addCommentLike",
        type: "POST",
        data:{
            commentID: commentID
        }
    }).then(function (data) {
        if(data==='success'){
            alert('Successfully liked comment');
            var likes = Number(document.getElementById("comment"+commentID+"likes").innerText );
            document.getElementById("comment"+commentID+"likes").innerText = String(Number(likes+1));
            // var oldValue = parseInt(document.getElementById("post"+postID+"likes").innerHTML);
            // var newValue = Number(oldValue) + Number(1);
            // document.getElementById("post"+postID+"likes").innerHTML=String(newValue);
        }
        else {
            alert(data);
        }
    });
}

function subsrcibe(subscribedToID){
    $.ajax({
        url: "user/subscribe",
        type: "POST",
        data:{
            subscribedToID: subscribedToID
        }
    }).then(function (data) {
        if(data==='success'){
            alert('Subscription successfully performed.');
        }
        else {
            alert(data)
        }
    });
}
