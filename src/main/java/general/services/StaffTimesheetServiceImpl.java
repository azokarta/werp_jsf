package general.services;

import general.Validation;
import general.dao.DAOException;
import general.dao.StaffTimesheetDao;
import general.tables.StaffTimesheet;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("staffTimesheetService")
public class StaffTimesheetServiceImpl implements
		StaffTimesheetService {

	@Autowired 
	StaffTimesheetDao tmsDao;
	
	
	
	@Override
	public void save(List<StaffTimesheet> tmsList) throws DAOException {
		for(StaffTimesheet tms:tmsList){
			if(Validation.isEmptyLong(tms.getId())){
				tms.setCreatedDate(Calendar.getInstance().getTime());
				tms.setUpdatedDate(Calendar.getInstance().getTime());
				tmsDao.create(tms);
			}else{
				tms.setUpdatedDate(Calendar.getInstance().getTime());
				tmsDao.update(tms);
			}
		}
	}
}