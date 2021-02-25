package request;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.ReqEventLogDao;
import general.dao.RequestDao;
import general.dao.RequestMatnrDao;
import general.dao.SalaryDao;
import general.services.RequestService;
import general.tables.Branch;
import general.tables.Matnr;
import general.tables.ReqEventLog;
import general.tables.Request;
import general.tables.RequestMatnr;
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

import datamodels.MatnrModel;
import user.User;

@ManagedBean(name = "requestCrudBean")
@ViewScoped
public class CrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4760394536674367547L;
	private static final Long TRANSACTION_ID = 84L;
	private static final String transactionCode = "REQ";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, TRANSACTION_ID);
			prepareMatnrModel();
			loadChiefList();
			prepareUserBranches();
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}

			prepareMatnrMap();
		}
	}

	private Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();

	private void prepareMatnrMap() {
		MatnrDao md = (MatnrDao) appContext.getContext().getBean("matnrDao");
		for (Matnr m : md.findAll()) {
			matnrMap.put(m.getMatnr(), m);
		}
	}

	private void prepareRequest() {
		if (!Validation.isEmptyString(mode)) {
			if (mode.equals("create")) {
				PermissionController.canWriteRedirect(userData, TRANSACTION_ID);
				request = new Request();
				if (!userData.isMain() && !userData.isSys_admin()) {
					request.setBukrs(userData.getBukrs());
					request.setBranch_id(userData.getBranch_id());
				}

				request.setDepartment_id(6L);// Отдел Логистики
				request.setLast_responsible(271L);// Жұлдыз
			}

			if (mode.equals("view")) {
				loadRequest();
				loadReqMatnrs();
				loadReqEvents();
				if (request != null) {
					try {
						rActionBean.initBean(request);
					} catch (Exception e) {

					}
				}
			}

			if (mode.equals("update")) {
				loadRequest();
				loadReqMatnrs();
			}
		}
	}

	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private void loadRequest() {
		if (id != null && id > 0) {
			RequestDao rDao = (RequestDao) appContext.getContext().getBean("requestDao");
			request = rDao.find(id);
		} else {
			request = new Request();
		}
	}

	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {
			prepareRequest();
		}
	}

	private Request request;

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public void Save() {
		try {
			if (!PermissionController.canCreate(userData, TRANSACTION_ID)) {
				throw new DAOException("Нет доступа");
			}
			if (Validation.isEmptyLong(request.getId())) {
				Create(request);
			} else {
				Update(request);
			}

			GeneralUtil.doRedirect("/request/View.xhtml?id=" + request.getId());

		} catch (DAOException e) {
			if (mode.equals("create")) {
				request.setId(null);
			}
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create(Request r) {
		RequestService rService = (RequestService) appContext.getContext().getBean("requestService");
		rService.create(r, reqMatnrs, userData);
	}

	private void Update(Request r) {
		RequestService rService = (RequestService) appContext.getContext().getBean("requestService");
		rService.update(r, reqMatnrs, userData);
	}

	private List<RequestMatnr> reqMatnrs = new ArrayList<RequestMatnr>();

	public List<RequestMatnr> getReqMatnrs() {
		return reqMatnrs;
	}

	private void loadReqMatnrs() {
		if (id != null && id > 0) {
			RequestMatnrDao rmDao = (RequestMatnrDao) appContext.getContext().getBean("requestMatnrDao");
			reqMatnrs = rmDao.findReqMatnrs(id);
			for (RequestMatnr rm : reqMatnrs) {
				if (!Validation.isEmptyLong(rm.getMatnr())) {
					rm.setMatnrObject(matnrMap.get(rm.getMatnr()));
					rm.setMatnr_str(matnrMap.get(rm.getMatnr()).getText45());
				} else {
					rm.setMatnrObject(new Matnr());
				}
			}
		}
	}

	private List<ReqEventLog> reqEvents;

	public List<ReqEventLog> getReqEvents() {
		return reqEvents;
	}

	private void loadReqEvents() {
		if (id != null && id > 0) {
			ReqEventLogDao relDao = (ReqEventLogDao) appContext.getContext().getBean("reqEventLogDao");
			reqEvents = relDao.findRequestEvents(id);
		}
	}

	private RequestMatnr currentReqMatnr;

	public RequestMatnr getCurrentReqMatnr() {
		return currentReqMatnr;
	}

	public void setCurrentReqMatnr(RequestMatnr currentReqMatnr) {
		this.currentReqMatnr = currentReqMatnr;
	}

	public void deleteRow(RequestMatnr item) {
		if (reqMatnrs.contains(item)) {
			reqMatnrs.remove(reqMatnrs.indexOf(item));
		}
	}

	public void addRow() {
		RequestMatnr rm = new RequestMatnr();
		rm.setUnits(1L);
		reqMatnrs.add(rm);
	}

	private Matnr selectedMatnr;

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	public void setReqMatnrs(List<RequestMatnr> reqMatnrs) {
		this.reqMatnrs = reqMatnrs;
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

	private MatnrModel matnrModel;

	public MatnrModel getMatnrModel() {
		return matnrModel;
	}

	public void setMatnrModel(MatnrModel matnrModel) {
		this.matnrModel = matnrModel;
	}

	private void prepareMatnrModel() {
		matnrModel = new MatnrModel((MatnrDao) appContext.getContext().getBean("matnrDao"));
	}

	private Map<Long, Matnr> selectedMatnrsMap = new HashMap<Long, Matnr>();

	public void assignMatnr() {
		if (currentReqMatnr != null && selectedMatnr != null) {
			if (!selectedMatnrsMap.containsKey(selectedMatnr.getMatnr())) {
				currentReqMatnr.setMatnrObject(selectedMatnr);
				currentReqMatnr.setMatnr(selectedMatnr.getMatnr());
				selectedMatnrsMap.put(selectedMatnr.getMatnr(), selectedMatnr);
			}
			selectedMatnr = null;
		}
	}

	private List<Branch> userBranches = new ArrayList<Branch>();

	public List<Branch> getUserBranches() {
		return userBranches;
	}

	public void setUserBranches(List<Branch> userBranches) {
		this.userBranches = userBranches;
	}

	private void prepareUserBranches() {
		userBranches.clear();

		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		userBranches = brDao.findChilds(userData.getBranch_id());
	}

	private List<Staff> chiefList;

	public List<Staff> getChiefList() {
		return chiefList;
	}

	private void loadChiefList() {
		chiefList = new ArrayList<Staff>();
		SalaryDao sDao = (SalaryDao) appContext.getContext().getBean("salaryDao");
		List<Salary> sList = sDao
				.findAllCurrentWithStaff(" s1.position_id IN('20','23','24','29','30','39','50','56','58')");
		for (Salary s : sList) {
			chiefList.add(s.getP_staff());
		}
	}

	@ManagedProperty("#{requestActionBean}")
	ActionBean rActionBean;

	public ActionBean getrActionBean() {
		return rActionBean;
	}

	public void setrActionBean(ActionBean rActionBean) {
		this.rActionBean = rActionBean;
	}

}
