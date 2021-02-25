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

import general.dao.CityDao;
import general.dao.DAOException;
import general.tables.City;

@ManagedBean(name = "cityF4Bean")
@ApplicationScoped
public class CityF4 {

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

	List<City> city_list = new ArrayList<City>();
	Map<Long, City> l_city_map = new HashMap<Long, City>();

	@PostConstruct
	public void init() {
		try {
			city_list = new ArrayList<City>();
			l_city_map = new HashMap<Long, City>();
			CityDao cityDao = (CityDao) appContext.getContext().getBean(
					"cityDao");
			city_list = cityDao.findAll();
			Long key;
			for (City wa_c : city_list) {
				key = wa_c.getIdcity();
				l_city_map.put(key, wa_c);
			}
		} catch (Exception ex) {
			System.out.println("City F4 not loaded");
			throw new DAOException("City F4 not loaded");
		}
	}

	public void updateF4() {
		try {
			city_list = new ArrayList<City>();
			l_city_map = new HashMap<Long, City>();
			CityDao cityDao = (CityDao) appContext.getContext().getBean(
					"cityDao");
			city_list = cityDao.findAll();
			Long key;
			for (City wa_c : city_list) {
				key = wa_c.getIdcity();
				l_city_map.put(key, wa_c);
			}
		} catch (Exception ex) {
			System.out.println("City F4 not loaded");
			throw new DAOException("City F4 not loaded");
		}
	}

	public List<City> getCity_list() {
		return city_list;
	}

	public Map<Long, City> getL_city_map() {
		return l_city_map;
	}

	public String getName(Long id) {
		return l_city_map.get(id) == null ? null : l_city_map.get(id).getName();
	}
}
