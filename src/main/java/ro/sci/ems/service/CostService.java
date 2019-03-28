package ro.sci.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ro.sci.ems.dao.CostDAO;
import ro.sci.ems.domain.Cost;
import ro.sci.ems.exception.ValidationException;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class CostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CostService.class);

    @Autowired
    private CostDAO costDAO;

    public Collection<Cost> listAll() {
        return costDAO.getAll();
    }

    public Collection<Cost> search(String query) {
        LOGGER.debug("Searching for " + query);
        return costDAO.searchByName(query);
    }

    public boolean delete(Long id) {
        LOGGER.debug("Deleting employee for id: " + id);
        Cost cost = costDAO.findById(id);
        if (cost != null) {
            costDAO.delete(cost);
            return true;
        }

        return false;
    }

    public Cost get(Long id) {
        LOGGER.debug("Getting employee for id: " + id);
        return costDAO.findById(id);

    }

    public void save(Cost cost) throws ValidationException {
        LOGGER.debug("Saving: " + cost);
        validate(cost);

        costDAO.update(cost);
    }

    private void validate(Cost cost) throws ValidationException {
        Date currentDate = new Date();

        List<String> errors = new LinkedList<String>();

        if (StringUtils.isEmpty(cost.getTitle())) {
            errors.add("Title is Empty or Invalid!");
        }


        if (cost.getCost() == 0) {
            errors.add("Cost is Empty");
        }



        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toArray(new String[] {}));
        }
    }

    public CostDAO getDao() {
        return costDAO;
    }

    public void setDao(CostDAO dao) {
        this.costDAO = dao;
    }
}

