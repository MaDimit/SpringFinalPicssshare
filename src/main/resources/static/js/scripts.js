function getImageSrc(data) {
    return "data:"+imageMime(data)+";base64," + data;
}

function imageMime(data){
    if(data.charAt(0)=='/'){
        return "image/jpeg";
    }else if(data.charAt(0)=='R'){
        return "image/gif";
    }else if(data.charAt(0)=='i'){
        return "image/png";
    }
}

function addImage(imageID, postUrl) {
    $.ajax({
        url:"/img/get",
        data: {url : postUrl}
    }).then(function (data) {
        $("#" +imageID).attr("src", getImageSrc(data));
    })
}

function getUserInfo(id){
    $.ajax({
        url:"/user/get",
        data:id
    }).then(function (data) {
        return {
            id : data.id,
            username : data.username,
            firstname : data.firstName,
            lastname : data.lastName,
            email : data.email,
            profilePicUrl : data.profilePicURL
        };
    });
}