package general.dao.impl;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.*;

import javax.persistence.Query;




















import org.springframework.stereotype.Component;

import dms.contract.ContractDetails;
import general.GeneralUtil;
import general.Validation;
import general.dao.ContractDao;
import general.dao.DAOException;
import general.output.tables.FacmassinOutputTable;
import general.output.tables.RfcolResultTable;
import general.tables.Address;
import general.tables.Contract; 
import general.tables.ContractType;
import general.tables.Customer;
import general.tables.Staff;
import general.tables.report.SalesReportOutput;
@Component("contractDao")
public class ContractDaoImpl extends GenericDaoImpl<Contract> implements ContractDao{
	public Long countContractByNumber(Long a_contract_number){
        Query query = this.em
                .createQuery("select COUNT(c.contract_id) FROM Contract c where c.contract_number= :contract_number");
        query.setParameter("contract_number", a_contract_number);        
        Long count = (Long) query.getSingleResult();        
        return count;
    } 
	
	public Contract findByContractNumber(Long a_contract_number){
    	Query query = this.em
                .createQuery("select c FROM Contract c where c.contract_number= :contract_number");
        query.setParameter("contract_number", a_contract_number);   
        Contract con = (Contract) query.getSingleResult();
    	return con;
    }
	
	public Contract findByTovarSN(String tovarSN) {
		Query query = this.em
                .createQuery("select c FROM Contract c where c.tovar_serial = '"
                		+ tovarSN + "' ORDER BY c.contract_date DESC");
        /*
		Query query = this.em
                .createQuery("select c.contract_number, c.branch_id, c.contract_date, "
                		+ "c.dealer, c.customer_id, c.serv_branch_id, c.addr_dom, "
                		+ "c.tel_dom, c.tel_mob1, c.tel_mob2, c.tel_rab, c.addr_dom_regid "
                		+ "FROM Contract c where c.tovar_serial = '"
                		+ tovarSN + "' ORDER BY c.contract_date DESC");
                		*/
        //query.setFirstResult(1);
        //query.setMaxResults(1);
        List<Contract> results = query.getResultList();
        if (results.size() > 0)
           return results.get(0);
        else 
        	return null;
        
    	//List<Contract> result = query.getResultList();
        /*if (result.size() > 0) {
        	return result.get(0);	
        }
        return null;
        */
	}
	
	public List<Contract> dynamicFindContracts(String a_dynamicWhere){ 
    	
		//System.out.println(a_dynamicWhere);
		
    	Query query = this.em
                .createQuery("select c FROM Contract c where "+a_dynamicWhere); 
    	//query.setMaxResults(20);  
    	List<Contract> con =  query.getResultList();
    	return con;
    }
	
	public Contract getByContractId(Long a_contract_id) {
		Query query = this.em
                .createQuery("select c FROM Contract c where c.contract_id= :contract_id");
        query.setParameter("contract_id", a_contract_id);   
        Contract conbyid = (Contract) query.getSingleResult();
    	return conbyid;
	}
	@Override
	public int updateDynamicSingleCon(Long a_con_num, String a_dynamicWhere)
	{
		String update="update Contract set "+a_dynamicWhere+" where contract_number = "+a_con_num;
		System.out.println(update);
		Query query = this.em.createQuery(update);
		return query.executeUpdate();
	}
	
	@Override
	public int updateDynamicSingleConPaid(Long a_con_num, double a_dmbtr)
	{
		String update="update Contract set paid = paid + "+a_dmbtr+" where contract_number = "+a_con_num;
		System.out.println(update);
		Query query = this.em.createQuery(update);
		return query.executeUpdate();
	}
	
