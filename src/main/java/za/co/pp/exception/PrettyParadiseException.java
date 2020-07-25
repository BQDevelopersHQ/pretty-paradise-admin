package za.co.pp.exception;

public class PrettyParadiseException extends RuntimeException{
    public PrettyParadiseException(String message){
        super(message);
    }

    public PrettyParadiseException(String message, Throwable cause){
        super(message, cause);
    }

}
