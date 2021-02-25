package reference;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.CityDao;
import general.dao.DAOException;
import general.services.CityService;
import general.tables.City;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "refCity")
@ViewScoped
public class RefCity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<City> items = new ArrayList<City>();

	public List<City> getItemsByStateId(Long stateId) {
		this.items.clear();
		if (stateId != null) {
			CityDao d = (CityDao) appContext.getContext().getBean("cityDao");
			this.items = d.findAll(" stateid = " + stateId);
		}
		System.out.println("TEMP");
		return items;
	}

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			loadItems();
		}
	}

	private Long countryId;
	private Long stateId;
	private String name;
	
	

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<City> getItems() {
		return items;
	}

	public void loadItems() {
		items = new ArrayList<City>();
		String cond = " 1 = 1 ";
		if (!Validation.isEmptyLong(countryId)) {
			cond += " AND countryid = " + countryId;
		}

		if (!Validation.isEmptyLong(stateId)) {
			cond += (cond.length() > 0 ? " AND " : "") + " stateid = "
					+ stateId;
		}

		if (!Validation.isEmptyString(name)) {
			cond += (cond.length() > 0 ? " AND " : "") + " name LIKE '%" + name
					+ "%' ";
		}

		CityDao d = (CityDao) appContext.getContext().getBean("cityDao");
		items = d.findAll(cond);
	}

	private String pageHeader = "Список городов";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}
	
	private City selected;
	

	public City getSelected() {
		return selected;
	}

	public void setSelected(City selected) {
		this.selected = selected;
	}
	
	public void prepareCreate(){
		selected = new City();
		isNew = true;
	}
	
	public void Reset(){
		selected = null;
	}
	
	private boolean isNew = false;
	
	public void Save(){
		
		try{
			CityService s = (CityService)appContext.getContext().getBean("cityService");
			if(isNew){
				selected.setIdcity(null);
				s.create(selected);
			}else{
				s.update(selected);
			}
			GeneralUtil.addSuccessMessage("Успешно сохранено!");
			GeneralUtil.hideDialog("CityCreateDialog");
			
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
