package project.controller.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import project.controller.managers.exceptions.InfoException;

import java.io.IOException;
import java.sql.SQLException;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(SQLException.class)
    public final ResponseEntity<ErrorMessage> databaseException(SQLException ex){
        ErrorMessage exceptionResponse = new ErrorMessage("Sorry, problems occurred in our database.", "Some mistake happen during query execution.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InfoException.class)
    public final ResponseEntity<ErrorMessage> infoException(InfoException ex){
        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "No worries everything is ok. You doing something wrong but we foreseen it.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public final ResponseEntity<ErrorMessage> ioException(IOException ex){
        ErrorMessage exceptionResponse = new ErrorMessage("Sorry, we have problem during file writing to our servers", "Input output exception. Happened during file upload/download");
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorMessage> exception(Exception ex){
        //TODO 500 page
        ErrorMessage exceptionResponse = new ErrorMessage("Sorry, we have problems with our servers.", "Unexpected internal server error");
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler(UserManager.UserManagerException.class)
//    public final ResponseEntity<ErrorMessage> userException(UserManager.UserManagerException ex){
//        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Non valid data was send to user service.");
//        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(CommentManager.CommentManagerException.class)
//    public final ResponseEntity<ErrorMessage> commentException(CommentManager.CommentManagerException ex){
//        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Comment manager exception.");
//        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(AlbumManager.AlbumManagerException.class)
//    public final ResponseEntity<ErrorMessage> albumException(AlbumManager.AlbumManagerException ex){
//        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Album manager exception.");
//        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//    @ExceptionHandler(LoggingManager.RegistrationException.class)
//    public final ResponseEntity<ErrorMessage> registrationException(LoggingManager.RegistrationException ex){
//        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Registration exception.");
//        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//    @ExceptionHandler(LoggingManager.LoggingException.class)
//    public final ResponseEntity<ErrorMessage> loggingException(LoggingManager.LoggingException ex){
//        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Login exception.");
//        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//    @ExceptionHandler(PostManager.PostManagerException.class)
//    public final ResponseEntity<ErrorMessage> postException(PostManager.PostManagerException ex){
//        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Post manager exception.");
//        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//    @ExceptionHandler(UtilManager.UtilManagerException.class)
//    public final ResponseEntity<ErrorMessage> utilException(UtilManager.UtilManagerException ex){
//        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Util manager exception.");
//        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }


    class ErrorMessage{
        private String message;
        private String details;

        public ErrorMessage(String message, String details) {
            this.message = message;
            this.details = details;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
