package ro.sci.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ro.sci.ems.dao.TimecardDAO;
import ro.sci.ems.domain.Timecard;
import ro.sci.ems.exception.ValidationException;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class TimecardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimecardService.class);

    @Autowired
    private TimecardDAO timecardDAO;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CostService costService;

    @Autowired
    private UserService userService;

    @Autowired
    private TimecardService timecardService;

    public Collection<Timecard> listAll() {
        return timecardDAO.getAll();
    }

    public Collection<Timecard> listAllByDate(Date date) {
        return timecardDAO.getAllByDate(date);
    }

    public boolean delete(Long id) {
        LOGGER.debug("Deleting timecard with id: " + id);
        Timecard timecard = timecardDAO.findById(id);
        if (timecard != null) {
            timecardDAO.delete(timecard);
            return true;
        }

        return false;
    }

    public Timecard findById(long id) {
        return timecardDAO.findById(id);
    }

    public void save(Timecard timecard) throws ValidationException {
        LOGGER.debug("Saving: " + timecard);
        validate(timecard);
        timecardDAO.update(timecard);
    }

    private void validate(Timecard timecard) throws ValidationException {

        Date currentDate = new Date();
        List<String> errors = new LinkedList<>();
        Collection<Timecard> timecards = timecardService.listAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN"));
        double sum = 0;
        double finalHours = 0;

        for (Timecard timecard1 : timecards) {

            Timecard myTimecard = timecardService.findById(timecard1.getId());

            if ((timecard.getDate().getTime() == myTimecard.getDate().getTime()) &&
                    (timecard.getEmployee_id() == myTimecard.getEmployee_id())) {
                sum += myTimecard.getHours();
                finalHours = timecard.getHours() + sum;
            }
        }

        if ((finalHours > 8) && (!hasAdminRole)) {
            errors.add("The hours in a working day reached the limit! Contact your sys admin.");
        }

        if ((timecard.getHours() % 2) != 0) {
            errors.add("Only even numbers accepted!");
        }

        if (timecard.getEmployee_id() == 0) {
            errors.add("Employee id is Empty");
        }

        if (timecard.getProject_id() == 0) {
            errors.add("Project id is Empty");
        }

        if (timecard.getHours() == 0) {
            errors.add("Gender is Empty");
        }

        if (timecard.getDate() == null) {
            errors.add("Date is Empty");
        } else {
            if (!hasAdminRole) {
                String currentDateConv = currentDate.toString().replaceAll("^(.*)(\\s[0-9][0-9][:].*)", "$1");
                String timeCardConv = timecard.getDate().toString().replaceAll("^(.*)(\\s[0-9][0-9][:].*)", "$1");
                if (!currentDateConv.equalsIgnoreCase(timeCardConv)) {
                    errors.add("Invalid date! Cannot add past or future dates!");
                    System.out.println("CURRENT DATE: " + currentDateConv);
                    System.out.println("PASSED DATE: " + timeCardConv);
                }
            } else {
                if (currentDate.before(timecard.getDate())) {
                    errors.add("Invalid date! Cannot add future dates!");
                }
            }
        }
        if (timecard.getComment().isEmpty()) {
            errors.add("Comment is Empty");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toArray(new String[]{}));
        }
    }


    public TimecardDAO getDao() {
        return timecardDAO;
    }

    public void setDao(TimecardDAO dao) {
        this.timecardDAO = dao;
    }

}
