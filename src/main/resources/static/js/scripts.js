function getImageSrc(data) {
    return "data:" + imageMime(data) + ";base64," + data;
}

function imageMime(data) {
    if (data.charAt(0) == '/') {
        return "image/jpeg";
    } else if (data.charAt(0) == 'R') {
        return "image/gif";
    } else if (data.charAt(0) == 'i') {
        return "image/png";
    }
}

function addImage(imageID, postUrl) {
    $.ajax({
        url: "/img/get",
        data: {url: postUrl}
    }).then(function (data) {
        $("#" + imageID).attr("src", getImageSrc(data));
    })
}

function getUserInfo(id) {
    $.ajax({
        url: "/user/get",
        data: id
    }).then(function (data) {
        return {
            id: data.id,
            username: data.username,
            firstname: data.firstName,
            lastname: data.lastName,
            email: data.email,
            profilePicUrl: data.profilePicURL
        };
    });
}

function autoComplete() {
    var searchbar = $("#searchbar");
    var input = searchbar.val();
    $("#search-results").empty();
    if (input === "") {
        return;
    }
    $.ajax({
        url: "/util/search",
        data: {input: input}
    }).then(function (data) {
        var users = data.users;
        var tags = data.tags;

        for (var i = 0; i < users.length; i++) {
            var user = users[i];
            var username = user.username;
            var id = user.id;
            var div = "<div onclick='loadUserPosts(" + id + ")' " +
                "<strong>" + username.substr(0, input.length) + "</strong>" +
                username.substr(input.length) +
                "</a></div>";
            $("#search-results").append(div);
        }

        for (var i = 0; i < tags.length; i++) {
            var tag = tags[i];
            var tagname = tag.tag;
            var id = tag.id;
            var div = "<div onclick='loadTagFeed(" + id + ")' " +
                "<strong>" + tagname.substr(0, input.length) + "</strong>" +
                tagname.substr(input.length) +
                "</a></div>";
            $("#search-results").append(div);
        }
    })
}