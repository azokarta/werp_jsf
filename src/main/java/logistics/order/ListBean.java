package logistics.order;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.CustomerDao;
import general.dao.OrderDao;
import general.dao.UserDao;
import general.tables.Customer;
import general.tables.Order;
import general.tables.Staff;
import general.tables.search.OrderSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import datamodels.CustomerModel;
import user.User;

@ManagedBean(name = "logOrderListBean")
@ViewScoped
public class ListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7794165058098371919L;
	static final Long transactionId = 109L;
	static final String transactionCode = "LG_OR";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			searchModel = new OrderSearch();
			loadStaffMap();
			loadCustomerMap();
			loadOList();
			customerModel = new CustomerModel((CustomerDao) appContext.getContext().getBean("customerDao"));
			customerModel.getSearchModel().setFiz_yur(1);
		}
	}

	private Map<Long, Customer> cstMap;

	private void loadCustomerMap() {
		CustomerDao d = (CustomerDao) appContext.getContext().getBean("customerDao");
		cstMap = d.getMappedList(" fiz_yur = 1 ");
	}

	Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	private void loadStaffMap() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			stfMap.put(u.getUser_id(), u.getStaff());
			// System.out.println("SSS: " + u.getUsername());
		}
	}

	List<Order> oList = new ArrayList<Order>();

	public List<Order> getoList() {
		return oList;
	}

	public void loadOList() {
		OrderDao d = (OrderDao) appContext.getContext().getBean("orderDao");
		String cond = getSearchModel().getCondition();
		oList = d.findAll((cond.length() == 0 ? " 1 = 1 " : cond) + " ORDER BY id DESC ");
		for (Order o : oList) {
			Staff s = stfMap.get(o.getCreated_by());
			if (s == null) {
				o.setCreator(new Staff());
			} else {
				o.setCreator(s);
			}

			Customer c = cstMap.get(o.getCustomer_id());
			if (c == null) {
				o.setCustomer(new Customer());
			} else {
				o.setCustomer(c);
			}
		}
	}

	private OrderSearch searchModel;

	public OrderSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(OrderSearch searchModel) {
		this.searchModel = searchModel;
	}

	private CustomerModel customerModel;

	public CustomerModel getCustomerModel() {
		return customerModel;
	}

	public void setCustomerModel(CustomerModel customerModel) {
		this.customerModel = customerModel;
	}

	public void onSelectCustomer(SelectEvent e) {
		Customer c = (Customer) e.getObject();
		searchModel.setCustomer(c);
		searchModel.setCustomer_id(c.getId());
		GeneralUtil.updateFormElement("searchForm");
		GeneralUtil.hideDialog("CustomerListDialog");
	}

	public void removeCustomer() {
		searchModel.setCustomer(new Customer());
		searchModel.setCustomer_id(0L);
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
