package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.dao.CourseDao;
import general.dao.DAOException;
import general.dao.StaffCourseDao;
import general.services.StaffCourseService;
import general.tables.Course;
import general.tables.StaffCourse;

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

@ManagedBean(name="hrsCourseBean")
@ViewScoped
public class HrsCourse implements Serializable
{
	private static final long serialVersionUID = 335373747343900229L;
	
	private StaffCourse newCourse = new StaffCourse();
	public StaffCourse getNewCourse() {
		return newCourse;
	}
	public void setNewCourse(StaffCourse newCourse) {
		this.newCourse = newCourse;
	}


	private List<Course> courseList = new ArrayList<Course>();
	public List<Course> getCourseList() {
		return courseList;
	}

	@PostConstruct
	public void init(){
		try{
			//Check Permission
			System.out.println(this.getClass() + " init... ");
			this.loadCourseList();
		}catch(DAOException e){
			
		}
	}
	
	private void loadCourseList(){
		CourseDao d = (CourseDao)appContext.getContext().getBean("courseDao");
		this.courseList = d.findAll();
	}
	
	
	
	public void setCourseStaff(Long staffId){
		this.newCourse.setStaff_id(staffId);
	}
	
	
	private List<StaffCourse> scList = new ArrayList<StaffCourse>();
	public List<StaffCourse> getScList() {
		return scList;
	}
	public void loadScList(Long staffId){
		StaffCourseDao d = (StaffCourseDao)appContext.getContext().getBean("staffCourseDao");
		this.scList = d.findAllByStaffId(staffId);
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
	
	private Long courseIdForDelete = 0L;
	public void setDeleteCourseId(Long courseId){
		/*Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		this.courseIdForDelete = Long.valueOf(params.get("courseIdForDelete"));*/
		this.courseIdForDelete = courseId;
		System.out.println("DELETE COURSE: " + this.courseIdForDelete);
	}
	
	public void create(){
		try{
			StaffCourseService service = (StaffCourseService)appContext.getContext().getBean("staffCourseService");
			this.newCourse.setCreated_by(Long.valueOf(userData.getUserid()));
			this.newCourse.setUpdated_by(Long.valueOf(userData.getUserid()));
			service.create(this.newCourse);
			GeneralUtil.addSuccessMessage("Course Added Successfully!");
			GeneralUtil.hideDialog("createCourseWidget");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void delete(StaffCourse sc){
		try{
			StaffCourseDao d = (StaffCourseDao)appContext.getContext().getBean("staffCourseDao");
			d.delete(sc.getSc_id());
			GeneralUtil.addSuccessMessage("Course Deleted");
			this.loadCourseList();
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
}