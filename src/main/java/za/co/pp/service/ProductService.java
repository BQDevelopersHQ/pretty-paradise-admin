package za.co.pp.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.domain.ProductDomainObject;

public interface ProductService {

    ProductDomainObject saveProduct(ProductDomainObject productDomainObject, final MultipartFile productImage);

    List<ProductDomainObject> getAllProducts();

    ProductDomainObject getProductDomainObject(Long id);

    ProductDomainObject updateProductDetails(ProductDomainObject dtoToDomainObject, Long productId);

    ProductDomainObject updateProductImage(MultipartFile updatedProductImage, Long productId);
}
