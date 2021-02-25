package general.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import general.dao.StaffOfficialDataDao;
import general.tables.StaffOfficialData;

import org.springframework.stereotype.Component;

@Component("staffOfficialDataDao")
public class StaffOfficialDataDaoImpl extends GenericDaoImpl<StaffOfficialData> implements StaffOfficialDataDao{


	@SuppressWarnings("unchecked")
	@Override
	public List<StaffOfficialData> findAll(String condition) {
		String q = " SELECT d FROM StaffOfficialData d WHERE is_deleted = 0 ";
		if(condition.length() > 0){
			q += " AND " + condition;
		}
		Query query = this.em.createQuery(q);
		List<StaffOfficialData> l = query.getResultList();
		return l;
	}
	@SuppressWarnings("unchecked")
	public Map<Long,StaffOfficialData> findAllMapByBukrs(String a_bukrs)
	{
		String select = "SELECT sod.staff_id, sod.currency, sod.ipn, sod.pension, sod.social_contribution, sod.sub_company_id,sod.sod_id,sod.osms,sod.osmsFromStaff FROM StaffOfficialData sod where bukrs = :s AND is_deleted = 0 ";
		Query query = this.em
                .createQuery(select); 
		query.setParameter("s",a_bukrs);
    	Map<Long,StaffOfficialData> l_sod_map = new HashMap<Long,StaffOfficialData>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		StaffOfficialData wa_sod = new StaffOfficialData();    		
    		wa_sod.setStaff_id((long) result[0]);
    		wa_sod.setCurrency(String.valueOf(result[1]));
    		wa_sod.setIpn((double) result[2]);
    		wa_sod.setPension((double) result[3]);
    		wa_sod.setSocial_contribution((double) result[4]);
    		wa_sod.setSub_company_id((long) result[5]);
    		wa_sod.setSod_id((long) result[6]);
    		if (result[7]!=null) wa_sod.setOsms((double) result[7]); 
    		if (result[8]!=null) wa_sod.setOsmsFromStaff((double) result[8]); 
    		
    		l_sod_map.put(wa_sod.getStaff_id(),wa_sod );
    	}
    	return l_sod_map;
	}
}
