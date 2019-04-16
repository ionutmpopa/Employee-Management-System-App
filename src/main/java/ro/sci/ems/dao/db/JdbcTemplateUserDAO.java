package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.UserDAO;
import ro.sci.ems.domain.Employee;
import ro.sci.ems.domain.Role;
import ro.sci.ems.domain.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

@Repository
public class JdbcTemplateUserDAO implements UserDAO {

    long millis=System.currentTimeMillis();
    java.sql.Date date = new java.sql.Date(millis);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public JdbcTemplateUserDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean delete(User model) {
        return false;
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query("select * from app_user",
                new UserMapper());
    }

    @Override
    public User findById(Long id) {
        return jdbcTemplate.queryForObject("select * from app_user where id = ?",
                new UserMapper(), id);
    }

    @Override
    public User update(User model) {

        String sql = "";
        String sql2 = "";
        String sql3 = "";
        Long newId = null;
        Long newRoleId = null;
        Long newEmployeeId = null;

        Role newRole = new Role();
        Employee employee = new Employee();

        sql = "insert into app_user (email,first_name,last_name, password, active) "
                + "values (?,?,?,?,?) returning id";

        newId = jdbcTemplate.queryForObject(sql, new Object[]{
                model.getEmail(),
                model.getFirstName(),
                model.getLastName(),
                bCryptPasswordEncoder.encode(model.getPassword()),
                true
        }, new RowMapper<Long>() {
            public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                return rs.getLong(1);
            }
        });
        //}
        model.setId(newId);

        sql2 = "insert into user_role (user_id,role_id) "
                + "values (?,?) returning id";

        newRoleId = jdbcTemplate.queryForObject(sql2, new Object[]{
                model.getId(),
                2
        }, new RowMapper<Long>() {
            public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                return rs.getLong(1);
            }
        });

        newRole.setId(newRoleId);

        sql3 = "insert into employee (employee_id, first_name, last_name, job_title, email, date_of_birth, employment_date, gender) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?) returning employee_id";

        newEmployeeId = jdbcTemplate.queryForObject(sql3, new Object[]{
                model.getId(),
                model.getFirstName(),
                model.getLastName(),
                "JUNIOR",
                model.getEmail(),
                new Timestamp(employee.getBirthDate().getTime()),
                new Timestamp(employee.getEmploymentDate().getTime()),
                "MALE"
        }, new RowMapper<Long>() {
            public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                return rs.getLong(1);
            }
        });

		//employee.setId(model.getId());

        return model;
    }

    @Override
    public Collection<User> searchByName(String query) {
        return null;
    }

    @Override
    public User findByEmail(String email){
        return jdbcTemplate.queryForObject("select * from app_user where email = ?",
                new UserMapper(), email);
    }

    private static class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int arg1) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setEnabled(rs.getBoolean("active"));
            return user;
        }
    }
}
