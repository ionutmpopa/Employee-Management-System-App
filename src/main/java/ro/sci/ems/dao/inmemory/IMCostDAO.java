package ro.sci.ems.dao.inmemory;

import org.springframework.util.StringUtils;
import ro.sci.ems.dao.CostDAO;
import ro.sci.ems.domain.Cost;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class IMCostDAO extends IMBaseDAO<Cost> implements CostDAO {

    @Override
    public Collection<Cost> searchByName(String query) {
        if (StringUtils.isEmpty(query)) {
            return getAll();
        }

        Collection<Cost> all = new LinkedList<Cost>(getAll());
        for (Iterator<Cost> it = all.iterator(); it.hasNext();) {
            Cost cst = it.next();
            String ss = cst.getTitle() + " " + cst.getCost();
            if (!ss.toLowerCase().contains(query.toLowerCase())) {
                it.remove();
            }
        }
        return all;
    }
}
