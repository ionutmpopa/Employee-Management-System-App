package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.ProjectDAO;
import ro.sci.ems.domain.Project;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

@Repository
public class JdbcTemplateProjectDAO implements ProjectDAO {

    private static final int INDEX_PROJECT_ID = 1;
    private static final int INDEX_PROJECT_NAME = 2;
    private static final int INDEX_DESCRIPTION = 3;

    @Autowired
    DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateProjectDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Collection<Project> getAll() {
        return jdbcTemplate.query("select * from project",
                new ProjectMapper());
    }

    @Override
    public Project findById(Long id) {
        return jdbcTemplate.queryForObject("select * from project where id = ?",

                new ProjectMapper(), id);
    }

    @Override
    public Collection<Project> getAllByDate(Date date) {
        return null;
    }

    @Override
    public Project update(Project model) throws SQLException {

        String sql = "";
        Long newId = null;
        if (model.getId() > 0) {
            sql = "update project set name=?, description=? "
                    + "where id = ?";
            jdbcTemplate.update(sql,
                    model.getName(),
                    model.getDescription(),
                    model.getId());
        } else {

            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);

                newId = getSequenceId(connection, "select sq_project_id.nextval from dual");

                model.setId(newId);

                sql = "insert into project (id, name, description) "
                        + "values (?, ?, ?)";

                jdbcTemplate.update(sql,
                        model.getId(),
                        model.getName(),
                        model.getDescription());

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
    public boolean delete(Project model) {
        return jdbcTemplate.update("delete from project where id = ?", model.getId()) > 0;
    }

    @Override
    public Collection<Project> searchByName(String query) {
        return jdbcTemplate.query("select * from project "
                        + "where lower(name || ' ' || description) like '%?%'",
                new String[]{query.toLowerCase()},
                new ProjectMapper());
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

    private static class ProjectMapper implements RowMapper<Project> {

        @Override
        public Project mapRow(ResultSet rs, int arg1) throws SQLException {
            Project project = new Project();
            project.setId(rs.getLong(INDEX_PROJECT_ID));
            project.setName(rs.getString(INDEX_PROJECT_NAME));
            project.setDescription(rs.getString(INDEX_DESCRIPTION));
            return project;
        }
    }
}

