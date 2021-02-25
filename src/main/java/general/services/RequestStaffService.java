package general.services;


import java.util.ArrayList;

import general.dao.DAOException;
import general.tables.RequestStaff;

import org.springframework.transaction.annotation.Transactional;

public interface RequestStaffService {
	
	@Transactional
	void create(RequestStaff rs) throws DAOException;
	
	@Transactional
	void create(ArrayList<RequestStaff> rsList) throws DAOException;
}