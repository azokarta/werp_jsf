package general.dao.impl;

import java.sql.Date;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import general.GeneralUtil;
import general.dao.BkpfDao;
import general.dao.DAOException;
import general.output.tables.Fcus01ResultTable;
import general.output.tables.RfcojResultTable;
import general.output.tables.RfdocrowResultTable;
import general.tables.Bkpf; 
import general.tables.BkpfOld;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component("bkpfDao")
public class BkpfDaoImpl extends GenericDaoImpl<Bkpf> implements BkpfDao {

	public List<Bkpf> getByBukrsAndDate(String bukrs, Date fdate, Date tdate) {
		Query query = this.em
				.createQuery("select b FROM Bkpf b where b.bukrs= :bukrs and b.budat between :fdate and :tdate");
		query.setParameter("bukrs", bukrs);
		query.setParameter("fdate", fdate);
		query.setParameter("tdate", tdate);
		List<Bkpf> r_bkpf = query.getResultList();
		return r_bkpf;
	}
	public List<Bkpf> dynamicFindBkpf(String a_dynamicWhere) {
		//System.out.println(a_dynamicWhere);
		Query query = this.em
				.createQuery("select b FROM Bkpf b where "+a_dynamicWhere); 
		List<Bkpf> l_bkpf = query.getResultList();
		return l_bkpf;
	} 
	public int dynamicCountBkpf(String a_dynamicWhere) {
		Long count;
		Query query = this.em
				.createQuery("select count(b.belnr) FROM Bkpf b where "+a_dynamicWhere); 
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
	
	public Bkpf dynamicFindSingleBkpf(String a_dynamicWhere, String a_bukrs) {
		
		a_dynamicWhere = " bukrs="+a_bukrs+" and " + a_dynamicWhere;
		try{ 
			Bkpf p_bkpf = new Bkpf();
			int count = dynamicCountBkpf(a_dynamicWhere);
			if (count==0)
			{
				Query query2 = this.em.createQuery("select b FROM BkpfOld b where "+a_dynamicWhere); 
				BkpfOld p_bkpf_old = (BkpfOld) query2.getSingleResult();
				BeanUtils.copyProperties(p_bkpf_old, p_bkpf);
			}
			else if (count==1)
			{
				Query query = this.em.createQuery("select b FROM Bkpf b where "+a_dynamicWhere); 
				p_bkpf = (Bkpf) query.getSingleResult();
			}
			
			
			return p_bkpf;
		}	
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!
			
			
			return null;
			}
	} 
	
