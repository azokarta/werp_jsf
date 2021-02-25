package hr.staff;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.StaffEducationDao;
import general.services.StaffEducationService;
import general.tables.Staff;
import general.tables.StaffEducation;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "hrsEducationBean")
@ViewScoped
public class HrsEducation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5998861912059176472L;

	private Staff selectedStaff;
	private StaffEducation selected;
	private List<StaffEducation> items;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	public StaffEducation getSelected() {
		return selected;
	}

	public void setSelected(StaffEducation selected) {
		this.selected = selected;
	}

	public List<StaffEducation> getItems() {
		if (items == null) {
			loadItems();
		}
		return items;
	}

	public void setItems(List<StaffEducation> items) {
		this.items = items;
	}

	private void loadItems() {
		if (selectedStaff != null) {
			StaffEducationDao sed = (StaffEducationDao) appContext.getContext()
					.getBean("staffEducationDao");
			items = sed.findAll(" staff_id = " + selectedStaff.getStaff_id());
		}
	}

	public StaffEducation prepareCreate() {
		if (selectedStaff != null) {
			selected = new StaffEducation();
			selected.setStaffId(selectedStaff.getStaff_id());
		}

		return selected;
	}

	public void Save() {
		try {
			if (selected == null) {
				throw new DAOException("Error");
			}

			if (selected.getSeId() == null) {
				Create(selected);
			} else {
				Update(selected);
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("EducationCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void Save(StaffEducation se) {
		try {
			if (se == null) {
				throw new DAOException("Error");
			}

			Update(se);
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			setEditRowIndex(-1);
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create(StaffEducation se) {
		StaffEducationService ses = (StaffEducationService) appContext
				.getContext().getBean("staffEducationService");
		ses.create(se);
	}

	private void Update(StaffEducation se) {
		StaffEducationService ses = (StaffEducationService) appContext
				.getContext().getBean("staffEducationService");
		ses.update(se);
	}

	public void Reset() {

	}

	private int editRowIndex = -1;

	public int getEditRowIndex() {
		return editRowIndex;
	}

	public void setEditRowIndex(int editRowIndex) {
		this.editRowIndex = editRowIndex;
	}

	@ManagedProperty("#{appContext}")
	AppContext appContext;

	@ManagedProperty("#{userinfo}")
	User userData;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

}
