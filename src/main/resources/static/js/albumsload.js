function loadAlbums() {
    $("#newpost").html("");
    $.ajax({
        url: "feed/albums",
    }).then(function (data) {

        document.getElementById('subscribeButton').style.display="none";
        $(".page-header").html("");
        document.getElementById('showSubscriptions').style.display="none";

        console.log("Albums: " + data);
        console.log("DATA:" + JSON.stringify(data));
        document.getElementById("container").style.display = "block";
        var container = document.getElementById("container");
        container.innerHTML =
            "<br><br><br><h1><b>Albums</b></h1>\n";
        for (i = 0; i < data.length; i++) {

            var album = data[i];
            var albumName = String(album.name);

            container.innerHTML += "\n" +
                "<div id='album\"+album.id+\"' class=\"responsive\" onclick='loadPicturesFromAlbum(" + album.id + ",\"" + albumName + "\")'>\n" +
                "  <div class=\"gallery\">\n" +
                "      <img src=\"data:image/jpeg;base64,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\" alt=\"Trolltunga Norway\" width=\"300\" height=\"200\">\n" +
                "    </a>\n" +
                "    <div class=\"desc\">" + album.name + "</div>\n" +
                "  </div>\n" +
                "</div>\n";

        }
    });


}

function loadAlbumsNames() {
    var element = "<p>Add to album:</p><select id='albums''>";

        $.ajax({
            url: "feed/albumNames",
            type: "POST",
        }).then(function (data) {
            console.log("DATA:" + JSON.stringify(data));

            for (var key in data) {
                if (data.hasOwnProperty(key)) {
                    var albumID = key;
                    var albumName = data[key];
                    var option = document.createElement("option");
                    option.text = albumName;
                    option.value = albumID;
                   element+="<option value=\""+option.value+"\">"+option.text+"</option>";

                    console.log(key + " -> " + data[key]);
                }
            }
            element+="</select><br>";

        });
        return element;
    }


//
function loadPicturesFromAlbum(albumID, albumName) {
    console.log("ALBUM ID:" + albumID);
    console.log("ALBUM NAME: " + albumName);
    $("#newpost").html("");
    $.ajax({
        url: "feed/album",
        type: "POST",
        data: {albumID: albumID},
    }).then(function (data) {
        console.log("DATA:" + JSON.stringify(data));
        $(".page-header").html(albumName);
        document.getElementById("container").style.display = "block";
        insertPosts(data);

    });
}


function showSubscriptions() {
    $("#newpost").html("");
    $.ajax({
        url: "user/getSubscriptions",
        type: "POST",
    }).then(function (data) {
        //hide page header
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
    });
}

function unsubscribe(subscribedToID){
    $.ajax({
        url: "user/unsubscribe",
        type: "POST",
        data:{
            subscribedToID: subscribedToID
        }
    }).then(function (data) {
        if(data==='success'){
            alert('You have unsubscribed to this user.');
            document.getElementById("subscriber"+subscribedToID).innerHTML="";
            document.getElementById("subscriber"+subscribedToID).innerHTML="";

        }
        else {
            alert(data)
        }
    });
}