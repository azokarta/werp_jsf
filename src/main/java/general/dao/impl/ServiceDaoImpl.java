package general.dao.impl;

import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.ServiceDao;
import general.tables.ServFilter;
import general.tables.ServiceTable;
import general.tables.Staff;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import service.ServiceTableOutput;

@Component("serviceDao")
public class ServiceDaoImpl extends GenericDaoImpl<ServiceTable> implements ServiceDao {

	@Override
	public ServiceTable findByNumber(Long a_number) {
		ServiceTable resultSA = null;
		Query q = this.em.createQuery(String.format("SELECT s FROM ServiceTable s WHERE s.serv_num = '%d'", a_number));
		List results = q.getResultList();
		if(!results.isEmpty())
		{
			resultSA = (ServiceTable) results.get(0);
		}
		
		return resultSA;
	}
	
	@Override
	public ServiceTable findByAwkey(Long a_awkey, String a_bukrs) {
		ServiceTable resultSA = null;
		Query q = this.em.createQuery(String.format("SELECT s FROM ServiceTable s WHERE s.awkey = '%d' and bukrs='"+a_bukrs+"'", a_awkey));
		List results = q.getResultList();
		if(!results.isEmpty())
		{
			resultSA = (ServiceTable) results.get(0);
		}
		return resultSA;
	}
	
	@Override
	public List<ServiceTable> findAll() {
		Query q = this.em.createQuery(String.format("SELECT s FROM ServiceTable s"));
		List<ServiceTable> results = q.getResultList();
		
		return results;
	}
	
	@Override
	public List<ServiceTable> dynamicFindAll(String wcl) {
		Query q = this.em.createQuery(String.format("SELECT s FROM ServiceTable s WHERE " + wcl));
		List<ServiceTable> results = q.getResultList();
		
		return results;
	}
	
	@Override
	public ServiceTable dynamicFindSingle(String wcl) {
		Query q = this.em.createQuery(String.format("SELECT s FROM ServiceTable s WHERE " + wcl));
		List<ServiceTable> results = q.getResultList();
		ServiceTable result = new ServiceTable();
		if (results.size() > 0)
			result = results.get(0);
		return result;
	}
	
	@Override
	public List<ServiceTableOutput> findAll(String condition, int first, int max) {
		
		String s = " SELECT c FROM ServiceTable c ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(max);
		List<ServiceTable> l = q.getResultList();
		List<ServiceTableOutput> soList = new ArrayList<ServiceTableOutput>();
		
		Staff wa_staff;
		int i=0;		
		for (ServiceTable st:l) {
			ServiceTableOutput so = new ServiceTableOutput(++i);
			so.setService(st);
			
			wa_staff = null;
			if (st.getMaster_id() != null && st.getMaster_id() > 0) {
				s = "SELECT c FROM Staff c ";
				s += " WHERE c.staff_id = " + st.getMaster_id();
				q = this.em.createQuery(s);
				List<Staff> wa_st = q.getResultList();
				if (wa_st.size() > 0) {
					wa_staff = wa_st.get(0);
				}
				if (wa_staff != null && wa_staff.getStaff_id() != null) {
					so.setMaster(wa_staff);
				} else {
					so.setMaster(new Staff());
				}
			}
			
			wa_staff = null;
			if (st.getOper_id() != null && st.getOper_id() > 0) {
				s = "SELECT c FROM Staff c ";
				s += " WHERE c.staff_id = " + st.getOper_id();
				q = this.em.createQuery(s);
				List<Staff> wa_st = q.getResultList();
				if (wa_st.size() > 0) {
					wa_staff = wa_st.get(0);
				}
				if (wa_staff != null && wa_staff.getStaff_id() != null) {
					so.setOper(wa_staff);
				} else {
					so.setOper(new Staff());
				}
			}
			
			soList.add(so);
		}
		
		return soList;
	}
	
	@Override
	public int getRowCount(String condition) {
		String s = "select COUNT(c.id) FROM ServiceTable c ";
		if(!Validation.isEmptyString(condition)){
			s += " WHERE " + condition;
		}
		Query query = this.em.createQuery(s);        
        return ((Long) query.getSingleResult()).intValue();
	}
	
	@Override
	public List<ServiceTable> findAllInCurrentMonthByType(Long inBranchId, Date inDate, int inTypeId) {
		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		String df = format1.format(GeneralUtil.getFirstDateOfMonth(inDate));
		Date d = inDate;
		String dl = format1.format(GeneralUtil.getLastDateOfMonth(inDate));
		String wcl = " s.date_open between '" + df + "' and '" + dl + "'"
				+ " and s.branch_id = " + inBranchId
				+ " and s.serv_status <> " + ServiceTable.STATUS_CANCELLED;
		if (inTypeId > 0) wcl += " and s.serv_type = " + inTypeId;
		
		Query q = this.em.createQuery(String.format("SELECT s FROM ServiceTable s WHERE " + wcl));
		List<ServiceTable> results = q.getResultList();
		return results;
	}	
}
