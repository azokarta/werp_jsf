package hr.timesheet;

import f4.BukrsF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.SalaryDao;
import general.dao.StaffTimesheetDao;
import general.services.StaffTimesheetService;
import general.tables.Salary;
import general.tables.StaffTimesheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "hrTimesheetBean")
@ViewScoped
public class HrTimesheet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6836328379839963027L;

	@PostConstruct
	public void init() {
		dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		year = Calendar.getInstance().get(Calendar.YEAR);
		month = Calendar.getInstance().get(Calendar.MONTH);
	}

	private Integer dayOfMonth;
	private Integer year;
	private Integer month;
	private Long departmentId;
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	private String bukrs;
	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	List<StaffTimesheet> timeList = new ArrayList<StaffTimesheet>();

	public List<StaffTimesheet> getTimeList() {
		return timeList;
	}

	public void setTimeList(List<StaffTimesheet> timeList) {
		this.timeList = timeList;
	}

	public void generateTimesheet() {
		timeList.clear();
		SalaryDao salDao = (SalaryDao) appContext.getContext().getBean(
				"salaryDao");
		String cond = " 1 = 1 ";
		if (!Validation.isEmptyString(bukrs)) {
			cond += " AND s1.bukrs = '" + bukrs + "' ";
		}

		if (!Validation.isEmptyLong(departmentId)) {
			cond += " AND s1.department_id = " + departmentId;
		}

		List<Salary> salList = salDao.findAllCurrentWithStaff(cond);
		String[] ids = new String[salList.size()];
		for (int i = 0; i < salList.size(); i++) {
			ids[i] = salList.get(i).getStaff_id().toString();
		}

		if (salList.size() > 0) {
			StaffTimesheetDao stfDao = (StaffTimesheetDao) appContext
					.getContext().getBean("staffTimesheetDao");
			String cond1 = String.format(
					" s2.staff_id IN(%s) ", "'" + String.join("','", ids) + "'");
			cond1 += String.format(" AND s1.year = %d AND s1.month = %d AND bukrs='%s' ", year,month,bukrs);
			List<StaffTimesheet> stfList = stfDao.findAll(cond1);

			Map<Long, StaffTimesheet> stfMap = new HashMap<Long, StaffTimesheet>();
			for (StaffTimesheet stf : stfList) {
				stfMap.put(stf.getStaff().getStaff_id(), stf);
			}
			
			for(Salary sal:salList){
				StaffTimesheet st = stfMap.get(sal.getStaff_id());
				if(st == null){
					st = new StaffTimesheet();
					st.setMonth(month);
					st.setYear(year);
					st.setStaff(sal.getP_staff());
					st.setCreatedBy(userData.getUserid());
					st.setUpdatedBy(userData.getUserid());
					st.setBukrs(bukrs);
					st.setBranchId(sal.getBranch_id());
				}
				
				
				timeList.add(st);
			}
		}

//		for (Salary s : salList) {
//			StaffTimesheet st = new StaffTimesheet();
//			st.setMonth(Calendar.getInstance().get(Calendar.MONTH));
//			st.setYear(Calendar.getInstance().get(Calendar.YEAR));
//			st.setStaff(s.getP_staff());
//			timeList.add(st);
//		}

		System.out.println("DAY_OF_M: "
				+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
	}

	public void Save() {
		try{
			StaffTimesheetService stfService = (StaffTimesheetService)appContext.getContext().getBean("staffTimesheetService");
			stfService.save(timeList);
			GeneralUtil.addSuccessMessage("Сохранено успешно");
			timeList = new ArrayList<StaffTimesheet>();
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
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
	public User getUserData() {
		return userData;
	}

	@ManagedProperty(value="#{bukrsF4Bean}")
	private BukrsF4 bukrsF4Bean;
	public BukrsF4 getBukrsF4Bean() {
		return bukrsF4Bean;
	}
	public void setBukrsF4Bean(BukrsF4 bukrsF4Bean) {
		this.bukrsF4Bean = bukrsF4Bean;
	}
	
}
