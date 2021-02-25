package hr.customer;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.CustomerDao;
import general.tables.Customer;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import datamodels.CustomerModel;
import user.User;

@ManagedBean(name = "hrcListBean")
@ViewScoped
public class HrcList implements Serializable {

	private static final long serialVersionUID = -2976785156528901862L;
	private final static Long transactionId = 90L;
	private final static String transactionCode = "HRCLIST";

	@PostConstruct
	public void init() {
		PermissionController.canRead(userData, transactionId);
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}

		loadCustomerModel();
	}

	private CustomerModel customerModel;

	private void loadCustomerModel() {
		customerModel = new CustomerModel((CustomerDao) appContext.getContext()
				.getBean("customerDao"));
	}

	public CustomerModel getCustomerModel() {
		return customerModel;
	}

	private Customer selected;
	public Customer getSelected() {
		return selected;
	}

	public void setSelected(Customer selected) {
		this.selected = selected;
	}

	public void Search() {
		this.selected = null;
		// loadCustomerModel();
	}

	public void Create() {
		// CustomerService s =
		// (CustomerService)appContext.getContext().getBean("customerService");
		// s.createCustomer(a_customer);
	}

	public void Update() {

	}

	public Customer prepareCreate() {
		this.selected = new Customer();
		return this.selected;
	}

	public void exportXLS() {

	}

	public void Reset() {
		this.selected = new Customer();
	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
