package f4;

import general.AppContext;
import general.Validation;
import general.dao.BukrsDao;
import general.dao.DAOException;
import general.tables.Bukrs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;


@ManagedBean(name="bukrsF4Bean")
@ApplicationScoped
public class BukrsF4 {
	List<Bukrs> bukrs_list = new ArrayList<Bukrs>(); 
		
	@PostConstruct
	public void init(){
		try
		{
			bukrs_list = new ArrayList<Bukrs>();
			BukrsDao d = (BukrsDao)appContext.getContext().getBean("bukrsDao");
			this.bukrs_list = d.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Bukrs F4 not loaded");
	    	throw new DAOException("Bukrs F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			bukrs_list = new ArrayList<Bukrs>();
			BukrsDao d = (BukrsDao)appContext.getContext().getBean("bukrsDao");
			this.bukrs_list = d.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Bukrs F4 not loaded");
	    	throw new DAOException("Bukrs F4 not loaded");
		}
	}
	
	public List<Bukrs> getBukrs_list(){
		return bukrs_list;
	} 
	
	public List<Bukrs> getBukrsByBukrs(String a_bukrs)
	{
		List<Bukrs> wa_bukrs_list = new ArrayList<Bukrs>();
		if (a_bukrs != null && a_bukrs.length()>0)
		{
			for(Bukrs wa_bukrs:bukrs_list)
			{
				if (wa_bukrs.getBukrs().equals(a_bukrs))
				{
					wa_bukrs_list.add(wa_bukrs);
				}				
			}
		}
		return wa_bukrs_list;
	}	
	public List<Bukrs> getByBukrs(String a_user_bukrs, boolean isMain)
	{
		//System.out.println(isMain);
		List<Bukrs> wa_bukrs_list = new ArrayList<Bukrs>();

		
		if (isMain)
		{
			for(Bukrs wa_bukrs:bukrs_list)
			{
				wa_bukrs_list.add(wa_bukrs);								
			}
			return wa_bukrs_list;
		}
		
		if (a_user_bukrs != null && a_user_bukrs.length()>0)
		{
			
			for(Bukrs wa_bukrs:bukrs_list)
			{
				if (wa_bukrs.getBukrs().equals(a_user_bukrs))
				{
					wa_bukrs_list.add(wa_bukrs);
				}				
			}			
		}
		return wa_bukrs_list;
		
	}
	
	public String getNameByBukrs(String bukrs){
		if(!Validation.isEmptyString(bukrs)){
			for(Bukrs b: this.bukrs_list){
				if(b.getBukrs().equals(bukrs)){
					return b.getName();
				}
			}
		}
		return "";
	}
	
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
