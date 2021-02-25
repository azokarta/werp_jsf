package hr.customer;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.AddressDao;
import general.dao.DAOException;
import general.services.AddressService;
import general.tables.Address;
import general.tables.Customer;
import user.User;

@ManagedBean(name = "hrCustomerAddresses")
@ViewScoped
public class HrCustomerAddresses implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2429609853454282379L;

	public void initBean(Customer customer) {
		this.customer = customer;
		loadItems();
	}

	private List<Address> items;

	private void loadItems() {
		AddressDao d = (AddressDao) appContext.getContext().getBean("addressDao");
		items = d.findAllByCustomerId(customer.getId());
	}

	public List<Address> getItems() {
		return items;
	}

	public void setItems(List<Address> items) {
		this.items = items;
	}

	Customer customer;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	private Address selected;

	public Address getSelected() {
		return selected;
	}

	public void setSelected(Address selected) {
		this.selected = selected;
		this.isNew = false;
	}

	private boolean isNew = false;

	public void prepareCreate() {
		selected = new Address();
		if (customer != null) {
			selected.setCustomerId(customer.getId());
		}
		isNew = true;
	}

	public void Save() {
		try {
			AddressService ser = appContext.getContext().getBean("addressService", AddressService.class);
			if (isNew) {
				ser.createAddress(selected, userData.getUserid(), "");
			} else {
				PermissionController.canWrite(userData, 537L);
				ser.updateAddress(selected, userData.getUserid(), "");
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("AddressCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void Reset() {
		this.selected = null;
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

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

}
