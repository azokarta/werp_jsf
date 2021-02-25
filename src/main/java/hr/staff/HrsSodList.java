package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.dao.DAOException;
import general.dao.StaffOfficialDataDao;
import general.services.StaffOfficialDataService;
import general.tables.Staff;
import general.tables.StaffOfficialData;
import general.tables.search.StaffOfficialDataSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name="hrsSodListBean")
@ViewScoped
public class HrsSodList implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private StaffOfficialDataSearch searchModel = new StaffOfficialDataSearch();
	private List<StaffOfficialData> items;
	private StaffOfficialData selected;
	private String listDialogHeader = "";
	
	public String getListDialogHeader() {
		return listDialogHeader;
	}


	private Staff selectedStaff;
	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
		this.listDialogHeader = (new MessageProvider().getValue("hr.official_data.staff_off_data")) + " (" + selectedStaff.getLF() + ")";
		this.searchModel.setStaff_id(selectedStaff.getStaff_id());
	}

	public StaffOfficialData getSelected() {
		return selected;
	}

	public void setSelected(StaffOfficialData selected) {
		this.selected = selected;
	}

	public List<StaffOfficialData> getItems() {
		StaffOfficialDataDao d = (StaffOfficialDataDao)appContext.getContext().getBean("staffOfficialDataDao");
		this.items = d.findAll(searchModel.getCondition());
		return items;
	}
	
	public void Search(){
		
	}
	
	public void Create(){
		try{
			if(this.selectedStaff == null){
				throw new DAOException("selected Staff is null");
			}
			StaffOfficialDataService service = (StaffOfficialDataService)appContext.getContext().getBean("staffOfficialDataService");
			this.selected.setCreated_by(userData.getUserid());
			this.selected.setUpdated_by(userData.getUserid());
			this.selected.setStaff_id(this.selectedStaff.getStaff_id());
			service.create(this.selected);
			GeneralUtil.addSuccessMessage("Данные добавлены успешно");
			GeneralUtil.hideDialog("SodCreateDialog");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void Update(){
		try{
			if(this.selectedStaff == null){
				throw new DAOException("selected Staff is null");
			}
			
			if(this.selected == null){
				throw new DAOException("selected is null");
			}
			
			StaffOfficialDataService service = (StaffOfficialDataService)appContext.getContext().getBean("staffOfficialDataService");
			this.selected.setUpdated_by(userData.getUserid());
			service.update(this.selected);
			GeneralUtil.addSuccessMessage("Данные добавлены успешно");
			GeneralUtil.hideDialog("SodUpdateDialog");
			
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public StaffOfficialData prepareCreate(){
		this.selected = new StaffOfficialData();
		return this.selected;
	}
	
	public void Reset(){
		
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
