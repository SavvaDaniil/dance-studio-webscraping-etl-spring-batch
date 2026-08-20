package org.savvadaniil.shared.repository;

import org.savvadaniil.shared.model.load.LoadWorkshop;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Collection;

@Repository
public class WorkshopRepository {

    private final static String TABLE_NAME = "ds_example_workshops";

    public void insertList(Connection connection, Collection<LoadWorkshop> loadWorkshops) throws SQLException {
        String sql = """
        INSERT INTO 
         """ + TABLE_NAME + """ 
        (time_from, time_to, name, dates, price, date_start, branch_id, style_id) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (
                //Connection connection = this.databaseLoader.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (LoadWorkshop loadWorkshop : loadWorkshops) {
                statement.setTime(1, Time.valueOf(loadWorkshop.getTimeFrom()));
                statement.setTime(2, Time.valueOf(loadWorkshop.getTimeTo()));
                statement.setString(3, loadWorkshop.getName());
                statement.setString(4, loadWorkshop.getDates());
                statement.setInt(5, loadWorkshop.getPrice());
                statement.setDate(6, Date.valueOf(loadWorkshop.getDateStart()));
                statement.setInt(7, loadWorkshop.getBranchId());
                statement.setInt(8, loadWorkshop.getStyleId());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
