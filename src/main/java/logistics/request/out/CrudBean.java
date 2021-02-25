package logistics.request.out;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.RelatedDocsDao;
import general.dao.RequestDao;
import general.dao.RequestMatnrDao;
import general.dao.RequestOutDao;
import general.dao.RequestOutMatnrDao;
import general.dao.UserDao;
import general.services.RequestOutService;
import general.tables.Branch;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.MyDocs;
import general.tables.RelatedDocs;
import general.tables.Request;
import general.tables.RequestMatnr;
import general.tables.RequestOut;
import general.tables.RequestOutMatnr;
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

@ManagedBean(name = "requestOutCrudBean")
@ViewScoped
public class CrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3786617747839870333L;
	static final Long transactionId = 233L;
	static final String transactionCode = "LG_REQ_OUT";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}

			setExcelFileName("Materialy_zayavki--№ " + id);

			prepareMatnrModel();
			loadMatnrMap();
			loadStaffMap();
		}
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();
	private String excelFileName;

	public String getExcelFileName() {
		return excelFileName;
	}

	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}

	private void loadStaffMap() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			stfMap.put(u.getUser_id(), u.getStaff());
		}
	}

	private Map<Long, Matnr> matnrMap;

	private void loadMatnrMap() {
		MatnrDao md = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = md.getMappedList("");
	}

	private Long id;

	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {
			initBean();
		}
	}

	private void initBean() {
		if (!Validation.isEmptyString(mode)) {
			if (mode.equals("create")) {
				request = new RequestOut();
				request.setDepartment_id(6L);
				loadAllParentDocs();
			} else if (mode.equals("view")) {
				loadRequest();
				loadReqMatnrs();
				loadParentDocs();
			} else if (mode.equals("update")) {
				loadRequest();
				loadReqMatnrs();
				loadParentDocs();
				loadAllParentDocs();
			}
		}
	}

	public void changeBukrs() {
		parentDocs = new ArrayList<Request>();
		reqMatnrs = new ArrayList<RequestOutMatnr>();
	}

	RequestOut request;

	public RequestOut getRequest() {
		return request;
	}

	public void setRequest(RequestOut request) {
		this.request = request;
	}

	private List<RequestOutMatnr> reqMatnrs = new ArrayList<RequestOutMatnr>();

	public List<RequestOutMatnr> getReqMatnrs() {
		return reqMatnrs;
	}

	private void loadReqMatnrs() {
		RequestOutMatnrDao romDao = (RequestOutMatnrDao) appContext.getContext().getBean("requestOutMatnrDao");
		reqMatnrs = romDao.findReqMatnrs(id);
		for (RequestOutMatnr rom : reqMatnrs) {
			Matnr m = matnrMap.get(rom.getMatnr());
			if (m != null) {
				rom.setMatnrObject(m);
			}
		}
	}

	private void loadRequest() {
		if (!Validation.isEmptyLong(id)) {
			RequestOutDao roDao = (RequestOutDao) appContext.getContext().getBean("requestOutDao");
			request = roDao.find(id);
		}
	}

	public void Save() {
		try {

			if (!PermissionController.canCreate(userData, transactionId)) {
				throw new DAOException(PermissionController.NO_PERMISSION_MSG);
			}

			if (mode.equals("create")) {
				Create();
			} else if (mode.equals("update")) {
				Update();
			}

			GeneralUtil.doRedirect("/logistics/request/out/View.xhtml?id=" + request.getId());
		} catch (DAOException e) {
			if (mode.equals("create")) {
				request.setId(null);
			}
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		RequestOutService roService = (RequestOutService) appContext.getContext().getBean("requestOutService");
		roService.create(request, reqMatnrs, parentDocs, userData);
	}

	private void Update() {
		RequestOutService roService = (RequestOutService) appContext.getContext().getBean("requestOutService");
		roService.update(request, reqMatnrs, parentDocs, userData);
	}

	private MatnrModel matnrModel;

	public MatnrModel getMatnrModel() {
		return matnrModel;
	}

	private Matnr selectedMatnr;

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	public void assignMatnr() {
		if (selectedMatnr != null) {
			boolean isExisted = false;
			for (RequestOutMatnr rom : reqMatnrs) {
				if (rom.getMatnr() != null && rom.getMatnr().equals(selectedMatnr.getMatnr())) {
					isExisted = true;
					break;
				}
			}

			if (isExisted) {
				GeneralUtil
						.addErrorMessage(String.format("Материал %s уже имеется в списке", selectedMatnr.getText45()));
			} else {
				currentReqMatnr.setMatnrObject(selectedMatnr);
				currentReqMatnr.setMatnr(selectedMatnr.getMatnr());
				currentReqMatnr.setQuantityInWerks(getQuantityInWerks(selectedMatnr.getMatnr()));
			}
		}
	}

	private RequestOutMatnr currentReqMatnr;

	public RequestOutMatnr getCurrentReqMatnr() {
		return currentReqMatnr;
	}

	public void setCurrentReqMatnr(RequestOutMatnr currentReqMatnr) {
		this.currentReqMatnr = currentReqMatnr;
	}

	private List<Request> reqInDocs;

	public List<Request> getReqInDocs() {
		return reqInDocs;
	}

	List<MyDocs> mdList = new ArrayList<MyDocs>();

	public List<MyDocs> getMdList() {
		return mdList;
	}

	private Map<Long, MyDocs> selectedMdMap = new HashMap<Long, MyDocs>();
	private List<MyDocs> selectedMdList = new ArrayList<MyDocs>();

	public List<MyDocs> getSelectedMdList() {
		return selectedMdList;
	}

	private double getQuantityInWerks(Long matnrId) {
		MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean("matnrListDao");
		String cond = String.format(" matnr = %d AND status = '%s' ", matnrId, MatnrList.STATUS_RECEIVED);
		List<MatnrList> mlList = mlDao.dynamicFind(cond);
		return mlList.size() > 0 ? mlList.get(0).getMenge() : 0D;
	}

	public void generateReqMatnrs() {
		reqMatnrs = new ArrayList<RequestOutMatnr>();
		if (selectedMdList.size() > 0) {
			String[] ids = new String[selectedMdList.size()];
			for (int i = 0; i < selectedMdList.size(); i++) {
				ids[i] = selectedMdList.get(i).getContext_id().toString();
			}

			String cond = String.format(" matnr IS NOT NULL AND matnr > 0 AND request_id IN(%s) ",
					"'" + String.join("','", ids) + "'");
			RequestMatnrDao rmDao = (RequestMatnrDao) appContext.getContext().getBean("requestMatnrDao");
			List<RequestMatnr> rmList = rmDao.findAll(cond);
			Map<Long, RequestOutMatnr> tempMap = new HashMap<Long, RequestOutMatnr>();
			for (RequestMatnr rm : rmList) {
				Matnr m = matnrMap.get(rm.getMatnr());
				if (m != null) {
					RequestOutMatnr rom;
					if (tempMap.containsKey(rm.getMatnr())) {
						rom = tempMap.get(rm.getMatnr());
						rom.setReq_quantity(rom.getReq_quantity() + rm.getQuantity());
					} else {
						rom = new RequestOutMatnr();
						rom.setMatnr(rm.getMatnr());
						rom.setMatnrObject(m);
						rom.setQuantity(rm.getQuantity());
						rom.setReq_quantity(rm.getQuantity());
					}
				}
			}
		}
	}

	private List<Request> selectedReqInDocs = new ArrayList<Request>();

	public List<Request> getSelectedReqInDocs() {
		return selectedReqInDocs;
	}

	public void setSelectedReqInDocs(List<Request> selectedReqInDocs) {
		this.selectedReqInDocs = selectedReqInDocs;
	}

	private Request selectedParentDoc;

	public Request getSelectedParentDoc() {
		return selectedParentDoc;
	}

	public void setSelectedParentDoc(Request selectedParentDoc) {
		this.selectedParentDoc = selectedParentDoc;
	}

	public void deleteParentDocRow(Request r) {
		parentDocs.remove(r);
		selectedParentDocMap.remove(r.getId());
	}

	public void deleteReqMatnrsRow(RequestOutMatnr rom) {
		reqMatnrs.remove(rom);
	}

	public void addReqMatnrsRow() {
		reqMatnrs.add(new RequestOutMatnr());
	}

	private void prepareMatnrModel() {
		matnrModel = new MatnrModel((MatnrDao) appContext.getContext().getBean("matnrDao"));
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

	/***********************/

	private Request currentParentDoc;

	public Request getCurrentParentDoc() {
		return currentParentDoc;
	}

	public void setCurrentParentDoc(Request currentParentDoc) {
		this.currentParentDoc = currentParentDoc;
	}

	private Map<Long, Request> selectedParentDocMap = new HashMap<Long, Request>();

	public void assignParentDoc() {
		if (selectedParentDoc != null && !selectedParentDocMap.containsKey(selectedParentDoc.getId())) {

			parentDocs.add(selectedParentDoc);
			selectedParentDocMap.put(selectedParentDoc.getId(), selectedParentDoc);

			Map<Long, Integer> tempMap = new HashMap<Long, Integer>();
			String cond = String.format(" matnr IS NOT NULL AND matnr > 0 AND request_id = %d ",
					selectedParentDoc.getId());
			RequestMatnrDao rmDao = (RequestMatnrDao) appContext.getContext().getBean("requestMatnrDao");
			List<RequestMatnr> rmList = rmDao.findAll(cond);
			for (RequestMatnr rm : rmList) {
				for (RequestOutMatnr rom : reqMatnrs) {
					if (rom.getMatnr().equals(rm.getMatnr())) {
						rom.setReq_quantity(rom.getReq_quantity() + rm.getQuantity());
						tempMap.put(rm.getMatnr(), 1);
					}
				}
			}

			for (RequestMatnr rm : rmList) {
				Matnr m = matnrMap.get(rm.getMatnr());
				if (m != null) {
					if (!tempMap.containsKey(rm.getMatnr())) {
						RequestOutMatnr rom = new RequestOutMatnr();
						rom.setMatnrObject(m);
						rom.setMatnr(m.getMatnr());
						rom.setQuantity(rm.getQuantity());
						rom.setReq_quantity(rm.getQuantity());
						rom.setQuantityInWerks(getQuantityInWerks(rm.getMatnr()));
						reqMatnrs.add(rom);
					}
				}
			}

			selectedParentDoc = null;
		}
	}

	List<Request> parentDocs = new ArrayList<Request>();

	public List<Request> getParentDocs() {
		return parentDocs;
	}

	private void loadParentDocs() {
		parentDocs = new ArrayList<Request>();
		RelatedDocsDao d1 = (RelatedDocsDao) appContext.getContext().getBean("relatedDocsDao");
		List<RelatedDocs> l = d1.findAll(
				String.format("context = '%s' AND context_id = %d AND parent_id IS NOT NULL AND parent_id > 0 ",
						RequestOut.CONTEXT, request.getId()));
		if (l.size() > 0) {
			String[] ids = new String[l.size()];
			for (int i = 0; i < l.size(); i++) {
				ids[i] = l.get(i).getParent_id().toString();
			}

			List<RelatedDocs> l2 = d1.findAll(String.format(" id IN(%s) ", "'" + String.join("','", ids) + "' "));
			if (l2.size() > 0) {
				String[] ids2 = new String[l2.size()];
				for (int i = 0; i < l2.size(); i++) {
					ids2[i] = l2.get(i).getContext_id().toString();
				}

				RequestDao d2 = (RequestDao) appContext.getContext().getBean("requestDao");
				parentDocs = d2.findAll(String.format(" id IN(%s) ", "'" + String.join("','", ids2) + "' "));
				for (Request req : parentDocs) {
					Staff stf = stfMap.get(req.getCreated_by());
					if (stf == null) {
						req.setCreator(new Staff());
					} else {
						req.setCreator(stf);
					}
				}
			}

		}
	}

	List<Request> loadedParentDocs = new ArrayList<Request>();

	public List<Request> getLoadedParentDocs() {
		return loadedParentDocs;
	}

	public void setLoadedParentDocs(List<Request> loadedParentDocs) {
		this.loadedParentDocs = loadedParentDocs;
	}

	Map<Long, List<Request>> allParentDocsMap;

	public void loadAllParentDocs() {
		loadedParentDocs = new ArrayList<Request>();
		if (allParentDocsMap == null) {
			allParentDocsMap = new HashMap<Long, List<Request>>();
			allParentDocsMap.put(Branch.AURA_MAIN_BRANCH_ID, new ArrayList<Request>());
			allParentDocsMap.put(Branch.GREEN_LIGHT_MAIN_BRANCH_ID, new ArrayList<Request>());
			RequestDao d = (RequestDao) appContext.getContext().getBean("requestDao");
			List<Request> l = d.findAll(" status_id = " + Request.STATUS_1 + " AND res_branch_id IN( "
					+ Branch.AURA_MAIN_BRANCH_ID + ", " + Branch.GREEN_LIGHT_MAIN_BRANCH_ID + ") ");
			System.out.println("DD: " + l.size());
			for (Request r : l) {
				Staff stf = stfMap.get(r.getCreated_by());
				if (stf == null) {
					r.setCreator(new Staff());
				} else {
					r.setCreator(stf);
				}

				allParentDocsMap.get(r.getRes_branch_id()).add(r);
			}
		}

		if (!Validation.isEmptyString(request.getBukrs())) {
			if (request.getBukrs().equals("1000")) {
				loadedParentDocs = allParentDocsMap.get(Branch.AURA_MAIN_BRANCH_ID);
			} else {
				loadedParentDocs = allParentDocsMap.get(Branch.GREEN_LIGHT_MAIN_BRANCH_ID);
			}
		}
	}

	private List<MatnrList> mlInWerks = new ArrayList<MatnrList>();

	public void loadMlInWerks(Long matnr) {

	}
}