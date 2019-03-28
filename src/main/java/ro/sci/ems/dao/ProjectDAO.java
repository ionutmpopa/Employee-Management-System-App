package ro.sci.ems.dao;

import ro.sci.ems.domain.Project;

import java.util.Collection;


public interface ProjectDAO extends BaseDAO<Project> {

    Collection<Project> searchByName(String query);
}
