package org.savvadaniil.shared.repository;


import org.savvadaniil.shared.model.stage.Price;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class PriceRepository {

    private final static String TABLE_NAME = "ds_example_prices";

    public void insertList(Connection connection, Collection<Price> prices) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (title, price) VALUES (?, ?)";

        try (
                //Connection connection = this.databaseLoader.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (Price price : prices) {
                statement.setString(1, price.getTitle());
                statement.setInt(2, price.getPrice());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
