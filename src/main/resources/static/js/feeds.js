
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
                $(".deleteComment").show();
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
