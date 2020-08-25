package za.co.pp.controller.validation;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.dto.Product;
import za.co.pp.exception.PrettyParadiseException;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_SCHEMA;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_TABLE_PRODUCT;
import static za.co.pp.utils.DbSetupCommonOperations.DROP_SCHEMA;

@SpringBootTest
class ProductValidationUnitTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductValidation productValidation;

    @Test
    void canThrowExceptionIfNoPriceDefined() throws Exception {
        Product productDto = createValidProductDetailsDto();
        productDto.setPrice(null);

        validateProductDetailsAndAssertThrowsWithMessage(productDto, "A product needs to have a price.");
    }

    private Product createValidProductDetailsDto() throws IOException {
        Product productDto = new Product();
        productDto.setName("Gray and Glitter");
        productDto.setPrice(20.00);
        return productDto;
    }

    @Test
    void canThrowExceptionIfPriceAttributeIsNegative() throws Exception {
        Product productDto = createValidProductDetailsDto();
        productDto.setPrice(-1.00);

        validateProductDetailsAndAssertThrowsWithMessage(productDto, "A product cannot have a negative price.");
    }

    @Test
    void canThrowExceptionIfNameAttributeIsNull() throws Exception {
        Product productDto = createValidProductDetailsDto();
        productDto.setName(null);

        validateProductDetailsAndAssertThrowsWithMessage(productDto, "A product must have a name.");
    }

    @Test
    void canThrowExceptionIfImageIsNotJpeg() {
        MultipartFile multipartFile = new MockMultipartFile("image", "Hello World".getBytes());

        try {
            ProductValidation.validateProductImage(multipartFile);
            fail("An invalid product image was passed, a PrettyParadiseException was expected.");
        } catch (PrettyParadiseException e) {
            assertThat(e.getMessage()).isEqualToIgnoringCase("An image of file type text/plain has been provided, accepted types: image/jpeg, image/png");
        }
    }

    @Test
    void doesNotThrowExceptionWhenImageIsOfTypePng() throws Exception {
        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.png"));
        MultipartFile multipartFile = new MockMultipartFile("test_image", inputStream);

        assertThatCode(() -> ProductValidation.validateProductImage(multipartFile))
                .doesNotThrowAnyException();

    }

    @Test
    void doesNotThrowExceptionWhenImageIsOfTypeJpeg() throws Exception {
        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        MultipartFile multipartFile = new MockMultipartFile("test_image", inputStream);

        assertThatCode(() -> ProductValidation.validateProductImage(multipartFile))
                .doesNotThrowAnyException();
    }

    @Test
    void doesNotThrowExceptionWhenImageIsOfTypeJpg() throws Exception {
        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpg"));
        MultipartFile multipartFile = new MockMultipartFile("test_image", inputStream);

        assertThatCode(() -> ProductValidation.validateProductImage(multipartFile))
                .doesNotThrowAnyException();
    }

    @Test
    void doesNotThrowExceptionForAValidProductDto() throws Exception {
        Product productDto = createValidProductDetailsDto();

        assertThatCode(() -> ProductValidation.validateProductDetails(productDto))
                .doesNotThrowAnyException();

    }

    @Test
    void canThrowExceptionIfGetProductOnInvalidId() {
        Operation operations = sequenceOf(
                DROP_SCHEMA,
                CREATE_SCHEMA,
                CREATE_TABLE_PRODUCT
        );
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();

        assertThatThrownBy(() -> {
            this.productValidation.validateIdExists(2L);
        })
                .isInstanceOf(PrettyParadiseException.class)
                .hasMessage("The provided product id 2 does not exist");
    }

    private void validateProductDetailsAndAssertThrowsWithMessage(final Product productDto, String errorMessage) {
        try {
            ProductValidation.validateProductDetails(productDto);
            fail("An invalid attribute was provided, a PrettyParadiseException was expected.");
        } catch (PrettyParadiseException e) {
            assertThat(e.getMessage()).isEqualToIgnoringCase(errorMessage);
        }
    }

}
