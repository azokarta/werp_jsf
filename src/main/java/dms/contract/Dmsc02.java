package dms.contract;

import f4.AddrTypeF4;
import f4.BranchF4;
import f4.BukrsF4;
import f4.CityF4;
import f4.CityregF4;
import f4.ContractStatusF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.MatnrF4;
import f4.PaymentTemplateF4;
import f4.PriceListF4;
import f4.StateF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.MessageProvider;
import general.PermissionController;
import general.Validation;
import general.dao.AddressDao;
import general.dao.BkpfDao;
import general.dao.BonusDao;
import general.dao.ContractDao;
import general.dao.ContractPromosDao;
import general.dao.ContractTypeDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.MatnrListDao;
import general.dao.PaymentScheduleDao;
import general.dao.PriceListDao;
import general.dao.StaffDao;
import general.dao.SubCompanyDao;
import general.dao.TempPayrollArchiveDao;
import general.dao.UserRoleDao;
import general.dao.WerksBranchDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchTypeFilter;
import general.services.AddressService;
import general.services.ContractService;
import general.services.CustomerService;
import general.services.PromotionService;
import general.services.StaffService;
import general.tables.Address;
import general.tables.AddressType;
import general.tables.Bkpf;
import general.tables.Bonus;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.City;
import general.tables.Cityreg;
import general.tables.Contract;
import general.tables.ContractHistory;
import general.tables.ContractPromos;
import general.tables.ContractStatus;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.Currency;
import general.tables.Customer;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.PaymentSchedule;
import general.tables.PaymentTemplate;
import general.tables.PriceList;
import general.tables.Promotion;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.State;
import general.tables.SubCompany;
import general.tables.TempPayrollArchive;
import general.tables.UserRole;
import general.tables.Werks;
import general.validators.ContractValidator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.beans.BeanUtils;

import user.User;
import datamodels.ContractModel;
import dms.promotion.PromoTable;

