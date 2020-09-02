package za.co.pp.controller;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import za.co.pp.data.dto.Problem;
import za.co.pp.data.dto.Product;
import za.co.pp.data.entity.ProductEntity;
import za.co.pp.data.repository.ProductRepository;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_SCHEMA;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_TABLE_PRODUCT;
import static za.co.pp.utils.DbSetupCommonOperations.DROP_SCHEMA;
import static za.co.pp.utils.DbSetupCommonOperations.INSERT_INTO_PRODUCTS_TABLE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

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
    @DisplayName("Given a valid request, when posting to the /products endpoint, then a created response is received and a new record is created")
    void canGetCreateResponseFromCreateProductEndpoint() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();

        ResponseEntity<Product> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", requestBody, Product.class);

        assertNewlyCreatedProductResponse(responseEntity);
    }

    @Test
    @DisplayName("Given a request with no image, when posting to the /products endpoint, then a zalando problem response is received")
    void canGetBadRequestFromNoProductImage() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();
        requestBody.remove("productImage");

        ResponseEntity<Problem> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", requestBody, Problem.class);

        assertZalandoProblemResponse(responseEntity, HttpStatus.BAD_REQUEST, "Required request part 'productImage' is not present");
    }

    @Test
    @DisplayName("Given a request with no product details, when posting to the /products endpoint, then a zalando problem response is received")
    void canGetBadRequestFromNoProductDetails() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();
        requestBody.remove("productDetails");

        ResponseEntity<Problem> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", requestBody, Problem.class);

        assertZalandoProblemResponse(responseEntity, HttpStatus.BAD_REQUEST, "Required request part 'productDetails' is not present");
    }

    @Test
    @DisplayName("Given a request with a product name of null, when posting to the /products endpoint, then a zalando problem response is received")
    void canGetBadRequestWhenProductNameIsNull() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();

        Product product = new Product();
        product.setPrice(25.00);
        requestBody.set("productDetails", product);

        ResponseEntity<Problem> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", requestBody, Problem.class);

        assertZalandoProblemResponse(responseEntity, HttpStatus.BAD_REQUEST, "A product must have a name.");

    }

    @Test
    @DisplayName("Given a request with a product price of null, when posting to the /products endpoint, then a zalando problem response is received")
    void canGetBadRequestWhenProductPriceIsNull() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();

        Product product = new Product();
        product.setName("Pink and Pretty");
        requestBody.set("productDetails", product);

        ResponseEntity<Problem> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", requestBody, Problem.class);

        assertZalandoProblemResponse(responseEntity, HttpStatus.BAD_REQUEST, "A product needs to have a price.");

    }

    @Test
    @DisplayName("Given a request with a product price of negative, when posting to the /products endpoint, then a zalando problem response is received")
    void canGetBadRequestWhenProductPriceIsNegative() throws Exception {
        MultiValueMap<String, Object> requestBody = getValidRequest();

        Product product = new Product();
        product.setName("Pink and Pretty");
        product.setPrice(-20.00);
        requestBody.set("productDetails", product);

        ResponseEntity<Problem> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/products", requestBody, Problem.class);

        assertZalandoProblemResponse(responseEntity, HttpStatus.BAD_REQUEST, "A product cannot have a negative price.");
    }

    @Test
    @DisplayName("Given the products table has 2 records, when get method called on /products endpoint, then returned with 2 expected products")
    void canGetAllProducts() {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), INSERT_INTO_PRODUCTS_TABLE);
        dbSetup.launch();

        ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/products", Product[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Product[] products = responseEntity.getBody();
        assertThat(products).hasSize(2);
    }

    @Test
    @DisplayName("Given a product record exists with id 1," +
            " when put method called on /products/1/details," +
            " with updated product details then the product details reflect the update")
    void canUpdateProductDetails() {
        createExistingProductEntity();

        ResponseEntity<Product> responseEntity = makeValidPutRequestWithUpdatedDetails();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertRecordUpdatedWithNewProductDetails(responseEntity.getBody());
    }

    private ResponseEntity<Product> makeValidPutRequestWithUpdatedDetails() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity httpEntity = new HttpEntity(getValidProductDetailsUpdateRequest(), httpHeaders);
        ResponseEntity<Product> responseEntity = restTemplate.exchange("http://localhost:" + port + "/products/1/details", HttpMethod.PUT, httpEntity, Product.class);
        return responseEntity;
    }

    @Test
    @DisplayName("Given a product record exists with id 1," +
            " when put method called on /products/1/image," +
            " with an updated product image then the product record must reflect the update")
    void canUpdateProductImage() throws Exception {
        ProductEntity existingProductEntity = createExistingProductEntity();

        ResponseEntity<Product> responseEntity = makeValidPutRequestWithUpdatedImage();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertRecordUpdatedWithNewImage(existingProductEntity);
    }

    private ResponseEntity<Product> makeValidPutRequestWithUpdatedImage() throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity httpEntity = new HttpEntity(getValidProductImageUpdateRequest(), httpHeaders);
        ResponseEntity<Product> responseEntity = restTemplate.exchange("http://localhost:" + port + "/products/1/image", HttpMethod.PUT, httpEntity, Product.class);
        return responseEntity;
    }

    @Test
    @DisplayName("Given a product with id 1 does not exist, " +
            "when put method called on /products/1/image, " +
            "then a zalando problem response is received")
    void canGetBadRequestWhenUpdatingNotExistingProductImage() throws Exception{
        ResponseEntity<Problem> responseEntity = makeBadPutRequestWithUpdatedImage();

        assertZalandoProblemResponse(responseEntity, HttpStatus.BAD_REQUEST, "The provided product id 1 does not exist");
    }

    @Test
    @DisplayName("Given a product with id 1 does not exist, " +
            "when put method called on /products/1/details, " +
            "then a zalando problem response is received")
    void canGetBadRequestWhenUpdatingNotExistingProductDetails(){
        ResponseEntity<Problem> responseEntity = makeBadPutRequestWithUpdatedDetails();

        assertZalandoProblemResponse(responseEntity, HttpStatus.BAD_REQUEST, "The provided product id 1 does not exist");
    }

    @Test
    @DisplayName("Given a product exists with id 1, " +
            "when delete called in /products/1," +
            "then the product record is removed and the response is 204 no content")
    void canDeleteProduct() {
        Operation operations = sequenceOf(
                INSERT_INTO_PRODUCTS_TABLE
        );
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();

        ResponseEntity<Void> responseEntity = restTemplate.exchange("http://localhost:" + port + "/products/1", HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(this.productRepository.findById(1L)).isEmpty();

    }

    private void assertRecordUpdatedWithNewImage(final ProductEntity existingProductEntity) {
        ProductEntity updatedProductEntity = this.productRepository.findById(1L).get();
        assertThat(Arrays.equals(updatedProductEntity.getImage(), existingProductEntity.getImage())).isFalse();
    }

    private void assertRecordUpdatedWithNewProductDetails(Product product) {
        ProductEntity updatedProductEntity = this.productRepository.findById(1L).get();
        assertThat(updatedProductEntity.getId()).isEqualTo(1L);
        assertThat(updatedProductEntity.getName()).isEqualTo(product.getName());
        assertThat(updatedProductEntity.getPrice()).isEqualTo(product.getPrice());
    }

    private ResponseEntity<Problem> makeBadPutRequestWithUpdatedImage() throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity httpEntity = new HttpEntity(getValidProductImageUpdateRequest(), httpHeaders);
        return restTemplate.exchange("http://localhost:" + port + "/products/1/image", HttpMethod.PUT, httpEntity, Problem.class);
    }

    private ResponseEntity<Problem> makeBadPutRequestWithUpdatedDetails() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity httpEntity = new HttpEntity(getValidProductDetailsUpdateRequest(), httpHeaders);
        ResponseEntity<Problem> responseEntity = restTemplate.exchange("http://localhost:" + port + "/products/1/details", HttpMethod.PUT, httpEntity, Problem.class);
        return responseEntity;
    }

    private ProductEntity createExistingProductEntity() {
        ProductEntity existingProductEntity = new ProductEntity();
        existingProductEntity.setName("Pink and Pretty");
        existingProductEntity.setPrice(20.00);
        existingProductEntity.setImage(new byte[100]);
        this.productRepository.save(existingProductEntity);
        return existingProductEntity;
    }

    private MultiValueMap<String, Product> getValidProductDetailsUpdateRequest() {
        MultiValueMap<String, Product> requestBody = new LinkedMultiValueMap<>();

        Product productDetails = new Product();
        productDetails.setName("Gray and Glitter");
        productDetails.setPrice(25.00);
        requestBody.add("productDetails", productDetails);

        return requestBody;
    }

    private MultiValueMap<String, Object> getValidProductImageUpdateRequest() throws IOException {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        FileSystemResource image = new FileSystemResource(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        requestBody.add("productImage", image);
        return requestBody;
    }

    private MultiValueMap<String, Object> getValidRequest() throws FileNotFoundException {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap();
        Product productDetails = new Product();
        productDetails.setName("Gray and Glitter");
        productDetails.setPrice(25.00);
        requestBody.add("productDetails", productDetails);

        FileSystemResource image = new FileSystemResource(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        requestBody.add("productImage", image);
        return requestBody;
    }

    private void assertNewlyCreatedProductResponse(final ResponseEntity<Product> responseEntity) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Product productResponse = responseEntity.getBody();
        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getId()).isEqualTo(1L);
        assertThat(productResponse.getName()).isEqualTo("Gray and Glitter");
    }

    private void assertZalandoProblemResponse(final ResponseEntity<Problem> responseEntity,
                                              final HttpStatus httpStatus,
                                              final String message) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(httpStatus);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(httpStatus.value());
        assertThat(responseEntity.getBody().getDetail()).isEqualTo(message);
        assertThat(responseEntity.getBody().getTitle()).isEqualTo(httpStatus.getReasonPhrase());
    }
}



