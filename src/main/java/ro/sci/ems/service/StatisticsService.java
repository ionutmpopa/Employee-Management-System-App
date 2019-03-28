package ro.sci.ems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.sci.ems.domain.Cost;
import ro.sci.ems.domain.Employee;
import ro.sci.ems.domain.Timecard;

import java.sql.Time;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private TimecardService timecardService;

    @Autowired
    private CostService costService;

    @Autowired
    private EmployeeService employeeService;

    public Long calculateCostByProject(long projectId) {
      //TODO
        return 0l;
    }
}
