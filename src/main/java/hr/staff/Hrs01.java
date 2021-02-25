package hr.staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import user.User;
import user.UserRoleActions;
import f4.BranchF4;
import f4.BukrsF4;
import f4.BusinessAreaF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.services.StaffService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Position;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.SubCompany;
import general.dao.DAOException;
import general.dao.SubCompanyDao;

@ManagedBean(name = "hrs01Bean")
@ViewScoped
public class Hrs01 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "HRS01";
	private final static Long transactionId = (long) 11;
	private Staff newStaff = new Staff();
	private Salary newSalary = new Salary();

	public Staff getNewStaff() {
		return newStaff;
	}

	public void setNewStaff(Staff newStaff) {
		this.newStaff = newStaff;
	}

	public Salary getNewSalary() {
		return newSalary;
	}

	public void setNewSalary(Salary newSalary) {
		this.newSalary = newSalary;
	}

	@PostConstruct
	public void init() {
		try {
			PermissionController.canWrite(userData, transactionId);

		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}

	}

	public void Save() {
		try {
			PermissionController.canWrite(userData, transactionId);
			StaffService staffService = (StaffService) appContext.getContext()
					.getBean("staffService");
			getNewStaff().setCreated_by(userData.getUserid());
			getNewStaff().setUpdated_by(userData.getUserid());
			newSalary.setCreated_by(userData.getUserid());
			newSalary.setUpdated_by(userData.getUserid());

			/*
			 * for(Branch b:p_branchF4Bean.getBranch_list()){
			 * if(b.getBranch_id().longValue() ==
			 * newSalary.getBranch_id().longValue()){
			 * newSalary.setCountry_id(b.getCountry_id()); break; } }
			 */
			staffService.createStaff(getNewStaff(), newSalary);
			newSalary = new Salary();
			setNewStaff(new Staff());
			GeneralUtil.addSuccessMessage("Сотрудник добавлен успешно");
		} catch (DAOException ex) {
			newSalary.setSalary_id(null);
			newStaff.setStaff_id(null);
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

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

}
