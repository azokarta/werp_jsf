package service.szfvc;

import f4.BranchF4;
import f4.BukrsF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.CountryDao;
import general.dao.DAOException;
import general.dao.ServPosDao;
import general.dao.ServiceDao;
import general.dao.UserRoleDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchParentFilter;
import general.filter.branch.BranchTypeFilter;
import general.output.tables.ServCRMScheduleOutput;
import general.output.tables.ServFilterOutput;
import general.services.ServFilterVCService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Contract;
import general.tables.Country;
import general.tables.SalePlan;
import general.tables.ServFilter;
import general.tables.ServFilterVC;
import general.tables.ServicePos;
import general.tables.ServiceTable;
import general.tables.UserRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;

import service.szf.CrmHistory;
import service.szf.ScheduledCalls;
import user.User;

@ManagedBean(name = "szvcflistBean", eager = true)
@ViewScoped
public class SzfVCList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 175430846008441441L;
	private final static String transaction_code = "SZFVCLIST";
	private final static Long transaction_id = (long) 500;
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

	// **********************ServiceCRMHistory***************************
	@ManagedProperty(value = "#{crmhBean}")
	private CrmHistory crmhBean;

	public CrmHistory getCrmhBean() {
		return crmhBean;
	}

	public void setCrmhBean(CrmHistory crmhBean) {
		this.crmhBean = crmhBean;
	}

	// ******************************************************************
	// **********************ServiceCRMSchedule***************************
	@ManagedProperty(value = "#{schedcallsBean}")
	private ScheduledCalls schedcallsBean;

	public ScheduledCalls getSchedcallsBean() {
		return schedcallsBean;
	}

	public void setSchedcallsBean(ScheduledCalls schedcallsBean) {
		this.schedcallsBean = schedcallsBean;
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
	// ******************************************************************

	@PostConstruct
	public void init() {
		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return; // Skip ajax requests.
		}

		try {

			toMainPage();
			PermissionController.canRead(userData, SzfVCList.transaction_id);
			if (userData.isMain()) {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					bukrs_list.add(wa_bukrs);
				}
			} else {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					if (wa_bukrs.getBukrs().equals(userData.getBukrs())) {
						bukrs_list.add(wa_bukrs);
						break;
					}
				}
				p_bukrs = userData.getBukrs();
				p_branch_id = (long) userData.getBranch_id();
				loadBranch();
			}
						
			checkAccessLevel();

		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
			toMainPage();
		}
	}

	private void checkAccessLevel() {
		disVaultTab = true;
		List<UserRole> urL = new ArrayList<UserRole>();
		UserRoleDao urlDao = (UserRoleDao) appContext.getContext().getBean(
				"userRoleDao");
		urL = urlDao.findUserRoles(userData.getUserid());

		boolean isCoordinator = false;
		// System.out.println("Role list: " + urL.size());
		for (UserRole wa_ur : urL) {
			// System.out.println(wa_ur.getRoleId());
			if (wa_ur.getRoleId() == 24 || wa_ur.getRoleId() == 30) {
				isCoordinator = true;
			}
		}
		System.out.println("IS COORDINATOR: " + isCoordinator);
		if (isCoordinator || userData.isMain() || userData.isSys_admin())
			disVaultTab = false;
	}

	public void loadBranch() {
		branch_list = new ArrayList<Branch>();
		// System.out.println(p_bukrs);

		Long branchId = userData.getBranch_id();
		if (userData.isMain() || userData.isSys_admin()) {
			if (p_bukrs.equals("1000"))
				branchId = Branch.AURA_MAIN_BRANCH_ID;
			else if (p_bukrs.equals("2000"))
				branchId = Branch.GREEN_LIGHT_MAIN_BRANCH_ID;
		}
		// System.out.println("In BranchID: " + branchId);
		branch_list = loadBranchListByUserBranch(p_bukrs, branchId,
				BusinessArea.AREA_SERVICE);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
	}

	public List<Branch> loadBranchList(String a_bukr) {
		BranchMatchAll bma = new BranchMatchAll();
		bma.addFilter(new BranchBukrsFilter(p_bukrs));
		bma.addFilter(new BranchBusinessAreaFilter(BusinessArea.AREA_SERVICE));
		bma.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
		return bma.filterBranch(p_branchF4Bean.getBranch_list());
	}

	public List<Branch> loadBranchListByUserBranch(String a_bukr,
			Long branchId, int ba) {
		List<Branch> output = new ArrayList<Branch>();
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(a_bukr));
		BranchDao brDao = (BranchDao) appContext.getContext().getBean(
				"branchDao");
		Branch inBranch = brDao.find(branchId);

		List<Branch> brL = new ArrayList<Branch>();
		if ((inBranch.getType() != Branch.TYPE_BRANCH)
				|| (inBranch.getBusiness_area_id() == 0)) {
			bf.addFilter(new BranchParentFilter(inBranch.getBranch_id()));
			brL = bf.filterBranch(p_branchF4Bean.getBranch_list());
		} else
			bf.addFilter(new BranchParentFilter(inBranch.getParent_branch_id()));

		bf.addFilter(new BranchBusinessAreaFilter(ba));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));

		output = bf.filterBranch(p_branchF4Bean.getBranch_list());

		for (Branch br : brL) {
			if ((br.getType() != Branch.TYPE_BRANCH)
					|| (br.getBusiness_area_id() == 0))
				output.addAll(loadBranchListByUserBranch(a_bukr,
						br.getBranch_id(), ba));
		}
		return output;
	}

	public void listTypeChange() {

	}

	public void toMainPage() {
		GeneralUtil.doRedirect("/general/mainPage.xhtml");
	}

	public void to_search() {
		try {
			if (Validation.isEmptyString(p_tovSN)) {
				if (Validation.isEmptyString(p_bukrs))
					throw new DAOException("Please select Company!");

				if (Validation.isEmptyLong(p_branch_id))
					throw new DAOException("Please select Branch!");

				if ((p_list_type == ServFilter.LIST_CURRENT
						|| p_list_type == ServFilter.LIST_OVERDUE)
						&& p_month == null)
					throw new DAOException("Please select date!");
			}
			
			/*
			 * Date d2 = new Date(); d2.setDate(p_month.getDate());
			 * d2.setMonth(p_month.getMonth()); d2.setYear(p_month.getYear());
			 */
			sfoList = getSfServ().getZFList(p_bukrs, p_branch_id, p_month,
					p_list_type, p_list_warranty, p_conNum, p_tovSN, p_list_cat);
			
			sfOutputList = new ArrayList<ServFilterOutput>();
			sfOutputList.addAll(sfoList);

			outputLength = sfoList.size();

			initSumm();
//			if ((p_list_type == ServFilter.LIST_CURRENT
//					|| p_list_type == ServFilter.LIST_OVERDUE)
//					&& Validation.isEmptyString(p_tovSN)) 
//					calcPlan();
//			
//			if (p_list_type != ServFilter.LIST_ALL)
//				p_list_cat = 0;
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void to_find() {
		sfOutputList.clear();
		boolean showAll = true;
		searchCon.setTovar_serial(searchCon.getTovar_serial().trim());
		for (ServFilterOutput sfo : sfoList) {
			if (!Validation.isEmptyLong(searchCon.getContract_number())) {
				showAll = false;
				if (searchCon.getContract_number().equals(sfo.getConNumber())) {
					sfOutputList.add(sfo);
					break;
				}
			} else if (!Validation.isEmptyLong(searchCon.getOld_sn())) {
				showAll = false;
				if (searchCon.getOld_sn().equals(sfo.getOldSN())) {
					sfOutputList.add(sfo);
					break;
				}
			} else if (!Validation.isEmptyString(searchCon.getTovar_serial())) {
				showAll = false;
				if (searchCon.getTovar_serial().equals(sfo.getTovSerial())) {
					sfOutputList.add(sfo);
					break;
				}
			} else if (!Validation.isEmptyString(searchCus)) {
				if (!Validation.isEmptyString(sfo.getCustomerFIO())) {
					String cusFIO = sfo.getCustomerFIO().toLowerCase();
					String regex = ".*" + searchCus.toLowerCase() + ".*";
					if (cusFIO.matches(regex)) {
						sfOutputList.add(sfo);
					}
				}
				showAll = false;
			} else if (!Validation.isEmptyString(searchAddr)) {
				if (!Validation.isEmptyString(sfo.getAddrService())) {
					String addr = sfo.getAddrService().toLowerCase();
					String regex = ".*" + searchAddr.toLowerCase() + ".*";
					if (addr.matches(regex)) {
						sfOutputList.add(sfo);
					}
				}
				showAll = false;
			} else if (!Validation.isEmptyString(searchTel)) {
				if (!Validation.isEmptyString(sfo.getTel())) {
					String tel = sfo.getTel().toLowerCase();
					String regex = ".*" + searchTel.toLowerCase() + ".*";
					if (tel.matches(regex)) {
						sfOutputList.add(sfo);
					}
				}
				showAll = false;
			}
		}

		if (showAll)
			sfOutputList.addAll(sfoList);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}

	private void initSumm() {
		summTotal = 0;
		summDone = 0;
		summMaster = 0;
		summOper = 0;
		summOverdue = 0;
		summPlan = 0;
		summRemain = 0;
		summTotalDone = 0;
		qDone = 0;
		qDoneOverdue = 0;
		qPlan = 0;
		qRemain = 0;
		qTotal = 0;
	}

	// ***********************************************************************************************
	// ***********************************************************************************************

	private ServFilterVCService getSfServ() {
		return (ServFilterVCService) appContext.getContext().getBean(
				"sfvcService");
	}

	public List<ServFilterVC> sfL = new ArrayList<>();

	private void calcPlan() {
		if (p_list_type == ServFilterVC.LIST_CURRENT)
			p_overdue = false;
		else 
			p_overdue = true;
				
		sfL = getSfServ().getZFListForPlan(p_bukrs, p_branch_id, p_month,
				p_overdue);
		System.out.println("Plan found records: " + sfL.size());

//		List<ServFilterPremis> sfpl = new ArrayList<ServFilterPremis>();
//		ServFilterPremisDao sfpDao = (ServFilterPremisDao) appContext
//				.getContext().getBean("servFilterPremisDao");
//		Branch br = p_branchF4Bean.getL_branch_map().get(p_branch_id);
//		sfpl = sfpDao.findAllByBukrsAndCountry(p_bukrs, br.getCountry_id());
		summPlan = 0;
		summMaster = 0;
		summOper = 0;
		summTotal = 0;
		// summDone = 0;
		// summRemain = 0;
		// summOverdue = 0;
//		if (sfpl.size() > 0)
//			fwaers = sfpl.get(0).getWaers();
		f1Q = 0;		

		for (ServFilterVC sf : sfL) {
			int fc = 0;

			if (p_overdue) {
				if (GeneralUtil.getFirstDateOfMonth(sf.getF1_date_next())
						.getTime() < GeneralUtil.getFirstDateOfMonth(p_month)
						.getTime()
						|| (isInMonth(sf.getF1_date())
								&& sf.getF1_date_prev() != null && GeneralUtil
								.calcAgeinMonths(sf.getF1_date(),
										sf.getF1_date_prev()) > sf.getF1_mt())) {
					fc++;
					f1Q++;
				}				
			} else {
				if (isInMonth(sf.getF1_date_next())
						|| (isInMonth(sf.getF1_date())
								&& (sf.getF1_date_prev() != null) && GeneralUtil
								.calcAgeinMonths(sf.getF1_date(),
										sf.getF1_date_prev()) == sf.getF1_mt())) {
					fc++;
					f1Q++;
				}				
			}
		}
		
		double stdPrice = ServFilterVC.STD_PRICE.get(fwaers);
		System.out.println("STD_PRICE: " + stdPrice + fwaers);
		
		summTotal += f1Q * stdPrice;
//		summMaster += sfp.getMaster();
//		summOper += sfp.getOperator();
		
		summPlan = summTotal;
		qTotal = sfL.size();
		qPlan = qTotal;
		int fc = f1Q + f2Q + f3Q + f5Q;
		System.out.println("FQ: " + fc);
		System.out.println("MQ: " + f4Q);
		if (p_overdue) {
			summPlan *= ServFilter.OVERDUE_RATE;
			qPlan = Math.round((float) (qTotal * ServFilter.OVERDUE_RATE));
		}

		System.out.println("Total Plan: " + summPlan + " " + fwaers);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
		reqCtx.update("form");
	}
	
	public void assignWaers() {
		fwaers = "";
		Branch p_branch = p_branchF4Bean.getL_branch_map().get(p_branch_id);
		
		if (p_branch != null && !Validation.isEmptyLong(p_branch.getCountry_id())) {
			CountryDao cDao = (CountryDao) appContext.getContext().getBean("countryDao");				
			Country p_country = cDao.find(p_branch.getCountry_id());
			if (p_country != null) {
				fwaers = p_country.getCurrency();
			}
		}		
	}	

	private boolean isInMonth(Date inDate) {
		return ((inDate.getTime() >= GeneralUtil.getFirstDateOfMonth(p_month)
				.getTime()) && (inDate.getTime() <= GeneralUtil
				.getLastDateOfMonth(p_month).getTime()));
	}

	public void calcDone() {
		List<ServiceTable> servL = getServiceDao().findAllInCurrentMonthByType(
				p_branch_id, p_month, ServiceTable.TYPE_PACKET);
		System.out.println("Found total Services in current month: "
				+ servL.size());
		summDone = 0;
		summOverdue = 0;
		qDone = 0;
		qDoneOverdue = 0;
		double summNull = 0;
		Date dl = GeneralUtil.getLastDateOfMonth(p_month);
		int i = 0;
		for (ServiceTable s : servL) {
			if (s.getServ_type() == ServiceTable.TYPE_PACKET) {
				List<ServicePos> spL = getServPosDao().findAllByServID(
						s.getId());
				ServFilterVC sf = getSfServ().getSFByContractId(
						s.getContract_id());
				int fc = 0;
				int mc = 0;
				boolean isOverdue = true;

				// System.out.print(i + " - ServiceID: " + s.getId() +
				// "        SN: " + s.getServ_num());
				// System.out.println("        ServFilterID: " + sf.getId());
				i++;
				if (sf != null) {
					for (ServicePos sp : spL) {

						if (!Validation.isEmptyLong(sp.getMatnr_id())
								&& sp.getOperation() == 1) {
							switch (sp.getFno()) {
							case 1: {
								// if (sf.getF1_sid() == s.getId()) {
								if ((sf.getF1_date_next().getTime() > dl
										.getTime())
										&& (isInMonth(sf.getF1_date())
												&& (sf.getF1_date_prev() != null) && GeneralUtil
												.calcAgeinMonths(
														sf.getF1_date(),
														sf.getF1_date_prev()) <= sf
												.getF1_mt())) {
									fc++;
									isOverdue = false;
								}
								break;
							}
							default:
								break;
							}
						}
					}
				} else
					summNull += s.getPayment_due();

				if (isOverdue) {
					summOverdue += s.getPayment_due();
					qDoneOverdue++;
				} else {
					summDone += s.getPayment_due();
					qDone++;
				}
			}
		}

		System.out.println("SummNULL = " + summNull);
		if (p_overdue) {
			summDone = summOverdue;
			qDone = qDoneOverdue;
		}

		summRemain = summPlan - summDone;
		qRemain = qPlan - qDone;

		perDone = summDone / summPlan * 100;
		perRem = summRemain / summPlan * 100;

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
		reqCtx.update("form");
	}

	public void savePlan() {
		try {
			byte p_type;
			if (p_overdue)
				p_type = SalePlan.TYPE_OVERDUE_ROBOCLEAN;
			else
				p_type = SalePlan.TYPE_CURRENT_ROBOCLEAN;
			if (getSfServ().savePlan(p_branch_id, p_type, p_month, summPlan,
					fwaers, userData, transaction_id))
				GeneralUtil.addSuccessMessage("План сохранен!");
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void saveDoneAmountToPlan() {
		try {
			byte p_type;
			if (p_overdue)
				p_type = SalePlan.TYPE_OVERDUE_ROBOCLEAN;
			else
				p_type = SalePlan.TYPE_CURRENT_ROBOCLEAN;
			if (getSfServ().saveDone(p_branch_id, p_type, p_month, summDone,
					fwaers, userData, transaction_id))
				GeneralUtil
						.addSuccessMessage("Сумма выполненных замен сохранен!");
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public ServiceDao getServiceDao() {
		return (ServiceDao) appContext.getContext().getBean("serviceDao");
	}

	public ServPosDao getServPosDao() {
		return (ServPosDao) appContext.getContext().getBean("servPosDao");
	}

	public int f1Q;
	public int f2Q;
	public int f3Q;
	public int f4Q;
	public int f5Q;
	public double summTotalDone;
	public double summPlan;
	public double summDone;
	public double summRemain;
	public double summMaster;
	public double summOper;
	public double summOverdue;
	public String fwaers;
	public double summTotal;
	public boolean disVaultTab;
	public int qTotal;
	public int qPlan;
	public int qDone;
	public int qDoneOverdue;
	public int qRemain;
	public double perDone;
	public double perRem;

	// ***********************************************************************************************
	// ***********************************************************************************************

	public double getPerDone() {
		return perDone;
	}

	public void setPerDone(double perDone) {
		this.perDone = perDone;
	}

	public double getPerRem() {
		return perRem;
	}

	public void setPerRem(double perRem) {
		this.perRem = perRem;
	}

	public int getqDoneOverdue() {
		return qDoneOverdue;
	}

	public void setqDoneOverdue(int qDoneOverdue) {
		this.qDoneOverdue = qDoneOverdue;
	}

	public boolean isDisVaultTab() {
		return disVaultTab;
	}

	public int getqTotal() {
		return qTotal;
	}

	public void setqTotal(int qTotal) {
		this.qTotal = qTotal;
	}

	public int getqPlan() {
		return qPlan;
	}

	public void setqPlan(int qPlan) {
		this.qPlan = qPlan;
	}

	public int getqDone() {
		return qDone;
	}

	public void setqDone(int qDone) {
		this.qDone = qDone;
	}

	public int getqRemain() {
		return qRemain;
	}

	public void setqRemain(int qRemain) {
		this.qRemain = qRemain;
	}

	public void setDisVaultTab(boolean disVaultTab) {
		this.disVaultTab = disVaultTab;
	}

	public double getSummTotal() {
		return summTotal;
	}

	public void setSummTotal(double summTotal) {
		this.summTotal = summTotal;
	}

	public double getSummRemain() {
		return summRemain;
	}

	public void setSummRemain(double summRemain) {
		this.summRemain = summRemain;
	}

	public int getF1Q() {
		return f1Q;
	}

	public void setF1Q(int f1q) {
		f1Q = f1q;
	}

	public int getF2Q() {
		return f2Q;
	}

	public void setF2Q(int f2q) {
		f2Q = f2q;
	}

	public int getF3Q() {
		return f3Q;
	}

	public void setF3Q(int f3q) {
		f3Q = f3q;
	}

	public int getF4Q() {
		return f4Q;
	}

	public void setF4Q(int f4q) {
		f4Q = f4q;
	}

	public int getF5Q() {
		return f5Q;
	}

	public void setF5Q(int f5q) {
		f5Q = f5q;
	}

	public double getSummPlan() {
		return summPlan;
	}

	public void setSummPlan(double summPlan) {
		this.summPlan = summPlan;
	}

	public double getSummDone() {
		return summDone;
	}

	public void setSummDone(double summDone) {
		this.summDone = summDone;
	}

	public double getSummMaster() {
		return summMaster;
	}

	public void setSummMaster(double summMaster) {
		this.summMaster = summMaster;
	}

	public double getSummOper() {
		return summOper;
	}

	public void setSummOper(double summOper) {
		this.summOper = summOper;
	}

	public double getSummOverdue() {
		return summOverdue;
	}

	public void setSummOverdue(double summOverdue) {
		this.summOverdue = summOverdue;
	}

	public String getFwaers() {
		return fwaers;
	}

	public void setFwaers(String fwaers) {
		this.fwaers = fwaers;
	}

	// **********************************************************************************

	public void prepareCRMHistoryDlg(int index) {
		if (!(index > 0)) {
			index = sfOutputList.indexOf(selectedSf)+1;
		}
		
		if (index > 0) {
			ServFilterOutput wa_sfo = sfoList.get(index - 1);
			crmhBean.setContract_id(wa_sfo.getContract_id());
			// crmhBean.setSfo(wa_sfo);
			crmhBean.getNewServCRMHistoryBean()
					.setTransactionID(transaction_id);
			crmhBean.setTransactionCode(transaction_code);			
			crmhBean.getNewcrmsBean().setTransactionID(transaction_id);
			crmhBean.init();
			crmhBean.setSfo(wa_sfo);
			crmhBean.setTransactionID(transaction_id);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("CrmHistoryForm");
		}
	}

	public void prepareCRMHistoryDlgFromSchedDlg(int index) {
		if (index > 0) {
			ServCRMScheduleOutput wa_scso = schedcallsBean.scsoL.get(index - 1);
			crmhBean.setContract_id(wa_scso.getScs().getContractId());
			// crmhBean.setSfo(wa_sfo);
			crmhBean.getNewServCRMHistoryBean()
					.setTransactionID(transaction_id);
			crmhBean.getNewcrmsBean().setTransactionID(transaction_id);
			crmhBean.init();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("CrmHistoryForm");
			reqCtx.update("ScheduledCallsForm");
		}
	}

	public void prepareCRMScheduleDlg() {
		schedcallsBean.getSearchscs().setBukrs(p_bukrs);
		schedcallsBean.loadBranch();
		schedcallsBean.getSearchscs().setBranchId(p_branch_id);
		schedcallsBean.setTransactionId(transaction_id);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ScheduledCallsForm");
	}

	// ****************************************************************************

	public List<ServFilterOutput> sfoList = new ArrayList<ServFilterOutput>();
	public ServFilterOutput selectedSf = new ServFilterOutput(0);
	public ServFilterOutput sfo = new ServFilterOutput(0);
	public Long p_branch_id;
	public String p_bukrs;
	public Date p_month;
	public boolean p_overdue;
	public int outputLength;
	public List<ServFilterOutput> sfOutputList = new ArrayList<ServFilterOutput>();
	public Contract searchCon = new Contract();
	public String searchCus;
	public String searchAddr;
	public String searchTel;
	public int p_list_type;
	public int p_list_cat;
	public boolean p_catBool;
	public String p_tovSN;
	public Long p_conNum;
	public int p_list_warranty;
	
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************

	public int getP_list_warranty() {
		return p_list_warranty;
	}

	public void setP_list_warranty(int p_list_warranty) {
		this.p_list_warranty = p_list_warranty;
	}

	public String getP_tovSN() {
		return p_tovSN;
	}

	public void setP_tovSN(String p_tovSN) {
		this.p_tovSN = p_tovSN;
	}

	public Long getP_conNum() {
		return p_conNum;
	}

	public void setP_conNum(Long p_conNum) {
		this.p_conNum = p_conNum;
	}

	public int getP_list_cat() {
		return p_list_cat;
	}

	public void setP_list_cat(int p_list_cat) {
		this.p_list_cat = p_list_cat;
	}

	public boolean isP_catBool() {
		return p_catBool;
	}

	public void setP_catBool(boolean p_catBool) {
		this.p_catBool = p_catBool;
	}

	public int getP_list_type() {
		return p_list_type;
	}

	public void setP_list_type(int p_list_type) {
		this.p_list_type = p_list_type;
	}

	public String getSearchAddr() {
		return searchAddr;
	}

	public void setSearchAddr(String searchAddr) {
		this.searchAddr = searchAddr;
	}

	public String getSearchTel() {
		return searchTel;
	}

	public void setSearchTel(String searchTel) {
		this.searchTel = searchTel;
	}

	public String getSearchCus() {
		return searchCus;
	}

	public void setSearchCus(String searchCus) {
		this.searchCus = searchCus;
	}

	public Contract getSearchCon() {
		return searchCon;
	}

	public void setSearchCon(Contract searchCon) {
		this.searchCon = searchCon;
	}

	public List<ServFilterOutput> getSfOutputList() {
		return sfOutputList;
	}

	public void setSfOutputList(List<ServFilterOutput> sfOutputList) {
		this.sfOutputList = sfOutputList;
	}

	public ServFilterOutput getSelectedSf() {
		return selectedSf;
	}

	public void setSelectedSf(ServFilterOutput selectedSf) {
		this.selectedSf = selectedSf;
	}

	public int getOutputLength() {
		return outputLength;
	}

	public void setOutputLength(int outputLength) {
		this.outputLength = outputLength;
	}

	public List<ServFilterOutput> getSfoList() {
		return sfoList;
	}

	public void setSfoList(List<ServFilterOutput> sfoList) {
		this.sfoList = sfoList;
	}

	public ServFilterOutput getSfo() {
		return sfo;
	}

	public Long getP_branch_id() {
		return p_branch_id;
	}

	public void setP_branch_id(Long p_branch_id) {
		this.p_branch_id = p_branch_id;
	}

	public String getP_bukrs() {
		return p_bukrs;
	}

	public void setP_bukrs(String p_bukrs) {
		this.p_bukrs = p_bukrs;
	}

	public Date getP_month() {
		return p_month;
	}

	public void setP_month(Date p_month) {
		this.p_month = p_month;
	}

	public boolean isP_overdue() {
		return p_overdue;
	}

	public void setP_overdue(boolean p_overdue) {
		this.p_overdue = p_overdue;
	}

	public void setSfo(ServFilterOutput sfo) {
		this.sfo = sfo;
	}

	public void preProcessPDF() {

	}

	public void printPDF() {

	}

	// *************************************************************************

	public void postProcessXLSDetail(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		header.setHeightInPoints(30);
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		
		HSSFFont fontHeader = wb.createFont();
		fontHeader.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellStyle.setFont(fontHeader);
		
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		cellStyle2.setDataFormat((short) 2);

		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);
			cell.setCellStyle(cellStyle);
			String value = cell.getStringCellValue();
			value = value.replaceAll("<br/>", " ");
			cell.setCellValue(value);
		}
		
		HSSFCellStyle cellStyle3 = wb.createCellStyle();
		cellStyle3.cloneStyleFrom(cellStyle2);
		cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		//cellStyle3.setShrinkToFit(true);					
		cellStyle3.setWrapText(true);
		
		HSSFFont fontCell = wb.createFont();
		fontCell.setFontHeightInPoints((short)9);
		
		String crmCat[] = {"", "ЗЕЛЕНЫЙ", "ЖЕЛТЫЙ", "КРАСНЫЙ", "ЧЕРНЫЙ"}; 
		
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			if (i > 0) {
				HSSFRow row = sheet.getRow(i);
				
				for (int i2 = 0; i2 < row.getPhysicalNumberOfCells(); i2++) {
					HSSFCell cell = row.getCell(i2);
					
					String value = cell.getStringCellValue();
					value = value.replaceAll("<br/>", " ");
					
					fontCell.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
					fontCell.setColor(HSSFFont.COLOR_NORMAL);
					
					if (i2==1) {
						Branch br = p_branchF4Bean.getL_branch_map().get(p_branch_id);
						if (br != null && !Validation.isEmptyLong(br.getBranch_id()))
							value = value.replaceAll(br.getText45(), " ");
					} else if (i2 >= 6 && i2 <= 10) {
						Long term = Long.parseLong(value);
						if (term >= 0)
							fontCell.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
						if (term > 0)
							fontCell.setColor(HSSFFont.COLOR_RED);
					} else if (i2 == 12 ) {
						int cat = Integer.parseInt(value);
						value = crmCat[cat];
					}
					
					cellStyle3.setFont(fontCell);
					cell.setCellStyle(cellStyle3);
						// System.out.println(cell.get);
					// System.out.println(value);
					// value = value.replace(",", ".");
					// double dvalue = Double.valueOf(value);
					// value = value.replace("\\s","");
					
					value = value.trim();
					if (i2 != 5 && i2 != 13)
						cell.setCellValue(value);
					else 
						cell.setCellValue("");
					
				}
			}
		}				
		HSSFRow row = sheet.getRow(1);
		for (int i2 = 0; i2 < row.getPhysicalNumberOfCells(); i2++) {
			sheet.autoSizeColumn(i2);
		}
	}

}