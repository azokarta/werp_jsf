package general.dao.impl;

import general.GeneralUtil;
import general.dao.BsikDao;
import general.dao.DAOException;
import general.dao.PayrollDao;
import general.dao.UserDao;
import general.output.tables.FaeaOutputTable;
import general.output.tables.RfcojResultTable;
import general.tables.Bkpf;
import general.tables.Bonus;
import general.tables.Payroll; 

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("payrollDao")
public class PayrollDaoImpl extends GenericDaoImpl<Payroll> implements PayrollDao{
	@Autowired
    private UserDao userDao;
	public List<Payroll> findByStaffId(Long a_staff_id, String a_bukrs){
    	Query query = this.em
                .createQuery("select p FROM Payroll p where p.staff_id= :staff_id order by payroll_date asc");
        query.setParameter("staff_id", a_staff_id);   
        List<Payroll> prl = query.getResultList();
    	return prl;
    }
	
	public List<String> findWaersByStaffId(Long a_staff_id){
    	Query query = this.em
                .createQuery("select p.waers FROM Payroll p where p.staff_id= :staff_id group by p.waers");
        query.setParameter("staff_id", a_staff_id);   
        List<String> prl = query.getResultList();
    	return prl;
    }
	
	public List<Payroll> findByMonatGjahr(int a_monat, int a_gjahr, String a_bukrs){
    	Query query = this.em
                .createQuery("select p FROM Payroll p where p.monat = :a_monat and p.gjahr = :a_gjahr and p.bukrs = :a_bukrs");
        query.setParameter("a_monat", a_monat);   
        query.setParameter("a_gjahr", a_gjahr);
        query.setParameter("a_bukrs", a_bukrs);
        List<Payroll> prl = query.getResultList();
    	return prl;
    }
	
	public List<Payroll> findByBukrsBranchAll(List<Long> al_staff_id, String a_bukrs){
    	Query query = this.em
                .createQuery("select p "+
        				" FROM  Payroll p"+
        				" where "
        				+ " p.staff_id IN (:staff_id)"
        				+ " order by p.staff_id ASC"); 
    	query.setParameter("staff_id", al_staff_id);
        List<Payroll> prl = query.getResultList();
    	return prl;
    }
	public List<Payroll> findByBukrsBranchAllPeriod(List<Long> al_staff_id, Date begDate, Date  endDate, String a_bukrs, int a_type,String a_waers){
//		<f:selectItem itemLabel="Все" itemValue="0" />
//		<f:selectItem itemLabel="Баланс" itemValue="1" />
//		<f:selectItem itemLabel="Депозит" itemValue="2" />
//		<f:selectItem itemLabel="Заблокировано" itemValue="3" />
//		<f:selectItem itemLabel="Запрос на аванс" itemValue="4" />
//		<f:selectItem itemLabel="Долг" itemValue="5" />
		Calendar curDate = Calendar.getInstance();
		String addSelect = "";
		if (a_type==1)
			addSelect = "where  p.bukrs = :bukrs and p.staff_id IN (:staff_id) and (approve = 0 or (approve = 8 and drcrk='H'))  and bldat<=:bldat and p.payroll_date between :begDate and :endDate and waers = :waers order by p.staff_id ASC";
		else if (a_type==2) addSelect = "where  p.bukrs = :bukrs and p.staff_id IN (:staff_id) and approve=1 and drcrk='S' and p.payroll_date between :begDate and :endDate and waers = :waers order by p.staff_id ASC";
		else if (a_type==3) addSelect = "where  p.bukrs = :bukrs and p.staff_id IN (:staff_id) and approve=0 and bldat>:bldat and p.payroll_date between :begDate and :endDate and waers = :waers order by p.staff_id ASC";
		else if (a_type==4) addSelect = "where p.payroll_id IN ( "
														+ "select max(p2.payroll_id) FROM  Payroll p2  "
														+ "where  p2.bukrs = :bukrs and p2.staff_id IN (:staff_id) and p2.approve in (3,4) and p2.drcrk='H'  "
														+ "group by p2.staff_id, p2.waers) and p.payroll_date between :begDate and :endDate and waers = :waers "
										+ "order by p.staff_id ASC";
		else if (a_type==5) addSelect = "where p.bukrs = :bukrs and  p.staff_id IN (:staff_id) and approve=8 and p.payroll_date between :begDate and :endDate and waers = :waers order by p.staff_id ASC";
		else if (a_type==0) addSelect = "where  p.bukrs = :bukrs and p.staff_id IN (:staff_id) and p.payroll_date between :begDate and :endDate and waers = :waers order by p.payroll_date Asc";
    	Query query = this.em
                .createQuery("select p "+
        				" FROM  Payroll p "+addSelect); 
    	query.setParameter("staff_id", al_staff_id);
    	query.setParameter("begDate", begDate);
    	query.setParameter("endDate", endDate);
    	query.setParameter("bukrs", a_bukrs);
    	query.setParameter("waers", a_waers);
    	if (a_type==1||a_type==3)
    	{
    		query.setParameter("bldat", GeneralUtil.getSQLDate(curDate));
    	}
    	
        List<Payroll> prl = query.getResultList();
    	return prl;
    }
	public List<Payroll> findByBukrsBranchAll2(List<Long> al_staff_id, String a_bukrs){
    	Query query = this.em
                .createQuery("select p.staff_id, "
                		+ "sum(CASE WHEN (drcrk = 'S') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, " 
                		+ "waers FROM  Payroll p "
                		+ "where  p.bukrs = :bukrs and p.staff_id IN (:staff_id) and approve=0 group by staff_id, waers order by p.staff_id ASC"); 
    	query.setParameter("staff_id", al_staff_id);
    	query.setParameter("bukrs", a_bukrs);
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }
	
