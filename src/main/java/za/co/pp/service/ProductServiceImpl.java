package za.co.pp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.entity.ProductEntity;
import za.co.pp.data.mapper.ProductMapper;
import za.co.pp.data.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDomainObject saveProduct(final ProductDomainObject productDomainObject) {
        ProductEntity productEntity = productMapper.domainObjectToEntity(productDomainObject);
        ProductEntity savedProductEntity = productRepository.save(productEntity);
        return productMapper.entityToDomainObject(savedProductEntity);
    }
}
