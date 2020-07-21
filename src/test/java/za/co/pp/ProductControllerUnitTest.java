package za.co.pp;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import za.co.pp.controller.ProductController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
public class ProductControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void canGetCreateResponseFromCreateProductEndpoint() throws Exception{
        MultiValueMap<String, String> productDetails = new LinkedMultiValueMap<>();
        productDetails.add("name", "Gray and Glitter");
        productDetails.add("price", "20.00");

        InputStream fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        MockMultipartFile multipartFile = new MockMultipartFile("image", "test_image.jpeg", MediaType.IMAGE_JPEG_VALUE, fileInputStream);
        this.mockMvc.perform(multipart("/products")
                .file(multipartFile)
                .params(productDetails))
            .andExpect(status().isNotImplemented());

    }

}
