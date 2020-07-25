package za.co.pp.controller.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.dto.Product;
import za.co.pp.exception.PrettyParadiseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
class ProductValidationUnitTest {

    @Test
    void canThrowExceptionIfNoPriceDefined() throws Exception {
        Product productDto = createValidProductDto();
        productDto.setPrice(null);

        validateAndAssertThrowsWithMessage(productDto, "A product needs to have a price.");
    }

    private Product createValidProductDto() throws IOException {
        Product productDto = new Product();
        productDto.setName("Gray and Glitter");
        productDto.setImage(createMultipartFile());
        productDto.setPrice(20.00);
        return productDto;
    }

    @Test
    void canThrowExceptionIfPriceAttributeIsNegative() throws Exception {
        Product productDto = createValidProductDto();
        productDto.setPrice(-1.00);

        validateAndAssertThrowsWithMessage(productDto, "A product cannot have a negative price.");
    }

    @Test
    void canThrowExceptionIfNameAttributeIsNull() throws Exception {
        Product productDto = createValidProductDto();
        productDto.setName(null);

        validateAndAssertThrowsWithMessage(productDto, "A product must have a name.");
    }

    @Test
    void canThrowExceptionIfImageIsNotJpeg() throws Exception {
        Product productDto = createValidProductDto();

        MultipartFile multipartFile = new MockMultipartFile("image", "Hello World".getBytes());
        productDto.setImage(multipartFile);

        validateAndAssertThrowsWithMessage(productDto,
                "An image of file type text/plain has been provided, accepted types: image/jpeg, image/png");
    }

    @Test
    void doesNotThrowExceptionWhenImageIsOfTypePng() throws Exception {
        Product productDto = createValidProductDto();

        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.png"));
        MultipartFile multipartFile = new MockMultipartFile("test_image", inputStream);

        productDto.setImage(multipartFile);

        assertThatCode(() -> ProductValidation.validateProduct(productDto))
                .doesNotThrowAnyException();

    }

    @Test
    void doesNotThrowExceptionWhenImageIsOfTypeJpeg() throws Exception {
        Product productDto = createValidProductDto();

        assertThatCode(() -> ProductValidation.validateProduct(productDto))
                .doesNotThrowAnyException();
    }

    @Test
    void doesNotThrowExceptionWhenImageIsOfTypeJpg() throws Exception {
        Product productDto = createValidProductDto();

        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpg"));
        MultipartFile multipartFile = new MockMultipartFile("test_image", inputStream);

        assertThatCode(() -> ProductValidation.validateProduct(productDto))
                .doesNotThrowAnyException();
    }

    @Test
    void doesNotThrowExceptionForAValidProductDto() throws Exception {
        Product productDto = createValidProductDto();

        assertThatCode(() -> ProductValidation.validateProduct(productDto))
                .doesNotThrowAnyException();

    }

    private void validateAndAssertThrowsWithMessage(final Product productDto, String errorMessage) {
        try {
            ProductValidation.validateProduct(productDto);
            fail("An invalid attribute was provided, a PrettyParadiseException was expected.");
        } catch (PrettyParadiseException e) {
            assertThat(e.getMessage()).isEqualToIgnoringCase(errorMessage);
        }
    }

    private MultipartFile createMultipartFile() throws IOException {
        File fileImage = ResourceUtils.getFile("classpath:images/test_image.jpeg");
        InputStream inputStream = new FileInputStream(fileImage);
        return new MockMultipartFile("test_image", inputStream);
    }

}
