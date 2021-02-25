package general.services;

import java.sql.Timestamp;
import java.util.Calendar;

import general.Validation;
import general.dao.DAOException;
import general.dao.EventLogDao;
import general.tables.EventLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("eventLogService")
public class EventLogServiceImpl implements
		EventLogService {
	
	@Autowired EventLogDao elDao;

	@Override
	public void create(EventLog el) throws DAOException {
		String error = validate(el);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		elDao.create(el);
	}
	
	private String validate(EventLog el){
		String s = "";
		Timestamp tm = new Timestamp(Calendar.getInstance().getTimeInMillis());
		el.setDatetime(tm);
		if(Validation.isEmptyLong(el.getStaff_id())){
			el.setStaff_id(0L);
		}
		
		if(Validation.isEmptyLong(el.getTransaction_id())){
			el.setTransaction_id(0L);
		}
		
		return s;
	}
}