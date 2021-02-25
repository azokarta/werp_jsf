package general.services;

import general.dao.DAOException;
import general.tables.StaffOfficialData;

import org.springframework.transaction.annotation.Transactional;

public interface StaffOfficialDataService {
	@Transactional
	void create(StaffOfficialData d) throws DAOException;
	
	@Transactional
	void update(StaffOfficialData d) throws DAOException;
}
