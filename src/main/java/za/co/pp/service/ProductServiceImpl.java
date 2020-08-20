package za.co.pp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.entity.ProductEntity;
import za.co.pp.data.mapper.ProductMapper;
import za.co.pp.data.repository.ProductRepository;

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
    public ProductDomainObject saveProduct(final ProductDomainObject productDomainObject) {
        final ProductEntity productEntity = productMapper.domainObjectToEntity(productDomainObject);
        final ProductEntity savedProductEntity = productRepository.save(productEntity);

        return productMapper.entityToDomainObject(savedProductEntity);
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
    public ProductDomainObject updateProduct(final ProductDomainObject updatedProductDomainObject, Long productId) {
        ProductDomainObject oldProductDomainObject = this.getProductDomainObject(productId);
        oldProductDomainObject.setName(updatedProductDomainObject.getName());
        oldProductDomainObject.setPrice(updatedProductDomainObject.getPrice());
        oldProductDomainObject.setImage(updatedProductDomainObject.getImage());
        return this.saveProduct(oldProductDomainObject);
    }
}
