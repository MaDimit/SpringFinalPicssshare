package project.controller.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.model.dao.PostDao;
import project.model.pojo.Post;
import project.model.pojo.User;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
@Component
public class PostManager {

    public static class PostException extends Exception{
        public PostException(String msg) {
            super(msg);
        }
    }
    @Autowired
    private PostDao postDao;



    //================= Post manipulation =================//
    public void addPost(Post post)throws PostException{
        validate(post);
        try {
            postDao.addPost(post);
        }catch (SQLException e){
            throw new PostException("Problem during adding post to DB");
        }
    }

    public void deletePost(Post post) throws PostException{
        validate(post);
        try{
            postDao.deletePost(post.getId());
        }catch (SQLException e){
            throw new PostException("Problem during removing post from DB");
        }
    }

    public Post getPost(int postID) throws PostException{
        try{
            return postDao.getPost(postID);
        }catch (SQLException e){
            throw new PostException("Post with this ID can't be found");
        }
    }

    //================= liking/disliking =================//

    public String likePost(Post post, User user) throws PostException{
        String likerPost = user.getId() + "" + post.getId();

        try{
            //get message to see what happened
            return postDao.addLike(post, user);
        }catch (SQLException e){
            throw new PostException("Problem during like adding");
        }
    }

    public String dislikePost(Post post, User user) throws PostException{
        String likerPost = user.getId() + "" + post.getId();
        try{
            return postDao.addDislike(post, user);
        }catch (SQLException e){
            throw new PostException("Problem during dislike adding");
        }
    }

    //================= Feed =================//

    public List<Post> getUserFeed(int userID) throws PostException{
        try {
            List<Post> posts = postDao.getUserFeed(userID);
            return posts;
        }catch (SQLException e){
            throw new PostException("Problem during user feed creation");
        }
    }

    public List<Post> getFriendsFeed(int userID) throws PostException, SQLException{
//        try {
            List<Post> posts = postDao.getFriendsFeed(userID);
            return posts;
//        }catch (SQLException e){
//            throw new PostException("Problem during friends feed creation");
//        }
    }

    public List<Post> getTrendingFeed() throws PostException{
        try {
            List<Post> posts = postDao.getTrendingFeed();
            posts.sort((p1, p2)->p1.getLikes() + p1.getDislikes() > p2.getLikes() + p2.getDislikes() ? -1 : 1);
            return posts;
        }catch (SQLException e){
            throw new PostException("Problem during trending feed creation");
        }
    }

    private void validate(Post post, User user) throws PostException{
        if(post == null){
            throw new PostException("Post does not exist");
        }
        if(user == null){
            throw new PostException("User does not exist");
        }

    }

    private void validate(Post post) throws PostException{
        validate(post, post.getPoster());
    }

}