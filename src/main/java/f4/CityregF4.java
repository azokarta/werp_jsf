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

import general.dao.CityregDao;
import general.dao.DAOException;
import general.tables.Cityreg;

@ManagedBean(name = "cityregF4Bean")
@ApplicationScoped
public class CityregF4 {

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

	List<Cityreg> cityreg_list = new ArrayList<Cityreg>();
	Map<Long, Cityreg> l_cityreg_map = new HashMap<Long, Cityreg>();

	@PostConstruct
	public void init() {
		try {
			cityreg_list = new ArrayList<Cityreg>();
			l_cityreg_map = new HashMap<Long, Cityreg>();
			CityregDao cityregDao = (CityregDao) appContext.getContext()
					.getBean("cityregDao");
			cityreg_list = cityregDao.findAll();
			Long key;
			for (Cityreg wa_c : cityreg_list) {
				key = wa_c.getIdcityreg();
				l_cityreg_map.put(key, wa_c);
			}
		} catch (Exception ex) {
			System.out.println("City region F4 not loaded");
			throw new DAOException("City region F4 not loaded");
		}

	}

	public void updateF4() {
		init();
	}

	public List<Cityreg> getByCityId(Long cityId) {
		List<Cityreg> out = new ArrayList<Cityreg>();
		for (Cityreg reg : cityreg_list) {
			if (reg.getCity_id().equals(cityId)) {
				out.add(reg);
			}
		}

		return out;
	}

	public List<Cityreg> getCityreg_list() {
		return cityreg_list;
	}

	public Map<Long, Cityreg> getL_cityreg_map() {
		return l_cityreg_map;
	}

	public String getName(Long id) {
		return l_cityreg_map.get(id) == null ? null : l_cityreg_map.get(id)
				.getRegname();
	}
}
