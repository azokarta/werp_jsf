package crm.beans.call;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import crm.constants.CallConstants;
import crm.constants.CommonConstants;
import crm.constants.ReasonConstants;
import crm.dao.CrmReasonDao;
import crm.services.CrmCallService;
import crm.tables.CrmCall;
import crm.tables.CrmDocReco;
import crm.tables.CrmReason;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import user.User;

@ManagedBean(name = "crmCallCrudBean")
@ViewScoped
public class CallCrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6608915568053818604L;

	@PostConstruct
	public void init() {

	}

	private CrmDocReco selectedReco;
	private String dialogHeader;
	private CrmCall selected;
	private List<String> phoneNumbers;
	private Map<Integer, String> callResults = CallConstants.getResults();
	private List<CrmReason> reasons;

	public List<CrmReason> getReasons() {
		return reasons;
	}

	public void setReasons(List<CrmReason> reasons) {
		this.reasons = reasons;
	}

	public Map<Integer, String> getCallResults() {
		return callResults;
	}

	public void setCallResults(Map<Integer, String> callResults) {

		this.callResults = callResults;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public CrmCall getSelected() {
		return selected;
	}

	public void setSelected(CrmCall selected) {
		this.selected = selected;
	}

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	public CrmDocReco getSelectedReco() {
		return selectedReco;
	}

	public void setSelectedReco(CrmDocReco selectedReco) {
		this.selectedReco = selectedReco;
	}

	public void prepareCreate(CrmDocReco selectedReco) {
		selected = new CrmCall();
		if (selectedReco.getCallerIsDealer() == 1) {
			selected.setCallerId(selectedReco.getResponsibleId());
		}

		selected.setContext(CommonConstants.CONTEXT_RECO);
		selected.setContextId(selectedReco.getContextId());
		selected.setCrmDocReco(selectedReco);
		selected.setBukrs(selectedReco.getBukrs());
		selected.setBranchId(selectedReco.getBranchId());
		phoneNumbers = new ArrayList<>();

//		if (!Validation.isEmptyString(selectedReco.getMobPhone())) {
//			phoneNumbers.add(selectedReco.getMobPhone());
//		}
//
//		if (!Validation.isEmptyString(selectedReco.getHomePhone())) {
//			phoneNumbers.add(selectedReco.getHomePhone());
//		}
//
//		if (!Validation.isEmptyString(selectedReco.getWorkPhone())) {
//			phoneNumbers.add(selectedReco.getWorkPhone());
//		}

		selected.setDateTime(Calendar.getInstance().getTime());

		// @TODO Надо доработать
		selected.setCallerId(userData.getStaff().getStaff_id());
		dialogHeader = "Добавление звонка";
	}

	public void prepareCreate(CrmDocReco selectedReco, String phoneNumber) {
		prepareCreate(selectedReco);
		selected.setPhoneNumber(phoneNumber);
	}

	public void save() {
		try {
			if (selected.getCrmDocReco() != null) {
				getService().validate(selected, userData.getUserid());
				if (CallConstants.RESULT_POSITIVE.equals(selected.getResultId())) {
					demoCrudBean.prepareCreate(selected.getCrmDocReco());
					GeneralUtil.updateFormElement("DemoCreateUpdateDialog");
					GeneralUtil.showDialog("DemoCreateUpdateDialog");
				} else {
					getService().save(selected, userData.getUserid());
					selected = null;
					GeneralUtil.updateFormElement("CallCreateUpdateDialog");
					GeneralUtil.hideDialog("CallCreateUpdateDialog");
					GeneralUtil.addSuccessMessage("Данные сохранены успешно");
				}
			}

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void saveWithDemo() {
		try {
			getService().save(selected, demoCrudBean.getSelected(), userData.getUserid());
			demoCrudBean.setSelected(null);
			selected = null;
			GeneralUtil.updateFormElement("DemoCreateUpdateDialog");
			GeneralUtil.updateFormElement("CallCreateUpdateDialog");
			GeneralUtil.hideDialog("DemoCreateUpdateDialog");
			GeneralUtil.hideDialog("CallCreateUpdateDialog");
			GeneralUtil.addSuccessMessage("Данные сохранены успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	CrmCallService getService() {
		return (CrmCallService) appContext.getContext().getBean("crmCallService");
	}

	public void onResultChange() {
		reasons = new ArrayList<>();
		if (selected != null) {
			if (CallConstants.RESULT_REFUSE.equals(selected.getResultId())) {
				if (selected.getCrmDocReco() != null) {
					reasons = getReasonDao().findAllByType(ReasonConstants.TYPE_DEMO_CALL_REFUSE);
				}
			}
		}
	}

	private CrmReasonDao getReasonDao() {
		return (CrmReasonDao) appContext.getContext().getBean("crmReasonDao");
	}

	@ManagedProperty(value = "#{crmDemoCrudBean}")
	private crm.beans.demo.DemoCrudBean demoCrudBean;

	public crm.beans.demo.DemoCrudBean getDemoCrudBean() {
		return demoCrudBean;
	}

	public void setDemoCrudBean(crm.beans.demo.DemoCrudBean demoCrudBean) {
		this.demoCrudBean = demoCrudBean;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

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
