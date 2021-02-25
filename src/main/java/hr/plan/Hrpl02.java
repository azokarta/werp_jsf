package hr.plan;

import java.util.ArrayList;
import java.util.List;

import f4.BranchF4;
import f4.BukrsF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.SalePlanDao;
import general.services.SalePlanService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.SalePlan;
import general.tables.search.SalePlanSearch;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name="hrpl02Bean")
@ViewScoped
public class Hrpl02 {
	
	private final static Long transactionId = 69L;
	private final static String transactionCode = "HRPL02";
	
	private String bukrs;
	private List<Bukrs> bukrsList;
	private List<Branch> branchList;
	private Long branchId = 0L;
	private List<SalePlan> planList = new ArrayList<SalePlan>();
	private SalePlanSearch searchModel = new SalePlanSearch();
	
	public SalePlanSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SalePlanSearch searchModel) {
		this.searchModel = searchModel;
	}

	@PostConstruct
	public void init(){
		try{
			PermissionController.canRead(userData, transactionId);
			bukrsList = bukrsF4Bean.getBukrs_list();
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void loadBranch(){
		branchList = new ArrayList<Branch>();
		for(Branch b: branchF4Bean.getBranch_list()){
			if(bukrs.equals(b.getBukrs())){
				branchList.add(b);
			}
		}
	}

	public void update(SalePlan sp){
		try{
			PermissionController.canWrite(userData, transactionId);
			sp.setUpdated_by(userData.getUserid());
			SalePlanService spService = (SalePlanService)appContext.getContext().getBean("salePlanService");
			spService.updateSalePlan(sp);
			GeneralUtil.addSuccessMessage("Изменение сохранено успешно");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void search(){
		try{
			SalePlanDao spDao = (SalePlanDao)appContext.getContext().getBean("salePlanDao");
			if(branchId == 0){
				//throw new DAOException("Выберите филиал");
			}
			planList = spDao.findAll(this.searchModel.getCondition());
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public List<Bukrs> getBukrsList() {
		return bukrsList;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}
	
	

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}



	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
		return userData;
	}
	public void setUserData(User userData) {
		this.userData = userData;
	}
	
	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 bukrsF4Bean;

	public BukrsF4 getBukrsF4Bean() {
		return bukrsF4Bean;
	}

	public void setBukrsF4Bean(BukrsF4 b) {
		this.bukrsF4Bean = b;
	}
	
	
	public List<SalePlan> getPlanList() {
		return planList;
	}

	@ManagedProperty(value="#{branchF4Bean}")
	private BranchF4 branchF4Bean;
	public BranchF4 getBranchF4Bean() {
		return branchF4Bean;
	}
	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	String breadcrumb = "Отдел кадров > План > Изменить план";
	public String getBreadcrumb() {
		return breadcrumb;
	}
}
