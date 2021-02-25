package general.dao.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.GeneralUtil;
import general.Validation;
import general.dao.BsegDao;
import general.dao.DAOException;
import general.output.tables.Frep1OutputTable;
import general.output.tables.Frep2OutputTable;
import general.output.tables.Podotchet;
import general.output.tables.RfcolResultTable;
import general.tables.Bseg;

@Component("bsegDao")
public class BsegDaoImpl extends GenericDaoImpl<Bseg> implements BsegDao {

	public List<Bseg> getByBukrs(String bukrs) {
		Query query = this.em
				.createQuery("select b FROM Bseg b where b.bukrs= :bukrs AND b.hkont like '1010%'");
		query.setParameter("bukrs", bukrs);
		List<Bseg> r_bseg = query.getResultList();
		return r_bseg;
	}

	public List<Bseg> getByNotLike(String bukrs) {
		Query query = this.em
				.createQuery("select b FROM Bseg b where b.bukrs= :bukrs AND b.hkont not like '1010%'");
		query.setParameter("bukrs", bukrs);
		List<Bseg> a_bseg = query.getResultList();
		return a_bseg;
	}
	
	public List<Bseg> dynamicFindBseg(String a_dynamicWhere){ 
    	//System.out.println(a_dynamicWhere);
    	Query query = this.em
                .createQuery("select b FROM Bseg b where "+a_dynamicWhere);    
    	List<Bseg> l_bseg =  query.getResultList();
    	return l_bseg;
    }
	
