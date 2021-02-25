package f4;

import general.AppContext;
import general.dao.DAOException;
import general.dao.LegalEntityDao;
import general.tables.LegalEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "legalEntityF4Bean")
@ApplicationScoped
public class LegalEntityF4 {

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

	List<LegalEntity> le_list = new ArrayList<LegalEntity>();
	Map<Long, LegalEntity> l_le_map = new HashMap<Long, LegalEntity>();

	@PostConstruct
	public void init() {
		try {
			le_list = new ArrayList<LegalEntity>();
			l_le_map = new HashMap<Long, LegalEntity>();
			LegalEntityDao leDao = (LegalEntityDao) appContext.getContext().getBean(
					"legalEntityDao");
			le_list = leDao.findAll();
			Long key;
			for (LegalEntity wa_c : le_list) {
				key = wa_c.getId();
				l_le_map.put(key, wa_c);
			}
		} catch (Exception ex) {
			System.out.println("LegalEntity F4 not loaded");
			throw new DAOException("LegalEntity F4 not loaded");
		}
	}

	public List<LegalEntity> getCity_list() {
		return le_list;
	}

	public Map<Long, LegalEntity> getL_city_map() {
		return l_le_map;
	}

	public String getName(Long id) {
		return l_le_map.get(id) == null ? null : l_le_map.get(id).getName();
	}
}
