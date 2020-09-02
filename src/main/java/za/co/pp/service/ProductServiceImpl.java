package za.co.pp.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.entity.ProductEntity;
import za.co.pp.data.mapper.ProductMapper;
import za.co.pp.data.repository.ProductRepository;
import za.co.pp.exception.PrettyParadiseException;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(final ProductMapper productMapper, final ProductRepository productRepository){
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public ProductDomainObject saveProduct(final ProductDomainObject productDomainObject, final MultipartFile productImage) {
        final ProductEntity productEntity = createProductEntity(productDomainObject, productImage);

        final ProductEntity savedProductEntity = productRepository.save(productEntity);

        return productMapper.entityToDomainObject(savedProductEntity);
    }

    private ProductEntity createProductEntity(final ProductDomainObject productDomainObject, final MultipartFile productImage) {
        final ProductEntity productEntity = productMapper.domainObjectToEntity(productDomainObject);

        try {
            productEntity.setImage(productImage.getBytes());
        } catch (IOException e){
            throw new PrettyParadiseException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return productEntity;
    }

    @Override
    public List<ProductDomainObject> getAllProducts() {
        List<ProductEntity> productEntities = this.productRepository.findAll();

        return productEntities.stream()
                .map(this.productMapper::entityToDomainObject)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDomainObject getProductDomainObject(final Long id) {
        ProductEntity retrievedProductEntity = this.productRepository.getOne(id);

        return productMapper.entityToDomainObject(retrievedProductEntity);
    }

    @Override
    public ProductDomainObject updateProductDetails(final ProductDomainObject updatedProductDetailsDomainObject, final Long productId) {
        ProductDomainObject oldProductDomainObject = this.getProductDomainObject(productId);
        oldProductDomainObject.setName(updatedProductDetailsDomainObject.getName());
        oldProductDomainObject.setPrice(updatedProductDetailsDomainObject.getPrice());
        ProductEntity updatedProductEntity =  productRepository.save(this.productMapper.domainObjectToEntity(oldProductDomainObject));
        return this.productMapper.entityToDomainObject(updatedProductEntity);
    }

    @Override
    public ProductDomainObject updateProductImage(final MultipartFile updatedProductImage, final Long productId) {
        ProductDomainObject oldProductDomainObject = this.getProductDomainObject(productId);
        try{
            oldProductDomainObject.setImage(updatedProductImage.getBytes());
        }catch (IOException e){
            throw new PrettyParadiseException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ProductEntity updatedProductEntity =  productRepository.save(this.productMapper.domainObjectToEntity(oldProductDomainObject));
        return this.productMapper.entityToDomainObject(updatedProductEntity);
    }

    @Override
    public void deleteProduct(final Long productId) {
        this.productRepository.deleteById(productId);
    }
}
