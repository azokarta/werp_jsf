package finance.cashbankoperations;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.FmglflextDao;
import general.output.tables.CashBankAccountStatement;
import general.output.tables.Podotchet;
import general.services.FinancePodotchet;
import general.services.FinanceService;
import general.services.StaffService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Bukrs;
import general.tables.ExchangeRate;
import general.tables.Fmglflext;
import general.tables.Hkont;
import general.tables.Salary;
import general.tables.Staff;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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


@ManagedBean(name = "faiaBean", eager = true)
@ViewScoped
public class Faia implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FAIA";
	private final static Long transaction_id = (long) 86;
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
  	@ManagedProperty(value="#{branchF4Bean}")
  	private BranchF4 p_branchF4Bean;
  	public void setP_branchF4Bean(BranchF4 p_branchF4) {
  	    this.p_branchF4Bean = p_branchF4;
  	}
  	public BranchF4 getP_branchF4Bean() {
  		return p_branchF4Bean;
	}
  	
  	private Branch selectedBranch = new Branch();
  	public Branch getSelectedBranch() {
  		return selectedBranch;
	}
	public void setSelectedBranch(Branch selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	List<Branch> branch_list = new ArrayList<Branch>();
  	public List<Branch> getBranch_list(){
		return branch_list;
	}
    //**********************************************************************
    //*****************************Hkont************************************
  	@ManagedProperty(value="#{hkontF4Bean}")
  	private HkontF4 p_hkontF4Bean;  	
  	public HkontF4 getP_hkontF4Bean() {
		return p_hkontF4Bean;
	}
	public void setP_hkontF4Bean(HkontF4 p_hkontF4Bean) {
		this.p_hkontF4Bean = p_hkontF4Bean;
	}
	
	private List<Hkont> l_hkont = new ArrayList<Hkont>();	
	public List<Hkont> getL_hkont() {
		return l_hkont;
	}
	public void setL_hkont(List<Hkont> l_hkont) {
		this.l_hkont = l_hkont;
	}
	
	private List<Hkont> l_hkont2 = new ArrayList<Hkont>();
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
	// *******************************Init method***********************************	
	@PostConstruct
	public void init() {
		try
		{
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Faia.transaction_id);
			
			Calendar curDate = Calendar.getInstance();

			for(Branch wa_branch:p_branchF4Bean.getBranch_list())
			{
				if (wa_branch.getBranch_id() == userData.getBranch_id())
				{
					selectedBranch = wa_branch;
				}
			}
			for (Bukrs wa_bukrs: p_bukrsF4Bean.getBukrs_list()){
				if (!wa_bukrs.getBukrs().equals("0001")){
					bukrs_list.add(wa_bukrs);
				}
			}
			
			for (Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
			{
				if (wa_hkont.getHkont().startsWith("1010") || wa_hkont.getHkont().startsWith("1030"))
				{
					l_hkont2.add(wa_hkont);
				}
			}
			
			for(ExchangeRate wa_er: p_exchangeRateF4Bean.getExchageRate_list())
			{
				if (wa_er.getType()==1)
				{
					l_er.add(wa_er);
				}
			}
			
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBlart("IA");
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
			clearAndAddBseg();
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	// *****************************************************************************	
	// *****************************Local variables********************************
	private String bkpf_customer_name = "";
	public String getBkpf_customer_name() {
		return bkpf_customer_name;
	}
	public void setBkpf_customer_name(String bkpf_customer_name) {
		this.bkpf_customer_name = bkpf_customer_name;
	}
	
	private Bkpf p_bkpf = new Bkpf();	
	public Bkpf getP_bkpf() {
		return p_bkpf;
	}
	public void setP_bkpf(Bkpf p_bkpf) {
		this.p_bkpf = p_bkpf;
	}

	private BsegClass p_bseg = new BsegClass();	
	public BsegClass getP_bseg() {
		return p_bseg;
	}
	public void setP_bseg(BsegClass p_bseg) {
		this.p_bseg = p_bseg;
	}
	
	private List<BsegClass> l_bseg = new ArrayList<BsegClass>();	
	public List<BsegClass> getL_bseg() {
		return l_bseg;
	}
	public void setL_bseg(List<BsegClass> l_bseg) {
		this.l_bseg = l_bseg;
	}

	public class BsegClass{
		public BsegClass()
		{
		}
		public BsegClass(int index, int buzei)
		{
			this.index = index;
			this.p_bseg.setBuzei(buzei);
		}
		private int index;
		private Bseg p_bseg = new Bseg();
		private String client_name="";
		private String supplier_name="";
		private String matnr_name = "";
		private String werks_name = "";
		private String meins_name = "";
		private String hkont_name = "";
		private String bschl_name = "";
		private boolean dmbtrDisable = false;
		private boolean wrbtrDisable = true;
		private boolean shkzgDisable = false;
		private String hkont_waers = "";
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public Bseg getP_bseg() {
			return p_bseg;
		}
		public void setP_bseg(Bseg p_bseg) {
			this.p_bseg = p_bseg;
		}
		public String getClient_name() {
			return client_name;
		}
		public void setClient_name(String client_name) {
			this.client_name = client_name;
		}
		public String getSupplier_name() {
			return supplier_name;
		}
		public void setSupplier_name(String supplier_name) {
			this.supplier_name = supplier_name;
		}
		public String getMatnr_name() {
			return matnr_name;
		}
		public void setMatnr_name(String matnr_name) {
			this.matnr_name = matnr_name;
		}
		public String getWerks_name() {
			return werks_name;
		}
		public void setWerks_name(String werks_name) {
			this.werks_name = werks_name;
		}
		public String getMeins_name() {
			return meins_name;
		}
		public void setMeins_name(String meins_name) {
			this.meins_name = meins_name;
		}
		public String getHkont_name() {
			return hkont_name;
		}
		public void setHkont_name(String hkont_name) {
			this.hkont_name = hkont_name;
		}
		public String getBschl_name() {
			return bschl_name;
		}
		public void setBschl_name(String bschl_name) {
			this.bschl_name = bschl_name;
		}			
		public boolean isDmbtrDisable() {
			return dmbtrDisable;
		}
		public void setDmbtrDisable(boolean dmbtrDisable) {
			this.dmbtrDisable = dmbtrDisable;
		}
		public boolean isWrbtrDisable() {
			return wrbtrDisable;
		}
		public void setWrbtrDisable(boolean wrbtrDisable) {
			this.wrbtrDisable = wrbtrDisable;
		}
		public String getHkont_waers() {
			return hkont_waers;
		}
		public void setHkont_waers(String hkont_waers) {
			this.hkont_waers = hkont_waers;
		}
		public boolean isShkzgDisable() {
			return shkzgDisable;
		}
		public void setShkzgDisable(boolean shkzgDisable) {
			this.shkzgDisable = shkzgDisable;
		}
	}
    //***********************************Others***********************************************************
	public void refreshUSD(){
  		try
  		{
  			if (p_bkpf.getWaers()!=null  && (!p_bkpf.getWaers().equals("USD")) )
  			{
  				BsegClass wa_bseg = l_bseg.get(0);
  				if (wa_bseg.getHkont_waers()!=null && !wa_bseg.getHkont_waers().equals("USD"))
  				{ 
  					wa_bseg.getP_bseg().setDmbtr(wa_bseg.getP_bseg().getWrbtr()/p_bkpf.getKursf()); 
  				}
  				else if (wa_bseg.getHkont_waers()!=null && wa_bseg.getHkont_waers().equals("USD"))
  				{ 
  					wa_bseg.getP_bseg().setWrbtr(wa_bseg.getP_bseg().getDmbtr()*p_bkpf.getKursf()); 
  				}
  				BsegClass wa_bseg1 = l_bseg.get(1);
  				wa_bseg1.getP_bseg().setDmbtr(wa_bseg.getP_bseg().getDmbtr());
  				wa_bseg1.getP_bseg().setWrbtr(wa_bseg.getP_bseg().getWrbtr());
  				RequestContext reqCtx = RequestContext.getCurrentInstance();
  				
  				for(int i = 0; i <= l_bseg.size()-1; i++)
  				{
  					reqCtx.update("form:bsegTable:"+i+":dmbtr");
  					reqCtx.update("form:bsegTable:"+i+":wrbtr");
  				}
  			} 
  		}
  	
  		catch(DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
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
  	//Assign ExchangeRate*************************************************************************
	private ExchangeRate selectedExchangeRate = new ExchangeRate();
	public ExchangeRate getSelectedExchangeRate() {
		return selectedExchangeRate;
	}
	public void setSelectedExchangeRate(ExchangeRate selectedExchangeRate) {
		this.selectedExchangeRate = selectedExchangeRate;
	}
	
	private List<Podotchet> l_pdo = new ArrayList<Podotchet>();	
	public List<Podotchet> getL_pdo() {
		return l_pdo;
	}
	public void setL_pdo(List<Podotchet> l_pdo) {
		this.l_pdo = l_pdo;
	}
	
	public void assignSelectedExchangeRate()
	{
		try
		{	
			if (p_bkpf.getBrnch()==null || p_bkpf.getBrnch()<1)
			{
				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
			}
			clearAndAddBseg();
			//selecting appropriate general ledger accounts
			l_hkont.clear();
			for (Hkont wa_hkont:p_hkontF4Bean.getTypeHkontBranchWaers(p_bkpf.getBukrs(), p_bkpf.getBrnch()))
			{
				if (wa_hkont.getWaers().equals(selectedExchangeRate.getSecondary_currency()))
				{
					l_hkont.add(wa_hkont);
				}
			}
			if (l_hkont.size()>0)
			{
				p_bkpf.setWaers(selectedExchangeRate.getSecondary_currency());
				p_bkpf.setKursf(selectedExchangeRate.getSc_value());
				FinancePodotchet financePodotchet = (FinancePodotchet) appContext.getContext().getBean("financePodotchet");
				l_pdo = financePodotchet.getPodotchetList(p_bkpf.getBukrs(), p_bkpf.getBrnch(), p_bkpf.getWaers());
			}
			else
			{
				addMessage("Info",Helper.getErrorMessage(91L, userData.getU_language()));
			}
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bkpf_waers");
			reqCtx.update("form:bkpf_kursf");
			reqCtx.update("form:singleHkont");
			reqCtx.update("form:bsegTable");
			reqCtx.update("form:staffTableASW");
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	public void assignSelectedExchangeRateUSD()
	{
		try
		{	
			if (p_bkpf.getBrnch()==null || p_bkpf.getBrnch()<1)
			{
				throw new DAOException("Выберите филиал.");
			}
			clearAndAddBseg();
			//selecting appropriate general ledger accounts
			l_hkont.clear();
			for (Hkont wa_hkont:p_hkontF4Bean.getTypeHkontBranchWaers(p_bkpf.getBukrs(), p_bkpf.getBrnch()))
			{
				if (wa_hkont.getWaers().equals(p_bkpf.getWaers()))
				{
					l_hkont.add(wa_hkont);
				}
			}
			if (l_hkont.size()>0)
			{
				p_bkpf.setWaers("USD");
				p_bkpf.setKursf(1);
				FinancePodotchet financePodotchet = (FinancePodotchet) appContext.getContext().getBean("financePodotchet");
				l_pdo = financePodotchet.getPodotchetList(p_bkpf.getBukrs(), p_bkpf.getBrnch(), p_bkpf.getWaers());
			}
			else
			{
				addMessage("Info",Helper.getErrorMessage(91L, userData.getU_language()));
			}
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bkpf_waers");
			reqCtx.update("form:bkpf_kursf");
			reqCtx.update("form:singleHkont");
			reqCtx.update("form:bsegTable");
			reqCtx.update("form:staffTableASW");
			
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	//********************************************************************************************	
  	//Assign Hkont********************************************************************************
	private Hkont selectedHkont = new Hkont();
    public Hkont getSelectedHkont() {
		return selectedHkont;
	}
	public void setSelectedHkont(Hkont selectedHkont) {
		this.selectedHkont = selectedHkont;
	}
	
	public void assignSelectedHkont()
	{
		try
		{
			if (p_bseg.getIndex()==1)
			{
				throw new DAOException("You can't select General Ledger for money on account");
			}
			p_bseg.setHkont_name(selectedHkont.getHkont()+" "+selectedHkont.getWaers());
			p_bseg.getP_bseg().setHkont(selectedHkont.getHkont());
			p_bseg.setHkont_waers(selectedHkont.getWaers());
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bsegTable:"+p_bseg.getIndex()+":hkont");
			if (selectedHkont.getWaers()!=null && selectedHkont.getWaers().equals("USD"))
			{
				p_bseg.getP_bseg().setWrbtr(0);
				p_bseg.setWrbtrDisable(true);
				p_bseg.setDmbtrDisable(false);
			}
			else
			{
				p_bseg.getP_bseg().setDmbtr(0);
				p_bseg.setWrbtrDisable(false);
				p_bseg.setDmbtrDisable(true);
			}
			reqCtx.update("form:bsegTable:"+p_bseg.getIndex()+":dmbtr");
			reqCtx.update("form:bsegTable:"+p_bseg.getIndex()+":wrbtr");
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	//********************************************************************************************
	//**************************Customer*******************************
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
	
	private int p_search_position_id;
	public int getP_search_position_id() {
		return p_search_position_id;
	}
	public void setP_search_position_id(int p_search_position_id) {
		this.p_search_position_id = p_search_position_id;
	}
	
	private Salary searchSalary = new Salary();	
	public Salary getSearchSalary() {
		return searchSalary;
	}
	public void setSearchSalary(Salary searchSalary) {
		this.searchSalary = searchSalary;
	}
	
	public void to_search_staff() {
		try {
			searchSalary.setBukrs(p_bkpf.getBukrs());
			searchSalary.setBranch_id(p_bkpf.getBrnch());
			StaffService staffService = (StaffService) appContext.getContext()
					.getBean("staffService");
			p_staff_list = staffService.dynamicSearchStaffSalary2(searchStaff,
					searchSalary);
			if (p_staff_list.size() == 0) {
				p_staff_list = new ArrayList<Staff>();
				addMessage("Инфо", "Не найдено.");
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");

		} catch (DAOException ex) {
			p_staff_list = new ArrayList<Staff>();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffTable");
			addMessage("Info", ex.getMessage());
		}
	}

	
	public void assignFoundEmployee() {
		if (selectedStaff != null && selectedStaff.getStaff_id() != null) {			
			p_bkpf.setCustomer_id(selectedStaff.getCustomer_id());
			l_bseg.get(1).getP_bseg().setLifnr(selectedStaff.getCustomer_id());
			bkpf_customer_name = Validation.returnFioInitials(selectedStaff.getFirstname(), selectedStaff.getLastname(), selectedStaff.getMiddlename());
			l_bseg.get(1).setClient_name(bkpf_customer_name);
		} else {
			p_bkpf.setCustomer_id(null);
			l_bseg.get(1).getP_bseg().setLifnr(null);
			l_bseg.get(1).setClient_name(null);
			bkpf_customer_name = null;
		}

		selectedStaff = new Staff();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bsegTable:"+1+":b_kunnr");
		reqCtx.update("form:customer_id");
		
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
	
	public void to_save()
	{
		try
		{			
			PermissionController.canWrite(userData, Faia.transaction_id);
			p_bkpf.setAwtyp(1);
			List<Bseg> wal_bseg = new ArrayList<Bseg>();
			wal_bseg.add(l_bseg.get(0).getP_bseg());
			wal_bseg.add(l_bseg.get(1).getP_bseg());
			FinanceService financeService = (FinanceService) appContext.getContext().getBean("financeService");			
			Long newDocBelnr = financeService.createFAICF(p_bkpf, wal_bseg);
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			try {
				userData.setNew_fi_doc(true);
				userData.setTrans_id(Faia.transaction_id);
				context.redirect(context.getRequestContextPath()
						+ "/accounting/assess/fa02.xhtml?belnr=" + newDocBelnr
						+ "&gjahr=" + p_bkpf.getGjahr() + "&bukrs="
						+ p_bkpf.getBukrs());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	public void to_check()
	{
		try
		{			
			refreshUSD();
			if (p_bkpf.getWaers()==null)
			{
				throw new DAOException("Select Currency");
			}

			if (p_bkpf.getCustomer_id()==null || p_bkpf.getCustomer_id()==0 || l_bseg.get(1).getP_bseg().getLifnr()==null || l_bseg.get(1).getP_bseg().getLifnr()==0)
			{
				throw new DAOException("Choose Staff");
			}
			Calendar cal_budat = Calendar.getInstance();
			Calendar curDate = Calendar.getInstance(); 
			cal_budat.setTime(p_bkpf.getBudat());
			p_bkpf.setUsnam((long) userData.getUserid());
			p_bkpf.setTcode(Faia.transaction_code);
			p_bkpf.setGjahr(cal_budat.get(Calendar.YEAR));
			p_bkpf.setMonat(cal_budat.get(Calendar.MONTH) + 1);
			p_bkpf.setBrnch(userData.getBranch_id());
			p_bkpf.setCpudt(curDate.getTime());
			p_bkpf.setAwtyp(1);
			double dmbtr_h = 0;
			double dmbtr_s = 0;
			double wrbtr_h = 0;
			double wrbtr_s = 0;
			for (int i = 0; i<2;i++)
			{
				BsegClass wa_bseg = l_bseg.get(i);
				wa_bseg.getP_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getP_bseg().setGjahr(p_bkpf.getGjahr());
				//USD and USD
				if (wa_bseg.getP_bseg().getHkont()==null)
				{
					//do nothing;
					throw new DAOException("Cash or bank accountnot selected");
				}
				
				if (wa_bseg.getP_bseg().getShkzg().equals("H")) 
				{
					dmbtr_h = dmbtr_h + wa_bseg.getP_bseg().getDmbtr();
					wrbtr_h = wrbtr_h + wa_bseg.getP_bseg().getWrbtr();
				}
				else 
				{
					dmbtr_s = dmbtr_s + wa_bseg.getP_bseg().getDmbtr();
					wrbtr_s = wrbtr_s + wa_bseg.getP_bseg().getWrbtr();
				}
				
			}
			if (p_bkpf.getWaers().equals("USD")){
				if (dmbtr_h==0 || dmbtr_s == 0)
				{
					throw new DAOException("USD amount incorrect");
				}
			}
			else
			{
				if (wrbtr_h==0 || wrbtr_s == 0)
				{
					throw new DAOException(p_bkpf.getWaers()+" amount incorrect");
				}
			}

			p_bkpf.setDmbtr(dmbtr_s);
			p_bkpf.setWrbtr(wrbtr_s);
			saveDisable = false;
			RequestContext reqCtx = RequestContext.getCurrentInstance();			
			for (int i = 0; i<l_bseg.size();i++)
			{
				BsegClass wa_bseg = l_bseg.get(i);
				wa_bseg.getP_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getP_bseg().setGjahr(p_bkpf.getGjahr());
				if (wa_bseg.getP_bseg().getShkzg().equals("H"))
				{
					wa_bseg.getP_bseg().setBschl("50");
				}
				else
				{
					wa_bseg.getP_bseg().setBschl("4");
				}
				wa_bseg.setWrbtrDisable(true);
				wa_bseg.setDmbtrDisable(true);
				reqCtx.update("form:bsegTable:"+i+":dmbtr");
				reqCtx.update("form:bsegTable:"+i+":wrbtr");
				reqCtx.update("form:bsegTable:"+i+":hkont");
				reqCtx.update("form:bsegTable:"+i+":shkzg");
			}
			reqCtx.update("form:saveButton");
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	public void to_edit()
	{
		try
		{	
			saveDisable = true;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:saveButton");
			for (int i = 0; i<1;i++)
			{
				BsegClass wa_bseg = l_bseg.get(i);
				if(wa_bseg.getP_bseg().getHkont()!=null && wa_bseg.getHkont_waers().equals("USD"))
				{
					wa_bseg.setWrbtrDisable(true);
					wa_bseg.setDmbtrDisable(false);
				}
				else if (wa_bseg.getP_bseg().getHkont()!=null)
				{
					wa_bseg.setWrbtrDisable(false);
					wa_bseg.setDmbtrDisable(true);
				}
				else
				{
					wa_bseg.setWrbtrDisable(true);
					wa_bseg.setDmbtrDisable(true);
				}
				reqCtx.update("form:bsegTable:"+i+":dmbtr");
				reqCtx.update("form:bsegTable:"+i+":wrbtr");
			}
			
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	public void clearAndAddBseg()
	{
		l_bseg.clear();
		l_bseg.add(new BsegClass(l_bseg.size(),1));
		l_bseg.add(new BsegClass(l_bseg.size(),2));
		l_bseg.get(0).setShkzgDisable(true);
		l_bseg.get(0).getP_bseg().setShkzg("H");
		l_bseg.get(1).setDmbtrDisable(true);
		l_bseg.get(1).setWrbtrDisable(true);
		l_bseg.get(1).setShkzgDisable(true);
		l_bseg.get(1).getP_bseg().setShkzg("S");
		l_bseg.get(1).getP_bseg().setHkont("12500001");
		l_bseg.get(1).setHkont_name("12500001 USD");
		l_bseg.get(1).setHkont_waers("USD");
		l_pdo.clear();
	}
	//*****************************************************************************************************
	//*****************************************************************************************************
	
	//*****************************************************************************************************
	public void changeBukrs(){
		//System.out.println(p_bkpf.getCustomer_id());
		l_hkont.clear();
		searchSalary.setBukrs(p_bkpf.getBukrs());
		p_bkpf.setCustomer_id(null);
		p_bkpf.setWaers(null);
		p_bkpf.setKursf(0);
		p_bkpf.setBrnch(null);
		clearAndAddBseg();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}
	public void changeBranch(){
		searchSalary.setBranch_id(p_bkpf.getBrnch());
		p_staff_list.clear();
		getCashOfficeHkont();
	}
	
	private List<CashBankAccountStatement> l_as = new ArrayList<CashBankAccountStatement>();
	public List<CashBankAccountStatement> getL_as() {
		return l_as;
	}
	public void setL_as(List<CashBankAccountStatement> l_as) {
		this.l_as = l_as;
	}

	public void getCashOfficeHkont()
	{
		try{
			if ((p_bkpf.getBrnch()!=null))
			{
				l_as = p_hkontF4Bean.getHkontBranchWaers(p_bkpf.getBukrs(), p_bkpf.getBrnch());
				if (l_as==null || l_as.size()==0)
				{
					throw new DAOException(Helper.getErrorMessage(3L, userData.getU_language()));
				}
			}
			else if ((selectedBranch==null || selectedBranch.getBranch_id()==null))
			{
				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
			}
			
			FmglflextDao fmglflextDao = (FmglflextDao) appContext.getContext().getBean("fmglflextDao");
			Calendar curDate = Calendar.getInstance();	
			int curMonth = curDate.get(Calendar.MONTH) + 1;
			List<Fmglflext> l_fgl = new ArrayList<Fmglflext>();
			List<String> sl_hkont = new ArrayList<String>();
			Map<String,CashBankAccountStatement> l_as_map = new HashMap<String,CashBankAccountStatement>();
			//System.out.println(p_bukrs.getBukrs());
			for (CashBankAccountStatement wa_as:l_as)
			{
				
					sl_hkont.add(wa_as.getHkont());
					l_as_map.put(wa_as.getHkont(), wa_as);
			}
			// removing duplicates
			Set<String> hs = new HashSet<>();
			hs.addAll(sl_hkont);
			sl_hkont.clear();
			sl_hkont.addAll(hs);
			
						
			l_fgl = fmglflextDao.findByBukrsGjahrHkontList(p_bkpf.getBukrs(),curDate.get(Calendar.YEAR),sl_hkont);
			
			if (l_fgl!=null && l_fgl.size()>0)
			{
				for(Fmglflext wa_fmgl: l_fgl)
	  			{
					CashBankAccountStatement wa_as = l_as_map.get(wa_fmgl.getHkont());
	  				double amount = 0;
	  				amount  = amount + wa_fmgl.getBeg_amount();
	  				for(int i = 1; i<=curMonth;i++)
	  				{ 
		  				switch (i) {
		  					case 1:  amount = amount + wa_fmgl.getMonth1();
		  	                    break;
		  					case 2:  amount = amount + wa_fmgl.getMonth2();
		  	                    break;
		  					case 3:  amount = amount + wa_fmgl.getMonth3();
		  	                    break;
		  					case 4:  amount = amount + wa_fmgl.getMonth4();
		  	                    break;
		  					case 5:  amount = amount + wa_fmgl.getMonth5();
		  	                    break;
		  					case 6:  amount = amount + wa_fmgl.getMonth6();
		  	                    break;
		  					case 7:  amount = amount + wa_fmgl.getMonth7();
		  	                    break;
		  					case 8:  amount = amount + wa_fmgl.getMonth8();
		  	                    break;
		  					case 9:  amount = amount + wa_fmgl.getMonth9();
		  	                    break;
		  					case 10: amount = amount + wa_fmgl.getMonth10();
		  	                    break;
		  					case 11: amount = amount + wa_fmgl.getMonth11();
		  	                    break;
		  					case 12: amount = amount + wa_fmgl.getMonth12();
		  	                    break;
		  					
		  					}
	  				}
	  				
	  				
					if (wa_as!=null)
					{
						if (wa_fmgl.getDrcrk().equals("S"))
						{
							wa_as.setAmount(wa_as.getAmount()+amount);
						}
						else if (wa_fmgl.getDrcrk().equals("H"))
						{
							wa_as.setAmount(wa_as.getAmount()-amount);
						}
					}
					
	  				
	  			}
			}
			
		
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:outputTableBranchCash");
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		} 
		

	}
}
