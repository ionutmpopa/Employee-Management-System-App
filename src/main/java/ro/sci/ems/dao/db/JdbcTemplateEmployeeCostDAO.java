package ro.sci.ems.dao.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.EmployeeCostDAO;
import ro.sci.ems.domain.EmployeeDailyCost;
import ro.sci.ems.service.UserService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

@Repository
public class JdbcTemplateEmployeeCostDAO implements EmployeeCostDAO {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public JdbcTemplateEmployeeCostDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Collection<EmployeeDailyCost> getAll(long id) {

        return jdbcTemplate.query(" select * from (\n" +
                        " select employee_id, job_title, project_name, working_date, hours_per_project, sum(cost) as cost_per_project \n" +
                        " from employee_daily_cost\n" +
                        " where employee_id = ?\n" +
                        " group by employee_id, job_title, working_date, project_name, hours_per_project\n" +
                        " order by employee_id, job_title, working_date, project_name, hours_per_project) t\n" +
                        " order by t.working_date DESC",
                new EmployeeCostMapper(), id);
    }


    @Override
    public boolean mergeIntoDailyCost() {

        String myMergeStatement = "MERGE INTO employee_daily_cost e\n" +
                " USING (SELECT * FROM employee_daily_cost_temp) ed\n" +
                " ON (e.employee_id = ed.employee_id AND e.project_name = ed.project_name \n" +
                " AND e.employee_name = ed.employee_name AND e.cost = ed.cost AND e.hours_per_project = ed.hours_per_project\n" +
                " AND e.working_date = ed.working_date)\n" +
                " WHEN NOT MATCHED THEN INSERT \n" +
                " (e.employee_id, e.project_name, e.employee_name, e.cost, e.hours_per_project, e.working_date, e.job_title)\n" +
                " VALUES \n" +
                " (ed.employee_id, ed.project_name, ed.employee_name, ed.cost, ed.hours_per_project, ed.working_date, ed.job_title)";

        return jdbcTemplate.update(myMergeStatement) > 0;
    }

    @Override
    public boolean updateTempCostReport(long employeeId, String projectName, String employeeName, double cost,
                                              Date workingDate, double hoursPerProject, String jobTitle) {

        return jdbcTemplate.update("insert into employee_daily_cost_temp" +
                        "(employee_id, project_name, employee_name, cost, " +
                        "hours_per_project, working_date, job_title) " +
                        "values (?, ?, ?, ?, ?, ?, ?)",
                employeeId, projectName, employeeName, cost, hoursPerProject,
                workingDate, jobTitle) > 0;

    }

    private static class EmployeeCostMapper implements RowMapper<EmployeeDailyCost> {

        @Override
        public EmployeeDailyCost mapRow(ResultSet rs, int arg1) throws SQLException {
            EmployeeDailyCost employeeDailyCost = new EmployeeDailyCost();
            employeeDailyCost.setJobTitle(rs.getString("job_title"));
            employeeDailyCost.setName(rs.getString("project_name"));
            employeeDailyCost.setWorkingDate(new Date(rs.getDate("working_date").getTime()));
            employeeDailyCost.setHoursPerProject(rs.getDouble("hours_per_project"));
            employeeDailyCost.setCost(rs.getDouble("cost_per_project"));

            return employeeDailyCost;
        }
    }
}
