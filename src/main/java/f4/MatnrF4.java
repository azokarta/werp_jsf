package f4;

import general.AppContext;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.tables.Matnr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean; 
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "matnrF4Bean")
@ApplicationScoped
public class MatnrF4 {
	List<Matnr> matnr_list = new ArrayList<Matnr>(); 
	public List<Matnr> getMatnr_list(){
		return matnr_list;
	}
	
	Map<Long,Matnr> l_matnr_map = new HashMap<Long,Matnr>();
	Map<String,Matnr> c_matnr_map = new HashMap<String,Matnr>();
	
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
			MatnrDao matnrDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
			matnr_list = matnrDao.findAll();
			c_matnr_map = new HashMap<String,Matnr>();
			l_matnr_map = new HashMap<Long,Matnr>();
			
			Long key;
			for (Matnr wa_matnr:matnr_list)
			{
				String c = wa_matnr.getCode();
				key = wa_matnr.getMatnr();
				l_matnr_map.put(key, wa_matnr);
				if (!Validation.isEmptyString(c)) {
					c_matnr_map.put(c, wa_matnr);
				}
			}
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Matnr F4 not loaded");
	    	throw new DAOException("Matnr F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			matnr_list = new ArrayList<Matnr>();
			l_matnr_map = new HashMap<Long,Matnr>();
			MatnrDao matnrDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
			matnr_list = matnrDao.findAll();
			
			Long key;
			for (Matnr wa_matnr:matnr_list)
			{
				key = wa_matnr.getMatnr();
				l_matnr_map.put(key, wa_matnr);
			}			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Matnr F4 not loaded");
	    	throw new DAOException("Matnr F4 not loaded");
		}
	}
	
	public Map<Long, Matnr> getL_matnr_map() {
		return l_matnr_map;
	}
	
	public Map<String, Matnr> getC_matnr_map() {
		return c_matnr_map;
	}

	public String getName(String a_matnr)
	{
		
		if (a_matnr!=null && a_matnr.length()>0)
		{
			Long a_matnr_long = Long.parseLong(a_matnr);
			Matnr wa_matnr = l_matnr_map.get(a_matnr_long);
			if (wa_matnr!=null)
			{
				return wa_matnr.getText45() + " (" + wa_matnr.getCode() + ") ";
			}
			else
			{
				return "";
			}
			
		}
		return "";
	}
	
	public List<Matnr> getByBukrs(String bukrs){
		List<Matnr> out = new ArrayList<Matnr>();
		if(Validation.isEmptyString(bukrs)){
			return out;
		} 		
		/*
		MatnrBukrsDao mbDao = (MatnrBukrsDao) appContext.getContext().getBean("matnrBukrsDao");
		List<MatnrBukrs> mbl = mbDao.findAllByBukrs(bukrs);
		for (MatnrBukrs mb:mbl) {
			out.add(getL_matnr_map().get(mb.getMatnr()));
		}
		return out;
		*/		
		MatnrDao mDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
		return mDao.findAllByBukrs(bukrs);		
	}
	
	public List<Matnr> getByBukrsAndCategory(String a_bukrs, int a_cat){
		List<Matnr> out = new ArrayList<Matnr>();
		if(Validation.isEmptyString(a_bukrs)){
			//System.out.println("Bukrs is empry: " + a_bukrs);
			return out;
		}
		
		/*
		MatnrBukrsDao mbDao = (MatnrBukrsDao) appContext.getContext().getBean("matnrBukrsDao");
		List<MatnrBukrs> ml = mbDao.findAllByBukrs(a_bukrs);
		for(MatnrBukrs mb:ml){
			Matnr m = getL_matnr_map().get(mb.getMatnr());
			if (m.getType() == 1) {
				if(m.getCategory().intValue() == a_cat){
					out.add(m);
					//System.out.println("Add to output list: " + m.getText45());
				}
			}
		}
		*/
		
		MatnrDao mDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
		List<Matnr> ml = mDao.findAllByBukrs(a_bukrs);
		for(Matnr m:ml){
			if (m.getType() == 1) {
				if(m.getCategory().intValue() == a_cat){
					out.add(m);
					//System.out.println("Add to output list: " + m.getText45());
				}
			}
		}
		
		
		return out;
	}
}
