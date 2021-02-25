package f4;

import general.AppContext;
import general.dao.AddressTypeDao;
import general.dao.DAOException;
import general.dao.WerksDao;
import general.tables.AddressType;
import general.tables.Werks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "addrTypeF4Bean")
@ApplicationScoped
public class AddrTypeF4 {

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

	List<AddressType> addrType_list = new ArrayList<AddressType>();
	Map<Long, AddressType> l_addrType_map = new HashMap<Long, AddressType>();

	@PostConstruct
	public void init() {
		try {
			addrType_list = new ArrayList<AddressType>();
			l_addrType_map = new HashMap<Long, AddressType>();

			AddressTypeDao atDao = (AddressTypeDao) appContext.getContext()
					.getBean("addressTypeDao");
			addrType_list = atDao.findAll();
			Long key;
			for (AddressType wa_at : addrType_list) {
				key = wa_at.getId();
				l_addrType_map.put(key, wa_at);
			}
		} catch (Exception ex) {
			System.out.println("AddressType F4 not loaded");
			throw new DAOException("AddressType F4 not loaded");
		}
	}

	public void updateF4()
	{
		try {
			addrType_list = new ArrayList<AddressType>();
			l_addrType_map = new HashMap<Long, AddressType>();

			AddressTypeDao atDao = (AddressTypeDao) appContext.getContext()
					.getBean("addressTypeDao");
			addrType_list = atDao.findAll();
			Long key;
			for (AddressType wa_at : addrType_list) {
				key = wa_at.getId();
				l_addrType_map.put(key, wa_at);
			}
		} catch (Exception ex) {
			System.out.println("AddressType F4 not loaded");
			throw new DAOException("AddressType F4 not loaded");
		}
	}
	public List<AddressType> getAddrType_list() {
		return addrType_list;
	}

	public Map<Long, AddressType> getL_addrType_map() {
		return l_addrType_map;
	}
	
	public String getName(Long id){
		for(AddressType at:addrType_list){
			if(at.getId().equals(id)){
				return at.getNameRu();
			}
		}
		
		return null;
	}
	public String getName(Long id, String a_lang){
		for(AddressType at:addrType_list){
			if(at.getId().equals(id)){
				return at.getName(a_lang);
			}
		}
		
		return null;
	}
}
