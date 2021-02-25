package general.beans;

import general.AppContext;
import general.dao.CustomerDao;
import general.tables.Customer;
import general.tables.search.CustomerSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class CustomerBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<Customer> items;
	private List<Customer> fizItems;
	private List<Customer> yurItems;
	private CustomerSearch searchModel = new CustomerSearch();
	private Customer selected;
	
	
	public CustomerSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(CustomerSearch searchModel) {
		this.searchModel = searchModel;
	}

	public Customer getSelected() {
		return selected;
	}

	public void setSelected(Customer selected) {
		this.selected = selected;
	}
	
	public void resetSelected(){
		this.selected = null;
	}

	public List<Customer> getItems() {
		CustomerDao d = (CustomerDao)appContext.getContext().getBean("customerDao");
		this.items = d.findAll(this.searchModel.getCondition());
		return items;
	}
	
	public List<Customer> getFizItems() {
		this.searchModel.setFiz_yur(2);
		this.fizItems = this.getItems();
		return fizItems;
	}

	public List<Customer> getYurItems() {
		this.searchModel.setFiz_yur(1);
		this.yurItems = this.getItems();
		return yurItems;
	}





	@ManagedProperty(value="#{appContext}")
	AppContext appContext;
	public AppContext getAppContext() {
		return appContext;
	}
	public void setAppContext(AppContext appContext) {
	  this.appContext = appContext;
	}
}
