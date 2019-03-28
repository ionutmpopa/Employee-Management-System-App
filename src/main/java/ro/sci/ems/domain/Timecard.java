package ro.sci.ems.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

public class Timecard extends AbstractModel {

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date date;

    private String comment;

    private double hours;

    private long employee_id;

    private long project_id;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public long getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(long employee_id) {
        this.employee_id = employee_id;
    }

    public long getProject_id() {
        return project_id;
    }

    public void setProject_id(long project_id) {
        this.project_id = project_id;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timecard timeCard = (Timecard) o;
        return Double.compare(timeCard.hours, hours) == 0 &&
                employee_id == timeCard.employee_id &&
                project_id == timeCard.project_id &&
                Objects.equals(date, timeCard.date) &&
                Objects.equals(comment, timeCard.comment);
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, comment, hours, employee_id, project_id);
    }

    @Override
    public String toString() {
        return "TimeCard{" +
                "date=" + date +
                ", comment='" + comment + '\'' +
                ", hours=" + hours +
                ", employee_id=" + employee_id +
                ", project_id=" + project_id +
                '}';
    }
}
