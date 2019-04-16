package ro.sci.ems.domain;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.NumberFormat;

import java.util.Objects;

public class Cost extends AbstractModel {

    private Title title;

    @NumberFormat(pattern = "#,###.###")
    @Range(min = 1, message = "Must be a valid entry!")
    private double cost;

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cost costs = (Cost) o;
        return Double.compare(costs.cost, cost) == 0 &&
                title == costs.title;
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, cost);
    }

    @Override
    public String toString() {
        return "Costs{" +
                "title=" + title +
                ", cost=" + cost +
                '}';
    }
}