	public Bkpf findStornoSingleBkpf(Long a_belnr, int a_gjahr, String a_bukrs)
	{
		try{ 
			Bkpf wa_bkpf = new Bkpf();
			Query query = this.em.createQuery("select b FROM Bkpf b where stblg = :belnr and stjah = :gjahr and bukrs=:bukrs"); 
			query.setParameter("belnr", a_belnr);
			query.setParameter("gjahr", a_gjahr);
			query.setParameter("bukrs", a_bukrs);
			wa_bkpf = (Bkpf) query.getSingleResult();
			
			return wa_bkpf;
		}	
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!
			return null;
			}
	}
	public Bkpf findOriginalSingleBkpf(Long a_belnr, int a_gjahr, String a_bukrs)
	{
		try{ 
			Bkpf wa_bkpf = new Bkpf();
			Query query = this.em.createQuery("select b FROM Bkpf b where belnr = :belnr and gjahr = :gjahr and bukrs=:bukrs"); 
			query.setParameter("belnr", a_belnr);
			query.setParameter("gjahr", a_gjahr);
			query.setParameter("bukrs", a_bukrs);
			wa_bkpf = (Bkpf) query.getSingleResult();
			
			return wa_bkpf;
		}	
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!
			return null;
			}
	}
	public int updateDynamicSingleBkpf(Long a_belnr, int a_gjahr, String a_dynamicWhere, String a_bukrs)
	{
		a_dynamicWhere = "update Bkpf set "+a_dynamicWhere+" where belnr ="+a_belnr+" and gjahr ="+a_gjahr+" and bukrs="+a_bukrs;
		System.out.println(a_dynamicWhere);
		Query query = this.em.createQuery(a_dynamicWhere);
		return query.executeUpdate();
	}
	 
	public List<Bkpf> findBkpfHrpp(List<Long> al_customer_id, String a_dynamicWhere) {
		List<Bkpf> l_bkpf = new ArrayList<Bkpf>();
		Query query = this.em
				.createQuery("select belnr, gjahr, bktxt, dmbtr, dmbtr_paid, wrbtr, wrbtr_paid, awtyp,customer_id, waers, kursf, blart FROM Bkpf b where customer_id IN (:customer_id) and "+a_dynamicWhere); 
		query.setParameter("customer_id", al_customer_id);
		List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		Bkpf wa_bkpf = new Bkpf();
    		wa_bkpf.setBelnr((long) result[0]);
    		wa_bkpf.setGjahr((int) result[1]);
    		wa_bkpf.setBktxt(String.valueOf(result[2]));
    		wa_bkpf.setDmbtr((double) result[3]);
    		wa_bkpf.setDmbtr_paid((double) result[4]);
    		wa_bkpf.setWrbtr((double) result[5]);
    		wa_bkpf.setWrbtr_paid((double) result[6]);
    		wa_bkpf.setAwtyp((int) result[7]);
    		wa_bkpf.setCustomer_id((long) result[8]);
    		wa_bkpf.setWaers(String.valueOf(result[9]));
    		wa_bkpf.setKursf((double) result[10]);
    		wa_bkpf.setBlart(String.valueOf(result[11]));
    		l_bkpf.add(wa_bkpf);
    	  }
    	 
    	return l_bkpf;
	}
	public String getWaersSingleBkpf(String a_dynamicWhere, String a_bukrs) {
		try{ 
			a_dynamicWhere = "select waers FROM Bkpf b where bukrs="+a_bukrs+" and "+a_dynamicWhere;
			Query query = this.em.createQuery(a_dynamicWhere); 
			String wa_waers = (String) query.getSingleResult();
			return wa_waers;
		}	
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!
			return null;
		}
	}
	
	public List<RfcojResultTable> findResultTableRfcoj(String a_dynamicWhere, String a_waers,int a_selectedService) {
		String sql = "";
		sql = "select b.belnr, b.bukrs, b.gjahr, b.blart, b.contract_number, b.bktxt,b.bldat,b.customer_id"
				+ ", bs.shkzg, bs.dmbtr, bs.wrbtr,b.usnam,b.awkey FROM Bkpf b, Bseg bs where b.bukrs=bs.bukrs and b.belnr = bs.belnr and b.gjahr=bs.gjahr and "+a_dynamicWhere;
		sql = "select a3.*,case when cus.fiz_yur=2 then initcap(cus.lastname) || ' ' || initcap(cus.firstname) else initcap(cus.name) end fio from ("+ sql+") a3 left join customer cus on a3.customer_id=cus.customer_id";
		sql = "select a2.belnr, a2.bukrs, a2.gjahr, a2.blart, a2.contract_number, a2.bktxt,a2.bldat,a2.customer_id, a2.shkzg, a2.dmbtr, a2.wrbtr,a2.usnam,a2.old_sn,se.id,a2.fio from ("
				+ "select a1.belnr, a1.bukrs, a1.gjahr, a1.blart, a1.contract_number, a1.bktxt,a1.bldat,a1.customer_id, a1.shkzg, a1.dmbtr, a1.wrbtr,a1.usnam,c.old_sn"
				+ ",a1.awkey,a1.fio "
				+ "from ("+ sql+") a1 left join contract c on a1.contract_number=c.contract_number order by bldat ) a2 left join service se on a2.awkey=se.awkey and a2.bukrs=se.bukrs";
				
		System.out.println(sql);
		List<RfcojResultTable> rt_list = new ArrayList<RfcojResultTable>();
		Query query = this.em
				.createNativeQuery(sql); 
		
		List<Object[]> results = query.getResultList();
    	for (Object[] result : results) {
    		RfcojResultTable rt = new RfcojResultTable();
    		rt.setBelnr(Long.parseLong(String.valueOf(result[0])));
    		rt.setBukrs(String.valueOf(result[1]));
    		rt.setGjahr(Integer.parseInt(String.valueOf(result[2])));
    		rt.setT_blart(String.valueOf(result[3]));
    		if (result[4]!=null)
    		{
    			rt.setT_contract_number(Long.parseLong( String.valueOf(result[4])));
    		}
    		if (result[5]!=null)
    		{
    			rt.setT_bktxt(String.valueOf(result[5]));
    		}
    		
    		
    		rt.setT_waers(a_waers);
    		rt.setT_date(new SimpleDateFormat("dd.MM.yyyy").format(result[6]));
    		if (result[7]!=null)
    		{
    			rt.setT_customer(Long.parseLong( String.valueOf(result[7])));
    		}
    		
    		String shkzg="";
    		double dmbtr = 0;
    		double wrbtr = 0;
    		shkzg = String.valueOf(result[8]);
    		dmbtr = Double.parseDouble(String.valueOf(result[9]));
    		wrbtr = Double.parseDouble(String.valueOf(result[10]));
						if (shkzg.equals("H")){
							rt.setT_s_h("Расход");
							if (a_waers.equals("USD"))
							{
								rt.setT_summa_rashod(dmbtr);
							}
							else
							{
								rt.setT_summa_rashod(wrbtr);
							}
							//summa_total_rashod = summa_total_rashod + rt.getT_summa_rashod();
						}
						else if (shkzg.equals("S")){
							rt.setT_s_h("Приход");
							if (a_waers.equals("USD"))
							{
								rt.setT_summa_prihod(dmbtr);
							}
							else
							{
								rt.setT_summa_prihod(wrbtr);
							}
							//summa_total_prihod = summa_total_prihod + rt.getT_summa_prihod();
						}
						
					
				
				if (rt.getT_summa_prihod()>0 || rt.getT_summa_rashod()>0)
				{
					if (result[11]!=null)
		    		{
		    			rt.setUsnam(Long.parseLong( String.valueOf(String.valueOf(result[11]))));
		    		}
					if (result[12]!=null) rt.setOld_sn(Long.parseLong( String.valueOf(result[12])));
					Long ser=0L;
					if (result[13]!=null) ser = Long.parseLong( String.valueOf(result[13]));
					if (result[14]!=null)
		    		{
		    			rt.setCusFio(String.valueOf(result[14]));
		    		}
					
					if (a_selectedService==2)
					{
						rt_list.add(rt);
					}
					else if (a_selectedService==1 && ser!=null && ser>0L)
					{
						rt_list.add(rt);
					}
					else if (a_selectedService==0 && (ser==0L || ser==null))
					{
						rt_list.add(rt);
					}
					//if (rt.getOld_sn().equals(0L)) rt.setOld_sn(null);
					//UserDao userDao = (UserDao) appContext.getContext().getBean("userDao");
					//if (wa_bkpf.getUsnam()!=null && wa_bkpf.getUsnam()>0)
					//{
					//	rt.setUserFio(userDao.getUserFio(wa_bkpf.getUsnam()));
					//}
					
				}
				
			} 
    	  
    	 
    	return rt_list;
	}
	
	public List<RfdocrowResultTable> findResultTableRfdocrow(String a_dynamicWhere,boolean customer) {

		String sqlCus = "";
		String sqlNoCus = "";
		sqlNoCus = "select b.bukrs, " //0
				+ " b.gjahr, " //1
				+ " b.belnr, " //2
				+ " b.budat, " //3
				+ " b.bldat, " //4
				+ " b.waers, " //5
				+ " b.kursf, " //6
				+ " b.bktxt, " //7
				+ " b.contract_number, " //8
				+ " bs.dmbtr," //9
				+ " bs.wrbtr," //10
				+ " bs.shkzg," //11
				+ " bl.text45," //12
				+ " s.text45" //13
				+" FROM Bkpf b, Bseg bs, Hkont s, Blart bl where b.bukrs = s.bukrs and b.belnr = bs.belnr and b.gjahr=bs.gjahr and bl.blart=b.blart and bs.hkont = s.hkont "+a_dynamicWhere;
		
		sqlCus = "select b.bukrs, " //0
				+ " b.gjahr, " //1
				+ " b.belnr, " //2
				+ " b.budat, " //3
				+ " b.bldat, " //4
				+ " b.waers, " //5
				+ " b.kursf, " //6
				+ " b.bktxt, " //7
				+ " b.contract_number, " //8
				+ " bs.dmbtr," //9
				+ " bs.wrbtr," //10
				+ " bs.shkzg," //11
				+ " bl.text45," //12
				+ " s.text45," //13
				+ " c.customer_id," //14
				+ " c.firstname," //15
				+ " c.lastname," //16
				+ " c.middlename," //17
				+ " c.name," //18
				+ " c.fiz_yur" //19
				+" FROM Bkpf b, Bseg bs, Hkont s, Blart bl,Customer c where b.bukrs = s.bukrs and b.belnr = bs.belnr and b.gjahr=bs.gjahr and bl.blart=b.blart and bs.hkont = s.hkont and b.customer_id = c.customer_id "+a_dynamicWhere;
		List<RfdocrowResultTable> rt_list = new ArrayList<RfdocrowResultTable>();
		
		
		//System.out.println(sqlCus);
		
    	//return rt_list;
    	Query queryCus = this.em.createQuery(sqlCus);
		List<Object[]> resultsCus = queryCus.getResultList();
    	for (Object[] result : resultsCus) {
    		RfdocrowResultTable rt = new RfdocrowResultTable();
    		
    		rt.setBukrs(String.valueOf(result[0]));
    		rt.setGjahr((int) result[1]);
    		rt.setBelnr((long) result[2]);
    		rt.setBudat(new SimpleDateFormat("dd.MM.yyyy").format(result[3]));
    		rt.setBudat(new SimpleDateFormat("dd.MM.yyyy").format(result[4]));
    		rt.setWaers(String.valueOf(result[5]));
    		rt.setKursf((double) result[6]);
    		if (result[7]!=null) rt.setBktxt(String.valueOf(result[7]));
    		if (result[8]!=null) rt.setContract_number((long) result[8]);    		
    		rt.setDmbtr((double) result[9]);
    		rt.setWrbtr((double) result[10]);
    		rt.setShkzg(String.valueOf(result[11]));
    		rt.setBlart(String.valueOf(result[12]));
    		rt.setHkont(String.valueOf(result[13]));
    		if (result[14]!=null) rt.setCustomer_id((long) result[14]);
    		if (result[15]!=null) rt.setFirstname(String.valueOf(result[15]));
    		if (result[16]!=null) rt.setLastname(String.valueOf(result[16]));
    		if (result[17]!=null) rt.setMiddlename(String.valueOf(result[17]));
    		if (result[18]!=null) rt.setName(String.valueOf(result[18]));
    		rt.setFiz_yur((int) result[19]);
    		rt.setBudatYYYY(new SimpleDateFormat("yyyyMMdd").format(result[3]));
    		rt_list.add(rt);
    	}
		
    	if (!customer)
		{
			Query queryNoCus = this.em.createQuery(sqlNoCus);
			List<Object[]> resultsNoCus = queryNoCus.getResultList();
	    	for (Object[] result : resultsNoCus) {
	    		RfdocrowResultTable rt = new RfdocrowResultTable();
	    		
	    		rt.setBukrs(String.valueOf(result[0]));
	    		rt.setGjahr((int) result[1]);
	    		rt.setBelnr((long) result[2]);
	    		rt.setBudat(new SimpleDateFormat("dd.MM.yyyy").format(result[3]));
	    		rt.setBudat(new SimpleDateFormat("dd.MM.yyyy").format(result[4]));
	    		rt.setWaers(String.valueOf(result[5]));
	    		rt.setKursf((double) result[6]);
	    		if (result[7]!=null) rt.setBktxt(String.valueOf(result[7]));
	    		if (result[8]!=null)rt.setContract_number((long) result[8]);    		
	    		rt.setDmbtr((double) result[9]);
	    		rt.setWrbtr((double) result[10]);
	    		rt.setShkzg(String.valueOf(result[11]));
	    		rt.setBlart(String.valueOf(result[12]));
	    		rt.setHkont(String.valueOf(result[13]));
	    		rt.setBudatYYYY(new SimpleDateFormat("yyyyMMdd").format(result[3]));
	    		rt_list.add(rt);
	    	}
		}
    	 
    	return rt_list;
	}
	public void findResultTableFcus01(Map<String,List<Fcus01ResultTable>> l_client_map
			,Map<String,List<Fcus01ResultTable>> l_supplier_map
			,Map<String,List<Fcus01ResultTable>> l_podotchet_map
			,Map<String,List<Fcus01ResultTable>> l_salary_map
			,Map<String,String> l_currency_map,String a_dynamicWhere,accounting.other.Fcus01.SearchTable p_searchTable)
	{
		String sqlCus = "";
		sqlCus = "select b.bukrs, " //0
				+ " b.gjahr, " //1
				+ " b.belnr, " //2
				+ " b.bldat, " //3
				+ " b.waers, " //4
				+ " b.kursf, " //5
				+ " b.bktxt, " //6
				+ " b.contract_number, " //7
				+ " bs.shkzg," //8
				+ " bs.hkont," //9
				+ " bs.dmbtr," //10
				+ " bs.wrbtr," //11				
				+ " bl.text45," //12
				+ " s.text45," //13
				+ " b.brnch," //14
				+ " br.text45" //15
				+" FROM Bkpf b, Bseg bs, Hkont s, Blart bl,Branch br "
				+ "where b.bukrs = s.bukrs "
				+ "and b.belnr = bs.belnr "
				+ "and b.gjahr=bs.gjahr "
				+ "and b.brnch=br.branch_id "
				+ "and bl.blart=b.blart "
				+ "and bs.hkont = s.hkont " +a_dynamicWhere +" order by b.bldat,br.text45,b.waers";
		//List<Fcus01ResultTable> rt_list = new ArrayList<Fcus01ResultTable>();
		System.out.println(sqlCus);
		Query queryCus = this.em.createQuery(sqlCus);
		List<Object[]> resultsCus = queryCus.getResultList();
    	for (Object[] result : resultsCus) {
    		Fcus01ResultTable rt = new Fcus01ResultTable();
    		
    		rt.setBukrs(String.valueOf(result[0]));
    		rt.setGjahr((int) result[1]);
    		rt.setBelnr((long) result[2]);
    		rt.setBldat(new SimpleDateFormat("dd.MM.yyyy").format(result[3]));
    		rt.setBldatYYYYMMDD(new SimpleDateFormat("YYYYMMdd").format(result[3]));
    		rt.setWaers(String.valueOf(result[4]));
    		rt.setKursf((double) result[5]);
    		if (result[6]!=null) rt.setBktxt(String.valueOf(result[6]));
    		if (result[7]!=null) rt.setContract_number((long) result[7]);    		
    		rt.setShkzg(String.valueOf(result[8]));
    		rt.setHkont(String.valueOf(result[9]));
    		if(rt.getShkzg().equals("S"))
    		{
    			rt.setDmbtr_s(GeneralUtil.round((double) result[10], 2));
    			rt.setWrbtr_s(GeneralUtil.round((double) result[11], 2));
    			if (rt.getWaers().equals("USD")) rt.setWrbtr_s(rt.getDmbtr_s());
    		}
    		else{
    			rt.setDmbtr_h(GeneralUtil.round((double) result[10], 2));
    			rt.setWrbtr_h(GeneralUtil.round((double) result[11], 2));
    			if (rt.getWaers().equals("USD")) rt.setWrbtr_h(rt.getDmbtr_h());
    		}	
    		rt.setBlart(String.valueOf(result[12]));
    		rt.setHkont_text(String.valueOf(result[13]));
    		rt.setBranch_id((long) result[14]);
    		rt.setBranchName(String.valueOf(result[15]));
    		Long hkont_long = Long.valueOf(rt.getHkont());
    		String key = "";
    		key = rt.getWaers();
    		l_currency_map.put(rt.getWaers(), rt.getWaers());
    		if (hkont_long>=12100001 && hkont_long<=12199999)
    		{
    			List<Fcus01ResultTable> l_rt = l_client_map.get(key);
    			if(l_rt==null || l_rt.size()==0)
    			{
    				l_rt = new ArrayList<Fcus01ResultTable>();
    				l_rt.add(rt);
    				l_client_map.put(key, l_rt);
    			}
    			else l_rt.add(rt);
    			
    			p_searchTable.setTotal_client_dmbtr_h(p_searchTable.getTotal_client_dmbtr_h()+rt.getDmbtr_h());
    			p_searchTable.setTotal_client_dmbtr_s(p_searchTable.getTotal_client_dmbtr_s()+rt.getDmbtr_s());
    			p_searchTable.setTotal_client_wrbtr_h(p_searchTable.getTotal_client_wrbtr_h()+rt.getWrbtr_h());
    			p_searchTable.setTotal_client_wrbtr_s(p_searchTable.getTotal_client_wrbtr_s()+rt.getWrbtr_s());
    		}
    		else if (hkont_long>=31100001 && hkont_long<=33105000)
    		{
    			List<Fcus01ResultTable> l_rt = l_client_map.get(key);
    			if(l_rt==null || l_rt.size()==0)
    			{
    				l_rt = new ArrayList<Fcus01ResultTable>();
    				l_rt.add(rt);
    				l_client_map.put(key, l_rt);
    			}
    			else l_rt.add(rt);
    			
    			p_searchTable.setTotal_client_dmbtr_h(p_searchTable.getTotal_client_dmbtr_h()+rt.getDmbtr_h());
    			p_searchTable.setTotal_client_dmbtr_s(p_searchTable.getTotal_client_dmbtr_s()+rt.getDmbtr_s());
    			p_searchTable.setTotal_client_wrbtr_h(p_searchTable.getTotal_client_wrbtr_h()+rt.getWrbtr_h());
    			p_searchTable.setTotal_client_wrbtr_s(p_searchTable.getTotal_client_wrbtr_s()+rt.getWrbtr_s());
    		}
    		else if (hkont_long>=33900001 && hkont_long<=33900130)
    		{
    			List<Fcus01ResultTable> l_rt = l_client_map.get(key);
    			if(l_rt==null || l_rt.size()==0)
    			{
    				l_rt = new ArrayList<Fcus01ResultTable>();
    				l_rt.add(rt);
    				l_client_map.put(key, l_rt);
    			}
    			else l_rt.add(rt);
    			
    			p_searchTable.setTotal_client_dmbtr_h(p_searchTable.getTotal_client_dmbtr_h()+rt.getDmbtr_h());
    			p_searchTable.setTotal_client_dmbtr_s(p_searchTable.getTotal_client_dmbtr_s()+rt.getDmbtr_s());
    			p_searchTable.setTotal_client_wrbtr_h(p_searchTable.getTotal_client_wrbtr_h()+rt.getWrbtr_h());
    			p_searchTable.setTotal_client_wrbtr_s(p_searchTable.getTotal_client_wrbtr_s()+rt.getWrbtr_s());
    		}
    		else if (hkont_long>=33500001 && hkont_long<=33500130)
    		{
    			List<Fcus01ResultTable> l_rt = l_salary_map.get(key);
    			if(l_rt==null || l_rt.size()==0)
    			{
    				l_rt = new ArrayList<Fcus01ResultTable>();
    				l_rt.add(rt);
    				l_salary_map.put(key, l_rt);
    			}
    			else l_rt.add(rt);
    			
    			p_searchTable.setTotal_salary_dmbtr_h(p_searchTable.getTotal_salary_dmbtr_h()+rt.getDmbtr_h());
    			p_searchTable.setTotal_salary_dmbtr_s(p_searchTable.getTotal_salary_dmbtr_s()+rt.getDmbtr_s());
    			p_searchTable.setTotal_salary_wrbtr_h(p_searchTable.getTotal_salary_wrbtr_h()+rt.getWrbtr_h());
    			p_searchTable.setTotal_salary_wrbtr_s(p_searchTable.getTotal_salary_wrbtr_s()+rt.getWrbtr_s());
    		}
    		else if (hkont_long==12500001)
    		{
    			List<Fcus01ResultTable> l_rt = l_podotchet_map.get(key);
    			if(l_rt==null || l_rt.size()==0)
    			{
    				l_rt = new ArrayList<Fcus01ResultTable>();
    				l_rt.add(rt);
    				l_podotchet_map.put(key, l_rt);
    			}
    			else l_rt.add(rt);
    			
    			p_searchTable.setTotal_podotchet_dmbtr_h(p_searchTable.getTotal_podotchet_dmbtr_h()+rt.getDmbtr_h());
    			p_searchTable.setTotal_podotchet_dmbtr_s(p_searchTable.getTotal_podotchet_dmbtr_s()+rt.getDmbtr_s());
    			p_searchTable.setTotal_podotchet_wrbtr_h(p_searchTable.getTotal_podotchet_wrbtr_h()+rt.getWrbtr_h());
    			p_searchTable.setTotal_podotchet_wrbtr_s(p_searchTable.getTotal_podotchet_wrbtr_s()+rt.getWrbtr_s());
    		}
    		
    		
    		
    		//rt_list.add(rt);
    	}

    	
	}
	
	public Long getNextValueBkpf(String a_blartType) {
		String sql = "SELECT SEQ_BKPF_BLART_"+a_blartType.toUpperCase()+".nextval FROM dual";


		try{
			Query query = this.em.createNativeQuery(sql);

			BigDecimal belnr = (BigDecimal) query.getSingleResult();

			return belnr.longValue();
		}
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!


			return null;
		}


	}

	
}