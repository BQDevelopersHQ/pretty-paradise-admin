package za.co.pp.data.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductDomainObject {
    private Long id;
    private String name;
    private Double price;
    private MultipartFile image;
}