	/*public List<Contract> findCollectorAmount(String a_bukrs, Date a_begDate,Date a_endDate){ 
    	
		//System.out.println(a_dynamicWhere);
		
    	Query query = this.em
                .createQuery("SELECT  c.collector,sum(p.sum / b.kursf) as price, c.bukrs, c.addr_dom_countryid FROM Contract as c, PaymentSchedule as p, Bkpf as b "
                		+ "where c.awkey = p.awkey "
                		+ "and b.contract_number = c.contract_number "
                		+ "and c.bukrs = :bukrs and b.blart = 'GC'  "
                		+ "and c.contract_status_id IN (1,6) and c.collector is not null "
                		+ "and payment_date < :endDate group by c.collector, c.bukrs, c.addr_dom_countryid"); 
    	//query.setMaxResults(20);  
    	
    	
    	
    	query.setParameter("bukrs", a_bukrs); 
    	//query.setParameter("begDate", a_begDate); 
    	query.setParameter("endDate", a_endDate); 
    	List<Contract> l_con = new ArrayList<Contract>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Contract wa_con = new Contract();    		
    		wa_con.setCollector((long) result[0]);
    		wa_con.setPrice((double) result[1]);
    		wa_con.setBukrs((String) result[2]);
    		wa_con.setAddr_dom_countryid((long) result[3]);
    		l_con.add(wa_con);
    	  }
    	return l_con;
    }
	
	public Map<Long,Contract> findCollectedAmountClosedContracts(String a_bukrs, Date a_begDate, Date a_endDate)
	{
		Query query = this.em
                .createQuery("select c.collector, sum(b.dmbtr) as price, c.bukrs, c.addr_dom_countryid  from Contract as c, Bkpf as b "
                		+ "where  b.blart='CP' and  c.contract_status_id = 5 and c.collector is not null "
                		+ "and b.contract_number = c.contract_number  and c.bukrs = :bukrs  "
                		+ "and budat between :begDate and :endDate group by c.collector, c.bukrs, c.addr_dom_countryid"); 
		query.setParameter("bukrs", a_bukrs); 
    	query.setParameter("begDate", a_begDate); 
    	query.setParameter("endDate", a_endDate); 
    	Map<Long,Contract> l_con_map = new HashMap<Long,Contract>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Contract wa_con = new Contract();    		
    		wa_con.setCollector((long) result[0]);
    		wa_con.setPrice((double) result[1]);
    		wa_con.setBukrs((String) result[2]);
    		wa_con.setAddr_dom_countryid((long) result[3]);
    		l_con_map.put(wa_con.getCollector(), wa_con);
    	  }
    	return l_con_map;
	}*/
	
