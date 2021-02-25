package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.StaffOfficialDataDao;
import general.dao.SubCompanyDao;
import general.services.StaffOfficialDataService;
import general.tables.Staff;
import general.tables.StaffOfficialData;
import general.tables.SubCompany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.sun.scenario.effect.Flood;

import user.User;

@ManagedBean(name = "hrSodBean")
@ViewScoped
public class HrSod implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4184935623187464407L;
	private static final Long transactionId = 355L;

	@PostConstruct
	public void init() {
		loadSubCompanyMap();
	}

	private Map<Long, SubCompany> subCompanyMap = new HashMap<Long, SubCompany>();

	private void loadSubCompanyMap() {
		SubCompanyDao d = (SubCompanyDao) appContext.getContext().getBean("subCompanyDao");
		List<SubCompany> l = d.findAll();
		for (SubCompany sc : l) {
			subCompanyMap.put(sc.getSc_id(), sc);
		}
	}

	private Staff selectedStaff;
	private StaffOfficialData selected;
	private List<StaffOfficialData> items = new ArrayList<StaffOfficialData>();

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public StaffOfficialData getSelected() {
		return selected;
	}

	public void setSelected(StaffOfficialData selected) {
		this.selected = selected;
	}

	public List<StaffOfficialData> getItems() {
		return items;
	}

	public void loadItems() {
		items = new ArrayList<StaffOfficialData>();
		if (PermissionController.canView(userData, transactionId) && selectedStaff != null) {
			items = getDao().findAll(" staff_id = " + selectedStaff.getStaff_id());
			for (StaffOfficialData sod : items) {
				SubCompany sc = subCompanyMap.get(sod.getSub_company_id());
				if (sc == null) {
					sod.setSubCompany(new SubCompany());
				} else {
					sod.setSubCompany(sc);
				}
			}
		}
	}

	public boolean canRead() {
		return PermissionController.canView(userData, transactionId);
	}

	public boolean canCreate() {
		return PermissionController.canCreate(userData, transactionId);
	}
	
	public boolean canAll() {
		return PermissionController.canAll(userData, transactionId);
	}

	public void Save() {
		try {
			if (selected == null) {
				throw new DAOException("Selected Not Found Exception");
			}

			if (!PermissionController.canCreate(userData, transactionId)) {
				throw new DAOException("Нет доступа для создания или редактирования");
			}

			if (Validation.isEmptyLong(selected.getId())) {
				Create();
			} else {
				Update();
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно");
			GeneralUtil.hideDialog("SodCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		selected.setStaff_id(this.selectedStaff.getStaff_id());
		selected.setCreated_by(userData.getUserid());
		selected.setUpdated_by(userData.getUserid());
		getService().create(selected);
	}

	private void Update() {
		selected.setStaff_id(this.selectedStaff.getStaff_id());
		selected.setUpdated_by(userData.getUserid());
		getService().update(selected);
	}

	public void Delete(StaffOfficialData sod) {
		if (sod == null) {
			GeneralUtil.addErrorMessage("Элемент не выбран!");
		}
		sod.setIs_deleted(1);
		this.selected = sod;
		Update();
		GeneralUtil.addSuccessMessage("Элемент удален успешно!");
	}

	public void Reset() {

	}

	public StaffOfficialData prepareCreate() {
		selected = new StaffOfficialData();
		return selected;
	}

	private StaffOfficialDataService getService() {
		return (StaffOfficialDataService) appContext.getContext().getBean("staffOfficialDataService");
	}

	private StaffOfficialDataDao getDao() {
		return (StaffOfficialDataDao) appContext.getContext().getBean("staffOfficialDataDao");
	}

	//private final double MZP = 22853;
	//private final double MZP = 24459;
	//private final double MZP = 28284;
	private final double MZP = 42500;
	private final double minSalary = 63125;

	public void countSod() {
		if (selected != null) {
			if (selected.getSalary() > 0) {
				double sal = selected.getSalary();
				double tenPercent = (selected.getSalary() * 10) / 100;
				double twoPercent = (selected.getSalary() * 2) / 100;
				selected.setPension(tenPercent);
				selected.setIpn(Math.ceil((sal - tenPercent - MZP- twoPercent) * 10 / 100));
				if (selected.getIpn() < 0) {
					selected.setIpn(0);
				}
				
				if(sal < minSalary){
					selected.setIpn(selected.getIpn()-Math.ceil(selected.getIpn()*90/100));
				}
				
				selected.setSocial_contribution((sal - tenPercent) * 3.5 / 100);
				selected.setOsms(GeneralUtil.round(sal*0.02, 2));
				selected.setOsmsFromStaff(GeneralUtil.round(sal*0.02, 2));

			} else {
				selected.setPension(0D);
				selected.setIpn(0D);
				selected.setSocial_contribution(0D);
				selected.setOsms(0D);
			}
		}
	}

	@ManagedProperty("#{appContext}")
	AppContext appContext;

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
