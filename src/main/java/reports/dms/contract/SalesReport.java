package reports.dms.contract;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ContractStatusF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchParentFilter;
import general.filter.branch.BranchTypeFilter;
import general.services.CustomerService;
import general.services.reports.ContractReportService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.Customer;
import general.tables.Staff;
import general.tables.report.SalesReportOutput;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import user.User;
import datamodels.ContractModel;

@ManagedBean(name = "salesrepBean", eager = true)
@ViewScoped
public class SalesReport {
	private final static String transaction_code = "SALESREP";
	private final static Long transaction_id = 457L;

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// *******************************************************************
	// ************************** BranchF4 ******************************
	// ******************************************************************
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 p_branchF4Bean;

	public BranchF4 getP_branchF4Bean() {
		return p_branchF4Bean;
	}

	public void setP_branchF4Bean(BranchF4 p_branchF4Bean) {
		this.p_branchF4Bean = p_branchF4Bean;
	}

	List<Branch> branch_list = new ArrayList<Branch>();
	List<String> selBranches = new ArrayList<String>();
	
	public List<String> getSelBranches() {
		return selBranches;
	}

	public void setSelBranches(List<String> selBranches) {
		this.selBranches = selBranches;
	}
	
	public List<Branch> getBranch_list() {
		return branch_list;
	}

	// ******************************************************************
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
	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	// **************************Employee*******************************
	private List<Staff> p_staff_list = new ArrayList<Staff>();

	public List<Staff> getP_staff_list() {
		return p_staff_list;
	}

	public void setP_staff_list(List<Staff> p_staff_list) {
		this.p_staff_list = p_staff_list;
	}

	// *********************************************************************
	@PostConstruct
	public void init() {
		if (GeneralUtil.isAjaxRequest())
			return;

		try {
			PermissionController.canRead(userData, this.transaction_id);

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
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		} catch (DAOException ex) {
			GeneralUtil.addMessage("Error", ex.getMessage());
			GeneralUtil.doRedirect("/general/mainPage.xhtml");
		}
	}
	
	// *************************************************************************

	public void loadBranch2() {
		branch_list = new ArrayList<Branch>();
		
		Long branchId = userData.getBranch_id();
		if (userData.isMain() || userData.isSys_admin()) {
			if (srSearch.getBukrs().equals("1000"))
				branchId = Branch.AURA_MAIN_BRANCH_ID;
			else if (srSearch.getBukrs().equals("2000"))
				branchId = Branch.GREEN_LIGHT_MAIN_BRANCH_ID;
		}  
		
//		branch_list = loadBranchListByUserBranch(srSearch.getBukrs(), branchId, BusinessArea.AREA_ALL_EXCEPT_SERVICE);

		branch_list = getUserBranchList(srSearch.getBukrs());
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
		reqCtx.update("form:branchMulSelect");		
	}
	public List<Branch> getUserBranchList(String a_bukrs){
		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		if (userData.isSys_admin()) return brDao.findUserBranchesDmsc01Admin(a_bukrs, userData.getUserid());
		else return brDao.findUserBranchesDmsc01(a_bukrs, userData.getUserid());		
		 
	}
	public List<Branch> loadBranchListByUserBranch(String a_bukr, Long branchId, int ba) {
		List<Branch> output = new ArrayList<Branch>();
		BranchMatchAll bf = new BranchMatchAll();
		bf.addFilter(new BranchBukrsFilter(a_bukr));

		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		Branch inBranch = brDao.find(branchId);
		
		List<Branch> brL  = new ArrayList<Branch>();
		if (inBranch.getType() != Branch.TYPE_BRANCH  || inBranch.getBusiness_area_id() == 0) {
			bf.addFilter(new BranchParentFilter(inBranch.getBranch_id()));
			brL = bf.filterBranch(p_branchF4Bean.getBranch_list());
		} else bf.addFilter(new BranchParentFilter(inBranch.getParent_branch_id()));
		
		bf.addFilter(new BranchBusinessAreaFilter(ba));
		bf.addFilter(new BranchTypeFilter(Branch.TYPE_BRANCH));
	
		output = bf.filterBranch(p_branchF4Bean.getBranch_list());
		
		for (Branch br:brL) {
			if (br.getType() != Branch.TYPE_BRANCH || br.getBusiness_area_id() == 0)
				output.addAll(loadBranchListByUserBranch(a_bukr, br.getBranch_id(), ba));			
		}		
		return output;
	}
	
