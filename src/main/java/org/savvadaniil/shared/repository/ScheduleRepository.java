package org.savvadaniil.shared.repository;

import org.savvadaniil.shared.model.load.LoadSchedule;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Collection;

@Repository
public class ScheduleRepository {

    private final static String TABLE_NAME = "ds_example_schedules";

    public void insertList(Connection connection, Collection<LoadSchedule> loadSchedules) throws SQLException {
        String sql = """
        INSERT INTO 
         """ + TABLE_NAME + """ 
        (time_from, weekday, branch_id, teacher_id, style_id, level_id) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (
                //Connection connection = this.databaseLoader.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (LoadSchedule loadSchedule : loadSchedules) {
                statement.setTime(
                        1,
                        Time.valueOf(loadSchedule.getTimeFrom())
                );

                statement.setInt(2, loadSchedule.getWeekday());
                statement.setInt(3, loadSchedule.getBranchId());
                statement.setInt(4, loadSchedule.getTeacherId());
                statement.setInt(5, loadSchedule.getStyleId());

                if (loadSchedule.getLevelId() == null) {
                    statement.setNull(6, Types.INTEGER);
                } else {
                    statement.setInt(6, loadSchedule.getLevelId());
                }

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
