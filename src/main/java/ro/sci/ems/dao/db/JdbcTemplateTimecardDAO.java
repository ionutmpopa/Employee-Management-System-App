package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.TimecardDAO;
import ro.sci.ems.domain.Timecard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

@Repository
public class JdbcTemplateTimecardDAO implements TimecardDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Timecard> getAll() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN"));

        if (hasAdminRole) {
            return jdbcTemplate.query("select * from time_card",
                    new TimecardMapper());
        }

        return jdbcTemplate.query("select * from time_card tc INNER JOIN employee emp ON (tc.employee_id = emp.employee_id) WHERE emp.email = ?",
                new TimecardMapper(), currentUserName);
    }

    @Override
    public Timecard findById(Long id) {
        return jdbcTemplate.queryForObject("select * from time_card where time_id = ?",
                new TimecardMapper(), id);
    }

    @Override
    public Timecard update(Timecard model) {


        String sql = "";
        Long newId = null;
        if (model.getId() > 0) {
            sql = "update time_card set user_comment=?, employee_id=?, project_id=?, hours=?, working_date=? "
                    + "where time_id = ? returning time_id";
            newId = jdbcTemplate.queryForObject(sql, new Object[]{
                    model.getComment(),
                    model.getEmployee_id(),
                    model.getProject_id(),
                    model.getHours(),
                    new Timestamp(model.getDate().getTime()),
                    model.getId()

            }, new RowMapper<Long>() {
                public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                    return rs.getLong(1);
                }
            });
        } else {
            sql = "insert into time_card (user_comment, employee_id, project_id, hours, working_date) "
                    + "values (?, ?, ?, ?, ?) returning time_id";

            newId = jdbcTemplate.queryForObject(sql, new Object[]{
                    model.getComment(),
                    model.getEmployee_id(),
                    model.getProject_id(),
                    model.getHours(),
                    new Timestamp(model.getDate().getTime())

            }, new RowMapper<Long>() {
                public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                    return rs.getLong(1);
                }
            });
        }
        model.setId(newId);

        return model;
    }

    @Override
    public boolean delete(Timecard model) {
        return jdbcTemplate.update("delete from time_card where time_id = ?", model.getId()) > 0;
    }

    private static class TimecardMapper implements RowMapper<Timecard> {

        @Override
        public Timecard mapRow(ResultSet rs, int arg1) throws SQLException {
            Timecard timecard = new Timecard();
            timecard.setId(rs.getLong("time_id"));
            timecard.setComment(rs.getString("user_comment"));
            timecard.setDate(new Date(rs.getTimestamp("working_date").getTime()));
            timecard.setEmployee_id(rs.getLong("employee_id"));
            timecard.setProject_id(rs.getLong("project_id"));
            timecard.setHours(rs.getDouble("hours"));

            return timecard;
        }

    }
}
