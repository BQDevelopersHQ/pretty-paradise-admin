package za.co.pp.data.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDomainObject {

    private Long id;
    private String name;
    private Double price;
    private byte[] image;
}
