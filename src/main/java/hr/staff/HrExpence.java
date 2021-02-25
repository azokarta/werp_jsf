package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.ExpenceTypeDao;
import general.dao.StaffExpenceDao;
import general.services.StaffExpenceService;
import general.tables.ExpenceType;
import general.tables.Staff;
import general.tables.StaffExpence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import user.User;

@ManagedBean(name = "hrExpenceBean")
@ViewScoped
public class HrExpence implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7068352027985867077L;
	
	private static final Long transactionId = 354L;

	@PostConstruct
	public void init() {
		setCanRead();
		loadExpenceTypeList();
	}
	
	public void initBean(Staff stf){
		this.selectedStaff = stf;
		loadItems();
		System.out.println(this.getClass().getName() + " init...");
	}

	private Staff selectedStaff;
	private StaffExpence selected;
	private List<StaffExpence> items;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public StaffExpence getSelected() {
		return selected;
	}

	public void setSelected(StaffExpence selected) {
		this.selected = selected;
	}

	public List<StaffExpence> getItems() {
		return items;
	}

	public void loadItems() {
		if (expenceTypeList == null) {
			//loadExpenceTypeList();
		}
		items = new ArrayList<>();
		if (PermissionController.canView(userData, transactionId) && selectedStaff != null) {
			items = getExpenceDao().findAllByStaffId(
					selectedStaff.getStaff_id());

			Map<Long, ExpenceType> eMap = new HashMap<Long, ExpenceType>();
			for (ExpenceType et : expenceTypeList) {
				eMap.put(et.getEt_id(), et);
			}

			for (StaffExpence se : items) {
				if (eMap.containsKey(se.getEt_id())) {
					se.setTypeName(eMap.get(se.getEt_id()).getName());
				}
			}
		}
	}
	private boolean canRead;
	public boolean getCanRead(){
		return canRead;
	}
	
	public void setCanRead(){
		canRead = PermissionController.canView(userData, transactionId);
	}
	
	public boolean canCreate(){
		return PermissionController.canCreate(userData, transactionId);
	}

	/************ CRUD ********************/

	public void Save() {
		try {
			if (selected == null) {
				throw new DAOException("Selected Not Found Exception");
			}

			if (Validation.isEmptyLong(selected.getSe_id())) {
				Create();
			} else {
				Update();
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно");
			GeneralUtil.hideDialog("ExpenceCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() { 
		selected.setStaff_id(this.selectedStaff.getStaff_id());
		selected.setCreated_by(userData.getUserid());
		selected.setUpdated_by(userData.getUserid());
		getExpenceService().create(selected);
	}

	private void Update() {

	}
	
	public void Delete(StaffExpence se){
		try{
			getExpenceService().delete(se);
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public StaffExpence prepareCreate() {
		selected = new StaffExpence();
		return selected;
	}

	public void Reset() {
		selected = null;
	}

	List<ExpenceType> expenceTypeList;

	public List<ExpenceType> getExpenceTypeList() {
		return expenceTypeList;
	}

	private void loadExpenceTypeList() {
		ExpenceTypeDao etd = (ExpenceTypeDao) appContext.getContext()
				.getBean("expenceTypeDao");
		expenceTypeList = etd.findAll();
	}

	/***********************************/
	
	private StaffExpenceService getExpenceService(){
		return (StaffExpenceService) appContext
				.getContext().getBean("staffExpenceService");
	}
	private StaffExpenceDao getExpenceDao() {
		return (StaffExpenceDao) appContext.getContext().getBean(
				"staffExpenceDao");
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
