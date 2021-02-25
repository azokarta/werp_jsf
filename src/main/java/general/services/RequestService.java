package general.services;


import java.util.List;

import general.dao.DAOException;
import general.tables.Request;
import general.tables.RequestMatnr;
import general.tables.Staff;

import org.springframework.transaction.annotation.Transactional;

import user.User;

public interface RequestService {
	
	@Transactional
	void create(Request r,List<RequestMatnr> reqMatnrs, User user) throws DAOException;
	
	@Transactional
	void update(Request r,List<RequestMatnr> reqMatnrs,User user) throws DAOException;
	
	@Transactional 
	void nextStep(Request r,List<general.tables.User> userList,User userData) throws DAOException;
}