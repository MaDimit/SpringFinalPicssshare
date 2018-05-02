package project.controller.managers.exceptions;

public class InfoException extends Exception{
    public InfoException() {
    }

    public InfoException(String s) {
        super(s);
    }

    public InfoException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InfoException(Throwable throwable) {
        super(throwable);
    }

    public InfoException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
