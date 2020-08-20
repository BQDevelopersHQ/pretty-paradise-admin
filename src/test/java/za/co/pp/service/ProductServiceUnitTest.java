package za.co.pp.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.entity.ProductEntity;
import za.co.pp.data.mapper.ProductMapper;
import za.co.pp.data.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceUnitTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Test
    void canSaveNewProduct() throws Exception {
        ProductDomainObject productDomainObject = new ProductDomainObject();
        productDomainObject.setName("Gray and Glitter");
        productDomainObject.setPrice(20.00);

        InputStream fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        productDomainObject.setImage(IOUtils.toByteArray(fileInputStream));

        productService.saveProduct(productDomainObject);

        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void canGetAllProducts() {
        productService.getAllProducts();

        verify(productRepository).findAll();
    }

    @Test
    void canGetProduct() throws Exception {
        InputStream fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        ProductEntity productEntity = getProductEntity(1L, "Pink and Pretty", 20.00, IOUtils.toByteArray(fileInputStream));
        when(productRepository.getOne(any())).thenReturn(productEntity);

        ProductDomainObject retrievedProductDomainObject =
                this.productService.getProductDomainObject(1L); // valid product id returned by mock repo

        ProductDomainObject expectedProductDomainObject = productMapper.entityToDomainObject(productEntity);
        assertActualAndExpected(retrievedProductDomainObject, expectedProductDomainObject);
    }

    private void assertActualAndExpected(final ProductDomainObject retrievedProductDomainObject, final ProductDomainObject expectedProductDomainObject) {
        assertThat(retrievedProductDomainObject).isNotNull();

        assertThat(retrievedProductDomainObject.getId()).isEqualTo(expectedProductDomainObject.getId());
        assertThat(retrievedProductDomainObject.getName()).isEqualTo(expectedProductDomainObject.getName());
        assertThat(retrievedProductDomainObject.getPrice()).isEqualTo(expectedProductDomainObject.getPrice());
        assertThat(Arrays.equals(retrievedProductDomainObject.getImage(), expectedProductDomainObject.getImage())).isTrue();
    }


    private ProductEntity getProductEntity(Long id, String name, Double price, byte[] image) throws IOException {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(id);
        productEntity.setPrice(price);
        productEntity.setName(name);
        productEntity.setImage(image);
        return productEntity;
    }

}
