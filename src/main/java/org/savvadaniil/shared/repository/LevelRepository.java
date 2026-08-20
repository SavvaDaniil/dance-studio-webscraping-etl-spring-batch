package org.savvadaniil.shared.repository;

import org.savvadaniil.shared.model.load.Level;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class LevelRepository {

    private final static String TABLE_NAME = "ds_example_levels";

    public void insertList(Connection connection, Collection<Level> levels) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, name) VALUES (?, ?)";

        try (
                //Connection connection = this.databaseLoader.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (Level level : levels) {
                statement.setInt(1, level.getId());
                statement.setString(2, level.getName());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
