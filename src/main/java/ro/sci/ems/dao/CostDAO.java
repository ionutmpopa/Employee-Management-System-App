package ro.sci.ems.dao;

import ro.sci.ems.domain.Cost;

import java.util.Collection;

public interface CostDAO extends BaseDAO<Cost> {

    Collection<Cost> searchByName(String query);
}
