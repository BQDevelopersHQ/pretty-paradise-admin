package za.co.pp.service;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import za.co.pp.data.domain.ProductDomainObject;
import za.co.pp.data.entity.ProductEntity;
import za.co.pp.data.repository.ProductRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ProductServiceUnitTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Test
    void canSaveNewProduct() throws Exception {
        ProductDomainObject productDomainObject = new ProductDomainObject();
        productDomainObject.setName("Gray and Glitter");
        productDomainObject.setPrice(20.00);

        InputStream fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        MockMultipartFile multipartFile = new MockMultipartFile("image", "test_image.jpeg", MediaType.IMAGE_JPEG_VALUE, fileInputStream);
        productDomainObject.setImage(multipartFile);

        productService.saveProduct(productDomainObject);

        verify(productRepository).save(any(ProductEntity.class));
    }

}
