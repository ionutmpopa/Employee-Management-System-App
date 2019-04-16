package ro.sci.ems.domain;

import java.util.Objects;

public class RoleName {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleName roleName = (RoleName) o;
        return Objects.equals(name, roleName.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "RoleName{" +
                "name='" + name + '\'' +
                '}';
    }
}
