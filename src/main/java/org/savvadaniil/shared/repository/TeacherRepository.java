package org.savvadaniil.shared.repository;

import org.savvadaniil.shared.model.load.Teacher;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class TeacherRepository {

    private final static String TABLE_NAME = "ds_example_teachers";

    public void insertList(Connection connection, Collection<Teacher> teachers) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, name) VALUES (?, ?)";

        try (
                //Connection connection = this.databaseLoader.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (Teacher teacher : teachers) {
                statement.setInt(1, teacher.getId());
                statement.setString(2, teacher.getName());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
