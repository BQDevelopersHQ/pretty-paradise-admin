package za.co.pp.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
public class PrettyParadiseException extends RuntimeException{
    private HttpStatus httpStatus;

    public PrettyParadiseException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }

    public PrettyParadiseException(String message, Throwable cause, HttpStatus httpStatus){
        super(message, cause);
        this.httpStatus = httpStatus;
    }

}
