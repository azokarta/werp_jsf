package reports.logistics;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.StaffDao;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Staff;
import general.tables.search.StaffSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import user.User;

@ManagedBean(name = "repAccountabilityBean")
@ViewScoped
public class RepAccountabilityBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1677082310081963463L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			loadMatnrMap();
			loadStaffMap();
			// generateList();
			if (!userData.isSys_admin() && !userData.isMain()) {
				searchModel.setBukrs(userData.getBukrs());
			}
		}
	}

	private List<Staff> staffList;

	public List<Staff> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<Staff> staffList) {
		this.staffList = staffList;
	}

	private Map<Long, Matnr> matnrMap;

	private void loadMatnrMap() {
		matnrMap = new HashMap<Long, Matnr>();
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrList = d.findAll();
		for (Matnr m : matnrList) {
			matnrMap.put(m.getMatnr(), m);
		}

	}

	public String getStaffName(Long stfId) {
		return stfMap.get(stfId) == null ? null : stfMap.get(stfId).getLF();
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	private void loadStaffMap() {
		stfMap = new HashMap<Long, Staff>();
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		staffList = d.findAll();
		for (Staff stf : staffList) {
			stfMap.put(stf.getStaff_id(), stf);
		}
	}

	private Map<Long, List<MatnrList>> outputMap;

	public Map<Long, List<MatnrList>> getOutputMap() {
		return outputMap;
	}

	Map<Long, HashMap<Long, List<String>>> barcodesMap = new HashMap<Long, HashMap<Long, List<String>>>();

	public List<String> getUserMatnrBarcodes(Long userId,Long matnr){
		if(barcodesMap.containsKey(userId)){
			if(barcodesMap.get(userId).containsKey(matnr)){
				return barcodesMap.get(userId).get(matnr);
			}
		}
		return null;
	}

	public void generateList() {
		if(Validation.isEmptyLong(searchModel.getWerks())){
			GeneralUtil.addErrorMessage("Выберите склад");
			return;
		}
		String cond = searchModel.getCondition();
		barcodesMap = new HashMap<Long, HashMap<Long, List<String>>>();
		MatnrListDao d = (MatnrListDao) appContext.getContext().getBean(
				"matnrListDao");
		List<MatnrList> mlList = d.dynamicFind3(cond + (cond.length() > 0 ? " AND "
				: " ")
				+ " status = '"
				+ MatnrList.STATUS_ACCOUNTABILITY
				+ "' AND barcode IS NOT NULL ");

		for (MatnrList ml : mlList) {
			HashMap<Long, List<String>> tempMap = new HashMap<Long, List<String>>();
			if (barcodesMap.containsKey(ml.getStaff_id())) {
				tempMap = barcodesMap.get(ml.getStaff_id());
			}

			List<String> tempList = new ArrayList<String>();
			if (tempMap.containsKey(ml.getMatnr())) {
				tempList = tempMap.get(ml.getMatnr());
			}

			tempList.add(ml.getBarcode());
			tempMap.put(ml.getMatnr(), tempList);
			barcodesMap.put(ml.getStaff_id(), tempMap);
		}

		outputMap = new HashMap<Long, List<MatnrList>>();

		List<MatnrList> l = d.findAllAccountability(cond);
		for (MatnrList ml : l) {
			Matnr m = matnrMap.get(ml.getMatnr());
			if (m == null) {
				ml.setMatnrObject(new Matnr());
			} else {
				ml.setMatnrObject(m);
			}
			List<MatnrList> temp = new ArrayList<MatnrList>();
			if (outputMap.containsKey(ml.getStaff_id())) {
				temp = outputMap.get(ml.getStaff_id());
			}
			temp.add(ml);
			outputMap.put(ml.getStaff_id(), temp);
			// System.err.println(ml.getStaff_id() + " => " + ml.getMatnr() +
			// " => " + ml.getMenge());
		}

	}

	private List<Matnr> matnrList;
	private List<Matnr> selectedMatnrs;
	private Matnr selectedMatnr;

	public List<Matnr> getMatnrList() {
		return matnrList;
	}

	public void setMatnrList(List<Matnr> matnrList) {
		this.matnrList = matnrList;
	}

	public List<Matnr> getSelectedMatnrs() {
		return selectedMatnrs;
	}

	public void setSelectedMatnrs(List<Matnr> selectedMatnrs) {
		this.selectedMatnrs = selectedMatnrs;
	}

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	private SearchModel searchModel = new SearchModel();

	public SearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	private StaffSearch staffSearchModel = new StaffSearch();

	public StaffSearch getStaffSearchModel() {
		return staffSearchModel;
	}

	public void setStaffSearchModel(StaffSearch staffSearchModel) {
		this.staffSearchModel = staffSearchModel;
	}

	public void loadStaffList() {
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		staffList = d.findAll(staffSearchModel.getCondition());
		// System.out.println(staffSearchModel.getCondition());
	}

	public class SearchModel {
		private String bukrs;
		private Long werks;
		private Long staffId;
		private Long matnr;

		public Long getMatnr() {
			return matnr;
		}

		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}

		public String getBukrs() {
			return bukrs;
		}

		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}

		public Long getWerks() {
			return werks;
		}

		public void setWerks(Long werks) {
			this.werks = werks;
		}

		public Long getStaffId() {
			return staffId;
		}

		public void setStaffId(Long staffId) {
			this.staffId = staffId;
		}

		public String getCondition() {
			String cond = "";
			if (!Validation.isEmptyString(getBukrs())) {
				cond = " bukrs = '" + getBukrs() + "' ";
			}

			if (selectedStaff != null) {
				cond += (cond.length() > 0 ? " AND " : " ") + " staff_id = "
						+ selectedStaff.getStaff_id();
			}

			if (selectedMatnr != null) {
				cond += (cond.length() > 0 ? " AND " : " ") + " matnr = "
						+ selectedMatnr.getMatnr();
			}

			if (!Validation.isEmptyLong(getWerks())) {
				cond += (cond.length() > 0 ? " AND " : " ") + " werks = "
						+ getWerks();
			}
			return cond;
		}
	}

	public class OutputTable {

	}

	public void removeSelectedMatnr() {
		selectedMatnr = null;
	}

	public void removeSelectedStaff() {
		selectedStaff = null;
	}

	public void onSelectMatnr(SelectEvent e) {
		selectedMatnr = (Matnr) e.getObject();
		GeneralUtil.hideDialog("MatnrListDialog");
	}

	public void onSelectStaff(SelectEvent e) {
		selectedStaff = (Staff) e.getObject();
		// System.out.println("SEL: " + selectedStaff.getStaff_id());
		GeneralUtil.hideDialog("StaffListDialog");
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