	public List<Podotchet> findPodocthetByBukrBranchWaers(String a_bukrs,Long a_branch_id, String a_waers)
	{
		try{ 
			Query query = this.em.createQuery("select c.customer_id"
					+ ", c.firstname"
					+ ", c.lastname"
					+ ", c.middlename"
					+ ", b.waers"
					+ ", sum(CASE WHEN (bs.shkzg = 'S') THEN bs.dmbtr ELSE (bs.dmbtr * -1) END) as dmbtr"
					+ ", c.staff_id"
					+ ", b.bukrs"
					+ ", b.brnch "
					+ ", sum(CASE WHEN (bs.shkzg = 'S') THEN bs.wrbtr ELSE (bs.wrbtr * -1) END) as wrbtr"
					+ ", c.iin_bin"
					+ " FROM Bkpf b,Bseg bs,Customer c "
					+ " where b.belnr = bs.belnr and "
					+ " b.gjahr=bs.gjahr and "
					+ " b.bukrs = bs.bukrs and "
					+ " b.brnch=:brnch and "
					+ " b.waers=:waers and "
					+ " b.bukrs=:bukrs and "
					+ " bs.lifnr=c.customer_id and "
					+ " bs.hkont='12500001'"
					+ " group by c.customer_id"
					+ ", c.staff_id"
					+ ", c.firstname"
					+ ", c.lastname"
					+ ", c.middlename"
					+ ", b.waers"
					+ ", b.bukrs"
					+ ", b.brnch"
					+ ", c.iin_bin "
					+ " order by c.lastname,c.firstname, c.middlename ASC"); 
			List<Podotchet> rt_list = new ArrayList<Podotchet>();
			query.setParameter("bukrs", a_bukrs);
			query.setParameter("waers", a_waers);
			query.setParameter("brnch", a_branch_id);
			List<Object[]> results = query.getResultList();
			for (Object[] result : results) {
				
				
				Podotchet rt = new Podotchet();
	    		rt.setCustomer_id((long) result[0]);
	    		if (result[1]!=null) rt.setFirstname(String.valueOf(result[1]));
	    		if (result[2]!=null) rt.setLastname(String.valueOf(result[2]));
	    		if (result[3]!=null) rt.setMiddlename(String.valueOf(result[3]));
	    		rt.setWaers(String.valueOf(result[4]));
	    		rt.setDmbtr((double) result[5]);
	    		rt.setStaff_id((long) result[6]);
	    		rt.setBukrs(String.valueOf(result[7]));
	    		rt.setBranch_id((long) result[8]);
	    		rt.setWrbtr((double) result[9]);
	    		rt.setIin_bin(String.valueOf(result[10]));
	    		if (a_waers.equals("USD")) rt.setSumma(rt.getDmbtr());
	    		else rt.setSumma(rt.getWrbtr());
	    		
	    		if(rt.getSumma()!=0)
	    		{
	    			rt_list.add(rt);
	    		}
	    		
	    	}
			return rt_list;
		}	
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!
			return null;
			}
	}
	public List<Frep1OutputTable> dynamicFindFrep1(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			List<Frep1OutputTable> outputTable = new ArrayList<Frep1OutputTable>();	
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");			
			Query query = this.em.createNativeQuery(
					"select s.text45,bs.shkzg,bs.dmbtr,bs.wrbtr,s.waers,b.budat,b.bldat,br.text45 as brname,b.bukrs,b.belnr,b.gjahr,b.bktxt from bkpf b, bseg bs,skat s,branch br" 
					+" where b.belnr=bs.belnr and b.gjahr=bs.gjahr and b.bukrs=bs.bukrs" 
					+" and br.branch_id=b.brnch"
					+" and s.bukrs=b.bukrs and s.hkont=bs.hkont"
					+" and b.blart='IF' and b.bukrs=1000 and bs.hkont not like '1020%'"
					+" "+a_dynamicWhere+" "
					+" order by b.bldat,bs.dmbtr,bs.wrbtr");
			List<Object[]> results = query.getResultList();
			for (Object[] result : results) {
				Frep1OutputTable wa_rt = new Frep1OutputTable();
				//wa_fot.setIndex(index);
				if (result[0]!=null) wa_rt.setHkontName(String.valueOf(result[0]));
				if (result[1]!=null) wa_rt.setShkzg(String.valueOf(result[1]));
				if (result[2]!=null) wa_rt.setDmbtr(Double.parseDouble(String.valueOf(result[2]))); 
				if (result[3]!=null) wa_rt.setWrbtr(Double.parseDouble(String.valueOf(result[3])));
				if (result[4]!=null) wa_rt.setWaers(String.valueOf(result[4]));
				if (result[6]!=null) wa_rt.setBldat(formatter.parse(String.valueOf(result[6])));
				
				if (result[7]!=null) wa_rt.setBranchName(String.valueOf(result[7]));
				if (result[8]!=null) wa_rt.setBukrs(String.valueOf(result[8]));
				if (result[9]!=null) wa_rt.setBelnr(Long.parseLong(String.valueOf(result[9])));
				if (result[10]!=null) wa_rt.setGjahr(Integer.parseInt(String.valueOf(result[10])));
				if (result[11]!=null) wa_rt.setBktxt(String.valueOf(result[11]));
				
				if (wa_rt.getWaers().equals("USD")) wa_rt.setSumma(wa_rt.getDmbtr());
				else wa_rt.setSumma(wa_rt.getWrbtr());
				
				if (wa_rt.getShkzg().equals("H")) wa_rt.setSumma(wa_rt.getSumma()*-1);
				outputTable.add(wa_rt);
				

			}
	    	
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	public List<Frep2OutputTable> dynamicFindFrep2(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			List<Frep2OutputTable> outputTable = new ArrayList<Frep2OutputTable>();	
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String s1 = "";
			s1="select bs.bukrs as n0,bs.dmbtr as n1,bs.wrbtr as n2,b.waers as n3,"
					+" b.budat as n4,b.usnam as n5,bs.lifnr as n6,con.contract_date as n7,con.contract_date+31  as n8,"
					+" cus.customer_id as n9,cus.fiz_yur as n10,cus.name as n11,cus.firstname as n12,cus.lastname as n13,cus.middlename as n14,b.contract_number  as n15,con.waers as n16,con.rate as n17,"
					+" ref_cus.customer_id as n18,ref_cus.fiz_yur as n19,ref_cus.name as n20,ref_cus.firstname as n21,ref_cus.lastname as n22,ref_cus.middlename as n23,ref_con.contract_number  as n24,ref_con.waers as n25,ref_con.rate  as n26,"
					+" b.belnr,b.gjahr,br.text45,b.bktxt,b.closed"
					+" from bkpf b,bseg bs,customer ref_cus,contract con,contract ref_con,customer cus,branch br where "
					+" b.belnr=bs.belnr and b.gjahr=bs.gjahr and b.bukrs=bs.bukrs and con.branch_id=br.branch_id"
					+" and bs.lifnr=ref_cus.customer_id and b.contract_number=con.contract_number and con.ref_contract_id=ref_con.contract_id"
					+" and cus.customer_id=con.customer_id"
					+" and b.blart='VZ' and b.storno=0  and bs.hkont like '331%'"
					+" "+a_dynamicWhere+" "
					+" order by con.contract_date";
			Query query = this.em.createNativeQuery(s1);
			List<Object[]> results = query.getResultList();
			for (Object[] result : results) {
				Frep2OutputTable wa_rt = new Frep2OutputTable();
				//wa_fot.setIndex(index);
				if (result[0]!=null) wa_rt.setBukrs(String.valueOf(result[0]));
				if (result[1]!=null) wa_rt.setDmbtr(Double.parseDouble(String.valueOf(result[1]))); 
				if (result[2]!=null) wa_rt.setWrbtr(Double.parseDouble(String.valueOf(result[2])));
				if (result[3]!=null) wa_rt.setWaers(String.valueOf(result[3]));
				if (result[4]!=null) wa_rt.setBudat(formatter.parse(String.valueOf(result[4])));
				
				if (result[6]!=null) wa_rt.setLifnr(Long.parseLong(String.valueOf(result[6])));
				if (result[7]!=null) wa_rt.setConDate(formatter.parse(String.valueOf(result[7])));
				if (result[8]!=null) wa_rt.setVydachaDate(formatter.parse(String.valueOf(result[8])));
				
				if (result[9]!=null) wa_rt.setCusId(Long.parseLong(String.valueOf(result[9])));
				if (result[10]!=null && Integer.parseInt(String.valueOf(result[10]))==1) wa_rt.setCusFIO(String.valueOf(result[11]));
				else if (result[10]!=null && Integer.parseInt(String.valueOf(result[10]))==2) wa_rt.setCusFIO(Validation.returnFioInitials(String.valueOf(result[12]), String.valueOf(result[13]), String.valueOf(result[14])));
				if (result[15]!=null) wa_rt.setConNum(Long.parseLong(String.valueOf(result[15])));
				if (result[16]!=null) wa_rt.setConWaers(String.valueOf(result[16]));
				if (result[17]!=null) wa_rt.setConKursf(Double.parseDouble(String.valueOf(result[17]))); 

				
				
				if (result[18]!=null) wa_rt.setRefCusId(Long.parseLong(String.valueOf(result[18])));
				if (result[19]!=null && Integer.parseInt(String.valueOf(result[19]))==1) wa_rt.setRefCusFIO(String.valueOf(result[20]));
				else if (result[19]!=null && Integer.parseInt(String.valueOf(result[19]))==2) wa_rt.setRefCusFIO(Validation.returnFioInitials(String.valueOf(result[21]), String.valueOf(result[22]), String.valueOf(result[23])));
				if (result[24]!=null) wa_rt.setRefConNum(Long.parseLong(String.valueOf(result[24])));
				if (result[25]!=null) wa_rt.setRefConWaers(String.valueOf(result[25]));
				if (result[26]!=null) wa_rt.setRefConKursf(Double.parseDouble(String.valueOf(result[26])));
				
				if (result[27]!=null) wa_rt.setBelnr(Long.parseLong(String.valueOf(result[27])));
				if (result[28]!=null) wa_rt.setGjahr(Integer.parseInt(String.valueOf(result[28])));
				if (result[29]!=null) wa_rt.setBranchName(String.valueOf(result[29]));
				if (result[30]!=null) wa_rt.setBktxt(String.valueOf(result[30]));
				if (result[31]!=null) wa_rt.setClosed(Integer.parseInt(String.valueOf(result[31])));
				if (wa_rt.getWaers().equals("USD")) wa_rt.setSumma(wa_rt.getDmbtr());
				else wa_rt.setSumma(wa_rt.getWrbtr());
				outputTable.add(wa_rt);
				

			}
	    	
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> dynamicFindFrep2OldBaza(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			List<Frep2OutputTable> outputTable = new ArrayList<Frep2OutputTable>();	
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String s1 = "";
			s1="select"
					+" 1000 as n0, valuta n1,"
					+" con.contract_date as n2,con.contract_date+31  as n3,"
					+" cus.customer_id as n4,cus.fiz_yur as n5,cus.name as n6,cus.firstname as n7,cus.lastname as n8,cus.middlename as n9,con.contract_number  as n10,con.waers as n11,con.rate as n12,"
					+" ref_cus.customer_id as n13,ref_cus.fiz_yur as n14,ref_cus.name as n15,ref_cus.firstname as n16,ref_cus.lastname as n17,ref_cus.middlename as n18,ref_con.contract_number  as n19,ref_con.waers as n20,ref_con.rate  as n21,"
					+" conbr.text45 as n22,m.summa as n23,refconco.currency as n24"
					+" from migvozn m,contract con, contract ref_con,branch conbr, branch refconbr,country conco,country refconco,customer cus,customer ref_cus"
					+" where m.connum=con.contract_number and m.refconnum=ref_con.contract_number and conbr.branch_id=con.branch_id and refconbr.branch_id=ref_con.branch_id"
					+" and conco.country_id=conbr.country_id and refconco.country_id=refconbr.country_id"
					+" and cus.customer_id=con.customer_id and ref_cus.customer_id=ref_con.customer_id"
					+" and m.vozn_given=0"
					+" "+a_dynamicWhere+" "
					+" order by con.contract_date";
			Query query = this.em.createNativeQuery(s1);
			List<Object[]> results = query.getResultList();
			return results;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	

	
	public List<Object[]> dynamicFindFrep2PaymentDetails(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			
			String s1 = "";
			s1="select b.bukrs,b.belnr, b.gjahr, bktxt, br.text45, bs.dmbtr, bs.wrbtr, budat, bldat,b.waers"
					+" from bkpf b,bseg bs, branch br where "
				+" b.belnr=bs.belnr and "
				+" b.gjahr=bs.gjahr and "
				+" b.bukrs=bs.bukrs and "
				+" b.brnch=br.branch_id and"
				+" b.blart='KP' and b.storno=0 and bs.hkont like '331%'"
					+" "+a_dynamicWhere+" "
					+" order by budat";
			Query query = this.em.createNativeQuery(s1);
			System.out.println(s1);
			List<Object[]> results = query.getResultList();
			
	    	
	    	return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
}
