package general.dao.impl;

import general.dao.ServCRMScheduleDao;
import general.tables.ServCRMSchedule;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("servCRMScheduleDao")
public class ServCRMScheduleDaoImpl extends GenericDaoImpl<ServCRMSchedule> implements ServCRMScheduleDao {
		public List<ServCRMSchedule> findAll() {
			Query query = this.em
					.createQuery("select a FROM ServCRMSchedule a");  
			List<ServCRMSchedule> la = query.getResultList();
			return la;
		}
		
		@Override
		public List<ServCRMSchedule> findAll(String condition) {
			String s = " SELECT a FROM ServCRMSchedule a WHERE " + condition 
					+ " order by a.scheduledDate DESC";
			//String s2 = " SELECT a.* FROM  SERV_CRMSCHEDULE a WHERE " + condition;
			//System.out.println("Query: " + s);
			Query query = this.em
					.createQuery(s); 
			List<ServCRMSchedule> la = query.getResultList();
			return la;
		}	

}
