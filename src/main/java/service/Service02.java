package service;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.ExchangeRateF4;
import f4.MatnrF4;
import f4.PriceListF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.clone.Clone;
import general.dao.AddressDao;
import general.dao.BonusDao;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.MatnrSparePartDao;
import general.dao.MatnrWarDao;
import general.dao.ServConMatnrWarDao;
import general.dao.ServFilterPremisDao;
import general.dao.ServPacketDao;
import general.dao.ServPacketPosDao;
import general.dao.ServPacketWarDao;
import general.dao.ServPosDao;
import general.dao.ServiceDao;
import general.dao.StaffDao;
import general.dao.UserRoleDao;
import general.dao.WerksBranchDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchTypeFilter;
import general.output.tables.ServConMatnrWarOutput;
import general.output.tables.ServPacketWarOutput;
import general.services.CustomerService;
import general.services.ServFilterService;
import general.services.ServiceService;
import general.services.StaffService;
import general.tables.Address;
import general.tables.Bonus;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Contract;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.Currency;
import general.tables.Customer;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.MatnrSparePart;
import general.tables.MatnrWar;
import general.tables.PriceList;
import general.tables.Salary;
import general.tables.ServCRMHistory;
import general.tables.ServConMatnrWar;
import general.tables.ServFilter;
import general.tables.ServFilterPremis;
import general.tables.ServPacket;
import general.tables.ServPacketPos;
import general.tables.ServPacketWar;
import general.tables.ServiceApplication;
import general.tables.ServicePos;
import general.tables.ServiceTable;
import general.tables.Staff;
import general.tables.UserRole;
import general.tables.Werks;
import general.tables.search.MatnrSearch;
import general.tables.search.ServiceSearch;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import logistics.order.MatnrPriceList;

import org.primefaces.context.RequestContext;
import org.springframework.beans.BeanUtils;

import user.User;
import datamodels.ContractModel;
import dms.contract.ContractDetails;

