package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.EmployeeDAO;
import ro.sci.ems.domain.Employee;
import ro.sci.ems.domain.Gender;
import ro.sci.ems.domain.Role;
import ro.sci.ems.domain.User;
import ro.sci.ems.service.UserService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collection;
import java.util.Date;

@Repository("dao")
public class JdbcTemplateEmployeeDAO implements EmployeeDAO {

    @Autowired
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Employee findById(Long id) {
        return jdbcTemplate.queryForObject("select * from employee where id = ?",
                new EmployeeMapper(), id);
    }

    @Override
    public Employee update(Employee model) throws SQLException {

        Role newRole = new Role();
        User newUser = new User();

        String sql;
        String sql2;
        String sql3;
        Long newId;
        Employee employee = new Employee();

        if (model.getId() > 0) {
            sql = "update employee set first_name=?, last_name=?, job_title=?, email=?, birth_date=?, employment_date=?, gender = ? "
                    + "where id = ?";
            jdbcTemplate.update(sql, model.getFirstName(),
                    model.getLastName(),
                    model.getJobTitle(),
                    model.getEmail(),
                    new java.sql.Date(model.getBirthDate().getTime()),
                    new java.sql.Date(model.getEmploymentDate().getTime()),
                    model.getGender().name(),
                    model.getId());
        } else {

            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);

                newId = getSequenceId(connection, "select sq_user_id.nextval from dual");

                model.setId(newId);

                sql = "insert into app_user (id,email,first_name,last_name,password,active) " +
                        "values (?,?,?,?,?,?)";

                jdbcTemplate.update(sql, model.getId(),
                        model.getEmail(),
                        model.getLastName(),
                        model.getFirstName(),
                        bCryptPasswordEncoder.encode("12345"),
                        "Y");

                sql2 = "insert INTO user_role (user_id,role_id) "
                        + "values (?,?)";

                jdbcTemplate.update(sql2, model.getId(),
                        2);

                sql3 = "insert INTO employee (id, first_name, last_name, job_title, email, birth_date, employment_date, gender) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?)";

                jdbcTemplate.update(sql3,
                        model.getId(),
                        model.getFirstName(),
                        model.getLastName(),
                        model.getJobTitle(),
                        model.getEmail(),
                        new java.sql.Date(model.getBirthDate().getTime()),
                        new java.sql.Date(model.getEmploymentDate().getTime()),
                        model.getGender().name());

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
    public boolean delete(Employee model) {


        String sql = "BEGIN\n" +
                "delete from employee where id = ?;\n" +
                "delete from user_role where user_id = ?;\n" +
                "delete from app_user where id = ?;\n" +
                "END;";

        Connection connection = null;

        try {

            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            boolean myUpdate = jdbcTemplate.update(sql,
                    model.getId(),
                    model.getId(),
                    model.getId()) > 0;

            connection.commit();
            return myUpdate;


        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                return false;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public Collection<Employee> searchByName(String query) {
        return jdbcTemplate.query("select * from employee "
                        + "where lower(first_name || ' ' || last_name) like ?",
                new String[]{"%" + query.toLowerCase() + "%"},
                new EmployeeMapper());
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

    private static class EmployeeMapper implements RowMapper<Employee> {

        @Override
        public Employee mapRow(ResultSet rs, int arg1) throws SQLException {
            Employee employee = new Employee();
            employee.setId(rs.getLong("id"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
            employee.setJobTitle(rs.getString("job_title"));
            employee.setEmail(rs.getString("email"));
            employee.setBirthDate(new Date(rs.getTimestamp("birth_date").getTime()));
            employee.setEmploymentDate(new Date(rs.getTimestamp("employment_date").getTime()));
            employee.setGender(Gender.valueOf(rs.getString("gender")));
            return employee;
        }
    }
}
