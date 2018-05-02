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

function subscribe(subscribedToID){
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
}
