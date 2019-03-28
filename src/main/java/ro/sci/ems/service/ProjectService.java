package ro.sci.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.sci.ems.dao.ProjectDAO;
import ro.sci.ems.domain.Cost;
import ro.sci.ems.domain.Employee;
import ro.sci.ems.domain.Project;
import ro.sci.ems.domain.Timecard;
import ro.sci.ems.exception.ValidationException;

import java.util.Collection;

@Service
public class ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private TimecardService timecardService;

    @Autowired
    private CostService costService;

    @Autowired
    private EmployeeService employeeService;



    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public Collection<Project> listAll() {
        return projectDAO.getAll();
    }


    public boolean delete(Long id) {
        LOGGER.debug("Deleting project with id: " + id);
        Project project = projectDAO.findById(id);
        if (project != null) {
            projectDAO.delete(project);
            return true;
        }

        return false;
    }

    public Project findById(long id) {
        return projectDAO.findById(id);
    }


    public void save(Project project) throws ValidationException {
        LOGGER.debug("Saving: " + project);

        projectDAO.update(project);
    }

    public long getCostPerProject(long projectId) {

        Collection<Timecard> timecards = timecardService.listAll();
        Collection<Employee> employees = employeeService.listAll();
        Collection<Cost> costs = costService.listAll();

        long sum = 0;
        long result = 0;

        for(Timecard timecard: timecards) {

            if(timecard.getProject_id() == projectId) {

                long hours = (long)timecard.getHours();
                long employeeId = timecard.getEmployee_id();
                Employee employee = employeeService.get(employeeId);

                for(Cost cost: costs) {

                    if (employee.getJobTitle() == cost.getTitle().toString()) {
                        result = (long)cost.getCost() * hours;
                    }
                }
                sum += result;
            }
        }
        return sum;
    }
}
