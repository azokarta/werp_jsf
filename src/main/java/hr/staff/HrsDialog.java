package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.dao.StaffDao;
import general.tables.Staff;
import general.tables.search.StaffSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="hrsDialogBean")
@ViewScoped
public class HrsDialog implements Serializable{
	
	private static final long serialVersionUID = -5786483242987245507L;
	
	private Staff selected = null;
	private List<Staff> staffList = null;
	private StaffSearch staffSearch = new StaffSearch();
	
	public StaffSearch getStaffSearch() {
		return staffSearch;
	}
	public void setStaffSearch(StaffSearch staffSearch) {
		this.staffSearch = staffSearch;
	}
	public Staff getSelected() {
		return selected;
	}
	public void setSelected(Staff selected) {
		this.selected = selected;
	}
	
	public void setSelected(Long staffId){
		if(staffId != null && staffId != 0){
			StaffDao d = (StaffDao)appContext.getContext().getBean("staffDao");
			this.selected = d.find(staffId);
		}
	}
	
	public List<Staff> getStaffList() {
		//this.Reset();
		if(staffList == null){
			loadStaffList();
		}
		return staffList;
	}
	
	private void loadStaffList(){
		StaffDao d = (StaffDao)appContext.getContext().getBean("staffDao");
		String condition = staffSearch.getCondition();
		condition += (condition.length() > 0 ? " AND " : " ") + " position_id != 1 ";
		this.staffList = d.findAll(condition);
	}
	
	public void Search(){
		this.selected = new Staff();
		loadStaffList();
	}
	
	public void Ok(){
		if(this.selected != null){
			GeneralUtil.hideDialog("StaffListDialog");
		}
	}
	
	public void Cancel(){
		this.selected = new Staff();
	}
	
	public void Reset(){
		this.selected = new Staff();
	}
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}