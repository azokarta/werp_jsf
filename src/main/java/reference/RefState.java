package reference;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.StateDao;
import general.services.StateService;
import general.tables.State;
import general.tables.search.StateSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "refState")
@ViewScoped
public class RefState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2101215937283900876L;

	private List<State> items = null;

	public List<State> getItemsByCountryId(Long countryId) {
		this.items = new ArrayList<State>();
		if (countryId != null) {
			StateDao d = (StateDao) appContext.getContext().getBean("stateDao");
			this.items = d.findAll(" countryid = " + countryId);
		}

		return items;
	}

	private void loadItems() {
		StateDao d = (StateDao) appContext.getContext().getBean("stateDao");
		items = d.findAll(searchModel.getCondition());
	}

	public List<State> getItems() {
		if (items == null) {
			StateDao d = (StateDao) appContext.getContext().getBean("stateDao");
			items = d.findAll();
		}

		return items;
	}

	private State selected;

	public State getSelected() {
		return selected;
	}

	public void setSelected(State selected) {
		this.selected = selected;
	}

	public State prepareCreate() {
		selected = new State();
		return selected;
	}

	public void Save() {
		try {
			if (selected != null
					&& !Validation.isEmptyLong(selected.getIdstate())) {
				Update();
			} else {
				Create();
			}
			loadItems();
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("StateCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		StateService s = (StateService) appContext.getContext().getBean(
				"stateService");
		s.create(selected);
	}

	private void Update() {
		StateService s = (StateService) appContext.getContext().getBean(
				"stateService");
		s.update(selected);
	}

	public void Search() {
		loadItems();
	}
	
	public void Reset(){
		
	}

	private StateSearch searchModel = new StateSearch();

	public StateSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(StateSearch searchModel) {
		this.searchModel = searchModel;
	}

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			loadItems();
		}
	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
