
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