package za.co.pp.utils;

import com.ninja_squad.dbsetup.operation.Operation;

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
}
