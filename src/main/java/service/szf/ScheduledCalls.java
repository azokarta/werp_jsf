package service.szf;

import f4.BranchF4;
import f4.BukrsF4;
import general.AppContext;
import general.GeneralUtil;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.ServCRMScheduleDao;
import general.dao.StaffDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchTypeFilter;
import general.output.tables.ServCRMScheduleOutput;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.ServCRMSchedule;

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

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "schedcallsBean")
@ViewScoped
public class ScheduledCalls implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6374930283517328436L;

	@PostConstruct
	public void init() {
		//Search();
		searchscs = new ServCRMSchedule();
		searchscs.setScheduledDate(Calendar.getInstance().getTime());		
	}

	public void Search() {
		try {
			ServCRMScheduleDao scsDao = (ServCRMScheduleDao) appContext
					.getContext().getBean("servCRMScheduleDao");
			
			String cond = " a.bukrs = " + searchscs.getBukrs()
					+ " and a.branchId = " + searchscs.getBranchId()
					+ " and a.status = " + ServCRMSchedule.STATUS_NEW;

//			SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yy");
//			SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
//			SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-DD");
			
			Date d = searchscs.getScheduledDate();
			Calendar cd = Calendar.getInstance();
			cd.setTime(d);
			Calendar cdf = Calendar.getInstance();
			cdf.setTime(GeneralUtil.getFirstDateOfMonth(d));
			Calendar cdl = Calendar.getInstance();
			cdl.setTime(GeneralUtil.getLastDateOfMonth(d));
			cdl = GeneralUtil.getLastDayOfMonth(cdl);
			
//			String ds = format1.format(cd.getTime());
//			String df = format1.format(cdf.getTime());
//			String dl = format1.format(cdl.getTime());
			
//			Timestamp ds = new Timestamp(cd.getTime().getTime());
//			Timestamp df = new Timestamp(cdf.getTime().getTime());
//			Timestamp dl = new Timestamp(cdl.getTime().getTime());
			
			java.sql.Date ds = new java.sql.Date(cd.getTime().getTime());
			java.sql.Date df = new java.sql.Date(cdf.getTime().getTime());
			java.sql.Date dl = new java.sql.Date(cdl.getTime().getTime());
			
			if (!bymonth)
				cond += " and a.scheduledDate = '" + ds + "'";
			else
				cond += " and (a.scheduledDate between '" + df 
					+ "' and '" + dl + "')";
			
			if (myOnly)
				cond += " and (a.staffId = " + userData.getStaff().getStaff_id() + ")";
			
			//System.out.println(cond);
			
			scsL = scsDao.findAll(cond);
			System.out.println("Total found Scheduled Calls number: " + scsL.size());

			LoadScsOutputList();
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("ScheduledCallsForm");
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void LoadScsOutputList() {
		scsoL = new ArrayList<ServCRMScheduleOutput>();
		int i = 1;
		for (ServCRMSchedule scs:scsL) {
			ServCRMScheduleOutput scso = new ServCRMScheduleOutput(i);
			
			scso.setScs(scs);
			
			ContractDao conDao = (ContractDao) appContext.getContext().getBean(
					"contractDao");
			scso.setContract(conDao.find(scs.getContractId()));

			CustomerDao cusDao = (CustomerDao) appContext.getContext().getBean(
					"customerDao");
			scso.setCustomer(cusDao.find(scso.getContract().getCustomer_id()));
			
			StaffDao stfDao = (StaffDao) appContext.getContext()
					.getBean("staffDao");
			scso.setStaff(stfDao.find(scs.getStaffId()));
			
			scsoL.add(scso);
			i++;
		}
	}
	
	public void loadBranch() {
		branch_list = new ArrayList<Branch>();
		// System.out.println(p_bukrs);
		branch_list = loadBranchList(searchscs.getBukrs());
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("ScheduledCallsForm");
	}

	public List<Branch> loadBranchList(String a_bukr) {
		BranchMatchAll bma = new BranchMatchAll();
		bma.addFilter(new BranchBukrsFilter(a_bukr));
		bma.addFilter(new BranchBusinessAreaFilter(BusinessArea.AREA_SERVICE));
		bma.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
		return bma.filterBranch(p_branchF4Bean.getBranch_list());
	}

	// ****************************************************************************
	public List<ServCRMSchedule> scsL;
	public List<ServCRMScheduleOutput> scsoL;
	public ServCRMScheduleOutput selScso;
	public ServCRMSchedule searchscs;
	public boolean bymonth;
	public boolean myOnly;
	public Long transactionId;
	
	// ****************************************************************************
	public ServCRMScheduleOutput getSelScso() {
		return selScso;
	}

	public void setSelScso(ServCRMScheduleOutput selScso) {
		this.selScso = selScso;
	}
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	
	public boolean isMyOnly() {
		return myOnly;
	}

	public void setMyOnly(boolean myOnly) {
		this.myOnly = myOnly;
	}

	public List<ServCRMScheduleOutput> getScsoL() {
		return scsoL;
	}

	public void setScsoL(List<ServCRMScheduleOutput> scsoL) {
		this.scsoL = scsoL;
	}
	public boolean isBymonth() {
		return bymonth;
	}

	public void setBymonth(boolean bymonth) {
		this.bymonth = bymonth;
	}

	public ServCRMSchedule getSearchscs() {
		return searchscs;
	}

	public void setSearchscs(ServCRMSchedule searchscs) {
		this.searchscs = searchscs;
	}

	public List<ServCRMSchedule> getScsL() {
		return scsL;
	}

	public void setScsL(List<ServCRMSchedule> scsL) {
		this.scsL = scsL;
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

	// **********************ServiceNewCRMHistory***************************
	@ManagedProperty(value = "#{newServCRMHistoryBean}")
	private NewServCrmHistory newServCRMHistoryBean;

	public NewServCrmHistory getNewServCRMHistoryBean() {
		return newServCRMHistoryBean;
	}

	public void setNewServCRMHistoryBean(NewServCrmHistory newServCRMHistoryBean) {
		this.newServCRMHistoryBean = newServCRMHistoryBean;
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
}
