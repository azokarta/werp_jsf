package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.dao.CourseDao;
import general.dao.DAOException;
import general.dao.StaffCourseDao;
import general.services.StaffCourseService;
import general.tables.Course;
import general.tables.Staff;
import general.tables.StaffCourse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "hrsCourseListBean")
@ViewScoped
public class HrsCourseList implements Serializable {

	private StaffCourse selected;
	private Staff selectedStaff;
	private List<StaffCourse> staffCourseList = new ArrayList<StaffCourse>(); // Обучения
																				// сотрудника
	private List<Course> courseList; // Список обучения

	public StaffCourse getSelected() {
		return selected;
	}

	public void setSelected(StaffCourse selected) {
		this.selected = selected;
	}

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public List<Course> getCourseList() {
		this.setCourseList();
		return courseList;
	}

	public void setCourseList() {
		if (this.courseList == null) {
			CourseDao d = (CourseDao) appContext.getContext().getBean(
					"courseDao");
			this.courseList = d.findAll();
		}
	}

	public List<StaffCourse> getStaffCourseList() {
		staffCourseList = new ArrayList<StaffCourse>();
		if (this.selectedStaff != null) {
			StaffCourseDao sDao = (StaffCourseDao) appContext.getContext()
					.getBean("staffCourseDao");
			this.staffCourseList = sDao.findAllByStaffId(this.selectedStaff
					.getStaff_id());
		}

		return staffCourseList;
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

	private String listDialogHeader;

	public String getListDialogHeader() {
		this.listDialogHeader = new MessageProvider().getValue("hr.staff.staff_courses");
		if (this.selectedStaff != null) {
			return this.listDialogHeader + " (" + this.selectedStaff.getLF() + ")";
		}
		return listDialogHeader;
	}

	public String getCourseName(Long courseId) {
		for (Course c : this.getCourseList()) {
			if (c.getC_id() == courseId) {
				return c.getName_ru();
			}
		}
		return "";
	}

	public StaffCourse prepareCreate() {
		this.selected = new StaffCourse();
		return this.selected;
	}

	public void Create() {
		try {
			StaffCourseService service = (StaffCourseService) appContext
					.getContext().getBean("staffCourseService");
			this.selected.setStaff_id(this.selectedStaff.getStaff_id());
			this.selected.setCreated_by(userData.getUserid());
			this.selected.setUpdated_by(userData.getUserid());
			service.create(this.selected);
			GeneralUtil.addSuccessMessage("Course Added Successfully!");
			GeneralUtil.hideDialog("CourseCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void Reset() {
		this.selected = null;
	}
	
	public void assignStaff(Staff stf){
		if(stf != null){
			this.selected.setEducator_id(stf.getStaff_id());
		}
	}
	
	public void Delete(StaffCourse crs){
		StaffCourseDao d = (StaffCourseDao)appContext.getContext().getBean("staffCourseDao");
		d.delete(crs);
	}
}