	public Map<Long,Contract> findCollectedAmount(String a_bukrs, Date a_begDate,Date a_endDate){ 
    	
		//System.out.println(a_dynamicWhere);
		
    	Query query = this.em
                .createQuery("SELECT c.collector,sum(b.dmbtr) as paid, c.bukrs FROM Contract as c, Bkpf as b  "
                		+ "where b.contract_number = c.contract_number "
                		+ "and c.bukrs = :bukrs and b.blart = 'CP' and b.storno = 0 and budat between :begDate and :endDate "
                		+ "and c.contract_status_id NOT IN (2,3,4) and c.collector is not null "
                		+ "and ((close_date is null) or  (contract_status_id=5 and close_date between :begDate and :endDate)) group by c.collector, c.bukrs"); 
    	//query.setMaxResults(20);  
    	
    	
    	
    	query.setParameter("bukrs", a_bukrs); 
    	query.setParameter("begDate", a_begDate); 
    	query.setParameter("endDate", a_endDate); 
    	Map<Long,Contract> l_con_map = new HashMap<Long,Contract>();
    	List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Contract wa_con = new Contract();
    		wa_con.setCollector((long) result[0]);
    		wa_con.setPaid((double) result[1]);
    		wa_con.setBukrs((String) result[2]);
    		l_con_map.put(wa_con.getCollector(), wa_con);
    	  }
    	return l_con_map;
    }
	
	@Override
	public List<ContractDetails> findAll(String s, int first, int max) {
		
		Query q = this.em.createNativeQuery(s, Contract.class);
		q.setFirstResult(first);
		q.setMaxResults(max);
		List<Contract> l = q.getResultList();
		List<ContractDetails> t = new ArrayList<ContractDetails>();
		
		Staff wa_staff = new Staff();
		Customer wa_customer = new Customer();
		ContractType ct = new ContractType();
		
		int k = 0;
		for (Contract wa_contract : l) {
			k++;
			if (wa_contract != null) {
				ContractDetails wa_OutputTableClass = new ContractDetails(k, wa_contract);
				
				if (wa_contract.getCustomer_id() != null 
						&& wa_contract.getCustomer_id() > 0) {
					s = "SELECT c FROM Customer c ";
					s += " WHERE c.customer_id = " + wa_contract.getCustomer_id();
					q = this.em.createQuery(s);
					List<Customer> wa_cust = q.getResultList();
					if (wa_cust.size() > 0) {
						wa_customer = wa_cust.get(0);
					}
					if (wa_customer != null && wa_customer.getId() != null) {
						wa_OutputTableClass.setCustomer(wa_customer);
					} else {
						wa_OutputTableClass.setCustomer(new Customer());
					}
				}
				
				if (!Validation.isEmptyLong(wa_contract.getAddrHomeId())) {
					s = "SELECT a FROM Address a ";
					s += " WHERE a.addr_id = " + wa_contract.getAddrHomeId();
					
					q = this.em.createQuery(s);
					List<Address> aL = q.getResultList();
					if (aL.size() > 0) { 
						Address wa_addr = aL.get(0);

						boolean tt = false;
						String tel = "";
						
						if (!Validation.isEmptyString(wa_addr.getTelMob1())) {
							tel = wa_addr.getTelMob1();
							tt = true;
						}
						if (!Validation.isEmptyString(wa_addr.getTelMob2())) {
							if (tt) tel += ", ";
							tel += wa_addr.getTelMob2();
							tt = true;
						}
							
						if (!Validation.isEmptyString(wa_addr.getTelDom())) {
							if (tt) tel += ", "; 
							tel += wa_addr.getTelDom();
						}
						
						wa_OutputTableClass.setTel(tel);
						wa_OutputTableClass.setAddress(wa_addr.getAddress());
					}
					
				}
				
				// *****************************************************************************************************
				if (wa_contract.getDealer() != null && wa_contract.getDealer() > 0) {
					s = "SELECT c FROM Staff c ";
					s += " WHERE c.staff_id = " + wa_contract.getDealer();
					q = this.em.createQuery(s);
					List<Staff> wa_st = q.getResultList();
					if (wa_st.size() > 0) {
						wa_staff = wa_st.get(0);
					}
					if (wa_staff != null && wa_staff.getStaff_id() != null) {
						wa_OutputTableClass.setDealer(wa_staff);
					} else {
						wa_OutputTableClass.setDealer(new Staff());
					}
				}
					
				// *************************************************************************************************
				wa_staff = null;
				if (wa_contract.getCollector() != null && wa_contract.getCollector() > 0) {
					s = "SELECT c FROM Staff c ";
					s += " WHERE c.staff_id = " + wa_contract.getCollector();
					q = this.em.createQuery(s);
					List<Staff> wa_st = q.getResultList();
					if (wa_st.size() > 0) {
						wa_staff = wa_st.get(0);
					}
					if (wa_staff != null && wa_staff.getStaff_id() != null) {
						wa_OutputTableClass.setCollector(wa_staff);
					} else {
						wa_OutputTableClass.setCollector(new Staff());
					}
				}
				
				ct = null;
				if (wa_contract.getContract_type_id() != null && wa_contract.getContract_type_id() > 0) {
					s = "SELECT c FROM ContractType c ";
					s += " WHERE c.contract_type_id = " + wa_contract.getContract_type_id();
					q = this.em.createQuery(s);
					List<ContractType> wa_ct = q.getResultList();
					if (wa_ct.size() > 0) {
						ct = wa_ct.get(0);
					}
					if (ct != null && ct.getContract_type_id() != null) {
						wa_OutputTableClass.setContrType(ct);
					} else {
						wa_OutputTableClass.setContrType(new ContractType());
					}
				}
				
				wa_OutputTableClass.setPrice(wa_contract.getPrice());
				wa_OutputTableClass.setPaid(wa_contract.getPaid());
				wa_OutputTableClass.setRemain(wa_OutputTableClass.getPrice()
						- wa_contract.getPaid());
				t.add(wa_OutputTableClass);
			}
			
		}		
		
		return t;
	}
	
	@Override
	public int getRowCount(String s) {
		//System.out.println("Query: " + s);
		String s2 = "SELECT c.* FROM";
		String s3 = "SELECT count(c.contract_id) FROM";
		s = s.replaceFirst(s2, s3);
		//System.out.println("Query: " + s);		
		//System.out.println("ConLength: " + condition.length());
		Query query = this.em.createNativeQuery(s);
		List<Object[]> resL = query.getResultList();
		if (resL.size() > 0) {
			return Integer.valueOf(String.valueOf(resL.get(0)));
		}
		return 0;
	}
	
	public int getRowCountJPQL(String s) {
		System.out.println("Query: " + s);
		String s2 = "SELECT c.* FROM";
		String s3 = "SELECT count(c.contract_id) FROM";
		s = s.replaceFirst(s2, s3);
		System.out.println("Query: " + s);		
		//System.out.println("ConLength: " + condition.length());
		Query query = this.em.createNativeQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}
	
	
	public List<FacmassinOutputTable> dynamicFindFacmassin(String a_dynamicWhere, String a_date) throws DAOException
	{	
		try
		{
			List<FacmassinOutputTable> outputTable = new ArrayList<FacmassinOutputTable>();	
			int count = 0;
			Long cur_awkey =0L;
			Long last_awkey = 0L;
			boolean skp = false;
			if (a_dynamicWhere!=null)
			{
				String s = "select c.contract_number n0," //0
	            		+ " c.contract_date n1," //1
	            		+ " c.price n2,"  //2
	            		+ " c.customer_id n3," //3
	            		+ " cus.fiz_yur n4," //4
	            		+ " cus.lastname  n5," //5
	            		+ " cus.firstname   n6," //6
	            		+ " cus.middlename   n7," //7
	            		+ " c.lastname n8," //8
	            		+ " c.firstname n9," //9
	            		+ " c.middlename n10," //10
	            		+ " cst.name n11," //11
	            		+ " ct.name n12," //12
	            		+ " m.text45 n13," //13
	            		+ " m.matnr n14," //14
	            		+ " c.awkey n15," //15
	            		+ " p.payment_schedule_id n16," //16
	            		+ " p.payment_date n17," //17
	            		+ " p.paid n18," //18
	            		+ " p.sum2 n19," //19
	            		+ " cus.name n20," //20
	            		+ " b.waers n21," //21
	            		+ " c.old_sn n22," //22
	            		+ " c.collector n23," //23
	            		+ " p.is_firstpayment n24" //24
	            			+" from (select c.*,s.lastname,s.firstname,s.middlename from Contract  c LEFT JOIN Staff  s ON c.collector = s.staff_id where "+ a_dynamicWhere +")  c, "
	            			+ "Payment_schedule  p , Bkpf  b, Contract_type  ct, Contract_status  cst,Matnr  m, Customer  cus "
	            			+" where ct.contract_type_id=c.contract_type_id and cst.contract_status_id=c.contract_status_id "
	            			+ "and m.matnr=ct.matnr and c.customer_id=cus.customer_id  "
	            			+" and p.awkey=c.awkey and p.bukrs = c.bukrs and b.contract_number=c.contract_number"
	            			+" and b.blart='GC' and b.storno=0  "+ a_date +" order by p.awkey, p.payment_schedule_id";
		
				
				System.out.println(s);
			   	Query query = this.em .createNativeQuery(s);
			   	
		    	//query.setMaxResults(20);  
		    	
		    	List<Object[]> results = query.getResultList();
		    	
		     	//int index = 0;
		     	//List<Object[]> results = query.getResultList();
		    	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		     	for (Object[] result : results) {
		    		
					FacmassinOutputTable wa_fot = new FacmassinOutputTable();
					//wa_fot.setIndex(index);
					
					if (result[0]!=null) wa_fot.setContract_number(Long.parseLong(String.valueOf(result[0])));
					if (result[1]!=null) wa_fot.setContract_date(formatter.parse(String.valueOf(result[1])));
					if (result[2]!=null) wa_fot.setPrice(Double.parseDouble(String.valueOf(result[2]))); 
					if (result[3]!=null) wa_fot.setCustomer_id(Long.parseLong(String.valueOf(result[3])));
					if (result[4]!=null && Integer.parseInt(String.valueOf(result[4]))==1 && result[20]!=null)  wa_fot.setClientFio(String.valueOf(result[20]));
					else if (result[4]!=null)
					{
						wa_fot.setClientFio(Validation.returnFio(String.valueOf(result[6]), String.valueOf(result[5]), String.valueOf(result[7])));
					}
					if (result[23]!=null)
					{
						String fname = "",lname = "",mname = "";
						if (result[9]!=null) fname = String.valueOf(result[9]);
						if (result[8]!=null) lname = String.valueOf(result[8]);
						if (result[10]!=null) mname = String.valueOf(result[10]);
						wa_fot.setCollectorFio(Validation.returnFio(fname, lname, mname));
					}
					
					
					if (result[11]!=null) wa_fot.setStatus(String.valueOf(result[11]));
					if (result[12]!=null) wa_fot.setType(String.valueOf(result[12]));
					if (result[14]!=null) wa_fot.setMatnr(Long.parseLong(String.valueOf(result[14])));
					if (result[15]!=null) wa_fot.setAwkey(Long.parseLong(String.valueOf(result[15])));
					if (result[16]!=null) wa_fot.setPayment_schedule_id(Long.parseLong(String.valueOf(result[16])));
					if (result[17]!=null) wa_fot.setPayment_date(formatter.parse(String.valueOf(result[17])));
					if (result[18]!=null) wa_fot.setPaid(Double.parseDouble(String.valueOf(result[18])));
					if (result[19]!=null) wa_fot.setPayment_due(Double.parseDouble(String.valueOf(result[19])) - wa_fot.getPaid());
					if (result[21]!=null) wa_fot.setWaers(String.valueOf(result[21]));
					if (result[22]!=null) wa_fot.setOld_sn(Long.parseLong(String.valueOf(result[22])));
					//index++;
					
					cur_awkey = wa_fot.getAwkey();
					if (!cur_awkey.equals(last_awkey))
					{
						count = 0;
						skp = false;
					}
					if (Integer.parseInt(String.valueOf(result[24]))==1)
					{
						wa_fot.setFirst_payment(true);
					}
					else
					{
						wa_fot.setFirst_payment(false);
					}
					//if (count == 0)
					//{
					//	wa_fot.setFirst_payment(true);
					//}
//					else
//					{
//						wa_fot.setFirst_payment(false);
//					}
					count++;
					last_awkey = cur_awkey;
					
					if (wa_fot.getPayment_due()>0)
					{	
						if (!skp)
						{
							outputTable.add(wa_fot);
						}
						
						if (wa_fot.isFirst_payment())
						{
							skp = true;
						}
					}
					
					
	
				}
	     	
			}
			/*
	     	count = 0;
			cur_awkey =0L;
			last_awkey = 0L;
			skp = false;
			if (a_dynamicWhere2!=null)
			{
				String s2 = "select c.contract_number," //0
	            		+ " c.contract_date," //1
	            		+ " c.price,"  //2
	            		+ " c.customer_id," //3
	            		+ " cus.fiz_yur," //4
	            		+ " cus.lastname," //5
	            		+ " cus.firstname," //6
	            		+ " cus.middlename," //7
	            		+ " cst.name," //8
	            		+ " ct.name," //9
	            		+ " m.text45," //10
	            		+ " m.matnr," //11
	            		+ " c.awkey," //12
	            		+ " p.payment_schedule_id," //13
	            		+ " p.payment_date," //14
	            		+ " p.paid," //15
	            		+ " p.sum," //16
	            		+ " cus.name," //17            		
	            		+ " b.waers," //18
	            		+ " c.old_sn" //19
	            			+" from Contract c, PaymentSchedule p , Bkpf b, ContractType ct, ContractStatus cst,Matnr m, Customer cus "
	            			+" where c.contract_status_id in (1,4,6) and ct.contract_type_id=c.contract_type_id and cst.contract_status_id=c.contract_status_id "
	            			+ "and m.matnr=ct.matnr and c.customer_id=cus.customer_id "
	            			+" and p.awkey=c.awkey and b.contract_number=c.contract_number and c.collector is null"
	            			+" and b.blart='GC' and b.storno=0 and "+ a_dynamicWhere2 +" order by p.awkey, p.payment_date";
				Query query2 = this.em .createQuery(s2);
		    	List<Object[]> results2 = query2.getResultList();
		    	for (Object[] result : results2) {
		    		
					FacmassinOutputTable wa_fot = new FacmassinOutputTable();
					//wa_fot.setIndex(index);
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					if (result[0]!=null) wa_fot.setContract_number((long) result[0]);
					if (result[1]!=null) wa_fot.setContract_date(formatter.parse(String.valueOf(result[1])));
					if (result[2]!=null) wa_fot.setPrice((double) result[2]); 
					if (result[3]!=null) wa_fot.setCustomer_id((long) result[3]);
					if (result[4]!=null && (int) result[4]==1 && result[17]!=null)  wa_fot.setClientFio(String.valueOf(result[17]));
					else if (result[4]!=null)
					{
						wa_fot.setClientFio(Validation.returnFio(String.valueOf(result[6]), String.valueOf(result[5]), String.valueOf(result[7])));
					}
					if (result[8]!=null) wa_fot.setStatus(String.valueOf(result[8]));
					if (result[9]!=null) wa_fot.setType(String.valueOf(result[9]));
					if (result[11]!=null) wa_fot.setMatnr((long) result[11]);
					if (result[12]!=null) wa_fot.setAwkey((long) result[12]);
					if (result[13]!=null) wa_fot.setPayment_schedule_id((long) result[13]);
					if (result[14]!=null) wa_fot.setPayment_date(formatter.parse(String.valueOf(result[14])));
					if (result[15]!=null) wa_fot.setPaid((double) result[15]);
					if (result[16]!=null) wa_fot.setPayment_due((double) result[16] - wa_fot.getPaid());
					if (result[18]!=null) wa_fot.setWaers(String.valueOf(result[18]));
					if (result[19]!=null) wa_fot.setOld_sn((long) result[19]);
					
					cur_awkey = wa_fot.getAwkey();
					if (!cur_awkey.equals(last_awkey))
					{
						count = 0;
						skp = false;
					}
					if (count == 0)
					{
						wa_fot.setFirst_payment(true);
					}
					else
					{
						wa_fot.setFirst_payment(false);
					}
					count++;
					last_awkey = cur_awkey;
					
					if (wa_fot.getPayment_due()>0)
					{	
						if (!skp)
						{
							outputTable.add(wa_fot);
						}
						
						if (wa_fot.isFirst_payment())
						{
							skp = true;
						}
					}
					
					

				}	 
			}
			*/
	    	
	    	
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public boolean checkPyramid(String inBukrs, java.util.Date inDate) throws DAOException {
		try
		{
			boolean res = false;
			java.util.Date fd = GeneralUtil.getFirstDateOfMonth(inDate);
			java.util.Date ld = GeneralUtil.getLastDateOfMonth(inDate);
			
			String sq = "select * from PYRAMID_ARCHIVE p where "
					+ " p.month = " + (fd.getMonth()+1) + " and  p.year = " + (fd.getYear()+1900)
					+ " and bukrs = '" + inBukrs + "'";
			
			Query query = this.em .createNativeQuery(sq);
			List<Object[]> results = query.getResultList();
		    System.out.println("Results: " + results.size());
		    	
		    if (results.size() > 0) res = true;
	    	return res;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}	
	
	public List<SalesReportOutput> getSalesReportBranch(String queryScript, String table) throws DAOException {
		try
		{
			List<SalesReportOutput> outputTable = new ArrayList<SalesReportOutput>();
			
			if (!Validation.isEmptyString(queryScript))
			{
				//System.out.println(queryScript);
			   	Query query = this.em .createNativeQuery(queryScript);
			   	//System.out.println("Table: " + table);
			   	//query.setParameter("table", table);
			   	
		    	List<Object[]> results = query.getResultList();
		    	//System.out.println("Results: " + results.size());
		    	
		    	int i = 0;
		     	for (Object[] result : results) {
		    		i++;
		     		SalesReportOutput wa_sr = new SalesReportOutput(i);
					//wa_fot.setIndex(index);
		     		
		     		if (result[0]!=null) wa_sr.getBranch().setBranch_id(Long.parseLong(String.valueOf(result[0])));
		     		if (result[1]!=null) wa_sr.getBranch().setText45(String.valueOf(result[1]));
		     		if (result[2]!=null) wa_sr.getDirector().setFirstname(String.valueOf(result[2]));
		     		if (result[3]!=null) wa_sr.setNalichka(Double.parseDouble(String.valueOf(result[3])));
					if (result[4]!=null) wa_sr.setRassrochka(Double.parseDouble(String.valueOf(result[4])));
		     		if (result[5]!=null) wa_sr.setTotal(Double.parseDouble(String.valueOf(result[5])));
		     		if (result[6]!=null) wa_sr.setCancelled(Double.parseDouble(String.valueOf(result[6])));
					
		     		outputTable.add(wa_sr);
				}
			}
			
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public List<SalesReportOutput> getSalesReportGroup(String queryScript, String table) throws DAOException {
		try
		{
			List<SalesReportOutput> outputTable = new ArrayList<SalesReportOutput>();
			
			if (!Validation.isEmptyString(queryScript))
			{
				//System.out.println("Group query: ");
				//System.out.println(queryScript);
			   	Query query = this.em .createNativeQuery(queryScript);
			   	//query.setParameter("table", table);
			   	
		    	List<Object[]> results = query.getResultList();
		    	
		    	int i = 0;
		     	for (Object[] result : results) {
		    		i++;
		     		SalesReportOutput wa_sr = new SalesReportOutput(i);
					//wa_fot.setIndex(index);
		     		
		     		if (result[0]!=null) wa_sr.getBranch().setBranch_id(Long.parseLong(String.valueOf(result[0])));
		     		if (result[1]!=null) wa_sr.getBranch().setText45(String.valueOf(result[1]));
		     		if (result[2]!=null) wa_sr.setPyramidId(Long.parseLong(String.valueOf(result[2])));
		     		if (result[3]!=null) wa_sr.getManager().setFirstname(String.valueOf(result[3]));
		     		if (result[4]!=null) wa_sr.setNalichka(Double.parseDouble(String.valueOf(result[4])));
					if (result[5]!=null) wa_sr.setRassrochka(Double.parseDouble(String.valueOf(result[5])));
		     		if (result[6]!=null) wa_sr.setTotal(Double.parseDouble(String.valueOf(result[6])));
		     		if (result[7]!=null) wa_sr.setCancelled(Double.parseDouble(String.valueOf(result[7])));
					
		     		outputTable.add(wa_sr);
				}
			}
			
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public List<SalesReportOutput> getSalesReportStaff(String queryScript, String table) throws DAOException {
		try
		{
			List<SalesReportOutput> outputTable = new ArrayList<SalesReportOutput>();
			
			if (!Validation.isEmptyString(queryScript))
			{
				//System.out.println("Staff query: ");
				//System.out.println(queryScript);
			   	Query query = this.em .createNativeQuery(queryScript);
			   	//query.setParameter("table", table);
			   	
		    	List<Object[]> results = query.getResultList();
		    	
		    	int i = 0;
		     	for (Object[] result : results) {
		    		i++;
		     		SalesReportOutput wa_sr = new SalesReportOutput(i);
					//wa_fot.setIndex(index);
		     		
		     		if (result[0]!=null) wa_sr.getBranch().setBranch_id(Long.parseLong(String.valueOf(result[0])));
		     		if (result[1]!=null) wa_sr.getBranch().setText45(String.valueOf(result[1]));
		     		if (result[2]!=null) wa_sr.getManager().setFirstname(String.valueOf(result[2]));
		     		if (result[3]!=null) wa_sr.getDealer().setFirstname(String.valueOf(result[3]));
		     		if (result[4]!=null) wa_sr.setNalichka(Double.parseDouble(String.valueOf(result[4])));
					if (result[5]!=null) wa_sr.setRassrochka(Double.parseDouble(String.valueOf(result[5])));
		     		if (result[6]!=null) wa_sr.setTotal(Double.parseDouble(String.valueOf(result[6])));
		     		if (result[7]!=null) wa_sr.setCancelled(Double.parseDouble(String.valueOf(result[7])));
					
		     		outputTable.add(wa_sr);
				}
			}
			
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
}
