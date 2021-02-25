package logistics.order;

import general.AppContext;
import general.dao.CustomerDao;
import general.dao.OrderOutDao;
import general.tables.Customer;
import general.tables.OrderOut;
import general.tables.search.OrderOutSearch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="looutListBean")
@ViewScoped
public class LooutList implements Serializable{

	/**
	 * 
	 */
	
	@PostConstruct
	public void init(){
		loadItems();
	}
	
	
	private static final long serialVersionUID = 1L;
	private List<OrderOut> items;
	private OrderOutSearch searchModel = new OrderOutSearch();
	public OrderOutSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(OrderOutSearch searchModel) {
		this.searchModel = searchModel;
	}
	public List<OrderOut> getItems() {
		return items;
	}
	
	public void loadItems(){
		OrderOutDao d = (OrderOutDao)appContext.getContext().getBean("orderOutDao");
		items = d.findAll(this.searchModel.getCondition());
		prepareOrders(items);
	}
	
	private void prepareOrders(List<OrderOut> l){
		CustomerDao d = (CustomerDao)appContext.getContext().getBean("customerDao");
		List<Customer> customerList = d.findAll("fiz_yur = 1");
		Map<Long, String> customersMap = new HashMap<Long, String>();
		for(int i = 0; i < customerList.size(); i++){
			customersMap.put(customerList.get(i).getId(), customerList.get(i).getName());
		}
		for(int k = 0; k < l.size(); k++){
			Long customerId = l.get(k).getCustomer_id();
			if(customersMap.containsKey(customerId)){
				l.get(k).setCustomerName(customersMap.get(customerId));
			}
		}
	}
	
	private OrderOut selected;	
	public OrderOut getSelected() {
		return selected;
	}

	public void setSelected(OrderOut selected) {
		this.selected = selected;
	}
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
