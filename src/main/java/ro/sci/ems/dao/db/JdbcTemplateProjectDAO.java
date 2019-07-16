package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.ProjectDAO;
import ro.sci.ems.domain.Project;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

@Repository
public class JdbcTemplateProjectDAO implements ProjectDAO {

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
        return jdbcTemplate.queryForObject("select * from project where project_id = ?",

                new ProjectMapper(), id);
    }

    @Override
    public Collection<Project> getAllByDate(Date date) {
        return null;
    }

    @Override
    public Project update(Project model) {

        String sql = "";
        Long newId = null;
        if (model.getId() > 0) {
            sql = "update project set project_name=?, description=? "
                    + "where project_id = ? returning project_id";
            newId = jdbcTemplate.queryForObject(sql, new Object[]{
                    model.getName(),
                    model.getDescription(),
                    model.getId()

            }, new RowMapper<Long>() {
                public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                    return rs.getLong(1);
                }
            });
        } else {
            sql = "insert into project (project_name, description) "
                    + "values (?, ?) returning project_id";

            newId = jdbcTemplate.queryForObject(sql, new Object[]{
                    model.getName(),
                    model.getDescription()
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
    public boolean delete(Project model) {
        return jdbcTemplate.update("delete from project where project_id = ?", model.getId()) > 0;
    }

    @Override
    public Collection<Project> searchByName(String query) {
        return jdbcTemplate.query("select * from project "
                        + "where lower(project_name || ' ' || description) like '%?%'",
                new String[]{query.toLowerCase()},
                new ProjectMapper());
    }

    private static class ProjectMapper implements RowMapper<Project> {

        @Override
        public Project mapRow(ResultSet rs, int arg1) throws SQLException {
            Project project = new Project();
            project.setId(rs.getLong("project_id"));
            project.setName(rs.getString("project_name"));
            project.setDescription(rs.getString("description"));
            return project;
        }
    }
}

