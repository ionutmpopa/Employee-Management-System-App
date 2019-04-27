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
import ro.sci.ems.domain.Employee;
import ro.sci.ems.domain.EmployeeCost;
import ro.sci.ems.exception.ValidationException;
import ro.sci.ems.service.EmployeeService;
import ro.sci.ems.service.TimecardService;

import javax.validation.Valid;
import java.util.*;


@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private static Logger LOGGER = LoggerFactory.getLogger("EmployeeController");

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

    @RequestMapping(value= "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id ) {
        employeeService.delete(id);
        return  "redirect:/employee";
    }

    @RequestMapping("/save")
    public ModelAndView save(@Valid Employee employee,
                             BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();
        if (!bindingResult.hasErrors()) {
            try {
                employeeService.save(employee);
                RedirectView redirectView = new RedirectView("/employee");
                modelAndView.setView(redirectView);
            } catch (ValidationException ex) {

                LOGGER.error("validation error", ex);

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

    @RequestMapping(value = "/calculateEmployeeCost{id}", method = RequestMethod.GET)
    public ModelAndView calculateEmployeeCost(@PathVariable("id") long id) {
        ModelAndView result = new ModelAndView("employee/details");

        Employee employee = employeeService.get(id);
        EmployeeCost employeeCost = new EmployeeCost();
        double cost = employeeService.getCostPerEmployee(id);
        Map<String, Double> myProjectList = employeeService.getProjectCostsPerEmployee(id);
        employeeCost.setCost(cost);
        employeeCost.setName(employee.getFirstName() + " " + employee.getLastName());
        employeeCost.setProjectNames(myProjectList);

        result.addObject("employeeCost", employeeCost);
        result.addObject("myProjectList", myProjectList);

        return result;
    }

    @RequestMapping(value = "/calculateDailyEmployeeCost{id}", method = RequestMethod.GET)
    public ModelAndView calculateDailyEmployeeCost(@PathVariable("id") long id) {
        ModelAndView result = new ModelAndView("employee/dailydetails");

        Employee employee = employeeService.get(id);
        EmployeeCost employeeCost = new EmployeeCost();
        double cost = employeeService.getCostPerEmployee(id);
        List<String> myProjectList = employeeService.getDailyProjectCostsPerEmployee(id);
        employeeCost.setCost(cost);
        employeeCost.setName(employee.getFirstName() + " " + employee.getLastName());
        employeeCost.setProjectName(myProjectList);
        Collections.sort(myProjectList, Collections.reverseOrder());

        result.addObject("employeeCost", employeeCost);
        result.addObject("myProjectList", myProjectList);

        return result;
    }

}
