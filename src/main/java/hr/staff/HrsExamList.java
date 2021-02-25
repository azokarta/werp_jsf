package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.dao.DAOException;
import general.dao.StaffExamDao;
import general.services.StaffExamService;
import general.tables.Staff;
import general.tables.StaffExam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name="hrsExamListBean")
@ViewScoped
public class HrsExamList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<StaffExam> items = new ArrayList<StaffExam>();
	public List<StaffExam> getItems() {
		items.clear();
		if(selectedStaff != null){
			StaffExamDao d = (StaffExamDao)appContext.getContext().getBean("staffExamDao");
			items = d.findAll(" staff_id = " + selectedStaff.getStaff_id());
		}
		return items;
	}
	
	private Staff selectedStaff;
	public Staff getSelectedStaff() {
		return selectedStaff;
	}
	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}
	
	private StaffExam selected;
	public StaffExam getSelected() {
		return selected;
	}
	public void setSelected(StaffExam selected) {
		this.selected = selected;
	}
	
	public StaffExam prepareCreate(){
		selected = new StaffExam();
		return selected;
	}
	
	private List<StaffExam> creationList = new ArrayList<StaffExam>();
	public List<StaffExam> getCreationList() {
		return creationList;
	}
	public void setCreationList(List<StaffExam> creationList) {
		this.creationList = creationList;
	}
	
	public void prepareCreationList(){
		creationList.clear();
		String[] titles = {
				"Пред. беседа","Демоға кіру","Ихтияж-Потребность","Аппарат таныстыру","Саудаға кіру","Өзіне сену"
		};
		for(int i = 0; i < 6; i++){
			StaffExam temp = new StaffExam();
			temp.setCreated_by(userData.getUserid());
			temp.setCreated_date(Calendar.getInstance().getTime());
			temp.setExam_date(Calendar.getInstance().getTime());
			temp.setGrade(0D);
			temp.setStaff_id(selectedStaff.getStaff_id());
			temp.setTitle(titles[i]);
			creationList.add(temp);
		}
	}
	
	public void addCreationList(){
		
	}
	
	public String getListDialogHeader(){
		if(selectedStaff != null){
			return "Экзамен (" + selectedStaff.getLF() + ") ";
		}
		return "Экзамен";
	}
	
	public void Create(){
		try{
			StaffExamService s = (StaffExamService)appContext.getContext().getBean("staffExamService");
			s.create(creationList);
			GeneralUtil.addSuccessMessage(new MessageProvider().getValue("success.changes_saved_success"));
			GeneralUtil.hideDialog("ExamCreateDialog");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void Reset(){
		
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;
	public void setUserData(User userData) {
		this.userData = userData;
	}
	

}
