package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.MessageProvider;
import general.dao.DAOException;
import general.dao.ExpenceTypeDao;
import general.dao.StaffExpenceDao;
import general.services.StaffExpenceService;
import general.tables.ExpenceType;
import general.tables.Staff;
import general.tables.StaffExpence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name="hrsExpenceListBean")
@ViewScoped
public class HrsExplenceList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1260565029764441604L;
	private Staff selectedStaff;
	private List<StaffExpence> staffExpenceList = new ArrayList<StaffExpence>();
	private List<ExpenceType> expenceTypeList = null;
	private StaffExpence selected;
	
	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public List<ExpenceType> getExpenceTypeList() {
		this.setExpenceTypeList();
		return expenceTypeList;
	}

	private void setExpenceTypeList() {
		if(this.expenceTypeList == null){
			ExpenceTypeDao eDao = (ExpenceTypeDao)appContext.getContext().getBean("expenceTypeDao");
			this.expenceTypeList = eDao.findAll();
		}
	}

	public StaffExpence getSelected() {
		return selected;
	}

	public void setSelected(StaffExpence selected) {
		this.selected = selected;
	}

	public List<StaffExpence> getStaffExpenceList() {
		if(this.selectedStaff != null){
			StaffExpenceDao sDao = (StaffExpenceDao)appContext.getContext().getBean("staffExpenceDao");
			this.staffExpenceList = sDao.findAllByStaffId(this.selectedStaff.getStaff_id());
		}
		return staffExpenceList;
	}
	
	private String listDialogHeader = "";
	public String getListDialogHeader(){
		this.listDialogHeader = new MessageProvider().getValue("hr.staff.staff_expence_list");
		if(this.selectedStaff != null){
			this.listDialogHeader += " (" + this.selectedStaff.getLastname() + " " + this.selectedStaff.getFirstname() + ")";
		}
		
		return this.listDialogHeader;
	}

	public String getExpenceType(Long etId){
		for(ExpenceType et:this.getExpenceTypeList()){
			if(et.getEt_id() == etId){
				return et.getName();
			}
		}
		return "";
	}
		
	public void Create(){
		try{
			StaffExpenceService service = (StaffExpenceService)appContext.getContext().getBean("staffExpenceService");
			this.selected.setStaff_id(this.selectedStaff.getStaff_id());
			this.selected.setCreated_by(userData.getUserid());
			this.selected.setUpdated_by(userData.getUserid());
			service.create(this.selected);
			GeneralUtil.addSuccessMessage("Expence Added Successfully!");
			GeneralUtil.hideDialog("ExpenceCreateDialog");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public StaffExpence prepareCreate(){
		this.selected = new StaffExpence();
		return this.selected;
	}
	
	public void Reset(){
		
	}
	
	public void Delete(StaffExpence se){
		if(se != null){
			StaffExpenceService service = (StaffExpenceService)appContext.getContext().getBean("staffExpenceService");
			service.delete(se);
		}
		
	}
	
	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	@ManagedProperty(value = "#{userinfo}")
	private User userData;
	public void setUserData(User userData) {
		this.userData = userData;
	}
	
}
