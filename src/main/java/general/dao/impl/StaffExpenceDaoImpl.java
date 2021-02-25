package general.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.GeneralUtil;
import general.dao.StaffExpenceDao;
import general.tables.StaffExpence;

@Component("staffExpenceDao")

public class StaffExpenceDaoImpl extends GenericDaoImpl<StaffExpence> implements StaffExpenceDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffExpence> findAllByStaffId(Long staffId) {
		Query q = em.createQuery("SELECT s FROM StaffExpence s WHERE s.staff_id = " + staffId + " AND s.is_deleted IS NOT NULL AND s.is_deleted = 0 ",StaffExpence.class);
		return q.getResultList();
	}
	
	public List<StaffExpence> findAllGrouppedByStaffIDCurrency() {
		String select = "SELECT se.staff_id,sum(se.sum) as sum ,se.currency FROM StaffExpence se WHERE se.is_deleted = 0 group by staff_id, currency order by staff_id";
		Query query = this.em.createQuery(select);
		
		List<Object[]> results = query.getResultList();
		List<StaffExpence> l_se = new ArrayList<StaffExpence>();
    	for (Object[] result : results) {
    		StaffExpence wa_se = new StaffExpence();
    		wa_se.setStaff_id((long) result[0]);
    		wa_se.setSum((double) result[1]);
    		wa_se.setCurrency(String.valueOf(result[2]));
    		l_se.add(wa_se);
    	  }    	 
    	return l_se;
	}
	
	@Override
	public List<StaffExpence> findAllByBukrs(String a_bukrs) {
		Query q = this.em.createQuery("SELECT se FROM StaffExpence se WHERE bukrs = :s AND is_deleted = 0 ");
		q.setParameter("s",a_bukrs);
		List<StaffExpence> l = q.getResultList();
		return l;
	}
	
	@Override
	public List<StaffExpence> findAllByBukrsDate(String a_bukrs,java.sql.Date a_date) {
		Query q = this.em.createQuery("SELECT se FROM StaffExpence se WHERE bukrs = :s AND is_deleted = 0 and expence_date<'"+a_date+"' ");
		q.setParameter("s",a_bukrs);
		List<StaffExpence> l = q.getResultList();
		return l;
	}
}
