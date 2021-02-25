package hr.plan;

import general.AppContext;
import general.dao.SalePlanDao;
import general.tables.SalePlan;
import general.tables.search.SalePlanSearch;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class HrPlanBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<SalePlan> items;
	private SalePlanSearch searchModel = new SalePlanSearch();
	private SalePlan selected;
	
	public SalePlan getSelected() {
		return selected;
	}
	public void setSelected(SalePlan selected) {
		this.selected = selected;
	}
	public SalePlanSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(SalePlanSearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<SalePlan> getItems() {
		SalePlanDao d = (SalePlanDao)appContext.getContext().getBean("salePlanDao");
		this.items = d.findAll(searchModel.getCondition());
		return items;
	}

	@PostConstruct
	public void init(){
		//TODO PERMISSION
	}
	
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public void Search(){
		
	}
}
