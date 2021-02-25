package general.dao.impl;

import general.dao.StaffTimesheetDao;
import general.tables.Staff;
import general.tables.StaffTimesheet;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("staffTimesheetDao")
public class StaffTimesheetDaoImpl extends GenericDaoImpl<StaffTimesheet> implements StaffTimesheetDao{

	@Override
	public List<StaffTimesheet> findAll(String condition) {
		String s = " SELECT s1,s2 FROM StaffTimesheet s1 INNER JOIN s1.staff s2 ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);
		List<Object[]> result = query.getResultList();
		List<StaffTimesheet> out = new ArrayList<StaffTimesheet>();
		for(Object[] r:result){
			StaffTimesheet s1 = (StaffTimesheet)r[0];
			Staff s2 = (Staff)r[1];
			if(s2 == null){
				continue;
			}
			
			s1.setStaff(s2);
			
			out.add(s1);
			
		}
		
		return out;
	}
	
}
