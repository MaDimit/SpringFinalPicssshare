package project.controller.servlets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import project.controller.managers.*;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    public final ResponseEntity<ErrorMessage> somethingWentWrong(Exception ex){
//        System.out.println("HOORAY!");
//        ex.printStackTrace();
//        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "WHAT ELSE DO OU WANT TO ADD?");
//        return new ResponseEntity<ErrorMessage>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(UserManager.UserManagerException.class)
    public final ResponseEntity<ErrorMessage> userException(UserManager.UserManagerException ex){
        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "User manager exception.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(CommentManager.CommentManagerException.class)
    public final ResponseEntity<ErrorMessage> commentException(CommentManager.CommentManagerException ex){
        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Comment manager exception.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AlbumManager.AlbumManagerException.class)
    public final ResponseEntity<ErrorMessage> albumException(AlbumManager.AlbumManagerException ex){
        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Album manager exception.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(LoggingManager.RegistrationException.class)
    public final ResponseEntity<ErrorMessage> registrationException(LoggingManager.RegistrationException ex){
        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Registration exception.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(LoggingManager.LoggingException.class)
    public final ResponseEntity<ErrorMessage> loggingException(LoggingManager.LoggingException ex){
        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Login exception.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(PostManager.PostManagerException.class)
    public final ResponseEntity<ErrorMessage> postException(PostManager.PostManagerException ex){
        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Post manager exception.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(UtilManager.UtilManagerException.class)
    public final ResponseEntity<ErrorMessage> utilException(UtilManager.UtilManagerException ex){
        ErrorMessage exceptionResponse = new ErrorMessage(ex.getMessage(), "Util manager exception.");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


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
