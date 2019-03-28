package ro.sci.ems.dao.inmemory;

import org.springframework.stereotype.Repository;
import ro.sci.ems.dao.TimecardDAO;
import ro.sci.ems.domain.Timecard;

//@Repository
public class IMTimecardDAO extends IMBaseDAO<Timecard> implements TimecardDAO {
}
