package za.co.pp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import za.co.pp.data.dto.Problem;

@ControllerAdvice
@Slf4j
public class PrettyParadiseExceptionHandler {

    @ExceptionHandler(PrettyParadiseException.class)
    public ResponseEntity<Problem> handlePrettyParadiseException(PrettyParadiseException e){
        log.error("PrettyParadiseException caught", e);
        Problem problem = new Problem();
        problem.setTitle(e.getHttpStatus().getReasonPhrase());
        problem.setStatus(e.getHttpStatus().value());
        problem.setDetail(e.getMessage());

        return new ResponseEntity<>(problem, getZalandoProblemHttpHeaders(), e.getHttpStatus());
    }

    private HttpHeaders getZalandoProblemHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return httpHeaders;
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Problem> handleMissingServletRequestPartException(MissingServletRequestPartException e){
        log.error("MissingServletRequestPartException caught", e);
        Problem problem = new Problem();
        problem.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        problem.setStatus(HttpStatus.BAD_REQUEST.value());
        problem.setDetail(e.getMessage());

        return new ResponseEntity<>(problem, getZalandoProblemHttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