	public List<Payroll> findByBukrsBranchAllSchet(List<Long> al_staff_id, Date a_today, String a_bukrs){
    	Query query = this.em
                .createQuery("select p.staff_id, "
                		+ "sum(CASE WHEN (drcrk = 'S') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, " 
                		+ "waers FROM  Payroll p "
                		+ "where  p.bukrs = :bukrs and p.staff_id IN (:staff_id) and (approve = 0)  and bldat<=:bldat group by staff_id, waers order by p.staff_id ASC"); 
    	query.setParameter("staff_id", al_staff_id);
    	query.setParameter("bldat", a_today);
    	query.setParameter("bukrs", a_bukrs);
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }
	public List<Payroll> findByBukrsBranchAllDeposit(List<Long> al_staff_id, String a_bukrs){
    	Query query = this.em
                .createQuery("select p.staff_id, "
                		+ "sum(CASE WHEN (drcrk = 'S') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, " 
                		+ "waers FROM  Payroll p "
                		+ "where  p.bukrs = :bukrs and p.staff_id IN (:staff_id) and approve=1 and drcrk='S' group by staff_id, waers order by p.staff_id ASC"); 
    	query.setParameter("staff_id", al_staff_id);
    	query.setParameter("bukrs", a_bukrs);
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }
	
	
	public List<Payroll> findByBukrsBranchAllZablok(List<Long> al_staff_id, Date a_today, String a_bukrs){
    	Query query = this.em
                .createQuery("select p.staff_id, "
                		+ "sum(CASE WHEN (drcrk = 'S') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, " 
                		+ "waers FROM  Payroll p "
                		+ "where  p.bukrs = :bukrs and p.staff_id IN (:staff_id) and approve=0  and bldat>:bldat group by staff_id, waers order by p.staff_id ASC"); 
    	query.setParameter("staff_id", al_staff_id);
    	query.setParameter("bldat", a_today);
    	query.setParameter("bukrs", a_bukrs);
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }
	public List<Payroll> findByBukrsBranchAllDolg(List<Long> al_staff_id, Date a_today, String a_bukrs){
    	Query query = this.em
                .createQuery("select p.staff_id, "
                		+ "sum(CASE WHEN (drcrk = 'H') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, " 
                		+ "waers FROM  Payroll p "
                		+ "where p.bukrs = :bukrs and  p.staff_id IN (:staff_id) and approve=8 group by staff_id, waers order by p.staff_id ASC"); 
    	query.setParameter("staff_id", al_staff_id);
    	query.setParameter("bukrs", a_bukrs);
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }
	public List<Payroll> findByBukrsBranchAllDoubtfulDolg(List<Long> al_staff_id, Date a_today, String a_bukrs){
		Query query = this.em
				.createQuery("select p.staff_id, "
						+ "sum(CASE WHEN (drcrk = 'H') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, "
						+ "waers FROM  Payroll p "
						+ "where p.bukrs = :bukrs and  p.staff_id IN (:staff_id) and approve=7 group by staff_id, waers order by p.staff_id ASC");
		query.setParameter("staff_id", al_staff_id);
		query.setParameter("bukrs", a_bukrs);
		List<Payroll> l_prl = new ArrayList<Payroll>();
		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			Payroll wa_prl = new Payroll();
			wa_prl.setStaff_id((long) result[0]);
			wa_prl.setDmbtr((double) result[1]);
			wa_prl.setWaers(String.valueOf(result[2]));
			l_prl.add(wa_prl);
		}
		return l_prl;
	}
    public List<Payroll> findByBukrsBranchAvansZapros(List<Long> al_staff_id, Date a_today, String a_bukrs){
    	Query query = this.em
                .createQuery(" select p.staff_id, "
                        		+ "CASE WHEN (drcrk = 'H') THEN dmbtr ELSE (dmbtr * -1) END as dmbtr, " 
                        		+ "waers,approve,payroll_id FROM  Payroll p "
                        		+ "where p.payroll_id IN ( "
                        										+ "select max(p2.payroll_id) FROM  Payroll p2  "
                        										+ "where  p2.bukrs = :bukrs and p2.staff_id IN (:staff_id) and p2.approve in (3,4) and p2.drcrk='H'  "
                        										+ "group by p2.staff_id, p2.waers) "
                        		+ "order by p.staff_id ASC"); 
    	query.setParameter("staff_id", al_staff_id);
    	query.setParameter("bukrs", a_bukrs);
    	//query.setParameter("bldat", a_today);
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		wa_prl.setApprove((int) result[3]);
    		wa_prl.setPayroll_id((long) result[4]);
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }

	@Override
	public List<Payroll> findAll(String condition) {
		String s = "select p FROM Payroll p ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		s += " order by payroll_date asc ";
		Query query = this.em
                .createQuery(s);
		return query.getResultList();
	}
	
	public int dynamicFindCountPayroll(String a_dynamicWhere){ 
    	
    	Query query = this.em
                .createQuery("select count(p.payroll_id) FROM Payroll p where "+a_dynamicWhere); 
    	Long count = 0L;
    	count = (long) query.getSingleResult();
    	return  (int) (long) count;
    	
    }
	public Payroll dynamicFindSinglePayroll(String a_dynamicWhere){ 
    	
    	Query query = this.em
                .createQuery("select p FROM Payroll p where "+a_dynamicWhere); 
    	List<Payroll> l_prl =  query.getResultList();
    	if (l_prl!=null && l_prl.size()>0)
    	{
    		return l_prl.get(0);
    	}
    	else
    		return null;
    	
    }
	
	public List<Long> dynamicFindPayrollId(String a_dynamicWhere){ 
    	List<Long> l_prl = new ArrayList<Long>();
    	Query query = this.em
                .createQuery("select p.payroll_id FROM Payroll p where "+a_dynamicWhere); 
    	
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		l_prl.add((long) result[0]);
    	  }
    	return l_prl;
    	
    }
	
	public int dynamicFindCountPayroll(String a_dynamicWhere,List<Long> al_manager_prl){ 
    	int count = 0;
    	Query query = this.em
                .createQuery("select p.payroll_id FROM Payroll p where parent_payroll_id IN (:payroll_id) and "+a_dynamicWhere); 
    	query.setParameter("payroll_id", al_manager_prl);
    	count = (int) query.getSingleResult();
    	return count;
    	
    }
	
	public List<FaeaOutputTable> dynamicFindFaea(String a_dynamicWhere){ 
    	int count = 0; 
    	Query query = this.em
                .createQuery("select p.bukrs,"
                		+ " p.branch_id,"
                		+ " p.staff_id,"
                		+ " p.approve,"
                		+ " p.waers,"
                		+ " p.dmbtr,"
                		+ " p.created_by,"
                		+ " p.approved_by,"
                		+ " p.payroll_date,"
                		+ " b.text45,"
                		+ " s.lastname,"
                		+ " s.firstname,"
                		+ " p.created_by,"
                		+ " p.approved_by,"
                		+ " p.payroll_id,"
                		+ " p.text45"
                		+ " FROM Payroll p, Branch b, Staff s where (p.branch_id = b.branch_id or p.branch_id is null) and (p.staff_id=s.staff_id) and "+a_dynamicWhere); 
    	//b.text45
    	//private String staffFio;
    	//private String approveText;
    	//private String created_byFio;
    	//private String approved_byFio;
    	List<FaeaOutputTable> rt_list = new ArrayList<FaeaOutputTable>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		FaeaOutputTable rt = new FaeaOutputTable();
    		rt.setBukrs(String.valueOf(result[0]));
    		if (result[1]!=null)
    		{
    			rt.setBranch_id((long) result[1]);
    		}
    		if (result[2]!=null)
    		{
    			rt.setStaff_id((long) result[2]);
    		}
    		rt.setApprove((int) result[3]);
    		if (rt.getApprove()==2 || rt.getApprove()==0 || rt.getApprove()==4)
    		{
    			rt.setNoteditable(true);
    		}
    		
    		if (result[4]!=null)
    		{
    			rt.setWaers(String.valueOf(result[4]));
    		}
    		rt.setDmbtr((double) result[5]);
    		if (result[6]!=null)
    		{
    			rt.setCreated_by((long) result[6]);
    		}
    		if (result[7]!=null)
    		{
    			rt.setApproved_by((long) result[7]);
    		}
    		rt.setPayroll_date(new SimpleDateFormat("dd.MM.yyyy").format(result[8]));
    		if (result[9]!=null)
    		{
    			rt.setBranchName(String.valueOf(result[9]));
    		}
    		
    		if (result[10]!=null)
    		{
    			rt.setStaffFio(String.valueOf(result[10]));
    		}
    		
    		if (result[11]!=null)
    		{
    			rt.setStaffFio(rt.getStaffFio()+" "+String.valueOf(result[11]));
    		}
    		
    		if (result[12]!=null)
    		{
    			rt.setCreated_by((long) result[12]);
    			rt.setCreated_byFio(userDao.getUserFio(rt.getCreated_by()));
    			
    		}
    		
    		if (result[13]!=null)
    		{
    			rt.setApproved_by((long) result[13]);
    			rt.setApproved_byFio(userDao.getUserFio(rt.getApproved_by()));
    		}
    		
    		if (result[14]!=null)
    		{
    			rt.setPayroll_id((long) result[14]);
    		}
    		
    		if (result[15]!=null)
    		{
    			rt.setApproveText(String.valueOf(result[15]));
    		}
    		
    		rt_list.add(rt);
    	}  
    	 
    	
    	
    	return rt_list;
    	
    }
	
	public List<Object[]> serviceZp(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

			sel = "select prem.*,st.customer_id,st.lastname||' '||st.firstname||' '||st.middlename as fio,br.text45  from"
					+" (select s.bukrs,s.branch_id,s.master_id staff_id,s.currency,sum(s.master_premi) as summa from service s where s.date_open between '"+a_firstDay+"' and '"+a_lastDay+"'"
					+" and s.serv_status not in 6 and s.master_id is not null and s.master_premi>0 and s.bukrs="+a_bukrs
							+" group by s.bukrs,s.branch_id,s.master_id,s.currency"
							+" UNION ALL"
							+" select s.bukrs,s.branch_id,s.oper_id staff_id,s.currency,sum(s.oper_premi) as summa from service s where s.date_open between '"+a_firstDay+"' and '"+a_lastDay+"'"
							+" and s.serv_status not in 6 and s.oper_id is not null and s.oper_premi>0 and s.bukrs="+a_bukrs
							+" group by s.bukrs,s.branch_id,s.oper_id,s.currency) prem,staff st,branch br"
							+" where prem.staff_id=st.staff_id"
							+" and prem.branch_id=br.branch_id"
							+" order by prem.bukrs,br.text45,fio";
			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public List<Object[]> nachalnik2000(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

			sel = "select sa.bukrs,sa.branch_id,sa.staff_id,'KZT' waers,0 summa,st.customer_id,st.lastname||' '||st.firstname||' '||st.middlename as fio,br.text45,p.position_id,p.text"
					+" from salary sa, staff st, position p,branch br"
					+" where sa.staff_id=st.staff_id and sa.end_date>='"+a_lastDay+"'"
					+" and sa.beg_date<='"+a_lastDay+"' and sa.position_id in (45,116,117)"
					+" and sa.position_id=p.position_id"
					+" and sa.branch_id=br.branch_id"
					+" and sa.bukrs="+a_bukrs;
			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public List<Object[]> nachalnikAndAnalyticZp(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

			sel = "select sa.bukrs,sa.branch_id,sa.staff_id,'KZT' waers,0 summa,st.customer_id,st.lastname||' '||st.firstname||' '||st.middlename as fio,br.text45,p.position_id,p.text,br.country_id"
					+" from salary sa, staff st, position p,branch br"
					+" where sa.staff_id=st.staff_id and sa.end_date>='"+a_lastDay+"'"
					+" and sa.beg_date<='"+a_lastDay+"' and sa.position_id in (98,104,95,99,97,44,45)"
					+" and sa.position_id=p.position_id"
					+" and sa.branch_id=br.branch_id and br.country_id in (1,5)"
					+" and sa.bukrs="+a_bukrs;
			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public List<Object[]> finAgent70000Tenge
	(String a_bukrs, int a_gjahr, int a_monat,String a_waers,double a_summa ) throws DAOException
	//(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay,String a_waers,double a_summa ) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

//			sel = "select br.branch_id,st.staff_id,st.customer_id, initcap(st.lastname)||' '|| initcap(st.firstname) fio,br.text45,p.position_id,cn.currency ,p.text"
//
//+" from ("
//+" select sa.bukrs,sa.staff_id,min(sa.branch_id) branch_id,min (sa.position_id) position_id from salary sa where sa.beg_date<'"+a_lastDay+"' and sa.end_date>='"+a_lastDay+"'"
//+" and sa.position_id=9 group by sa.staff_id,sa.bukrs)zz ,staff st, branch br,position p,country cn"
//+" where "
//+" st.staff_id=zz.staff_id and zz.branch_id=br.branch_id and p.position_id=zz.position_id"
//+" and cn.country_id=br.country_id and"
//+" zz.staff_id in (select nvl(collector,0) from ("
//+" select(case con3.collector when null then '' else "
//                                           +" (select INITCAP(firstname) ||' '|| INITCAP(lastname) ||' '|| INITCAP(middlename) "
//                                           +" from staff st where st.staff_id=con3.collector )end) as collector_name,"
//                        +" count(con3.contract_id) as kol,con3.waers,con3.collector, con3.bukrs ,"
//                        +" sum(CASE con3.payment_schedule WHEN 1 THEN 0 ELSE (con3.sum2-con3.paid)+collectedamount END) as ras_plan ,"
//                        +" sum(CASE con3.payment_schedule WHEN 1 THEN 0 ELSE collectedamount END) as ras_poluchen ,"
//                        +" sum(CASE con3.payment_schedule WHEN 1 THEN (con3.sum2-con3.paid)+collectedamount  ELSE 0 END) as one_month_plan ,"
//                        +" sum(CASE con3.payment_schedule WHEN 1 THEN collectedamount  ELSE 0 END) as one_month_poluchen, "
//                        +" sum(CASE con3.payment_schedule WHEN 1 THEN 0 ELSE con3.sum2 - case when con3.paid-collectedamount < 0 then 0 else con3.paid-collectedamount end END) as ras_plan_nach_mes "                        
//                                 +" from (  select conz1.*, sum(case conz1.waers when 'USD'  then  nvl(b.dmbtr,0) else nvl(b.wrbtr,0) end)  collectedamount "
//                                         +" from ( select con.contract_id,con.contract_number,con.bukrs,con.waers,con.collector,con.payment_schedule,con.contract_status_id,con.cancel_date ,"
//+" con.fin_branch_id branch_id ,sum(case when is_firstpayment = 1 then 0 else ps.sum2 end ) sum2 ,sum(case when is_firstpayment = 1 then 0 else ps.paid end ) "
//+" paid from contract con, payment_schedule ps where ps.awkey=con.awkey and ps.bukrs = con.bukrs and con.waers='"+a_waers+"' "
//+" and ps.payment_date<='"+a_lastDay+"' and con.contract_date<='"+a_lastDay+"' and con.payment_schedule<>0 and con.contract_status_id not in (2,3,7) "
//+" and con.contract_status_id in (1,5,6,10,11,12,13) group by con.contract_id,con.contract_number,con.fin_branch_id,con.bukrs,"
//+" con.waers,con.collector,con.price,con.paid, con.payment_schedule,con.contract_status_id,con.cancel_date ,con.branch_id"
//                                              +" ) conz1  left join bkpf b on conz1.contract_number=b.contract_number  and b.blart='CP' "
//                                              +" and b.bldat >'2016-10-11' and b.bldat  between '"+a_firstDay+"' and '"+a_lastDay+"' "
//                                              +" group by conz1.contract_id,conz1.contract_number,conz1.branch_id,conz1.bukrs,conz1.waers,conz1."
//                                              +" collector,conz1.payment_schedule, conz1.contract_status_id,conz1.cancel_date,sum2,paid"
//                                      +" ) con3, branch br where br.branch_id = con3.branch_id "
//                                      +" and sum2+paid+collectedamount>0  and br.bukrs = '"+a_bukrs+"'  "
//                                      +" group by con3.waers,con3.collector,con3.bukrs) where ras_plan_nach_mes<="+a_summa+")  ";
			
			sel = "select r1.branch_id,r1.staff_id,st.customer_id, r1.collector_name,br.text45,9,r1.waers"
					+" from ( select r.staff_id,r.collector_name,r.waers,sum(r.ras_plan) ras_plan,min(r.branch_id) branch_id"
					+" from rfcol r where r.status=0"
					+" and r.bukrs="+a_bukrs+" and r.gjahr="+a_gjahr+" and r.monat="+a_monat+" and r.period='BEG' and r.waers='"+a_waers+"'"
					+" and r.staff_id is not null"
           +" group by r.staff_id,r.collector_name,r.waers"
           +" ) r1, staff st, branch br where  br.branch_id=r1.branch_id and  r1.staff_id=st.staff_id"
           +" and r1.ras_plan<="+a_summa
           +" and r1.staff_id in (select p.staff_id from pyramid p where p.position_id=9)";
			

			
			System.out.println(sel);			
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public Object[] totalContractGreenLight(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(*) total"
				 +" ,sum(case when c.contract_type_id in (4,6) then 1 else 0 end) atlas"
				 +" ,sum(case when c.contract_type_id in (5,10) then 1 else 0 end) eco"
				 +" ,sum(case when c.contract_type_id in (3,17) then 1 else 0 end) rainbow"  
				 +" from Contract c where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"'" 
				 +" and c.contract_status_id not in (2,3) and c.bukrs='"+a_bukrs+"'";
								
			Query query = this.em.createNativeQuery(sel);
			
			
			Object[] result = (Object[]) query.getSingleResult();
			
			return result;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public int totalContractExceptBaku(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and br.country_id in (1,5,6) and c.bukrs='"+a_bukrs+"' and c.markedType=0";
								
			Query query = this.em.createQuery(sel);
			
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public int totalContractDynamic(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay,List<Long> l_countryId) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and br.country_id in (:countryId) and c.bukrs='"+a_bukrs+"' and c.markedType=0 ";
								
			Query query = this.em.createQuery(sel);
			query.setParameter("countryId", l_countryId);
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	
	public int totalContractsByUserBranch(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay, Long a_staff_id) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

			sel = "select count(distinct(c.contract_id)) from contract c, branch b, user_branch ub, user_table u"
					+" where c.bukrs='"+a_bukrs+"' and c.marked_type=0  and c.contract_status_id not in (2,3)"
					+" and c.branch_id=b.branch_id and ub.user_id=u.user_id and u.staff_id="+a_staff_id
					+" and b.branch_id=ub.branch_id"
					+" and c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"'";

			
						
			Query query = this.em.createNativeQuery(sel);
//			BigDecimal _a = new BigDecimal("0.03");
			
			Number singleResult = (Number) query.getSingleResult();
			return singleResult.intValue();
//			Object[] result 
//			if (result[0]!=null) return (int) result[0];
//			else return 0;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
		
		
		
			
	}
	public int totalContractRobocleanDynamic(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay,List<Long> l_countryId) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and c.bukrs='"+a_bukrs+"' and c.markedType=0 and br.business_area_id=1 and br.country_id in (:countryId) ";
								
			Query query = this.em.createQuery(sel);
			query.setParameter("countryId", l_countryId);
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public int totalContractCebilonDynamic(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay,List<Long> l_countryId) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and c.bukrs='"+a_bukrs+"' and c.markedType=0 and br.business_area_id=2 and br.country_id in (:countryId) ";
								
			Query query = this.em.createQuery(sel);
			query.setParameter("countryId", l_countryId);
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public int totalContractKazakhstan(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and br.country_id in (1) and c.bukrs='"+a_bukrs+"' and c.markedType=0";
								
			Query query = this.em.createQuery(sel);
			
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	
	
	public int totalContractRobocleanExceptBaku(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and c.bukrs='"+a_bukrs+"' and c.markedType=0 and br.business_area_id=1 and br.country_id in (1,5,6) ";
								
			Query query = this.em.createQuery(sel);
			
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public int totalContractRoboclean(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and c.bukrs='"+a_bukrs+"' and c.markedType=0 and br.business_area_id=1";
								
			Query query = this.em.createQuery(sel);
			
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public int totalContractCebilon(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and c.bukrs='"+a_bukrs+"' and c.markedType=0 and br.business_area_id=2";
								
			Query query = this.em.createQuery(sel);
			
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public int totalContractCebilonExceptBaku(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and c.bukrs='"+a_bukrs+"' and c.markedType=0 and br.business_area_id=2 and br.country_id in (1,5,6) ";
								
			Query query = this.em.createQuery(sel);
			
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public int totalContractTotal(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			int count = 0;
			String sel = "";

			sel = "select count(c) from Contract c,Branch br"
					+" where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.contract_status_id not in (2,3)"
					+" and br.branch_id =c.branch_id and c.bukrs='"+a_bukrs+"' and c.markedType=0";
								
			Query query = this.em.createQuery(sel);
			
			
			count =Integer.parseInt(String.valueOf(query.getSingleResult()));			
			return count;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public List<Object[]> finAgentZp(String a_bukrs, int a_gjahr, int a_monat) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

			sel = "select r.bukrs,r.waers,r.staff_id,r.collector_name,st.customer_id,r.branch_id"
					+" ,ras_plan, ras_poluchen,one_month_poluchen*5/100  one_month_poluchen,br.text45"
					+" from rfcol r, staff st, branch br where br.branch_id=r.branch_id and r.status=0 and r.staff_id=st.staff_id and r.bukrs="+a_bukrs+" and r.gjahr="+a_gjahr+" and r.monat="+a_monat
					+" and r.staff_id in (select p.staff_id from pyramid p where p.position_id=9) and r.period='END'";

			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> finManagerZp(String a_bukrs, int a_gjahr, int a_monat) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

			sel = "select "+
 " pln.bukrs,"+
 " pln.waers,"+
 " emp.staff_id,"+
 " emp.finmanager,"+
 " emp.customer_id,"+
 " pln.branch_id,"+
 " emp.branchName,"+
 " ras_plan,"+
 " ras_poluchen,"+
 " one_month_plan,"+
 " one_month_poluchen,"+
 " country_id"+
  " from ("+
        
        " select r.bukrs,"+
                " r.branch_id,"+
                " r.waers,"+
                " sum(ras_plan) ras_plan,"+
                " sum(ras_poluchen) ras_poluchen,"+
                " sum(r.one_month_plan) one_month_plan,"+
                " sum(r.one_month_poluchen) one_month_poluchen"+
          " from rfcol r"+
         " where r.status = 0"+
           " and r.bukrs = "+a_bukrs+
           " and r.gjahr = "+a_gjahr+
           " and r.monat = "+a_monat+
           " and r.period = 'END'"+
           " and r.waers <> 'USD'"+
         " group by r.bukrs, r.branch_id, r.waers) pln,"+
       " (select s.lastname || ' ' || s.firstname finmanager,"+
               " br.text45,"+
               " br.branch_id,"+
               " s.staff_id,"+
               " s.customer_id,"+
               " br.text45 branchName, "+
               " br.country_id "+
          " from pyramid p, staff s, branch br"+
         " where p.staff_id = s.staff_id"+
           " and p.position_id = 65"+
           " and br.branch_id = p.branch_id) emp"+
 " where pln.branch_id = emp.branch_id";

			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public List<Object[]> dynamicFrep7Result(String a_bukrs,String a_where, String a_lang) throws DAOException
	{	
		try
		{

			String text = "Уволенные";
			if (a_lang.equals("en")) text = "Dismissed";
			else if (a_lang.equals("tr")) text = "Dismissed";
			
			String sel = "";

			sel = "select b1.bukrs,b1.branch_name,b1.waers,"
				    +" sum(b1.balance) balance,"
				    +" sum(b1.deposit) deposit,"
				    +" sum(b1.dolg) dolg,"
				    +" sum(b1.zablok) zablok,"
				    +" sum(b1.avans_zapros) avans_zapros"
				    
				    +" from ("
				    +" select"
				    +" a1.bukrs,"
				    +" a1.staff_id,"
				    +" a1.waers,"
				    +" a1.balance,"
				    +" a1.deposit,"
				    +" a1.dolg,"
				    +" a1.zablok,"
				    +" a1.avans_zapros,"
				    +" NVL2(a2.branch_id,a2.branch_id,0)  branch_id,"
				    +" NVL2(a2.text45,a2.text45,'"+text+"')  branch_name"
				    +" from"
				    +" ("
				    +" select p.bukrs,"
				       +" p.staff_id,"
				       +" p.waers,"
				       +" sum"
				       +" ("
				       +" case"
				            +" when p.approve = 0 and p.drcrk='S' and bldat<=to_date(sysdate,'YYYY-MM-DD')"
				            +" then p.dmbtr"
				            +" when p.approve = 0 and p.drcrk='H' and bldat<=to_date(sysdate,'YYYY-MM-DD')"
				            +" then p.dmbtr*-1"
				            +" else 0"
				          +" end"
				       +" ) balance,"
				       +" sum"
				       +" (  case"
				            +" when p.drcrk = 'H' and p.approve in (3,4)"
				            +" then p.dmbtr"
				            +" else 0"
				          +" end"
				        +" )  avans_zapros,"
				        +" sum"
				        +" ("
				        +" case"
				            +" when p.drcrk = 'S' and p.approve = 1"
				            +" then p.dmbtr"
				            +" else 0"
				          +" end"
				        +" ) deposit,"
				        +" sum"
				        +" ("
				        +" case"
				            +" when p.drcrk = 'S' and p.approve = 8"
				            +" then (p.dmbtr*-1)"
				            +" when p.drcrk = 'H'  and p.approve = 8"
				            +" then p.dmbtr"
				            +" else 0"
				          +" end"
				        +" ) dolg,"
				        +" sum"
				        +" ("
				        +" case"
				            +" when p.drcrk = 'S' and p.approve = 0 and bldat>to_date(sysdate,'YYYY-MM-DD')"
				            +" then p.dmbtr"
							+" when p.drcrk = 'H' and p.approve = 0 and bldat>to_date(sysdate,'YYYY-MM-DD')"
							+" then (p.dmbtr*-1)"
							+" else 0"
				          +" end"
				        +" ) zablok"
				        +" from payroll p"
				        +" where"
				        +" p.bukrs="+a_bukrs
				        +" group by p.bukrs,p.staff_id,p.waers) a1 left join"
				        
				        +" ("
				            +" select a3.*,br2.text45 from branch br2,"
				            +" ("
				            +" select sa.bukrs,sa.staff_id,min(sa.branch_id) branch_id from salary sa, branch br"
				            +" where sa.branch_id=br.branch_id"
				            +" and sa.beg_date<=to_date(sysdate,'YYYY-MM-DD')"
				            +" and sa.end_date>=to_date(sysdate,'YYYY-MM-DD')"
				            +" and sa.bukrs="+a_bukrs+" and br.type=3"
				            +" group by sa.bukrs,sa.staff_id"
				            +" ) a3"
				            +" where a3.branch_id=br2.branch_id"
				        
				        +" ) a2"
				        
				        +" on a1.staff_id=a2.staff_id"
				
				
				
				       +" )b1"
				       +" where "+a_where
				       + " group by rollup(b1.bukrs,b1.waers,b1.branch_name)"
				       +"  order by b1.bukrs,b1.waers,b1.branch_name";

			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}

	public List<Object[]> dynamicFrep7Detail(String a_bukrs,String a_where) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

			sel = "select b1.bukrs,b1.branch_name,b1.waers,"
				    +" b1.balance,"
				    +" b1.deposit,"
				    +" b1.dolg,"
				    +" b1.zablok,"
				    +" b1.avans_zapros,"
				    +" initcap(st.lastname)||' '||initcap(st.firstname)||' '||initcap(st.middlename) fio,"
				    +" b1.staff_id"
				    +" from ("
				    +" select"
				    +" a1.bukrs,"
				    +" a1.staff_id,"
				    +" a1.waers,"
				    +" a1.balance,"
				    +" a1.deposit,"
				    +" a1.dolg,"
				    +" a1.zablok,"
				    +" a1.avans_zapros,"
				    +" NVL2(a2.branch_id,a2.branch_id,0)  branch_id,"
				    +" NVL2(a2.text45,a2.text45,'---Уволенные')  branch_name"
				    +" from"
				    +" ("
				    +" select p.bukrs,"
				       +" p.staff_id,"
				       +" p.waers,"
				       +" sum"
				       +" ("
				       +" case"
				            +" when p.approve = 0 and p.drcrk='S' and bldat<=to_date(sysdate,'YYYY-MM-DD')"
				            +" then p.dmbtr"
				            +" when p.approve = 0 and p.drcrk='H' and bldat<=to_date(sysdate,'YYYY-MM-DD')"
				            +" then p.dmbtr*-1"
				            +" else 0"
				          +" end"
				       +" ) balance,"
				       +" sum"
				       +" (  case"
				            +" when p.drcrk = 'H' and p.approve in (3,4)"
				            +" then p.dmbtr"
				            +" else 0"
				          +" end"
				        +" )  avans_zapros,"
				        +" sum"
				        +" ("
				        +" case"
				            +" when p.drcrk = 'S' and p.approve = 1"
				            +" then p.dmbtr"
				            +" else 0"
				          +" end"
				        +" ) deposit,"
				        +" sum"
				        +" ("
				        +" case"
				            +" when p.drcrk = 'S' and p.approve = 8"
				            +" then (p.dmbtr*-1)"
				            +" when p.drcrk = 'H'  and p.approve = 8"
				            +" then p.dmbtr"
				            +" else 0"
				          +" end"
				        +" ) dolg,"
				        +" sum"
				        +" ("
				        +" case"
				            +" when p.drcrk = 'S' and p.approve = 0 and bldat>to_date(sysdate,'YYYY-MM-DD')"
				            +" then p.dmbtr"
							+" when p.drcrk = 'H' and p.approve = 0 and bldat>to_date(sysdate,'YYYY-MM-DD')"
							+" then (p.dmbtr*-1)"
							+" else 0"
				          +" end"
				        +" ) zablok"
				        +" from payroll p"
				        +" where"
				        +" p.bukrs="+a_bukrs
				        +" group by p.bukrs,p.staff_id,p.waers) a1 left join"
				        
				        +" ("
				            +" select a3.*,br2.text45 from branch br2,"
				            +" ("
				            +" select sa.bukrs,sa.staff_id,min(sa.branch_id) branch_id from salary sa, branch br"
				            +" where sa.branch_id=br.branch_id"
				            +" and sa.beg_date<=to_date(sysdate,'YYYY-MM-DD')"
				            +" and sa.end_date>=to_date(sysdate,'YYYY-MM-DD')"
				            +" and sa.bukrs="+a_bukrs+" and br.type=3"
				            +" group by sa.bukrs,sa.staff_id"
				            +" ) a3"
				            +" where a3.branch_id=br2.branch_id"
				        
				        +" ) a2"
				        
				        +" on a1.staff_id=a2.staff_id"
				
				
				
				       +" )b1, staff st"
				       +" where b1.staff_id=st.staff_id and "+a_where
					   +" order by fio";
			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
     
	public List<Object[]> findMarketingTSStaff(String a_bukrs, java.sql.Date a_firstDay, java.sql.Date a_lastDay) throws DAOException
	{	
		try
		{
			
			
			String sel = "";

			sel = "select "//--s.staff_id ststaffid,s.ts_date,initcap(s.lastname)||' '||initcap(s.middlename)||' '||initcap(s.firstname) stfio,"
					//+" --br.branch_id stbrid,br.text45 stbrtext,sal.bukrs,p.position_id stposid,p.text stpostext,"

					//+" --s.ts_staff_id,tsref.customer_id refcustomerid, tsref.fio reffio,"
					//+" --tsref.bukrs,tsref.position_id refposid,"
					//+" --tsref.postext refposttext,tsref.branch_id refbrid,tsref.brtext refbrtext"

+" tsref.branch_id refbrid,s.ts_staff_id,tsref.customer_id refcustomerid,tsref.position_id refposid, tsref.fio reffio ,con.prodajaSN,con.prodajaBranchId,s.ts_date"
+" from staff s,salary sal,branch br, position p,"

+" (select tsref.staff_id,tsref.customer_id,tsref.iin_bin,initcap(tsref.lastname)||' '||initcap(tsref.middlename)||' '||initcap(tsref.firstname) fio,"
+" tsbr.branch_id,tsbr.text45 brtext,"
+" tssal.bukrs,tssal.amount,tssal.waers,tssal.beg_date,tssal.end_date,"
+" tspos.position_id,tspos.text postext"
+"  from staff tsref, branch tsbr, salary tssal, position tspos"
+" where tsref.staff_id=tssal.staff_id"
+" and tssal.branch_id=tsbr.branch_id"
+" and tssal.position_id=tspos.position_id"
+" and tssal.bukrs='"+a_bukrs+"'"
+" and '"+a_lastDay+"' between tssal.beg_date and tssal.end_date"
//+" and tspos.position_id in (3,4,10,8,67)"
+ ") tsref,"

+" (select c.dealer, c.contract_number prodajaSN, c.branch_id prodajaBranchId from contract c where c.contract_date between '"+a_firstDay+"' and '"+a_lastDay+"' and c.marked_type = 0"
+" and c.bukrs='"+a_bukrs+"' " 
+" ) con"



+" where s.ts_is_active=1"
+" and s.ts_staff_id=tsref.staff_id"
+" and s.staff_id=sal.staff_id"
+" and '"+a_lastDay+"' between sal.beg_date and sal.end_date"
+" and sal.bukrs='"+a_bukrs+"'"
+" and sal.branch_id=br.branch_id"
+" and sal.position_id=p.position_id"
+" and sal.bukrs=tsref.bukrs"
+" and p.position_id in (4,67)"
+" and ((tsref.position_id not in (3,6,10) and p.position_id in (4,67)) or (p.position_id in (4,67) and sal.branch_id<>tsref.branch_id))"
//+" --dealer, demosec dealerdi alyp kele alady nemese basqa qaladan bolsa manager,directorlarda alyp kele alady"
+" and s.staff_id=con.dealer";
			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	/*
	public List<Payroll> findByBukrsBranchAllSchet_oldwages( Date a_today){
    	Query query = this.em
                .createQuery("select p.staff_id, "
                		+ "sum(CASE WHEN (drcrk = 'S') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, " 
                		+ "waers,branch_id,bukrs FROM  Payroll p "
                		+ "where approve in (0) and bldat<=:bldat group by staff_id, waers,branch_id,bukrs order by p.staff_id ASC"); 
    	query.setParameter("bldat", a_today);
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		if (result[3]!=null)
    		{
    			wa_prl.setBranch_id((long) result[3]);
    		}
    		
    		wa_prl.setBukrs(String.valueOf(result[4]));
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }
	public List<Payroll> findByBukrsBranchAllDeposit_oldwages(){
    	Query query = this.em
                .createQuery("select p.staff_id, "
                		+ "sum(CASE WHEN (drcrk = 'S') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, " 
                		+ "waers,branch_id,bukrs FROM  Payroll p "
                		+ "where approve=1 and drcrk='S' group by staff_id, waers,branch_id,bukrs order by p.staff_id ASC"); 
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		if (result[3]!=null)
    		{
    			wa_prl.setBranch_id((long) result[3]);
    		}
    		wa_prl.setBukrs(String.valueOf(result[4]));
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }
	
	
	public List<Payroll> findByBukrsBranchAllZablok_oldwages( Date a_today){
    	Query query = this.em
                .createQuery("select p.staff_id, "
                		+ "sum(CASE WHEN (drcrk = 'S') THEN dmbtr ELSE (dmbtr * -1) END) as dmbtr, " 
                		+ "waers,branch_id,bukrs FROM  Payroll p "
                		+ "where approve=0 and drcrk='S' and bldat>:bldat group by staff_id, waers,branch_id,bukrs order by p.staff_id ASC"); 
    	query.setParameter("bldat", a_today);
    	List<Payroll> l_prl = new ArrayList<Payroll>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Payroll wa_prl = new Payroll();
    		wa_prl.setStaff_id((long) result[0]);
    		wa_prl.setDmbtr((double) result[1]);
    		wa_prl.setWaers(String.valueOf(result[2]));
    		if (result[3]!=null)
    		{
    			wa_prl.setBranch_id((long) result[3]);
    		}
    		wa_prl.setBukrs(String.valueOf(result[4]));
    		l_prl.add(wa_prl);
    	  }
    	return l_prl;
    }*/
	//Query query = this.em
	//		.createQuery("select belnr, gjahr, bktxt, dmbtr, dmbtr_paid, wrbtr, wrbtr_paid, awtyp,customer_id, waers, kursf, blart FROM Bkpf b where customer_id IN (:customer_id) and "+a_dynamicWhere); 
	//query.setParameter("customer_id", al_customer_id);
}
