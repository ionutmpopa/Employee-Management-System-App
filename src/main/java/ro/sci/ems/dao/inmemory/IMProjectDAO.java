package ro.sci.ems.dao.inmemory;

import org.springframework.util.StringUtils;
import ro.sci.ems.dao.ProjectDAO;
import ro.sci.ems.domain.Project;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

//@Repository
public class IMProjectDAO extends IMBaseDAO<Project> implements ProjectDAO {

    @Override
    public Collection<Project> searchByName(String query) {
        if (StringUtils.isEmpty(query)) {
            return getAll();
        }

        Collection<Project> all = new LinkedList<Project>(getAll());
        for (Iterator<Project> it = all.iterator(); it.hasNext();) {
            Project emp = it.next();
            String ss = emp.getName();
            if (!ss.toLowerCase().contains(query.toLowerCase())) {
                it.remove();
            }
        }
        return all;
    }


}
