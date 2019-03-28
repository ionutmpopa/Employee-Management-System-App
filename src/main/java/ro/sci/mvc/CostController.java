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
import ro.sci.ems.domain.Cost;
import ro.sci.ems.exception.ValidationException;
import ro.sci.ems.service.CostService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/cost")
public class CostController {

    private static Logger LOGGER = LoggerFactory.getLogger("CostController");

    @Autowired
    private CostService costService;

    @RequestMapping("")
    public ModelAndView list() {
        ModelAndView result = new ModelAndView("cost/listcost");


        Collection<Cost> costs = costService.listAll();
        result.addObject("costs", costs);

        return result;
    }

    @RequestMapping("/add")
    public ModelAndView add() {
        ModelAndView modelAndView = new ModelAndView("cost/addcost");
        modelAndView.addObject("cost", new Cost());
        return modelAndView;
    }


    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable("id") long id) {
        Cost cost = costService.get(id);
        ModelAndView modelAndView = new ModelAndView("cost/addcost");
        modelAndView.addObject("cost", cost);
        return modelAndView;
    }

    @RequestMapping(value= "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id ) {
        costService.delete(id);
        return "redirect:/cost";
    }

    @RequestMapping("/save")
    public ModelAndView save(@Valid Cost cost,
                             BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();
        if (!bindingResult.hasErrors()) {
            try {
                costService.save(cost);
                RedirectView redirectView = new RedirectView("/cost");
                modelAndView.setView(redirectView);
            } catch (ValidationException ex) {

                LOGGER.error("validation error", ex);

                List<String> errors = new LinkedList<>();
                errors.add(ex.getMessage());
                modelAndView = new ModelAndView("cost/addcost");
                modelAndView.addObject("errors", errors);
                modelAndView.addObject("cost", cost);
            }

        } else {
            List<String> errors = new LinkedList<>();

            for (FieldError error :
                    bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ":" + error.getCode());
            }

            modelAndView = new ModelAndView("cost/addcost");
            modelAndView.addObject("errors", errors);
            modelAndView.addObject("cost", cost);
        }

        return modelAndView;
    }
}
