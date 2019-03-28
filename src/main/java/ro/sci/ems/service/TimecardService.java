package ro.sci.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.sci.ems.dao.TimecardDAO;
import ro.sci.ems.domain.Timecard;
import ro.sci.ems.exception.ValidationException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class TimecardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimecardService.class);

    @Autowired
    private TimecardDAO timecardDAO;


    public Collection<Timecard> listAll() {
        return timecardDAO.getAll();
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

        List<String> errors = new LinkedList<String>();
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
        }

        if (timecard.getComment().isEmpty()) {
            errors.add("Comment is Empty");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toArray(new String[] {}));
        }
    }

    public TimecardDAO getDao() {
        return timecardDAO;
    }

    public void setDao(TimecardDAO dao) {
        this.timecardDAO = dao;
    }

}
