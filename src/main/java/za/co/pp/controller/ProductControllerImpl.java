package za.co.pp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.controller.validation.ProductValidation;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.dto.Product;
import za.co.pp.data.mapper.ProductMapper;
import za.co.pp.service.ProductService;

@RestController
@CrossOrigin
public class ProductControllerImpl implements ProductController {

    private final ProductValidation productValidation;

    private final ProductMapper productMapper;

    private final ProductService productService;

    @Autowired
    public ProductControllerImpl(ProductMapper productMapper, ProductService productService, ProductValidation productValidation) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.productValidation = productValidation;
    }

    @Override
    public ResponseEntity<Product> createNewProduct(final Product product, final MultipartFile productImage) {
        ProductValidation.validateProductDetails(product);
        ProductValidation.validateProductImage(productImage);

        final ProductDomainObject productDomainObject = productMapper.dtoToDomainObject(product);

        final ProductDomainObject savedProductDomainObject = productService.saveProduct(productDomainObject, productImage);
        return new ResponseEntity<>(productMapper.domainToDto(savedProductDomainObject), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<Product>> getAllProducts() {
        List<ProductDomainObject> productDomainObjects = productService.getAllProducts();

        List<Product> products = productDomainObjects.stream()
                .map(this.productMapper::domainToDto)
                .collect(Collectors.toList());

        return new ResponseEntity(products, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Product> getProduct(final Long productId) {
        productValidation.validateIdExists(productId);
        ProductDomainObject retrievedProductDomainObject = productService.getProductDomainObject(productId);
        Product retrievedProduct = productMapper.domainToDto(retrievedProductDomainObject);
        return new ResponseEntity<>(retrievedProduct, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Product> editProductDetails(final Long productId, final Product updatedProductDetails) {
        productValidation.validateIdExists(productId);
        ProductValidation.validateProductDetails(updatedProductDetails);
        ProductDomainObject updatedProductDetailsDomainObject = this.productMapper.dtoToDomainObject(updatedProductDetails);
        ProductDomainObject savedProductDetailsDomainObject = productService.updateProductDetails(updatedProductDetailsDomainObject, productId);
        return new ResponseEntity<>(
                productMapper.domainToDto(savedProductDetailsDomainObject),
                HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Product> editProductImage(final Long productId, final MultipartFile updatedProductImage) {
        productValidation.validateIdExists(productId);
        ProductValidation.validateProductImage(updatedProductImage);
        ProductDomainObject savedUpdatedProductDetailsDomainObject = productService.updateProductImage(updatedProductImage, productId);
        return new ResponseEntity<>(
                productMapper.domainToDto(savedUpdatedProductDetailsDomainObject),
                HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteProduct(final Long productId) {
        productValidation.validateIdExists(productId);
        this.productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
