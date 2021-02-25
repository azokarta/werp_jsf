package general.services;


import java.util.List;

import general.dao.DAOException;
import general.tables.RequestMatnr;

import org.springframework.transaction.annotation.Transactional;

public interface RequestMatnrService {
	
	@Transactional
	void create(RequestMatnr rm) throws DAOException;
	
	@Transactional
	void create(List<RequestMatnr> rmList) throws DAOException;
}