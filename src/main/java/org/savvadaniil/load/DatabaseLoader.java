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

    public DatabaseLoader(
            DatabaseConfiguration configuration,
          StyleRepository styleRepository,
          TeacherRepository teacherRepository,
          BranchRepository branchRepository,
          LevelRepository levelRepository,
          PriceRepository priceRepository,
          ScheduleRepository scheduleRepository,
          WorkshopRepository workshopRepository
    ) {
        this.configuration = configuration;
        this.styleRepository = styleRepository;
        this.teacherRepository = teacherRepository;
        this.branchRepository = branchRepository;
        this.levelRepository = levelRepository;
        this.priceRepository = priceRepository;
        this.scheduleRepository = scheduleRepository;
        this.workshopRepository = workshopRepository;
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
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            }
        }
    }

    private void truncateTables(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
            TRUNCATE TABLE
                mdcnrg_prices,
                mdcnrg_styles,
                mdcnrg_teachers,
                mdcnrg_branches,
                mdcnrg_levels,
                mdcnrg_workshops,
                mdcnrg_schedules
            CASCADE
            """);
        }
    }
}
