package hr.staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Staff;
import user.User;

@ManagedBean(name = "hrsWerksBean")
@ViewScoped
public class HrsWerks implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			MatnrDao mDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
			matnrMap = mDao.getMappedList("");
		}
	}

	private Map<Long, Matnr> matnrMap = new HashMap<>();

	private boolean canRead = true;
	private Staff staff;

	public boolean isCanRead() {
		return canRead;
	}

	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public void initBean(Staff staff) {
		if (!GeneralUtil.isAjaxRequest()) {
			this.staff = staff;
			loadItems();
		}
	}

	private List<MatnrList> items = new ArrayList<>();

	public void loadItems() {
		items = new ArrayList<>();
		if (staff != null) {
			MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean("matnrListDao");
			items = mlDao.findStaffAllMatnrList(staff.getStaff_id());
		}
	}

	public List<MatnrList> getItems() {
		return items;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
