package service.szf;

import java.io.Serializable;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.services.ServAppService;
import general.services.ServFilterService;
import general.tables.Contract;
import general.tables.ServCRMHistory;
import general.tables.ServiceApplication;
import user.User;

@ManagedBean(name = "newServCRMHistoryBean")
@ViewScoped
public class NewServCrmHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3747854921469119166L;

	@PostConstruct
	public void init() {
		newSCH = new ServCRMHistory();
		Calendar cal = Calendar.getInstance();
		newSCH.setActionDate(cal.getTime());
		callState = "";
		callRespond = "";
		cancellCall = false;		
	}

	public void saveNewSCH() {
		try {
			ServFilterService sfsDao = (ServFilterService) appContext
					.getContext().getBean("servFilterService");
			newSCH.setInfo(getFullInfo());
			newSCH.setStaffId(userData.getStaff().getStaff_id());
			
			// *****************************APPLICATION*****************************
			if (addApplication) {
				newSA = new ServiceApplication();
				
				newSA.setAdate(newSCH.getActionDate());
				newSA.setApp_status(1L);

				if (transactionCode.equalsIgnoreCase("SZFLIST")) {
					newSA.setApp_type(7L); // Zamena Filtr
				} else if (transactionCode.equalsIgnoreCase("SZFVCLIST")) {
					newSA.setApp_type(6L); // Profilaktika
				}
				
				newSA.setApplicant_name(callRespond);
				newSA.setBranch_id(contract.getServ_branch_id());
				newSA.setBukrs(contract.getBukrs());
				newSA.setContract_number(contract.getContract_number());
				newSA.setCreated_by(userData.getUserid());
				newSA.setCustomer_id(contract.getCustomer_id());
				newSA.setInfo(newSCH.getInfo());
				
				createSA();				
			}
			
			if (sfsDao.createNewSCH(newSCH, userData, transactionID)) {
				GeneralUtil.addSuccessMessage("Call successfully saved!");				
			}
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}
	
	public void createSA() {
		try {
			if (newSA != null) {
				newSA.setId(null);
				// System.out.println("SA Type: " + newSA.getApp_type());
				if (newSA.getApp_type() != null && newSA.getApp_type() > 0) {
					// System.out.println("Inside CREATE ServApp!");
					ServAppService saServDao = (ServAppService) appContext
							.getContext().getBean("servAppService");
					saServDao.createServApp(newSA, userData.getUserid(),
							"SALIST");
					GeneralUtil.addMessage(
							"Success!",
							"Новая заявка "
									+ newSA.getApp_number()
									+ " успешно сохранена");
					GeneralUtil.hideDialog("ServAppCreateDialog");
					newSA = new ServiceApplication();					
				} else {
					// System.out.println("SA Type: " + newSA.getApp_type());
					GeneralUtil.addMessage("Empty Service Apllication type!",
							"Please select the type of Service Application");
					RequestContext reqCtx = RequestContext.getCurrentInstance();
					reqCtx.update("ServAppCreateForm");
				}
			}
		} catch (DAOException ex) {
			GeneralUtil.addMessage(null, ex.getMessage());
		}
	}

	ServiceApplication newSA = new ServiceApplication();

	public ServiceApplication getNewSA() {
		return newSA;
	}

	public void setNewSA(ServiceApplication newSA) {
		this.newSA = newSA;
	}
	
	public void cancelNewSCH() {
		
	}
	
	public String getFullInfo() {
		String info = callState;
		if (!Validation.isEmptyString(callRespond))
			info += ": " + callRespond;
		info += " | Info: " + newSCH.getInfo();
		return info;
	}

	// ****************************************************************************
	public ServCRMHistory newSCH;
	public Long transactionID;
	public String callRespond = "";
	public String callState = "";
	public boolean recallNextMonth;
	public boolean cancellCall;
	public boolean disCancelCall = true;
	public boolean addApplication;	
	public String transactionCode;
	public Contract contract;

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public boolean isAddApplication() {
		return addApplication;
	}

	public void setAddApplication(boolean addApplication) {
		this.addApplication = addApplication;
	}

	public boolean isDisCancelCall() {
		return disCancelCall;
	}

	public void setDisCancelCall(boolean disCancelCall) {
		this.disCancelCall = disCancelCall;
	}

	public boolean isRecallNextMonth() {
		return recallNextMonth;
	}

	public void setRecallNextMonth(boolean recallNextMonth) {
		this.recallNextMonth = recallNextMonth;
	}

	public boolean isCancellCall() {
		return cancellCall;
	}

	public void setCancellCall(boolean cancellCall) {
		this.cancellCall = cancellCall;
	}

	public String getCallState() {
		return callState;
	}

	public void setCallState(String callState) {
		this.callState = callState;
	}

	public String getCallRespond() {
		return callRespond;
	}

	public void setCallRespond(String callRespond) {
		this.callRespond = callRespond;
	}

	public Long getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(Long transactionID) {
		this.transactionID = transactionID;
	}

	public ServCRMHistory getNewSCH() {
		return newSCH;
	}

	public void setNewSCH(ServCRMHistory newSCH) {
		this.newSCH = newSCH;
	}

	// ****************************************************************************

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
