package general.beans;

import general.AppContext;
import general.GeneralUtil;
import general.dao.CustomerDao;
import general.dao.MatnrPriceDao;
import general.tables.Customer;
import general.tables.MatnrPrice;
import general.tables.search.MatnrPriceSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.hibernate.annotations.common.util.StringHelper;


@ManagedBean(name="matnrPriceBean")
@ViewScoped
public class MatnrPriceBean implements Serializable{
	
	@PostConstruct
	public void init(){
		System.out.println("INIT....");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MatnrPrice> items;
	private MatnrPrice selected;
	private MatnrPriceSearch searchModel = new MatnrPriceSearch();
	
	private void loadItems(){
		MatnrPriceDao mpDao = (MatnrPriceDao)appContext.getContext().getBean("matnrPriceDao");
		this.items = mpDao.findAll(this.searchModel.getCondition());
	}
	
	public MatnrPrice getSelected() {
		return selected;
	}
	public void setSelected(MatnrPrice selected) {
		this.selected = selected;
	}
	public MatnrPriceSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(MatnrPriceSearch searchModel) {
		this.searchModel = searchModel;
	}
	public List<MatnrPrice> getItems() {
		if(items == null){
			loadItems();
			loadCustomerList();
		}
		return items;
	}
	
	private List<Customer> customerList = new ArrayList<Customer>();
	public String getCustomerName(Long custId){
		if(custId != null && custId > 0){
			for(Customer c:customerList){
				if(c.getId() == custId){
					return c.getName();
				}
			}
		}
		
		return "";
	}
	
	private void loadCustomerList(){
		CustomerDao d = (CustomerDao)appContext.getContext().getBean("customerDao");
		String[] ids = new String[items.size()];
		int counter = 0;
		for(MatnrPrice mp:items){
			ids[counter] = mp.getCustomer_id().toString();
			counter++;
		}
		if(ids.length > 0){
			String condition = String.format(" customer_id IN (%s) ", "'" + StringHelper.join("','", ids) + "'");
			customerList = d.findAll(condition);
			System.out.println("CUSTOMER SIZE: " + customerList.size());
		}else{
			customerList = new ArrayList<Customer>();
		}
		
	}
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
