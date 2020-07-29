package za.co.pp.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.dto.Product;
import za.co.pp.data.entity.ProductEntity;

@Mapper(componentModel = "spring", uses = {MultipartFileMapper.class})
@Component
public interface ProductMapper {
    @Mapping(target = "image", ignore = true)
    Product domainToDto(ProductDomainObject productDomainObject);

    @Mapping(source = "image", target = "image", qualifiedByName = "multipartFleToByteArray")
    ProductDomainObject dtoToDomainObject(Product productDto);

    ProductEntity domainObjectToEntity(ProductDomainObject productDomainObject);

    ProductDomainObject entityToDomainObject(ProductEntity savedProductEntity);
}
