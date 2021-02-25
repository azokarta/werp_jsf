package general.beans;

import general.AppContext;
import general.dao.PayrollDao;
import general.tables.Payroll;
import general.tables.search.PayrollSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="payrollBean")
@ViewScoped
public class PayrollBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8190297999402699984L;
	/**
	 * 
	 */
	private List<Payroll> items;
	private Payroll selected;
	private PayrollSearch searchModel = new PayrollSearch();
	
	public List<Payroll> getItems() {
		PayrollDao d = (PayrollDao)appContext.getContext().getBean("payrollDao");
		this.items = d.findAll(this.searchModel.getCondition());
		return items;
	}
	public void setItems(List<Payroll> items) {
		this.items = items;
	}
	
	public Payroll getSelected() {
		return selected;
	}
	public void setSelected(Payroll selected) {
		this.selected = selected;
	}
	
	public PayrollSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(PayrollSearch searchModel) {
		this.searchModel = searchModel;
	}



	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}