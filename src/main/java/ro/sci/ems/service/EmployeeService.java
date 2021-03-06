package ro.sci.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ro.sci.ems.dao.EmployeeDAO;
import ro.sci.ems.domain.*;
import ro.sci.ems.exception.ValidationException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EmployeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeDAO dao;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CostService costService;

    @Autowired
    private TimecardService timecardService;

    @Autowired
    private ProjectService projectService;

    public Collection<Employee> listAll() {
        return dao.getAll();
    }

    public Collection<Employee> search(String query) {
        LOGGER.debug("Searching for " + query);
        return dao.searchByName(query);
    }

    public boolean delete(Long id) {
        LOGGER.debug("Deleting employee for id: " + id);
        Employee emp = null;
        try {
            emp = dao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.warn("trying to fing an inexisting employee");
            return false;
        }
        if (emp != null) {
            dao.delete(emp);
            return true;
        }

        return false;
    }

    public Employee get(Long id) {
        LOGGER.debug("Getting employee for id: " + id);
        return dao.findById(id);
    }

    public void save(Employee employee) throws ValidationException, SQLException {
        LOGGER.debug("Saving: " + employee);
        validate(employee);

        dao.update(employee);
    }

    private void validate(Employee employee) throws ValidationException {
        Date currentDate = new Date();
        List<String> errors = new LinkedList<String>();


        if (StringUtils.isEmpty(employee.getFirstName())) {
            errors.add("First Name is Empty");
        }

        if (StringUtils.isEmpty(employee.getLastName())) {
            errors.add("Last Name is Empty");
        }

        if (employee.getGender() == null) {
            errors.add("Gender is Empty");
        }

        if (StringUtils.isEmpty(employee.getJobTitle())) {
            errors.add("JobTitle is Empty");
        }

        if (employee.getBirthDate() == null) {
            errors.add("BirthDate is Empty");
        } else {
            if (currentDate.before(employee.getBirthDate())) {
                errors.add("Birthdate in future");
            }

            Calendar c = GregorianCalendar.getInstance();
            c.setTime(new Date());
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 18);
            if (employee.getBirthDate().after(c.getTime())) {
                errors.add("Too young to get employeed");
            }

            c.set(Calendar.YEAR, 1901);
            if (employee.getBirthDate().before(c.getTime())) {
                errors.add("Too old to get employeed");
            }

        }

        if (employee.getEmploymentDate() == null) {
            errors.add("EmploymentDate is Empty");
        } else {
            if (currentDate.before(employee.getEmploymentDate())) {
                errors.add("Employment Date in future");
            }
        }

        if (employee.getBirthDate() != null && employee.getEmploymentDate() != null) {
            if (employee.getEmploymentDate().before(employee.getBirthDate())) {
                errors.add("EmploymentDate before BirthDate");
            }
        }


        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toArray(new String[]{}));
        }
    }

    public double getCostPerEmployee(long employeeId) {

        Collection<Timecard> timecards = timecardService.listAll();
        Collection<Cost> costs = costService.listAll();

        double sum = 0;
        double result = 0;

        for (Timecard timecard : timecards) {
            if (timecard.getEmployee_id() == employeeId) {
                double hours = timecard.getHours();
                Employee employee = employeeService.get(employeeId);
                for (Cost cost : costs) {
                    if (employee.getJobTitle() == cost.getTitle().toString()) {
                        result = cost.getCost() * hours;
                    }
                }
                sum += result;
            }
        }
        return sum;
    }

    public Collection<EmployeeCost> getProjectCostsPerEmployee(long employeeId) {

        Collection<Timecard> timecards = timecardService.listAll();
        Collection<Project> projects = projectService.listAll();
        Collection<Cost> costs = costService.listAll();
        List<EmployeeCost> orderList = null;

        Collection<EmployeeCost> myProjects = new LinkedList<>();
        for (Project project : projects) {
            double sum = 0;
            double result = 0;
            for (Timecard timecard : timecards) {
                Employee employee = employeeService.get(timecard.getEmployee_id());
                double hours = timecard.getHours();
                if (timecard.getEmployee_id() == employeeId) {
                    if (project.getId() == timecard.getProject_id()) {
                        for (Cost cost : costs) {
                            if (employee.getJobTitle() == cost.getTitle().toString()) {
                                sum += hours;
                                result = cost.getCost() * sum;
                            }
                        }
                    }
                }
            }
            if (result != 0.0) {
                EmployeeCost employeeCost = new EmployeeCost();
                employeeCost.setName(project.getName());
                employeeCost.setCost(result);
                employeeCost.setHoursPerProject(sum);

                myProjects.add(employeeCost);
                orderList = new LinkedList<>(myProjects);
                Collections.sort(orderList);
            }
        }
        return orderList;
    }

    public Collection<EmployeeDailyCost> getDailyProjectCostsPerEmployee(long employeeId) {

        Collection<Timecard> timecards = timecardService.listAll();
        Collection<Project> projects = projectService.listAll();
        Collection<Cost> costs = costService.listAll();
        List<EmployeeDailyCost> reverseList = null;
        SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");

        Collection<EmployeeDailyCost> myProjects = new LinkedList<>();
        for (Timecard timecard : timecards) {
            double hours = timecard.getHours();
            Employee employee = employeeService.get(timecard.getEmployee_id());
            if (timecard.getEmployee_id() == employeeId) {
                double sum = 0;
                for (Project project : projects) {
                    if (project.getId() == timecard.getProject_id()) {
                        for (Cost cost : costs) {
                            if (employee.getJobTitle() == cost.getTitle().toString()) {
                                EmployeeDailyCost employeeDailyCost = new EmployeeDailyCost();
                                sum += hours;
                                double result = cost.getCost() * sum;
                                employeeDailyCost.setId(employeeId);
                                employeeDailyCost.setName(project.getName());
                                employeeDailyCost.setCost(result);
                                employeeDailyCost.setHoursPerProject(hours);
                                employeeDailyCost.setJobTitle(employee.getJobTitle());
                                employeeDailyCost.setWorkingDate(new Date(timecard.getDate().getTime()));
                                myProjects.add(employeeDailyCost);
                                reverseList = new LinkedList<>(myProjects);
                                Collections.sort(reverseList, Collections.reverseOrder());
                            }
                        }
                    }
                }
            }
        }
        return reverseList;
    }



    public EmployeeDAO getDao() {
        return dao;
    }

    public void setDao(EmployeeDAO dao) {
        this.dao = dao;
    }


}
