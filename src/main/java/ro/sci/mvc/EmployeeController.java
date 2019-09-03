package ro.sci.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ro.sci.ems.dao.EmployeeCostDAO;
import ro.sci.ems.domain.Employee;
import ro.sci.ems.domain.EmployeeCost;
import ro.sci.ems.domain.EmployeeDailyCost;
import ro.sci.ems.exception.ValidationException;
import ro.sci.ems.service.EmployeeService;
import ro.sci.ems.service.TimecardService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.*;


@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private static Logger logger = LoggerFactory.getLogger("EmployeeController");

    @Autowired
    private EmployeeCostDAO employeeCostDAO;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TimecardService timecardService;

    @RequestMapping("")
    public ModelAndView list() {
        ModelAndView result = new ModelAndView("employee/list");

        Collection<Employee> allEmployees = employeeService.listAll();
        List<Employee> employees = new LinkedList<>(allEmployees);
        Collections.sort(employees);
        result.addObject("employees", employees);

        return result;
    }

    @RequestMapping(value = "/dailydetails/{id}/{showHistory}", method = RequestMethod.GET)
    public ModelAndView dailydetails(@PathVariable("id") long id, @PathVariable("showHistory") boolean showHistory) {
        ModelAndView resultHistory = new ModelAndView("employee/costhistory");
        double cost = employeeService.getCostPerEmployee(id);
        Employee employee = employeeService.get(id);
        Collection<EmployeeDailyCost> allEmployees = employeeCostDAO.getAll(id);

        double calculatedCost = 0;
        for (EmployeeDailyCost dailyCost : allEmployees) {
            calculatedCost = calculatedCost + dailyCost.getCost();
        }

        if (showHistory) {
            List<EmployeeDailyCost> employees = new LinkedList<>(allEmployees);
            Collections.sort(employees, Collections.reverseOrder());
            EmployeeDailyCost employeeDailyCost = new EmployeeDailyCost();
            employeeDailyCost.setCost(calculatedCost);
            employeeDailyCost.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            resultHistory.addObject("employeeDet", employeeDailyCost);
            resultHistory.addObject("employeeCost", employees);
            return resultHistory;

        }
        throw new UnsupportedOperationException("You don't have the necessary access!");

    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView add() {
        ModelAndView modelAndView = new ModelAndView("employee/add");
        modelAndView.addObject("employee", new Employee());
        return modelAndView;
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable("id") long id) {
        Employee employee = employeeService.get(id);
        ModelAndView modelAndView = new ModelAndView("employee/add");
        modelAndView.addObject("employee", employee);

        return modelAndView;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id) {
        employeeService.delete(id);
        return "redirect:/employee";
    }

    @RequestMapping("/save")
    public ModelAndView save(@Valid Employee employee,
                             BindingResult bindingResult) throws SQLException {

        ModelAndView modelAndView = new ModelAndView();
        if (!bindingResult.hasErrors()) {
            try {
                employeeService.save(employee);
                RedirectView redirectView = new RedirectView("/employee");
                modelAndView.setView(redirectView);
            } catch (ValidationException ex) {

                logger.error("validation error", ex);

                List<String> errors = new LinkedList<>();
                errors.add(ex.getMessage());
                modelAndView = new ModelAndView("employee/add");
                modelAndView.addObject("errors", errors);
                modelAndView.addObject("employee", employee);
            }

        } else {
            List<String> errors = new LinkedList<>();

            for (FieldError error :
                    bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ":" + error.getDefaultMessage());
            }

            modelAndView = new ModelAndView("employee/add");
            modelAndView.addObject("errors", errors);
            modelAndView.addObject("employee", employee);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/calculateEmployeeCost/{id}/{showDaily}", method = RequestMethod.GET)
    public ModelAndView calculateEmployeeCost(@PathVariable("id") long id, @PathVariable("showDaily") boolean showDaily) {

        ModelAndView resultTotal = new ModelAndView("employee/details");
        ModelAndView resultDaily = new ModelAndView("employee/dailydetails");
        Employee employee = employeeService.get(id);
        double cost = employeeService.getCostPerEmployee(id);
        Collection<EmployeeDailyCost> myProjectList;
        Collection<EmployeeCost> myProjectListObject;

        if (showDaily) {

            myProjectList = employeeService.getDailyProjectCostsPerEmployee(id);
            EmployeeDailyCost employeeDailyCost = new EmployeeDailyCost();
            employeeDailyCost.setCost(cost);
            employeeDailyCost.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            resultDaily.addObject("employeeDet", employeeDailyCost);
            resultDaily.addObject("employeeCost", myProjectList);

            try {
                for (EmployeeDailyCost c : myProjectList) {
                    Date newDate = new java.sql.Date(c.getWorkingDate().getTime());
                    employeeCostDAO.updateTempCostReport(id, c.getName(), employeeDailyCost.getEmployeeName(), c.getCost(), newDate, c.getHoursPerProject(), c.getJobTitle());
                    employeeCostDAO.mergeIntoDailyCost();
                }
            } catch (NullPointerException e) {
                System.out.println("You do not have access! " + e.getMessage());
            }

            return resultDaily;
        } else {

            myProjectListObject = employeeService.getProjectCostsPerEmployee(id);
            EmployeeCost employeeCost = new EmployeeCost();
            employeeCost.setCost(cost);
            employeeCost.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            resultTotal.addObject("employeeDet", employeeCost);
            resultTotal.addObject("employeeCost", myProjectListObject);
            return resultTotal;
        }
    }
}