@ManagedBean(name = "service02Bean", eager = true)
@ViewScoped
public class Service02 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -613107630833352096L;
	private final static String transaction_code = "SERVICE02";
	private final static Long transaction_id = (long) 96;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;

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
	// *********************************************************************
	@PostConstruct
	public void init() {
		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return; // Skip ajax requests.
		}
		
		
		
		
		
		p_service = new ServiceTable();
		try {
			PermissionController.canWrite(userData, this.transaction_id);
			service_number = new Long(
					GeneralUtil.getRequestParameter("service_number"));
			if (!Validation.isEmptyLong(service_number)) {
				p_service_search.setServ_num(service_number);
				
				
				
				if (!userData.isSys_admin()) {
					try {					

						ExternalContext context = FacesContext.getCurrentInstance()
								.getExternalContext();
						
							context.redirect(userData.getLinkToReact()+"/service/mainoperation/smes?serviceNumber="+service_number);
					} catch (Exception ex) {
						GeneralUtil.addErrorMessage(ex.getMessage());
					}
				}

//				to_search();
//				service_number = null;
			}
		} catch (NumberFormatException nex) {
			service_number = 0L;
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
			toMainPage();
		}
	}

	public void toMainPage() {
		GeneralUtil.doRedirect("/general/mainPage.xhtml");
	}

	public void toViewPage() {
		if (p_service != null && p_service.getServ_num() != null
				&& p_service.getServ_num() > 0)
			GeneralUtil
					.doRedirect("/service/maintenance/service03.xhtml?service_number="
							+ p_service.getServ_num());
	}

	public void to_search() {
		try {
			ServiceDao sDao = (ServiceDao) appContext.getContext().getBean(
					"serviceDao");
			System.out.println(p_service_search.getCondition());
			p_service = sDao.dynamicFindSingle(p_service_search.getCondition());
			p_service_old = p_service.clone();
			if (Validation.isEmptyString(p_service.getBukrs()))
				GeneralUtil.addInfoMessage("Service not found!");
			else {
				if (p_service.getServ_status() == ServiceTable.STATUS_CANCELLED) {
					toViewPage();
				}

				System.out.println("Bukrs: " + p_service.getBukrs());
				System.out.println("Paid: " + p_service.getPaid());

				ContractDao conDao = (ContractDao) appContext.getContext()
						.getBean("contractDao");
				p_contract = conDao.find(p_service.getContract_id());
				
				ContractTypeDao ctDao = (ContractTypeDao) appContext
						.getContext().getBean("contractTypeDao");
				if (!Validation.isEmptyLong(p_service.getContract_id()))
					ct = ctDao.find(p_contract.getContract_type_id());
				
				p_branch = p_branchF4Bean.getL_branch_map().get(
						p_service.getBranch_id());

				loadBranch();

				// assignBranchObject();
				searchStaff = new Staff();
				searchStaff.setBranch_id(p_service.getBranch_id());
				loadByCategory();

				MatnrDao mDao = (MatnrDao) appContext.getContext().getBean(
						"matnrDao");
				Matnr matnr = mDao.find(p_service.getTovar_id());

				loadMatnrPriceList();
				loadWerksMatnrList();

				p_tovar_name = matnr.getText45();

				CustomerDao cusDao = (CustomerDao) appContext.getContext()
						.getBean("customerDao");
				p_customer = cusDao.find(p_service.getCustomer_id());

				StaffDao stfDao = (StaffDao) appContext.getContext().getBean(
						"staffDao");
				p_master = stfDao.find(p_service.getMaster_id());
				if (p_service.getOper_id() != null
						&& p_service.getOper_id() > 0)
					p_oper = stfDao.find(p_service.getOper_id());

				prepareByServType();

				// Load ServPos List
				ServPosDao spDao = (ServPosDao) appContext.getContext()
						.getBean("servPosDao");
				servPosListTable = new ArrayList<ServPosListTable>();
				List<ServicePos> sp_l = spDao.findAllByServNumber(p_service
						.getServ_num());

				spl_old = new ArrayList<ServicePos>();
				for (ServicePos sp1:sp_l) {
					ServicePos sp2 = new ServicePos();
					BeanUtils.copyProperties(sp1, sp2);
					spl_old.add(sp2);
				}				
				
				int i = 1;
				sum_wrbtr = 0;
				sum_dmbtr = 0;
				for (ServicePos sp : sp_l) {
					ServPosListTable splt = new ServPosListTable();
					splt.setServPos(sp);
					splt.setDis_mat(true);
					splt.setDis_qq(true);
					splt.setDis_pr(true);
					splt.setGroup(1);
					splt.setIndex(i++);
					if (sp.getMatnr_id() != null && sp.getMatnr_id() > 0) {
						Matnr wa_matnr = mDao.find(sp.getMatnr_id());
						splt.setMatnr(wa_matnr);
					}
					splt.setSum_sc(sp.getSumm());
					sum_wrbtr += sp.getSumm();
					// no dmbtr
					if (sp.getWarranty() > 0)
						splt.setWarranty(true);
					else
						splt.setWarranty(false);
					servPosListTable.add(splt);					
				}

				String awkey = ((p_service.getAwkey() != null && p_service
						.getAwkey() > 0) ? p_service.getAwkey().toString() : "");
				belnr = "";
				gjahr = "";
				if (!Validation.isEmptyString(awkey)) {
					belnr = awkey.substring(0, awkey.length() - 4);
					gjahr = awkey.substring(awkey.length() - 4, awkey.length());
				}

				disPayBtn = false;
				if ((p_service.getServ_status() == 4 && p_service.getPaid() >= p_service
						.getPayment_due()) || p_service.getServ_type() == 2)
					disPayBtn = true;

				disFinBtn = false;
				if (p_service.getServ_type() == 2)
					disFinBtn = true;

				//loadSpList();
				loadSpWarDetails(p_service.getId());

				if (p_service.getServ_type() == 4) {
					loadSpList();
					enableServPacket();
				}
				else {
					disableServPacket();
				}

				p_currency = p_service.getCurrency();
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
			reqCtx.update("matnrForm");
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		} catch (CloneNotSupportedException cex) {
			throw new DAOException(cex.getMessage());
		}
	}

	public void assignBranchObject() {

		p_branch = p_branchF4Bean.getL_branch_map().get(
				p_service.getBranch_id());
		contractModel.searchModel.setServ_branch_id(p_service.getBranch_id());
		// p_currency = getCurrencyName(p_branch.getBranch_id());
		// p_service.setCurrency(p_currency);
		p_currency = p_service.getCurrency();
		// p_currate = p_exchangeRateF4Bean.getL_er_map_national()
		// .get("1" + p_currency).getSc_value();

		ExchangeRate er = p_exchangeRateF4Bean.getL_er_map_national()
				.get("1" + p_currency);
		p_currate = 1;
		if (er != null && er.getSc_value() > 0)
			p_currate = er.getSc_value();
		
		loadContractModel();
		// clearContractField();
		loadPriceList();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public void loadPriceList() {
		priceList_list = new ArrayList<PriceList>();

		if (p_service.getDate_open() != null) {
			for (PriceList wa_priceList2 : p_priceListF4Bean
					.getPriceListByCountryAndDate(p_service.getBukrs(),
							p_branch.getCountry_id(), p_service.getDate_open())) {

				priceList_list.add(wa_priceList2);
				pl.put(wa_priceList2.getMatnr(), wa_priceList2);

				// System.out.println("Matnr_id: " + wa_priceList2.getMatnr()
				// + " - Price: " + wa_priceList2.getPrice() + " "
				// + wa_priceList2.getWaers());
			}
		} else {
			GeneralUtil.addInfoMessage("Please select date!");
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:priceTable");
	}

	// ***********************************************************************

	public void loadByCategory() {
		st_l = new ArrayList<Service02.ServType_Loc>();
		ServType_Loc st = new ServType_Loc("Сервисное обслуживание",
				ServiceTable.TYPE_SERVICE);
		st_l.add(st);
		switch (p_service.getCategory()) {
		case 1: {
			st = new ServType_Loc("Сервис пакет", ServiceTable.TYPE_PACKET);
			st_l.add(st);
			break;
		}
		case 2: {
			st = new ServType_Loc("Замена фильтров", ServiceTable.TYPE_FILTERS);
			st_l.add(st);
			st = new ServType_Loc("Установка", ServiceTable.TYPE_FITTING);
			st_l.add(st);
			st = new ServType_Loc("Сервис пакет", ServiceTable.TYPE_PACKET);
			st_l.add(st);
			break;
		}
		default: {
			break;
		}
		}

		Comparator<ServType_Loc> c = new Comparator<Service02.ServType_Loc>() {
			@Override
			public int compare(ServType_Loc o1, ServType_Loc o2) {
				// TODO Auto-generated method stub
				int res = -1;
				if (o1.getV() > o2.getV())
					res = 1;
				return res;
			}
		};
		st_l.sort(c);
		// prepareStaffDlg();

		contractModel.searchModel.setTovar_category(p_service.getCategory());
		loadContractModel();
		// clearContractField();
		// clearServAppField();
		// for (ServType_Loc stl : st_l) { System.out.println(stl.v + " - " +
		// stl.n); }
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:serv_type");

		enableContractBtn();
	}

	public void enableContractBtn() {
		p_disConBtn = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:contrBtn");
		reqCtx.update("form:contrXBtn");
	}

	public void disableContractBtn() {
		p_disConBtn = true;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:contrBtn");
		reqCtx.update("form:contrXBtn");
	}

	public void prepareStaffDlg() {
		switch (p_service.getCategory()) {
		case 1: {
			p_search_position_id = 16L;
			break;
		}
		case 2: {
			if (p_service.getServ_type() == 1)
				p_search_position_id = 13L;
			else
				p_search_position_id = 17L;
			break;
		}
		default: {
			break;
		}
		}
		to_search_staff();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("StaffListForm");
	}

	// *********************** STAFF ***************************************

	public void to_search_staff() {
		try {
			Salary p_salary = new Salary();
			p_salary.setBukrs(p_service.getBukrs());
			p_salary.setBranch_id(searchStaff.getBranch_id());
			p_salary.setPosition_id((long) p_search_position_id);

			StaffService staffService = (StaffService) appContext.getContext()
					.getBean("staffService");
			p_staff_list = staffService.dynamicSearchStaffSalary(searchStaff,
					p_salary);

			if (p_staff_list.size() == 0) {
				p_staff_list = new ArrayList<Staff>();
				GeneralUtil.addInfoMessage("Сотрудников не найдено!");
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("StaffListForm:staffTable");

		} catch (DAOException ex) {
			p_staff_list = new ArrayList<Staff>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("StaffListForm:staffTable");
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

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

	public Staff searchStaff;

	public Staff getSearchStaff() {
		return searchStaff;
	}

	public void setSearchStaff(Staff searchStaff) {
		this.searchStaff = searchStaff;
	}

	// ***********************************************************************

	public void assignFoundEmployee() {
		String fio = "";
		if (selectedStaff != null && selectedStaff.getStaff_id() != null) {
			fio = selectedStaff.getFullFIO();

			System.out.println("Selected Service Category: "
					+ p_service.getCategory());
			System.out.println("Selected Staff Position: "
					+ selectedStaff.getPosition_id());

			if (isMaster) {
				p_service.setMaster_id(selectedStaff.getStaff_id());
				p_master = Clone.cloneStaff(selectedStaff);
				clearPriceTable();
				clearServPosListTable();
				disable_save_button();
				loadMatnrPriceList();
				loadWerksMatnrList();

				if (p_service.getServ_type() == ServiceTable.TYPE_FILTERS
						|| p_service.getServ_type() == ServiceTable.TYPE_SERVICE)
					addRow();
				
				// Problemniy bonus nachislaetsa s Aprilya 2017
				Date april2017 = new Date();
				april2017.setMonth(3);
				april2017.setYear(117);
				april2017.setDate(1);
				
//				SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
//				System.out.println("April: " + f.format(april2017));				
//				if ((new Date()).before(april2017)) {
//					System.out.println("Today is before April 1 of 2017.");
//				}
				
				if (!Validation.isEmptyLong(selectedStaff.getPosition_id()) 
						&& selectedStaff.getPosition_id() == Staff.POS_MASTER_FILTER_OVERDUE
						&& (!(new Date()).before(april2017))) {
					isOverdueMaster = true;					
					loadServFilter();
				}
				
			} else {
				p_service.setOper_id(selectedStaff.getStaff_id());
				p_oper = Clone.cloneStaff(selectedStaff);
			}
		}

		selectedStaff = new Staff();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:stuff_master");
		reqCtx.update("form:stuff_oper");
	}

	public void clearServPosListTable() {
		servPosListTable.clear();
		sum_dmbtr = 0;
		sum_wrbtr = 0;
		p_service.setPayment_due(0);
		p_service.setMaster_premi(0);
		p_service.setOper_premi(0);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public boolean isMaster = false;
	
	// *******************************************OverDue_Master_bonus_***********************************************
		private boolean isOverdueMaster = false;
		private boolean isAccrual = false;
		private static final int minOverdue = 6; 

		private void loadServFilter() {
			ServFilterService sfs = (ServFilterService) appContext.getContext().getBean("servFilterService");		
			ServFilter sf = sfs.getSFByContractId(p_service.getContract_id());
//			ServFilterOutput servFilterOutput = new ServFilterOutput(1);
			
			if (sf != null && !Validation.isEmptyLong(sf.getId())) {
//				BeanUtils.copyProperties(sf, servFilterOutput);
				
				int f1 = GeneralUtil.calcAgeinMonths(new Date(), sf.getF1_date_next());
				int f2 = GeneralUtil.calcAgeinMonths(new Date(), sf.getF2_date_next());
				int f3 = GeneralUtil.calcAgeinMonths(new Date(), sf.getF3_date_next());
				int f4 = GeneralUtil.calcAgeinMonths(new Date(), sf.getF4_date_next());
				int f5 = GeneralUtil.calcAgeinMonths(new Date(), sf.getF5_date_next());
				
				System.out.println("F1 = " + f1);
				System.out.println("F2 = " + f2);
				System.out.println("F3 = " + f3);
				System.out.println("F4 = " + f4);
				System.out.println("F5 = " + f5);			
				
				if (f1 >= minOverdue && f2 >= minOverdue && f3 >= minOverdue && f4 >= minOverdue && f5 >= minOverdue) {
					isAccrual = true;
					System.out.println("All filters overdue at least: " + minOverdue + " months.");
				}
			}
		}
		
		private void assignOverdueMasterBonus() {
			List<Bonus> bonusList = new ArrayList<Bonus>();
			Bonus bonusMaster = new Bonus();
			BonusDao bDao = (BonusDao) appContext.getContext().getBean(
					"bonusDao");
			
			String wcl = " bukrs = '" + p_service.getBukrs()
			+ "' and bonus_type_id = 2";
			
			wcl += " and category_id = " + Bonus.BONUS_CAT_FROM_SELL;
			
			wcl += " and position_id = "
					+ Staff.POS_MASTER_FILTER_OVERDUE + " and country_id = "
					+ p_branch.getCountry_id()
					+ " and business_area_id = "
					+ p_branch.getBusiness_area_id();
			wcl += " and from_num <= " + p_service.getPayment_due() + " and to_num >= " + p_service.getPayment_due();
			
			System.out.println("Master Pos ID: " + p_master.getPosition_id());
			bonusList = bDao.dynamicFindBonuses(wcl);
			System.out.println("Blist size = " + bonusList.size());
			
			if (bonusList.size() > 0) {
				bonusMaster = bonusList.get(0);
				p_service.setMaster_premi(p_service.getMaster_premi() + bonusMaster.getCoef());
				System.out.println("Bonus OverdueMaster: "
						+ bonusMaster.getCoef() + " "
						+ bonusMaster.getWaers());
			}
		}

	public void setSearchPositionId(Long a_pos_id) {
		if (a_pos_id != null) {
			if (p_service.getServ_type() == 1)
				p_search_position_id = a_pos_id;
			else if (p_service.getServ_type() > 1) {
				if (p_service.getCategory() == ContractType.TOVARCAT_VACUUM_CLEANER) {
					if (a_pos_id == 13)
						p_search_position_id = 16L;
					else if (a_pos_id == 18)
						p_search_position_id = 19L;
				} else if (p_service.getCategory() == ContractType.TOVARCAT_WATER_FILTER)
					p_search_position_id = 17L;
			}
			if (a_pos_id == 13)
				isMaster = true;
			else
				isMaster = false;
		}

		searchStaff.setBranch_id(p_service.getBranch_id());
		p_staff_list = new ArrayList<Staff>();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("StaffListForm:staffTable");
		reqCtx.update("StaffListForm:se_pos");
		reqCtx.update("StaffListForm");
	}

	public void clearStaffField(Long a_pos) {
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		if (a_pos == 11) {
			p_master = null;
			p_service.setMaster_id(null);
			clearPriceTable();
			clearServPosListTable();
			disable_save_button();
			reqCtx.update("form");
		} else if (a_pos == 18) {
			p_oper = null;
			p_service.setOper_id(null);
			reqCtx.update("form:stuff_oper");
		}
	}

	public void prepareContractSelect() {
		p_search_contract.setBukrs(p_service.getBukrs());
		// p_search_contract.setBranch_id(p_service.getBranch_id());
		// loadDealerList();
	}

	// ****************************************************************************

	public Long p_search_position_id;

	public Long getP_search_position_id() {
		return p_search_position_id;
	}

	public void setP_search_position_id(Long p_search_position_id) {
		this.p_search_position_id = p_search_position_id;
	}

	public Branch p_branch;

	public Branch getP_branch() {
		return p_branch;
	}

	public void setP_branch(Branch p_branch) {
		this.p_branch = p_branch;
	}

	public List<ServPosListTable> servPosListTable;	

	public List<Branch> loadBranchList(String a_bukr) {
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(a_bukr));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
		bf.addFilter(new BranchBusinessAreaFilter(BusinessArea.AREA_SERVICE));
		return bf.filterBranch(p_branchF4Bean.getBranch_list());
	}

	// ***************************************************************************************
	// ***************************MatnrF4*****************************************************

	@ManagedProperty(value = "#{matnrF4Bean}")
	private MatnrF4 p_matnrF4Bean;

	public MatnrF4 getP_matnrF4Bean() {
		return p_matnrF4Bean;
	}

	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
		this.p_matnrF4Bean = p_matnrF4Bean;
	}

	public List<MatnrPriceList> p_matnr_list = new ArrayList<MatnrPriceList>();

	public List<MatnrPriceList> getP_matnr_list() {
		return p_matnr_list;
	}

	public void setP_matnr_list(List<MatnrPriceList> p_matnr_list) {
		this.p_matnr_list = p_matnr_list;
	}

	private MatnrPriceList selectedMatnr = new MatnrPriceList();

	public MatnrPriceList getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(MatnrPriceList selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	public boolean dis_selectCustomer;

	public boolean isDis_selectCustomer() {
		return dis_selectCustomer;
	}

	public void setDis_selectCustomer(boolean dis_selectCustomer) {
		this.dis_selectCustomer = dis_selectCustomer;
	}

	public boolean dis_selectMatnr;

	public boolean isDis_selectMatnr() {
		return dis_selectMatnr;
	}

	public void setDis_selectMatnr(boolean dis_selectMatnr) {
		this.dis_selectMatnr = dis_selectMatnr;
	}

	public void calcRow(int index) {
		if (index > 0) {
			int pos = index - 1;
			int sumRow = 0;

			ServicePos sp2 = servPosListTable.get(pos).getServPos();

			if (sp2 != null && sp2.getQuantity() != null
					&& !servPosListTable.get(pos).isWarranty()) {
				servPosListTable.get(pos).setSum_sc(
						sp2.getQuantity() * sp2.getMatnr_price());
				servPosListTable.get(pos).setSum(
						servPosListTable.get(pos).getSum_sc() / p_currate);
			}

			if (p_service.getServ_type() != 1) {
				sum_dmbtr = 0;
				sum_wrbtr = 0;
				for (int i = 0; i < servPosListTable.size(); i++) {
					sum_dmbtr += servPosListTable.get(i).getSum();
					sum_wrbtr += servPosListTable.get(i).getSum_sc();
					sumRow += 1;
				}
			} else {
				sum_wrbtr = p_service.getSummTotal();
				sum_dmbtr = sum_wrbtr / p_currate;
			}

			p_service.setSummTotal(sum_wrbtr);

			// System.out.println("Pos: " + pos);
			// System.out.println("SummaryRow: " + sumRow);

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
			reqCtx.update("form:olTable:" + pos + ":b_dmbtr");
			reqCtx.update("form:olTable:" + pos + ":b_wrbtr");
			reqCtx.update("form:olTable:" + (sumRow - 1) + ":summaryMC");
			reqCtx.update("form:olTable:" + (sumRow - 1) + ":summarySC");
		}
	}

	public double sum_dmbtr;
	public double sum_wrbtr;

	public double getSum_dmbtr() {
		return sum_dmbtr;
	}

	public void setSum_dmbtr(double sum_dmbtr) {
		this.sum_dmbtr = sum_dmbtr;
	}

	public double getSum_wrbtr() {
		return sum_wrbtr;
	}

	public void setSum_wrbtr(double sum_wrbtr) {
		this.sum_wrbtr = sum_wrbtr;
	}

	public String p_mcRatetext;

	public String getP_mcRatetext() {
		return p_mcRatetext;
	}

	public ServPosListTable p_spTableRow;

	public ServPosListTable getP_spTableRow() {
		return p_spTableRow;
	}

	public void setP_spTableRow(ServPosListTable p_spTableRow) {
		this.p_spTableRow = p_spTableRow;
	}

	public void updateMCRateText() {
		// p_main_currency = p_service.getCurrency();
		p_currency = getCurrencyName(userData.getBranch_id());
		p_currate = 0;
		p_currate = getCurrencyRate(p_service.getCurrency(), p_currency);

		p_mcRatetext = " 1 USD = " + p_currate + " " + p_currency;

		System.out.println("Rate is: " + p_mcRatetext);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:mainCurRateToLocalCurrency");
	}

	// *****************************************************************************************************

	public ServPosListTable addRow() {
		servPosListTable.add(new ServPosListTable(servPosListTable.size() + 1,
				p_bukrs, p_main_currency));
		// servPosListTable.get(servPosListTable.size()-1).getServPos().setOperation(1L);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");

		disable_save_button();
		return servPosListTable.get(servPosListTable.size() - 1);
	}

	public void deleteTableRow(int i) {
		int j = i - 1;
		ServPosListTable spl = servPosListTable.get(j);

		if (spl != null) {
			MatnrPriceList mpl = mplMap.get(spl.getServPos().getMatnr_id());
			Long q = spl.getServPos().getQuantity();
			if (q > 0
					&& !Validation.isEmptyLong(spl.getServPos().getMatnr_id())
					&& mpl != null) {
				mpl.setqMinus(mpl.getqMinus() - q);
				mpl.setMenge(mpl.getqInit() - mpl.getqMinus());
				refreshMatnrList();
			}
		}

		servPosListTable.remove(j);
		while (j < servPosListTable.size()) {
			servPosListTable.get(j).setIndex(j + 1);
			j++;
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");
		disable_save_button();
	}

	public void removeEmptyRows() {
		addRow();

		for (int i = 0; i < servPosListTable.size(); i++) {
			// System.out.println("Inspecting the Row: " + i);
			if (servPosListTable.get(i) != null
					&& servPosListTable.get(i).isWarranty())
				servPosListTable.get(i).getServPos().setWarranty(1);
			else
				servPosListTable.get(i).getServPos().setWarranty(0);

			if (servPosListTable.get(i).getMatnr() == null
					|| servPosListTable.get(i).getMatnr().getMatnr() == null) {
				if ((servPosListTable.get(i).getServPos().getOperation() != null)
						&& ((servPosListTable.get(i).getServPos()
								.getOperation() == 1) || (servPosListTable
								.get(i).getServPos().getOperation() == 2 && servPosListTable
								.get(i).getServPos().getInfo2().isEmpty()))) {
					deleteTableRow(i + 1);
					// System.out.println("Deleting the row: " + i);
					i--;
				}
			}
		}
		//calcRow(servPosListTable.size());
		addPremis();
		calcRow(servPosListTable.size());
		if ((p_service.getServ_type() == 2)
				|| (servPosListTable.size() > 0 && (p_service.getServ_type() == 1
						|| p_service.getServ_type() == 3 || p_service
						.getServ_type() == 4)))
			enable_save_button();
		// calcRow(servPosListTable.size());
		validateServPos();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public void validateServPos() {
		try {
			int f[] = new int[6];
			f[1] = 0;
			f[2] = 0;
			f[3] = 0;
			f[4] = 0;
			f[5] = 0;	
			
			for (ServPosListTable splt : servPosListTable) {
				if (splt.getServPos().getMatnr_price() < 0) {
					double p = splt.getServPos().getMatnr_price();
					splt.getServPos().setMatnr_price(0);
					calcRow(splt.getIndex());
					throw new DAOException(
							"Unit price cannot be a negative number! Price: "
									+ p + " is changed to 0!");
				}

				if (Validation.isEmptyLong(splt.getServPos().getQuantity())
						|| splt.getServPos().getQuantity() < 0) {
					splt.getServPos().setQuantity(1L);
					calcRow(splt.getIndex());
					throw new DAOException(
							"Quantity cannot be equal or less than 0! It is changed to 1!");
				}
				
				if (p_service.getServ_type() == ServiceTable.TYPE_FILTERS
						&& splt.getServPos().getOperation() == ServicePos.OPER_SELL) {
					if (splt.getServPos().getFno() > 0 && splt.getServPos().getFno() <= ServicePos.MAX_FNO) {
						f[splt.getServPos().getFno()]++;
						if (f[splt.getServPos().getFno()] > 1) 
							throw new DAOException(
									"Одинаковые проядковые номера FNO = " + splt.getServPos().getFno());
					} else {
						throw new DAOException("FNO is out of boundary!");
					}
				}
			}

			if (spChanged(spl_old, extractSPFromSPLT(servPosListTable)))
				doCreateON();
		} catch (DAOException ex) {
			disable_save_button();
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public boolean spChanged(List<ServicePos> spL1, List<ServicePos> spL2) {
		if (p_service.getServ_status() == ServiceTable.STATUS_CANCELLED) {
			return false;
		}
		
		boolean res = false;
		if (spL1.size() != spL2.size())
			return true;
		for (int i = 0; i < spL1.size(); i++) {
			if (spL1.get(i).hashCode() != spL2.get(i).hashCode())
				return true;
		}
		return res;
	}
	
	public void disable_save_button() {
		p_disabled_save_button = true;
		p_check_text = "Пожалуйста проверьте данные перед сохранением!";
		p_check_text_color = "noteRegular";
		p_check_icon = "ui-icon ui-icon-refresh";
		p_check_payments = false;

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:check_text");
		reqCtx.update("form:save_button");
		reqCtx.update("form:check_button");
	}

	public void enable_save_button() {
		p_disabled_save_button = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:save_button");
	}

	public void calcAll() {
		for (int i = 1; i <= servPosListTable.size(); i++)
			calcRow(i);
	}

	public void addPremis() {
		// COUNT PREMIS OF STUFF
		calcAll();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		// reqCtx.update("form:olTable:" + (splt.getIndex()-1));
		reqCtx.update("form");

		try {
			List<Bonus> bonusList = new ArrayList<Bonus>();
			Bonus bonusMaster = new Bonus();
			BonusDao bDao = (BonusDao) appContext.getContext().getBean(
					"bonusDao");

			if (p_service.getServ_type() == 2) {
				// FITTING

				System.out.println("Checking for Master Premi");
				if (p_master != null && p_master.getStaff_id() > 0) {
					System.out.println("Master is not NULL: "
							+ p_master.getStaff_id());

					String wcl = " bukrs = '" + p_service.getBukrs()
							+ "' and bonus_type_id = 1 and position_id = "
							+ Staff.POS_MASTER_WAT + " and country_id = "
							+ p_branch.getCountry_id()
							+ " and business_area_id = "
							+ p_branch.getBusiness_area_id();

					System.out.println("Master Pos ID: "
							+ p_master.getPosition_id());
					bonusList = bDao.dynamicFindBonuses(wcl);
					System.out.println("Blist size = " + bonusList.size());

					if (bonusList.size() > 0) {
						bonusMaster = bonusList.get(0);

						p_service.setMaster_premi(bonusMaster.getCoef());
						p_service.setMaster_currency(bonusMaster.getWaers());

						System.out.println("Premi Master: "
								+ bonusMaster.getCoef() + " "
								+ bonusMaster.getWaers());

						reqCtx.update("form:masterPremi");
						reqCtx.update("form:masterCurrency");

					}
				} else {
					System.out.println("Master is NULL!");
					throw new DAOException(
							"Master is empty! Please select the Master first!");
				}
			}

			else if (p_service.getServ_type() == 1) {
				// FILTER CHANGE
				int fc = 0;
				int mc = 0;
				for (ServPosListTable splt : servPosListTable) {
					if (!splt.isWarranty()) {
						if (splt.getMatnr().getIs_m() > 0)
							mc++;
						else
							fc++;
					}
				}

				List<ServFilterPremis> sfpl = new ArrayList<ServFilterPremis>();
				ServFilterPremisDao sfpDao = (ServFilterPremisDao) appContext
						.getContext().getBean("servFilterPremisDao");
				sfpl = sfpDao.findAllByBukrsAndCountry(p_service.getBukrs(),
						p_branch.getCountry_id());

				p_service.setMaster_premi(0);
				p_service.setOper_premi(0);
				p_service.setSummTotal(0);
				p_service.setPayment_due(0);
				p_service.setDiscount(0);
				
				for (ServFilterPremis sfp : sfpl) {
					if (sfp.getFc() == fc && sfp.getMc() == mc) {
						p_service.setMaster_premi(sfp.getMaster());
						p_service.setMaster_currency(sfp.getWaers());
						p_service.setOper_premi(sfp.getOperator());
						p_service.setOper_currency(sfp.getWaers());
						p_service.setSummTotal(sfp.getTotal());
						p_service.setDiscount(0);
						double pdue = p_service.getSummTotal()
								- p_service.getDiscount();
						p_service.setPayment_due(pdue);
						p_service.setCurrency(sfp.getWaers());
						break;
					}
				}

				ServPosListTable splt = addRow();
				splt.servPos.setOperation(2L);
				splt.servPos.setInfo1("Общая стоимость услуги");
				splt.servPos.setMatnr_price(p_service.getPayment_due());
				splt.setSum_sc(p_service.getPayment_due());
				splt.getServPos().setSumm(splt.getSum_sc());
				splt.setDis_mat(true);
				splt.setDis_pr(true);
				splt.setDis_qq(true);

				System.out.println("Master premi: " + p_service.getMaster_premi());
				
				if (isAccrual) {
					assignOverdueMasterBonus();
				}
				
				// reqCtx.update("form:olTable:" + (splt.getIndex()-1));
				reqCtx.update("form:olTable");
				reqCtx.update("form:paymentDue");
				reqCtx.update("form:masterPremi");
				reqCtx.update("form:masterCurrency");
				reqCtx.update("form:operPremi");
				reqCtx.update("form:operCurrency");
			}

			else if (p_service.getServ_type() == 3) {
				// NORMAL SERVICE
				double premiMaster = 0;
				double summMatnr = 0;
				double summTotal = 0;
				double summAccessories = 0;
				double summDiscount = 0;
				int qAccerssories = 0;
				double summWork = 0;
				for (ServPosListTable wa_splt : servPosListTable) {
					if (wa_splt.getServPos().getMatnr_id() != null
							&& wa_splt.getServPos().getMatnr_id() > 0) {
						if (!Validation.isEmptyLong(wa_splt.getServPos()
								.getServ_type())
								&& wa_splt.getServPos().getServ_type() == Matnr.CAT_AC) {
							summAccessories += wa_splt.getSum_sc();
							qAccerssories++;
							System.out.println("Yes");

						} else
							summMatnr += wa_splt.getSum_sc();
					} else if (wa_splt.getServPos().getOperation().intValue() == 2) {
						summWork += wa_splt.getSum_sc();
					}
				}

				if (p_master != null && p_master.getStaff_id() != null
						&& p_master.getStaff_id() > 0) {
					bonusMaster = new Bonus();
					int pos = 0;
					if (p_service.getCategory() == 1)
						pos = Staff.POS_MASTER_VAC;
					else
						pos = Staff.POS_MASTER_WAT;

					// Load from Bonus table for OT PRODAJI
					String wcl = " bukrs = '" + p_service.getBukrs()
							+ "' and bonus_type_id = 2 and position_id = "
							+ pos + " and country_id = "
							+ p_branch.getCountry_id()
							+ " and business_area_id = 5 and category_id = 1";
					bonusList = bDao.dynamicFindBonuses(wcl);
					if (bonusList.size() > 0) {
						bonusMaster = bonusList.get(0);
						System.out.println("Master bonus Coef for Sell: "
								+ bonusMaster.getCoef());
						premiMaster = summMatnr * bonusMaster.getCoef();
					} else {
						throw new DAOException("Bonus is not set for Bukrs: "
								+ p_service.getBukrs()
								+ " Business_area_id: 5 PositionID: "
								+ p_master.getPosition_id()
								+ " Bonus_Category_ID: 1");
					}

					// Load from Discount for ACCESSORIES
					wcl = " bukrs = '" + p_service.getBukrs()
							+ "' and bonus_type_id = 8 " + " and country_id = "
							+ p_branch.getCountry_id()
							+ " and business_area_id = 5 and category_id = 3"
							+ " and from_num <= " + qAccerssories
							+ " and to_num >= " + qAccerssories;
					bonusList = bDao.dynamicFindBonuses(wcl);
					if (bonusList.size() > 0) {
						Bonus bonus = bonusList.get(0);
						System.out.println("Discount for Accessories: "
								+ bonus.getCoef());
						summDiscount += summAccessories * bonus.getCoef();
					}
					
					// Load from Bonus table for OT ACCESSORIES
					wcl = " bukrs = '" + p_service.getBukrs()
							+ "' and bonus_type_id = 2 " + " and country_id = "
							+ p_branch.getCountry_id()
							+ " and business_area_id = 5 and category_id = 3";
					bonusList = bDao.dynamicFindBonuses(wcl);
					if (bonusList.size() > 0) {
						bonusMaster = bonusList.get(0);
						System.out
								.println("Master bonus Coef for Accessories: "
										+ bonusMaster.getCoef());
						premiMaster += (summAccessories-summDiscount) * bonusMaster.getCoef();
					} else {
						throw new DAOException(
								"Bonus is not set for Bukrs: "
										+ p_service.getBukrs()
										+ " Business_area_id: 5 Bonus_Category_ID: Accessory (3)");
					}

					

					// Load from Bonus table for OT RABOTY
					bonusMaster = new Bonus();
					wcl = " bukrs = '" + p_service.getBukrs()
							+ "' and bonus_type_id = 2 and position_id = "
							+ pos + " and country_id = "
							+ p_branch.getCountry_id()
							+ " and business_area_id = 5 and category_id = 2";
					bDao = (BonusDao) appContext.getContext().getBean(
							"bonusDao");
					bonusList = bDao.dynamicFindBonuses(wcl);
					if (bonusList.size() > 0) {
						bonusMaster = bonusList.get(0);
						System.out.println("Master bonus Coef for Work: "
								+ bonusMaster.getCoef());
						premiMaster += summWork * bonusMaster.getCoef();
					} else {
						throw new DAOException("Bonus is not set for Bukrs: "
								+ p_service.getBukrs()
								+ " Business_area_id: 5 PositionID: "
								+ p_master.getPosition_id()
								+ " Bonus_Category_ID: 2");
					}
				}

				summTotal = summMatnr + summWork + summAccessories
						- summDiscount;
				System.out.println("Accessories selected: " + qAccerssories);
				System.out.println("Accessories summ: " + summAccessories);
				p_service.setSummTotal(sum_wrbtr);
				p_service.setDiscount(summDiscount);
				p_service.setPayment_due(summTotal);
				p_service.setCurrency(p_currency);

				p_service.setMaster_premi(premiMaster);
				p_service.setMaster_currency(p_service.getCurrency());

				reqCtx.update("form:paymentDue");
				reqCtx.update("form:masterPremi");
				reqCtx.update("form:masterCurrency");
			}

			else if (p_service.getServ_type() == 4) {
				// PROPHYLAXIS

			}
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	// ************************************************************************
	// ************************************************************************

	public ServiceTable p_service;
	public ServiceTable p_service_old;
	public String p_main_currency;
	public String p_currency;
	public double p_currate;
	public Customer p_customer;
	public Contract p_contract;
	public String p_contr_output;
	public Staff p_master;
	public String p_master_pos;
	public Staff p_oper;
	public String p_oper_pos;
	public String p_tovar_name;

	public String getP_tovar_name() {
		return p_tovar_name;
	}

	public void setP_tovar_name(String p_tovar_name) {
		this.p_tovar_name = p_tovar_name;
	}

	public String getP_contr_output() {
		return p_contr_output;
	}

	public void setP_contr_output(String p_contr_output) {
		this.p_contr_output = p_contr_output;
	}

	public Staff getP_master() {
		return p_master;
	}

	public void setP_master(Staff p_master) {
		this.p_master = p_master;
	}

	public Staff getP_oper() {
		return p_oper;
	}

	public void setP_oper(Staff p_oper) {
		this.p_oper = p_oper;
	}

	public String getP_master_pos() {
		return p_master_pos;
	}

	public void setP_master_pos(String p_master_pos) {
		this.p_master_pos = p_master_pos;
	}

	public String getP_oper_pos() {
		return p_oper_pos;
	}

	public void setP_oper_pos(String p_oper_pos) {
		this.p_oper_pos = p_oper_pos;
	}

	public Customer getP_customer() {
		return p_customer;
	}

	public void setP_customer(Customer p_customer) {
		this.p_customer = p_customer;
	}

	public Contract getP_contract() {
		return p_contract;
	}

	public void setP_contract(Contract p_contract) {
		this.p_contract = p_contract;
	}

	public List<ServPosListTable> getServPosListTable() {
		return servPosListTable;
	}

	public void setServPosListTable(List<ServPosListTable> servPosListTable) {
		this.servPosListTable = servPosListTable;
	}

	public ServiceTable getP_service() {
		return p_service;
	}

	public void setP_service(ServiceTable p_service) {
		this.p_service = p_service;
	}

	public String getP_main_currency() {
		return p_main_currency;
	}

	public void setP_main_currency(String p_main_currency) {
		this.p_main_currency = p_main_currency;
	}

	public String getP_currency() {
		return p_currency;
	}

	public void setP_currency(String p_currency) {
		this.p_currency = p_currency;
	}

	public double getP_currate() {
		return p_currate;
	}

	public void setP_currate(double p_currate) {
		this.p_currate = p_currate;
	}

	public void setP_mcRatetext(String p_mcRatetext) {
		this.p_mcRatetext = p_mcRatetext;
	}

	public void clearService() {
		p_service = new ServiceTable();
		p_service_old = new ServiceTable();
		p_customer = new Customer();
		p_master = new Staff();
		p_oper = new Staff();
		p_contract = new Contract();
		p_contr_output = "";
		p_tovar_name = "";

		docancel = false;
		docreate = false;

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	// ***********************************************************************

	public void loadCategories() {
		st_l = new ArrayList<Service02.ServType_Loc>();
		ServType_Loc st = new ServType_Loc("Сервисное обслуживание",
				ServiceTable.TYPE_SERVICE);
		st_l.add(st);
		st = new ServType_Loc("Замена фильтров", ServiceTable.TYPE_FILTERS);
		st_l.add(st);
		st = new ServType_Loc("Установка", ServiceTable.TYPE_FITTING);
		st_l.add(st);

		Comparator<ServType_Loc> c = new Comparator<Service02.ServType_Loc>() {
			@Override
			public int compare(ServType_Loc o1, ServType_Loc o2) {
				// TODO Auto-generated method stub
				int res = -1;
				if (o1.getV() > o2.getV())
					res = 1;
				return res;
			}
		};
		st_l.sort(c);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:serv_type");
	}

	public List<Service02.ServType_Loc> st_l = new ArrayList<Service02.ServType_Loc>();

	public List<Service02.ServType_Loc> getSt_l() {
		return st_l;
	}

	public class ServType_Loc {
		ServType_Loc(String s, int v) {
			this.n = s;
			this.v = v;
		}

		public String n;
		public int v;

		public String getN() {
			return n;
		}

		public void setN(String n) {
			this.n = n;
		}

		public int getV() {
			return v;
		}

		public void setV(int v) {
			this.v = v;
		}
	}

	public boolean disPayBtn;

	public boolean isDisPayBtn() {
		return disPayBtn;
	}

	public void setDisPayBtn(boolean disPayBtn) {
		this.disPayBtn = disPayBtn;
	}

	public void enableServPacket() {
		disServPacket = false;
		System.out.println("Enabling Service Packets!");
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public void disableServPacket() {
		disServPacket = true;
		System.out.println("Disabling Service Packets!");
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public boolean disServPacket;

	public boolean isDisServPacket() {
		return disServPacket;
	}

	public void setDisServPacket(boolean disServPacket) {
		this.disServPacket = disServPacket;
	}

	// ***************************************************************************

	public List<ServConMatnrWarOutput> spConWarL = new ArrayList<ServConMatnrWarOutput>();

	public List<ServConMatnrWarOutput> getSpConWarL() {
		return spConWarL;
	}

	public void setSpConWarL(List<ServConMatnrWarOutput> spConWarL) {
		this.spConWarL = spConWarL;
	}

	public void loadSpWarDetails(Long servId) {
		if (servId != null && servId > 0) {
			System.out.println("Service Id: " + servId);
			ServConMatnrWarDao spDao = (ServConMatnrWarDao) appContext
					.getContext().getBean("servContrMatnrWar");
			List<ServConMatnrWar> spWars = spDao.findAllByServId(servId);

			int i = 0;
			spConWarL = new ArrayList<ServConMatnrWarOutput>();
			System.out.println("Warranties given: " + spWars.size());
			for (ServConMatnrWar spWar : spWars) {
				i++;
				ServConMatnrWarOutput spWarO = new ServConMatnrWarOutput(i);
				spWarO.setScMW(spWar);
				if (spWar.getMatnr_id() != null && spWar.getMatnr_id() > 0)
					spWarO.setMatnr(p_matnrF4Bean.getL_matnr_map().get(
							spWar.getMatnr_id()));
				spConWarL.add(spWarO);
			}
		}
	}

	public ServPacket spSelected;
	public Long selectedSPId;
	public List<ServPacket> spList;

	public List<ServPacket> getSpList() {
		return spList;
	}

	public void setSpList(List<ServPacket> spList) {
		this.spList = spList;
	}

	public ServPacket getSpSelected() {
		return spSelected;
	}

	public void setSpSelected(ServPacket spSelected) {
		this.spSelected = spSelected;
	}

	public Long getSelectedSPId() {
		return selectedSPId;
	}

	public void setSelectedSPId(Long selectedSPId) {
		this.selectedSPId = selectedSPId;
	}

	// *********************************************************************************

	public Long service_number;

	public Long getService_number() {
		return service_number;
	}

	public void setService_number(Long service_number) {
		this.service_number = service_number;
	}

	ServiceSearch p_service_search = new ServiceSearch();

	public ServiceSearch getP_service_search() {
		return p_service_search;
	}

	public void setP_service_search(ServiceSearch p_service_search) {
		this.p_service_search = p_service_search;
	}

	public boolean disFinBtn = false;

	public boolean p_disConBtn = true;

	public boolean isP_disConBtn() {
		return p_disConBtn;
	}

	public void setP_disConBtn(boolean p_disConBtn) {
		this.p_disConBtn = p_disConBtn;
	}

	public boolean isDisFinBtn() {
		return disFinBtn;
	}

	public void setDisFinBtn(boolean disFinBtn) {
		this.disFinBtn = disFinBtn;
	}

	public String belnr;
	public String gjahr;

	public String getBelnr() {
		return belnr;
	}

	public void setBelnr(String belnr) {
		this.belnr = belnr;
	}

	public String getGjahr() {
		return gjahr;
	}

	public void setGjahr(String gjahr) {
		this.gjahr = gjahr;
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

	// ******************************************************************
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

	public double getCurrencyRate(String main_currency, String sec_currency) {
		double out;
		out = 1;
		ExchangeRate wa_exrM = p_exchangeRateF4Bean.getL_er_map_national().get(
				"1" + main_currency);
		ExchangeRate wa_exrS = p_exchangeRateF4Bean.getL_er_map_national().get(
				"1" + sec_currency);

		if (wa_exrM != null && wa_exrM.getSc_value() > 0 && wa_exrS != null)
			out = wa_exrS.getSc_value() / wa_exrM.getSc_value();

		return out;
	}

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

	// *********************************************************************
	// ***************************ExchangeRateF4*******************************
	@ManagedProperty(value = "#{exchangeRateF4Bean}")
	private ExchangeRateF4 p_exchangeRateF4Bean;

	public ExchangeRateF4 getP_exchangeRateF4Bean() {
		return p_exchangeRateF4Bean;
	}

	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
		this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
	}

	List<ExchangeRate> exchangeRate_list = new ArrayList<ExchangeRate>();

	public List<ExchangeRate> getExchangeRate_list() {
		return exchangeRate_list;
	}

	// ******************************************************************
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

	// ************************************************************************

	public boolean dis_selectServType;

	public boolean isDis_selectServType() {
		return dis_selectServType;
	}

	public void setDis_selectServType(boolean dis_selectServType) {
		this.dis_selectServType = dis_selectServType;
	}

	public boolean p_disabled_save_button;

	public boolean isP_disabled_save_button() {
		return p_disabled_save_button;
	}

	public void setP_disabled_save_button(boolean p_disabled_save_button) {
		this.p_disabled_save_button = p_disabled_save_button;
	}

	public boolean isP_disabled_add_button() {
		return p_disabled_add_button;
	}

	public void setP_disabled_add_button(boolean p_disabled_add_button) {
		this.p_disabled_add_button = p_disabled_add_button;
	}

	public void setSt_l(List<Service02.ServType_Loc> st_l) {
		this.st_l = st_l;
	}

	public boolean p_disabled_add_button;

	public boolean dis_selectFNO = true;
	public boolean dis_selOperType = false;
	public boolean dis_delServPosBtn = false;

	public boolean isDis_selectFNO() {
		return dis_selectFNO;
	}

	public void setDis_selectFNO(boolean dis_selectFNO) {
		this.dis_selectFNO = dis_selectFNO;
	}

	public boolean isDis_selOperType() {
		return dis_selOperType;
	}

	public void setDis_selOperType(boolean dis_selOperType) {
		this.dis_selOperType = dis_selOperType;
	}

	public boolean isDis_delServPosBtn() {
		return dis_delServPosBtn;
	}

	public void setDis_delServPosBtn(boolean dis_delServPosBtn) {
		this.dis_delServPosBtn = dis_delServPosBtn;
	}

	List<OperTypeClass> ot_l = new ArrayList<OperTypeClass>();

	public List<OperTypeClass> getOt_l() {
		return ot_l;
	}

	public void setOt_l(List<OperTypeClass> ot_l) {
		this.ot_l = ot_l;
	}

	public String p_bukrs;

	public String getP_bukrs() {
		return p_bukrs;
	}

	public void setP_bukrs(String p_bukrs) {
		this.p_bukrs = p_bukrs;
	}

	// ******************************************************************

	public void prepareByServType() {
		clearSPMode();
		if (p_service.getServ_type() == 2) {
			p_disabled_save_button = true;
			ot_l.clear();
			servPosListTable.clear();
			p_disabled_add_button = true;
			dis_selectFNO = true;
			disableServPacket();
			dis_selOperType = true;
			dis_delServPosBtn = false;
			dis_selectMatnr = true;
		}

		else if (p_service.getServ_type() == 1) {
			p_disabled_save_button = true;
			p_disabled_add_button = false;
			dis_selectFNO = false;
			disableServPacket();
			servPosListTable.add(new ServPosListTable(1, p_bukrs, p_currency));
			ot_l.clear();
			ot_l.add(new OperTypeClass(1L, "ПРОДАЖА_ЗАП"));
			ot_l.add(new OperTypeClass(2L, "УСЛУГА"));
			// reload matnr list
			dis_selOperType = true;
			dis_delServPosBtn = false;
		}

		else if (p_service.getServ_type() == 3) {
			p_disabled_add_button = false;
			p_disabled_save_button = true;
			dis_selectFNO = true;
			dis_selOperType = false;
			dis_delServPosBtn = false;
			disableServPacket();
			servPosListTable.add(new ServPosListTable(1, p_bukrs, p_currency));

			ot_l.clear();
			ot_l.add(new OperTypeClass(1L, "ПРОДАЖА_ЗАП"));
			ot_l.add(new OperTypeClass(2L, "УСЛУГА"));

			// ...
		}

		else if (p_service.getServ_type() == 4) {
			ot_l.clear();
			p_disabled_add_button = true;
			p_disabled_save_button = true;
			dis_selectFNO = true;
			dis_delServPosBtn = true;
			dis_selOperType = true;
			ot_l.clear();
			ot_l.add(new OperTypeClass(1L, "ПРОДАЖА_ЗАП"));
			ot_l.add(new OperTypeClass(2L, "УСЛУГА"));
			loadSpList();
			enableServPacket();
			System.out.println("Service Type is Service Packet!");
		}

		else {
			ot_l.clear();
			p_disabled_add_button = true;
			p_disabled_save_button = true;
			dis_selectFNO = true;
			dis_delServPosBtn = false;
			disableServPacket();
			System.out.println("Service Type is undefined!");
		}

		updateByServType();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");
		// reqCtx.update("form:olTable");
		reqCtx.update("form:add_button");
		reqCtx.update("form:save_button");

	}

	public void prepareTable() {
		prepareByServType();
		clearStaffField(11L);
		clearStaffField(18L);
	}

	public void clearSPMode() {
		sum_dmbtr = 0;
		sum_wrbtr = 0;
		if (p_service != null)
			p_service.setServ_packet_id(null);
		spWarL = new ArrayList<ServPacketWarOutput>();
		servPosListTable = new ArrayList<ServPosListTable>();
		selectedSPId = 0L;
	}

	public void updateByServType() {
		switch (p_service.getServ_type()) {
		case 1: {
			dis_oper = false;
			break;
		}
		case 4: {
			dis_oper = false;
			break;
		}
		default: {
			dis_oper = true;
			p_service.setOper_id(null);
			p_oper = null;
			break;
		}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:stuff_oper");
		reqCtx.update("form:oper_select_btn");
		reqCtx.update("form:oper_clear_btn");
	}

	public void updateByOperType(int index) {
		int pos = index - 1;
		switch (servPosListTable.get(pos).getServPos().getOperation()
				.intValue()) {
		case 2: {
			servPosListTable.get(pos).getServPos().setMatnr_id(null);
			servPosListTable.get(pos).setMatnr(null);
			servPosListTable.get(pos).setDis_mat(true);
			servPosListTable.get(pos).getServPos().setQuantity(1L);
			servPosListTable.get(pos).setDis_qq(true);
			break;
		}
		default: {
			servPosListTable.get(pos).setDis_mat(false);
			servPosListTable.get(pos).setDis_qq(false);
			break;
		}
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olTable:" + pos + ":mat");
		reqCtx.update("form:olTable:" + pos + ":b_matnr");
		reqCtx.update("form:olTable:" + pos + ":b_menge");
		calcRow(index);
	}

	public void clearServAppField() {
		p_service.setServ_app_num(null);
		selectedSA = new ServiceApplication();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:application");
	}

	public boolean dis_oper = true;

	public boolean isDis_oper() {
		return dis_oper;
	}

	public void setDis_oper(boolean dis_oper) {
		this.dis_oper = dis_oper;
	}

	public ServiceApplication selectedSA;

	public ServiceApplication getSelectedSA() {
		return selectedSA;
	}

	public void setSelectedSA(ServiceApplication selectedSA) {
		this.selectedSA = selectedSA;
	}

	// *********************************************************************************
	public Map<Long, PriceList> pl = new HashMap<Long, PriceList>();

	GeneralUtil gu = new GeneralUtil();

	public void loadMatnrPriceList() {
		loadPriceList();
		p_matnr_list = new ArrayList<MatnrPriceList>();
		p_ml.clear();

		p_ml.addAll(loadMatnrByTovarID(p_service.getTovar_id()));

		for (Matnr wa_matnr : p_ml) {
			if ((wa_matnr.getType() == 2 && p_service.getServ_type() == 3)
					|| (wa_matnr.getType() == 3 && p_service.getServ_type() == 1)) {
				MatnrPriceList mpl = new MatnrPriceList();
				mpl.setMatnr(wa_matnr);

				if (pl.get(wa_matnr.getMatnr()) != null) {
					mpl.setCurrency(pl.get(wa_matnr.getMatnr()).getWaers());
					mpl.setPrice(pl.get(wa_matnr.getMatnr()).getPrice());
					// System.out.println("New price from customer: " +
					// pl.get(wa_matnr.getMatnr()).getPrice() + " " +
					// pl.get(wa_matnr.getMatnr()).getWaers());
				} else {
					mpl.setCurrency(p_currency);
					mpl.setPrice(0);
				}
				p_matnr_list.add(mpl);
			}
		}

		p_mpl = new ArrayList<MatnrPriceList>();
		p_mpl.addAll(p_matnr_list);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("matnrForm:singleMatnr");
		reqCtx.update("form:matnrTableWidget");
	}

	public void loadWerksMatnrList() {
		WerksBranchDao wbDao = (WerksBranchDao) appContext.getContext()
				.getBean("werksBranchDao");
		Branch br = p_branchF4Bean.getL_branch_map().get(
				p_service.getBranch_id());
		List<Werks> wl = wbDao.findAllWerksByBranch(br.getParent_branch_id());

		MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean(
				"matnrListDao");
		List<MatnrList> ml = new ArrayList<MatnrList>();
		List<MatnrList> mlOut = new ArrayList<MatnrList>();
		System.out.println("Werks found: " + wl.size());
		for (Werks w : wl) {
			System.out.println(w.getText45());
			String cond = String
					.format(" bukrs = '%s' AND status IN('%s','%s') and werks = %d and staff_id = %d ",
							p_service.getBukrs(),
							MatnrList.STATUS_ACCOUNTABILITY,
							MatnrList.STATUS_RECEIVED, w.getWerks(),
							p_service.getMaster_id());

			// ml = mlDao.findAllWithStaff(cond);
			ml = mlDao.getGrouppedListForService(cond);
			System.out.println("Found Master matnrs: " + ml.size());
			mlOut.clear();
			if (ml.size() > 0)
				mlOut.addAll(ml);
		}
		Map<Long, Double> mlMap = new HashMap<Long, Double>();
		for (MatnrList mli : mlOut) {
			mlMap.put(mli.getMatnr(), mli.getMenge());
		}
		p_mpl.clear();
		for (MatnrPriceList mpl : p_matnr_list) {
			Double quantity = mlMap.get(mpl.getMatnr().getMatnr());
			if (quantity != null && quantity > 0) {
				mpl.setMenge(quantity);
				mpl.setqInit(quantity);
				mpl.setqMinus(0L);
				p_mpl.add(mpl);
			}
		}
		System.out.println("P_MPL size: " + p_mpl.size());
		p_matnr_list.clear();
		p_matnr_list.addAll(p_mpl);
		generateMapForMPL();
	}

	public void generateMapForMPL() {
		mplMap = new HashMap<Long, MatnrPriceList>();
		for (MatnrPriceList mpl : p_mpl) {
			mplMap.put(mpl.getMatnr().getMatnr(), mpl);
		}
	}

	public void refreshMatnrList() {
		p_matnr_list.clear();
		if (Validation.isEmptyString(matnrSearch.getCode())
				&& Validation.isEmptyString(matnrSearch.getText45())) {
			p_matnr_list.addAll(p_mpl);
		} else {
			// System.out.println(matnrSearch);
			// System.out.println(matnrSearch.getText45());
			for (MatnrPriceList mpl : p_mpl) {
				if (mpl.getMatnr()
						.getText45()
						.toLowerCase()
						.matches(
								"(?i).*"
										+ matnrSearch.getText45().toLowerCase()
										+ ".*")
						&& mpl.getMatnr()
								.getCode()
								.toLowerCase()
								.matches(
										"(?i).*"
												+ matnrSearch.getCode()
														.toLowerCase() + ".*")) {
					p_matnr_list.add(mpl);
				}
			}
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("matnrForm:singleMatnr");
	}

	public void quantityChange(int index) {
		ServPosListTable spl = servPosListTable.get(index - 1);
		if (spl.getServPos().getQuantity() <= 0) {
			GeneralUtil.addInfoMessage("Incorrect value!");
			spl.getServPos().setQuantity(1L);
		}
		calcRow(index);
		if (spl != null && spl.getServPos() != null
				&& !Validation.isEmptyLong(spl.getServPos().getMatnr_id())) {
			if (!Validation.isEmptyLong(spl.getServPos().getQuantity())
					&& spl.getServPos().getQuantity() > 0) {
				MatnrPriceList mpl = mplMap.get(spl.getServPos().getMatnr_id());
				Long q = countMatnrQuantity(spl.getServPos().getMatnr_id());
				if (q > mpl.getqInit()) {
					GeneralUtil.addInfoMessage("Insufficient amount!");
					q = q - spl.getServPos().getQuantity();
					spl.getServPos()
							.setQuantity(Math.round(mpl.getqInit() - q));
					q += spl.getServPos().getQuantity();
				}
				mpl.setqMinus(q);
				mpl.setMenge(mpl.getqInit() - mpl.getqMinus());
				refreshMatnrList();
			}
		}
	}

	public Long countMatnrQuantity(Long matnr) {
		Long q = 0L;
		for (ServPosListTable splt : servPosListTable) {
			if (splt != null && splt.getServPos() != null
					&& !Validation.isEmptyLong(splt.getServPos().getMatnr_id())
					&& splt.getServPos().getMatnr_id().equals(matnr)) {
				if (!Validation.isEmptyLong(splt.getServPos().getQuantity())
						&& splt.getServPos().getQuantity() > 0) {
					q += splt.getServPos().getQuantity();
				}
			}
		}
		return q;
	}

	private Map<Long, MatnrPriceList> mplMap = new HashMap<Long, MatnrPriceList>();

	public List<Matnr> loadMatnrByTovarID(Long tovID) {
		List<Matnr> lm = new ArrayList<Matnr>();
		MatnrSparePartDao mspDao = (MatnrSparePartDao) appContext.getContext()
				.getBean("matnrSparePartDao");
		List<MatnrSparePart> msp_l = mspDao.findAllByTovarID(tovID);
		System.out.println("Found Spare Parts for " + tovID + " total: "
				+ msp_l.size());
		p_matnrF4Bean.init();
		for (MatnrSparePart msp : msp_l) {
			lm.add(p_matnrF4Bean.getL_matnr_map().get(msp.getSparepart_id()));
		}
		return lm;
	}

	public List<Matnr> p_ml = new ArrayList<Matnr>();

	public List<Matnr> getP_ml() {
		return p_ml;
	}

	public void setP_ml(List<Matnr> p_ml) {
		this.p_ml = p_ml;
	}

	public List<MatnrPriceList> getP_mpl() {
		return p_mpl;
	}

	public void setP_mpl(List<MatnrPriceList> p_mpl) {
		this.p_mpl = p_mpl;
	}

	public List<MatnrPriceList> p_mpl = new ArrayList<MatnrPriceList>();

	public void loadBranch() {
		System.out.println("Bukrs: " + p_service.getBukrs());
		p_bukrs = p_service.getBukrs();

		dis_selectCustomer = false; // selectCustomer
		branch_list = new ArrayList<Branch>();
		priceList_list = new ArrayList<PriceList>();
		// p_service.setBranch_id(null);

		initContractModel();
		contractModel.searchModel.setBukrs(p_service.getBukrs());
		branch_list = loadBranchList(p_service.getBukrs());
		staffBranch_list = loadBranchList(p_service.getBukrs());
		loadContrBranch();
		loadContractModel();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
		reqCtx.update("form:branch-supplier");
		reqCtx.update("form:selectCustomer");
		clearPriceTable();
	}

	List<Branch> staffBranch_list = new ArrayList<Branch>();

	public List<Branch> getStaffBranch_list() {
		return staffBranch_list;
	}

	public void setStaffBranch_list(List<Branch> staffBranch_list) {
		this.staffBranch_list = staffBranch_list;
	}

	// **********************************CONTRACT_SEARCH_FORM**********************************************

	// **********************************CONTRACT_SEARCH_FORM**********************************************

	public void searchContract() {
		try {
			loadContractModel();
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Info", ex.getMessage());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm:outputTable");
	}

	private void loadContractModel() {
		// String cond = " ";
		contractModel.searchModel.setMarkedType(Contract.MT_ALL);
		contractModel.searchModel.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
		
		String cond = " and tovar_serial is not null and contract_status_id <> 3";
		contractModel.setCondition(cond);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		if (contractModel.getSearchModel().getContract_number() != null
				&& contractModel.getSearchModel().getContract_number() > 0) {
			reqCtx.update("ContractListForm:bukrs");
			reqCtx.update("ContractListForm:branch");
			reqCtx.update("ContractListForm:dealer");
			reqCtx.update("ContractListForm:fio");
			reqCtx.update("ContractListForm:tovarSN");
		} else {
			contractModel.searchModel.setContract_number(null);
			reqCtx.update("ContractListForm");
		}

		// initContractModel();

	}

	public void initContractModel() {
		contractModel = new ContractModel((ContractDao) appContext.getContext()
				.getBean("contractDao"));
		contractModel.searchModel.setMarkedType(Contract.MT_ALL);
		contractModel.searchModel.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
	}

	public void assignSelectedContrId() {
		try {
			if (selectedContr != null && selectedContr.getContract() != null
					&& selectedContr.getContract().getContract_id() != null
					&& selectedContr.getContract().getContract_id() > 0) {

				p_contract = new Contract();
				p_contract = selectedContr.getContract().clone();
				p_customer = Clone.cloneCustomer(selectedContr.getCustomer());

				Branch br = p_branchF4Bean.getL_branch_map().get(
						p_contract.getBranch_id());
				p_service.setContract_info(br.getText45() + " - SN:"
						+ selectedContr.getContract().getContract_number());

				ct = p_contractTypeF4Bean.getCt_map().get(
						p_contract.getContract_type_id());
				Matnr tovar = p_matnrF4Bean.getL_matnr_map().get(ct.getMatnr());

				p_service.setTovar_id(tovar.getMatnr());
				p_tovar_name = tovar.getText45();
				p_service.setTovar_sn(p_contract.getTovar_serial());
				p_service.setContract_id(p_contract.getContract_id());

				AddressDao addrDao = (AddressDao) appContext.getContext()
						.getBean("addressDao");
				Address addr = addrDao.find(selectedContr.getContract()
						.getAddrServiceId());
				p_service.setAddr(addr.getAddress());
				p_service.setTel(addr.getTelDom() + " " + addr.getTelMob1()
						+ " " + addr.getTelMob2());

				p_service.setCustomer_firstname(selectedContr.getCustomer()
						.getFirstname());
				p_service.setCustomer_lastname(selectedContr.getCustomer()
						.getLastname());
				p_service.setCustomer_midname(selectedContr.getCustomer()
						.getMiddlename());

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
				GeneralUtil.hideDialog("ContractWidget");
				enableSelectServType();
			} else {
				GeneralUtil.addMessage("Empty contract!",
						"No contract has been selected!");
			}
		} catch (Exception ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void enableSelectServType() {
		dis_selectServType = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:serv_type");
	}

	public void disableSelectServType() {
		dis_selectServType = true;
		p_service.setServ_type(0);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:serv_type");
		prepareTable();
	}

	public void clearContractField() {
		selectedContr = new ContractDetails();
		p_contract = new Contract();
		p_service.setContract_info(" - ");
		p_service.setContract_id(null);
		p_tovar_name = "";
		p_service.setTovar_id(null);
		p_service.setTovar_sn(null);
		p_customer = new Customer();
		p_service.setAddr(null);
		p_service.setTel(null);
		p_service.setPaid(0);
		p_service.setPayment_due(0);
		p_service.setMaster_premi(0);
		p_service.setMaster_currency("");
		p_service.setOper_premi(0);
		p_service.setDiscount(0);
		p_service.setOper_currency("");
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
		disableSelectServType();
		disable_save_button();
	}

	// **************************************************************************

	List<Branch> cbranch_list = new ArrayList<Branch>();

	public List<Branch> getCbranch_list() {
		return cbranch_list;
	}

	public void loadContrBranch() {
		cbranch_list = new ArrayList<Branch>();
		if (contractModel.searchModel.getBukrs() != null) {
			BranchMatchAll bf = new BranchMatchAll();
			bf.addFilter(new BranchBukrsFilter(contractModel.searchModel
					.getBukrs()));
			bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
			bf.addFilter(new BranchBusinessAreaFilter(0));
			cbranch_list = bf.filterBranch(p_branchF4Bean.getBranch_list());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm:cbranch");
	}

	private List<Staff> l_dealer;

	public List<Staff> getL_dealer() {
		return l_dealer;
	}

	public void setL_dealer(List<Staff> l_dealer) {
		this.l_dealer = l_dealer;
	}

	public void loadDealerList() {
		l_dealer = new ArrayList<Staff>();
		// SalistService ss = new SalistService();
		Branch wa_bra = p_branchF4Bean.getL_branch_map().get(
				p_search_contract.getBranch_id());
		// l_dealer = ss.loadDealers(wa_bra);

		String dwcl = "";
		if (wa_bra != null && wa_bra.getBranch_id() > 0) {
			dwcl = dwcl + "and sal.position_id = 4 and sal.branch_id = "
					+ wa_bra.getBranch_id() + " and sal.bukrs = "
					+ wa_bra.getBukrs();

			StaffDao staffDao = (StaffDao) appContext.getContext().getBean(
					"staffDao");

			System.out.println(dwcl);
			l_dealer = staffDao.dynamicFindStaffSalary(dwcl);
			System.out.println("Stuff found: " + l_dealer.size());
		}

		contractModel.searchModel.setServ_branch_id(p_service.getBranch_id());
		loadContractModel();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm");
	}

	// ******************************* CUSTOMER *****************************

	public void to_search_customer() {
		try {
			CustomerService personService = (CustomerService) appContext
					.getContext().getBean("customerService");
			p_customer_list = personService.dynamicSearch(searchCustomer);

			if (p_customer_list.size() == 0) {
				p_customer_list = new ArrayList<Customer>();
				GeneralUtil.addMessage("Инфо", "Не найдено.");
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("CustomerSearchForm:customerTable");

		} catch (DAOException ex) {
			p_customer_list = new ArrayList<Customer>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("CustomerSearchForm:customerTable");
			GeneralUtil.addMessage("Error", ex.getMessage());
		}
	}

	// *************************** Customer ********************************
	private List<Customer> p_customer_list = new ArrayList<Customer>();

	public List<Customer> getP_customer_list() {
		return p_customer_list;
	}

	public void setP_customer_list(List<Customer> p_customer_list) {
		this.p_customer_list = p_customer_list;
	}

	private Customer selectedCustomer = new Customer();

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	private Customer searchCustomer = new Customer();

	public Customer getSearchCustomer() {
		return searchCustomer;
	}

	public void setSearchCustomer(Customer searchCustomer) {
		this.searchCustomer = searchCustomer;
	}

	// ********************************************************************************

	public void assignFoundCustomer() {
		if (selectedCustomer != null && selectedCustomer.getId() != null) {
			contractModel.searchModel.setCustomer_id(selectedCustomer.getId());
			contractModel.searchModel.customer_fio = selectedCustomer
					.getFullFIO();
		} else {
			contractModel.searchModel.setCustomer_id(null);
			contractModel.searchModel.customer_fio = " ";
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm:fio");
		GeneralUtil.hideDialog("CustomerWidget");
		// selectedCustomer = new Customer();
	}

	public void clearCustomerField() {
		contractModel.searchModel.setCustomer_id(null);
		contractModel.searchModel.customer_fio = "";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ContractListForm:fio");
	}

	// ***********************************************************************

	public void clearPriceTable() {
		priceList_list = new ArrayList<PriceList>();
		pl.clear();
	}

	List<Branch> contract_branch_list = new ArrayList<Branch>();

	public List<Branch> getContract_branch_list() {
		return contract_branch_list;
	}

	public Contract p_search_contract;

	public Contract getP_search_contract() {
		return p_search_contract;
	}

	public void setP_search_contract(Contract p_search_contract) {
		this.p_search_contract = p_search_contract;
	}

	private ContractModel contractModel;

	public ContractModel getContractModel() {
		return contractModel;
	}

	public void setContractModel(ContractModel contractModel) {
		this.contractModel = contractModel;
	}

	public int getOutputLength() {
		return this.contractModel.getRowCount();
	}

	public ContractDetails selectedContr;

	public ContractDetails getSelectedContr() {
		return selectedContr;
	}

	public void setSelectedContr(ContractDetails selectedContr) {
		this.selectedContr = selectedContr;
	}

	private ContractType ct;

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
	// **************************************************************************************
	// ********************************ServicePACKET*****************************************

	public void loadSpList() {
		ServPacketDao spDao = (ServPacketDao) appContext.getContext().getBean(
				"servPacketDao");
		if (ct != null && ct.getMatnr() != null) {

			spList = spDao.findAllByTovarID(
					p_service.getBukrs(),
					p_countryF4Bean.getL_country_map()
							.get(p_branch.getCountry_id()).getCountry_id(),
					ct.getMatnr());
			System.out.println("Found SP for Matnr " + ct.getMatnr()
					+ " total: " + spList.size());
		} else
			System.out
					.println("Couldn't load SP List since ContractType is null!");
	}

	public void loadSpDetails() {
		servPosListTable.clear();
		if (selectedSPId != null && selectedSPId > 0) {
			System.out.println("Selected SP Id: " + selectedSPId);
			ServPacketDao spDao = (ServPacketDao) appContext.getContext()
					.getBean("servPacketDao");
			spSelected = spDao.find(selectedSPId);

			if (spSelected != null && spSelected.getId() > 0) {
				System.out
						.println("Selected SP Found: " + spSelected.getName());

				p_service.setServ_packet_id(spSelected.getId());
				p_service.setMaster_currency(spSelected.getWaers());
				p_service.setMaster_premi(spSelected.getMaster_bonus());
				p_service.setOper_currency(spSelected.getWaers());
				p_service.setOper_premi(spSelected.getOper_bonus());
				p_service.setSummTotal(spSelected.getSumm());
				p_service.setPayment_due(spSelected.getPrice());
				p_service.setDiscount(spSelected.getDiscount());

				sum_wrbtr = spSelected.getSumm();
				if (p_currate > 0)
					sum_dmbtr = sum_wrbtr / p_currate;
				else
					sum_dmbtr = 0;

				ServPacketPosDao spPosDao = (ServPacketPosDao) appContext
						.getContext().getBean("servPacketPosDao");
				List<ServPacketPos> out_spPosL = spPosDao
						.findAllByServPacketID(spSelected.getId());
				int i = 0;

				for (ServPacketPos spPos : out_spPosL) {
					i++;
					ServPosListTable spt = new ServPosListTable(i,
							p_service.getBukrs(), spSelected.getWaers());
					ServicePos sp = new ServicePos();

					sp.setCurrency(spSelected.getWaers());
					sp.setOperation(spPos.getOperation());
					if (sp.getOperation() == 1) {
						sp.setMatnr_id(spPos.getMatnr_id());
						Matnr m = p_matnrF4Bean.getL_matnr_map().get(
								sp.getMatnr_id());
						if (m != null)
							spt.setMatnr(m);
					}
					sp.setInfo2(spPos.getInfo());
					sp.setMatnr_price(spPos.getPrice());
					sp.setNew_war_inm(spPos.getNew_war_inm());
					sp.setQuantity(spPos.getQuantity());
					sp.setSumm(spPos.getSumm());

					spt.setServPos(sp);
					spt.setDis_mat(true);
					spt.setDis_qq(true);
					spt.setDis_pr(true);

					servPosListTable.add(spt);
					calcRow(servPosListTable.size());
				}

				ServPacketWarDao spWarDao = (ServPacketWarDao) appContext
						.getContext().getBean("servPacketWarDao");
				List<ServPacketWar> out_spWarL = spWarDao
						.findAllByServPacketId(spSelected.getId());
				i = 0;
				spWarL = new ArrayList<ServPacketWarOutput>();
				for (ServPacketWar spWar : out_spWarL) {
					i++;
					ServPacketWarOutput spWarO = new ServPacketWarOutput(i);
					spWarO.setSpWar(spWar);
					spWarL.add(spWarO);
				}
			} else {
				spSelected = new ServPacket();
				p_service.setServ_packet_id(null);
				spWarL.clear();
			}
		} else {
			spSelected = new ServPacket();
			p_service.setServ_packet_id(null);
			spWarL.clear();
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public List<ServPacketWarOutput> spWarL;

	public List<ServPacketWarOutput> getSpWarL() {
		return spWarL;
	}

	public void setSpWarL(List<ServPacketWarOutput> spWarL) {
		this.spWarL = spWarL;
	}

	// **************************************************************************************
	// **********************************************************************

	private MatnrSearch matnrSearch = new MatnrSearch();

	public MatnrSearch getMatnrSearch() {
		return matnrSearch;
	}

	public void setMatnrSearch(MatnrSearch matnrSearch) {
		this.matnrSearch = matnrSearch;
	}

	protected MatnrDao getMatnrDao() {
		return (MatnrDao) appContext.getContext().getBean("matnrDao");
	}

	public void assignSelectedMatnr() {
		disable_save_button();
		Matnr m = selectedMatnr.getMatnr();
		MatnrPriceList mpl = mplMap.get(m.getMatnr());
		if (mpl.getMenge() > 0) {

			mpl.setqMinus(mpl.getqMinus() + 1);
			mpl.setMenge(mpl.getqInit() - mpl.getqMinus());
			refreshMatnrList();

			p_spTableRow.setMatnr(selectedMatnr.getMatnr());
			p_spTableRow.getServPos().setMatnr_id(
					selectedMatnr.getMatnr().getMatnr());

			p_spTableRow.setWarranty(false);

			ServicePos sp2 = new ServicePos();
			sp2.setMatnr_id(selectedMatnr.getMatnr().getMatnr());
			sp2.setQuantity(1L);
			sp2.setOperation(1L);
			sp2.setServ_type(m.getCategory());
			PriceList pl2 = new PriceList();

			if (pl.get(selectedMatnr.getMatnr().getMatnr()) != null
					&& pl.get(selectedMatnr.getMatnr().getMatnr()).getMatnr() > 0) {
				pl2 = pl.get(selectedMatnr.getMatnr().getMatnr());
			}

			double rate = 1;
			if (pl2.getWaers() != null && pl2.getWaers().length() > 0) {
				rate = getCurrencyRate(pl2.getWaers(), p_currency);
				System.out.println(pl2.getWaers() + " vs " + p_currency
						+ " rate: " + rate);
			}

			p_spTableRow.setDis_pr(true);
			if (p_service.getServ_type() != 1) {
				double price = pl2.getPrice() * rate;
				sp2.setMatnr_price(price);
				if (// p_service.getBranch_id() == 73 // Bishkek-Service
					//|| 
					p_service.getBukrs().equals("2000") ||
					p_service.getBranch_id() == 74 || // Tashkent-Service
					p_service.getBranch_id() == 75) // Baku-Service
					p_spTableRow.setDis_pr(false);
			}
			sp2.setCurrency(p_main_currency);

			String inf = "";
			if (rate != 1) {
				// inf = "Initial Price: " + pl2.getPrice() + " " +
				// pl2.getWaers() +
				// " | ";
				// inf += "Rate: 1 " + pl2.getWaers() + " = " + rate + " " +
				// p_main_currency + " | ";
			} else {
				inf = "";
			}
			sp2.setInfo1(inf);

			sp2.setFno(selectedMatnr.getMatnr().getFno());

			// matnr_war
			MatnrWarDao mwDao = (MatnrWarDao) appContext.getContext().getBean(
					"matnrWarDao");
			MatnrWar mw = mwDao
					.findByMatnr(selectedMatnr.getMatnr().getMatnr());
			if (mw != null && mw.getMatnr_id() != null && mw.getMatnr_id() > 0
					&& mw.getWar_months() > 0)
				sp2.setNew_war_inm(mw.getWar_months());

			p_spTableRow.setServPos(sp2);
			p_spTableRow.setSum(sp2.getQuantity() * sp2.getMatnr_price()
					/ p_currate);
			p_spTableRow.setSum_sc(p_spTableRow.getSum() * p_currate);

			if (p_service.getServ_type() == 1) {
				p_spTableRow.getServPos().setQuantity(1L);
				p_spTableRow.dis_qq = true;
				p_spTableRow.dis_pr = true;
			}

			calcRow(servPosListTable.size());
			int pos = p_spTableRow.getIndex() - 1;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:olTable:" + pos + ":b_matnr");
			reqCtx.update("form:olTable:" + pos + ":unitPrice");
			reqCtx.update("form:olTable:" + pos + ":b_menge");
			reqCtx.update("form:olTable:" + pos + ":b_dmbtr");
			reqCtx.update("form:olTable:" + pos + ":b_wrbtr");
			reqCtx.update("form:olTable:" + pos + ":infoText");
			reqCtx.update("form:olTable:" + pos + ":fno");
			reqCtx.update("form:olTable:" + pos + ":newWar");

			selectedMatnr = null;
			GeneralUtil.hideDialog("matnrWidget");
		} else {
			msg.clear();
			msg.put("ru", "Недостаточное количество!");
			msg.put("en", "Insufficient amount!");
			msg.put("tr", "Yetersiz miktarda!");
			GeneralUtil.addInfoMessage(msg.get(userData.getU_language()));
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		}
	}

	Map<String, String> msg = new HashMap<String, String>();

	// ********************************************************************************

	public void checkWarranty(int i) {
		int j = i - 1;

		if (p_contract != null) {
			
			System.out.println("ContrTYPE: " + p_contract.getContract_type_id());
			ContractType ct = new ContractType();
			ct = p_contractTypeF4Bean.getCt_map().get(
					p_contract.getContract_type_id());
			int war_term = 0;

			if (p_customer.getFiz_yur() == 2) {
				war_term = ct.getWar_fiz();
			} else {
				war_term = ct.getWar_yur();
			}

			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			cal.setTime(p_contract.getWar_start());
			SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");

			System.out.println("Warranty Start Date: "
					+ format1.format(cal.getTime()));
			cal.add(Calendar.MONTH, war_term);
			Date war_end = cal.getTime();

			System.out.println("Warranty End Date: " + format1.format(war_end));
			System.out.println("Warranty Term: " + war_term);
			System.out.println("Warranty Today: " + format1.format(today));

			if (servPosListTable.get(j).isWarranty()) {
				System.out.println("Check the warranty term!");

				List<UserRole> urL = new ArrayList<UserRole>();
				UserRoleDao urlDao = (UserRoleDao) appContext.getContext()
						.getBean("userRoleDao");
				urL = urlDao.findUserRoles(userData.getUserid());

				boolean isNotCoordinator = true;
				// System.out.println("Role list: " + urL.size());
				for (UserRole wa_ur : urL) {
					// System.out.println(wa_ur.getRoleId());
					if (wa_ur.getRoleId() == 24) {
						isNotCoordinator = false;
						// System.out.println("IS COORDINATOR!");
					}
				}
				System.out.println("IS NOT COORDINATOR: " + isNotCoordinator);

				if (servPosListTable.get(j).getServPos().getOperation() == ServicePos.OPER_SELL
						&& !Validation.isEmptyLong(servPosListTable.get(j).getServPos().getMatnr_id())) {
					System.out.println("Operation: " + ServicePos.OPER_SELL);
					System.out.println("Matnr: " + servPosListTable.get(j).getServPos().getMatnr_id());
					
					if (war_end.compareTo(today) <= 0 && isNotCoordinator
							&& !hasWarranty(p_service.getDate_open(), servPosListTable.get(j).getServPos(), p_service.getContract_id())) {
						servPosListTable.get(j).setWarranty(false);

						System.out.println("Срок гарантии истек!");
						GeneralUtil.addMessage("Срок гарантии истек!",
								"Срок гарантии истек!");
						
					} else {
						applyWarranty(i);
					}
				} else {
					GeneralUtil.addMessage("Гарантия присваивается к материалам!",
							"К услугам гарантия не присваивается!");
					servPosListTable.get(j).setWarranty(false);
				}
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:olTable:" + j + ":warrantyCheckBox");
			} else {
				calcRow(i);
			}
		} else {
			GeneralUtil.addMessage("Empty contract!",
					"No contract has been selected!");
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olTable");
	}
	
	private boolean hasWarranty(Date inDate, ServicePos inSp, Long inConId) {
		if (inSp.getOperation() == ServicePos.OPER_SELL
				&& !Validation.isEmptyLong(inSp.getMatnr_id())) {
			
			ServConMatnrWarDao scmDao = (ServConMatnrWarDao) appContext.getContext().getBean("servContrMatnrWar");
			List<ServConMatnrWar> scmL = scmDao.findAllByContractId(inConId);
			
			for (ServConMatnrWar scm:scmL) {
			    if (scm.getMatnr_id().equals(inSp.getMatnr_id())) {
			    	SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");				    	
				    System.out.println("Warranty for: " + inSp.getMatnr_id() + " is valid untill " + f.format(scm.getTo_date()));
				    
				    if (scm.getTo_date().compareTo(inDate) >= 0) {
				    	System.out.println("Гарантия присвоена по запчасти!");
				    	return true;
				    }
			    }
			    	
			}
		} else {
			System.out.println("Гарантия присваивается к материалам! К услугам гарантия не присваивается!");
		}
		return false;
	}

	private void applyWarranty(int i) {
		if (i > 0) {
			int j = i - 1;
			servPosListTable.get(j).setSum(0);
			servPosListTable.get(j).setSum_sc(0);
			servPosListTable.get(j).getServPos().setSumm(0);
			// servPosListTable.get(j).getServPos().setMatnr_price(0);
			// GeneralUtil.addMessage("На подтверждение.","Гарантия будет присвоена после подтверждения координатором.");
			GeneralUtil.addMessage("По гарантии.", "Гарантия присвоена!");
		}
	}

	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************

	private List<ServicePos> extractSPFromSPLT(List<ServPosListTable> spltL) {
		List<ServicePos> sp_l = new ArrayList<ServicePos>();
		for (ServPosListTable wa_splt : spltL) {
			wa_splt.getServPos().setSumm(wa_splt.getSum_sc());
			if (!Validation.isEmptyLong(wa_splt.getServPos().getMatnr_id()))
				wa_splt.getServPos().setOperation(1L);
			sp_l.add(wa_splt.getServPos());
		}
		return sp_l;
	}

	private void cancelService(ServiceService ss) {
		getSfService().deleteSCHByServId(p_service.getId(), userData, transaction_id);		
		if (p_service_old.getServ_type() == ServiceTable.TYPE_FILTERS) {
			// System.out.println("Cancel Filter Service!");
			ss.cancelFilter(p_service_old, p_branch, userData,
					transaction_code, transaction_id, spl_old);
		} else if (p_service_old.getServ_type() == ServiceTable.TYPE_FITTING) {
			ss.cancelFitting(p_service_old, p_branch, p_contract, userData,
					transaction_code, transaction_id);
		} else if (p_service_old.getServ_type() == ServiceTable.TYPE_SERVICE) {
			ss.cancelService(p_service_old, userData, transaction_code);
		} else if (p_service_old.getServ_type() == ServiceTable.TYPE_PACKET) {
			ss.cancelPacket(p_service_old, p_branch, userData,
					transaction_code, transaction_id);
		}
	}
	
	private ServFilterService getSfService() {
		return (ServFilterService) appContext.getContext().getBean("servFilterService");
	}
	
	private void createService(ServiceService ss) {
		p_service.setId(null);
		if (docancel)
			p_service.setPaid(0);
		List<ServicePos> sp_l = this.extractSPFromSPLT(servPosListTable);
		
		if (p_service_old.getServ_type() == ServiceTable.TYPE_FILTERS) {
			ss.createServiceZamenFilter(p_service, p_branch, p_contract,
					p_service.getCustomer_id(), userData, transaction_code,
					transaction_id, sp_l);
		} else if (p_service_old.getServ_type() == ServiceTable.TYPE_FITTING) {
			ss.createServiceFitting(p_service, p_branch, p_contract, userData,
					transaction_id);
		} else if (p_service_old.getServ_type() == ServiceTable.TYPE_SERVICE) {
			ss.createService(p_service, p_branch, p_contract,
					p_service.getCustomer_id(), userData, transaction_code,
					transaction_id, sp_l);
		} else if (p_service_old.getServ_type() == ServiceTable.TYPE_PACKET) {
			ServPacketWarDao spWarDao = (ServPacketWarDao) appContext
					.getContext().getBean("servPacketWarDao");
			List<ServPacketWar> vspWarL = spWarDao
					.findAllByServPacketId(p_service.getServ_packet_id());
			ss.createServiceProphylaxis(p_service, p_branch, p_contract,
					p_service.getCustomer_id(), userData, transaction_code,
					transaction_id, sp_l, vspWarL);
		}
		creatSCHforNewService();
	}

	private void creatSCHforNewService() {
		ServCRMHistory sch = new ServCRMHistory();
		
		sch.setActionId(new Long(p_service.getServ_type()));
		sch.setActionDate(p_service.getDate_open());
		sch.setContractId(p_service.getContract_id());
		String inf = "";
		int l = 0;
		if (p_service.getServ_type() == 1) {
			inf = "Замена фильтров. ";
			for (ServPosListTable wa_splt : servPosListTable) {
				if (wa_splt.getServPos().getMatnr_id() != null
						&& wa_splt.getServPos().getMatnr_id() > 0) {
					wa_splt.getServPos().setOperation(1L);
					l++;
					if (l > 1) inf += ", ";
					inf += wa_splt.getServPos().getFno() 
							+ "-" + wa_splt.getMatnr().getCode();
				}				
			}
		} else if (p_service.getServ_type() == 2) {
			inf = "Произведена установка аппарата.";
		} else if (p_service.getServ_type() == 3) {
			inf = "Ремонт. ";
			for (ServPosListTable wa_splt : servPosListTable) {
				l++;
				wa_splt.getServPos().setSumm(wa_splt.getSum_sc());
				// System.out.println(wa_splt.getIndex() + ". Sum_sc = "
				// + wa_splt.getServPos().getSumm());
				if (l > 1) inf += ", ";
				if (wa_splt.getServPos().getMatnr_id() != null
						&& wa_splt.getServPos().getMatnr_id() > 0) {
					inf += wa_splt.getMatnr().getCode();					
				} else 
					inf += wa_splt.getServPos().getInfo1();
			}
		} else if (p_service.getServ_type() == 4) {
			inf = "Сервис пакет. ";
			for (ServPosListTable wa_splt : servPosListTable) {
				l++;
				wa_splt.getServPos().setSumm(wa_splt.getSum_sc());
				// System.out.println(wa_splt.getIndex() + ". Sum_sc = "
				// + wa_splt.getServPos().getSumm());
				if (l > 1) inf += ", ";
				if (wa_splt.getServPos().getMatnr_id() != null
						&& wa_splt.getServPos().getMatnr_id() > 0) {
					inf += wa_splt.getMatnr().getCode();					
				} else 
					inf += wa_splt.getServPos().getInfo1();
			}
		}
		inf += " | " + userData.getStaffDisplayName();
		sch.setInfo(inf);
		sch.setStaffId(p_service.getMaster_id());
		sch.setTovarSN(p_service.getTovar_sn());
		sch.setServiceId(p_service.getId());
		getSfService().createNewSCH(sch, userData, transaction_id);	
	}
	
	private void updateService(ServiceService ss) {
		ss.updateService(p_service, p_service_old, p_branch, p_contract,
				userData, transaction_code, transaction_id);
	}

	public void to_save() {
		try {
			PermissionController.canCreate(userData, transaction_id);
			if (p_service != null) {
				ServiceService ss = (ServiceService) appContext.getContext()
						.getBean("serviceService");
				if (docancel) {
					cancelService(ss);
					GeneralUtil.addSuccessMessage("Old Service # "
							+ p_service_old.getServ_num() + " cancelled!");
					if (docreate)
						createService(ss);
					GeneralUtil.addSuccessMessage("New Service # "
							+ p_service.getServ_num() + " created!");
				} else {
					updateService(ss);
					GeneralUtil.addSuccessMessage("Service # "
							+ p_service.getServ_num() + " updated!");
				}
				// clearService();
				toViewPage();
			}
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void statusChange() {
		if (!p_service_old.getServ_status().equals(p_service.getServ_status())
				&& (p_service.getServ_status() == ServiceTable.STATUS_CANCELLED)) {
			GeneralUtil.addInfoMessage("Ready for Cancel!");
			doCancelON();
			doCreateOFF();
		} else {
			GeneralUtil.addInfoMessage("Status is not applicable!");
			p_service.setServ_status(p_service_old.getServ_status());
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	public void doCreateON() {
		doCancelON();
		docreate = true;
		System.out.println("Create ON!");
	}

	public void doCreateOFF() {
		// doCancelOFF();
		docreate = false;
	}

	public void doCancelON() {
		docancel = true;
	}

	public void dateChanged() {
		if (p_service_old.getDate_open().getTime() != p_service.getDate_open()
				.getTime())
			doCreateON();
	}

	public void doCancelOFF() {
		docancel = false;
	}

	// *********************************************************
	public String p_check_text;
	public boolean p_check_payments;
	public String p_check_icon; // ui-icon ui-icon-check
	public String p_check_text_color;
	private boolean docancel = false;
	private boolean docreate = false;
	private List<ServicePos> spl_old;

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

	public String getP_check_text_color() {
		return p_check_text_color;
	}

	public void setP_check_text_color(String p_check_text_color) {
		this.p_check_text_color = p_check_text_color;
	}

}
