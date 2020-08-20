package za.co.pp.controller;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.apache.commons.io.IOUtils;
import org.hibernate.dialect.DB2Dialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.dto.Product;
import za.co.pp.data.entity.ProductEntity;
import za.co.pp.data.repository.ProductRepository;
import za.co.pp.exception.PrettyParadiseException;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static com.ninja_squad.dbsetup.Operations.sql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_SCHEMA;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_TABLE_PRODUCT;
import static za.co.pp.utils.DbSetupCommonOperations.DROP_SCHEMA;
import static za.co.pp.utils.DbSetupCommonOperations.INSERT_INTO_PRODUCTS_TABLE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerUnitTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        Operation operations = sequenceOf(
                DROP_SCHEMA,
                CREATE_SCHEMA,
                CREATE_TABLE_PRODUCT
        );

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();
    }

    @Test
    void canGetCreateResponseFromCreateProductEndpoint() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();

        HttpEntity<MultiValueMap<String, Object>> entity = createContentTypeHeader(requestBody);

        ResponseEntity<Product> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", entity, Product.class);

        assertNewlyCreatedProductResponse(responseEntity);
    }

    @Test
    @DisplayName("Given a request with no image, when posting to the /products endpoint, then a bad request response is received")
    void canGetEBadRequest() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();
        requestBody.remove("image");

        HttpEntity<MultiValueMap<String, Object>> entity = createContentTypeHeader(requestBody);

        ResponseEntity<Product> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", entity, Product.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Given a request with a product name of null, when posting to the /products endpoint, then a server error response is received")
    void canGetInternalServerError() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();
        requestBody.set("name", null);

        HttpEntity<MultiValueMap<String, Object>> entity = createContentTypeHeader(requestBody);

        ResponseEntity<Product> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", entity, Product.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Given the products table has 2 records, when get method called on /products endpoint, then returned with 2 expected products")
    public void canGetAllProducts(){
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), INSERT_INTO_PRODUCTS_TABLE);
        dbSetup.launch();

        ResponseEntity<Object> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/products", Object.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        // add assertions to test the content of the body
    }

    private HttpEntity<MultiValueMap<String, Object>> createContentTypeHeader(final MultiValueMap<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(requestBody, headers);
    }

    @Test
    @DisplayName("Given a product record exists with id 1, when put method called on /products/1, then the record is updated successfully")
    void canUpdateProduct() throws Exception {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), INSERT_INTO_PRODUCTS_TABLE);
        dbSetup.launch();

        restTemplate.put("http://localhost:" + port + "/products/1", getValidRequest());

        ProductEntity updatedProductEntity = this.productRepository.findById(1L).get();
        assertThat(updatedProductEntity.getId()).isEqualTo(1L);
        assertThat(updatedProductEntity.getName()).isEqualTo("Gray and Glitter");
        assertThat(updatedProductEntity.getPrice()).isEqualTo(20.00);
    }

    private MultiValueMap<String, Object> getValidRequest() throws FileNotFoundException {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap();
        requestBody.add("name", "Gray and Glitter");
        requestBody.add("price", 20.00);
        FileSystemResource image = new FileSystemResource(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        requestBody.add("image", image);
        return requestBody;
    }

    private void assertNewlyCreatedProductResponse(final ResponseEntity<Product> responseEntity) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Product productResponse = responseEntity.getBody();
        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getId()).isEqualTo(1L);
        assertThat(productResponse.getName()).isEqualTo("Gray and Glitter");
    }

}



