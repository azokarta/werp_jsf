package general.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.ServFilterArchiveDao;
import general.tables.ServFilterArchive;

@Component("servFilterArchiveDao")
public class ServFilterArchiveDaoImpl extends GenericDaoImpl<ServFilterArchive> implements ServFilterArchiveDao {

	@Override
	public List<ServFilterArchive> findAllByMonth(String in_bukrs, Date in_date) {
		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		String df = format1.format(getFirstDateOfMonth(in_date));
		String dl = format1.format(getLastDateOfMonth(in_date));
		String query = "SELECT sf FROM ServFilterArchive sf WHERE sf.bukrs = '" + in_bukrs + "' "
				+ " AND (sf.serv_date between '" + df + "' and '" + dl + "')";
		Query q = this.em.createQuery(query);
		//List<ServFilter> result = q.setMaxResults(100).getResultList();
		List<ServFilterArchive> result = q.getResultList();
		return result;		
	}
	
	public List<ServFilterArchive> findByTovarSN(String in_bukrs, String in_tovSN) {
		String query = "SELECT sf FROM ServFilterArchive sf WHERE sf.bukrs = '" + in_bukrs + "and sf.tovar_sn = :tsn";
		Query q = this.em.createQuery(query);
		q.setParameter("tsn", in_tovSN);
		List<ServFilterArchive> result = q.getResultList();
		return result;			
	}

	public ServFilterArchive findPrevFNOByTovarSN(String in_bukrs, String in_tovSN, int a_fno, Long last_servId) {
		String query = "SELECT sf FROM ServFilterArchive sf "
				+ "WHERE sf.bukrs = '" + in_bukrs + "' and sf.tovar_sn = :tsn "
						+ "and sf.fno = :a_fno "
						+ "and sf.serv_id != :last_servId "
						+ "ORDER BY sf.serv_date DESC";
		Query q = this.em.createQuery(query);
		q.setParameter("tsn", in_tovSN);
		q.setParameter("a_fno", a_fno);
		q.setParameter("last_servId", last_servId);
		List<ServFilterArchive> result = q.getResultList();
		
		ServFilterArchive res = new ServFilterArchive();
		if (result.size() > 1) 
			res = result.get(1);
		else if (result.size() > 0) 
			res = result.get(0);
		return res;
	}
	
	public List<ServFilterArchive> findAllByServId(Long a_servId) {
		String query = "SELECT sf FROM ServFilterArchive sf "
				+ "WHERE sf.serv_id = :a_servId";
		Query q = this.em.createQuery(query);
		q.setParameter("a_servId", a_servId);
		List<ServFilterArchive> result = q.getResultList();
		return result;
	}
	
	private Date getFirstDateOfMonth(Date in_d) {
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(in_d);
		  cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		  return cal.getTime();
	}
	
	private Date getLastDateOfMonth(Date in_d) {
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(in_d);
		  cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		  return cal.getTime();
	}
	
}