	// *************************************************************************
	
	public void showReport() {
		try {
			validateSearch();
			srOutBranch.clear();
			srOutGroup.clear();
			srOutDealer.clear();
			srOutBranch = getConRepService().getSalesReportBranch(srSearch, selBranches, userData, transaction_id);
			tabindex = 0;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tabView");
			//reqCtx.update("form:tabView:branchTable");
		} catch (DAOException ex) {
			ex.getStackTrace();
			GeneralUtil.addMessage(ex.getMessage(),"");
		}
	}
	
	public void showReportGroup(Long inBranchId) {
		try {			
			validateSearch();
			srSearch.getBranch().setBranch_id(inBranchId);
			srOutGroup.clear();
			srOutGroup = getConRepService().getSalesReportGroup(srSearch, userData, transaction_id);
			srOutDealer = getConRepService().getSalesReportDealer(srSearch, userData, transaction_id);
			tabindex = 1;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		} catch (DAOException ex) {
			ex.getStackTrace();
			GeneralUtil.addMessage(ex.getMessage(),"");
		}
	}
	
	public void showReportStaffByGroup(Long inGroupId) {
		try {			
			validateSearch();
			srSearch.setParentPyramidId(inGroupId);
			srOutDealer.clear();
			srOutDealer = getConRepService().getSalesReportDealerGroup(srSearch, userData, transaction_id);
			tabindex = 2;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		} catch (DAOException ex) {
			ex.getStackTrace();
			GeneralUtil.addMessage(ex.getMessage(),"");
		}
	}
	
	private void validateSearch() throws DAOException {
		try {
			//System.out.println("Bukrs: " + srSearch.getBukrs());
			if (Validation.isEmptyString(srSearch.getBukrs())
					|| srSearch.getBukrs().equals("0")) {			
				throw new DAOException("Please select Company!");
			}
			
			if (selBranches.size() == 0) 
				throw new DAOException("Please select Branch!");
			
			if (srSearch.getContract().getContract_date() == null)			
				throw new DAOException("Please select Month!");
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	private ContractReportService getConRepService() {
		return (ContractReportService) appContext.getContext().getBean("salesReportService");
	}
	
	// *************************************************************************
	public SalesReportOutput srSearch = new SalesReportOutput();
	public List<SalesReportOutput> srOutBranch = new ArrayList<SalesReportOutput>();
	public List<SalesReportOutput> srOutGroup = new ArrayList<SalesReportOutput>();
	public List<SalesReportOutput> srOutDealer = new ArrayList<SalesReportOutput>();
	private int tabindex = 0;
	
	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public List<SalesReportOutput> getSrOutBranch() {
		return srOutBranch;
	}

	public void setSrOutBranch(List<SalesReportOutput> srOutBranch) {
		this.srOutBranch = srOutBranch;
	}

	public List<SalesReportOutput> getSrOutGroup() {
		return srOutGroup;
	}

	public void setSrOutGroup(List<SalesReportOutput> srOutGroup) {
		this.srOutGroup = srOutGroup;
	}

	public List<SalesReportOutput> getSrOutDealer() {
		return srOutDealer;
	}

	public void setSrOutDealer(List<SalesReportOutput> srOutDealer) {
		this.srOutDealer = srOutDealer;
	}

	public SalesReportOutput getSrSearch() {
		return srSearch;
	}

	public void setSrSearch(SalesReportOutput srSearch) {
		this.srSearch = srSearch;
	}

}
