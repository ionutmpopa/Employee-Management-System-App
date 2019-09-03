package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.UserDAO;
import ro.sci.ems.domain.Employee;
import ro.sci.ems.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

@Repository
public class JdbcTemplateUserDAO implements UserDAO {

    private static final int INDEX_USER_ID = 1;
    private static final int INDEX_USER_EMAIL = 2;
    private static final int INDEX_USER_LAST_NAME = 3;
    private static final int INDEX_USER_FIRST_NAME = 4;
    private static final int INDEX_USER_PASSWORD = 5;
    private static final int INDEX_USER_ACTIVE = 6;

    @Autowired
    public DataSource dataSource;

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
    public Collection<User> getAllByDate(Date date) {
        return null;
    }

    @Override
    public User findById(Long id) {
        return jdbcTemplate.queryForObject("select * from app_user where id = ?",
                new UserMapper(), id);
    }

    @Override
    public User update(User model) throws SQLException {

        String sql;
        String sql2;
        String sql3;
        Long newId;
        Employee employee = new Employee();

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
                    bCryptPasswordEncoder.encode(model.getPassword()),
                    "Y");

            sql2 = "insert INTO user_role (user_id,role_id) "
                    + "values (?,?)";

            jdbcTemplate.update(sql2, model.getId(),
                    2);

            sql3 = "insert INTO employee (id, first_name, last_name, job_title, email, birth_date, employment_date, gender) "
                    + "values (?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql3, model.getId(),
                    model.getFirstName(),
                    model.getLastName(),
                    "JUNIOR",
                    model.getEmail(),
                    new java.sql.Date(employee.getBirthDate().getTime()),
                    new java.sql.Date(employee.getEmploymentDate().getTime()),
                    "MALE");

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
        return model;
    }

    @Override
    public Collection<User> searchByName(String query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User findByEmail(String email) {
        return jdbcTemplate.queryForObject("select * from app_user where email = ?",
                new UserMapper(), email);
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

    private static class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int arg1) throws SQLException {
            User user = new User();
            user.setId(rs.getLong(INDEX_USER_ID));
            user.setEmail(rs.getString(INDEX_USER_EMAIL));
            user.setLastName(rs.getString(INDEX_USER_LAST_NAME));
            user.setFirstName(rs.getString(INDEX_USER_FIRST_NAME));
            user.setPassword(rs.getString(INDEX_USER_PASSWORD));
            user.setEnabled(rs.getString(INDEX_USER_ACTIVE));
            return user;
        }
    }
}