@ManagedBean(name = "dmsc02Bean", eager = true)
@ViewScoped
public class Dmsc02 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7273436618818482217L;
	private final static String transaction_code = "DMSC02";
	private final static Long transaction_id = 21L;

	// ******************************************************************

	public void disableMatnrSelect() {
		disSelMatnr = true;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:matnrSelect");
		// reqCtx.update("form:clearMatnr");
	}

	public void enableMatnrSelect() {
		disSelMatnr = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:matnrSelect");
		// reqCtx.update("form:clearMatnr");
	}

	public void checkConLastState() {
		if (p_contract.getLast_state() == 1 || p_contract.getLast_state() == 3)
			enableMatnrSelect();
		else
			disableMatnrSelect();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:matnrSelect");
	}

	public void clearMatnrList() {
		p_contract.setMatnr_list_id(null);
		p_contract.setTovar_serial(null);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tovarSerial");
	}


	public void GoToEditPrice() {
		GeneralUtil
				.doRedirect("/dms/contract/editpricedmsc.xhtml?contract_number="
						+ p_contract.getContract_number());
	}
	// *************************************************************************
	public List<MatnrList> matnrListDetail = new ArrayList<MatnrList>();

	public List<MatnrList> getMatnrListDetail() {
		return matnrListDetail;
	}

	public void setMatnrListDetail(List<MatnrList> matnrListDetail) {
		this.matnrListDetail = matnrListDetail;
	}

	public List<MatnrList> mlDetail = new ArrayList<MatnrList>();

	public List<MatnrList> getMlDetail() {
		return mlDetail;
	}

	public void setMlDetail(List<MatnrList> mlDetail) {
		this.mlDetail = mlDetail;
	}

	public MatnrList selectedML = new MatnrList();

	public MatnrList getSelectedML() {
		return selectedML;
	}

	public void setSelectedML(MatnrList selectedML) {
		this.selectedML = selectedML;
	}

	MatnrList mlSearch = new MatnrList();

	public MatnrList getMlSearch() {
		return mlSearch;
	}

	public void setMlSearch(MatnrList mlSearch) {
		this.mlSearch = mlSearch;
	}

	public void refreshMatnrListDlg() {
		mlDetail = new ArrayList<MatnrList>();
		if (!Validation.isEmptyString(mlSearch.getBarcode())) {
			for (MatnrList m : matnrListDetail) {
				if (!Validation.isEmptyString(m.getBarcode())) {
					if (m.getBarcode()
							.toLowerCase()
							.matches(mlSearch.getBarcode().toLowerCase() + ".*"))
						mlDetail.add(m);
				}
			}
		} else {
			mlDetail.addAll(matnrListDetail);
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("MatnrListForm:matnrListInWerksDlgTable");
	}

	public void assignSelectedMatnrList() {
		MatnrListChange();
		p_contract.setMatnr_list_id(selectedML.getMatnr_list_id());
		p_contract.setTovar_serial(selectedML.getBarcode());
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tovarSerial");
	}

	public void MatnrListChange() {
		try {
			removeCHLif(30L);
			String wa_info = "";
			Matnr matnr = p_matnrF4Bean.getL_matnr_map().get(
					new_cd.getContrType().getMatnr());

			MatnrListDao mlDao = (MatnrListDao) appContext.getContext()
					.getBean("matnrListDao");
			MatnrList oldML = new MatnrList();
			if (p_oldcontract.getMatnr_list_id() != null
					&& p_oldcontract.getMatnr_list_id() > 0)
				oldML = mlDao.find(p_oldcontract.getMatnr_list_id());
			MatnrList newML = selectedML;

			if (Validation.isEmptyString(oldML.getBarcode()))
				oldML.setBarcode(" ");
			if (Validation.isEmptyString(newML.getBarcode()))
				newML.setBarcode(" ");

			if (newML != null
					&& (!newML.getBarcode().equals(oldML.getBarcode()))) {
				ContractHistory wa_ch = new ContractHistory(
						p_contract.getContract_id());

				wa_ch.setOld_id(p_oldcontract.getMatnr_list_id());
				wa_ch.setNew_id(p_contract.getMatnr_list_id());
				wa_ch.setOper_on(30L);

				wa_ch.setOper_type(3L);
				wa_info = "Matnr Serial Number changed. // User: "
						+ userData.getUsername();

				wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
				wa_ch.setInfo(wa_info);
				wa_ch.setProcessed(1);
				wa_ch.setUser_id(userData.getUserid());
				wa_ch.setOld_text(oldML.getBarcode());
				wa_ch.setNew_text(newML.getBarcode());

				chL.add(wa_ch);

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:contractHistoryTable");

				enable_save_button();
			} else {
				p_contract.setMatnr_list_id(p_oldcontract.getMatnr_list_id());
				p_contract.setTovar_serial(p_oldcontract.getTovar_serial());
				throw new DAOException("Mantr Serial Number cannot be empty!");
			}
		} catch (DAOException ex) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:contractHistoryTable");
			GeneralUtil.addMessage("", ex.getMessage());
		}

	}

	public void loadWerksMatnrList() {
		clearMatnrListAll();
		matnrListDetail = new ArrayList<MatnrList>();

		ContractTypeDao ctDao = (ContractTypeDao) appContext.getContext()
				.getBean("contractTypeDao");
		ContractType ct = ctDao.find(p_contract.getContract_type_id());

		Matnr matnr = p_matnrF4Bean.getL_matnr_map().get(ct.getMatnr());
		WerksBranchDao wbDao = (WerksBranchDao) appContext.getContext()
				.getBean("werksBranchDao");
		Branch br = p_branchF4Bean.getL_branch_map().get(
				p_contract.getBranch_id());
		List<Werks> wl = wbDao.findAllWerksByBranch(br.getParent_branch_id());

		MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean(
				"matnrListDao");
		List<MatnrList> ml = new ArrayList<MatnrList>();
		System.out.println("Werks found: " + wl.size());
		for (Werks w : wl) {
			System.out.println(w.getText45());
			String cond = String
					.format(" bukrs = '%s' AND status IN('%s','%s') AND matnr = %d and werks = %d",
							p_contract.getBukrs(),
							MatnrList.STATUS_ACCOUNTABILITY,
							MatnrList.STATUS_RECEIVED, ct.getMatnr(),
							w.getWerks());

			ml = mlDao.findAllWithStaff(cond);
			if (ml.size() > 0)
				matnrListDetail.addAll(ml);
		}

		for (MatnrList ml2 : matnrListDetail) {
			ml2.setMatnrObject(matnr);
		}
		mlSearch = new MatnrList();
		refreshMatnrListDlg();
	}

	public void clearMatnrListAll() {
		mlSearch = new MatnrList();
		matnrListDetail = new ArrayList<MatnrList>();
		mlDetail = new ArrayList<MatnrList>();
		selectedML = new MatnrList();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("MatnrListForm");
	}

	public boolean checkStaffPayrol() {
		TempPayrollArchiveDao tpaDao = (TempPayrollArchiveDao) appContext
				.getContext().getBean("tmpPrlArcDao");
		int mon = p_contract.getContract_date().getMonth() + 1;
		int year = p_contract.getContract_date().getYear() + 1900;
		String con = " bukrs = " + p_contract.getBukrs() + " and monat = "
				+ mon + " and gjahr = " + year;
		List<TempPayrollArchive> tpa = tpaDao.dynamicSearch(con);
		System.out.println("Year: " + year + "      Month: " + mon);
		System.out.println("Payroll size: " + tpa.size());
		if (tpa.size() > 0)
			return true;
		else
			return false;
	}

	// *****************************************************************************

	@PostConstruct
	public void init() {

		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return; // Skip ajax requests.
		}

		disable_save_button();
		dis_field1 = true;
		dis_field2 = false;
		ref_textClass = "noteRegular";
		try {
			PermissionController.canWrite(userData, this.transaction_id);
			initContractModel();
			if (contract_number != null) {
				contract_number_search = contract_number;
				System.out
						.println("Contract number: " + contract_number_search);

				to_search();

				contract_number = null;
				// System.out.println("SelectedPrice: " +
				// selectedPrice.getPrice());

				outputTable.searchModel.setMarkedType(Contract.MT_STANDARD_PRICE);
				outputTable.searchModel.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:branchref");
				reqCtx.update("form:dealerref");
			}		
			
		} catch (DAOException ex) {
			contract_number = null;
			GeneralUtil.addMessage("Error", ex.getMessage());
			GeneralUtil.doRedirect("/general/mainPage.xhtml");
		}
	}

	public List<Branch> loadBranchList(String a_bukr, int ba) {
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(p_contract.getBukrs()));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
		if (ba >= 0)
			bf.addFilter(new BranchBusinessAreaFilter(ba));
		return bf.filterBranch(p_branchF4Bean.getBranch_list());
	}

	public void loadLEList() {
		SubCompanyDao scDao = (SubCompanyDao) appContext.getContext().getBean("subCompanyDao");
		List<SubCompany> scL = new ArrayList<SubCompany>();
		scL = scDao.findAll();
		
		sc_list.clear();
		
		for (SubCompany sc:scL) {
			if (!Validation.isEmptyString(sc.getBukrs())) {
				if (sc.getBukrs().equals(p_contract.getBukrs())
						&& (sc.getClosed_date() == null
						|| sc.getClosed_date().after(p_contract.getContract_date()))) {
					sc_list.add(sc);
				}
			}
		}		
		System.out.println("Bukrs: " + p_contract.getBukrs() + "    IP found: " + sc_list.size());
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:subcompany");
	}
	
	List<SubCompany> sc_list = new ArrayList<SubCompany>();
	
	public List<SubCompany> getSc_list() {
		return sc_list;
	}
	
	public List<Branch> loadSalesBranchList(String a_bukr) {
		return loadBranchList(a_bukr, 0);
	}

	public List<Branch> loadServBranchList(String a_bukr) {
		return loadBranchList(a_bukr, 5);
	}

	public void finBranchChange() {
		try {
			removeCHLif(110L);

			String wa_info = "";
			Branch wa_new_br = p_branchF4Bean.getL_branch_map().get(
					p_contract.getFinBranchId());
			Branch wa_old_br = p_branchF4Bean.getL_branch_map().get(
					p_oldcontract.getFinBranchId());

			if (wa_old_br == null)
				wa_old_br = new Branch();
			ContractHistory wa_ch = new ContractHistory(
					p_contract.getContract_id());

			wa_ch.setOld_id(p_oldcontract.getFinBranchId());
			wa_ch.setNew_id(p_contract.getFinBranchId());
			wa_ch.setOper_on(110L);
			wa_ch.setOper_type(3L);

			if (wa_new_br != null) {
				wa_info = "Finance-Branch changed. // User: "
						+ userData.getStaffDisplayName();
			} else {
				p_contract.setFinBranchId(p_oldcontract.getFinBranchId());
				throw new DAOException("Finance-Branch cannot be empty!");
			}

			wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
			wa_ch.setInfo(wa_info);
			wa_ch.setProcessed(1);
			wa_ch.setUser_id(userData.getUserid());
			wa_ch.setOld_text(wa_old_br.getText45());
			wa_ch.setNew_text(wa_new_br.getText45());

			chL.add(wa_ch);

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:contractHistoryTable");

			enable_save_button();
		} catch (DAOException ex) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:contractHistoryTable");
			GeneralUtil.addMessage("", ex.getMessage());
		}
	}

	public void servBranchChange() {
		try {
			removeCHLif(100L);

			String wa_info = "";
			Branch wa_new_br = p_branchF4Bean.getL_branch_map().get(
					p_contract.getServ_branch_id());
			Branch wa_old_br = p_branchF4Bean.getL_branch_map().get(
					p_oldcontract.getServ_branch_id());

			if (wa_old_br == null)
				wa_old_br = new Branch();
			ContractHistory wa_ch = new ContractHistory(
					p_contract.getContract_id());

			wa_ch.setOld_id(p_oldcontract.getServ_branch_id());
			wa_ch.setNew_id(p_contract.getServ_branch_id());
			wa_ch.setOper_on(100L);
			wa_ch.setOper_type(3L);

			if (wa_new_br != null) {
				wa_info = "Service-Branch changed. // User: "
						+ userData.getStaffDisplayName();
			} else {
				p_contract.setServ_branch_id(p_oldcontract.getServ_branch_id());
				throw new DAOException("Service-Branch cannot be empty!");
			}

			wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
			wa_ch.setInfo(wa_info);
			wa_ch.setProcessed(1);
			wa_ch.setUser_id(userData.getUserid());
			wa_ch.setOld_text(wa_old_br.getText45());
			wa_ch.setNew_text(wa_new_br.getText45());

			chL.add(wa_ch);

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:contractHistoryTable");

			enable_save_button();
		} catch (DAOException ex) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:contractHistoryTable");
			GeneralUtil.addMessage("", ex.getMessage());
		}
	}

	public void addressChange(int a) {
		String wa_info = "";
		String old_addr = "";
		String new_addr = "";
		Long operCode = 0L;
		ContractHistory wa_ch = new ContractHistory(p_contract.getContract_id());

		if (a == 1) {
			wa_info = "Home Address was changed!";
			old_addr = addrHome2.getAddress();
			new_addr = addrHome.getAddress();
			if (Validation.isEmptyString(addrHome2.getAddress()))
				old_addr = "";
			if (Validation.isEmptyString(addrHome.getAddress()))
				new_addr = "";
			operCode = 201L;
		} else if (a == 2) {
			wa_info = "Work Address was changed!";
			old_addr = addrWork2.getAddress();
			new_addr = addrWork.getAddress();
			if (Validation.isEmptyString(addrWork2.getAddress()))
				old_addr = "";
			if (Validation.isEmptyString(addrWork.getAddress()))
				new_addr = "";
			operCode = 202L;
		} else if (a == 3) {
			wa_info = "Service Address was changed!";
			old_addr = addrService2.getAddress();
			new_addr = addrService.getAddress();
			if (Validation.isEmptyString(addrService2.getAddress()))
				old_addr = "";
			if (Validation.isEmptyString(addrService.getAddress()))
				new_addr = "";
			operCode = 203L;
		}

		wa_ch.setOld_text(old_addr);
		wa_ch.setNew_text(new_addr);
		wa_ch.setOper_on(operCode);
		wa_ch.setOper_type(3L);
		wa_info += " (User: " + userData.getUsername() + ")";
		wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
		wa_ch.setInfo(wa_info);
		wa_ch.setUser_id(userData.getUserid());
		wa_ch.setProcessed(1);

		removeCHLif(operCode);
		if (old_addr.equals(new_addr) == false) {
			chL.add(wa_ch);
			enable_save_button();
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:contractHistoryTable");
	}

	// **************************************************************************************
	public void to_search_staff() {
		try {
			Salary p_salary = new Salary();
			p_salary.setBukrs(p_contract.getBukrs());
			// System.out.println("Staff Branch ID: " +
			// searchStaff.getBranch_id());
			p_salary.setBranch_id(searchStaff.getBranch_id());
			p_salary.setPosition_id((long) p_search_position_id);
			if (p_contract.getContract_status_id() == ContractStatus.STATUS_GIFT // 2
					|| p_contract.getContract_status_id() == ContractStatus.STATUS_CANCELLED // 3
			// || p_contract.getContract_status_id() ==
			// ContractStatus.STATUS_CLOSED //5
			) {

				GeneralUtil
						.addMessage("Info",
								"Employee not applicable for the chosen contract status");
				return;
			}

			StaffService staffService = (StaffService) appContext.getContext()
					.getBean("staffService");
			p_staff_list = staffService.dynamicSearchStaffSalary(searchStaff,
					p_salary);

			if (p_staff_list.size() == 0) {
				p_staff_list = new ArrayList<Staff>();
				GeneralUtil.addMessage("Инфо", "Не найдено.");
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");

		} catch (DAOException ex) {
			p_staff_list = new ArrayList<Staff>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");
			GeneralUtil.addMessage("Error", ex.getMessage());
		}
	}

	public void assignFoundEmployee() {
		String fio = "";
		String oldFio = "";
		String wa_info = "";
		ContractHistory wa_ch = new ContractHistory(p_contract.getContract_id());
		StaffDao stfDao = (StaffDao) appContext.getContext()
				.getBean("staffDao");

		if (selectedStaff != null && selectedStaff.getStaff_id() != null) {
			fio = selectedStaff.getLF();

			if (p_selected_position_id == 4) {
				removeCHLif(40L);
				if (!selectedStaff.getStaff_id().equals(
						p_oldcontract.getDealer())) {
					wa_ch.setOld_id(p_contract.getDealer());
					wa_ch.setNew_id(selectedStaff.getStaff_id());
					wa_ch.setOper_on(40L);
					if (p_oldcontract.getDealer() != null
							&& p_oldcontract.getDealer() > 0)
						wa_ch.setOper_type(3L);
					else
						wa_ch.setOper_type(1L);
					if(p_oldcontract.getDealer()!=null)
					{
						oldFio = stfDao.find(p_oldcontract.getDealer()).getLF();
					}					
					wa_ch.setOld_text(oldFio);
					wa_ch.setNew_text(fio);
					wa_info = "Dealer changed.";
					wa_info += " (User: " + userData.getUsername() + ")";
					wa_info = wa_info.substring(0,
							Math.min(wa_info.length(), 125));
					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(0);
					wa_ch.setUser_id(userData.getUserid());
					chL.add(wa_ch);
				}

				p_contract.setDealer(selectedStaff.getStaff_id());
				p_fioDealer = fio;

			} else if (p_selected_position_id == 8) {
				removeCHLif(50L);
				if (!selectedStaff.getStaff_id().equals(
						p_oldcontract.getDemo_sc())) {
					wa_ch.setOld_id(p_contract.getDemo_sc());
					wa_ch.setNew_id(selectedStaff.getStaff_id());
					wa_ch.setOper_on(50L);

					if (p_oldcontract.getDemo_sc() != null
							&& p_oldcontract.getDemo_sc() > 0) {
						wa_ch.setOper_type(3L);
						oldFio = stfDao.find(p_oldcontract.getDemo_sc())
								.getLF();
						wa_info = "Demo-secretary changed.";
					} else {
						wa_ch.setOper_type(1L);
						wa_info = "Demo-secretary " + fio + " assigned.";
					}

					wa_info += " (User: " + userData.getUsername() + ")";
					wa_info = wa_info.substring(0,
							Math.min(wa_info.length(), 125));
					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(0);
					wa_ch.setOld_text(oldFio);
					wa_ch.setNew_text(fio);
					wa_ch.setUser_id(userData.getUserid());
					chL.add(wa_ch);
				}
				p_contract.setDemo_sc(selectedStaff.getStaff_id());
				p_fioDemoSec = fio;
			} else if (p_selected_position_id == 9) {
				removeCHLif(60L);
				if (!selectedStaff.getStaff_id().equals(
						p_oldcontract.getCollector())) {
					wa_ch.setOld_id(p_contract.getCollector());
					wa_ch.setNew_id(selectedStaff.getStaff_id());
					wa_ch.setOper_on(60L);
					if (p_oldcontract.getCollector() != null
							&& p_oldcontract.getCollector() > 0) {
						oldFio = stfDao.find(p_oldcontract.getCollector())
								.getLF();
						wa_ch.setOper_type(3L);
						wa_info = "Collector changed.";
					} else {
						wa_ch.setOper_type(1L);
						wa_info = "Collector " + fio + " is assigned.";
					}

					wa_info += " (User: " + userData.getUsername() + ")";
					wa_info = wa_info.substring(0,
							Math.min(wa_info.length(), 125));
					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(0);
					wa_ch.setOld_text(oldFio);
					wa_ch.setNew_text(fio);
					wa_ch.setUser_id(userData.getUserid());
					chL.add(wa_ch);
				}
				p_contract.setCollector(selectedStaff.getStaff_id());
				p_fioColl = fio;
			}
			/*
			 * else if (p_selected_position_id == 11) {
			 * p_contract.setFitter(selectedStaff.getStaff_id()); p_fioFitter =
			 * fio; }
			 */
		} else {
			if (p_selected_position_id == 4) {
				if (p_oldcontract.getDealer() != null
						&& p_oldcontract.getDealer() > 0) {
					wa_ch.setOld_id(p_contract.getDealer());
					wa_ch.setOper_on(40L);
					wa_ch.setOper_type(2L);

					oldFio = stfDao.find(p_oldcontract.getDealer()).getLF();
					wa_info = "Dealer: " + oldFio + " is removed.";
					wa_info += " (User: " + userData.getUsername() + ")";
					wa_info = wa_info.substring(0,
							Math.min(wa_info.length(), 125));
					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(0);
					wa_ch.setOld_text(oldFio);
					wa_ch.setNew_text(fio);
					wa_ch.setUser_id(userData.getUserid());
					chL.add(wa_ch);

					p_contract.setDealer(null);
					p_fioDealer = fio;
				}
			} else if (p_selected_position_id == 8) {
				if (p_oldcontract.getDemo_sc() != null
						&& p_oldcontract.getDemo_sc() > 0) {
					wa_ch.setOld_id(p_contract.getDemo_sc());
					wa_ch.setOper_on(50L);
					wa_ch.setOper_type(2L);

					oldFio = stfDao.find(p_oldcontract.getDemo_sc()).getLF();
					wa_info = "Demo-secretary: " + oldFio + " is removed.";
					wa_info += " (User: " + userData.getUsername() + ")";
					wa_info = wa_info.substring(0,
							Math.min(wa_info.length(), 125));
					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(0);
					wa_ch.setOld_text(oldFio);
					wa_ch.setNew_text(fio);
					wa_ch.setUser_id(userData.getUserid());
					chL.add(wa_ch);

					p_contract.setDemo_sc(null);
					p_fioDemoSec = fio;
				}
			} else if (p_selected_position_id == 9) {
				if (p_oldcontract.getCollector() != null
						&& p_oldcontract.getCollector() > 0)
					wa_ch.setOld_id(p_contract.getCollector());
				wa_ch.setOper_on(60L);
				wa_ch.setOper_type(2L);

				oldFio = stfDao.find(p_oldcontract.getCollector()).getLF();
				wa_info = "Collector: " + oldFio + " is removed.";
				wa_info += " (User: " + userData.getUsername() + ")";
				wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
				wa_ch.setInfo(wa_info);
				wa_ch.setProcessed(0);
				wa_ch.setOld_text(oldFio);
				wa_ch.setNew_text(fio);
				wa_ch.setUser_id(userData.getUserid());
				chL.add(wa_ch);

				p_contract.setCollector(null);
				p_fioColl = fio;
			}
		}

		enable_save_button();

		selectedStaff = new Staff();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:demo_sc");
		reqCtx.update("form:dealer");
		reqCtx.update("form:collector");
		reqCtx.update("form:fitter");
		reqCtx.update("form:contractHistoryTable");
	}

	public void setSearchPositionId(int a_pos_id) {
		p_selected_position_id = a_pos_id;
		p_staff_list = new ArrayList<Staff>();
		searchStaff = new Staff();
		searchStaff.setBranch_id(p_contract.getBranch_id());
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:staffBranch");
		reqCtx.update("form:staffTable");
		reqCtx.update("form:se_pos");
	}

	public void statusChanged() {

		List<UserRole> urL = new ArrayList<UserRole>();
		UserRoleDao urlDao = (UserRoleDao) appContext.getContext().getBean(
				"userRoleDao");
		urL = urlDao.findUserRoles(userData.getUserid());

		boolean isCoordinator = false;
		// System.out.println("Role list: " + urL.size());
		for (UserRole wa_ur : urL) {
			// System.out.println(wa_ur.getRoleId());
			if (wa_ur.getRoleId() == 25 || wa_ur.getRoleId() == 19
					|| wa_ur.getRoleId() == 115 || userData.isSys_admin()) {
				isCoordinator = true;
				// System.out.println("IS COORDINATOR!");
			}
		}
		System.out.println("IS COORDINATOR: " + isCoordinator);

		new_adding_priceCheck = true;
		if (p_contract.getContract_status_id() == initial_contract_status_id) {
			assignInitials();
		} else if (p_contract.getContract_status_id() != ContractStatus.STATUS_CANCELLED
				&& p_contract.getContract_status_id() != ContractStatus.STATUS_CLOSED
				&& ((p_contractStatusF4Bean.getCsMap()
						.get(p_contract.getContract_status_id())
						.getAccessLevel() == 1 && isCoordinator) || (p_contractStatusF4Bean
						.getCsMap().get(p_contract.getContract_status_id())
						.getAccessLevel() == 0))) {
			assignInitials();
			if (p_contract.getContract_status_id() == ContractStatus.STATUS_FORCANCEL)
				GeneralUtil.addMessage("Запрос на отмену отправлен!",
						"Договор будет отменен после одобрения координатора!");

			removeCHLif(1L);
			ContractHistory wa_ch = new ContractHistory(
					p_contract.getContract_id());
			wa_ch.setContract_id(p_contract.getContract_id());
			String oldCS = p_contractStatusF4Bean.getCsMap()
					.get(initial_contract_status_id).getName();
			String newCS = p_contractStatusF4Bean.getCsMap()
					.get(p_contract.getContract_status_id()).getName();
			wa_ch.setInfo("Status changed from \"" + oldCS + "\" to \"" + newCS
					+ "\" by user " + userData.getStaffDisplayName());
			wa_ch.setOld_id(initial_contract_status_id);
			wa_ch.setNew_id(p_contract.getContract_status_id());
			wa_ch.setOld_text(oldCS);
			wa_ch.setNew_text(newCS);
			wa_ch.setOper_type(3L);
			wa_ch.setOper_on(1L);
			wa_ch.setUser_id(userData.getUserid());
			wa_ch.setRec_date(Calendar.getInstance().getTime());
			chL.add(wa_ch);
		} else {
			p_contract.setContract_status_id(initial_contract_status_id);
			assignInitials();
			new_adding_priceCheck = true;
			GeneralUtil.addMessage("Info", "Not applicable contract status");
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public void doResold() {
			if (initial_contract_status_id == 5) {
				p_contract.setContract_status_id(17L);
				
				removeCHLif(1L);
				ContractHistory wa_ch = new ContractHistory(
						p_contract.getContract_id());
				wa_ch.setContract_id(p_contract.getContract_id());
				String oldCS = p_contractStatusF4Bean.getCsMap()
						.get(initial_contract_status_id).getName();
				String newCS = p_contractStatusF4Bean.getCsMap()
						.get(p_contract.getContract_status_id()).getName();
				wa_ch.setInfo("Status changed from \"" + oldCS + "\" to \"" + newCS
						+ "\" by user " + userData.getStaffDisplayName());
				wa_ch.setOld_id(initial_contract_status_id);
				wa_ch.setNew_id(p_contract.getContract_status_id());
				wa_ch.setOld_text(oldCS);
				wa_ch.setNew_text(newCS);
				wa_ch.setOper_type(3L);
				wa_ch.setOper_on(1L);
				wa_ch.setUser_id(userData.getUserid());
				wa_ch.setRec_date(Calendar.getInstance().getTime());
				chL.add(wa_ch);
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
			} else {
				GeneralUtil.addErrorMessage("Договор не полностью погашен! Статус договора не позволяет!");
				System.out.println("Договор не полностью погашен! Статус договора не позволяет!");
			}
	}
	
	private ContractService getConServ() {
		return (ContractService) appContext.getContext().getBean(
				"contractService");
	}

	private ContractValidator getConVal() {
		return (ContractValidator) appContext.getContext().getBean(
				"contractValidator");
	}

	public void to_update() {
		try {
			PermissionController.canWrite(userData, this.transaction_id);

			Calendar cal = Calendar.getInstance();
			p_contract.setUpdated_date(cal.getTime());
			p_contract.setUpdated_by(userData.getUserid());

			System.out.println("SAVING :");

			boolean update10 = true;
			boolean update20 = true;
			boolean update30 = true;
			boolean update80 = true;
			boolean update90 = true;
			boolean update40 = true;
			boolean updateDefault = true;

			for (ContractHistory wa_ch : chL) {
				switch (wa_ch.getOper_on().intValue()) {
				case 20: {
					if (update20) {
						System.out.println("Price is being updated.");

						update20 = false;
					}
					break;
				}
				case 80: {
					if (update80) {
						System.out
								.println("Promo-campaigns are being updated.");

						List<Promotion> wa_promoList = new ArrayList<Promotion>();
						int k = 0;
						for (PromoTable wa_pt : selectedPTS) {
							wa_promoList.add(wa_pt.getPromo());
						}

						List<ContractHistory> promochL = new ArrayList<ContractHistory>();
						List<ContractHistory> chLCopy = chL;
						for (ContractHistory wa_ch1 : chLCopy) {
							if (wa_ch1.getOper_on().equals(80L)) {
								promochL.add(wa_ch1);
							}
						}

						getConServ().updateContractPromos(p_contract,
								new_cd.getBkpf(), wa_promoList,
								userData.getUserid(), this.transaction_code,
								p_promoName, promochL);
						System.out
								.println("Promo-campaigns Successfully updated!");
						update80 = false;
					}
					break;
				}
				case 30: {
					if (update30) {
						System.out.println("Tovar is being changed.");
						getConServ().changeContractTovar(p_contract,
								p_oldcontract, new_cd.getBkpf(),
								userData.getUserid(), this.transaction_code);
						System.out.println("Tovar Successfully updated!");
						update30 = false;
					}
					break;
				}
				case 40: {
					if (update40) {
						System.out.println("Dealer is being updated.");

						// String wcl = "contract_number = "
						// + p_contract.getContract_number()
						// + " and blart = 'GC' and storno = 0";
						// BkpfDao bkpfDao = (BkpfDao) appContext.getContext()
						// .getBean("bkpfDao");
						//
						// ContractTypeDao ctDao = (ContractTypeDao) appContext
						// .getContext().getBean("contractTypeDao");
						// ContractType p_contrType =
						// ctDao.find(p_contract.getContract_type_id());
						//
						// Bkpf p_bkpf = bkpfDao.dynamicFindSingleBkpf(wcl);
						//
						// p_currency = p_bkpf.getWaers();
						// p_currate = p_bkpf.getKursf();
						//
						// BranchDao branchDao = (BranchDao)
						// appContext.getContext()
						// .getBean("branchDao");
						// Branch p_branch =
						// branchDao.find(p_contract.getBranch_id());
						//
						// double wrbtr = p_contract.getPrice();
						// double dmbtr = wrbtr / p_currate;

						// getConServ().updateContractPrice(p_contract, p_bkpf,
						// p_branch, userData.getUserid(),
						// this.transaction_code, p_currency, p_currate,
						// dmbtr, wrbtr, p_contrType.getMatnr(), ps,
						// chL.get(0));

						getConServ().changeDealer(p_contract,
								wa_ch.getOld_id(), wa_ch.getNew_id(),
								userData.getUserid(), transaction_code);

						// public void changeDealer(Contract a_con,Long
						// a_old_dealer_staff_id,Long a_new_dealer_staff_id,Long
						// a_userID)throws DAOException;

						System.out.println("Dealer Successfully changed!");
						update40 = false;
					}
					break;
				}

				case 90: {
					if (update90) {
						System.out.println("Recommender is being updated.");

						getConServ().AddRemoveRecommender(p_contract,
								p_oldcontract, refRate, refWaers,
								refDiscountWaers, new_cd.getBkpf(),
								userData.getUserid(), this.transaction_code);
						System.out.println("Recommender Successfully updated!");
						update90 = false;
					}
					break;
				}
				default: {
					if (updateDefault) {
						System.out
								.println("Default operation like contact data, etc.");
						getConServ().updateContract(p_contract, ps,
								userData, userData.getU_language());
						updateDefault = false;
					}
					break;
				}
				}
			}

			if (chL.size() > 0) {
				getConServ().SaveContractHistory(p_contract.getContract_id(),
						chL, userData.getUserid());
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
			GeneralUtil.addMessage("Success", "Contract updated");

			toDmsc03(p_contract.getContract_number());
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
		}
	}

	public void toDmsc03(Long a_value) {
		GeneralUtil.doRedirect("/dms/contract/dmsc03.xhtml?contract_number="
				+ a_value);
	}

	public void otherContactDetChange(int chType) {
		switch (chType) {
		case 6: {
			ContractHistory wa_ch = new ContractHistory(
					p_contract.getContract_id());
			wa_ch.setContract_id(p_contract.getContract_id());
			wa_ch.setOper_on(new Long(206));
			wa_ch.setOper_type(3L);
			String wa_info = "Old info: "
					+ p_oldcontract.getInfo();
			wa_info += " (User: " + userData.getUsername() + ")";
			wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
			wa_ch.setUser_id(userData.getUserid());
			wa_ch.setInfo(wa_info);
			wa_ch.setProcessed(0);
			chL.add(wa_ch);
			break;
		}

		case 17: { 
			ContractHistory wa_ch = new ContractHistory(
					p_contract.getContract_id());
			wa_ch.setContract_id(p_contract.getContract_id());
			wa_ch.setOper_on(new Long(17));
			wa_ch.setOper_type(3L);
			
			SubCompanyDao scDao = (SubCompanyDao) appContext.getContext().getBean("subCompanyDao");
			
			String wa_info = ""; 
			if (!Validation.isEmptyLong(p_oldcontract.getLegal_entity_id())) {
				SubCompany scOld = scDao.find(p_oldcontract.getLegal_entity_id());
				wa_ch.setOld_text(scOld.getName_ru());
				wa_info += "Old: " + scOld.getName_ru();				
			}
			System.out.println("LE New: " + p_contract.getLegal_entity_id());
			SubCompany scNew = scDao.find(p_contract.getLegal_entity_id());
			wa_ch.setNew_text(scNew.getName_ru());
			wa_info	+= " New: " + scNew.getName_ru();
			wa_info += " (User: " + userData.getStaffDisplayName() + ")";
			wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
			wa_ch.setUser_id(userData.getUserid());
			wa_ch.setInfo(wa_info);
			wa_ch.setProcessed(0);
			chL.add(wa_ch);
			break;
		}
			
		default:
			break;
		} 
	
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:contractHistoryTable");
	}
	
	// *********************************************************************
	// *************************************************************************
	public Address newAddr = new Address();
	public List<Address> addrList = new ArrayList<Address>();
	public Address selectedAddr = new Address();
	public Address addrHome = new Address();
	public Address addrWork = new Address();
	public Address addrService = new Address();

	public Address addrHome2 = new Address();
	public Address addrWork2 = new Address();
	public Address addrService2 = new Address();

	private int currentAddr;

	public int getCurrentAddr() {
		return currentAddr;
	}

	public void setCurrentAddr(int currentAddr) {
		this.currentAddr = currentAddr;
	}

	public Address getAddrHome() {
		return addrHome;
	}

	public void setAddrHome(Address addrHome) {
		this.addrHome = addrHome;
	}

	public Address getAddrWork() {
		return addrWork;
	}

	public void setAddrWork(Address addrWork) {
		this.addrWork = addrWork;
	}

	public Address getAddrService() {
		return addrService;
	}

	public void setAddrService(Address addrService) {
		this.addrService = addrService;
	}

	public Address getSelectedAddr() {
		return selectedAddr;
	}

	public void setSelectedAddr(Address selectedAddr) {
		this.selectedAddr = selectedAddr;
	}

	public Address getNewAddr() {
		return newAddr;
	}

	public void setNewAddr(Address newAddr) {
		this.newAddr = newAddr;
	}

	public List<Address> getAddrList() {
		return addrList;
	}

	public void setAddrList(List<Address> addrList) {
		this.addrList = addrList;
	}

	public Country p_country;

	public Country getP_country() {
		return p_country;
	}

	public void setP_country(Country p_country) {
		this.p_country = p_country;
	}

	public void createNewAddress() {
		try {
			AddressService addrService = (AddressService) appContext
					.getContext().getBean("addressService");
			newAddr.setTelMob1(p_country.getPhonecode() + " "
					+ newAddr.getTelMob1());
			if (addrService.createAddress(newAddr, userData.getUserid(),
					this.transaction_code)) {
				GeneralUtil.addInfoMessage("New Address successfully saved!");
				GeneralUtil.hideDialog("NewAddressDlg");
				setCustomerAddressList();
			}
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void prepareNewAddress() {
		if (!Validation.isEmptyLong(p_contract.getCustomer_id())) {
			GeneralUtil.showDialog("NewAddressDlg");
			newAddr = new Address();
			newAddr.setCustomerId(p_contract.getCustomer_id());
			Branch p_branch = p_branchF4Bean.getL_branch_map().get(
					p_contract.getBranch_id());
			if (p_branch.getState_id() != null && p_branch.getState_id() > 0) {
				for (State wa_state : p_stateF4Bean.getState_list()) {
					if (wa_state.getIdstate().equals(p_branch.getState_id())) {
						newAddr.setCountryId(wa_state.getCountryid());
						// System.out.println("Country : " +
						// newAddr.getCountryId());
						break;
					}
				}
				loadStates();
				newAddr.setStateId(p_branch.getState_id());
				loadCities();
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("NewAddressForm");
		} else {
			GeneralUtil.addInfoMessage("Please select Customer first!");
		}
	}

	public void loadCustomerAddrList() {
		addrList = new ArrayList<Address>();
		AddressDao addrDao = (AddressDao) appContext.getContext().getBean(
				"addressDao");
		addrList = addrDao.findAllByCustomerId(p_contract.getCustomer_id());
	}

	public void setCustomerAddressList() {
		loadCustomerAddrList();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("AddressListForm");
		GeneralUtil.showDialog("AddressListDlg");
	}

	public void cancelNewAddress() {
		newAddr = new Address();
	}

	public void assignSelectedAddr() {
		try {
			if (selectedAddr != null
					&& !Validation.isEmptyLong(selectedAddr.getAddr_id())) {
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				if (currentAddr == 1) {
					System.out.println("Assigning Home Address!");
					p_contract.setAddrHomeId(selectedAddr.getAddr_id());
					addrHome = (Address) selectedAddr.clone();
					System.out
							.println("Home Address: " + addrHome.getAddress());
					reqCtx.update("form:addrHome");
				} else if (currentAddr == 2) {
					p_contract.setAddrWorkId(selectedAddr.getAddr_id());
					addrWork = (Address) selectedAddr.clone();
				} else if (currentAddr == 3) {
					p_contract.setAddrServiceId(selectedAddr.getAddr_id());
					addrService = (Address) selectedAddr.clone();
				}

				addressChange(currentAddr);

				reqCtx.update("form");
			}
		} catch (Exception ex) {
			GeneralUtil.addInfoMessage(ex.getMessage());
		}
	}

	public void clearAddressField(int i) {
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		switch (i) {
		case 1: {
			p_contract.setAddrHomeId(null);
			addrHome = new Address();
			reqCtx.update("form:addrHome");
			break;
		}
		case 2: {
			p_contract.setAddrWorkId(null);
			addrWork = new Address();
			break;
		}
		case 3: {
			p_contract.setAddrServiceId(null);
			addrService = new Address();
		}
		}
		addressChange(i);
		reqCtx.update("form");
	}

	public void loadCustomerAddress() {
		loadCustomerAddrList();

		loadCustomerAddrList();
		for (Address wa_addr : addrList) {
			System.out.println("AddrId: " + wa_addr.getAddr_id());
			if (wa_addr.getAddr_id().equals(p_contract.getAddrHomeId())) {
				addrHome = wa_addr;
				// System.out.println("HomeAddress: " + addrHome.getAddress());
			}
			if (wa_addr.getAddr_id().equals(p_contract.getAddrServiceId())) {
				addrService = wa_addr;
				// System.out.println("ServiceAddress: " +
				// addrService.getAddress());
			}
			if (wa_addr.getAddr_id().equals(p_contract.getAddrWorkId())) {
				addrWork = wa_addr;
				// System.out.println("WorkAddress: " + addrWork.getAddress());
			}
		}

		try {
			addrHome2 = addrHome.clone();
			addrWork2 = addrWork.clone();
			addrService2 = addrService.clone();
		} catch (Exception ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}

	}

	// *********************************************************************
	public void to_search() {
		try {
			new_cd = new ContractDetails();
			chL.clear();

			p_contract = new Contract();
			selectedStaff = new Staff();
			selectedCustomer = new Customer();
			p_fioColl = "";
			p_fioDealer = "";
			p_fio = "";
			p_fioDemoSec = "";
			p_fioFitter = "";
			p_fioRef = "";
			default_update = false;

			statusChangeRole = true;
			initial_contract_status_id = (long) 0;
			new_adding_priceCheck = true;
			updateButton = true;

			ContractService contractService = (ContractService) appContext
					.getContext().getBean("contractService");
			p_contract = contractService
					.searchByContractNumber(contract_number_search);

			// **********************************************************************************************************
			if (p_contract != null) {
				loadLEList();				
				initial_contract_status_id = p_contract.getContract_status_id();
				if (initial_contract_status_id != ContractStatus.STATUS_CANCELLED
						//&& initial_contract_status_id != ContractStatus.STATUS_CLOSED
						&& initial_contract_status_id != ContractStatus.STATUS_FORSERVICE
						)
					statusChangeRole = false;

				new_cd.setContract(p_contract);
				p_oldcontract = new Contract();
				BeanUtils.copyProperties(p_contract, p_oldcontract);

				new_cd.setBranch(p_branchF4Bean.getL_branch_map().get(
						p_contract.getBranch_id()));

				String wcl = "contract_number = "
						+ p_contract.getContract_number()
						+ " and blart = 'GC' and storno = 0";
				BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean(
						"bkpfDao");
				new_cd.setBkpf(bkpfDao.dynamicFindSingleBkpf(wcl,p_contract.getBukrs()));

				p_currency = p_contract.getWaers();
				p_currate = p_contract.getRate();

				ContractTypeDao ctDao = (ContractTypeDao) appContext
						.getContext().getBean("contractTypeDao");
				new_cd.setContrType(ctDao.find(p_contract.getContract_type_id()));

				CustomerDao cusDao = (CustomerDao) appContext.getContext()
						.getBean("customerDao");
				new_cd.setCustomer(cusDao.find(p_contract.getCustomer_id()));
				p_fio = new_cd.getCustomer().getFullFIO();
				System.out.println("Customer: " + p_fio);

				StaffDao stfDao = (StaffDao) appContext.getContext().getBean(
						"staffDao");

				Staff staff = new Staff();
				if (p_contract.getDealer() != null
						&& p_contract.getDealer() > 0) {
					staff = stfDao.find(p_contract.getDealer());
					if (staff != null && staff.getStaff_id() > 0) {
						new_cd.setDealer(staff);
						p_fioDealer = new_cd.getDealer().getLF();
						System.out.println("Dealer: " + p_fioDealer);
					}
				}

				if (p_contract.getDemo_sc() != null
						&& p_contract.getDemo_sc() > 0) {
					staff = stfDao.find(p_contract.getDemo_sc());
					if (staff != null && staff.getStaff_id() > 0) {
						new_cd.setDemosec(staff);
						p_fioDemoSec = new_cd.getDemosec().getLF();
						System.out.println("DemoSec: " + p_fioDemoSec);
					}
				}

				if (p_contract.getFitter() != null
						&& p_contract.getFitter() > 0) {
					staff = stfDao.find(p_contract.getFitter());
					if (staff != null && staff.getStaff_id() > 0) {
						new_cd.setFitter(staff);
						p_fioFitter = new_cd.getFitter().getLF();
						System.out.println("Fitter: " + p_fioFitter);
					}
				}

				if (p_contract.getCollector() != null
						&& p_contract.getCollector() > 0) {
					staff = stfDao.find(p_contract.getCollector());
					if (staff != null && staff.getStaff_id() > 0) {
						new_cd.setCollector(staff);
						p_fioColl = new_cd.getCollector().getLF();
						System.out.println("Collector: " + p_fioColl);
					}
				}

				if (new_cd.getContract().getContract_status_id() == 5) {
					new_cd.setPrice(new_cd.getContract().getPrice());
					new_cd.firstPayment = new_cd.getContract()
							.getFirst_payment();
				} else {
					
					PriceListDao plDao = (PriceListDao) appContext.getContext()
							.getBean("priceListDao");
					if (!Validation.isEmptyLong(p_contract.getPrice_list_id())) {
						new_cd.setPriceList(plDao.find(p_contract.getPrice_list_id()));
					
						new_cd.setPrice(new_cd.getPriceList().getPrice()
								* p_currate);
						new_cd.paymentTempList = p_paymentTemplateF4Bean
								.getPaymentTemplateOf(new_cd.getPriceList()
										.getPrice_list_id());
						new_cd.firstPayment = new_cd.getPaymentTempList().get(0)
								.getMonthly_payment_sum()
								* new_cd.getBkpf().getKursf();
					}	

					loadPriceList();

					selectedPrice = new PriceTableClass();
					if (p_contract != null
							&& p_contract.getContract_id() != null) {
						for (PriceTableClass ptc : priceTable) {
							if (ptc.getpListId().equals(
									p_contract.getPrice_list_id())) {
								// BeanUtils.copyProperties(ptc, selectedPrice);
								selectedPrice = ptc;
								System.out
										.println("Default Price is selected!");
								break;
							}
						}
					}

					System.out
							.println("Default Price is "
									+ selectedPrice.getPriceNative() + " "
									+ p_currency);
					System.out.println("Default Price ID: "
							+ selectedPrice.getpListId());

				}

				wcl = "awkey = " + p_contract.getAwkey();
				PaymentScheduleDao psDao = (PaymentScheduleDao) appContext
						.getContext().getBean("paymentScheduleDao");
				new_cd.setPsList(psDao.findAll(wcl,p_contract.getBukrs()));

				new_cd.paid = p_contract.getPaid();
				new_cd.remain = new_cd.price - new_cd.paid;
				new_cd.repayment = 0;

				new_cd.setStatusName(p_csF4Bean.getCsMap()
						.get(p_contract.getContract_status_id()).getName());

				new_cd.icon = "ui-icon-close";

				selectedCustomer = contractService.getCustomerById(p_contract
						.getCustomer_id());

				if (p_contract.getRef_contract_id() != null
						&& p_contract.getRef_contract_id() > 0) {
					Contract refcontr = new Contract();
					ContractDao contractDao = (ContractDao) appContext
							.getContext().getBean("contractDao");
					refcontr = contractDao.getByContractId(p_contract
							.getRef_contract_id());

					Customer wa_customer = new Customer();
					CustomerDao customerDao = (CustomerDao) appContext
							.getContext().getBean("customerDao");
					wa_customer = customerDao.find(refcontr.getCustomer_id());
					if (wa_customer != null && wa_customer.getId() != null) {
						p_fioRef = "SN:" + refcontr.getContract_number() + " "
								+ wa_customer.getFIO();
					} else {
						p_fioRef = "No customer found";
					}
				}

				// ***************************_Load_payment_schedule_*********************

				List<PaymentSchedule> ps_list = new ArrayList<PaymentSchedule>();
				String whereClauseForPS = "awkey = " + p_contract.getAwkey();
				ps_list = psDao.findAll(whereClauseForPS,p_contract.getBukrs());

				psDetFirstPayment.setIndex(0);
				if (ps_list.size() > 0) {
					psDetFirstPayment.setPs(ps_list.get(0));
				}

				psDetL = new ArrayList<PaymentScheduleDetails>();
				for (int i = 1; i < ps_list.size(); i++) {
					PaymentScheduleDetails psd = new PaymentScheduleDetails(i);
					psd.setPs(ps_list.get(i));
					psd.setMon_dis(true);

					psDetL.add(psd);
				}

				// ********************************************************************

				Calendar cal = Calendar.getInstance();
				// p_contract.setNew_contract_date(cal.getTime());
				p_contract.setUpdated_date(cal.getTime());

				// loadPromoMatnrListByBukrs(p_contract.getBukrs());
				loadPromoTableByBranch(p_contract.getBranch_id());
				loadPromoTable();

				// ********************************************************************
				initContractModel();
				outputTable.searchModel.setBukrs(p_contract.getBukrs());
				outputTable.searchModel.setBranch_id(p_contract.getBranch_id());
				checkConLastState();
				// disSelMatnr = false;

				if (checkStaffPayrol())
					disableStaffChangeBtn();
				else
					enableStaffChangeBtn();

				loadCustomerAddress();
				disRef = true;

				// *********************************************

				p_bukrs = p_contract.getBukrs();
				branch_list = loadSalesBranchList(p_bukrs);
				staffBranch_list = loadBranchList(p_bukrs, -1);
				serv_branch_list = loadServBranchList(p_bukrs);
				p_branch_id = p_contract.getBranch_id();

				p_country = p_countryF4Bean.getL_country_map().get(
						newAddr.getCountryId());

				loadDealers();
				loadWerksMatnrList();
				setDisRefField();
				
				
				//*****************************************

				
				disReferenderBtn = false;			
				
				if (p_contract.getContract_status_id().equals(2L) ||p_contract.getContract_status_id().equals(3L)||p_contract.getContract_status_id().equals(5L)){
					disReferenderBtn = true;
				}
				
				if (userData.isSys_admin()){
					disReferenderBtn = false;
				}
				else if (userData.getL_ra_map()!=null && userData.getL_ra_map().get(115L)!=null && userData.getL_ra_map().get(115L).size()>0)
				{
					disReferenderBtn = false;
				}

				//**************************************************************************************************
				

				disCollectorBtn = false;
				Calendar curDate = Calendar.getInstance();
//				Calendar datey2020m03d20 = Calendar.getInstance();
//				datey2020m03d20.set(2020, 02, 20);				
//				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
				
//				if (
//						fmt.format(curDate.getTime()).equals(fmt.format(datey2020m03d20.getTime())) ||
//						curDate.getTime().after((datey2020m03d20.getTime()))
//						
//						){
//					int day = curDate.get(Calendar.DAY_OF_MONTH);
//					if (day>5){
//						disCollectorBtn = true;
//					}
//					
//				}
				
				int day = curDate.get(Calendar.DAY_OF_MONTH);
				if (day>5){
					disCollectorBtn = true;
				}
				

				if (userData.isSys_admin())
				{
					disCollectorBtn = false;
				}			
				else if (userData.getL_ra_map()!=null && userData.getL_ra_map().get(115L)!=null && userData.getL_ra_map().get(115L).size()>0)
				{
					disCollectorBtn = false;
				}
				
				

				//*****************************************

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
			}

		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
		}
	}

	public void disableStaffChangeBtn() {
		disStaffBtn = true;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:dealerBtn");
		reqCtx.update("form:demosecBtn");
		reqCtx.update("form:collectorBtn");
		reqCtx.update("form:dealerDelBtn");
		reqCtx.update("form:demoseDelcBtn");
		reqCtx.update("form:collectorDelBtn");
	}
	
	private void setDisRefField() {
		disRefField = true;
		
		if (p_contract != null
				&& p_contract.getContract_date() != null) {
			if (isCoordinator()) { 
				disRefField = false;
			} else {
				Date today = new Date();
				int ma = GeneralUtil.calcAgeinMonths(today, p_contract.getContract_date());
				if (ma > 0) disRefField = true;
				else if (ma == 0) disRefField = false;
			}
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:refField");
		reqCtx.update("form:refField");
	}

//	boolean isNotCoordinator = true;
	
	private boolean isCoordinator() {
		boolean isCoord = false;
		
		List<UserRole> urL = new ArrayList<UserRole>();
		UserRoleDao urlDao = (UserRoleDao) appContext.getContext()
				.getBean("userRoleDao");
		urL = urlDao.findUserRoles(userData.getUserid());
		
		// System.out.println("Role list: " + urL.size());
		for (UserRole wa_ur : urL) {
			// System.out.println(wa_ur.getRoleId());
			if (wa_ur.getRoleId() == 19
					|| wa_ur.getRoleId() == 115
					|| wa_ur.getRoleId() == 25) {
				isCoord = true;
				// System.out.println("IS COORDINATOR!");
			}
		}
		System.out.println("IS COORDINATOR: " + isCoord);
		return isCoord;
	}
	
	public void enableStaffChangeBtn() {
		disStaffBtn = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:dealerBtn");
		reqCtx.update("form:demosecBtn");
		reqCtx.update("form:collectorBtn");
		reqCtx.update("form:dealerDelBtn");
		reqCtx.update("form:demoseDelcBtn");
		reqCtx.update("form:collectorDelBtn");
	}

	public boolean disStaffBtn = false;

	public boolean isDisStaffBtn() {
		return disStaffBtn;
	}

	public void setDisStaffBtn(boolean disStaffBtn) {
		this.disStaffBtn = disStaffBtn;
	}
	
	public boolean disCollectorBtn = false;
	public boolean disReferenderBtn = false;

	public boolean isDisCollectorBtn() {
		return disCollectorBtn;
	}

	public void setDisCollectorBtn(boolean disCollectorBtn) {
		this.disCollectorBtn = disCollectorBtn;
	}

	public boolean isDisReferenderBtn() {
		return disReferenderBtn;
	}

	public void setDisReferenderBtn(boolean disReferenderBtn) {
		this.disReferenderBtn = disReferenderBtn;
	}

	
	
	
	

	// *********************** PROMO-CAMPAIGN ******************************
	// *********************************************************************

	
	public void loadPromoTable() {
		selectedPTS = new ArrayList<PromoTable>();
		initialPTS = new ArrayList<PromoTable>();

		ContractPromosDao cpDao = (ContractPromosDao) appContext.getContext()
				.getBean("cpDao");
		List<ContractPromos> cps = cpDao.findAllByContrID(p_contract
				.getContract_id());
		System.out.println("Total Promos on this Contract: " + cps.size());

		int index = 0;
		p_promoName = "";

		for (ContractPromos cp : cps) {
			for (PromoTable wa_pt : promoTable) {
				if (cp.getPromo_id().equals(wa_pt.getPromo().getId())) {
					selectedPTS.add(wa_pt);
					initialPTS.add(wa_pt);
					if (index > 0) {
						p_promoName += ", ";
					}
					p_promoName += wa_pt.getPromo().getName();
					index++;
					System.out.println(index + ". Promo: "
							+ wa_pt.getPromo().getName());
				}
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("PromotionListForm");
		reqCtx.update("form:promoName");
	}

	// ******************************************************************************************************

	public void loadPromoTableByBranch(Long a_brId) {

		// selectedPTS = new ArrayList<PromoTable>();
		List<Promotion> pm_list = new ArrayList<Promotion>();
		promoTable = new ArrayList<PromoTable>();

		PromotionService promoService = (PromotionService) appContext
				.getContext().getBean("promotionService");

		pm_list = promoService.findAllByBranch(a_brId);

		int index = 0;
		for (Promotion p : pm_list) {
			index++;
			PromoTable pt = new PromoTable(index);
			pt.setPromo(p);
			pt.setBranch(p_branchF4Bean.getL_branch_map().get(p.getBranch_id()));

			for (Bukrs b : p_bukrsF4Bean.getBukrs_list()) {
				if (b.getBukrs().equals(p.getBukrs())) {
					pt.setBukr(b);
				}
			}

			for (Country c : p_countryF4Bean.getCountry_list()) {
				if (c.getCountry_id() == p.getCountry_id()) {
					pt.setCountry(c);
				}
			}

			pt.setMatnr(p_matnrF4Bean.getL_matnr_map().get(p.getMatnr()));
			promoTable.add(pt);
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("PromotionListForm");
	}

	// *****************************************************************************************************

	public void loadPromoTable(Long a_brId) {
		// selectedPTS = new ArrayList<PromoTable>();
		List<Promotion> pm_list = new ArrayList<Promotion>();
		promoTable = new ArrayList<PromoTable>();

		PromotionService promoService = (PromotionService) appContext
				.getContext().getBean("promotionService");

		pm_list = promoService.findAllByBranch(a_brId);

		int index = 0;
		for (Promotion p : pm_list) {
			index++;
			PromoTable pt = new PromoTable(index);
			pt.setPromo(p);
			pt.setBranch(p_branchF4Bean.getL_branch_map().get(p.getBranch_id()));

			for (Bukrs b : p_bukrsF4Bean.getBukrs_list()) {
				if (b.getBukrs().equals(p.getBukrs())) {
					pt.setBukr(b);
				}
			}
			for (Country c : p_countryF4Bean.getCountry_list()) {
				if (c.getCountry_id() == p.getCountry_id()) {
					pt.setCountry(c);
				}
			}

			pt.setMatnr(p_matnrF4Bean.getL_matnr_map().get(p.getMatnr()));
			promoTable.add(pt);
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("PromotionListForm");

	}

	public void clearPromoField() {
		p_promoName = "";
		promos = new Long[0];
		selectedPTS = new ArrayList<PromoTable>();

		removeCHLif(80L);
		lookForRemovedPromos();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:promoName");
		reqCtx.update("form:contractHistoryTable");
	}

	public void lookForRemovedPromos() {
		for (PromoTable wa_ipt : initialPTS) {
			if (!selectedPTS.contains(wa_ipt)) {
				ContractHistory wa_ch = new ContractHistory(
						p_contract.getContract_id());
				wa_ch.setContract_id(p_contract.getContract_id());
				wa_ch.setOld_id(wa_ipt.getPromo().getId());
				wa_ch.setOper_on(80L);
				wa_ch.setOper_type(2L);
				wa_ch.setOld_text(wa_ipt.getPromo().getName());
				String wa_info = "Promo-Campaign : "
						+ wa_ipt.getPromo().getName() + " is removed.";
				wa_info += " (User: " + userData.getUsername() + ")";
				wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
				wa_ch.setUser_id(userData.getUserid());
				wa_ch.setInfo(wa_info);
				wa_ch.setProcessed(0);
				chL.add(wa_ch);
			}
		}
	}

	public void removeCHLif(Long a_oper_on) {
		int j = chL.size();
		while (j > 0) {
			j--;
			ContractHistory wa_ch = chL.get(j);
			if (wa_ch.getOper_on().equals(a_oper_on)) {
				chL.remove(wa_ch);
			}
		}
	}

	public void assignSelectedPromos() {
		p_promoName = "";
		Long country_id = p_branchF4Bean.getL_branch_map().get(p_contract.getBranch_id()).getCountry_id();
		if ((country_id.equals(1L)||country_id.equals(5L)) && (selectedPTS.size()>1) && (p_contract.getBukrs().equals("1000"))){
			selectedPTS = new ArrayList<PromoTable>();

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:promoName");
			reqCtx.execute("PF('PromotionDlg').show();");
		}
		else
		{
			
			// *****************************CONTRACT_HISTORY***********************************
			disable_save_button();
			Calendar cal = Calendar.getInstance();
			// chdL.removeIf(n -> n.getCh().getOper_on().equals(80L));
			removeCHLif(80L);
			lookForRemovedPromos();

			// Look for new applied promos
			for (PromoTable wa_spt : selectedPTS) {
				if (!initialPTS.contains(wa_spt)) {
					ContractHistory wa_ch = new ContractHistory();
					wa_ch.setContract_id(p_contract.getContract_id());
					wa_ch.setRec_date(cal.getTime());
					wa_ch.setNew_id(wa_spt.getPromo().getId());
					wa_ch.setOper_on(80L);
					wa_ch.setOper_type(1L);
					String new_text = wa_spt.getPromo().getName();
					wa_ch.setNew_text(new_text);
					String wa_info = "Promo-Campaign : " + new_text
							+ " is applied.";
					wa_info += " (User: " + userData.getUsername() + ")";
					wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
					wa_ch.setUser_id(userData.getUserid());
					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(0);
					chL.add(wa_ch);
				}
			}

			// *****************************************************************************

			System.out.println(selectedPTS.size());
			if (selectedPTS.size() > 0) {
				promos = new Long[selectedPTS.size()];

				if (selectedPTS != null) {
					for (int i = 0; i < selectedPTS.size(); i++) {
						promos[i] = selectedPTS.get(i).getPromo().getId();
						if (i > 0)
							p_promoName += ", ";
						p_promoName += selectedPTS.get(i).getPromo().getName();
						// System.out.println("Selected Promos: " + p_promoName);
					}
				}
			} else {
				promos = new Long[0];
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:promoName");
			reqCtx.update("form:contractHistoryTable");
			reqCtx.execute("PF('PromotionDlg').hide();");
		}
		
	}

	public void clearPaymentElements() {
		p_contract.setPayment_schedule(0);
		p_contract.setFirst_payment(0);
		p_contract.setPrice(0);
		p_contract.setPrice_list_id(new Long(0));

		p_contract.setDealer(null);
		p_contract.setDemo_sc(null);
		p_contract.setCollector(null);
		p_contract.setFitter(null);

		p_fio = "";
		p_fioColl = "";
		p_fioDemoSec = "";
		p_fioDealer = "";
		p_fioFitter = "";

		selectedPrice = new PriceTableClass();
		psDetL.clear();

		disable_save_button();

		ref_textClass = "noteRegular";
		refDiscStatus = "";
		p_contract.setDiscount_from_ref((byte) 0);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:price_list_id");
		reqCtx.update("form:priceTable");
		reqCtx.update("form:payment_schedule");
		reqCtx.update("form:first_payment");
		reqCtx.update("form:PaymentTable");
		reqCtx.update("form:demo_sc");
		reqCtx.update("form:dealer");
		reqCtx.update("form:collector");
		reqCtx.update("form:fitter");
		reqCtx.update("form:dealer_subtract");
		reqCtx.update("form:skidkaRef");
		reqCtx.update("form:refDiscText");
		reqCtx.update("form:check_text");
		reqCtx.update("form:check_button");
		reqCtx.update("form:save_button");

	}

	public void loadStates() {
		state_list = new ArrayList<State>();
		city_list = new ArrayList<City>();
		cityreg_list = new ArrayList<Cityreg>();
		newAddr.setStateId(null);
		newAddr.setCityId(null);
		newAddr.setRegId(null);

		p_stateF4Bean.init();
		for (State wa_state : p_stateF4Bean.getState_list()) {
			if (wa_state.getCountryid().equals(newAddr.getCountryId())) {
				state_list.add(wa_state);
			}
		}

		p_country = p_countryF4Bean.getL_country_map().get(
				newAddr.getCountryId());

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewAddressForm:state_id");
		reqCtx.update("NewAddressForm:city_id");
		reqCtx.update("NewAddressForm:cityreg_id");
		reqCtx.update("NewAddressForm:tel_mob1");
		reqCtx.update("NewAddressForm:tel_mob1_phonecode");
	}

	public void loadCities() {
		city_list = new ArrayList<City>();
		cityreg_list = new ArrayList<Cityreg>();
		newAddr.setCityId(null);
		newAddr.setRegId(null);

		p_cityF4Bean.init();
		for (City wa_city : p_cityF4Bean.getCity_list()) {
			if (wa_city.getStateid().equals(newAddr.getStateId())) {
				city_list.add(wa_city);
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewAddressForm:city_id");
		reqCtx.update("NewAddressForm:cityreg_id");
	}

	public void loadCityregs() {
		cityreg_list = new ArrayList<Cityreg>();
		newAddr.setRegId(null);

		p_cityregF4Bean.init();
		for (Cityreg wa_cityreg : p_cityregF4Bean.getCityreg_list()) {
			if (wa_cityreg.getCity_id().equals(newAddr.getCityId())) {
				cityreg_list.add(wa_cityreg);
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("NewAddressForm:cityreg_id");
	}

	// *********************************************************************
	// ******************** Check Payment Table ****************************
	// *********************************************************************

	public void to_spread_payment_table() {
		disable_save_button();

		if (selectedPrice != null) {

			if (selectedPrice.getpListId() != null
					&& selectedPrice.getpListId() > 0) {

				double fp = selectedPrice.getFirstPayment();

				if (fp <= p_contract.getFirst_payment()) {
					loadMonthlyPayments(p_contract.getPrice(),
							p_contract.getPayment_schedule(),
							p_contract.getFirst_payment(),
							p_contract.getDealer_subtract(),
							p_contract.getDiscount_from_ref());
					p_check_text = msgProvider
							.getValue("dmsc.hint_distributed")
							+ " "
							+ msgProvider.getValue("dmsc.hint_check");
					// p_check_text =
					// "График оплаты распределена. Пожалуйста не забудьте проверить суммы оплат перед сохранением!";
					p_check_text_color = "noteRegular";

				} else {
					p_check_text = msgProvider.getValue("dmsc.hint_min_fp")
					// "Сумма первоначальной оплаты должна составлять не меньее "
							+ fp + " " + p_currency;
					p_check_text_color = "noteWarn";
					// throw new
					// DAOException("First payment cannot be less than 30% amount ("
					// + fp+ ") of the price.");
				}

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:check_text");
			} else {
				throw new DAOException("No price selected!");
			}
		} else {
			GeneralUtil.addMessage("Price is empty!", "Price is not selected!");
		}
	}

	public void assign_firstPayment() {
		disable_save_button();
		p_contract.setFirst_payment(psDetFirstPayment.getPs().getSum());
	}

	public void monthController() {
		if (p_contract.getPayment_schedule() > selectedPrice.month) {
			p_contract.setPayment_schedule(selectedPrice.month.intValue());
			MessageController.getInstance().addError(
					"Number of months cannot be greater than "
							+ selectedPrice.month + ".");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:payment_schedule");
		}
	}

	public void checkAll() {
		ps[0] = psDetFirstPayment.getPs();
		System.out.println("First Payment: Date:" + ps[0].getPayment_date()
				+ "  Sum: " + ps[0].getSum() + "  Paid: " + ps[0].getPaid()
				+ "  Bukrs" + ps[0].getBukrs());

		for (int i = 1; i <= psDetL.size(); i++) {
			// PaymentSchedule nw = new PaymentSchedule();
			ps[i] = psDetL.get(i - 1).getPs();
		}
		try {
			if (getConVal().validateReferencer(p_contract, refRate, refWaers,
					refDiscountWaers, userData.getU_language()))
				enable_save_button();
		} catch (Exception ex) {
			disable_save_button();
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void to_check_payment_table() {
		enable_save_button();
		System.out.println("CHECK PAYMENT TABLE!");
		System.out.println("Selected Price: " + selectedPrice);

		if (selectedPrice != null) {
			if (selectedPrice.getpListId() != null
					&& selectedPrice.getpListId() > 0) {
				disable_save_button();

				double fp = selectedPrice.getFirstPayment();

				if (fp <= p_contract.getFirst_payment()) {
					if (checkPaymentScheduleAndPrice(p_contract)) {
						p_check_text = p_check_text = msgProvider
								.getValue("dmsc.hint_correct_distribute")
								+ " "
								+ msgProvider.getValue("dmsc.hint_check");
						// "Суммы оплат распределены правильно! Пожалуйста проверьте остальные данные перед сохранением!";p_check_text_color
						// = "noteOk";
						p_check_icon = "ui-icon ui-icon-check";
						enable_save_button();
						System.out.println("CORRECT!");
					} else {
						p_check_text = p_check_text = msgProvider
								.getValue("dmsc.hint_wrong_distribute");
						// "Суммы оплат распределены не правильно! Пожалуйста проверьте еще раз суммы!";
						p_check_text_color = "noteWarn";
					}

				} else {
					p_check_text = p_check_text = msgProvider
							.getValue("dmsc.hint_min_fp")
					// "Сумма первоначальной оплаты должна составлять не меньее "
							+ fp + " " + p_currency;
					p_check_text_color = "noteWarn";
					// throw new
					// DAOException("First payment cannot be less than 30% amount ("
					// + fp+ ") of the price.");
				}

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:check_text");
				reqCtx.update("form:save_button");
				reqCtx.update("form:check_button");
			} else {
				throw new DAOException("No price selected!");
			}
		} else {
			GeneralUtil.addMessage("Price is empty!", "Price is not selected!");
		}
	}

	public void disable_save_button() {
		p_disabled_save_button = true;
		p_check_text = msgProvider.getValue("dmsc.hint_check");
		// "Пожалуйста проверьте данные перед сохранением!";
		p_check_text_color = "noteRegular";
		p_check_icon = "ui-icon ui-icon-refresh";
		p_check_payments = false;

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:check_text");
		reqCtx.update("form:save_button");
		reqCtx.update("form:check_button");
	}

	public void enable_save_button() {
		if (chL.size() > 0) {
			p_disabled_save_button = false;
		} else {
			p_disabled_save_button = true;
		}
		p_disabled_save_button = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:save_button");
	}

	// ***********************_CHECK_PAYMENT_SCHEDULE_&_PRICE_*********************

	public boolean checkPaymentScheduleAndPrice(Contract a_contract) {
		if (a_contract.getPrice() > 0) {
			int numPaySc = 0;
			double paymentDueSum = 0;

			System.out.println("Price: " + a_contract.getPrice());
			System.out.println("First payment: "
					+ a_contract.getFirst_payment());
			System.out.println("Months: " + a_contract.getPayment_schedule());

			for (PaymentScheduleDetails psd : psDetL) {
				if (psd.getPs().getSum() > 0) {
					numPaySc += 1;
				}
				paymentDueSum += psd.getPs().getSum();
			}

			if (numPaySc <= a_contract.getPayment_schedule()
					&& a_contract.getPrice() == a_contract.getFirst_payment()
							+ paymentDueSum) {
				return true;
			} else
				return false;

		} else if (a_contract.getPayment_schedule() == 0
				&& a_contract.getPrice() == 0) {
			double paymentDueSum = 0;
			for (PaymentScheduleDetails psd : psDetL) {
				paymentDueSum += psd.getPs().getSum();
			}

			if ((a_contract.getPrice() == a_contract.getFirst_payment()
					+ paymentDueSum)
					&& (a_contract.getContract_status_id() == 2)) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public void loadMonthlyPayments(double wa_price, int wa_mon,
			double wa_prepayment, double wa_dsubtract, byte wa_refdisc) {

		psDetL.clear();

		PaymentScheduleDetails psd;

		double remainder = (p_contract.getPrice() - wa_prepayment) % wa_mon;

		Calendar cal = Calendar.getInstance();
		// cal.setTime(p_contract.getContract_date());

		for (int i = 1; i <= wa_mon; i++) {
			psd = new PaymentScheduleDetails(i);
			psd.getPs()
					.setSum((double) ((long) ((p_contract.getPrice() - wa_prepayment) / wa_mon)));
			psd.setMon_dis(false);
			psd.getPs().setBukrs(p_contract.getBukrs());
			psd.getPs().setPaid(0);

			cal.add(Calendar.MONTH, 1);
			psd.getPs().setPayment_date(cal.getTime());

			if (i == wa_mon)
				psd.getPs()
						.setSum((double) ((long) ((p_contract.getPrice() - wa_prepayment) / wa_mon))
								+ remainder);
			psDetL.add(psd);
		}

		double wa_paid = p_contract.getPaid();
		wa_paid -= psDetFirstPayment.getPs().getSum();

		int j = psDetL.size() - 1;
		while (wa_paid > 0) {
			if (wa_paid < psDetL.get(j).getPs().getSum()) {
				psDetL.get(j).getPs().setPaid(wa_paid);
			} else {
				psDetL.get(j).getPs().setPaid(psDetL.get(j).getPs().getSum());
			}
			wa_paid -= psDetL.get(j).getPs().getSum();
			j--;
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:payment_schedule");
		reqCtx.update("form:first_payment");
		reqCtx.update("form:PaymentTable");
	}

	public void assignPriceListId() {
		if (selectedPrice != null && selectedPrice.getpListId() != null
				&& selectedPrice.getpListId() > 0) {
			removeCHLif(20L);
			ContractHistory wa_ch = new ContractHistory();

			new_adding_priceCheck = false;
			disable_save_button();
			min_first_payment = selectedPrice.getFirstPayment();
			new_cd.setPriceList(p_priceListF4Bean.getPl_map_l().get(
					selectedPrice.getpListId()));
			new_cd.setPrice(selectedPrice.getPriceNative());

			wa_ch.setContract_id(p_contract.getContract_id());
			wa_ch.setOld_id(p_contract.getPrice_list_id());
			wa_ch.setNew_id(selectedPrice.pListId);
			Calendar cal = Calendar.getInstance();
			wa_ch.setRec_date(cal.getTime());
			wa_ch.setOper_on(20L);
			wa_ch.setOper_type(3L);

			String new_text = new_cd.getPrice() + " " + p_currency + " for "
					+ new_cd.getPriceList().getMonth() + " month(s).";
			String old_text = p_contract.getPrice() + " " + p_currency
					+ " for " + p_contract.getPayment_schedule() + " month(s) ";
			String wa_info = "Price changed." + new_text;
			wa_info += " (User: " + userData.getUsername() + ")";
			wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
			wa_ch.setUser_id(userData.getUserid());
			wa_ch.setInfo(wa_info);
			wa_ch.setProcessed(0);
			wa_ch.setNew_text(new_text);
			wa_ch.setOld_text(old_text);
			chL.add(wa_ch);

			p_contract.setPrice_list_id(selectedPrice.getpListId());
			p_contract.setPrice(selectedPrice.getPriceNative());
			p_contract.setNew_contract_date(cal.getTime());

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:price_list_id");
			reqCtx.update("form:prepayment");
			reqCtx.update("form:payment_schedule");
			reqCtx.update("form:prepayment_currency");
			reqCtx.update("form:contractHistoryTable");
			reqCtx.update("form:bank_partner");
			loadMonthlyPaymentsAndPrice();

		} else {
			GeneralUtil.addMessage("Empty price!", "No price selected");
		}
	}

	public void resetRefDisc() {
		ref_textClass = "noteRegular";
		refDiscStatus = "";
		ref_discount = false;
		p_contract.setDiscount_from_ref((byte) 0);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:refDiscText");
		reqCtx.update("form:skidkaRef");
	}

	public void loadMonthlyPaymentsAndPrice() {
		wa_paymentTemplate = new PaymentTemplate();
		psDetFirstPayment = new PaymentScheduleDetails();
		psDetL.clear();

		PriceList wa_priceList = new PriceList();
		if (p_contract.getContract_status_id() != 1) {
			p_contract.setPrice_list_id((long) 0);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:price_list_id");
			GeneralUtil.addMessage("Info",
					"Prices not applicable for the chosen contract status");
			return;
		}

		p_contract.setPayment_schedule(0);
		p_contract.setFirst_payment(0);
		p_contract.setPrice(0);

		wa_priceList = p_priceListF4Bean.getPl_map_l().get(
				p_contract.getPrice_list_id());

		double wa_paid = p_contract.getPaid();
		double wa_remain = wa_priceList.getPrice() * p_currate
				- p_contract.getPaid();
		int mon = 0;

		List<PaymentTemplate> pt_l = p_paymentTemplateF4Bean
				.getPaymentTemplateOf(wa_priceList.getPrice_list_id());

		Calendar cal = Calendar.getInstance();
		// cal.setTime(p_contract.getContract_date());

		int j = 0;
		for (int i = 0; i < pt_l.size(); i++) {
			for (int k = 1; (k <= pt_l.get(i).getMonth_num()); k++) {
				PaymentScheduleDetails psd = new PaymentScheduleDetails(j);
				PaymentSchedule wa_ps = new PaymentSchedule();
				wa_ps.setPaid(0);
				wa_ps.setSum(pt_l.get(i).getMonthly_payment_sum() * p_currate);
				wa_ps.setBukrs(p_contract.getBukrs());

				if (j > 0)
					cal.add(Calendar.MONTH, 1);
				wa_ps.setPayment_date(cal.getTime());

				psd.setInfo(pt_l.get(i).getInfo());
				psd.setPs(wa_ps);
				psd.mon_dis = true;

				if (j == 0) {
					/*
					 * if (wa_paid > psd.getPs().getSum()) {
					 * psd.getPs().setSum(wa_paid); }
					 * psd.getPs().setPaid(wa_paid);
					 */
					psDetFirstPayment = psd;
				} else {
					psDetL.add(psd);
				}

				j++;
			}
		}

		if (wa_paid < psDetFirstPayment.getPs().getSum()) {
			psDetFirstPayment.getPs().setPaid(wa_paid);
		} else {
			psDetFirstPayment.getPs().setPaid(
					psDetFirstPayment.getPs().getSum());
		}
		wa_paid -= psDetFirstPayment.getPs().getSum();

		j = psDetL.size() - 1;
		while (wa_paid > 0) {
			if (wa_paid < psDetL.get(j).getPs().getSum()) {
				psDetL.get(j).getPs().setPaid(wa_paid);
			} else {
				psDetL.get(j).getPs().setPaid(psDetL.get(j).getPs().getSum());
			}
			wa_paid -= psDetL.get(j).getPs().getSum();
			j--;
		}

		p_contract.setPayment_schedule(wa_priceList.getMonth());
		p_contract.setPrice((double) wa_priceList.getPrice() * p_currate);
		p_contract.setFirst_payment((double) pt_l.get(0)
				.getMonthly_payment_sum() * p_currate);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:payment_schedule");
		reqCtx.update("form:first_payment");
		reqCtx.update("form:PaymentTable");

	}

	public void setRefDiscount() {
		if (ref_contr != null) {

			if (ref_discount) {
				if (ref_contr.remain > 0) {
					refDiscStatus = "Рекомендатель еще не погасил свой долг.";
					ref_textClass = "noteWarn";
					ref_discount = false;
				} else if (ref_contr.remain == 0) {
					refDiscStatus = "Скидка от рекомендателя присвоена.";
					ref_textClass = "noteOk";
					p_contract.setDiscount_from_ref((byte) 1);
				}

			} else {
				refDiscStatus = "Скидка не присвоена.";
				ref_textClass = "noteWarn";
				ref_discount = false;
				p_contract.setDiscount_from_ref((byte) 0);
			}
		} else {
			ref_discount = false;
			ref_textClass = "noteWarn";
			refDiscStatus = "Вы еще не выбрали рекомендателя.";
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:refDiscText");
		reqCtx.update("form:skidkaRef");
		System.out.println("Скидка от рекомендателя: "
				+ p_contract.getDiscount_from_ref());
	}

	public void loadPriceList() {
		priceList_list = new ArrayList<PriceList>();
		priceTable = new ArrayList<PriceTableClass>();
		selectedPrice = new PriceTableClass();

		ContractType wa_ct = p_contractTypeF4Bean.getCt_map().get(
				p_contract.getContract_type_id());
		Branch wa_branch = p_branchF4Bean.getL_branch_map().get(
				p_contract.getBranch_id());

		for (PriceList wa_priceList2 : p_priceListF4Bean
				.getPriceListByContractType(wa_ct, wa_branch.getCountry_id())) {

			if (new_cd.getContract().getPrice_list_id() != null 
					&& new_cd.getContract().getPrice_list_id() > 0) {
				if (wa_priceList2.getMatnr().equals(
						new_cd.getContrType().getMatnr())
						&& wa_priceList2.getCountry_id().equals(
								new_cd.getBranch().getCountry_id())
						&& wa_priceList2.getBukrs().equals(
								new_cd.getContrType().getBukrs())) {

					List<PaymentTemplate> wa_pt = p_paymentTemplateF4Bean
							.getPaymentTemplateOf(wa_priceList2
									.getPrice_list_id());
					priceList_list.add(wa_priceList2);

					System.out.println("Price List ID: "
							+ wa_priceList2.getPrice_list_id());

					PriceTableClass pt = new PriceTableClass();
					pt.setpListId(wa_priceList2.getPrice_list_id());
					pt.setPrice(wa_priceList2.getPrice());
					pt.setMonth(new Long(wa_priceList2.getMonth()));
					pt.setPriceNative((double) wa_priceList2.getPrice()
							* p_currate);
					pt.setMatnrId(wa_priceList2.getMatnr());
					pt.setFirstPayment(wa_pt.get(0).getMonthly_payment_sum()
							* p_currate);
					double mrest = pt.priceNative - pt.firstPayment;
					pt.setMrest(mrest);
					priceTable.add(pt);

					System.out.println("Price: " + pt.getPrice()
							+ " NativePrice: " + pt.getPriceNative()
							+ " Month: " + pt.getMonth());
				}
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:priceTable");

	}

	// ******************************************************************************************************

	public void loadPromoMatnrListByBukrs(String a_bukrs) {
		p_matnr_list = new ArrayList<Matnr>();
		for (Matnr m : p_matnrF4Bean.getByBukrs(p_contract.getBukrs())) {
			if ((m.getType() == 8)) {
				p_matnr_list.add(m);
				// System.out.println(m.getText45());
			}
		}
	}

	// *********************************************************************************

	public void clearStuffField(int a_pos) {
		// System.out.println("Field position to clear: " + a_pos);
		ContractHistory wa_ch = new ContractHistory(p_contract.getContract_id());

		String oldFio = "";
		String wa_info = "";
		StaffDao stfDao = (StaffDao) appContext.getContext()
				.getBean("staffDao");

		if (a_pos == 4) {
			removeCHLif(40L);
			if (p_oldcontract.getDealer() != null
					&& p_oldcontract.getDealer() > 0) {
				wa_ch.setOld_id(p_contract.getDealer());
				wa_ch.setOper_on(40L);
				wa_ch.setOper_type(2L);

				oldFio = stfDao.find(p_oldcontract.getDealer()).getLF();
				wa_info = "Dealer: " + oldFio + " is removed.";
				wa_info += " (User: " + userData.getUsername() + ")";
				wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
				wa_ch.setUser_id(userData.getUserid());
				wa_ch.setInfo(wa_info);
				wa_ch.setProcessed(0);

				wa_ch.setOld_text(oldFio);
				chL.add(wa_ch);
			}

			p_contract.setDealer(null);
			p_fioDealer = "";
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:dealer");
		} else if (a_pos == 8) {
			removeCHLif(50L);
			if (p_oldcontract.getDemo_sc() != null
					&& p_oldcontract.getDemo_sc() > 0) {
				wa_ch.setOld_id(p_oldcontract.getCollector());
				wa_ch.setOper_on(50L);
				wa_ch.setOper_type(2L);
				oldFio = stfDao.find(p_oldcontract.getDemo_sc()).getLF();
				wa_info = "Demo-secretary: " + oldFio + " is removed.";
				wa_info += " (User: " + userData.getUsername() + ")";
				wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
				wa_ch.setUser_id(userData.getUserid());
				wa_ch.setInfo(wa_info);
				wa_ch.setProcessed(0);
				wa_ch.setOld_text(oldFio);
				chL.add(wa_ch);
			}

			p_contract.setDemo_sc(null);
			p_fioDemoSec = "";
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:demo_sc");
		} else if (a_pos == 9) {
			removeCHLif(60L);
			if (p_oldcontract.getCollector() != null
					&& p_oldcontract.getCollector() > 0) {
				wa_ch.setOld_id(p_oldcontract.getCollector());
				wa_ch.setOper_on(60L);
				wa_ch.setOper_type(2L);

				oldFio = stfDao.find(p_oldcontract.getCollector()).getLF();
				wa_info = "Collector: " + oldFio + " is removed.";
				wa_info += " (User: " + userData.getUsername() + ")";
				wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
				wa_ch.setUser_id(userData.getUserid());
				wa_ch.setInfo(wa_info);
				wa_ch.setProcessed(0);
				wa_ch.setOld_text(oldFio);
				chL.add(wa_ch);
			}

			p_contract.setCollector(null);
			p_fioColl = "";
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:collector");
		} else if (a_pos == -1) {
			// System.out.println("Customer is being cleaned.");
			p_contract.setCustomer_id(0L);
			p_fio = "";
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:fio");
		} else if (a_pos == -2) {
			// System.out.println("Referencer is being cleaned.");
			removeCHLif(90L);
			if (p_oldcontract.getRef_contract_id() != null
					&& p_oldcontract.getRef_contract_id() > 0) {
				wa_ch.setOld_id(p_oldcontract.getRef_contract_id());
				wa_ch.setOper_on(90L);
				wa_ch.setOper_type(2L);

				oldFio = p_fioRef;
				wa_info = "Referencer: " + oldFio + " is removed.";
				wa_info += " (User: " + userData.getUsername() + ")";
				wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
				wa_ch.setUser_id(userData.getUserid());
				wa_ch.setInfo(wa_info);
				wa_ch.setProcessed(0);
				wa_ch.setOld_text(oldFio);
				chL.add(wa_ch);
			}

			p_contract.setRef_contract_id(0L);
			p_fioRef = "";
			ref_contr = null;
			resetRefDisc();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:ref-id");
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:contractHistoryTable");
	}

	// **************************Employee*******************************
	private List<Staff> l_dealer = new ArrayList<Staff>();

	public List<Staff> getL_dealer() {
		return l_dealer;
	}

	public void setL_dealer(List<Staff> l_dealer) {
		this.l_dealer = l_dealer;
	}

	private Long p_dealer_id;

	public Long getP_dealer_id() {
		return p_dealer_id;
	}

	public void setP_dealer_id(Long p_dealer_id) {
		this.p_dealer_id = p_dealer_id;
	}

	public void loadDealers() {
		try {

			p_dealer_id = null;
			l_dealer = new ArrayList<Staff>();
			String dynamicWhereClause = "";
			StaffDao staffDao = (StaffDao) appContext.getContext().getBean(
					"staffDao");
			dynamicWhereClause = dynamicWhereClause
					+ " and sal.position_id = 4 and sal.branch_id = "
					+ p_branch_id + " and sal.bukrs = '" + p_bukrs + "'";
			l_dealer = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
			// System.out.println(l_dealer.size());
			if (l_dealer.size() > 0) {
				for (Staff wa_staff : l_dealer) {
					wa_staff.setFirstname(wa_staff.getLF());
				}
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:dealerref");
			reqCtx.update("form:outputTable");

		} catch (DAOException ex) {
			GeneralUtil.addMessage("Info", ex.getMessage());
		}
	}

	// *********************************************************************
	private String p_bukrs;

	public String getP_bukrs() {
		return p_bukrs;
	}

	public void setP_bukrs(String p_bukrs) {
		this.p_bukrs = p_bukrs;
	}

	private Long p_branch_id;

	public Long getP_branch_id() {
		return p_branch_id;
	}

	public void setP_branch_id(Long p_branch_id) {
		this.p_branch_id = p_branch_id;
	}

	// **********************************************************************

	public ContractDetails selectedRefContr;

	public ContractDetails getSelectedRefContr() {
		return selectedRefContr;
	}

	public void setSelectedRefContr(ContractDetails selectedRefContr) {
		this.selectedRefContr = selectedRefContr;
	}

	// ********************************************************************************

	public double refRate = 0;
	public String refWaers = "";
	public String refDiscountWaers = "";

	public String getRefDiscountWaers() {
		return refDiscountWaers;
	}

	public void setRefDiscountWaers(String refDiscountWaers) {
		this.refDiscountWaers = refDiscountWaers;
	}

	public String getRefWaers() {
		return refWaers;
	}

	public void setRefWaers(String refWaers) {
		this.refWaers = refWaers;
	}

	public double getRefRate() {
		return refRate;
	}

	public void setRefRate(double refRate) {
		this.refRate = refRate;
	}

	public boolean disRef;

	public boolean isDisRef() {
		return disRef;
	}

	public void setDisRef(boolean disRef) {
		this.disRef = disRef;
	}

	// ***************************CurrencyF4*******************************
	@ManagedProperty(value = "#{currencyF4Bean}")
	private CurrencyF4 p_currencyF4Bean;

	public CurrencyF4 getP_currencyF4Bean() {
		return p_currencyF4Bean;
	}

	public void setP_currencyF4Bean(CurrencyF4 p_currencyF4Bean) {
		this.p_currencyF4Bean = p_currencyF4Bean;
	}

	List<Currency> currency_list = new ArrayList<Currency>();

	public List<Currency> getCurrency_list() {
		return currency_list;
	}

	public double getCurrencyRate(String main_currency, String sec_currency) {
		double out;
		out = 1;
		if (sec_currency != null && sec_currency.length() > 0) {
			System.out.println("INTERNAL REATE RERQUEST: " + "2" + sec_currency
					+ p_contract.getBukrs());

			out = getExrDao().getLastCurrencyRateInternal(
					p_contract.getBukrs(), main_currency, sec_currency);
			// if (main_currency.equals(sec_currency)) return 1;
			// ExchangeRate wa_exr = p_exchangeRateF4Bean.getL_er_map_internal()
			// .get("2" + sec_currency + p_contract.getBukrs());
			// out = wa_exr.getSc_value();
			System.out.println("INTERNAL REATE: " + out);
		}
		return out;
	}

	private ExchangeRateDao getExrDao() {
		return (ExchangeRateDao) appContext.getContext().getBean(
				"exchangeRateDao");
	}

	// ***************** Get Currency & Rate from branch *******************
	public String getCurrencyName(Long wa_branch_id) {
		String out;
		out = "";
		for (Branch wa_branch : p_branchF4Bean.getBranch_list()) {
			if (wa_branch.getBranch_id().equals(wa_branch_id)) {
				for (Country wa_country : p_countryF4Bean.getCountry_list()) {
					if (wa_country.getCountry_id().equals(
							wa_branch.getCountry_id())) {
						for (Currency wa_currency : p_currencyF4Bean
								.getCurrency_list()) {
							if (wa_currency.getCurrency_id().equals(
									wa_country.getCurrency_id())) {
								out = wa_currency.getCurrency();
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return out;
	}

	public void assignFoundRefContrId() {
		if (selectedRefContr != null
				&& selectedRefContr.getContract().getContract_id() != null
				&& selectedRefContr.getContract().getContract_id() > 0
				&& !selectedRefContr.getContract().getContract_id()
						.equals(p_contract.getContract_id())) {
			ContractHistory wa_ch = new ContractHistory(
					p_contract.getContract_id());
			String oldRef = p_fioRef;
			String wa_info = "";

			ref_contr = selectedRefContr;
			p_contract.setRef_contract_id(selectedRefContr.getContract()
					.getContract_id());
			p_fioRef = " SN:"
					+ selectedRefContr.getContract().getContract_number() + " "
					+ selectedRefContr.getCustomer().getFullFIO();

			// ********************************************************************
			if (ref_contr.getContract().getContract_number() != null) {
				refWaers = ref_contr.getContract().getWaers();
				refRate = ref_contr.getContract().getRate();
				System.out.println("RefRate: " + refRate);
			}

			ref_contr.setBranch(p_branchF4Bean.getL_branch_map().get(
					ref_contr.getContract().getBranch_id()));

			disRef = true;
			if (refWaers.equals("USD")) {
				// disRef = false;
				// GeneralUtil.addInfoMessage("Введите курс рекомендателя!");
				if (ref_contr.getBranch().getBranch_id() != null) {
					refWaers = getCurrencyName(ref_contr.getBranch()
							.getBranch_id());
				}
				refRate = getCurrencyRate("USD", refWaers);
				GeneralUtil
						.addInfoMessage("Скидка рекомендателю присвоен текущий внутренний курс! "
								+ refRate + " " + refWaers);
			}
			
			refRate=1;

			ref_contr.setBranch(p_branchF4Bean.getL_branch_map().get(
					ref_contr.getContract().getBranch_id()));
			// System.out.println("REF CnotractType: " +
			// ref_contr.getContrType().getName());
			
			ContractType p_ct = p_contractTypeF4Bean.getCt_map().get(p_contract.getContract_type_id());
			Branch p_br = p_branchF4Bean.getL_branch_map().get(p_contract.getBranch_id());
			
			//System.out.println("REF CnotractType: " + ref_contr.getContrType().getName());
//			String wcl = " bukrs = '" + p_contract.getBukrs()
//					+ "' and bonus_type_id = 8 and position_id = -1 "
//					+ " and matnr = " + p_ct.getMatnr()
//					+ " and country_id = " + p_br.getCountry_id();
//			BonusDao bonDao = (BonusDao) appContext.getContext().getBean(
//					"bonusDao");
			BonusDao bonDao = (BonusDao) appContext.getContext().getBean(
					"bonusDao");
			int skidkaRek = 16;//Скидка от дилера клиенту
			String wcl = " bonus_type_id = "+skidkaRek+" and branch_id = " + p_contract.getBranch_id();
			
			List<Bonus> bL = bonDao.dynamicFindBonuses(wcl);
			Bonus skidka_rek = new Bonus();
			if (bL.size() > 0) {
				skidka_rek = bL.get(0);
				refDiscountWaers = skidka_rek.getWaers();
				System.out.println("Skidka amount: " + skidka_rek.getCoef()
						+ " " + skidka_rek.getWaers());
			} else
				GeneralUtil
						.addErrorMessage("Couldn't find Referencer's discount in Bonus Table!");

			Branch refBr = p_branchF4Bean.getL_branch_map().get(
					ref_contr.getContract().getBranch_id());
			Country refCountry = p_countryF4Bean.getL_country_map().get(
					refBr.getCountry_id());
			refWaers = refCountry.getCurrency();

			resetRefDisc();
			p_contr_num = null;
			// outputTable = new ArrayList<OutputTableClass>();

			// History
			wa_ch.setNew_id(p_contract.getRef_contract_id());
			wa_ch.setNew_text(p_fioRef);
			wa_ch.setOper_on(90L);
			if (p_oldcontract.getRef_contract_id() != null
					&& p_oldcontract.getRef_contract_id() > 0) {
				wa_ch.setOld_id(p_oldcontract.getRef_contract_id());
				wa_ch.setOld_text(oldRef);
				wa_ch.setOper_type(3L);
				wa_info = "Referencer changed.";
			} else {
				wa_ch.setOper_type(1L);
				wa_info = "Referencer assigned.";
			}

			wa_info += " (User: " + userData.getUsername() + ")";
			wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
			wa_ch.setUser_id(userData.getUserid());
			wa_ch.setInfo(wa_info);
			wa_ch.setProcessed(0);
			removeCHLif(90L);
			chL.add(wa_ch);

			disable_save_button();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:outputTable");
			reqCtx.update("form:ref-id");
			reqCtx.update("form:contr_num");
			reqCtx.update("form:contractHistoryTable");
			reqCtx.update("form");
			reqCtx.update("form:refRate");
			reqCtx.update("form:currencyName7");

		} else {
			GeneralUtil.addMessage("Empty!", "No contract selected!");
		}
	}

	// *********************************************************************************

	public Long p_contr_num;

	public Long getP_contr_num() {
		return p_contr_num;
	}

	public void setP_contr_num(Long p_contr_num) {
		this.p_contr_num = p_contr_num;
	}

	// *****************************************************************************************************
	// ****************************_REFERENCER_CONTRACTS_LIST_**********************************************
	// *****************************************************************************************************

	public int getOutputLength() {
		return this.outputTable.getRowCount();
	}

	public void searchRefContr() {
		try {
			outputTable.searchModel.setMarkedType(Contract.MT_STANDARD_PRICE);
			outputTable.searchModel.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
			loadContractModel();
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Info", ex.getMessage());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:outputTable");
	}

	private void loadContractModel() {
		String cond = " ";
		if (outputTable.getSearchModel().getContract_number() != null
				&& outputTable.getSearchModel().getContract_number() > 0) {
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bukrs");
			reqCtx.update("form:branch");
			reqCtx.update("form:dealer");
			reqCtx.update("form:collector");
			reqCtx.update("form:start_date");
			reqCtx.update("form:end_date");
		} else {
			if (outputTable.getSearchModel().getDealer() != null
					&& outputTable.getSearchModel().getDealer().intValue() == -1
					&& l_dealer.size() > 0) {
				cond += " and dealer not in (";
				int count = 0;
				for (Staff wa_staff : l_dealer) {
					count = count + 1;
					if (count == 1) {
						cond += wa_staff.getStaff_id();
					} else {
						cond += ", " + wa_staff.getStaff_id();
					}
				}
				cond += ")";
			}
		}

		// initContractModel();
		outputTable.setCondition(cond);
	}

	public void initContractModel() {
		outputTable = new ContractModel((ContractDao) appContext.getContext()
				.getBean("contractDao"));
	}

	// ***************************************************************************************************
	// ***************************************************************************************************
	// ***************************************************************************************************

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ******************************************************************
	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	// ******************************************************************
	// ***************************Contract**********************************
	private Contract p_contract = new Contract();

	public Contract getP_contract() {
		return p_contract;
	}

	public void setP_contract(Contract p_contract) {
		this.p_contract = p_contract;
	}

	// ******************************************************************
	// ***************************ContractStatusF4*******************************
	@ManagedProperty(value = "#{contractStatusF4Bean}")
	private ContractStatusF4 p_contractStatusF4Bean;

	public ContractStatusF4 getP_contractStatusF4Bean() {
		return p_contractStatusF4Bean;
	}

	public void setP_contractStatusF4Bean(
			ContractStatusF4 p_contractStatusF4Bean) {
		this.p_contractStatusF4Bean = p_contractStatusF4Bean;
	}

	// ******************************************************************
	// ***************************BranchF4*******************************
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 p_branchF4Bean;

	public BranchF4 getP_branchF4Bean() {
		return p_branchF4Bean;
	}

	public void setP_branchF4Bean(BranchF4 p_branchF4Bean) {
		this.p_branchF4Bean = p_branchF4Bean;
	}

	List<Branch> branch_list = new ArrayList<Branch>();

	public List<Branch> getBranch_list() {
		return branch_list;
	}

	List<Branch> serv_branch_list = new ArrayList<Branch>();

	public List<Branch> getServ_branch_list() {
		return serv_branch_list;
	}

	// ********************************************************************
	// ***************************Country*******************************
	@ManagedProperty(value = "#{countryF4Bean}")
	private CountryF4 p_countryF4Bean;

	public CountryF4 getP_countryF4Bean() {
		return p_countryF4Bean;
	}

	public void setP_countryF4Bean(CountryF4 p_countryF4Bean) {
		this.p_countryF4Bean = p_countryF4Bean;
	}

	List<Country> country_list = new ArrayList<Country>();

	public List<Country> getCountry_list() {
		return country_list;
	}

	// ******************************************************************
	// ***************************State*******************************
	@ManagedProperty(value = "#{stateF4Bean}")
	private StateF4 p_stateF4Bean;

	public StateF4 getP_stateF4Bean() {
		return p_stateF4Bean;
	}

	public void setP_stateF4Bean(StateF4 p_stateF4Bean) {
		this.p_stateF4Bean = p_stateF4Bean;
	}

	List<State> state_list = new ArrayList<State>();

	public List<State> getState_list() {
		return state_list;
	}

	// ******************************************************************
	// ***************************City*******************************
	@ManagedProperty(value = "#{cityF4Bean}")
	private CityF4 p_cityF4Bean;

	public CityF4 getP_cityF4Bean() {
		return p_cityF4Bean;
	}

	public void setP_cityF4Bean(CityF4 p_cityF4Bean) {
		this.p_cityF4Bean = p_cityF4Bean;
	}

	List<City> city_list = new ArrayList<City>();

	public List<City> getCity_list() {
		return city_list;
	}

	// ******************************************************************
	// ***************************Cityreg*******************************
	@ManagedProperty(value = "#{cityregF4Bean}")
	private CityregF4 p_cityregF4Bean;

	public CityregF4 getP_cityregF4Bean() {
		return p_cityregF4Bean;
	}

	public void setP_cityregF4Bean(CityregF4 p_cityregF4Bean) {
		this.p_cityregF4Bean = p_cityregF4Bean;
	}

	List<Cityreg> cityreg_list = new ArrayList<Cityreg>();

	public List<Cityreg> getCityreg_list() {
		return cityreg_list;
	}

	// ******************************************************************
	// ****************************ContractTypeF4***************************
	@ManagedProperty(value = "#{contractStatusF4Bean}")
	private ContractStatusF4 p_csF4Bean;

	public ContractStatusF4 getP_csF4Bean() {
		return p_csF4Bean;
	}

	public void setP_csF4Bean(ContractStatusF4 p_csF4Bean) {
		this.p_csF4Bean = p_csF4Bean;
	}

	List<ContractStatus> cs_list = new ArrayList<ContractStatus>();

	public List<ContractStatus> getCs_list() {
		return cs_list;
	}

	// ********************************************************************
	// ***************************ContractTypeF4*******************************
	@ManagedProperty(value = "#{contractTypeF4Bean}")
	private ContractTypeF4 p_contractTypeF4Bean;

	public ContractTypeF4 getP_contractTypeF4Bean() {
		return p_contractTypeF4Bean;
	}

	public void setP_contractTypeF4Bean(ContractTypeF4 p_contractTypeF4Bean) {
		this.p_contractTypeF4Bean = p_contractTypeF4Bean;
	}

	List<ContractType> contractType_list = new ArrayList<ContractType>();

	public List<ContractType> getContractType_list() {
		return contractType_list;
	}

	// ******************************************************************
	// ***************************PriceListF4*******************************
	@ManagedProperty(value = "#{priceListF4Bean}")
	private PriceListF4 p_priceListF4Bean;

	public PriceListF4 getP_priceListF4Bean() {
		return p_priceListF4Bean;
	}

	public void setP_priceListF4Bean(PriceListF4 p_priceListF4Bean) {
		this.p_priceListF4Bean = p_priceListF4Bean;
	}

	List<PriceList> priceList_list = new ArrayList<PriceList>();

	public List<PriceList> getPriceList_list() {
		return priceList_list;
	}

	// ******************************************************************
	// ***************************PaymentTemplateF4*******************************
	@ManagedProperty(value = "#{paymentTemplateF4Bean}")
	private PaymentTemplateF4 p_paymentTemplateF4Bean;

	public PaymentTemplateF4 getP_paymentTemplateF4Bean() {
		return p_paymentTemplateF4Bean;
	}

	public void setP_paymentTemplateF4Bean(
			PaymentTemplateF4 p_paymentTemplateF4Bean) {
		this.p_paymentTemplateF4Bean = p_paymentTemplateF4Bean;
	}

	List<PaymentTemplate> paymentTemplate_list = new ArrayList<PaymentTemplate>();

	public List<PaymentTemplate> getPaymentTemplate_list() {
		return paymentTemplate_list;
	}

	// ******************************************************************
	// ***************************BukrsF4*******************************
	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;

	public BukrsF4 getP_bukrsF4Bean() {
		return p_bukrsF4Bean;
	}

	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
		this.p_bukrsF4Bean = p_bukrsF4Bean;
	}

	List<Bukrs> bukrs_list = new ArrayList<Bukrs>();

	public List<Bukrs> getBukrs_list() {
		return bukrs_list;
	}

	// ******************************************************************
	// ***************************MatnrF4********************************
	@ManagedProperty(value = "#{matnrF4Bean}")
	private MatnrF4 p_matnrF4Bean;

	public MatnrF4 getP_matnrF4Bean() {
		return p_matnrF4Bean;
	}

	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
		this.p_matnrF4Bean = p_matnrF4Bean;
	}

	public List<Matnr> p_matnr_list = new ArrayList<Matnr>();

	public List<Matnr> getP_matnr_list() {
		return p_matnr_list;
	}

	// ******************************************************************
	// *********************************************************************
	// *********FIO of Customer, Dealer, Demo secretary, and Collector******

	private String p_fio;

	public String getP_fio() {
		return p_fio;
	}

	public void setP_fio(String p_fio) {
		this.p_fio = p_fio;
	}

	private String p_fioDealer;

	public String getP_fioDealer() {
		return p_fioDealer;
	}

	public void setP_fioDealer(String p_fioDealer) {
		this.p_fioDealer = p_fioDealer;
	}

	private String p_fioDemoSec;

	public String getP_fioDemoSec() {
		return p_fioDemoSec;
	}

	public void setP_fioDemoSec(String p_fioDemoSec) {
		this.p_fioDemoSec = p_fioDemoSec;
	}

	private String p_fioColl;

	public String getP_fioColl() {
		return p_fioColl;
	}

	public void setP_fioColl(String p_fioColl) {
		this.p_fioColl = p_fioColl;
	}

	public boolean ref_disc;

	public boolean isRef_disc() {
		return ref_disc;
	}

	public void setRef_disc(boolean ref_disc) {
		this.ref_disc = ref_disc;
	}

	public String p_currency;

	public String getP_currency() {
		return p_currency;
	}

	public void setP_currency(String p_currency) {
		this.p_currency = p_currency;
	}

	public double p_currate;

	public double getP_currate() {
		return p_currate;
	}

	public void setP_currate(double p_currate) {
		this.p_currate = p_currate;
	}

	public String p_fioFitter;
	public String p_fioRef;

	public String getP_fioFitter() {
		return p_fioFitter;
	}

	public void setP_fioFitter(String p_fioFitter) {
		this.p_fioFitter = p_fioFitter;
	}

	public String getP_fioRef() {
		return p_fioRef;
	}

	public void setP_fioRef(String p_fioRef) {
		this.p_fioRef = p_fioRef;
	}

	// *********************************************************************

	// **************************Employee*******************************
	private List<Staff> p_staff_list = new ArrayList<Staff>();

	public List<Staff> getP_staff_list() {
		return p_staff_list;
	}

	public void setP_staff_list(List<Staff> p_staff_list) {
		this.p_staff_list = p_staff_list;
	}

	private Staff selectedStaff = new Staff();

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	private Staff searchStaff = new Staff();

	public Staff getSearchStaff() {
		return searchStaff;
	}

	public void setSearchStaff(Staff searchStaff) {
		this.searchStaff = searchStaff;
	}

	// *********************************************************************

	public Customer selectedCustomer = new Customer();

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	private Long contract_number_search;

	public Long getContract_number_search() {
		return contract_number_search;
	}

	public void setContract_number_search(Long contract_number_search) {
		this.contract_number_search = contract_number_search;
	}

	private int p_search_position_id;

	public int getP_search_position_id() {
		return p_search_position_id;
	}

	public void setP_search_position_id(int p_search_position_id) {
		this.p_search_position_id = p_search_position_id;
	}

	private boolean statusChangeRole = true;

	public boolean isStatusChangeRole() {
		return statusChangeRole;
	}

	public void setStatusChangeRole(boolean statusChangeRole) {
		this.statusChangeRole = statusChangeRole;
	}

	private Long initial_contract_status_id;

	private Date initial_new_contract_date;
	private double new_adding_price;
	private boolean new_adding_priceCheck = true;

	private boolean updateButton = false;

	public boolean isUpdateButton() {
		return updateButton;
	}

	public void setUpdateButton(boolean updateButton) {
		this.updateButton = updateButton;
	}

	public double getNew_adding_price() {
		return new_adding_price;
	}

	public void setNew_adding_price(double new_adding_price) {
		this.new_adding_price = new_adding_price;
	}

	public boolean isNew_adding_priceCheck() {
		return new_adding_priceCheck;
	}

	public void setNew_adding_priceCheck(boolean new_adding_priceCheck) {
		this.new_adding_priceCheck = new_adding_priceCheck;
	}

	private Long contract_number;

	public Long getContract_number() {
		return contract_number;
	}

	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
	}

	// ***************************************************************************************
	public PaymentScheduleDetails psDetFirstPayment = new PaymentScheduleDetails();
	public List<PaymentScheduleDetails> psDetL = new ArrayList<PaymentScheduleDetails>();

	public PaymentScheduleDetails getPsDetFirstPayment() {
		return psDetFirstPayment;
	}

	public void setPsDetFirstPayment(PaymentScheduleDetails psDetFirstPayment) {
		this.psDetFirstPayment = psDetFirstPayment;
	}

	public List<PaymentScheduleDetails> getPsDetL() {
		return psDetL;
	}

	public void setPsDetL(List<PaymentScheduleDetails> psDetL) {
		this.psDetL = psDetL;
	}

	// ***************************************************************************************************
	public PaymentTemplate wa_paymentTemplate;

	public PaymentTemplate getWa_paymentTemplate() {
		return wa_paymentTemplate;
	}

	public void setWa_paymentTemplate(PaymentTemplate wa_paymentTemplate) {
		this.wa_paymentTemplate = wa_paymentTemplate;
	}

	public PriceTableClass selectedPrice;

	public PriceTableClass getSelectedPrice() {
		return selectedPrice;
	}

	public void setSelectedPrice(PriceTableClass selectedPrice) {
		this.selectedPrice = selectedPrice;
	}

	private List<PriceTableClass> priceTable = new ArrayList<PriceTableClass>();

	public List<PriceTableClass> getPriceTable() {
		return priceTable;
	}

	public void setPriceTable(List<PriceTableClass> priceTable) {
		this.priceTable = priceTable;
	}

	// *******************************************************************************************
	public List<PromoTable> selectedPTS;
	public List<PromoTable> initialPTS;

	public List<PromoTable> getSelectedPTS() {
		return selectedPTS;
	}

	public void setSelectedPTS(List<PromoTable> selectedPTS) {
		this.selectedPTS = selectedPTS;
	}

	public List<PromoTable> promoTable;

	public List<PromoTable> getPromoTable() {
		return promoTable;
	}

	public void setPromoTable(List<PromoTable> promoTable) {
		this.promoTable = promoTable;
	}

	private Long promos[];

	private double skidka_from_ref = 0;

	public String p_promoName;

	public String getP_promoName() {
		return p_promoName;
	}

	public void setP_promoName(String p_promoName) {
		this.p_promoName = p_promoName;
	}

	public void assignInitials() {
		// p_contract.setNew_contract_date(initial_new_contract_date);

	}

	// *******************************************************************************************

	public String p_check_text;
	public boolean p_check_payments;
	public String p_check_icon; // ui-icon ui-icon-check
	public String p_check_text_color;
	public boolean p_disabled_save_button;

	public double min_first_payment;
	public String mon_dis[] = { "true", "true", "true", "true", "true", "true",
			"true", "true", "true", "true", "true", "true", "true" };

	public String[] getMon_dis() {
		return mon_dis;
	}

	public void setMon_dis(String[] mon_dis) {
		this.mon_dis = mon_dis;
	}

	public boolean isP_disabled_save_button() {
		return p_disabled_save_button;
	}

	public void setP_disabled_save_button(boolean p_disabled_save_button) {
		this.p_disabled_save_button = p_disabled_save_button;
	}

	public String getP_check_text_color() {
		return p_check_text_color;
	}

	public void setP_check_text_color(String p_check_text_color) {
		this.p_check_text_color = p_check_text_color;
	}

	public String getP_check_text() {
		return p_check_text;
	}

	public void setP_check_text(String p_check_text) {
		this.p_check_text = p_check_text;
	}

	public boolean isP_check_payments() {
		return p_check_payments;
	}

	public void setP_check_payments(boolean p_check_payments) {
		this.p_check_payments = p_check_payments;
	}

	public String getP_check_icon() {
		return p_check_icon;
	}

	public void setP_check_icon(String p_check_icon) {
		this.p_check_icon = p_check_icon;
	}

	public boolean dis_field1;
	public boolean dis_field2;
	public String ref_textClass;

	public String getRef_textClass() {
		return ref_textClass;
	}

	public void setRef_textClass(String ref_textClass) {
		this.ref_textClass = ref_textClass;
	}

	public boolean isDis_field1() {
		return dis_field1;
	}

	public void setDis_field1(boolean dis_field1) {
		this.dis_field1 = dis_field1;
	}

	public boolean isDis_field2() {
		return dis_field2;
	}

	public void setDis_field2(boolean dis_field2) {
		this.dis_field2 = dis_field2;
	}

	// ****************************************HISTORY********************************************

	public List<ContractHistory> chL = new ArrayList<ContractHistory>();

	public List<ContractHistory> getChL() {
		return chL;
	}

	public void setChL(List<ContractHistory> chL) {
		this.chL = chL;
	}

	// *********************************OLD_AND_NEW_CONTRACT_DETAILS******************************

	private Contract p_oldcontract;
	public ContractDetails new_cd;

	public ContractDetails getNew_cd() {
		return new_cd;
	}

	public void setNew_cd(ContractDetails new_cd) {
		this.new_cd = new_cd;
	}

	// *********************************************************************

	public PaymentSchedule ps[] = new PaymentSchedule[36];

	public PaymentSchedule[] getPs() {
		return ps;
	}

	public void setPs(PaymentSchedule[] ps) {
		this.ps = ps;
	}

	// *********************************************************************
	private int p_selected_position_id;

	public int getP_selected_position_id() {
		return p_selected_position_id;
	}

	public void setP_selected_position_id(int p_selected_position_id) {
		this.p_selected_position_id = p_selected_position_id;
	}

	// *****************************************************

	// **************************************************************************************
	public boolean ref_discount;

	public boolean isRef_discount() {
		return ref_discount;
	}

	public void setRef_discount(boolean ref_discount) {
		this.ref_discount = ref_discount;
	}

	public String refDiscStatus;

	public String getRefDiscStatus() {
		return refDiscStatus;
	}

	public void setRefDiscStatus(String refDiscStatus) {
		this.refDiscStatus = refDiscStatus;
	}

	public ContractDetails ref_contr;

	public ContractDetails getRef_contr() {
		return ref_contr;
	}

	public void setRef_contr(ContractDetails ref_contr) {
		this.ref_contr = ref_contr;
	}

	private ContractModel outputTable;

	public ContractModel getOutputTable() {
		return outputTable;
	}

	public void setOutputTable(ContractModel outputTable) {
		this.outputTable = outputTable;
	}

	// ***************************************************************************************************

	public boolean default_update;
	MessageProvider msgProvider = new MessageProvider();

	public boolean disSelMatnr = true;

	public boolean isDisSelMatnr() {
		return disSelMatnr;
	}

	public void setDisSelMatnr(boolean disSelMatnr) {
		this.disSelMatnr = disSelMatnr;
	}

	private Customer searchCustomer = new Customer();

	public Customer getSearchCustomer() {
		return searchCustomer;
	}

	public void setSearchCustomer(Customer searchCustomer) {
		this.searchCustomer = searchCustomer;
	}

	public void clearCustomerField() {
		outputTable.searchModel.setCustomer_id(null);
		outputTable.searchModel.customer_fio = "";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:refCustomerFio");
	}

	// ******************************* CUSTOMER *****************************

	public void to_search_customer() {
		try {
			CustomerService personService = (CustomerService) appContext
					.getContext().getBean("customerService");
			p_customer_list = personService.dynamicSearch(searchCustomer);

			if (p_customer_list.size() == 0) {
				p_customer_list = new ArrayList<Customer>();
				GeneralUtil.addInfoMessage("Не найдено.");
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:customerTable");

		} catch (DAOException ex) {
			p_customer_list = new ArrayList<Customer>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:customerTable");
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void assignFoundCustomer() {
		if (selectedCustomer != null && selectedCustomer.getId() != null) {
			outputTable.searchModel.setCustomer_id(selectedCustomer.getId());
			outputTable.searchModel.setCustomer_fio(selectedCustomer.getFIO());
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:refCustomerFio");
		} else
			clearCustomerField();
		selectedCustomer = new Customer();
	}

	// *************************** Customer ********************************
	private List<Customer> p_customer_list = new ArrayList<Customer>();

	public List<Customer> getP_customer_list() {
		return p_customer_list;
	}

	public void setP_customer_list(List<Customer> p_customer_list) {
		this.p_customer_list = p_customer_list;
	}

	// ***************************AddrTypeF4*****************************
	@ManagedProperty(value = "#{addrTypeF4Bean}")
	private AddrTypeF4 p_addrTypeF4Bean;
	List<AddressType> addrType_list = new ArrayList<AddressType>();

	public AddrTypeF4 getP_addrTypeF4Bean() {
		return p_addrTypeF4Bean;
	}

	public void setP_addrTypeF4Bean(AddrTypeF4 p_addrTypeF4Bean) {
		this.p_addrTypeF4Bean = p_addrTypeF4Bean;
	}

	public List<AddressType> getAddrType_list() {
		return p_addrTypeF4Bean.getAddrType_list();
	}

	// ********************************************************

	List<Branch> staffBranch_list = new ArrayList<Branch>();

	public List<Branch> getStaffBranch_list() {
		return staffBranch_list;
	}

	public void setStaffBranch_list(List<Branch> staffBranch_list) {
		this.staffBranch_list = staffBranch_list;
	}
	
	// ********************************************************
	
	public boolean disRefField;

	public boolean isDisRefField() {
		return disRefField;
	}

	public void setDisRefField(boolean disRefField) {
		this.disRefField = disRefField;
	}
	
}
