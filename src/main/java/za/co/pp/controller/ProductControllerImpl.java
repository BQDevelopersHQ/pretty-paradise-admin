package za.co.pp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestPart;
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
    public ResponseEntity<Product> createNewProduct(final MultiValueMap<String, String> productDetails, final MultipartFile image) {
        final Product productDto = new Product();
        productDto.setName(productDetails.get("name").get(0));
        productDto.setPrice(Double.parseDouble(productDetails.get("price").get(0)));
        productDto.setImage(image);

        ProductValidation.validateProduct(productDto);
        final ProductDomainObject productDomainObject = productMapper.dtoToDomainObject(productDto);

        final ProductDomainObject savedProductDomainObject = productService.saveProduct(productDomainObject);
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
    public ResponseEntity<Product> editProduct(final Long productId, MultiValueMap<String, String> updatedProduct, final MultipartFile image) {
        productValidation.validateIdExists(productId);

        final Product productDto = new Product();
        productDto.setId(1L);
        productDto.setName(updatedProduct.get("name").get(0));
        productDto.setPrice(Double.parseDouble(updatedProduct.get("price").get(0)));
        productDto.setImage(image);
        ProductValidation.validateProduct(productDto);

        ProductDomainObject updatedProductDomainObject = productService.updateProduct(this.productMapper.dtoToDomainObject(productDto), productId);
        return new ResponseEntity<>(
                productMapper.domainToDto(updatedProductDomainObject),
                HttpStatus.CREATED);
    }
}
