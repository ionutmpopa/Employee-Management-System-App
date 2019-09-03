package ro.sci.ems.domain;

public class EmployeeCost implements Comparable<EmployeeCost> {

    private String name;
    private String employeeName;
    private double cost;
    private double hoursPerProject;

    public double getHoursPerProject() {
        return hoursPerProject;
    }

    public void setHoursPerProject(double hoursPerProject) {
        this.hoursPerProject = hoursPerProject;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
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
    public int compareTo(EmployeeCost o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "EmployeeCost{" +
                "name='" + name + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", cost=" + cost +
                ", hoursPerProject=" + hoursPerProject +
                '}';
    }
}
