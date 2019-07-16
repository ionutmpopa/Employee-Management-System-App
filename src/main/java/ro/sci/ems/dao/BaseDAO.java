package ro.sci.ems.dao;

import ro.sci.ems.domain.AbstractModel;

import java.util.Collection;
import java.util.Date;

public interface BaseDAO<T extends AbstractModel> {

	Collection<T> getAll();

	Collection<T> getAllByDate(Date date);
	
	T findById(Long id);
	
	T update(T model);
	
	boolean delete(T model);
}
