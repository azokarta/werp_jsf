package hr.staff;

import general.AppContext;
import general.dao.DepositDao;
import general.tables.Deposit;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="hrsDeposit")
@ViewScoped
public class HrsDeposit implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Deposit> items;
	public List<Deposit> getItems() {
		return items;
	}
	
	public List<Deposit> getItemsByStaffId(Long staffId) {
		DepositDao d = (DepositDao)appContext.getContext().getBean("depositDao");
		return d.dynamicFind("staff_id = " + staffId);
	}
	
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
