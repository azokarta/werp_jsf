package accounting.assess;

import f4.BukrsF4;
import f4.ExchangeRateF4;
import general.AppContext;
import general.GeneralUtil;
import general.Helper;
import general.dao.DAOException;
import general.dao.HkontDao;
import general.dao.PayrollDao;
import general.dao.StaffDao;
import general.dao.UserRoleDao;
import general.services.FinanceServicePayroll;
import general.tables.Bukrs;
import general.tables.ExchangeRate;
import general.tables.Payroll;
import general.tables.Staff;
import general.tables.UserRole;

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


@ManagedBean(name = "fahrbBean", eager = true)
@ViewScoped
public class Fahrb implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FAHRB";
	private final static Long transaction_id = (long) 474;
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
			
			if (!canRead())
			{
				toMainPage();
			}
			if (!(userData.getUserid()==1L))
			{
				//toMainPage();
			}
//			PermissionController.canRead(userData,Fahrb.transaction_id);
			if (!(new_prl.getBukrs()==null || new_prl.getBukrs().length()<4 || new_prl.getStaff_id()==null || new_prl.getBranch_id()==null))
			{
				StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
				searchStaff = staffDao.find(new_prl.getStaff_id());		
				PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");							
				HkontDao hkontDao = (HkontDao) appContext.getContext().getBean("hkontDao");
				l_waers = hkontDao.findWaersByBukrsBranchId(new_prl.getBukrs(),new_prl.getBranch_id());
				l_waers.addAll(payrollDao.findWaersByStaffId(new_prl.getStaff_id()));
				Set<String> hs = new HashSet<>();
				hs.addAll(l_waers);
				l_waers.clear();
				l_waers.addAll(hs);
				
				saveDisable = false;
				loadItems();
				breadcrumb = "Перевод долг сотрудника";

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
	private int oper_type = 0;	
	public int getOper_type() {
		return oper_type;
	}
	public void setOper_type(int oper_type) {
		this.oper_type = oper_type;
	}

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




	

	

  	//******************************************************************************************* 	
	//*******************************Check and Save********************************************************
	private boolean saveDisable = true;
	public boolean isSaveDisable() {
		return saveDisable;
	}
	public void setSaveDisable(boolean saveDisable) {
		this.saveDisable = saveDisable;
	}
	
	public void to_save()
	{
		try
		{		
//			if (!(new_prl.getBukrs()==null || new_prl.getBukrs().length()<4 || new_prl.getStaff_id()==null || new_prl.getBranch_id()==null)  && new_prl.getWaers()!=null && new_prl.getDmbtr()>0)
//			{
//				if (!canWrite(oper_type))
//				{
//					throw new DAOException(Helper.getErrorMessage(102L, userData.getU_language()));
//				}
//				FinanceServicePayroll financeServicePayroll = (FinanceServicePayroll) appContext.getContext().getBean("financeServicePayroll");
//				loadItems();
//				if (new_prl.getWaers()==null || new_prl.getWaers().length()==0)
//				{
//					throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
//				}
//				if (new_prl.getDmbtr()==0 || new_prl.getDmbtr()<0)
//				{
//					throw new DAOException(Helper.getErrorMessage(61L, userData.getU_language()));
//				}
//				
//				//<f:selectItem itemValue="1" itemLabel="Оплатить долг"/>				
//				if (oper_type==1)
//				{
//					if (p_outputTablePayroll==null || p_outputTablePayroll.l_payroll_schet==null)
//					{
//						throw new DAOException(Helper.getErrorMessage(70L, userData.getU_language()));
//					}
//					else if (p_outputTablePayroll.l_payroll_schet!=null)
//					{
//						for (Payroll wa_prl:p_outputTablePayroll.l_payroll_schet)
//						{
//							if (new_prl.getWaers().equals(wa_prl.getWaers()) && new_prl.getDmbtr()>wa_prl.getDmbtr())
//							{
//								throw new DAOException(Helper.getErrorMessage(71L, userData.getU_language()));
//							}
//						}
//					}
//					
//					
//					if (p_outputTablePayroll==null || p_outputTablePayroll.l_payroll_dolg==null)
//					{
//						throw new DAOException(Helper.getErrorMessage(76L, userData.getU_language()));
//					}
//					for (Payroll wa_prl:p_outputTablePayroll.l_payroll_dolg)
//					{
//						if (new_prl.getWaers().equals(wa_prl.getWaers()) && new_prl.getDmbtr()<wa_prl.getDmbtr())
//						{
//							throw new DAOException(Helper.getErrorMessage(72L, userData.getU_language()));
//						}
//					}
////					System.out.println("Operation 1 starts");
//					financeServicePayroll.fahrbSave(new_prl.getBukrs(), userData.getUserid(), new_prl.getBranch_id(), oper_type, new_prl);
//					
//				}
//				//<f:selectItem itemValue="2" itemLabel="Перевод с долг. счета на баланс"/> 
//				
//				else if (oper_type==2)
//				{
//					if (p_outputTablePayroll==null || p_outputTablePayroll.l_payroll_dolg==null)
//					{
//						throw new DAOException(Helper.getErrorMessage(73L, userData.getU_language()));
//					}
//					else if (p_outputTablePayroll.l_payroll_dolg!=null)
//					{
//						if (p_outputTablePayroll.l_payroll_dolg.size()==0)
//						{
//							throw new DAOException(Helper.getErrorMessage(73L, userData.getU_language()));
//						}
//						int count=0;
//						for (Payroll wa_prl:p_outputTablePayroll.l_payroll_dolg)
//						{
//							if (new_prl.getWaers().equals(wa_prl.getWaers()))
//							{
//								if (wa_prl.getDmbtr()>0)
//								{
//									count++;
//									if (wa_prl.getDmbtr()>=new_prl.getDmbtr())
//									{
////										System.out.println("Operation 2 starts");
//										financeServicePayroll.fahrbSave(new_prl.getBukrs(), userData.getUserid(), new_prl.getBranch_id(), oper_type, new_prl);
//									}
//									else
//									{
//										throw new DAOException(Helper.getErrorMessage(74L, userData.getU_language()));
//									}
//									
//								}
//								
//							}
//						}
//						if (count==0)
//						{
//							throw new DAOException(Helper.getErrorMessage(73L, userData.getU_language()));
//						}
//					}
//					
//				}
//				//<f:selectItem itemValue="3" itemLabel="Перевод с баланс. счета на долг"/> 
//				else if (oper_type==3)
//				{
//					if (p_outputTablePayroll==null || p_outputTablePayroll.l_payroll_schet==null)
//					{
//						throw new DAOException(Helper.getErrorMessage(75L, userData.getU_language()));
//					}
//					else if (p_outputTablePayroll.l_payroll_schet!=null)
//					{
//						
//						if (p_outputTablePayroll.l_payroll_schet.size()==0)
//						{
//							throw new DAOException(Helper.getErrorMessage(75L, userData.getU_language()));
//						}
//						int count=0;
//						for (Payroll wa_prl:p_outputTablePayroll.l_payroll_schet)
//						{
//							if (new_prl.getWaers().equals(wa_prl.getWaers()))
//							{
//								if (wa_prl.getDmbtr()<0)
//								{
//									count++;
//									double minussumma = wa_prl.getDmbtr()* -1;
//									if (minussumma>=new_prl.getDmbtr())
//									{
////										System.out.println("Operation 3 starts");
//										financeServicePayroll.fahrbSave(new_prl.getBukrs(), userData.getUserid(), new_prl.getBranch_id(), oper_type, new_prl);
//									}
//									else
//									{
//										throw new DAOException(Helper.getErrorMessage(74L, userData.getU_language()));
//									}
//								}
//								
//							}
//						}
//						if (count==0)
//						{
//							throw new DAOException(Helper.getErrorMessage(75L, userData.getU_language()));
//						}
//					}
//					
//				}
//				
//				
////				Calendar curDate = Calendar.getInstance();
////				new_prl.setGjahr(curDate.get(Calendar.YEAR));
////				new_prl.setMonat(curDate.get(Calendar.MONTH)+1);				
////				new_prl.setPayroll_date(curDate.getTime());
////				new_prl.setBldat(curDate.getTime());
////				
////				if (new_prl.getDrcrk().equals("S"))
////				{
////					new_prl.setApprove(6);
////				}
////				else
////				{
////					new_prl.setApprove(9);
////				}
////				new_prl.setCreated_by(userData.getUserid());
////				PayrollService payrollService = (PayrollService) appContext.getContext().getBean("payrollService");
////				payrollService.createNew(new_prl,userData.getUserid(),false,Fahrb.transaction_code,0);
////				addMessage("Info","Запрос отправлен.");
////				new_prl.setApprove(0);
////				new_prl.setDmbtr(0);
////				
//				
//				Payroll wa_prl =  new Payroll();
//				wa_prl.setBukrs(new_prl.getBukrs());
//				wa_prl.setBranch_id(new_prl.getBranch_id());
//				wa_prl.setStaff_id(new_prl.getStaff_id());
//				new_prl = wa_prl;
//				loadItems();
//				
//				RequestContext reqCtx = RequestContext.getCurrentInstance();
//				reqCtx.update("form");
//				
//			}

		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	
	//*****************************************************************************************************
	private Calendar curDate = Calendar.getInstance();
	private Staff searchStaff = new Staff();
	public Staff getSearchStaff() {
		return searchStaff;
	}
	public void setSearchStaff(Staff searchStaff) {
		this.searchStaff = searchStaff;
	}
	public void loadItems(){
		try{
			//List<Salary> l_salary = new ArrayList<Salary>();
			p_outputTablePayroll.l_payroll_schet = new ArrayList<Payroll>();
			p_outputTablePayroll.l_payroll_deposit = new ArrayList<Payroll>();
			p_outputTablePayroll.l_payroll_avans_zapros = new ArrayList<Payroll>();			
			p_outputTablePayroll.l_payroll_zablok = new ArrayList<Payroll>();
			p_outputTablePayroll.l_payroll_dolg = new ArrayList<Payroll>();
			//List<Payroll> l_payroll_avans_odob = new ArrayList<Payroll>();
			
			PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");		
			List<Long> l_staff_id = new ArrayList<Long>();
			l_staff_id.add(new_prl.getStaff_id());
			
			List<Payroll> l_payroll = new ArrayList<Payroll>();
			List<Payroll> l_payroll1 = new ArrayList<Payroll>();
			List<Payroll> l_payroll2 = new ArrayList<Payroll>();
			List<Payroll> l_payroll3 = new ArrayList<Payroll>();
			List<Payroll> l_payroll4 = new ArrayList<Payroll>();
			List<Payroll> l_payroll5 = new ArrayList<Payroll>();
			l_payroll1 = payrollDao.findByBukrsBranchAllSchet(l_staff_id, GeneralUtil.getSQLDate(curDate),new_prl.getBukrs());
			p_outputTablePayroll.l_payroll_schet.addAll(l_payroll1);
			
//			l_payroll2 = payrollDao.findByBukrsBranchAllDeposit(l_staff_id,new_prl.getBukrs());
//			p_outputTablePayroll.l_payroll_deposit.addAll(l_payroll2);
//			
//			l_payroll3 = payrollDao.findByBukrsBranchAvansZapros(l_staff_id,  GeneralUtil.getSQLDate(curDate),new_prl.getBukrs());
//			p_outputTablePayroll.l_payroll_avans_zapros.addAll(l_payroll3);
//			
//			l_payroll4 = payrollDao.findByBukrsBranchAllZablok(l_staff_id,  GeneralUtil.getSQLDate(curDate),new_prl.getBukrs());
//			p_outputTablePayroll.l_payroll_zablok.addAll(l_payroll4);
			
			l_payroll5 = payrollDao.findByBukrsBranchAllDolg(l_staff_id,  GeneralUtil.getSQLDate(curDate),new_prl.getBukrs());
			p_outputTablePayroll.l_payroll_dolg.addAll(l_payroll5);
			l_payroll.addAll(l_payroll1);
			l_payroll.addAll(l_payroll2);
			l_payroll.addAll(l_payroll3);
			l_payroll.addAll(l_payroll4);
			l_payroll.addAll(l_payroll5);
			
			for (Payroll wa_prl:l_payroll)
			{
				wa_prl.setBukrs(new_prl.getBukrs());
			}
			
			
			
			
			
			
			
			
			//System.out.println(count);
		}
		catch (DAOException ex)
		{
			addMessage("Info  ",ex.getMessage());  
		}
		
	}
	
private OutputTablePayroll p_outputTablePayroll = new OutputTablePayroll();	
	
	

	public OutputTablePayroll getP_outputTablePayroll() {
		return p_outputTablePayroll;
	}

	public void setP_outputTablePayroll(OutputTablePayroll p_outputTablePayroll) {
		this.p_outputTablePayroll = p_outputTablePayroll;
	}

	public class OutputTablePayroll{
		public OutputTablePayroll()
		{
			
		}
		private List<Payroll> l_payroll_schet = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_deposit = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_zablok = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_avans_zapros = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_avans_odob = new ArrayList<Payroll>();
		private List<Payroll> l_payroll_dolg = new ArrayList<Payroll>();
		private Long staff_id;
		private String staff_name;
		
		public Long getStaff_id() {
			return staff_id;
		}
		public void setStaff_id(Long staff_id) {
			this.staff_id = staff_id;
		}
		public String getStaff_name() {
			return staff_name;
		}
		public void setStaff_name(String staff_name) {
			this.staff_name = staff_name;
		}
		public List<Payroll> getL_payroll_schet() {
			return l_payroll_schet;
		}
		public void setL_payroll_schet(List<Payroll> l_payroll_schet) {
			this.l_payroll_schet = l_payroll_schet;
		}
		public List<Payroll> getL_payroll_deposit() {
			return l_payroll_deposit;
		}
		public void setL_payroll_deposit(List<Payroll> l_payroll_deposit) {
			this.l_payroll_deposit = l_payroll_deposit;
		}
		public List<Payroll> getL_payroll_zablok() {
			return l_payroll_zablok;
		}
		public void setL_payroll_zablok(List<Payroll> l_payroll_zablok) {
			this.l_payroll_zablok = l_payroll_zablok;
		}
		public List<Payroll> getL_payroll_avans_zapros() {
			return l_payroll_avans_zapros;
		}
		public void setL_payroll_avans_zapros(List<Payroll> l_payroll_avans_zapros) {
			this.l_payroll_avans_zapros = l_payroll_avans_zapros;
		}
		public List<Payroll> getL_payroll_avans_odob() {
			return l_payroll_avans_odob;
		}
		public void setL_payroll_avans_odob(List<Payroll> l_payroll_avans_odob) {
			this.l_payroll_avans_odob = l_payroll_avans_odob;
		}
		public List<Payroll> getL_payroll_dolg() {
			return l_payroll_dolg;
		}
		public void setL_payroll_dolg(List<Payroll> l_payroll_dolg) {
			this.l_payroll_dolg = l_payroll_dolg;
		}
		
	}
	
	private boolean canRead()
	{
		UserRoleDao userRoleDao = (UserRoleDao) appContext.getContext().getBean("userRoleDao");	
		for (UserRole wa:userRoleDao.findUserRoles(userData.getUserid()))
		{
			if (wa.getRoleId().equals(1L) || wa.getRoleId().equals(4L) || wa.getRoleId().equals(28L) || wa.getRoleId().equals(115L) || wa.getRoleId().equals(374L))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean canWrite(int a_oper_type)
	{
		UserRoleDao userRoleDao = (UserRoleDao) appContext.getContext().getBean("userRoleDao");	
		for (UserRole wa:userRoleDao.findUserRoles(userData.getUserid()))
		{	
			if (a_oper_type==1 && (wa.getRoleId().equals(4L) || wa.getRoleId().equals(28L) || wa.getRoleId().equals(1L) || wa.getRoleId().equals(115L)  || wa.getRoleId().equals(374L)))
			{
				return true;
			}
			if ((a_oper_type==2||a_oper_type==3) && (wa.getRoleId().equals(1L) || wa.getRoleId().equals(115L)  || wa.getRoleId().equals(374L)))
			{
				return true;
			}
		}
		return false;
	}
}
