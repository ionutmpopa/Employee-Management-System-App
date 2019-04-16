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
import ro.sci.ems.exception.ValidationException;
import ro.sci.ems.service.EmployeeService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private static Logger LOGGER = LoggerFactory.getLogger("EmployeeController");

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("")
    public ModelAndView list() {
        ModelAndView result = new ModelAndView("employee/list");


        Collection<Employee> employees = employeeService.listAll();
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
}
