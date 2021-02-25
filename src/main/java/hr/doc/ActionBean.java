package hr.doc;

import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.services.hr.HrDocService;
import general.tables.HrDoc;
import general.tables.HrDocActionLog;
import general.tables.HrDocApprover;
import general.tables.HrDocItem;
import general.tables.Pyramid;
import general.tables.Salary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import datamodels.SalaryModel;
import user.User;

@ManagedBean(name = "hrDocActionBean")
@ViewScoped
public class ActionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1513142092270919124L;

	private HrDoc document;
	private String mode;

	public void initBean(HrDoc document, String mode) throws Exception {
		this.document = document;
		this.mode = mode;
		salaryModel = new SalaryModel(appContext.getContext().getBean("salaryDao", SalaryDao.class));
		if (document != null) {
			salaryModel.getSearchModel().setBukrs(document.getBukrs());
		}

		if (!userData.isSys_admin() && !userData.isMain()) {
			salaryModel.getSearchModel().setUserBranches(userData.getUserBranches());
		}

		prepareButtons();
		if (document.getStatusId() == HrDoc.STATUS_ON_EXECUTION) {
			preparePyramidItems();
		}
	}

	private List<Salary> salaryItems = new ArrayList<Salary>();

	public List<Salary> getSalaryItems() {
		return salaryItems;
	}

	public void setSalaryItems(List<Salary> salaryItems) {
		this.salaryItems = salaryItems;
	}

	private List<Button> buttonList = new ArrayList<ActionBean.Button>();

	public List<Button> getButtonList() {
		return buttonList;
	}

	private void prepareButtons() {
		buttonList = new ArrayList<>();
		if (document != null) {
			if (document.getStatusId() == HrDoc.STATUS_ON_CREATE) {
				if (userData.getUserid().equals(document.getCreatedBy())) {
					Button b1 = new Button(HrDocActionLog.ACTION_UPDATE);
					buttonList.add(b1);
					if (HrDoc.TYPE_BYPASS_SHEET == document.getTypeId()) {
						Button b2 = new Button(HrDocActionLog.ACTION_ADD_APPROVER);
						buttonList.add(b2);

						if (!Validation.isEmptyCollection(document.getHrDocApprovers())) {
							Button b3 = new Button(HrDocActionLog.ACTION_SEND);
							buttonList.add(b3);
						}
					} else {
						Button b2 = new Button(HrDocActionLog.ACTION_SEND);
						buttonList.add(b2);
					}

					if (document.getTypeId() == HrDoc.TYPE_DISMISS) {
						// Button b3 = new
						// Button(HrDocActionLog.ACTION_ADD_APPROVER);
						// buttonList.add(b3);
						// Button b3 = new
						// Button(HrDocActionLog.ACTION_CREATE_BYPASS_SHEET);
						// buttonList.add(b3);
					}

					Button b4 = new Button(HrDocActionLog.ACTION_CANCEL);
					buttonList.add(b4);
				}
			} else if (HrDoc.STATUS_ON_APPROVEMENT == document.getStatusId()) {
				if (document.getHrDocApprovers() != null) {
					// boolean b = false;
					HrDocApprover currentApprover = null;
					for (HrDocApprover ap : document.getHrDocApprovers()) {
						// if (HrDocApprover.STATUS_NONE == ap.getStatusId()
						// && userData.getUserid().equals(ap.getUserId())) {
						// b = true;
						// break;
						// }
						if (HrDocApprover.STATUS_NONE == ap.getStatusId()) {
							currentApprover = ap;
							break;
						}
					}

					// if (b) {
					if (currentApprover != null && userData.getUserid().equals(currentApprover.getUserId())) {
						Button b1 = new Button(HrDocActionLog.ACTION_APPROVE);
						Button b2 = new Button(HrDocActionLog.ACTION_REFUSE);
						buttonList.add(b1);
						buttonList.add(b2);
					}
				}
			} else if (HrDoc.STATUS_ON_EXECUTION == document.getStatusId()) {
				if (userData.getUserid().equals(document.getResponsibleId())) {
					Button b1 = new Button(HrDocActionLog.ACTION_ADD_APPROVER);
					buttonList.add(b1);
					if (document.getTypeId() != HrDoc.TYPE_DISMISS && document.getTypeId() != HrDoc.TYPE_BYPASS_SHEET) {
						Button b4 = new Button(HrDocActionLog.ACTION_ADD_AMOUNT);
						buttonList.add(b4);
					}
					boolean b = false;
					if (document.getHrDocApprovers() != null) {
						for (HrDocApprover appr : document.getHrDocApprovers()) {
							if (HrDocApprover.STATUS_NONE == appr.getStatusId()) {
								b = true;
								break;
							}
						}
						if (b) {
							Button b2 = new Button(HrDocActionLog.ACTION_SEND, "Отправить на согласование", "post");
							buttonList.add(b2);
						}
					}

					Button cancelBtn = new Button(HrDocActionLog.ACTION_CANCEL);
					buttonList.add(cancelBtn);

					if (!b) {
						if (document.getTypeId() == HrDoc.TYPE_DISMISS) {
							Button b3 = new Button(HrDocActionLog.ACTION_DISMISS_EMPLOYEE, "Уволить сотрудников",
									"post");
							buttonList.add(b3);
						} else if (document.getTypeId() == HrDoc.TYPE_CHANGE_SALARY) {
							Button b3 = new Button(HrDocActionLog.ACTION_COMPLETE_DOC);
							buttonList.add(b3);
						} else if (document.getTypeId() == HrDoc.TYPE_BYPASS_SHEET) {
							Button b3 = new Button(HrDocActionLog.ACTION_COMPLETE_DOC);
							buttonList.add(b3);
						} else {
							Button b3 = new Button(HrDocActionLog.ACTION_ADD_SALARY);
							buttonList.add(b3);
						}
					}
				}
			}

			Button btnList = new Button(0, "В список", "get");
			buttonList.add(btnList);
		}
	}

	public class Button {
		private Integer id;
		private String label;
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Button(Integer id, String label, String type) {
			super();
			this.id = id;
			this.label = label;
			this.type = type;
		}

		public Button(Integer id) {
			super();
			this.id = id;
			this.label = HrDocActionLog.getName(id);
		}

		public void doAction() {
			if (getId().equals(0)) {
				doRedirectToList();
			} else if (getId().equals(HrDocActionLog.ACTION_UPDATE)) {
				doRedirectToUpdate();
			} else if (getId().equals(HrDocActionLog.ACTION_SEND)) {
				doSend();
			} else if (getId().equals(HrDocActionLog.ACTION_ADD_APPROVER)) {
				GeneralUtil.showDialog("SalaryListDialog");
			} else if (getId().equals(HrDocActionLog.ACTION_SEND_TO_APPROVER)) {
				sendToAgreement();
			} else if (getId().equals(HrDocActionLog.ACTION_APPROVE)) {
				doApprove();
			} else if (getId().equals(HrDocActionLog.ACTION_CREATE_NEW_SALARY)) {
				// GeneralUtil.doRedirect("/hr/document/recruitment/CreateSalary.xhtml?docId="
				// + document.getId());
			} else if (getId().equals(HrDocActionLog.ACTION_ADD_AMOUNT)) {
				GeneralUtil.showDialog("amountWidget");
			} else if (getId().equals(HrDocActionLog.ACTION_ADD_SALARY)) {
				GeneralUtil.doRedirect("/hr/doc/CreateSalary.xhtml?docId=" + document.getId());
			} else if (getId().equals(HrDocActionLog.ACTION_DISMISS_EMPLOYEE)) {
				GeneralUtil.showDialog("dismissWidget");
			} else if (getId().equals(HrDocActionLog.ACTION_CANCEL)) {
				cancelDocument();
			} else if (HrDocActionLog.ACTION_COMPLETE_DOC.equals(getId())) {
				completeDoc();
			} else if (HrDocActionLog.ACTION_REFUSE.equals(getId())) {
				GeneralUtil.showDialog("refuseWidget");
			} else if (HrDocActionLog.ACTION_CREATE_BYPASS_SHEET.equals(getId())) {
				GeneralUtil.showDialog("bypassSheetForm");
			}
		}

		public void clickListener() {
			System.out.println("TEST BTN " + this.getLabel());
		}
	}

	private Salary selectedSalary;

	public Salary getSelectedSalary() {
		return selectedSalary;
	}

	public void setSelectedSalary(Salary selectedSalary) {
		this.selectedSalary = selectedSalary;
	}

	/********* ACTIONS ****************/

	private void completeDoc() {
		try {
			HrDocService s = (HrDocService) appContext.getContext().getBean("hrDocService");
			s.completeDocument(document, userData);
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void cancelDocument() {
		try {
			HrDocService s = (HrDocService) appContext.getContext().getBean("hrDocService");
			s.cancelDocument(document, userData);
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void addAmount() {
		try {
			HrDocService s = (HrDocService) appContext.getContext().getBean("hrDocService");
			s.addAmount(document, userData);
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void dismissEmployees() {
		try {
			getService().dismissEmployeesAndCloseDoc(document, userData);
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void assignSalary() {
		try {
			if (selectedSalary != null) {
				getService().addApprover(document, selectedSalary, userData);
			}
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void saveBypassSheet() {
		GeneralUtil.addSuccessMessage("Saved!");
	}

	private HrDocService getService() {
		return (HrDocService) appContext.getContext().getBean("hrDocService");
	}

	private String refuseNote;

	public String getRefuseNote() {
		return refuseNote;
	}

	public void setRefuseNote(String refuseNote) {
		this.refuseNote = refuseNote;
	}

	public void refuse() {
		try {
			getService().refuse(document, userData, refuseNote);
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	/**
	 * Удаление согласующего
	 */

	public void removeApprover(Long id) {
		try {
			getService().removeApprover(document, userData, id);
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	List<Pyramid> pyramidItems = new ArrayList<Pyramid>();

	public List<Pyramid> getPyramidItems() {
		return pyramidItems;
	}

	private void preparePyramidItems() {
		pyramidItems = new ArrayList<Pyramid>();
		PyramidDao pd = (PyramidDao) appContext.getContext().getBean("pyramidDao");
		List<Pyramid> l = pd.findAllWithDetailsByBukrs(document.getBukrs());
		for (Pyramid pr : l) {
			if (pr.getParent_pyramid_id() == 0) {
				pr.setDisplayName("(" + positionF4Bean.getName(pr.getPosition_id() + "") + ") ");
				pyramidItems.add(pr);
				setPyrChilds(pr.getPyramid_id(), l, 0);
			}
		}
	}

	private void setPyrChilds(Long parentId, List<Pyramid> l, int deep) {
		for (Pyramid pr : l) {
			if (pr.getParent_pyramid_id().equals(parentId)) {
				StringBuffer sb = new StringBuffer();
				for (int k = 0; k < deep; k++) {
					sb.append(" - ");
				}

				if (!Validation.isEmptyLong(pr.getStaff_id())) {
					sb.append(pr.getStaff().getLF());
				}
				sb.append(" (" + positionF4Bean.getName(pr.getPosition_id() + "") + ") ");
				pr.setDisplayName(sb.toString());

				pyramidItems.add(pr);
				setPyrChilds(pr.getPyramid_id(), l, deep + 1);
			}
		}
	}

	public void generateSalaryItems() {
		// preparePyramidItems();
		salaryItems = new ArrayList<Salary>();
		for (HrDocItem hdi : document.getHrDocItems()) {
			// if (hdi.getStatusId() .equals(HrDocumentItem.STATUS_DONE)) {
			// continue;
			// }
			Salary slr = new Salary();
			slr.setAmount(0D);
			slr.setBeg_date(hdi.getBeginDate());
			slr.setBranch_id(hdi.getBranchId());
			slr.setBukrs(hdi.getBukrs());
			slr.setCreated_by(userData.getUserid());
			slr.setCreated_date(Calendar.getInstance().getTime());
			slr.setDepartment_id(hdi.getDepartmentId());
			slr.setP_staff(hdi.getStaff());
			slr.setPosition_id(hdi.getPositionId());
			slr.setStaff_id(hdi.getStaff().getStaff_id());
			salaryItems.add(slr);
		}
	}

	public void doRedirectToList() {
		if (document.getTypeId() == HrDoc.TYPE_RECRUITMENT) {
			GeneralUtil.doRedirect("/hr/doc/recruitment/List.xhtml");
		} else if (document.getTypeId() == HrDoc.TYPE_TRANSFER) {
			GeneralUtil.doRedirect("/hr/doc/transfer/List.xhtml");
		} else if (document.getTypeId() == HrDoc.TYPE_DISMISS) {
			GeneralUtil.doRedirect("/hr/doc/dismiss/List.xhtml");
		} else if (document.getTypeId() == HrDoc.TYPE_CHANGE_SALARY) {
			GeneralUtil.doRedirect("/hr/doc/change-salary/List.xhtml");
		}else if(document.getTypeId() == HrDoc.TYPE_BYPASS_SHEET){
			GeneralUtil.doRedirect("/hr/doc/bypass-sheet/List.xhtml");
		}
	}

	public void doRedirectToUpdate() {
		if (document.getTypeId() == HrDoc.TYPE_RECRUITMENT) {
			GeneralUtil.doRedirect("/hr/doc/recruitment/Update.xhtml?id=" + document.getId());
		} else if (document.getTypeId() == HrDoc.TYPE_TRANSFER) {
			GeneralUtil.doRedirect("/hr/doc/transfer/Update.xhtml?id=" + document.getId());
		} else if (document.getTypeId() == HrDoc.TYPE_DISMISS) {
			GeneralUtil.doRedirect("/hr/doc/dismiss/Update.xhtml?id=" + document.getId());
		} else if (document.getTypeId() == HrDoc.TYPE_CHANGE_SALARY) {
			GeneralUtil.doRedirect("/hr/doc/change-salary/Update.xhtml?id=" + document.getId());
		}
	}

	public void reloadPage() {
		GeneralUtil.doRedirect("/hr/doc/View.xhtml?id=" + document.getId());
	}

	public void doCreate() {
		try {

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doSend() {
		try {
			getService().send(document, userData);
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void sendToAgreement() {
		try {
			HrDocService s = (HrDocService) appContext.getContext().getBean("hrDocService");
			// s.sendToAgreement(document, userData);
			GeneralUtil.doRedirect("/hr/document/recruitment/View.xhtml?id=" + document.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doApprove() {
		try {
			HrDocService s = (HrDocService) appContext.getContext().getBean("hrDocService");
			s.approve(document, userData);
			reloadPage();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doCreateSalary() {
		try {
			// HrDocumentService s = (HrDocumentService)
			// appContext.getContext().getBean("hrDocumentService");
		} catch (DAOException e) {

		}
	}

	SalaryModel salaryModel;

	public SalaryModel getSalaryModel() {
		return salaryModel;
	}

	public void setSalaryModel(SalaryModel salaryModel) {
		this.salaryModel = salaryModel;
	}

	private List<general.tables.User> userList = new ArrayList<general.tables.User>();

	public List<general.tables.User> getUserList() {
		return userList;
	}

	public void setUserList(List<general.tables.User> userList) {
		this.userList = userList;
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
	PositionF4 positionF4Bean;

	public PositionF4 getPositionF4Bean() {
		return positionF4Bean;
	}

	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}

}