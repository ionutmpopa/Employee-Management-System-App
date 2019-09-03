package ro.sci.ems.dao;

import ro.sci.ems.domain.EmployeeDailyCost;

import java.util.Collection;
import java.util.Date;

public interface EmployeeCostDAO {

    Collection<EmployeeDailyCost> getAll(long id);

    boolean mergeIntoDailyCost();

    boolean updateTempCostReport(long employeeId, String projectName, String employeeName, double cost,
                                    Date workingDate, double hoursPerProject, String jobTitle);


}
