package general.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import general.dao.DAOException;
import general.dao.RfcolDao;
import general.output.tables.RfcolResultTable;
import general.tables.Blart;
import general.tables.Rfcol;

import org.springframework.stereotype.Component;

@Component("rfcolDao")
public class RfcolDaoImpl  extends GenericDaoImpl<Rfcol> implements RfcolDao{
	public List<RfcolResultTable> dynamicFindCollectorStat(String a_dynamicWhere, java.sql.Date a_budat_from, java.sql.Date a_budat_to,int a_group_by, int a_status) throws DAOException
	{	
		try
		{
			String a_status_string;
			if (a_status==0)
			{
				a_status_string = "(1,5,6,10,11,12,13)";
//				--1  Стандарт
//				--5  ВЫПОЛНЕН
//				--6  Переоформлен
//				--10  Загород
//				--11  Юротдел
//				--12  Временный Арест
//				--13  Временный Возврат
			}
			else
			{
				a_status_string = "(8,9,4,14,15)";
//				--8  СУД
//				--9  ДО СУДА
//				--4  Проблемный - REAL
//				--14  Проблемный - 6 МЕСЯЦЕВ
//				--15	Проблемный - ДИЛЕР
			}
			
			
			
			List<RfcolResultTable> outputTable = new ArrayList<RfcolResultTable>();	
			String sel = "";
			if (a_group_by==1)
			{
				sel = sel  +" select text45, ' ' as collector_name, sum(kol), waers, 0 collector, branch_id,bukrs,sum(ras_plan) ras_plan, sum(ras_poluchen) ras_poluchen, sum(one_month_plan) one_month_plan"
						+ ", sum(one_month_poluchen) one_month_poluchen from (";
			}
			
			sel = sel + "select * from ("+
					"select br.text45, (case con3.collector when null then '' else (select INITCAP(firstname) ||' '|| INITCAP(lastname) ||' '|| INITCAP(middlename) from staff st where st.staff_id=con3.collector )end) as collector_name"+

						",count(con3.contract_id) as kol,con3.waers,con3.collector,con3.branch_id,con3.bukrs "+
					   	",sum(CASE con3.payment_schedule WHEN 1 THEN 0 ELSE (con3.sum2-con3.paid)+collectedamount END) as ras_plan "+
						",sum(CASE con3.payment_schedule WHEN 1 THEN 0 ELSE collectedamount END) as ras_poluchen "+
						",sum(CASE con3.payment_schedule WHEN 1 THEN (con3.sum2-con3.paid)+collectedamount  ELSE 0 END) as one_month_plan "+
						",sum(CASE con3.payment_schedule WHEN 1 THEN collectedamount  ELSE 0 END) as one_month_poluchen "+
						
						" from ( "+
						
							" select conz1.*,"+
							" sum(case conz1.waers when 'USD'  then  nvl(b.dmbtr,0) else nvl(b.wrbtr,0) end)  collectedamount"+
							" from ("+
										" select con.contract_id,con.contract_number,con.bukrs,con.waers,con.collector,con.payment_schedule,con.contract_status_id,con.cancel_date"+
										" ,con.fin_branch_id branch_id"+
										" ,sum(case when is_firstpayment = 1 then 0 else ps.sum2 end ) sum2"+
									    " ,sum(case when is_firstpayment = 1 then 0 else ps.paid end ) paid"+										
										" from contract con, payment_schedule ps"+
										" where ps.awkey=con.awkey and ps.bukrs = con.bukrs and ps.payment_date<='"+a_budat_to+"'"+
										" and con.contract_date<='"+a_budat_to+"'"+
										" and con.payment_schedule<>0"+
						                " and con.contract_status_id not in (2,3,7) and con.contract_status_id in "+a_status_string+
						                " group by con.contract_id,con.contract_number,con.fin_branch_id,con.bukrs,con.waers,con.collector,con.price,con.paid,"+
						                " con.payment_schedule,con.contract_status_id,con.cancel_date"+
						                " ,con.branch_id"+
							") conz1 left join bkpf b "+
							"on conz1.contract_number=b.contract_number  and b.blart='CP' and b.bldat >'2016-10-11' and b.bldat between '"+a_budat_from+"' and '"+a_budat_to+"'"+
							" group by conz1.contract_id,conz1.contract_number,conz1.branch_id,conz1.bukrs,conz1.waers,conz1.collector,conz1.payment_schedule,"+
							" conz1.contract_status_id,conz1.cancel_date,sum2,paid"+
					") con3, branch br where br.branch_id = con3.branch_id and sum2+paid+collectedamount>0 "+
						a_dynamicWhere 
						+" group by br.text45, con3.waers,con3.collector,con3.branch_id,con3.bukrs) con4 order by con4.text45,con4.waers, con4.collector_name";
			if (a_group_by==1)
			{
				sel = sel  +" ) group by text45, waers,branch_id,bukrs order by text45,waers";
			}
			System.out.println(sel);			
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			for (Object[] result : results) {
	     		RfcolResultTable wa_rt = new RfcolResultTable();
				//wa_fot.setIndex(index);
				if (result[0]!=null) wa_rt.setBranch_name(String.valueOf(result[0]));
				if (result[1]!=null) wa_rt.setCollector_name(String.valueOf(result[1]));
				//System.out.println();
				wa_rt.setContract_amount(Integer.parseInt(String.valueOf(result[2])));
				if (result[3]!=null) wa_rt.setWaers(String.valueOf(result[3]));
				if (result[4]!=null) wa_rt.setStaff_id(Long.parseLong(String.valueOf(result[4])));
				if (result[5]!=null) wa_rt.setBranch_id(Long.parseLong(String.valueOf(result[5])));
				if (result[6]!=null) wa_rt.setBukrs(String.valueOf(result[6]));
				
				if (result[7]!=null) wa_rt.setRas_plan(Double.parseDouble(String.valueOf(result[7]))); 
				if (result[8]!=null) wa_rt.setRas_poluchen(Double.parseDouble(String.valueOf(result[8]))); 
				if (result[9]!=null) wa_rt.setOne_month_plan(Double.parseDouble(String.valueOf(result[9]))); 
				if (result[10]!=null) wa_rt.setOne_month_poluchen(Double.parseDouble(String.valueOf(result[10]))); 
				
				outputTable.add(wa_rt);
				

			}
	    	
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> dynamicFindCollectorDetail(String a_dynamicWhere, java.sql.Date a_budat_from, java.sql.Date a_budat_to, int a_status) throws DAOException
	{	
		try
		{
			String a_status_string;
			if (a_status==0)
			{
				a_status_string = "(1,5,6,10,11,12,13)";
//				--1  Стандарт
//				--5  ВЫПОЛНЕН
//				--6  Переоформлен
//				--10  Загород
//				--11  Юротдел
//				--12  Временный Арест
//				--13  Временный Возврат
			}
			else
			{
				a_status_string = "(8,9,4,14,15)";
//				--8  СУД
//				--9  ДО СУДА
//				--4  Проблемный - REAL
//				--14  Проблемный - 6 МЕСЯЦЕВ
//				--15	Проблемный - ДИЛЕР
			}
			
			String sel = "";

			sel = "select "
				+" con3.waers,"
				+" CASE con3.payment_schedule WHEN 1 THEN 0 ELSE (con3.sum2-con3.paid)+collectedamount END as ras_plan ,"
				+" CASE con3.payment_schedule WHEN 1 THEN 0 ELSE collectedamount END as ras_poluchen ,"
				+" CASE con3.payment_schedule WHEN 1 THEN (con3.sum2-con3.paid)+collectedamount ELSE 0 END as one_month_plan ,"
				+" CASE con3.payment_schedule WHEN 1 THEN collectedamount  ELSE 0 END as one_month_poluchen,"
				+" contract_number,"
				+" contract_date,"
				+" c.lastname||' '|| c.middlename ||' '|| c.firstname as fio,"
				+" c.name   "
				+" from (  "
				
				                +" select conz1.*, "
				                +" sum(case conz1.waers when 'USD'  then  nvl(b.dmbtr,0) else nvl(b.wrbtr,0) end)  collectedamount "
				                +" from ("
						                +" select con.contract_date,con.customer_id,"
						                +" con.contract_id,con.contract_number,con.bukrs,con.waers,con.collector,con.payment_schedule,con.contract_status_id,con.cancel_date ,con.fin_branch_id branch_id ,"
						                +" sum(case when is_firstpayment = 1 then 0 else ps.sum2 end ) sum2,"
									    +" sum(case when is_firstpayment = 1 then 0 else ps.paid end ) paid"	
										+" from contract con,payment_schedule ps"
						                + " where ps.awkey=con.awkey  and ps.bukrs = con.bukrs "
						                +" and ps.payment_date<='"+a_budat_to+"' and con.contract_date<='"+a_budat_to+"'"
						                +" and con.payment_schedule<>0 "
						                +" and con.contract_status_id not in (2,3,7) and con.contract_status_id in "+a_status_string
						                +" group by con.contract_id,con.contract_number,con.fin_branch_id,con.bukrs,con.waers,con.collector,con.price,con.paid, con.payment_schedule,"
						                +" con.contract_status_id,con.cancel_date ,con.branch_id,con.contract_date,con.customer_id"					              
				                +" ) conz1 left join bkpf b on conz1.contract_number=b.contract_number  "
				                +" and b.blart='CP' and b.bldat >'2016-10-11' "
				                +" and b.bldat between '"+a_budat_from+"' and '"+a_budat_to+"'"
				                +" group by conz1.contract_id,conz1.contract_number,conz1.branch_id,conz1.bukrs,conz1.waers,conz1.collector,conz1.payment_schedule, "
				                +" conz1.contract_status_id,conz1.cancel_date,sum2,paid"
				                +" ,conz1.contract_date,conz1.customer_id"
				
				+" ) con3, branch br,customer c "
				+" where br.branch_id = con3.branch_id and c.customer_id=con3.customer_id  and sum2+paid+collectedamount>0 "+
						a_dynamicWhere;
			
						
			Query query = this.em.createNativeQuery(sel);
			
			
			List<Object[]> results = query.getResultList();
			
			return results;
//			for (Object[] result : results) {
//	     		RfcolResultTable wa_rt = new RfcolResultTable();
//				//wa_fot.setIndex(index);
//				if (result[0]!=null) wa_rt.setBranch_name(String.valueOf(result[0]));
//				if (result[1]!=null) wa_rt.setCollector_name(String.valueOf(result[1]));
//				//System.out.println();
//				wa_rt.setContract_amount(Integer.parseInt(String.valueOf(result[2])));
//				if (result[3]!=null) wa_rt.setWaers(String.valueOf(result[3]));
//				if (result[4]!=null) wa_rt.setStaff_id(Long.parseLong(String.valueOf(result[4])));
//				if (result[5]!=null) wa_rt.setBranch_id(Long.parseLong(String.valueOf(result[5])));
//				if (result[6]!=null) wa_rt.setBukrs(String.valueOf(result[6]));
//				
//				if (result[7]!=null) wa_rt.setRas_plan(Double.parseDouble(String.valueOf(result[7]))); 
//				if (result[8]!=null) wa_rt.setRas_poluchen(Double.parseDouble(String.valueOf(result[8]))); 
//				if (result[9]!=null) wa_rt.setOne_month_plan(Double.parseDouble(String.valueOf(result[9]))); 
//				if (result[10]!=null) wa_rt.setOne_month_poluchen(Double.parseDouble(String.valueOf(result[10]))); 
//				
//				outputTable.add(wa_rt);
//				
//
//			}
//	    	
//	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public int countSaved(String a_bukrs, int a_gjahr, int a_monat) throws DAOException
	{
		try
		{
			Long count;
			String select = "select count(rfcol_id) from Rfcol where bukrs ="+a_bukrs+" and gjahr="+a_gjahr+ " and monat="+a_monat;
			Query query = this.em.createQuery(select);
			count = (Long) query.getSingleResult();
			if (count == null || count == 0)
			{
				return 0;
			}
			else
			{
				return (int) (long) count;
			}
			
	        
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public List<Rfcol> findBy(String a_bukrs, int a_gjahr, int a_monat, String a_dyn, int a_group_by, int a_status) throws DAOException
	{
		try
		{
			List<Rfcol> l_rfcol = new ArrayList<Rfcol>();
			String select = "";
			if (a_group_by==1)
			{
				select = " select branch_name,  sum(contract_amount) as contract_amount , waers, branch_id,bukrs,sum(ras_plan) as ras_plan, sum(ras_poluchen) as ras_poluchen, sum(one_month_plan) as one_month_plan"
						+ ", sum(one_month_poluchen) as one_month_poluchen from Rfcol br where bukrs ="+a_bukrs+" and gjahr="+a_gjahr+ " and status="+a_status+" and monat="+a_monat+a_dyn+
						" group by branch_name, waers,branch_id,bukrs order by branch_name,waers";
				Query query = this.em.createQuery(select);
				List<Object[]> results = query.getResultList();
				for (Object[] result : results) {
					Rfcol wa_rt = new Rfcol();
					//wa_fot.setIndex(index);
					if (result[0]!=null) wa_rt.setBranch_name(String.valueOf(result[0]));
					wa_rt.setContract_amount(Integer.parseInt(String.valueOf(result[1])));
					if (result[2]!=null) wa_rt.setWaers(String.valueOf(result[2]));
					if (result[3]!=null) wa_rt.setBranch_id(Long.parseLong(String.valueOf(result[3])));
					if (result[4]!=null) wa_rt.setBukrs(String.valueOf(result[4]));
					
					if (result[5]!=null) wa_rt.setRas_plan(Double.parseDouble(String.valueOf(result[5]))); 
					if (result[6]!=null) wa_rt.setRas_poluchen(Double.parseDouble(String.valueOf(result[6]))); 
					if (result[7]!=null) wa_rt.setOne_month_plan(Double.parseDouble(String.valueOf(result[7]))); 
					if (result[8]!=null) wa_rt.setOne_month_poluchen(Double.parseDouble(String.valueOf(result[8]))); 
					l_rfcol.add(wa_rt);
					

				}
			}
			else
			{
				select =  "select br from Rfcol br where bukrs ="+a_bukrs+" and gjahr="+a_gjahr+ " and status="+a_status+"and monat="+a_monat+a_dyn;
				Query query = this.em.createQuery(select);
				l_rfcol =  query.getResultList();
				
			}
			return l_rfcol;
			
			
	        
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public List<Object[]> dynamicFrep3Result(String a_dynamicWhere, String a_lang) throws DAOException
	{	
		try
		{

			String hkont_column = "text45";
			if (a_lang.equals("en")) hkont_column = "name_en";
			else if (a_lang.equals("tr")) hkont_column = "name_tr";
			
			String sel = "";

			sel = "select a2.waers,a2.brnch,a2.hkont,a2.dmbtr,a2.wrbtr,a2.brname,s."+hkont_column+" as hname from"
				  +" (      select a1.*,br.text45 brname,br.bukrs"
				          +" from "
				              +" (select a3.*,rownum rsort from (select b.waers,b.brnch,bsr.hkont,sum(bsr.dmbtr) dmbtr,sum(bsr.wrbtr) wrbtr"
				              +" FROM Bkpf b,Bseg bsr"
				              +" where b.belnr = bsr.belnr and b.gjahr=bsr.gjahr "
				              +" and (b.blart in ('SP','KP') or (b.blart='AE' and b.awtyp=0 and b.tcode='FKAAEC'))"
				              + a_dynamicWhere
				              +" and bsr.hkont not like '1010%' and bsr.hkont not like '1030%' and bsr.shkzg='S'  and b.storno=0"
				              +" GROUP BY ROLLUP(b.waers,b.brnch,bsr.hkont)"
				              +" order by b.waers,b.brnch,bsr.hkont) a3"
				          +" )a1 left join Branch br"
				          +" on a1.brnch = br.branch_id"
				   +" )a2 left join Skat s"
				   +" on a2.hkont = s.hkont and a2.bukrs=s.bukrs order by rsort";
			
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public List<Object[]> dynamicFrep3Detail(String a_dynamicWhere, String a_lang) throws DAOException
	{	
		try
		{

			String hkont_column = "text45";
			if (a_lang.equals("en")) hkont_column = "name_en";
			else if (a_lang.equals("tr")) hkont_column = "name_tr";
			
			String sel = "";

			sel = "select a1.*, case when c.fiz_yur=1 then c.name when fiz_yur =2 then initcap(c.lastname)||' '||initcap(c.firstname) end "
			+ "from (select b.waers,bsr.hkont,bsr.dmbtr,bsr.wrbtr,br.text45 as bname,s."+hkont_column+" as hname,b.bukrs,b.belnr,b.gjahr,b.customer_id "
		  +" FROM Bkpf b,Bseg bsr,Branch br,Skat s"
		  +" where b.belnr = bsr.belnr and b.gjahr=bsr.gjahr "
		  +" and (b.blart in ('SP','KP') or (b.blart='AE' and b.awtyp=0 and b.tcode='FKAAEC'))"
		  + a_dynamicWhere
		  +" and bsr.hkont not like '1010%' and bsr.hkont not like '1030%' and bsr.shkzg='S'  and b.storno=0 "
		  + "and bsr.hkont = s.hkont and bsr.bukrs=s.bukrs and b.brnch = br.branch_id) a1 left join customer c on a1.customer_id = c.customer_id";
			
//			sel = "select b.waers,bsr.hkont,bsr.dmbtr,bsr.wrbtr,br.text45 as bname,s.text45 as hname,b.bukrs,b.belnr,b.gjahr,b.customer_id "
//				  +" FROM Bkpf b,Bseg bsr,Branch br,Skat s"
//				  +" where b.belnr = bsr.belnr and b.gjahr=bsr.gjahr "
//				  +" and (b.blart in ('SP','KP') or (b.blart='AE' and b.awtyp=0 and b.tcode='FKAAEC'))"
//				  + a_dynamicWhere
//				  +" and bsr.hkont not like '1010%' and bsr.hkont not like '1030%' and bsr.shkzg='S'  and b.storno=0 "
//				  + "and bsr.hkont = s.hkont and bsr.bukrs=s.bukrs and b.brnch = br.branch_id";
			
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> dynamicFrep4Result(String a_dynamicWhere, String a_lang) throws DAOException
	{	
		try
		{

			String hkont_column = "text45";
			if (a_lang.equals("en")) hkont_column = "name_en";
			else if (a_lang.equals("tr")) hkont_column = "name_tr";
			
			String sel = "";

			sel = "select a2.waers,a2.brnch,a2.hkont,a2.dmbtr,a2.wrbtr,a2.brname,s."+hkont_column+" as hname from"
				  +" (      select a1.*,br.text45 brname,br.bukrs"
				          +" from "
				              +" (select a3.*,rownum rsort from (select b.waers,b.brnch,bsr.hkont,sum(bsr.dmbtr) dmbtr,sum(bsr.wrbtr) wrbtr"
				              +" FROM Bkpf b,Bseg bsr"
				              +" where b.belnr = bsr.belnr and b.gjahr=bsr.gjahr and b.bukrs=bsr.bukrs"				              
				              + a_dynamicWhere
				              +" and (bsr.hkont like '1010%' or bsr.hkont not like '1030%') and bsr.shkzg='S'  and b.storno=0"
				              +" GROUP BY ROLLUP(b.waers,b.brnch,bsr.hkont)"
				              +" order by b.waers,b.brnch,bsr.hkont) a3"
				          +" )a1 left join Branch br"
				          +" on a1.brnch = br.branch_id"
				   +" )a2 left join Skat s"
				   +" on a2.hkont = s.hkont and a2.bukrs=s.bukrs order by rsort";
			
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public List<Object[]> dynamicFrep4Detail(String a_dynamicWhere, String a_lang) throws DAOException
	{	
		try
		{
			

			String hkont_column = "text45";
			if (a_lang.equals("en")) hkont_column = "name_en";
			else if (a_lang.equals("tr")) hkont_column = "name_tr";
			
			String sel = "";

			sel = "select a1.*,c.old_sn from (select b.waers,bsr.hkont,bsr.dmbtr,bsr.wrbtr,br.text45 as bname,s."+hkont_column+" as hname,b.bukrs,b.belnr,b.gjahr,b.contract_number,b.bldat"
				  +" FROM Bkpf b,Bseg bsr,Branch br,Skat s"
				  +" where b.belnr = bsr.belnr and b.gjahr=bsr.gjahr and bsr.dmbtr>0"
				  + a_dynamicWhere
				  +" and (bsr.hkont like '1010%' or bsr.hkont not like '1030%') and bsr.shkzg='S'  and b.storno=0 "
				  +" and bsr.hkont = s.hkont and bsr.bukrs=s.bukrs and b.brnch = br.branch_id) a1 left join Contract c on a1.contract_number=c.contract_number order by bldat";
			
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> dynamicFrep5Result(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			
			String sel = "";
			sel = "select br.text45,a2.* from "
					+" ("
					+" select a1.* "
					+" ,rownum rsort from ("
					+" select c.branch_id,"
					+" c.waers,"
					+" sum(CASE WHEN c.payment_schedule < 2 THEN 1 ELSE 0 END) nal_kol,"
					+" sum(CASE WHEN c.payment_schedule < 2 THEN dmbtr ELSE 0 END) nal_dmbtr,"
					+" sum(CASE WHEN c.payment_schedule < 2 THEN wrbtr ELSE 0 END) nal_wrbtr,"
					+" sum(CASE WHEN c.payment_schedule > 1 THEN 1 ELSE 0 END) ras_kol,"
					+" sum(CASE WHEN c.payment_schedule > 1 THEN dmbtr ELSE 0 END) ras_dmbtr,"
					+" sum(CASE WHEN c.payment_schedule > 1 THEN wrbtr ELSE 0 END) ras_wrbtr,"
					+" sum(1) tot_kolm,"
					+" sum(dmbtr) tot_dmbtr,"
					+" sum(wrbtr) tot_wrbtr"
					+" from bkpf b, contract c "
					+" where c.contract_number=b.contract_number and b.blart='GC' and b.storno=0  and c.contract_status_id not in (2,3)"
					+ a_dynamicWhere
					+" group by ROLLUP (c.waers,c.branch_id)"
					+" order by c.waers,c.branch_id) a1"
					+" order by rsort) a2  left join branch br on a2.branch_id=br.branch_id"
					+" order by rsort";

			
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> dynamicFrep5Detail(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			 		
			String sel = "";

			sel = "select b.belnr,"
						+" b.gjahr,"
						+" b.bukrs,"
						+" b.waers,"
						+" b.dmbtr,"
						+" b.wrbtr,"
						+" br.text45,"
						+" c.old_sn,"
						+" c.contract_date,"
						+" c.contract_number"
					+" from bkpf b, contract c,branch br "
					+" where c.contract_number=b.contract_number and b.blart='GC' and b.storno=0 and br.branch_id=c.branch_id  and c.contract_status_id not in (2,3)"
					+ a_dynamicWhere;
					//+" and b.bukrs=1000 and c.contract_date between '2016-11-01' and '2016-11-25' and c.payment_schedule < 2"
				
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> dynamicFrep8Result(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			
			String sel = "";
			sel = "select cp.waers,"
				+" case cp.waers when 'USD' then cp.dmbtr else cp.wrbtr end summa,br.text45,c.contract_number,c.old_sn,"
				+" case when cus.fiz_yur=2 then initcap(cus.lastname) ||' '|| initcap(cus.firstname) ||' '|| initcap(cus.middlename)"
				+" else cus.name end       fio,"
				+" cp.budat,c.price-c.paid remain, cp.belnr,cp.gjahr,cp.blart,c.bukrs,c.customer_id"
				+" from bkpf gc, bkpf cp, contract c,customer cus,branch br"
				+" where gc.contract_number=c.contract_number"
				+" and cp.contract_number=c.contract_number"
				+" and gc.storno=0 and cp.storno=0"
				+" and gc.blart = 'GC' and cp.blart in ('CF','CP')"
				+" and gc.official = 1 and c.customer_id=cus.customer_id"
				+ a_dynamicWhere
				+" and br.branch_id=c.branch_id order by budat,text45,fio";

			
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> dynamicFoeaResult(String a_dynamicWhere, String a_lang) throws DAOException
	{	
		try
		{
			String hkont_column = "text45";
			if (a_lang.equals("en")) hkont_column = "name_en";
			else if (a_lang.equals("tr")) hkont_column = "name_tr";
			
			String sel = "";
			sel = "select a1.*,"
			    +" case when cus.fiz_yur=2"
			    +" then initcap(cus.lastname) || ' ' || initcap(cus.firstname) || ' ' || initcap(cus.middlename) "
			    +" when cus.fiz_yur=1"
			    +" then initcap(cus.name) else '' end fioclient"
			    +" ,cus.customer_id"
			    +" from (SELECT br.text45 branchname,pre.blart,to_char(pre.bldat,'DD.MM.YYYY') bldat,pre.waers,pre.summa"
			    +" ,d."+hkont_column+" hkont_d,k."+hkont_column+"||' '||k.waers hkont_k,u.username,pre.status,pre.bktxt,pre.awkey,pre.customer_id,pre.prebkpf_id,pre.belnr,pre.gjahr,to_char(pre.created_date,'DD.MM.YYYY') created_date"
			    +" FROM PREBKPF pre, skat d, skat k, user_table u,branch br"
			    +" where pre.bukrs=d.bukrs and pre.hkont_d=d.hkont"
			    +" and pre.bukrs=k.bukrs and pre.hkont_k=k.hkont"
			    +" and pre.usnam=u.user_id"
			    +" and pre.brnch=br.branch_id"
			    + a_dynamicWhere
			    + ") a1 left join customer cus"
			    +" on a1.customer_id=cus.customer_id order by prebkpf_id";
			
			
			

			
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> dynamicFoeaDetail(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			 		
			String sel = "";

			sel = "select a1.prebkpf_id,a1.waers,a1.dmbtr,a1.wrbtr,a1.shkzg,a1.menge,a1.hkont,a1.werks,m.text45 matnr from ("
					+" select pbf.prebkpf_id"
					+" ,pbf.waers"
					+" ,pbs.dmbtr"
					+" ,pbs.wrbtr"
					+" ,case when pbs.shkzg='S' then 'Дебет' else 'Кредит' end shkzg"
					+" ,pbs.menge"
					+" ,s.text45 hkont"
					+" ,(select w.text45 from werks_type w where w.werks=pbs.werks) werks"
					+" ,pbs.matnr"
					+" from prebseg pbs,prebkpf pbf,skat s where "
					+" pbf.prebkpf_id=pbs.prebkpf_id "
					+" and pbf.bukrs=s.bukrs and pbs.hkont=s.hkont"
					+" and "+ a_dynamicWhere
					+ ") a1 left join matnr m on a1.matnr=m.matnr";
				
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	
}
