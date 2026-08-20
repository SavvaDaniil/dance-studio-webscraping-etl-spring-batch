package org.savvadaniil.shared.repository;

import org.savvadaniil.shared.model.load.Style;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class StyleRepository {

    private final static String TABLE_NAME = "ds_example_styles";

    public void insertList(Connection connection, Collection<Style> styles) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, name) VALUES (?, ?)";

        try (
                //Connection connection = this.databaseLoader.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (Style style : styles) {
                statement.setInt(1, style.getId());
                statement.setString(2, style.getName());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
