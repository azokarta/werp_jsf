package general.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.TempPayrollDao;
import general.output.tables.FosaBranchResultTable;
import general.output.tables.FosaResultTable;
import general.tables.TempPayroll;
@Component("tmpPrlDao")
public class TempPayrollImpl extends GenericDaoImpl<TempPayroll> implements TempPayrollDao{
	public Long countDynamicSearch(String a_dynamicWhere){
    	Query query = this.em
                .createQuery("select count(t.bukrs) FROM TempPayroll t where " + a_dynamicWhere); 
    	Long count = (Long) query.getSingleResult();        
        return count;
    }
	public List<TempPayroll> dynamicSearch(String a_dynamicWhere){
    	Query query = this.em
                .createQuery("select t FROM TempPayroll t where " + a_dynamicWhere);
        List<TempPayroll> tp =  query.getResultList(); 
    	return tp;
    }
	
	public List<TempPayroll> findAll(){  
    	Query query = this.em
                .createQuery("select tp FROM TempPayroll tp"); 
    	 
    	List<TempPayroll> tp =  query.getResultList();
    	return tp;
    }
	
	public void deleteAllByBukrs(String a_bukrs){      	       
        Query query = em.createQuery ("DELETE FROM TempPayroll where bukrs = :bukrs");
		query.setParameter("bukrs", a_bukrs);
		int deleted = query.executeUpdate (); 
    }
	
