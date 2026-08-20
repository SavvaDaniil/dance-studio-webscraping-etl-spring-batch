package org.savvadaniil.load;

import org.savvadaniil.shared.config.DatabaseConfiguration;
import org.savvadaniil.shared.model.load.*;
import org.savvadaniil.shared.model.stage.Price;
import org.savvadaniil.shared.repository.*;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Service
public class DatabaseLoader {

    private final DatabaseConfiguration configuration;
    private final StyleRepository styleRepository;
    private final TeacherRepository teacherRepository;
    private final BranchRepository branchRepository;
    private final LevelRepository levelRepository;
    private final PriceRepository priceRepository;
    private final ScheduleRepository scheduleRepository;
    private final WorkshopRepository workshopRepository;


    public DatabaseLoader(DatabaseConfiguration configuration) {
        this.configuration = configuration;
        this.styleRepository = new StyleRepository();
        this.teacherRepository = new TeacherRepository();
        this.branchRepository = new BranchRepository();
        this.levelRepository = new LevelRepository();
        this.priceRepository = new PriceRepository();
        this.scheduleRepository = new ScheduleRepository();
        this.workshopRepository = new WorkshopRepository();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                configuration.getUrl(),
                configuration.getUsername(),
                configuration.getPassword()
        );
    }

    public void load(
            List<Price> prices,
            Collection<Teacher> teachers,
            Collection<Style> styles,
            Collection<Branch> branches,
            Collection<Level> levels,
            List<LoadWorkshop> workshops,
            List<LoadSchedule> schedules
    ) throws SQLException {

        try (Connection connection = getConnection()) {

            connection.setAutoCommit(false);

            try {
                truncateTables(connection);

                this.priceRepository.insertList(connection, prices);
                this.teacherRepository.insertList(connection, teachers);
                this.styleRepository.insertList(connection, styles);
                this.branchRepository.insertList(connection, branches);
                this.levelRepository.insertList(connection, levels);
                this.workshopRepository.insertList(connection, workshops);
                this.scheduleRepository.insertList(connection, schedules);

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private void truncateTables(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
            TRUNCATE TABLE
                ds_example_prices,
                ds_example_styles,
                ds_example_teachers,
                ds_example_branches,
                ds_example_levels,
                ds_example_workshops,
                ds_example_schedules
            CASCADE
            """);
        }
    }
}
