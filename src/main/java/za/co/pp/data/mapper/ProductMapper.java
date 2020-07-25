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

    Product domainToDto(ProductDomainObject productDomainObject);

    ProductDomainObject dtoToDomainObject(Product productDto);

    @Mapping(source = "image", target = "image", qualifiedByName = "byteArrayToMultipartFile")
    ProductDomainObject entityToDomainObject(ProductEntity productEntity);

    @Mapping(source = "image", target = "image", qualifiedByName = "multipartFleToByteArray")
    ProductEntity domainObjectToEntity(ProductDomainObject productDomainObject);
}