	public List<TempPayroll> dynamicSearchGroupByStaffBranchWaers(String a_dynamicWhere){
		List<TempPayroll> tp = new ArrayList<TempPayroll>();
    	Query query = this.em
                .createQuery("select t.staff_id,t.staff_name,t.branch_name, t.waers, sum( CASE WHEN t.drcrk = 'S' THEN t.amount ELSE (t.amount*-1) END ) FROM TempPayroll t where " + a_dynamicWhere+" group by t.staff_id,t.staff_name,t.branch_name, t.waers");
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		TempPayroll wa_tp = new TempPayroll();
    		wa_tp.setStaff_id((long) result[0]);
    		wa_tp.setStaff_name(String.valueOf(result[1]));
    		wa_tp.setBranch_name(String.valueOf(result[2]));
    		wa_tp.setWaers(String.valueOf(result[3]));
    		wa_tp.setAmount((double) result[4]);
    		tp.add(wa_tp);
    	  }
    	 
    	
    	return tp;
    }
	public List<FosaResultTable> getSalaryByOffices(String a_bukrs,String a_branch_id,java.sql.Date a_budat_from)
	{
		List<FosaResultTable> outputTable = new ArrayList<FosaResultTable>();
    	Query query = this.em
                .createNativeQuery("select tp.staff_id,"
                		+ "tp.waers,tp.amount,"
                		+ "(case tp.approve when 0 then 0 else 1 end ) approve,"
                		+ "salst.fio,"
                		+ "tp.branch_id,"
                		+ "tp.branch_id,"
                		+ "(select br2.text45 from branch br2 where br2.branch_id=tp.branch_id) as branch_name,"
                		+ "salst.fio as fio2 "
                		+ ",tp.amount2 "
                		+ ",tp.amount3 "
                		+ ",tp.amount4 "
                		+ " from "+
								"(select t.staff_id  ,t.waers,sum(case t.drcrk when 'H' then (t.amount*-1) else t.amount end) amount,"
								+ "sum(case t.type when 3 then 0 else (case t.drcrk when 'S' then t.amount else 0 end) end ) amount2, "
								+ "sum(case t.drcrk when 'H' then (t.amount*-1) else 0 end) amount3, "
								+ "sum(case t.type when 3 then (case t.drcrk when 'S' then t.amount else 0 end) else 0 end) amount4, "
									+ "sum(approve) approve,t.branch_id "+
									"from temp_payroll t "+
									"where t.bukrs='"+a_bukrs+"' and t.branch_id in ("+a_branch_id+")"+
									"group by t.staff_id,t.waers,t.branch_id) tp "+
									"LEFT JOIN "+
									"(select st.staff_id,"
									+ "INITCAP(lastname) ||' '|| INITCAP(firstname) ||' '|| INITCAP(middlename) as fio"
									+ " from staff st  ) salst "+
		"ON tp.staff_id=salst.staff_id "+
		"order by tp.branch_id, salst.fio "
                		);
    	int count=0;
    	List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			FosaResultTable wa_rt = new FosaResultTable();
			//wa_fot.setIndex(index);
			count++;
			//System.out.println(count);
			if (result[0]!=null) wa_rt.setStaff_id(Long.parseLong(String.valueOf(result[0])));
			if (result[1]!=null) wa_rt.setWaers(String.valueOf(result[1]));
			wa_rt.setAmount(Double.parseDouble(String.valueOf(result[2])));
			wa_rt.setApprove(Integer.parseInt(String.valueOf(result[3])));
			if (result[4]!=null) wa_rt.setFio(String.valueOf(result[4]));
			if (result[5]!=null) wa_rt.setSalary_id(Long.parseLong(String.valueOf(result[5])));			
			if (result[6]!=null) wa_rt.setBranch_id(Long.parseLong(String.valueOf(result[6])));
			if (result[7]!=null) wa_rt.setBranch_name(String.valueOf(result[7]));
			
			wa_rt.setEditable(true);
			if (result[5]==null)
			{
				wa_rt.setFio(String.valueOf(result[8]));
				wa_rt.setText("Отсутствует должность.");
				wa_rt.setEditable(false);
			}
			wa_rt.setAmount2(Double.parseDouble(String.valueOf(result[9])));
			wa_rt.setAmount3(Double.parseDouble(String.valueOf(result[10])));	
			wa_rt.setAmount4(Double.parseDouble(String.valueOf(result[11])));	
			wa_rt.setBukrs(a_bukrs);
			
			
			outputTable.add(wa_rt);
			

		}
    	
    	return outputTable;
		
	}
	public List<FosaBranchResultTable> getSalaryByOffices2(String a_bukrs,String a_branch_id,java.sql.Date a_budat_from)
	{
		List<FosaBranchResultTable> outputTable = new ArrayList<FosaBranchResultTable>();
    	Query query = this.em
                .createNativeQuery(
                		"select branch_name, sum(approve) approve from ("+
                		"select tp.staff_id,"
                		+ "tp.waers,tp.amount,"
                		+ "(case tp.approve when 0 then 0 else 1 end ) approve,"
                		+ "salst.fio,"
                		+ "salst.salary_id,"
                		+ "salst.branch_id, "
                		+ "(select br2.text45 from branch br2 where br2.branch_id=salst.branch_id) as branch_name,"
                		+ "(case salst.salary_id when null then (select INITCAP(firstname) ||' '|| INITCAP(lastname) ||' '|| INITCAP(middlename) as fio2 from staff st4 where st4.staff_id=tp.staff_id) else '' end) as fio2 "
                		
                		+ " from "+
								"(select t.staff_id  ,t.waers,sum(case t.drcrk when 'S' then t.amount else (t.amount*-1) end) amount, "
									+ "sum(approve) approve "+
									"from temp_payroll t "+
									"where t.bukrs='"+a_bukrs+"' and t.branch_id in ("+a_branch_id+")"+
									"group by t.staff_id,t.waers) tp "+
									"LEFT JOIN "+
									"(select s2.staff_id,"
									+ "INITCAP(firstname) ||' '|| INITCAP(lastname) ||' '|| INITCAP(middlename) as fio,"
									+ "s2.salary_id,s2.branch_id from salary s2, staff st where "+ 
									"st.staff_id = s2.staff_id and "+
									"s2.salary_id in ( "+
									"select max(s.salary_id) from salary s, branch b "+
									"where s.end_date > '"+a_budat_from+"' and s.bukrs='"+a_bukrs+"' and s.branch_id=b.branch_id and b.type=3 "+
									"group by s.staff_id )) salst "+
		"ON tp.staff_id=salst.staff_id "+
		"order by salst.branch_id, salst.fio) bybranch group by bybranch.branch_name "
                		);
    	List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			FosaBranchResultTable wa_rt = new FosaBranchResultTable();
			//wa_fot.setIndex(index);
			
			
			if (result[0]!=null) wa_rt.setBranch_name(String.valueOf(result[0]));
			wa_rt.setAmount_not_approved(Integer.parseInt(String.valueOf(result[1])));
			outputTable.add(wa_rt);

		}
		return outputTable;
	}
	public int updateDynamicTempPayroll(int a_approve, String a_dynamicWhere)
	{
		a_dynamicWhere = "update TempPayroll set approve = "+a_approve+" where "+a_dynamicWhere;
		System.out.println(a_dynamicWhere);
		Query query = this.em.createQuery(a_dynamicWhere);
		return query.executeUpdate();
	}
}
