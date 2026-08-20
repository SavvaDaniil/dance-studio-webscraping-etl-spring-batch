package org.savvadaniil.shared.repository;

import org.savvadaniil.shared.model.load.Branch;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class BranchRepository {

    private final static String TABLE_NAME = "ds_example_branches";

    public void insertList(Connection connection, Collection<Branch> branches) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, name) VALUES (?, ?)";

        try (
                //Connection connection = this.databaseLoader.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (Branch branch : branches) {
                statement.setInt(1, branch.getId());
                statement.setString(2, branch.getName());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
