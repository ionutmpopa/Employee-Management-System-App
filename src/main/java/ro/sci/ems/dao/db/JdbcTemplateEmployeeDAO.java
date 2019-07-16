package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.EmployeeDAO;
import ro.sci.ems.domain.*;
import ro.sci.ems.service.UserService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

@Repository("dao")
public class JdbcTemplateEmployeeDAO implements EmployeeDAO {

    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public JdbcTemplateEmployeeDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Collection<Employee> getAll() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN"));

        if (hasAdminRole) {
            return jdbcTemplate.query("select * from employee",
                    new EmployeeMapper());
        }

        return jdbcTemplate.query("select * from employee where email = ?",
                new EmployeeMapper(), currentUserName);
    }

    @Override
    public Collection<Employee> getAllByDate(Date date) {
        return null;
    }

    @Override
    public Employee findById(Long id) {
        return jdbcTemplate.queryForObject("select * from employee where employee_id = ?",

                new EmployeeMapper(), id);
    }

    @Override
    public Employee update(Employee model) {

        User user = new User();
        Role role = new Role();

        String sql = "";
        String sql2 = "";
        Long newId = null;
        Long newUserId = null;
        String sql3 = "";
        Long newRoleId = null;

        if (model.getId() > 0) {
            sql = "update employee set first_name=?, last_name=?, job_title=?, email=?, date_of_birth=?, employment_date=?, gender = ? "
                    + "where employee_id = ? returning employee_id";
            newId = jdbcTemplate.queryForObject(sql, new Object[]{
                    model.getFirstName(),
                    model.getLastName(),
                    model.getJobTitle().toString(),
                    model.getEmail(),
                    new Timestamp(model.getBirthDate().getTime()),
                    new Timestamp(model.getEmploymentDate().getTime()),
                    model.getGender().name(),
                    model.getId()

            }, new RowMapper<Long>() {
                public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                    return rs.getLong(1);
                }
            });
        } else {
            sql = "insert into employee (first_name, last_name, job_title, email, date_of_birth, employment_date, gender) "
                    + "values (?, ?, ?, ?, ?, ?, ?) returning employee_id";

            newId = jdbcTemplate.queryForObject(sql, new Object[]{
                    model.getFirstName(),
                    model.getLastName(),
                    model.getJobTitle().toString(),
                    model.getEmail(),
                    new Timestamp(model.getBirthDate().getTime()),
                    new Timestamp(model.getEmploymentDate().getTime()),
                    model.getGender().name()

            }, new RowMapper<Long>() {
                public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                    return rs.getLong(1);
                }
            });
        model.setId(newId);

        sql2 = "insert into app_user (first_name, last_name, email, password, active) "
                + "values (?, ?, ?, ?, ?) returning id";

        newUserId = jdbcTemplate.queryForObject(sql2, new Object[]{
                model.getFirstName(),
                model.getLastName(),
                model.getEmail(),
                bCryptPasswordEncoder.encode("12345"),
                true
        }, new RowMapper<Long>() {
            public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                return rs.getLong(1);
            }
        });

        user.setId(newUserId);

            sql3 = "insert into user_role (user_id,role_id) "
                    + "values (?,?) returning id";

            newRoleId = jdbcTemplate.queryForObject(sql3, new Object[]{
                    user.getId(),
                    2
            }, new RowMapper<Long>() {
                public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                    return rs.getLong(1);
                }
            });

            role.setId(newRoleId);
    }

        return model;
    }

    @Override
    public boolean delete(Employee model) {
        return jdbcTemplate.update("delete from employee where employee_id = ?", model.getId()) > 0;
    }

    @Override
    public Collection<Employee> searchByName(String query) {
        return jdbcTemplate.query("select * from employee "
                        + "where lower(first_name || ' ' || last_name) like ?",
                new String[]{"%" + query.toLowerCase() + "%"},
                new EmployeeMapper());
    }

    private static class EmployeeMapper implements RowMapper<Employee> {

        @Override
        public Employee mapRow(ResultSet rs, int arg1) throws SQLException {
            Employee employee = new Employee();
            employee.setId(rs.getLong("employee_id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
            employee.setJobTitle(rs.getString("job_title"));
            employee.setEmail(rs.getString("email"));
            employee.setBirthDate(new Date(rs.getTimestamp("date_of_birth").getTime()));
            employee.setEmploymentDate(new Date(rs.getTimestamp("employment_date").getTime()));
            employee.setGender(Gender.valueOf(rs.getString("gender")));
            return employee;
        }
    }
}
