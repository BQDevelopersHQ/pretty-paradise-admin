package za.co.pp.data.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class Problem {
    String title;
    int status;
    String detail;

}
