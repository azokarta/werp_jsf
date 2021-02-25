package hr.staff;

import general.AppContext;
import general.dao.DepositDao;
import general.tables.Deposit;
import general.tables.Staff;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "hrDepositBean")
@ViewScoped
public class HrDeposit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8355204082009327030L;

	@PostConstruct
	public void init() {

	}

	private Staff selectedStaff;
	private List<Deposit> items;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public List<Deposit> getItems() {
		return items;
	}

	public void loadItems() {
		if (selectedStaff != null) {
			DepositDao d = (DepositDao) appContext.getContext().getBean(
					"depositDao");
			items = d.dynamicFind("staff_id = " + selectedStaff.getStaff_id());
		}
	}

	/**********************************************/
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
