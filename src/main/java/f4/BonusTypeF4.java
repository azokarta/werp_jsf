package f4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import general.AppContext;
import general.dao.BonusTypeDao;
import general.dao.DAOException; 
import general.tables.BonusType;

import java.util.List;

@ManagedBean(name = "bonusTypeF4Bean")
@ApplicationScoped
public class BonusTypeF4 {
	
	
	// ***************************Application Context***************************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// *************************************************************************
	
	List<BonusType> bonusType_list = new ArrayList<BonusType>();
	Map<Long,BonusType> l_bonus_type_map = new HashMap<Long,BonusType>();
	@PostConstruct
	public void init() {
		
		try
		{
			BonusTypeDao bonusTypeDao = (BonusTypeDao) appContext.getContext().getBean("bonusTypeDao");
			bonusType_list = bonusTypeDao.findAll();
			Long key;
			for (BonusType wa_bon_type:bonusType_list)
			{
				key = wa_bon_type.getBonus_type_id();
				l_bonus_type_map.put(key, wa_bon_type);
			}
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Bonus F4 not loaded");
	    	throw new DAOException("Bonus type F4 not loaded");
		}
		 
	}
	
	public void updateF4()
	{
		try
		{
			bonusType_list = new ArrayList<BonusType>();
			l_bonus_type_map = new HashMap<Long,BonusType>();
			BonusTypeDao bonusTypeDao = (BonusTypeDao) appContext.getContext().getBean("bonusTypeDao");
			bonusType_list = bonusTypeDao.findAll();
			Long key;
			for (BonusType wa_bon_type:bonusType_list)
			{
				key = wa_bon_type.getBonus_type_id();
				l_bonus_type_map.put(key, wa_bon_type);
			}
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Bonus F4 not loaded");
	    	throw new DAOException("Bonus type F4 not loaded");
		}
	}

	public List<BonusType> getBonusType_list() {
		return bonusType_list;
	}

	public Map<Long, BonusType> getL_bonus_type_map() {
		return l_bonus_type_map;
	}
	
	public String getBonusTypeName(Long a_bon_type_id)
	{ 
		String name = "";
		if (a_bon_type_id!=null && a_bon_type_id>0)
		{
			BonusType wa_bon_type = l_bonus_type_map.get(a_bon_type_id);
			if (wa_bon_type!=null)
			{
				name = 	wa_bon_type.getText45();		
			}
		}
		return name;
	}
	
	public String getBonusTypeNameString(String a_bon_type_id)
	{ 
		String name = "";
		if (a_bon_type_id!=null && a_bon_type_id.length()>0)
		{
			BonusType wa_bon_type = l_bonus_type_map.get(Long.parseLong(a_bon_type_id));
			if (wa_bon_type!=null)
			{
				name = 	wa_bon_type.getText45();		
			}
		}
		return name;
	}
	
	public String getBonusTypeNameString(String a_bon_type_id, String a_lang)
	{ 
		String name = "";
		if (a_bon_type_id!=null && a_bon_type_id.length()>0)
		{
			BonusType wa_bon_type = l_bonus_type_map.get(Long.parseLong(a_bon_type_id));
			if (wa_bon_type!=null)
			{
				name = 	wa_bon_type.getName(a_lang);		
			}
		}
		return name;
	}

}
