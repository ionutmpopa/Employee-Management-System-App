package ro.sci.ems.domain;

import java.util.Date;

public class EmployeeDailyCost implements Comparable<EmployeeDailyCost> {

    private long id;
    private String name;
    private Date workingDate;
    private String employeeName;
    private String jobTitle;
    private double cost;
    private double hoursPerProject;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public double getHoursPerProject() {
        return hoursPerProject;
    }

    public void setHoursPerProject(double hoursPerProject) {
        this.hoursPerProject = hoursPerProject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public int compareTo(EmployeeDailyCost o) {
        return this.getWorkingDate().compareTo(o.getWorkingDate());
    }

    @Override
    public String toString() {
        return "EmployeeDailyCost{" +
                "name='" + name + '\'' +
                ", workingDate=" + workingDate +
                ", employeeName='" + employeeName + '\'' +
                ", cost=" + cost +
                ", hoursPerProject=" + hoursPerProject +
                '}';
    }
}
