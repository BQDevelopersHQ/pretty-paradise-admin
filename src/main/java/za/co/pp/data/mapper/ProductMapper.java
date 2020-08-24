package za.co.pp.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.dto.Product;
import za.co.pp.data.entity.ProductEntity;

@Mapper(componentModel = "spring", uses = {ByteArrayMapper.class})
@Component
public interface ProductMapper {
    @Mapping(source = "image", target = "encodedImage", qualifiedByName = "byteArrayToEncodedString")
    Product domainToDto(ProductDomainObject productDomainObject);

    ProductDomainObject dtoToDomainObject(Product productDto);

    ProductEntity domainObjectToEntity(ProductDomainObject productDomainObject);

    ProductDomainObject entityToDomainObject(ProductEntity savedProductEntity);
}
