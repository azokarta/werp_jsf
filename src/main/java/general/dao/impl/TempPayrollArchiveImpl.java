package general.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.TempPayrollArchiveDao;
import general.tables.TempPayrollArchive;
@Component("tmpPrlArcDao")
public class TempPayrollArchiveImpl extends GenericDaoImpl<TempPayrollArchive> implements TempPayrollArchiveDao{
	public Long countDynamicSearch(String a_dynamicWhere){
    	Query query = this.em
                .createQuery("select count(t.bukrs) FROM TempPayrollArchive t where " + a_dynamicWhere); 
        Long count = (Long) query.getSingleResult();        
        return count;
    }
	public List<TempPayrollArchive> dynamicSearch(String a_dynamicWhere){
    	Query query = this.em
                .createQuery("select t FROM TempPayrollArchive t where " + a_dynamicWhere);
        List<TempPayrollArchive> tpa =  query.getResultList(); 
    	return tpa;
    }
	
	public List<TempPayrollArchive> dynamicSearchGroupByStaffBranchWaers(String a_dynamicWhere){
		List<TempPayrollArchive> tp = new ArrayList<TempPayrollArchive>();
    	Query query = this.em
                .createQuery("select t.staff_id,t.staff_name,t.branch_name, t.waers, sum( CASE WHEN t.drcrk = 'S' THEN t.amount ELSE (t.amount*-1) END ) FROM TempPayrollArchive t where " + a_dynamicWhere+" group by t.staff_id,t.staff_name,t.branch_name, t.waers");
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		TempPayrollArchive wa_tp = new TempPayrollArchive();
    		wa_tp.setStaff_id((long) result[0]);
    		wa_tp.setStaff_name(String.valueOf(result[1]));
    		wa_tp.setBranch_name(String.valueOf(result[2]));
    		wa_tp.setWaers(String.valueOf(result[3]));
    		wa_tp.setAmount((double) result[4]);
    		tp.add(wa_tp);
    	  }
    	 
         
    	return tp;
    }
}
