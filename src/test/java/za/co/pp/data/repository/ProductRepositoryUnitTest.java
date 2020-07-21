package za.co.pp.data.repository;

import javax.sql.DataSource;
import java.io.FileInputStream;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import za.co.pp.data.entity.ProductEntity;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_SCHEMA;
import static za.co.pp.utils.DbSetupCommonOperations.CREATE_TABLE_PRODUCT;
import static za.co.pp.utils.DbSetupCommonOperations.DROP_SCHEMA;

@SpringBootTest
public class ProductRepositoryUnitTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setup(){
        Operation operations = sequenceOf(
                DROP_SCHEMA,
                CREATE_SCHEMA,
                CREATE_TABLE_PRODUCT);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();
    }

    @Test
    void canPersistProductEntity() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("Gray and Glitter");
        productEntity.setPrice(20.00);

        InputStream imageInputStream = new FileInputStream(ResourceUtils.getFile("classpath:images/test_image.jpeg"));
        productEntity.setImage(IOUtils.toByteArray(imageInputStream));

        productRepository.save(productEntity);

        Optional<ProductEntity> savedProductEntity = productRepository.findById(1L);
        assertThat(savedProductEntity).isPresent();
        assertThat(savedProductEntity.get().getId()).isEqualTo(1L);
        assertThat(savedProductEntity.get().getName()).isEqualTo("Gray and Glitter");
        assertThat(savedProductEntity.get().getPrice()).isEqualTo(20.00);
        assertThat(savedProductEntity.get().getImage()).isNotNull();
    }

}
