package hr.pyramid;

import f4.BranchF4;
import f4.BukrsF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.dao.StaffDao;
import general.services.PyramidService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Pyramid;
import general.tables.Staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name="hrpCreate")
@ViewScoped
public class HrpCreate implements Serializable
{
	private final static String transaction_code = "HRP01";
	private final static Long transaction_id = (long) 28;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2520314901376102372L;
	private String bukrs = "";
	private Pyramid newPyramid = new Pyramid();
	public List<PyramidTypeClass> getPyramidTypes() {
		return pyramidTypes;
	}
	public Long getParentPyramidId() {
		return parentPyramidId;
	}
	public Long getParentPositionId() {
		return parentPositionId;
	}
	public List<Staff> getStaffList() {
		return staffList;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public Pyramid getNewPyramid() {
		return newPyramid;
	}
	public void setNewPyramid(Pyramid newPyramid) {
		this.newPyramid = newPyramid;
	}
	
	public void Create(){
		try{
			PermissionController.canWrite(userData, transaction_id);
			PyramidService pService = (PyramidService)appContext.getContext().getBean("pyramidService");
			if(newPyramid.getPyramid_id() == 0L){
				newPyramid.setCreated_by(Long.valueOf(userData.getUserid()));
			}
			
			newPyramid.setUpdated_by(Long.valueOf(userData.getUserid()));
			newPyramid.setParent_pyramid_id(this.parentPyramidId);
			
			if(this.selectedStaff != null){
				newPyramid.setPosition_id(selectedStaff.getPosition_id());
			}
			
			if(newPyramid.getPyramid_id() == null){
				pService.createPyramid(newPyramid);
			}else{
				pService.updatePyramid(newPyramid);
			}
			newPyramid = new Pyramid();
			selectedStaff = new Staff();
			GeneralUtil.addSuccessMessage("Success");
			RequestContext.getCurrentInstance().execute("PF('pyramidWidget').hide();");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void Reset(){
		this.newPyramid = null;
	}
	
	@PostConstruct
	public void init(){
		try{
			PermissionController.canWrite(userData, transaction_id);
			this.prepareTypes();
			if (userData.getBukrs().equals("0001")) {
				for (Bukrs b : bukrsF4Bean.getBukrs_list()) {
					if (!b.getBukrs().equals("0001")) {
						bukrsList.add(b);
					}
				}
			} else {
				for (Bukrs b : bukrsF4Bean.getBukrs_list()) {
					if (userData.getBukrs().equals(b.getBukrs())) {
						bukrsList.add(b);
					}
				}
			}
		}catch(DAOException e){
			
		}
	}
	
	List<PyramidTypeClass> pyramidTypes = new ArrayList<HrpCreate.PyramidTypeClass>();
	private void prepareTypes(){
		PyramidTypeClass p1 = new PyramidTypeClass();
		p1.setKey("region");
		p1.setValue("Регион");
		this.pyramidTypes.add(p1);
		PyramidTypeClass p2 = new PyramidTypeClass();
		p2.setKey("branch");
		p2.setValue("Филиал");
		this.pyramidTypes.add(p2);
		PyramidTypeClass p3 = new PyramidTypeClass();
		p3.setKey("staff");
		p3.setValue("Сотрудник");
		this.pyramidTypes.add(p3);
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

	public class PyramidTypeClass{
		private String key;
		private String value;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	Long parentPyramidId;
	Long parentPositionId;
	List<Staff> staffList = new ArrayList<Staff>();
	
	public void preparePyramidForAdd() {
		staffList = new ArrayList<Staff>();
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		this.parentPyramidId = Long.valueOf(params.get("parentPyramidId"));
		this.newPyramid = new Pyramid();
		//this.parentPositionId = Long.valueOf(params.get("parentPositionId"));
		/*
		 * try{ this.loadStaffList(); }catch(DAOException e){
		 * MessageController.getInstance().addError(e.getMessage()); }
		 */
	}
	
	private String selectedStaffFio = "";
	public String getSelectedStaffFio() {
		return selectedStaffFio;
	}
	public void setSelectedStaffFio(String selectedStaffFio) {
		this.selectedStaffFio = selectedStaffFio;
	}
	
	private Staff searchStaff = new Staff();
	public Staff getSearchStaff() {
		return searchStaff;
	}
	public void setSearchStaff(Staff searchStaff) {
		this.searchStaff = searchStaff;
	}
	
	private void loadStaffList() throws DAOException {
		staffList = new ArrayList<Staff>();
		StaffDao stfDao = (StaffDao) appContext.getContext()
				.getBean("staffDao");
		String q = "";
		if (searchStaff.getFirstname().length() > 0) {
			q += String.format(" stf.firstname LIKE '%s' ",
					"%" + searchStaff.getFirstname() + "%");
		}

		if (searchStaff.getMiddlename().length() > 0) {
			q = q.length() > 0 ? q + " AND " : q;
			q += String.format(" stf.middlename LIKE '%s' ",
					"%" + searchStaff.getMiddlename() + "%");
		}

		if (searchStaff.getLastname().length() > 0) {
			q = q.length() > 0 ? q + " AND " : q;
			q += String.format(" stf.lastname LIKE '%s' ",
					"%" + searchStaff.getLastname() + "%");
		}

		if (q.length() == 0) {
			// throw new DAOException("Заполните поля для поиска");
		}

		/*String cond = this.getStaffSearchCondition();
		if (cond.length() == 0) {
			throw new DAOException("Error parent position");
		}
		q = (q.length() > 0 ? q + " AND " : q) + cond;*/
		staffList = stfDao.dynamicFindStaffSalary(q.length() > 0 ? " AND " + q : "");
	}
	
	public void doSearchStaff() {
		try {
			this.loadStaffList();
		} catch (DAOException ex) {
			staffList = new ArrayList<Staff>();
			MessageController.getInstance().addError(ex.getMessage());
		}
	}
	
	private Staff selectedStaff = new Staff();

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}
	
	public void assignFoundEmployee() {
		if(selectedStaff !=null && selectedStaff.getStaff_id() != null && selectedStaff.getStaff_id() != 0L)
		{
			this.newPyramid.setStaff_id(selectedStaff.getStaff_id());
			this.setSelectedStaffFio(Validation.returnFio(selectedStaff.getFirstname(), selectedStaff.getLastname(),selectedStaff.getMiddlename()));
		}
	}
	
	public void removeSelectedStaff(){
		this.selectedStaff = new Staff();
		this.selectedStaffFio = "";
		this.newPyramid.setStaff_id(0L);
	}
	
	List<Bukrs> bukrsList = new ArrayList<Bukrs>();

	public List<Bukrs> getBukrsList() {
		return bukrsList;
	}

	List<Branch> branchList = new ArrayList<Branch>();

	public List<Branch> getBranchList() {
		return branchList;
	}
	
	public void loadBranch() {
		branchList = new ArrayList<Branch>();
		for (Branch b : branchF4Bean.getBranch_list()) {
			if (b.getBukrs().equals(newPyramid.getBukrs())) {
				branchList.add(b);
			}
		}
	}
	
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 branchF4Bean;

	public BranchF4 getBranchF4Bean() {
		return branchF4Bean;
	}

	public void setBranchF4Bean(BranchF4 b) {
		this.branchF4Bean = b;
	}
	
	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 bukrsF4Bean;

	public BukrsF4 getBukrsF4Bean() {
		return bukrsF4Bean;
	}

	public void setBukrsF4Bean(BukrsF4 b) {
		this.bukrsF4Bean = b;
	}
	
	Long pyramidIdForDelete = 0L;

	public void setPyramidIdForDelete() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		this.pyramidIdForDelete = Long
				.valueOf(params.get("pyramidIdForDelete"));
	}

	public void delete() {
		try {
			PyramidService pService = (PyramidService) appContext.getContext()
					.getBean("pyramidService");
			pService.deletePyramid(pyramidIdForDelete);
			//this.preparePyramidList();
			MessageController.getInstance()
					.addInfo(
							"Элемент успешно удален из иерархи "
									+ pyramidIdForDelete);
		} catch (Exception e) {
			MessageController.getInstance().addError(e.getMessage());
		}
	}
	
	public void load() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		Long id = Long
				.valueOf(params.get("id"));
		try{
			PyramidDao pDao = (PyramidDao)appContext.getContext().getBean("pyramidDao");
			newPyramid = pDao.find(id);
			if(newPyramid == null){
				throw new DAOException("P Not Found!");
			}
			if(newPyramid.getStaff_id() != null){
				StaffDao sDao = (StaffDao)appContext.getContext().getBean("staffDao");
				this.selectedStaff = sDao.find(newPyramid.getStaff_id());
				this.setSelectedStaffFio(Validation.returnFio(selectedStaff.getFirstname(), selectedStaff.getLastname(),selectedStaff.getMiddlename()));
			}
			
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
		
	}
}
