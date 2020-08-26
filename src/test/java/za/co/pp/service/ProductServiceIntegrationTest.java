package za.co.pp.service;

import javax.sql.DataSource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.co.pp.data.repository.ProductRepository;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_SCHEMA;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_TABLE_PRODUCT;
import static za.co.pp.utils.DbSetupCommonOperations.DROP_SCHEMA;
import static za.co.pp.utils.DbSetupCommonOperations.INSERT_INTO_PRODUCTS_TABLE;

@SpringBootTest
public class ProductServiceIntegrationTest {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("Given a product exists with the given productId, " +
            "when deleteProduct called" +
            "then the product no longer exists in the database")
    void canDeleteProduct(){
        Operation operations = sequenceOf(
                DROP_SCHEMA,
                CREATE_SCHEMA,
                CREATE_TABLE_PRODUCT,
                INSERT_INTO_PRODUCTS_TABLE);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();

        this.productService.deleteProduct(1L);

        assertThat(this.productRepository.findById(1L)).isEmpty();

    }

}
