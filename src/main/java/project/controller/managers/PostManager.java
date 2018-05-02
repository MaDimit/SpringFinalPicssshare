package project.controller.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.controller.managers.exceptions.InfoException;
import project.model.dao.PostDao;
import project.model.pojo.Post;
import project.model.pojo.User;
import project.model.pojo.wrappers.TagFeedWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Component
public class PostManager {

    private static final int LIKES_MODIFIER = 2;
    private static final int DISLIKES_MODIFIER = 1;
    private static final int COMMENT_MODIFIER = 5;


    public static class PostManagerException extends InfoException {
        public PostManagerException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private PostDao postDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostManager.class);

    //================= Post manipulation =================//
    public void addPost(Post post) throws SQLException, PostManagerException {
        validate(post);
        try {
            postDao.addPost(post);
        }catch (SQLException e){
            LOGGER.error("Data base exception occurred in addPost() for userID:{}, postID:{} . {}", post.getPoster().getId(), post.getId(), e.getMessage());
            throw e;
        }
        LOGGER.info("User {}, id:{} added post with id:{}",post.getPoster().getUsername(), post.getPoster().getId(), post.getId());
    }

    public void deletePost(int postID) throws SQLException {
        //TODO validate if current user is post owner
        try {
            postDao.deletePost(postID);
        } catch (SQLException e) {
            LOGGER.error("Data base exception occurred in deletePost() for postID:{} . {}", postID, e.getMessage());
            throw e;
        }
        LOGGER.info("Post with id:{} was deleted", postID);
    }

    public Post getPost(int postID) throws SQLException{
        try{
            return postDao.getPost(postID);
        }catch (SQLException e){
            LOGGER.error("Data base exception occurred in getPost() for postID:{} . {}", postID, e.getMessage());
            throw e;
        }
    }

    //================= liking/disliking =================//

    public String likePost(Post post, User user) throws PostManagerException, SQLException {
        String text;
        try {
            text = postDao.addLike(post, user);
        }catch (SQLException e){
            LOGGER.error("Data base exception occurred in likePost() for userID:{}, postID:{} . {}", user.getId(), post.getId(), e.getMessage());
            throw e;
        }
        return checkStatus(text);
    }

    public String dislikePost(Post post, User user) throws PostManagerException, SQLException {
        String text;
        try {
            text = postDao.addDislike(post, user);
        }catch (SQLException e){
            LOGGER.error("Data base exception occurred in dislikePost() for userID:{}, postID:{} . {}", user.getId(), post.getId(), e.getMessage());
            throw e;
        }
        return checkStatus(text);
    }

    private String checkStatus(String text) throws PostManagerException{
        switch (text){
            case "success": text="success"; break;
            case "You have already liked this post.": throw new PostManagerException("You have already liked this post.");
            case "removedDislikeAddLike": text="removedDislikeAddLike"; break;
            case "removedLikeAddDislike": text="removedLikeAddDislike"; break;
            case "null": throw new PostManagerException("Problem during adding post.");
        }
        return text;
    }

    //================= Feed =================//

    public List<Post> getUserFeed(int userID) throws SQLException{
        try {
            return postDao.getUserFeed(userID);
        }catch (SQLException e){
            LOGGER.error("Data base exception occurred in getUserFeed() for userID:{} . {}",userID, e.getMessage());
            throw e;
        }
    }

    public List<Post> getFriendsFeed(int userID) throws SQLException{
        try {
            return postDao.getFriendsFeed(userID);
        }catch (SQLException e){
            LOGGER.error("Data base exception occurred in getFriendsFeed() for userID:{} . {}",userID, e.getMessage());
            throw e;
        }
    }

    public List<Post> getTrendingFeed() throws SQLException{
        try {
            List<Post> posts = postDao.getTrendingFeed();
            posts.sort((p1, p2)->{
                return p1.getLikes() * LIKES_MODIFIER
                        + p1.getDislikes() * DISLIKES_MODIFIER
                        + p1.getComments().size() * COMMENT_MODIFIER
                        > p2.getLikes() * LIKES_MODIFIER
                        + p2.getDislikes() * DISLIKES_MODIFIER
                        + p2.getComments().size() * COMMENT_MODIFIER
                        ? -1 : 1;
            });
            return posts;
        }catch (SQLException e){
            LOGGER.error("Data base exception occurred in getTrendingFeed(). {}", e.getMessage());
            throw e;
        }
    }

    public TagFeedWrapper getTagFeed(int tagID) throws SQLException{
        String tagname = null;
        try {
            tagname = postDao.getTagByID(tagID);

            List<Post> posts = postDao.getPostsByTags(tagname);
            return new TagFeedWrapper(posts, tagname);
        } catch (SQLException e) {
            LOGGER.error("Data base exception occurred in getTagFeed(). {}", e.getMessage());
            throw e;
        }
    }

    private void validate(Post post, User user) throws PostManagerException{
        if(post == null){
            throw new PostManagerException("Post does not exist");
        }
        if(user == null){
            throw new PostManagerException("User does not exist");
        }

    }

    private void validate(Post post) throws PostManagerException{
        validate(post, post.getPoster());
    }

    public List<String> addTags(String input, int postID) throws SQLException, PostManagerException{
        if(input == null || input.isEmpty()){
            throw new PostManagerException("Input tags, or upload post without tags.");
        }
        List<String> tags = new ArrayList<>(Arrays.asList(input.split("\\s*(,|\\s)\\s*")));
        List<String> modifiedTags = new ArrayList<>();
        tags.forEach(s -> modifiedTags.add("#"+s));
        try {
            return postDao.addTags(modifiedTags, postID);
        } catch (SQLException e) {
            LOGGER.error("Data base exception occurred in addTags() for tags {} to postID:{} . {}",input, postID, e.getMessage());
            throw e;
        }
    }

}