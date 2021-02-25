package general.dao;

import java.util.List;
import general.tables.StaffTimesheet;

public interface StaffTimesheetDao extends GenericDao<StaffTimesheet>{
	
	public List<StaffTimesheet> findAll(String condition);
}
