package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.UserDAO;
import ro.sci.ems.domain.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class JdbcTemplateUserDAO implements UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateUserDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean delete(User model) {
        return false;
    }

    @Override
    public Collection<User> getAll() {
        return null;
    }

    @Override
    public User findById(Long id) {
        return jdbcTemplate.queryForObject("select * from app_user where id = ?",

                new JdbcTemplateUserDAO.UserMapper(), id);
    }

    @Override
    public User update(User model) {

        String sql = "";
        Long newId = null;

        sql = "insert into app_user (email,first_name,last_name, password, active) "
                + "values (?,?,?,?,?) returning id";

        boolean enabled = true;

        newId = jdbcTemplate.queryForObject(sql, new Object[]{
                model.getEmail(),
                model.getFirstName(),
                model.getLastName(),
                model.getPassword(),
                model.setEnabled(enabled)
        }, new RowMapper<Long>() {
            public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                return rs.getLong(1);
            }
        });
        //}
        model.setId(newId);
        return model;
    }

    @Override
    public Collection<User> searchByName(String query) {
        return null;
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
