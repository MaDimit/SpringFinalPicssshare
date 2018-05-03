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
        url: "/album",
        type: "POST",
        data:{
            albumName: albumName
        },
        success: function (data) {
            loadAlbums();
            alert('Successfully added album.');
            albumsDropdown = getAlbumsForPage();
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
         }
        });

}

function loadAlbums() {
    $("#newpost").html("");
    $.ajax({
        url: "/album",
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
        url: "album/add/post",
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
        url : "album/names",
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
        url: "album/posts",
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
    $(".album-dropdown").hide();
    $(".post-album-delete").show();
}

function deleteAlbum(albumID){
    $.ajax({
        url: "album/delete",
        type: "POST",
        data:{
            albumID: albumID
        },
        success: function (data) {
            $("#newpost").html("");

            document.getElementById('album'+albumID).innerHTML="";
            $(".page-header").html("");
            alert('You have deleted this album.');
            albumsDropdown = getAlbumsForPage();
        },
        error: function(jqXHR, exception) {
            alert(jqXHR.responseJSON.message);
        }
    });
}

function deleteAlbumPost(postID){
    $.ajax({
        url : "album/delete/post",
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
