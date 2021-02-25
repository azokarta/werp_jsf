package service.applications;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.filter.branch.BranchBukrsFilter;
import general.filter.branch.BranchBusinessAreaFilter;
import general.filter.branch.BranchMatchAll;
import general.filter.branch.BranchParentFilter;
import general.filter.branch.BranchTypeFilter;
import general.output.tables.ServAppReportOutput;
import general.services.reports.ServAppReportService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.ServiceApplication;
import general.tables.Staff;
import user.User;

@ManagedBean(name = "sarepBean", eager = true)
@ViewScoped
public class ServAppReport {
	private final static String transaction_code = "SAREP";
	private final static Long transaction_id = 518L;

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

			srSearch = new ServAppReportOutput();
			srSearch.setServapp(new ServiceApplication());
			
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
		
		branch_list = loadBranchListByUserBranch(srSearch.getBukrs(), branchId, BusinessArea.AREA_SERVICE);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
		reqCtx.update("form:branchMulSelect");		
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
			srOutOper.clear();
			srOutBranch = getSARepService().getServAppReportBranch(srSearch, selBranches, userData, transaction_id);
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
//			srOutGroup.clear();
//			srOutGroup = getConRepService().getSalesReportGroup(srSearch, userData, transaction_id);
			//srOutOper = getSARepService().getSalesReportDealer(srSearch, userData, transaction_id);
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
//			srSearch.setParentPyramidId(inGroupId);
			srOutOper.clear();
//			srOutOper = getConRepService().getSalesReportDealerGroup(srSearch, userData, transaction_id);
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
			
			if (srSearch.getServapp().getAdate() == null)			
				throw new DAOException("Please select Month!");
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	private ServAppReportService getSARepService() {
		return (ServAppReportService) appContext.getContext().getBean("sarepService");
	}
	
	// *************************************************************************
	public ServAppReportOutput srSearch = new ServAppReportOutput();
	public List<ServAppReportOutput> srOutBranch = new ArrayList<ServAppReportOutput>();
	public List<ServAppReportOutput> srOutOper = new ArrayList<ServAppReportOutput>();
	private int tabindex = 0;
	
	public List<ServAppReportOutput> getSrOutOper() {
		return srOutOper;
	}

	public void setSrOutOper(List<ServAppReportOutput> srOutOper) {
		this.srOutOper = srOutOper;
	}

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public List<ServAppReportOutput> getSrOutBranch() {
		return srOutBranch;
	}

	public void setSrOutBranch(List<ServAppReportOutput> srOutBranch) {
		this.srOutBranch = srOutBranch;
	}


	public ServAppReportOutput getSrSearch() {
		return srSearch;
	}

	public void setSrSearch(ServAppReportOutput srSearch) {
		this.srSearch = srSearch;
	}

}
