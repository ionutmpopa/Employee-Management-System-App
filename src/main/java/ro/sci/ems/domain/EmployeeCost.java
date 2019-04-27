package ro.sci.ems.domain;

import java.util.List;
import java.util.Map;

public class EmployeeCost implements Comparable<EmployeeCost> {

    private Map<String, Double> projectNames;
    private List<String> projectName;
    private String name;
    private double cost;

    public Map<String, Double> getProjectNames() {
        return projectNames;
    }

    public void setProjectNames(Map<String, Double> projectNames) {
        this.projectNames = projectNames;
    }


    public List<String> getProjectName() {
        return projectName;
    }

    public void setProjectName(List<String> projectName) {
        this.projectName = projectName;
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
}
