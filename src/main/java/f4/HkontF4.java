package f4;

import general.AppContext;
import general.dao.DAOException;
import general.dao.HkontDao;
import general.output.tables.CashBankAccountStatement;
import general.output.tables.Frep1OutputTable;
import general.tables.Hkont;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;  
import javax.faces.bean.ManagedProperty;

import org.springframework.beans.BeanUtils;

@ManagedBean(name = "hkontF4Bean")
@ApplicationScoped
public class HkontF4 {
	List<Hkont> hkont_list = new ArrayList<Hkont>(); 
	public List<Hkont> getHkont_list(){
		return hkont_list;
	}
	

	List<Hkont> hkont_list1000 = new ArrayList<Hkont>(); 
	public List<Hkont> getHkont_list1000(){
		return hkont_list1000;
	}
	
	List<Hkont> hkont_list2000 = new ArrayList<Hkont>(); 
	public List<Hkont> getHkont_list2000(){
		return hkont_list2000;
	}

	
	Map<String,List<Hkont>> l_hkont_cash_bank_map = new HashMap<String,List<Hkont>>();
	public Map<String, List<Hkont>> getL_hkont_cash_bank_map() {
		return l_hkont_cash_bank_map;
	}

	public void setL_hkont_cash_bank_map(Map<String, List<Hkont>> l_hkont_cash_bank_map) {
		this.l_hkont_cash_bank_map = l_hkont_cash_bank_map;
	}


