package za.co.pp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import za.co.pp.data.dto.Product;

public interface ProductController {

    @RequestMapping(value = "/products", method = RequestMethod.POST)
    default ResponseEntity<Product> createNewProduct(@RequestPart(value = "productDetails") Product product, @RequestPart(value = "productImage") MultipartFile productImage) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    default ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/products/{productId}", method = RequestMethod.GET)
    default ResponseEntity<Product> getProduct(@PathVariable Long productId){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/products/{productId}/details", method = RequestMethod.PUT)
    default ResponseEntity<Product> editProductDetails(@PathVariable Long productId, @RequestPart(value = "productDetails") Product updatedProductDetails){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/products/{productId}/image", method = RequestMethod.PUT)
    default ResponseEntity<Product> editProductImage(@PathVariable Long productId, @RequestPart(value = "productImage") MultipartFile updatedProductImage){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/products/{productId}", method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteProduct(@PathVariable Long productId){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
