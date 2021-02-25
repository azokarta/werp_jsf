package service.szf;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.ServFilterService;
import general.tables.ServCRMHistory;
import general.tables.ServCRMSchedule;

import java.io.Serializable;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "newcrmsBean")
@ViewScoped
public class NewServCRMSchedule implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1842899481184385633L;
	
	@PostConstruct
	public void init() {
		newSCS = new ServCRMSchedule();
		Calendar cal = Calendar.getInstance();
		newSCS.setCreatedDate(cal.getTime());		
	}

	public void saveNewSCS() {
		try {
			ServFilterService sfsDao = (ServFilterService) appContext
					.getContext().getBean("servFilterService");
			newSCS.setStaffId(userData.getStaff().getStaff_id());
			newSCS.setStatus(new Long(ServCRMSchedule.STATUS_NEW));
			System.out.println(newSCS.getScheduledDate());
			if (sfsDao.createNewSCS(newSCS, userData, transactionID)) {
				GeneralUtil.addSuccessMessage("Call successfully scheduled!");
				addToHistory();
			}
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}
	
	private void addToHistory() {
		try {
			ServCRMHistory sch = new ServCRMHistory();
			sch.setActionDate(Calendar.getInstance().getTime());
			sch.setActionId(11L); //Call scheduled
			sch.setContractId(newSCS.getContractId());
			sch.setInfo("New call scheduled to: " + newSCS.getScheduledDate());
			sch.setStaffId(userData.getStaff().getStaff_id());
		
			ServFilterService sfsDao = (ServFilterService) appContext
					.getContext().getBean("servFilterService");
			if (sfsDao.createNewSCH(sch, userData, transactionID)) {
				GeneralUtil.addSuccessMessage("Call successfully saved!");				
			}
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}
	
	public void cancelNewSCS() {
		
	}
	
	// ****************************************************************************
	
	public ServCRMSchedule newSCS = new ServCRMSchedule();
	public Long transactionID;
	
	// ****************************************************************************
	
	public ServCRMSchedule getNewSCS() {
		return newSCS;
	}

	public void setNewSCS(ServCRMSchedule newSCS) {
		this.newSCS = newSCS;
	}

	public Long getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(Long transactionID) {
		this.transactionID = transactionID;
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
