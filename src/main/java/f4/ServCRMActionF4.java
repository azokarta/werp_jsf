package f4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.AppContext;
import general.dao.DAOException;
import general.dao.ServCRMActionDao;
import general.tables.ServCRMAction;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "servCRMActionF4Bean")
@ApplicationScoped
public class ServCRMActionF4 {
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

	List<ServCRMAction> crmaction_list = new ArrayList<ServCRMAction>();
	Map<Long, ServCRMAction> l_crmaction_map = new HashMap<Long, ServCRMAction>();

	@PostConstruct
	public void init() {
		try {
			crmaction_list = new ArrayList<ServCRMAction>();
			l_crmaction_map = new HashMap<Long, ServCRMAction>();
			ServCRMActionDao scrmaDao = (ServCRMActionDao) appContext
					.getContext().getBean("servCRMActionDao");
			crmaction_list = scrmaDao.findAll();
			Long key;
			for (ServCRMAction wa_c : crmaction_list) {
				key = wa_c.getId();
				l_crmaction_map.put(key, wa_c);
			}
		} catch (Exception ex) {
			System.out.println("Service CRM actions F4 not loaded");
			throw new DAOException("Service CRM actions F4 not loaded. "
					+ ex.getMessage());
		}
	}

	public void updateF4() {
		init();
	}

	public List<ServCRMAction> getCrmaction_list() {
		return crmaction_list;
	}

	public void setCrmaction_list(List<ServCRMAction> crmaction_list) {
		this.crmaction_list = crmaction_list;
	}

	public Map<Long, ServCRMAction> getL_crmaction_map() {
		return l_crmaction_map;
	}

	public void setL_crmaction_map(Map<Long, ServCRMAction> l_crmaction_map) {
		this.l_crmaction_map = l_crmaction_map;
	}

}
