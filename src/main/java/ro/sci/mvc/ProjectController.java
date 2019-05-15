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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ro.sci.ems.domain.Project;
import ro.sci.ems.domain.ProjectCost;
import ro.sci.ems.exception.ValidationException;
import ro.sci.ems.service.ProjectService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private static Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @RequestMapping("")
    public ModelAndView listAll() {
        ModelAndView result = new ModelAndView("project/list");

        Collection<Project> allProjects = projectService.listAll();

        List<Project> projects = new LinkedList<>(allProjects);
        Collections.sort(projects);

        result.addObject("projects", projects);

        return result;
    }

    @RequestMapping("/{project_id}")
    public ModelAndView findById(@RequestParam long projectId) {
        ModelAndView result = new ModelAndView("project/projectDetails");

        projectService.findById(projectId);

        return result;

    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView add() {
        ModelAndView modelAndView = new ModelAndView("project/add");
        modelAndView.addObject("project", new Project());
        return modelAndView;
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable("id") long id) {
        Project project = projectService.findById(id);
        ModelAndView modelAndView = new ModelAndView("project/add");
        modelAndView.addObject("project", project);

        return modelAndView;
    }

    @RequestMapping(value= "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id ) {
        projectService.delete(id);
        return "redirect:/project";
    }

    @RequestMapping("/save")
    public ModelAndView save(@Valid Project project,
                             BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();
        if (!bindingResult.hasErrors()) {
            try {
                projectService.save(project);
                RedirectView redirectView = new RedirectView("/project");
                modelAndView.setView(redirectView);
            } catch (ValidationException ex) {

                LOGGER.error("validation error", ex);

                List<String> errors = new LinkedList<>();
                errors.add(ex.getMessage());
                modelAndView = new ModelAndView("project/add");
                modelAndView.addObject("errors", errors);
                modelAndView.addObject("project", project);
            }

        } else {
            List<String> errors = new LinkedList<>();

            for (FieldError error :
                    bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ":" + error.getCode());
            }

            modelAndView = new ModelAndView("project/add");
            modelAndView.addObject("errors", errors);
            modelAndView.addObject("project", project);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/calculateCost{id}", method = RequestMethod.GET)
    public ModelAndView calculateProjectCost(@PathVariable("id") long id) {
        ModelAndView result = new ModelAndView("project/details");

        Project project = projectService.findById(id);
        ProjectCost projectCost = new ProjectCost();
        double cost = projectService.getCostPerProject(id);
        projectCost.setCost(cost);
        projectCost.setName(project.getName());

        result.addObject("projectCost", projectCost);

        return result;
    }
}
