package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.CostDAO;
import ro.sci.ems.domain.Cost;
import ro.sci.ems.domain.Title;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class JdbcTemplateCostDAO implements CostDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateCostDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Collection<Cost> getAll() {
        return jdbcTemplate.query("select * from cost",
                new CostMapper());
    }

    @Override
    public Cost findById(Long id) {
        return jdbcTemplate.queryForObject("select * from cost where cost_id = ?",

                new CostMapper(), id);
    }

    @Override
    public Cost update(Cost model) {

        String sql = "";
        Long newId = null;
        if (model.getId() > 0) {
            sql = "update cost set job_title=?, job_cost=? "
                    + "where cost_id = ? returning cost_id";
            newId = jdbcTemplate.queryForObject(sql, new Object[]{
                    model.getTitle().toString(),
                    model.getCost(),
                    model.getId()

            }, new RowMapper<Long>() {
                public Long mapRow(ResultSet rs, int arg1) throws SQLException {
                    return rs.getLong(1);
                }
            });
        } else {
            sql = "insert into cost (job_title, job_cost) "
                    + "values (?, ?) returning cost_id";

            newId = jdbcTemplate.queryForObject(sql, new Object[]{
                    model.getTitle().toString(),
                    model.getCost()
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
    public boolean delete(Cost model) {
        return jdbcTemplate.update("delete from cost where cost_id = ?", model.getId()) > 0;
    }

    @Override
    public Collection<Cost> searchByName(String query) {
        return jdbcTemplate.query("select * from cost "
                        + "where lower(job_title || ' ' || job_cost) like ?",
                new String[]{"%" + query.toLowerCase() + "%"},
                new CostMapper());
    }

    private static class CostMapper implements RowMapper<Cost> {

        @Override
        public Cost mapRow(ResultSet rs, int arg1) throws SQLException {
            Cost cost = new Cost();
            cost.setId(rs.getLong("cost_id"));
            cost.setCost(rs.getDouble("job_cost"));
            cost.setTitle(Title.valueOf(rs.getString("job_title")));
            return cost;
        }

    }

}
