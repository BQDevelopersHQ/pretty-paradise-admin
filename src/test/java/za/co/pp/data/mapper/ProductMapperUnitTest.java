package za.co.pp.data.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.dto.Product;
import za.co.pp.data.entity.ProductEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductMapperUnitTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void canMapFromDomainToDto() throws IOException {
        ProductDomainObject productDomainObject = createProductDomainObject();

        Product productDto = productMapper.domainToDto(productDomainObject);

        assertDtoAndDomainObjectMapping(productDto, productDomainObject);
    }

    @Test
    void canMapFromDtoToDomainObject() throws IOException {
        Product productDto = createProductDto();

        ProductDomainObject productDomainObject = productMapper.dtoToDomainObject(productDto);

        assertDtoAndDomainObjectMapping(productDto, productDomainObject);
    }

    @Test
    void canMapFromEntityToDomainObject() throws Exception {
        ProductEntity productEntity = createProductEntity();

        ProductDomainObject productDomainObject = productMapper.entityToDomainObject(productEntity);

        assertDomainObjectAndEntityMapping(productDomainObject, productEntity);
    }

    @Test
    void canMapDomainObjectToDomainObject() throws Exception {
        ProductDomainObject productDomainObject = createProductDomainObject();

        ProductEntity productEntity = productMapper.domainObjectToEntity(productDomainObject);

        assertDomainObjectAndEntityMapping(productDomainObject, productEntity);
    }

    private ProductEntity createProductEntity() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Gray and Glitter");
        productEntity.setPrice(20.00);
        productEntity.setImage(createByteImage());
        return productEntity;
    }

    private void assertDomainObjectAndEntityMapping(final ProductDomainObject productDomainObject, final ProductEntity productEntity) throws IOException {
        assertThat(productDomainObject.getId()).isEqualTo(productEntity.getId());
        assertThat(productDomainObject.getName()).isEqualTo(productEntity.getName());
        assertThat(productDomainObject.getPrice()).isEqualTo(productEntity.getPrice());
        assertThat(getByteArrayFromMultipartFile(productDomainObject)).isEqualTo(productEntity.getImage());
    }

    private byte[] getByteArrayFromMultipartFile(ProductDomainObject productDomainObject) throws IOException {
        return productDomainObject.getImage().getBytes();
    }

    private byte[] createByteImage() throws Exception {
        File imageFile = ResourceUtils.getFile("classpath:images/test_image.jpeg");
        InputStream inputStreamFile = new FileInputStream(imageFile);
        return IOUtils.toByteArray(inputStreamFile);
    }

    private Product createProductDto() throws IOException {
        Product productDto = new Product();
        productDto.setId(1L);
        productDto.setName("Gray and Glitter");
        productDto.setPrice(20.00);
        productDto.setImage(createMultipartFile());
        return productDto;
    }

    private ProductDomainObject createProductDomainObject() throws IOException {
        ProductDomainObject productDomainObject = new ProductDomainObject();
        productDomainObject.setId(1L);
        productDomainObject.setName("Gray and Glitter");
        productDomainObject.setPrice(20.00);

        productDomainObject.setImage(createMultipartFile());
        return productDomainObject;
    }

    private MultipartFile createMultipartFile() throws IOException {
        InputStream fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        MultipartFile multipartFile = new MockMultipartFile("image", "test_image.jpeg", MediaType.IMAGE_JPEG_VALUE, fileInputStream);
        return multipartFile;

    }

    private void assertDtoAndDomainObjectMapping(final Product productDto, final ProductDomainObject productDomainObject) {
        assertThat(productDto.getId()).isEqualTo(productDomainObject.getId());
        assertThat(productDto.getId()).isEqualTo(productDomainObject.getId());
        assertThat(productDto.getName()).isEqualTo(productDomainObject.getName());
        assertThat(productDto.getPrice()).isEqualTo(productDomainObject.getPrice());
        assertThat(productDto.getImage()).isEqualTo(productDomainObject.getImage());
    }

}
