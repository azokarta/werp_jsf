package hr.pyramid;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.services.PyramidService;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;
import hr.pyramid.HrpCreate.PyramidTypeClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "hrpCrud")
@ViewScoped
public class HrpCrud implements Serializable {

	private static final long serialVersionUID = 1L;
	private String mode = "create";
	private Pyramid selected;

	public Pyramid getSelected() {
		return selected;
	}

	public void setSelected(Pyramid p) {
		this.selected = p;
	}

	public void Create() {
		try {
			if (this.selected == null) {
				throw new DAOException("Selected Is Null");
			}
			PyramidService pService = (PyramidService) appContext.getContext()
					.getBean("pyramidService");
			this.selected.setCreated_by(userData.getUserid());
			this.selected.setUpdated_by(userData.getUserid());
			pService.createPyramid(this.selected);
			this.selected = null;
			GeneralUtil.addSuccessMessage("Pyramid Created Successfully!");
			GeneralUtil.hideDialog("PyramidCreateDialog");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}

	}

	public void Update() {
		try {
			if (this.selected == null) {
				throw new DAOException("Selected Is Null");
			}
			PyramidService pService = (PyramidService) appContext.getContext()
					.getBean("pyramidService");
			this.selected.setUpdated_by(userData.getUserid());
			pService.updatePyramid(this.selected);
			this.selected = null;
			GeneralUtil.addSuccessMessage("Pyramid Updated Successfully!");
			GeneralUtil.hideDialog("PyramidCreateDialog");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void Reset() {
		this.selected = null;
		resetSalary();
	}

	public void Delete(Long pyramidId) {
		try {
			PyramidDao d = (PyramidDao) appContext.getContext().getBean(
					"pyramidDao");
			PyramidService pService = (PyramidService) appContext.getContext()
					.getBean("pyramidService");
			pService.deletePyramid(pyramidId);
			GeneralUtil.addSuccessMessage("Pyramid Deleted!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public Pyramid prepareCreate(Long parentId) {
		System.out.println("Parent: " + parentId);
		this.selected = new Pyramid();
		this.selected.setParent_pyramid_id(parentId);
		this.setCreateMode();
		return this.selected;
	}

	public void removeSelectedStaff() {
		if (this.selected != null) {
			this.selected.setStaff_id(0L);
			this.selected.setBranch_id(0L);
			this.selected.setPosition_id(0L);
		}
	}

	public String selectedStaffFio() {
		return "";
	}

	public void loadBranch() {

	}

	public void assignStaff(Staff staff) {
		if (staff != null && this.selected != null) {
			this.selected.setStaff_id(staff.getStaff_id());
			this.selected.setBranch_id(staff.getBranch_id());
			this.selected.setPosition_id(staff.getPosition_id());
		}
	}

	List<PyramidTypeClass> pyramidTypes;

	public List<PyramidTypeClass> getPyramidTypes() {
		if (pyramidTypes == null) {
			this.pyramidTypes = new ArrayList<HrpCrud.PyramidTypeClass>();
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
		return pyramidTypes;
	}

	public class PyramidTypeClass {
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

	private Salary selectedSalary;

	public Salary getSelectedSalary() {
		return selectedSalary;
	}

	public void setSelectedSalary(Salary selectedSalary) {
		this.selectedSalary = selectedSalary;
	}

	public void assignSalary() {
		if (selectedSalary != null && selectedSalary.getP_staff() != null) {
			if (selected != null) {
				selected.setBukrs(selectedSalary.getBukrs());
				selected.setStaff_id(selectedSalary.getStaff_id());
				selected.setPosition_id(selectedSalary.getPosition_id());
				selected.setBranch_id(selectedSalary.getBranch_id());
				selected.setBusiness_area_id(selectedSalary
						.getBusiness_area_id());
			}
		}
	}

	public void resetSalary() {
		selectedSalary = null;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public void setCreateMode() {
		this.mode = "create";
	}

	public void setUpdateMode() {
		this.mode = "update";
	}

	public String getMode() {
		return mode;
	}
}
