package hr.doc;

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

import f4.BranchF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.Validation;
import general.dao.HrDocDao;
import general.dao.SalaryDao;
import general.services.hr.HrDocActionLogService;
import general.tables.Branch;
import general.tables.HrDoc;
import general.tables.HrDocActionLog;
import general.tables.HrDocItem;
import general.tables.Salary;
import general.tables.Staff;
import user.User;

@ManagedBean(name = "hrDocViewBean")
@ViewScoped
public class HrDocView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7265877041723482643L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}

			loadSalaryList();
			loadDocument();
			MessageProvider mp = new MessageProvider();
			// loadItems();
			if (document != null) {
				setPageHeader(
						mp.getValue("hr.doc.view") + " " + document.getDocTypeName() + ", № " + document.getFormattedRegNumber());
				try {
					actionBean.initBean(document, "view");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private HrDoc document;

	public HrDoc getDocument() {
		return document;
	}

	public void setDocument(HrDoc document) {
		this.document = document;
	}

	private Long id;

	private List<Branch> userBranches = new ArrayList<Branch>();

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	private void loadDocument() {
		if (!Validation.isEmptyLong(id)) {
			HrDocDao d = (HrDocDao) appContext.getContext().getBean("hrDocDao");
			document = d.findWithDetail(id);
			if (document != null) {
				if(HrDoc.TYPE_DISMISS == document.getTypeId() && HrDoc.STATUS_ON_CREATE == document.getStatusId()){
					
				}
				
				// LOG
				HrDocActionLogService logService = (HrDocActionLogService) appContext.getContext()
						.getBean("hrDocActionLogService");
				HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_VIEW, null, document.getId(),
						userData.getUserid());
				logService.create(log, userData.getUserid());
			}
			// document = d.find(id);
		}
	}

	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private List<Salary> salaryList = new ArrayList<Salary>();

	public List<Salary> getSalaryList() {
		return salaryList;
	}

	private void loadSalaryList() {
		SalaryDao d = (SalaryDao) appContext.getContext().getBean("salaryDao");
		String cond = "";
		if (!userData.isSys_admin()) {
			cond = " s1.bukrs = " + userData.getBukrs();
		}
		cond += (cond.length() > 0 ? " AND " : "") + " s1.position_id IN(5,6,10,20,21,22,23,29,49,50,70,71) ";
		salaryList = d.findAllCurrentWithStaff(cond);
	}

	private Map<String, Map<Long, List<Staff>>> managersMap = new HashMap<>();

	public List<Staff> getManagers() {
		if (Validation.isEmptyString(document.getBukrs())) {
			return new ArrayList<>();
		}

		if (Validation.isEmptyLong(document.getBranchId())) {
			return new ArrayList<>();
		}

		if (!managersMap.containsKey(document.getBukrs())) {
			return new ArrayList<>();
		}

		if (!managersMap.get(document.getBukrs()).containsKey(document.getBranchId())) {
			return new ArrayList<>();
		}

		return managersMap.get(document.getBukrs()).get(document.getBranchId());
	}

	public void onSelectStaff(SelectEvent e) {
		Staff stf = (Staff) e.getObject();
		if (stf != null) {
			HrDocItem item = new HrDocItem();
			item.setStaffId(stf.getStaff_id());
			item.setStaff(stf);
			boolean isExisted = false;
			if (document.getHrDocItems() != null) {
				if (document.getHrDocItems().contains(item)) {
					GeneralUtil.addErrorMessage("Сотрудник уже добавлен в список!");
					isExisted = true;
				}
			}

			if (!isExisted) {
				document.addHrDocItem(item);
				GeneralUtil.hideDialog("staffWidget");
			}
		} else {
			GeneralUtil.addErrorMessage("Ошибка!");
		}
	}

	private List<Staff> staffList = new ArrayList<Staff>();

	public List<Staff> getStaffList() {
		return staffList;
	}

	Map<Long, List<Staff>> directorsMap = new HashMap<>();

	@ManagedProperty("#{hrDocActionBean}")
	ActionBean actionBean;

	public ActionBean getActionBean() {
		return actionBean;
	}

	public void setActionBean(ActionBean actionBean) {
		this.actionBean = actionBean;
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

	@ManagedProperty("#{positionF4Bean}")
	PositionF4 positionF4;

	public PositionF4 getPositionF4() {
		return positionF4;
	}

	public void setPositionF4(PositionF4 positionF4) {
		this.positionF4 = positionF4;
	}

	@ManagedProperty("#{branchF4Bean}")
	BranchF4 branchF4;

	public BranchF4 getBranchF4() {
		return branchF4;
	}

	public void setBranchF4(BranchF4 branchF4) {
		this.branchF4 = branchF4;
	}

	@ManagedProperty("#{hrDocBypassSheet}")
	HrDocBypassSheet bypassSheetViewBean;

	public HrDocBypassSheet getBypassSheetViewBean() {
		return bypassSheetViewBean;
	}

	public void setBypassSheetViewBean(HrDocBypassSheet bypassSheetViewBean) {
		this.bypassSheetViewBean = bypassSheetViewBean;
	}

}
