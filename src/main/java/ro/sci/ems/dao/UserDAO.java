package ro.sci.ems.dao;

import ro.sci.ems.domain.User;

import java.util.Collection;

public interface UserDAO extends BaseDAO<User>{

    Collection<User> searchByName(String query);

    User findByEmail(String email);
}