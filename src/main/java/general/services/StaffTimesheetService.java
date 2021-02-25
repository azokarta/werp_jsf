package general.services;

import general.dao.DAOException;
import general.tables.StaffTimesheet;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface StaffTimesheetService {
	
	@Transactional
	void save(List<StaffTimesheet> tmsList) throws DAOException;
}
