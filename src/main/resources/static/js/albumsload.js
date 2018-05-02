function askForAlbumName(){
    var txt;
    var albumName = prompt("Please the album name:", "Something like friends, collegues, trip....");
    if (albumName == null || albumName == "") {
        alert("No album name entered.");
    } else {
        addAlbum(albumName);
    }
}

function addAlbum(albumName){
    $.ajax({
        url: "feed/addAlbum",
        type: "POST",
        data:{
            albumName: albumName
        },
        success: function (data) {
            loadAlbums();
            alert('Successfully added album.');
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
         }
        });
    albums = getAlbumsForPage();
}

function loadAlbums() {
    $("#newpost").html("");
    $.ajax({
        url: "feed/albums",
        success: function (data) {
            document.getElementById('subscribeButton').style.display="none";
            $(".page-header").html("");
            document.getElementById('showSubscriptions').style.display="none";
            document.getElementById("container").style.display = "block";
            var container = document.getElementById("container");
            container.innerHTML =
                "<br><br><br><table><tr><td><h1><b>Albums</b></h1></td><td width='50%'><button class=\"btn btn-success\" onclick=\"askForAlbumName()\" style='float: right'>Add new album</button></td></tr></table>\n";

            for (i = 0; i < data.length; i++) {

                var album = data[i];
                var albumName = String(album.name);

                container.innerHTML += "\n" +
                    "<div id='album" + album.id + "' class=\"responsive\" onclick='loadPicturesFromAlbum(" + album.id + ",\"" + albumName + "\")'>\n" +
                    "  <div class=\"gallery\">\n" +
                    "      <img src='../images/albumImage.jpeg' alt=\"Trolltunga Norway\" width=\"300\" height=\"200\">\n" +
                    "    </a>\n" +
                    "<div class=\"desc\"><h4>" + album.name + "</h4><a href='#' " +
                    "style='float: inherit' onclick='deleteAlbum(" + album.id + ")'>delete</a></div>\n" +
                    "" +
                    "  </div>\n" +
                    "</div>\n";

            }
            },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });


}

function addToAlbum(albumID, postID){
    $.ajax({
        url: "feed/addToAlbum",
        type: "POST",
        data:{postID : postID, albumID : albumID},
        success: function (data) {
            alert('Successfully added to album.');
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function getAlbumsForPage(){
    var albums = [];
    $.ajax({
        url : "feed/albumNames",
        success: function (data) {
            for(var key in data){
                if(data.hasOwnProperty(key)){
                    albums.push({
                        id : key,
                        albumName : data[key]
                    });
                }
            }
        },
        error: function(jqXHR, exception) {
            //no message shown for convenience
           // alert(jqXHR.responseJSON.message);
        }
    });
    //what for?
    return albums;
}

var currentAlbumID;

function loadPicturesFromAlbum(albumID, albumName) {
    $("#newpost").html("");
    $.ajax({
        url: "feed/album",
        type: "POST",
        data: {albumID: albumID},
        success: function (data) {
            if(data.length>0){
                $(".page-header").html(albumName);
            }
            currentAlbumID = albumID;
            document.getElementById("container").style.display = "block";
            insertPosts(data);
            replaceDropdowns();
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function replaceDropdowns(){
    // var dropdowns = document.getElementsByClassName("album-dropdown");
    // var deletePosts = document.getElementsByClassName("post-album-delete");
    // for(var i = 0; i < dropdowns.length; i++){
    //     dropdowns[i].style.display = "none";
    //     deletePosts[i].style.display = "block";
    // }

    $(".album-dropdown").hide();
    $(".post-album-delete").show();
}

function deleteAlbum(albumID){
    $.ajax({
        url: "feed/deleteAlbum",
        type: "POST",
        data:{
            albumID: albumID
        },
        success: function (data) {
            $("#newpost").html("");

            document.getElementById('album'+albumID).innerHTML="";
            $(".page-header").html("");
            alert('You have deleted this album.');
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });

    albums = getAlbumsForPage();
}

function deleteAlbumPost(postID){
    $.ajax({
        url : "feed/deleteAlbumPost",
        type : "POST",
        data:{postID : postID, albumID : currentAlbumID},
        success: function (data) {
            document.getElementById("post" + postID).innerHTML = "";
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function showSubscriptions() {
    $("#newpost").html("");
    $.ajax({
        url: "user/getSubscriptions",
        type: "POST",
        success: function (data) {
            $(".page-header").html("");
            document.getElementById("container").style.display="block";
            var container = document.getElementById("container");
            container.innerHTML= "<br><br><br><h1><b>Subscriptions</b></h1>\n";

            //hid the showSubscription button
            document.getElementById('showSubscriptions').style.display="none";
            var newChild;
            for(i=0;i<data.length;i++){
                newChild+="<div id=\"subscriber" + data[i].id + "\" class=\"row\">\n" +
                    "                        <div class=\"col-sm-1 text-center\">\n" +
                    "                            <img id='subscriberPic" + data[i].id + "' src=\"\" \n" +
                    "                                 class=\"img-circle\" height=\"65\" width=\"65\" alt=\"Avatar\">\n" +
                    "                        </div>\n" +
                    "                        <div class=\"col-sm-8\">\n" +
                    "                      <h4><a href=\"#\" onclick='loadUserPosts("+data[i].id+")'>" + data[i].username + "</a></h4>\n" +
                    "                            <button class='btn btn-primary' onclick='unsubscribe("+data[i].id+")'>Unsubscribe</button><br><hr>\n" +
                    "                        </div>\n" +
                    "                    </div>";
                var imageID = "subscriberPic" + data[i].id;
                addImage(imageID, data[i].profilePicUrl);
            }
            container.innerHTML+=newChild;
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function unsubscribe(subscribedToID){
    $.ajax({
        url: "user/unsubscribe",
        type: "POST",
        data:{
            subscribedToID: subscribedToID
        },
        success: function (data) {
            alert("You have unsubscribed to this user.");
            document.getElementById("subscriber"+subscribedToID).innerHTML="";
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}
