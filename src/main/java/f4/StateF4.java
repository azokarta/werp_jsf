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
import general.dao.StateDao;
import general.tables.State;

@ManagedBean(name = "stateF4Bean")
@ApplicationScoped
public class StateF4 {

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

	List<State> state_list = new ArrayList<State>();

	@PostConstruct
	public void init() {
		try {
			state_list = new ArrayList<State>();
			l_state_map = new HashMap<Long, State>();
			StateDao stateDao = (StateDao) appContext.getContext().getBean(
					"stateDao");
			state_list = stateDao.findAll();
			Long key;
			for (State wa_s : state_list) {
				key = wa_s.getIdstate();
				l_state_map.put(key, wa_s);
			}
		} catch (Exception ex) {
			System.out.println("State F4 not loaded");
			throw new DAOException("State F4 not loaded");
		}

	}

	public void updateF4() {
		try {
			state_list = new ArrayList<State>();
			l_state_map = new HashMap<Long, State>();
			StateDao stateDao = (StateDao) appContext.getContext().getBean(
					"stateDao");
			state_list = stateDao.findAll();
			Long key;
			for (State wa_s : state_list) {
				key = wa_s.getIdstate();
				l_state_map.put(key, wa_s);
			}
		} catch (Exception ex) {
			System.out.println("State F4 not loaded");
			throw new DAOException("State F4 not loaded");
		}
	}

	public List<State> getState_list() {
		return state_list;
	}

	Map<Long, State> l_state_map = new HashMap<Long, State>();

	public Map<Long, State> getL_state_map() {
		return l_state_map;
	}

	public String getName(Long id) {
		return l_state_map.get(id) == null ? null : l_state_map.get(id)
				.getStatename();
	}
}
