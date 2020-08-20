package za.co.pp.service;

import java.util.List;

import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.dto.Product;

public interface ProductService {

    ProductDomainObject saveProduct(ProductDomainObject productDomainObject);

    List<ProductDomainObject> getAllProducts();

    ProductDomainObject getProductDomainObject(Long id);
}
