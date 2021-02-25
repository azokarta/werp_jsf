package service.szf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.springframework.beans.BeanUtils;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.AddressDao;
import general.dao.BranchDao;
import general.dao.BukrsDao;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.ServCRMHistoryDao;
import general.dao.ServFilterDao;
import general.dao.ServFilterVCDao;
import general.dao.StaffDao;
import general.output.tables.ServCRMHistoryOutput;
import general.output.tables.ServFilterOutput;
import general.services.ServFilterService;
import general.services.ServFilterVCService;
import general.tables.Address;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.ContractType;
import general.tables.Customer;
import general.tables.ServCRMHistory;
import general.tables.ServFilter;
import general.tables.ServFilterVC;
import general.tables.Staff;
import user.User;

@ManagedBean(name = "crmhBean")
@ViewScoped
public class CrmHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 493777583378601861L;

	@PostConstruct
	public void init() {
		if (!Validation.isEmptyLong(contract_id)) {
			Search();
			prepareNewCRMHDialog();
			prepareNewCRMSDialog();
		}
	}

	public void Search() {
		ContractDao conDao = (ContractDao) appContext.getContext().getBean(
				"contractDao");
		contract = conDao.find(contract_id);

		CustomerDao cusDao = (CustomerDao) appContext.getContext().getBean(
				"customerDao");
		customer = cusDao.find(contract.getCustomer_id());
		
		StaffDao stfDao = (StaffDao) appContext.getContext()
				.getBean("staffDao");
		
		dealer = new Staff();
		if (!Validation.isEmptyLong(contract.getDealer()))
			dealer = stfDao.find(contract.getDealer());		

		BukrsDao bukrsDao = (BukrsDao) appContext.getContext().getBean(
				"bukrsDao");
		bukrs = bukrsDao.find(contract.getBukrs());

		BranchDao brDao = (BranchDao) appContext.getContext().getBean(
				"branchDao");
		branch = brDao.find(contract.getBranch_id());

		AddressDao addrDao = (AddressDao) appContext.getContext().getBean(
				"addressDao");
//		addrRab = new Address();
		if (!Validation.isEmptyLong(contract.getAddrServiceId())) {
			addrService = addrDao.find(contract.getAddrServiceId());
			if (contract.getAddrWorkId() != null && !Validation.isEmptyLong(contract.getAddrWorkId()))
				addrRab  = addrDao.find(contract.getAddrWorkId());
		}

		if (contract.getTovar_category() == ContractType.TOVARCAT_WATER_FILTER) {
			ServFilterDao sfDao = (ServFilterDao) appContext.getContext().getBean(
					"servFilterDao");
			sf = sfDao.findByContractID(contract_id);
			loadSFO();
		} else if (contract.getTovar_category() == ContractType.TOVARCAT_VACUUM_CLEANER) {
			ServFilterVCDao sfvcDao = (ServFilterVCDao) appContext.getContext().getBean(
					"servFilterVCDao");
			sfvc = sfvcDao.findByContractID(contract_id);
			loadVCSFO();
		}
		
		
		loadSCHOutput();
	}

	public void loadSFO() {
		sfo = new ServFilterOutput(0);
		BeanUtils.copyProperties(sf, sfo);
		Calendar cal = Calendar.getInstance();
		sfo.setF1(GeneralUtil.calcAgeinMonths(cal.getTime(),
				sfo.getF1_date_next()));
		sfo.setF2(GeneralUtil.calcAgeinMonths(cal.getTime(),
				sfo.getF2_date_next()));
		sfo.setF3(GeneralUtil.calcAgeinMonths(cal.getTime(),
				sfo.getF3_date_next()));
		sfo.setF4(GeneralUtil.calcAgeinMonths(cal.getTime(),
				sfo.getF4_date_next()));
		sfo.setF5(GeneralUtil.calcAgeinMonths(cal.getTime(),
				sfo.getF5_date_next()));
		
		if (addrService != null) {
			boolean tt = false;
			sfo.tel = "";
			if (!Validation.isEmptyString(addrService.getTelMob1())) {
				sfo.tel = addrService.getTelMob1();
				tt = true;
			}
			if (!Validation.isEmptyString(addrService.getTelMob2())) {
				if (tt)
					sfo.tel += ", ";
				sfo.tel += addrService.getTelMob2();
				tt = true;
			}
			if (!Validation.isEmptyString(addrService.getTelDom())) {
				if (tt)
					sfo.tel += ", ";
				sfo.tel += addrService.getTelDom();
			}
//			System.out.println("Tel: " + sfo.tel);
		}
		
		if (addrRab != null) {
			boolean tt = false;
			sfo.telRab = "";
			if (!Validation.isEmptyString(addrRab.getTelMob1())) {
				sfo.telRab = addrRab.getTelMob1();
				tt = true;
			}
			if (!Validation.isEmptyString(addrRab.getTelMob2())) {
				if (tt)
					sfo.telRab += ", ";
				sfo.telRab += addrRab.getTelMob2();
				tt = true;
			}
			if (!Validation.isEmptyString(addrRab.getTelDom())) {
				if (tt)
					sfo.telRab += ", ";
				sfo.telRab += addrRab.getTelDom();
			}
//			System.out.println("Tel: " + sfo.tel);
		}
	}
	
	public void loadVCSFO() {
		sfo = new ServFilterOutput(0);
//		ServFilterVC sf = new ServFilterVC();
		BeanUtils.copyProperties(sfvc, sfo);
		sf = new ServFilter();
		BeanUtils.copyProperties(sfvc, sf);
		
		Calendar cal = Calendar.getInstance();
		
		sfo.setF1(GeneralUtil.calcAgeinMonths(cal.getTime(),
				sfo.getF1_date_next()));
		
		
		if (addrService != null) {
			boolean tt = false;
			sfo.tel = "";
			if (!Validation.isEmptyString(addrService.getTelMob1())) {
				sfo.tel = addrService.getTelMob1();
				tt = true;
			}
			if (!Validation.isEmptyString(addrService.getTelMob2())) {
				if (tt)
					sfo.tel += ", ";
				sfo.tel += addrService.getTelMob2();
				tt = true;
			}
			if (!Validation.isEmptyString(addrService.getTelDom())) {
				if (tt)
					sfo.tel += ", ";
				sfo.tel += addrService.getTelDom();
			}
//			System.out.println("Tel: " + sfo.tel);
		}
		
		if (addrRab != null) {
			boolean tt = false;
			sfo.telRab = "";
			if (!Validation.isEmptyString(addrRab.getTelMob1())) {
				sfo.telRab = addrRab.getTelMob1();
				tt = true;
			}
			if (!Validation.isEmptyString(addrRab.getTelMob2())) {
				if (tt)
					sfo.telRab += ", ";
				sfo.telRab += addrRab.getTelMob2();
				tt = true;
			}
			if (!Validation.isEmptyString(addrRab.getTelDom())) {
				if (tt)
					sfo.telRab += ", ";
				sfo.telRab += addrRab.getTelDom();
			}
//			System.out.println("Tel: " + sfo.tel);
		}
	}

	public void loadSCHOutput() {
		crmHisL = new ArrayList<ServCRMHistoryOutput>();
		schL = getCRMHistory(contract_id);
		int i = 1;
		StaffDao stfDao = (StaffDao) appContext.getContext()
				.getBean("staffDao");
		for (ServCRMHistory sch : schL) {
			ServCRMHistoryOutput schO = new ServCRMHistoryOutput(i);
			schO.setSch(sch);
			if (!Validation.isEmptyLong(sch.getStaffId())) {
				Staff stf = stfDao.find(sch.getStaffId());
				schO.setStaff(stf);
			}
			crmHisL.add(schO);
			i++;
		}
	}

	public void refreshCrmhBean() {
		this.init();
		GeneralUtil.hideDialog("NewCallWidget");
		GeneralUtil.hideDialog("NewScheduleWidget");

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("CrmHistoryForm");
	}

	public void saveNewCall() {
		newServCRMHistoryBean.saveNewSCH();
		refreshCrmhBean();
	}

	public void saveNewSchedule() {
		newcrmsBean.saveNewSCS();
		refreshCrmhBean();
	}

	public void prepareNewCRMHDialog() {
		newServCRMHistoryBean.init();
		newServCRMHistoryBean.getNewSCH().setContractId(contract_id);
		newServCRMHistoryBean.getNewSCH().setActionId(10L);
		newServCRMHistoryBean.setTransactionCode(transactionCode);
		newServCRMHistoryBean.setTransactionID(transactionID);
		newServCRMHistoryBean.setContract(contract);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewCallForm");
	}

	public void prepareNewCRMSDialog() {
		newcrmsBean.init();
		newcrmsBean.getNewSCS().setContractId(contract_id);
		newcrmsBean.getNewSCS().setBranchId(contract.getServ_branch_id());
		newcrmsBean.getNewSCS().setBukrs(bukrs.getBukrs());
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewScheduleForm");
	}

	public List<ServCRMHistory> getCRMHistory(Long contract_id) {
		ServCRMHistoryDao crmhDao = (ServCRMHistoryDao) appContext.getContext()
				.getBean("servCRMHistoryDao");
		List<ServCRMHistory> crmhL = crmhDao.findAllByContractID(contract_id);
		return crmhL;
	}

	public void changeCRMCategory() {
		try {
			if (contract.getTovar_category() == ContractType.TOVARCAT_WATER_FILTER) {
				
				ServFilterService sfService = (ServFilterService) appContext.getContext().getBean("servFilterService");
				
				sfService.updateSF(sf, userData, transactionID);
				
			} else if (contract.getTovar_category() == ContractType.TOVARCAT_VACUUM_CLEANER) {
				ServFilterVCService sfvcService = (ServFilterVCService) appContext.getContext().getBean("sfvcService");
				BeanUtils.copyProperties(sf, sfvc);
				sfvcService.updateSF(sfvc, userData, transactionID);
			}
			
			GeneralUtil.addSuccessMessage("Category successfully changed!");
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}
	
	public Long transactionID;
	public String transactionCode;
	
	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public Long getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(Long transactionID) {
		this.transactionID = transactionID;
	}

	public Long contract_id;
	public Contract contract;
	public Customer customer;
	public Staff dealer;
	public Address addrService;
	public Address addrRab;
	public Bukrs bukrs;
	public Branch branch;
	public ServFilterOutput sfo;
	public ServFilter sf;
	public ServCRMHistory selectedCH;
	public List<ServCRMHistoryOutput> crmHisL;
	public List<ServCRMHistory> schL;
	
	public ServFilterVC sfvc;

	public ServFilterVC getSfvc() {
		return sfvc;
	}

	public void setSfvc(ServFilterVC sfvc) {
		this.sfvc = sfvc;
	}

	public List<ServCRMHistoryOutput> getCrmHisL() {
		return crmHisL;
	}

	public void setCrmHisL(List<ServCRMHistoryOutput> crmHisL) {
		this.crmHisL = crmHisL;
	}

	public List<ServCRMHistory> getSchL() {
		return schL;
	}

	public void setSchL(List<ServCRMHistory> schL) {
		this.schL = schL;
	}

	public ServFilter getSf() {
		return sf;
	}

	public void setSf(ServFilter sf) {
		this.sf = sf;
	}

	public ServCRMHistory getSelectedCH() {
		return selectedCH;
	}

	public void setSelectedCH(ServCRMHistory selectedCH) {
		this.selectedCH = selectedCH;
	}

	public ServFilterOutput getSfo() {
		return sfo;
	}

	public void setSfo(ServFilterOutput sfo) {
		this.sfo = sfo;
	}

	public Bukrs getBukrs() {
		return bukrs;
	}

	public void setBukrs(Bukrs bukrs) {
		this.bukrs = bukrs;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Long getContract_id() {
		return contract_id;
	}

	public void setContract_id(Long contract_id) {
		this.contract_id = contract_id;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Staff getDealer() {
		return dealer;
	}

	public void setDealer(Staff dealer) {
		this.dealer = dealer;
	}

	public Address getAddrService() {
		return addrService;
	}

	public void setAddrService(Address addrService) {
		this.addrService = addrService;
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

	// **********************ServiceCRMHistory***************************
	@ManagedProperty(value = "#{newServCRMHistoryBean}")
	private NewServCrmHistory newServCRMHistoryBean;

	public NewServCrmHistory getNewServCRMHistoryBean() {
		return newServCRMHistoryBean;
	}

	public void setNewServCRMHistoryBean(NewServCrmHistory newServCRMHistoryBean) {
		this.newServCRMHistoryBean = newServCRMHistoryBean;
	}

	// **********************ServiceCRMSchedule***************************
	@ManagedProperty(value = "#{newcrmsBean}")
	private NewServCRMSchedule newcrmsBean;

	public NewServCRMSchedule getNewcrmsBean() {
		return newcrmsBean;
	}

	public void setNewcrmsBean(NewServCRMSchedule newcrmsBean) {
		this.newcrmsBean = newcrmsBean;
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
