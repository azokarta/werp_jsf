package reference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.PermissionController;
import general.dao.BankDao;
import general.dao.CourseDao;
import general.dao.DAOException;
import general.services.CourseService;
import general.tables.Course;
import general.tables.search.CourseSearch;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import user.User;
@ManagedBean(name="refCourse",eager=true)
@ViewScoped
public class RefCourse extends RefBase<Course> implements Serializable
{
	private static final long serialVersionUID = 3743979661990801072L;
	private final static Long transactionId = 71L;

	private CourseSearch searchModel = new CourseSearch();	
	public CourseSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(CourseSearch searchModel) {
		this.searchModel = searchModel;
	}


	@PostConstruct
	public void init(){
		PermissionController.canRead(userData, transactionId);
		super.init();
		this.selectedRecord = new Course();
	}
	

	public void Create(){
		try{
			PermissionController.canWrite(userData, transactionId);
			CourseService cService = (CourseService)appContext.getContext().getBean("courseService");
			selectedRecord.setCreated_by(Long.valueOf(userData.getUserid()));
			selectedRecord.setUpdated_by(Long.valueOf(userData.getUserid()));
			cService.create(selectedRecord);
			selectedRecord = new Course();
			GeneralUtil.addSuccessMessage("Course Added Successfully");
			GeneralUtil.hideDialog("CourseCreateDialog");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void Update(){
	}
	
	public void delete() {
		System.out.println("DELETED");
	}
	
	@Override
	public List<Course> getRecordList() {
		CourseDao d = (CourseDao)appContext.getContext().getBean("courseDao");
		this.recordList = d.findAll("");
		return super.getRecordList();
	}


	@Override
	public void Search() {
		
	}


	@Override
	public Long getTransactionId() {
		return transactionId;
	}
	
	@Override
	public Course prepareCreate() {
		this.selectedRecord = new Course();
		return this.selectedRecord;
	}
	
	@Override
	public String getBreadcrumb() {
		return super.getBreadcrumb() + " > Список курсов";
	}
}
