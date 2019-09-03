package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.TimecardDAO;
import ro.sci.ems.domain.Timecard;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

@Repository
public class JdbcTemplateTimecardDAO implements TimecardDAO {

    private static final int INDEX_TIMECARD_ID = 1;
    private static final int INDEX_TIMECARD_PR_ID = 2;
    private static final int INDEX_TIMECARD_EMP_ID = 3;
    private static final int INDEX_TIMECARD_WORKING_DATE = 4;
    private static final int INDEX_USER_COMMENT = 5;
    private static final int INDEX_HOURS = 6;
    private Date date = new Date();

    private Authentication authentication;
    private String currentUserName;

    @Autowired
    DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateTimecardDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Collection<Timecard> getAll() {

        authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN"));

        if (hasAdminRole) {
            return jdbcTemplate.query("select * from time_card",
                    new TimecardMapper());
        }

        return jdbcTemplate.query("select * from time_card tc INNER JOIN employee emp ON (tc.employee_id = emp.id) WHERE emp.email = ?",
                new TimecardMapper(), getCurrentUserName());
    }

    @Override
    public Collection<Timecard> getAllByDate(Date date) {

        authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN"));

        if (hasAdminRole) {
            return jdbcTemplate.query("select * from time_card where working_date = ?",
                    new TimecardMapper(), date);
        }

        return jdbcTemplate.query("select * from time_card tc INNER JOIN employee emp ON (tc.employee_id = emp.id) WHERE emp.email = ? AND tc.working_date = ?",
                new TimecardMapper(), getCurrentUserName(), date);
    }

    @Override
    public Timecard findById(Long id) {
        return jdbcTemplate.queryForObject("select * from time_card where id = ?",
                new TimecardMapper(), id);
    }

    @Override
    public Timecard update(Timecard model) throws SQLException {

        String sql;
        Long newId;

        if ((model.getId() > 0) && (getHours(model) <= 8)) {
            sql = "update time_card set user_comment=?, employee_id=?,"
                    + " project_id=?, hours=?, working_date=?, updated_by_user=?, last_update=? "
                    + "where id = ?";
            jdbcTemplate.update(sql,
                    model.getComment(),
                    model.getEmployee_id(),
                    model.getProject_id(),
                    model.getHours(),
                    new java.sql.Date(model.getDate().getTime()),
                    getCurrentUserName(),
                    new java.sql.Date(date.getTime()),
                    model.getId());

        } else if (getHours(model) > 8) {
            updateExisting(model);
        } else {

            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);

                newId = getSequenceId(connection, "select sq_time_card_id.nextval from dual");

                model.setId(newId);

                sql = "insert into time_card (id, user_comment, employee_id, project_id, hours, working_date, "
                        +"inserted_by_user, date_inserted) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?)";

                jdbcTemplate.update(sql,
                        model.getId(),
                        model.getComment(),
                        model.getEmployee_id(),
                        model.getProject_id(),
                        model.getHours(),
                        new java.sql.Date(model.getDate().getTime()),
                        getCurrentUserName(),
                        new java.sql.Date(date.getTime()));

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
        return model;
    }

    @Override
    public boolean delete(Timecard model) {
        return jdbcTemplate.update("delete from time_card where id = ?", model.getId()) > 0;
    }

    private void updateExisting(Timecard model) {

        String sql;

        if (getHours(model) > 8) {
            Collection<Timecard> timecards = this.getAll();
            for (Timecard timecard1 : timecards) {
                if ((model.getDate().getTime() == timecard1.getDate().getTime()) &&
                        (model.getEmployee_id() == timecard1.getEmployee_id())) {

                    sql = "update time_card set user_comment=?, employee_id=?,"
                            + " project_id=?, hours=?, working_date=?, updated_by_user=?, last_update=?"
                            + "where id = ?";
                    jdbcTemplate.update(sql,
                            model.getComment(),
                            model.getEmployee_id(),
                            model.getProject_id(),
                            model.getHours(),
                            new java.sql.Date(model.getDate().getTime()),
                            getCurrentUserName(),
                            new java.sql.Date(date.getTime()),
                            timecard1.getId());
                    cleanDuplicates(model);
                }
            }
        }
    }

    private double getHours(Timecard model) {

        double sum = 0;
        double finalHours = 0;
        Collection<Timecard> timecards = this.getAll();
        for (Timecard timecard1 : timecards) {
            if ((model.getDate().getTime() == timecard1.getDate().getTime()) &&
                    (model.getEmployee_id() == timecard1.getEmployee_id())) {
                sum += timecard1.getHours();
                finalHours = model.getHours() + sum;
            }
        }
        return finalHours;
    }

    private void cleanDuplicates(Timecard model) {

        if (getHours(model) > 8) {
            Collection<Timecard> timecards = this.getAll();
            for (Timecard timecard1 : timecards) {
                if ((model.getDate().getTime() == timecard1.getDate().getTime()) &&
                        (model.getEmployee_id() == timecard1.getEmployee_id())) {
                    jdbcTemplate.update("delete from time_card\n" +
                            "WHERE id IN\n" +
                            "(SELECT id\n" +
                            "FROM \n" +
                            "(SELECT id,\n" +
                            "ROW_NUMBER() OVER( PARTITION BY project_id, employee_id, working_date, hours, user_comment\n" +
                            "ORDER BY project_id, employee_id, working_date, hours, user_comment) AS row_num\n" +
                            "FROM time_card) t\n" +
                            "WHERE t.row_num > 1)");
                }
            }
        }
    }

    private Long getSequenceId(Connection connection, String query) throws SQLException {

        PreparedStatement myUserStatement = null;
        try {
            myUserStatement = connection.prepareStatement(query);
            ResultSet res = myUserStatement.executeQuery();
            while (res.next()) {
                return res.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (myUserStatement != null) {
                myUserStatement.close();
            }
        }
        return null;
    }

    private String getCurrentUserName() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        return currentUserName = authentication.getName();
    }

    private static class TimecardMapper implements RowMapper<Timecard> {

        @Override
        public Timecard mapRow(ResultSet rs, int arg1) throws SQLException {
            Timecard timecard = new Timecard();
            timecard.setId(rs.getLong(INDEX_TIMECARD_ID));
            timecard.setComment(rs.getString(INDEX_USER_COMMENT));
            timecard.setDate(new Date(rs.getTimestamp(INDEX_TIMECARD_WORKING_DATE).getTime()));
            timecard.setEmployee_id(rs.getLong(INDEX_TIMECARD_EMP_ID));
            timecard.setProject_id(rs.getLong(INDEX_TIMECARD_PR_ID));
            timecard.setHours(rs.getDouble(INDEX_HOURS));

            return timecard;
        }

    }
}
