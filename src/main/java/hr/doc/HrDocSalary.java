package hr.doc;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.HrDocDao;
import general.dao.HrDocumentDao;
import general.dao.SalaryDao;
import general.services.hr.HrDocService;
import general.tables.HrDoc;
import general.tables.HrDocItem;
import general.tables.HrDocument;
import general.tables.HrDocumentItem;
import general.tables.Salary;
import general.tables.Staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "hrDocSalaryBean")
@ViewScoped
public class HrDocSalary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1199916341271781330L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				docId = Long.parseLong(GeneralUtil.getRequestParameter("docId"));
				loadHrDocument();
				loadManagers();
			} catch (NumberFormatException e) {
				docId = 0L;
			}
		}
	}

	private Long docId;
	private HrDoc document;

	private void loadHrDocument() {
		if (!Validation.isEmptyLong(docId)) {
			HrDocDao hrDao = (HrDocDao) appContext.getContext().getBean("hrDocDao");
			document = hrDao.findWithDetail(docId);
		}
	}

	public HrDoc getDocument() {
		return document;
	}

	public void setDocument(HrDoc document) {
		this.document = document;
	}

	private String pageHeader = "Добавление должностей";

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public void Save() {
		try {
			HrDocService s = (HrDocService) appContext.getContext().getBean("hrDocService");
			s.addSalariesAndCloseDoc(document, userData);
			GeneralUtil.doRedirect("/hr/doc/View.xhtml?id=" + document.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	Map<Long, List<Staff>> managersMap = new HashMap<>();

	public List<Staff> getBranchManagers(Long branchId) {
		return managersMap.get(branchId);
	}

	private void loadManagers() {
		if (document != null) {
			List<HrDocItem> items = document.getHrDocItems();
			if (items.size() > 0) {
				SalaryDao d = (SalaryDao) appContext.getContext().getBean("salaryDao");
				String[] ids = new String[items.size()];
				for (int k = 0; k < items.size(); k++) {
					Long brId;
					if ((brId = items.get(k).getBranchId()) != null) {
						ids[k] = brId.toString();
						if (!managersMap.containsKey(brId)) {
							managersMap.put(brId, new ArrayList<>());
						}
					}
				}

				List<Salary> l = d.findAllCurrentWithStaff(
						" s1.position_id = 3 AND s1.branch_id IN( " + String.join(",", ids) + ") ");
				for (Salary s : l) {
					if (s.getP_staff() != null) {
						List<Staff> temp = managersMap.get(s.getBranch_id());
						temp.add(s.getP_staff());
						managersMap.put(s.getBranch_id(), temp);
					}
				}
			}
		}
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
