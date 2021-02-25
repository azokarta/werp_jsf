package f4;
import general.AppContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean; 
import javax.faces.bean.ManagedProperty;

import general.dao.DAOException;
import general.dao.PositionDao;
import general.tables.Position;
@ManagedBean(name = "positionF4Bean")
@ApplicationScoped
public class PositionF4 {
	List<Position> position_list = new ArrayList<Position>(); 
	public List<Position> getPosition_list(){
		return position_list;
	}
	List<Position> position_list_by_en = new ArrayList<Position>(); 
	public List<Position> getPosition_list_by_en(){
		return position_list_by_en;
	}
	
	List<Position> position_list_by_tr = new ArrayList<Position>(); 
	public List<Position> getPosition_list_by_tr(){
		return position_list_by_tr;
	}
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
			PositionDao positionDao = (PositionDao) appContext.getContext().getBean("positionDao");
			position_list = positionDao.findAllOrdered("ru");
			position_list_by_en = positionDao.findAllOrdered("en");
			position_list_by_tr = positionDao.findAllOrdered("tr");
			
			Long key;
			for (Position wa_pos:position_list)
			{
				key = wa_pos.getPosition_id();
				l_pos_map.put(key, wa_pos);
			}
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Position F4 not loaded");
	    	throw new DAOException("Position F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			 position_list = new ArrayList<Position>();
			l_pos_map = new HashMap<Long,Position>();
			PositionDao positionDao = (PositionDao) appContext.getContext().getBean("positionDao");
			position_list = positionDao.findAll();
			
			Long key;
			for (Position wa_pos:position_list)
			{
				key = wa_pos.getPosition_id();
				l_pos_map.put(key, wa_pos);
			}			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Position F4 not loaded");
	    	throw new DAOException("Position F4 not loaded");
		}
	}
	
	Map<Long,Position> l_pos_map = new HashMap<Long,Position>();
	public Map<Long, Position> getL_pos_map() {
		return l_pos_map;
	}


	public String getName(String a_pos_id)
	{
		
		if (a_pos_id!=null && a_pos_id.length()>0)
		{
			Long a_pos_long = Long.parseLong(a_pos_id);
			Position wa_pos = l_pos_map.get(a_pos_long);
			if (wa_pos!=null)
			{
				return wa_pos.getText();
			}
			else
			{
				return "";
			}
			
		}
		return "";
	}
	
	public String getName(String a_pos_id, String a_lang)
	{
		
		if (a_pos_id!=null && a_pos_id.length()>0)
		{
			Long a_pos_long = Long.parseLong(a_pos_id);
			Position wa_pos = l_pos_map.get(a_pos_long);
			if (wa_pos!=null)
			{
				return wa_pos.getName(a_lang);
			}
			else
			{
				return "";
			}
			
		}
		return "";
	}
	
	public List<Position> getPL(String a_lang){
		if (a_lang==null || a_lang.equals("ru")) return position_list;
		else if (a_lang==null || a_lang.equals("tr")) return position_list_by_en;
		else if (a_lang==null || a_lang.equals("en")) return position_list_by_en;
		else return position_list;
	}
	
} 
