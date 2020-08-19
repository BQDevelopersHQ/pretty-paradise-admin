package za.co.pp.utils;

import com.ninja_squad.dbsetup.operation.Operation;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sql;

public class DbSetupCommonOperations {

    public static Operation CREATE_SCHEMA = sql("CREATE SCHEMA pretty_paradise;");

    public static Operation DROP_SCHEMA = sql("DROP SCHEMA IF EXISTS pretty_paradise CASCADE;");

    public static Operation CREATE_TABLE_PRODUCT = sql(
            "CREATE TABLE pretty_paradise.product (" +
                    "product_id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(50)," +
                    "price DECIMAL UNSIGNED," +
                    "image MEDIUMBLOB);");

    public static Operation INSERT_INTO_PRODUCTS_TABLE = insertInto("pretty_paradise.product")
            .columns("product_id", "name", "price", "image")
            .values(1, "Pink and Pretty", 20.00, new byte[100])
            .values(2, "Purple and Pesky", 30.00, new byte[100]).build();
}
