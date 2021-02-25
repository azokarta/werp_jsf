package accounting.assess;

import f4.BukrsF4;
import f4.ExchangeRateF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.HkontDao;
import general.dao.PayrollDao;
import general.dao.StaffDao;
import general.services.PayrollService;
import general.tables.Bukrs;
import general.tables.ExchangeRate;
import general.tables.Payroll;
import general.tables.Staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;


@ManagedBean(name = "faesBean", eager = true)
@ViewScoped
public class Faes implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FAES";
	private final static Long transaction_id = (long) 194;
	public static Long getTransactionId() {
		return transaction_id;
	}
	//private final static Long read = (long) 1;
	//private final static Long write = (long) 2; 
		
	//***************************Application Context********************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;
	}
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	//******************************************************************
	//**********************Getter Setter for sessionData**********************
	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
		return userData;
	}
	public void setUserData(User userData) {
		this.userData = userData;
	}
	//*************************************************************************	
    //*****************************Branch**********************************
    //**********************************************************************
    //*****************************ExchangeRate*****************************
  	@ManagedProperty(value="#{exchangeRateF4Bean}")
  	private ExchangeRateF4 p_exchangeRateF4Bean;	
	public ExchangeRateF4 getP_exchangeRateF4Bean() {
		return p_exchangeRateF4Bean;
	}
	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
		this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
	}
	
	private List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
	public List<ExchangeRate> getL_er() {
		return l_er;
	}
	public void setL_er(List<ExchangeRate> l_er) {
		this.l_er = l_er;
	}
	//**********************************************************************
	// *****************************Bukrs**********************************
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
	//Managed Beans end*****************************************************
	Payroll new_prl = new Payroll();
	
	public Payroll getNew_prl() {
		return new_prl;
	}
	public void setNew_prl(Payroll new_prl) {
		this.new_prl = new_prl;
	}

	private List<String> l_waers = new ArrayList<String>();
	// *******************************Init method***********************************	

	public List<String> getL_waers() {
		return l_waers;
	}
	public void setL_waers(List<String> l_waers) {
		this.l_waers = l_waers;
	}
	
	@PostConstruct
	public void init() {
		try
		{
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) { 
	            return; // Skip ajax requests.
	        }
			if (!(userData.getUserid()==1L))
			{
				//toMainPage();
			}
			PermissionController.canRead(userData,Faes.transaction_id);
			if (!(new_prl.getBukrs()==null || new_prl.getBukrs().length()<4 || new_prl.getStaff_id()==null || new_prl.getDrcrk()==null 
					|| new_prl.getDrcrk().length()<1 || new_prl.getBranch_id()==null))
			{
				//System.out.println(bukrs+123);
				StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
				PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");
				searchStaff = staffDao.find(new_prl.getStaff_id());				
				HkontDao hkontDao = (HkontDao) appContext.getContext().getBean("hkontDao");
				l_waers = hkontDao.findWaersByBukrsBranchId(new_prl.getBukrs(),new_prl.getBranch_id());
				l_waers.addAll(payrollDao.findWaersByStaffId(new_prl.getStaff_id()));
				Set<String> hs = new HashSet<>();
				hs.addAll(l_waers);
				l_waers.clear();
				l_waers.addAll(hs);
				
				saveDisable = false;
				if (new_prl.getDrcrk().equals("S"))
				{
					if (userData.getU_language().equals("ru")){
						breadcrumb = "Начисление ЗП вручную";						
					}
					else  {
						breadcrumb = "Manual salary accrual";						
					}
				}
				else
				{
					if (userData.getU_language().equals("ru")){
						breadcrumb = "Удержание ЗП вручную";					
					}
					else  {
						breadcrumb = "Manual salary deduction";						
					}
				}
				if (l_waers.size()==0)
				{
					
				}
			}
			else
				saveDisable = true;
			
			
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	// *****************************************************************************	
	
	private String breadcrumb = "";
	
  	public String getBreadcrumb() {
		return breadcrumb;
	}
	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}
	public void toMainPage() {
		try {
			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath()  + "/general/mainPage.xhtml");
		}
		catch (Exception ex)
		{
			 
			addMessage("Info",ex.getMessage());  
		}
	}	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");
    } 	
	
	//*****************************************************************	
	//********************************************************************************************

	//********************************************************************************************	

	//**************************Customer*******************************


	private Staff searchStaff = new Staff();
	public Staff getSearchStaff() {
		return searchStaff;
	}
	public void setSearchStaff(Staff searchStaff) {
		this.searchStaff = searchStaff;
	}

	

	

  	//******************************************************************************************* 	
	//*******************************Check and Save********************************************************
	private boolean saveDisable = true;
	public boolean isSaveDisable() {
		return saveDisable;
	}
	public void setSaveDisable(boolean saveDisable) {
		this.saveDisable = saveDisable;
	}
	
	//Zapros na nachislenie ili uderzhanie
	public void to_save()
	{
		try
		{		
			if (!(new_prl.getBukrs()==null || new_prl.getBukrs().length()<4 || new_prl.getStaff_id()==null || new_prl.getDrcrk()==null 
					|| new_prl.getDrcrk().length()<1 || new_prl.getBranch_id()==null)  && searchStaff!=null && new_prl.getWaers()!=null && new_prl.getDmbtr()>0)
			{
				PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");
				PermissionController.canWrite(userData, Faes.transaction_id);
				
				if (new_prl.getDrcrk().equals("S") && payrollDao.dynamicFindCountPayroll(" staff_id ="+new_prl.getStaff_id()+" and approve=6 and bukrs="+new_prl.getBukrs())>0)
				{
					throw new DAOException(Helper.getErrorMessage(77L, userData.getU_language()));
				}
				
				if (new_prl.getDrcrk().equals("H") && payrollDao.dynamicFindCountPayroll(" staff_id ="+new_prl.getStaff_id()+" and approve=9 and bukrs="+new_prl.getBukrs())>0)
				{
					throw new DAOException(Helper.getErrorMessage(77L, userData.getU_language()));
				}
				
				
				Calendar curDate = Calendar.getInstance();
				new_prl.setGjahr(curDate.get(Calendar.YEAR));
				new_prl.setMonat(curDate.get(Calendar.MONTH)+1);				
				new_prl.setPayroll_date(curDate.getTime());
				new_prl.setBldat(curDate.getTime());
				
				if (new_prl.getDrcrk().equals("S"))
				{
					new_prl.setApprove(6);
				}
				else
				{
					new_prl.setApprove(9);
				}
				new_prl.setCreated_by(userData.getUserid());
				PayrollService payrollService = (PayrollService) appContext.getContext().getBean("payrollService");
				payrollService.createNew(new_prl,userData.getUserid(),false,Faes.transaction_code,0);
				addMessage("Info","Запрос отправлен.");
				new_prl.setApprove(0);
				new_prl.setDmbtr(0);
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:dmbtr");
				
			}

		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	
	//*****************************************************************************************************

}