	Map<String,Hkont> l_hkont_map = new HashMap<String,Hkont>();
	//***************************Application Context***************************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;  
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	//*************************************************************************
	@PostConstruct
	public void init(){
		try
		{
			hkont_list = new ArrayList<Hkont>();				
			hkont_list1000 = new ArrayList<Hkont>();			
			hkont_list2000 = new ArrayList<Hkont>();
			HkontDao hkontDao = (HkontDao) appContext.getContext().getBean("hkontDao");
			hkont_list = hkontDao.findAll();
			for (Hkont wa_hkont:hkont_list)
			{
				l_hkont_map.put(wa_hkont.getBukrs()+wa_hkont.getHkont(), wa_hkont);
				
				if (wa_hkont.getBukrs().equals("1000"))
				{
					hkont_list1000.add(wa_hkont);
					
					if (wa_hkont.getBranch_id()!=null && (wa_hkont.getHkont().startsWith("1010")||wa_hkont.getHkont().startsWith("1030")))
					{
						List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()));
						if (wal_hkont==null || wal_hkont.size()==0)
						{
							wal_hkont = new ArrayList<Hkont>();
							wal_hkont.add(wa_hkont);
							l_hkont_cash_bank_map.put(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()), wal_hkont);
						}
						else
						{
							wal_hkont.add(wa_hkont);
						}
					}
					
				}
				else if (wa_hkont.getBukrs().equals("2000"))
				{
					hkont_list2000.add(wa_hkont);
					if (wa_hkont.getBranch_id()!=null && (wa_hkont.getHkont().startsWith("1010")||wa_hkont.getHkont().startsWith("1030")))
					{
						List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()));
						if (wal_hkont==null || wal_hkont.size()==0)
						{
							wal_hkont = new ArrayList<Hkont>();
							wal_hkont.add(wa_hkont);
							l_hkont_cash_bank_map.put(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()), wal_hkont);
						}
						else
						{
							wal_hkont.add(wa_hkont);
						}
					}
				}
				else
				{
					if (wa_hkont.getBranch_id()!=null && (wa_hkont.getHkont().startsWith("1010")||wa_hkont.getHkont().startsWith("1030")))
					{
						List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()));
						if (wal_hkont==null || wal_hkont.size()==0)
						{
							wal_hkont = new ArrayList<Hkont>();
							wal_hkont.add(wa_hkont);
							l_hkont_cash_bank_map.put(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()), wal_hkont);
						}
						else
						{
							wal_hkont.add(wa_hkont);
						}
					}
				}
			}
			

			List<Object[]> results = hkontDao.getCashBankBranchAll();
			
			//
			for (Object[] result : results) {
				String bukrs = "";
				Long branchId = 0L;
				String hkont = "";
				
				bukrs = String.valueOf(result[0]);
				branchId = Long.parseLong(String.valueOf(result[1]));
				hkont = String.valueOf(result[2]);
				
				Hkont wa_hkont = new Hkont();				
				wa_hkont = l_hkont_map.get(bukrs+hkont);
				
				List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(bukrs+String.valueOf(branchId));
				if (wal_hkont==null || wal_hkont.size()==0)
				{
					wal_hkont = new ArrayList<Hkont>();
					wal_hkont.add(wa_hkont);
					l_hkont_cash_bank_map.put(bukrs+String.valueOf(branchId), wal_hkont);
				}
				else
				{
					wal_hkont.add(wa_hkont);
				}

			}
	    	
		}
	    catch(Exception ex)
		{
	    	System.out.println("Hkont F4 not loaded");
	    	throw new DAOException("Hkont F4 not loaded");
		}
	}
	
	
	public void updateF4()
	{
		try
		{
			hkont_list = new ArrayList<Hkont>();					
			hkont_list1000 = new ArrayList<Hkont>();			
			hkont_list2000 = new ArrayList<Hkont>();
			l_hkont_cash_bank_map = new HashMap<String,List<Hkont>>();
			l_hkont_map = new HashMap<String,Hkont>();
			HkontDao hkontDao = (HkontDao) appContext.getContext().getBean("hkontDao");
			hkont_list = hkontDao.findAll();
			for (Hkont wa_hkont:hkont_list)
			{
				l_hkont_map.put(wa_hkont.getBukrs()+wa_hkont.getHkont(), wa_hkont);
				if (wa_hkont.getBukrs().equals("1000"))
				{
					hkont_list1000.add(wa_hkont);
					if (wa_hkont.getBranch_id()!=null && (wa_hkont.getHkont().startsWith("1010")||wa_hkont.getHkont().startsWith("1030")))
					{
						List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()));
						if (wal_hkont==null || wal_hkont.size()==0)
						{
							wal_hkont = new ArrayList<Hkont>();
							wal_hkont.add(wa_hkont);
							l_hkont_cash_bank_map.put(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()), wal_hkont);
						}
						else
						{
							wal_hkont.add(wa_hkont);
						}
					}
				}
				else if (wa_hkont.getBukrs().equals("2000"))
				{
					hkont_list2000.add(wa_hkont);
					if (wa_hkont.getBranch_id()!=null && (wa_hkont.getHkont().startsWith("1010")||wa_hkont.getHkont().startsWith("1030")))
					{
						List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()));
						if (wal_hkont==null || wal_hkont.size()==0)
						{
							wal_hkont = new ArrayList<Hkont>();
							wal_hkont.add(wa_hkont);
							l_hkont_cash_bank_map.put(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()), wal_hkont);
						}
						else
						{
							wal_hkont.add(wa_hkont);
						}
					}
				}
				else
				{
					if (wa_hkont.getBranch_id()!=null && (wa_hkont.getHkont().startsWith("1010")||wa_hkont.getHkont().startsWith("1030")))
					{
						List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()));
						if (wal_hkont==null || wal_hkont.size()==0)
						{
							wal_hkont = new ArrayList<Hkont>();
							wal_hkont.add(wa_hkont);
							l_hkont_cash_bank_map.put(wa_hkont.getBukrs()+String.valueOf(wa_hkont.getBranch_id()), wal_hkont);
						}
						else
						{
							wal_hkont.add(wa_hkont);
						}
					}
				}
			}
			
			
			List<Object[]> results = hkontDao.getCashBankBranchAll();
			
			//
			for (Object[] result : results) {
				String bukrs = "";
				Long branchId = 0L;
				String hkont = "";
				
				bukrs = String.valueOf(result[0]);
				branchId = Long.parseLong(String.valueOf(result[1]));
				hkont = String.valueOf(result[2]);
				
				Hkont wa_hkont = new Hkont();				
				wa_hkont = l_hkont_map.get(bukrs+hkont);
				
				List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(bukrs+String.valueOf(branchId));
				if (wal_hkont==null || wal_hkont.size()==0)
				{
					wal_hkont = new ArrayList<Hkont>();
					wal_hkont.add(wa_hkont);
					l_hkont_cash_bank_map.put(bukrs+String.valueOf(branchId), wal_hkont);
				}
				else
				{
					wal_hkont.add(wa_hkont);
				}

			}
		}
	    catch(Exception ex)
		{
	    	System.out.println("Hkont F4 not loaded");
	    	throw new DAOException("Hkont F4 not loaded");
		}
	}
	public List<Hkont> getByBukrs(String a_bukrs){
		if (a_bukrs==null)
		{
			return null;
		}
		else if (a_bukrs.equals("1000"))
		{
			return hkont_list1000;
		}
		else if (a_bukrs.equals("2000"))
		{
			return hkont_list2000;
		}
		return null;
	}
	
	public String getNameByBukrsHkont(String a_bukrs, String a_hkont, String a_spras)
	{
		if (a_bukrs!=null && a_hkont!=null)
		{
			Hkont wa_hkont = l_hkont_map.get(a_bukrs+a_hkont);
			if (wa_hkont!=null && wa_hkont.getHkont()!=null && wa_hkont.getHkont().equals(a_hkont) && wa_hkont.getBukrs().equals(a_bukrs))
			{
				String waers = "";
				String text = "";
				if (wa_hkont.getWaers()!=null)
				{
					waers = wa_hkont.getWaers();
				}
				if (a_spras==null || a_spras.equals("ru"))
				{
					text = wa_hkont.getText45()+" "+waers;					
					return wa_hkont.getText45()+" "+waers;
				}
				else if (a_spras.equals("en"))
				{
					if (wa_hkont.getName_en()==null) text = wa_hkont.getText45()+" "+waers;
					else text = wa_hkont.getName_en()+" "+waers;
					return text;
				}
				else if (a_spras.equals("tr"))
				{

					if (wa_hkont.getName_tr()==null) text = wa_hkont.getText45()+" "+waers;
					else text = wa_hkont.getName_tr()+" "+waers;
					return text;
				}
			}
		}
		return "";
	}
	public List<CashBankAccountStatement> getHkontBranchWaers(String a_bukrs, Long a_branch_id)
	{
		List<CashBankAccountStatement> l_hkont = new ArrayList<CashBankAccountStatement>();
		CashBankAccountStatement p_hkont = new CashBankAccountStatement();
		if (a_bukrs!=null && a_branch_id!=null)
		{
			
			List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(a_bukrs+String.valueOf(a_branch_id));
			if (wal_hkont==null || wal_hkont.size()==0)
			{
				wal_hkont = new ArrayList<Hkont>();
				return l_hkont;
			}
			else
			{
				for (Hkont wa_hkont:wal_hkont)
				{
					p_hkont = new CashBankAccountStatement();
					BeanUtils.copyProperties(wa_hkont, p_hkont);
					p_hkont.setHkont_name(wa_hkont.getText45());
					l_hkont.add(p_hkont);
				}
				
				return l_hkont;
			}
			
		}
		return l_hkont;
		
	}
	public List<Hkont> getTypeHkontBranchWaers(String a_bukrs, Long a_branch_id)
	{
		List<Hkont> l_hkont = new ArrayList<Hkont>();
		Hkont p_hkont = new Hkont();
		if (a_bukrs!=null && a_branch_id!=null)
		{
			
			List<Hkont> wal_hkont = l_hkont_cash_bank_map.get(a_bukrs+String.valueOf(a_branch_id));
			if (wal_hkont==null || wal_hkont.size()==0)
			{
				wal_hkont = new ArrayList<Hkont>();
				return l_hkont;
			}
			else
			{
				for (Hkont wa_hkont:wal_hkont)
				{
					p_hkont = new Hkont();
					BeanUtils.copyProperties(wa_hkont, p_hkont);
					l_hkont.add(p_hkont);
				}
				
				return l_hkont;
			}
			
		}
		return l_hkont;
		
	}
}
