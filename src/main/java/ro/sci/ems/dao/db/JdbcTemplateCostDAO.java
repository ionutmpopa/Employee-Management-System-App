package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.CostDAO;
import ro.sci.ems.domain.Cost;
import ro.sci.ems.domain.Title;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

@Repository
public class JdbcTemplateCostDAO implements CostDAO {

    private static int INDEX_COST_ID = 1;
    private static int INDEX_JOB_TITLE = 2;
    private static int INDEX_JOB_COST = 3;

    @Autowired
    private DataSource dataSource;

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
        return jdbcTemplate.queryForObject("select * from cost where id = ?",

                new CostMapper(), id);
    }

    @Override
    public Collection<Cost> getAllByDate(Date date) {
        return null;
    }

    @Override
    public Cost update(Cost model) throws SQLException {

        String sql = "";
        Long newId = null;

        if (model.getId() > 0) {
            sql = "update cost set job_title=?, job_cost=? "
                    + "where id = ?";
            jdbcTemplate.update(sql,
                    model.getTitle().toString(),
                    model.getCost(),
                    model.getId());
        } else {

            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);

                newId = getSequenceId(connection, "select sq_cost_id.nextval from dual");

                model.setId(newId);

                sql = "insert into cost (id,job_title, job_cost) "
                        + "values (?,?,?)";

                jdbcTemplate.update(sql,
                        model.getId(),
                        model.getTitle().toString(),
                        model.getCost());

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
    public boolean delete(Cost model) {
        return jdbcTemplate.update("delete from cost where id = ?", model.getId()) > 0;
    }

    @Override
    public Collection<Cost> searchByName(String query) {
        return jdbcTemplate.query("select * from cost "
                        + "where lower(job_title || ' ' || job_cost) like '%?%'",
                new String[]{query.toLowerCase()},
                new CostMapper());
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

    private static class CostMapper implements RowMapper<Cost> {

        @Override
        public Cost mapRow(ResultSet rs, int arg1) throws SQLException {
            Cost cost = new Cost();
            cost.setId(rs.getLong(INDEX_COST_ID));
            cost.setCost(rs.getDouble(INDEX_JOB_COST));
            cost.setTitle(Title.valueOf(rs.getString(INDEX_JOB_TITLE)));
            return cost;
        }
    }

}
