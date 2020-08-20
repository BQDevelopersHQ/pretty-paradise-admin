package za.co.pp.controller.validation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.dto.Product;
import za.co.pp.data.repository.ProductRepository;
import za.co.pp.exception.PrettyParadiseException;

@Component
public class ProductValidation {

    private final ProductRepository productRepository;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);

    @Autowired
    public ProductValidation(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static void validateProduct(final Product productDto) {
        validateProductPrice(productDto.getPrice());
        validateProductName(productDto.getName());
        validateProductImage(productDto.getImage());
    }

    private static void validateProductImage(final MultipartFile image) {
        final Tika tika = new Tika();
        try {
            String mimeType = tika.detect(image.getBytes());
            if (!ALLOWED_FILE_TYPES.contains(mimeType)) {
                throw new PrettyParadiseException(
                        String.format("An image of file type %s has been provided, accepted types: %s",
                                mimeType,
                                String.join(", ", ALLOWED_FILE_TYPES)));
            }
        } catch (IOException e) {
            throw new PrettyParadiseException(e.getMessage(), e);
        }

    }

    private static void validateProductName(final String productName) {
        Optional.ofNullable(productName)
                .orElseThrow(() -> new PrettyParadiseException("A product must have a name."));
    }

    private static void validateProductPrice(final Double productPrice) {
        Optional.ofNullable(productPrice)
                .orElseThrow(() -> new PrettyParadiseException("A product needs to have a price."));

        if (productPrice < 0) {
            throw new PrettyParadiseException("A product cannot have a negative price.");
        }
    }

    public void validateIdExists(final Long productId) {
        productRepository.findById(productId)
                .orElseThrow( () -> new PrettyParadiseException(String.format("The provided product id %s does not exist", productId)));
    }
}
