package general.beans;

import general.AppContext;
import general.dao.StaffDao;
import general.tables.Staff;
import general.tables.search.StaffSearch;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class StaffBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Staff> items;
	private StaffSearch searchModel = new StaffSearch();

	public StaffSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(StaffSearch searchModel) {
		this.searchModel = searchModel;
	}

	private Staff selected;

	public Staff getSelected() {
		return selected;
	}

	public void setSelected(Staff selected) {
		this.selected = selected;
	}

	public void removeSelected() {
		this.setSelected(null);
	}

	private void loadStaff() {
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		String cond = this.searchModel.getCondition();
		
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		cond += (cond.length() > 0 ? " AND ": "") + " s.staff_id IN (SELECT sl.staff_id FROM Salary sl WHERE sl.end_date > '"
				+ sdf.format(Calendar.getInstance().getTime()) + "' ) ";

		this.items = d.findAll(cond);
	}

	public List<Staff> getItems() {
		if (items == null) {
			// this.loadStaff();
		}

		return this.items;
	}

	public void Search() {
		loadStaff();
	}

	public String getStaffLabel(Long staffId) {
		if (staffId == null || staffId == 0) {
			return "";
		}
		if (this.items == null) {
			this.loadStaff();
		}

		for (Staff stf : this.items) {
			if (stf.getStaff_id().longValue() == staffId) {
				return stf.getLF();
			}
		}

		return "";
	}

	@PostConstruct
	public void init() {
		System.out.println("TEST");
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
