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
import ro.sci.ems.domain.Project;
import ro.sci.ems.domain.Timecard;
import ro.sci.ems.exception.ValidationException;
import ro.sci.ems.service.EmployeeService;
import ro.sci.ems.service.ProjectService;
import ro.sci.ems.service.TimecardService;
import ro.sci.ems.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/timecards")
public class TimecardController {

    private static Logger LOGGER = LoggerFactory.getLogger(TimecardController.class);

    @Autowired
    private TimecardService timecardService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;


    @RequestMapping("")
    public ModelAndView listAll() {
        ModelAndView result = new ModelAndView("timecards/list");

        Collection<Timecard> timecards = timecardService.listAll();
        Collections.sort((List)timecards, Collections.reverseOrder());
        result.addObject("timecards", timecards);

        Map<String, String> employeeNames = new HashMap<>();
        for (Timecard timecard : timecards) {
            Employee employee = employeeService.get(timecard.getEmployee_id());
            employeeNames.put(timecard.getEmployee_id() + "", employee.getFirstName() + " " + employee.getLastName());
        }
        result.addObject("employeeNames", employeeNames);

        Map<String, String> projectNames = new HashMap<>();
        for (Timecard timecard : timecards) {
            Project project = projectService.findById(timecard.getProject_id());
            projectNames.put(timecard.getProject_id() + "", project.getName());
        }
        result.addObject("projectNames", projectNames);
        return result;
    }

    @RequestMapping(value = "/listAllByDate{date}", method = RequestMethod.GET)
    public ModelAndView listAllByDate(@PathVariable("date") Date date) {
        ModelAndView result = new ModelAndView("timecards/list");

        Collection<Timecard> timecards = timecardService.listAllByDate(date);
        Collections.sort((List)timecards, Collections.reverseOrder());
        result.addObject("timecards", timecards);

        Map<String, String> employeeNames = new HashMap<>();
        for (Timecard timecard : timecards) {
            Employee employee = employeeService.get(timecard.getEmployee_id());
            employeeNames.put(timecard.getEmployee_id() + "", employee.getFirstName() + " " + employee.getLastName());
        }
        result.addObject("employeeNames", employeeNames);

        Map<String, String> projectNames = new HashMap<>();
        for (Timecard timecard : timecards) {
            Project project = projectService.findById(timecard.getProject_id());
            projectNames.put(timecard.getProject_id() + "", project.getName());
        }
        result.addObject("projectNames", projectNames);
        return result;
    }


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView add() {
        ModelAndView modelAndView = new ModelAndView("timecards/add");

        Collection<Timecard> timecards = timecardService.listAll();

        Map<Long, Double> timeCards = new HashMap<>();
        for (Timecard myTimecard : timecards) {
            timeCards.put(myTimecard.getEmployee_id(), myTimecard.getHours());
        }
        Collection<Project> projects = projectService.listAll();
        modelAndView.addObject("projects", projects);
        Collection<Employee> employees = employeeService.listAll();
        modelAndView.addObject("employees", employees);
        modelAndView.addObject("timecards", timecards);
        modelAndView.addObject("timecard", new Timecard());
        return modelAndView;

    }

    @RequestMapping(value = "/edit/{id}/{empId}/{prId}", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable("id") long id, @PathVariable("empId") long empId,
                             @PathVariable("prId") long prId) {
        Timecard timecard = timecardService.findById(id);
        ModelAndView modelAndView = new ModelAndView("timecards/add");
        modelAndView.addObject("timecard", timecard);

        Project projects = projectService.findById(prId);
        modelAndView.addObject("projects", projects);

        Employee employees = employeeService.get(empId);
        modelAndView.addObject("employees", employees);

        return modelAndView;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id) {
        timecardService.delete(id);
        return "redirect:/timecards";
    }

    @RequestMapping("/save")
    public ModelAndView save(@Valid Timecard timecard,
                             BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();
        if (!bindingResult.hasErrors()) {
            try {
                timecardService.save(timecard);
                RedirectView redirectView = new RedirectView("/timecards");
                modelAndView.setView(redirectView);
            } catch (ValidationException ex) {

                LOGGER.error("validation error", ex);

                List<String> errors = new LinkedList<>();
                errors.add(ex.getMessage());
                modelAndView = new ModelAndView("timecards/add");
                modelAndView.addObject("errors", errors);
                modelAndView.addObject("timecard", timecard);
            }

        } else {
            List<String> errors = new LinkedList<>();

            for (FieldError error :
                    bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ":" + error.getCode());
            }

            modelAndView = new ModelAndView("timecards/add");
            modelAndView.addObject("errors", errors);
            modelAndView.addObject("timecard", timecard);
        }

        return modelAndView;
    }
}
