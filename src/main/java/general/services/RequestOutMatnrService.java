package general.services;


import java.util.List;

import general.dao.DAOException;
import general.tables.RequestOutMatnr;

import org.springframework.transaction.annotation.Transactional;

public interface RequestOutMatnrService {
	
	@Transactional
	void create(RequestOutMatnr rom) throws DAOException;
	
	@Transactional
	void create(List<RequestOutMatnr> romList) throws DAOException;
}