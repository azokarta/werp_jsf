package finance.cashbankoperations;

import f4.BranchF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.FmglflextDao;
import general.output.tables.CashBankAccountStatement;
import general.services.CustomerService;
import general.services.FinanceService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Currency;
import general.tables.Customer; 
import general.tables.ExchangeRate;
 








import general.tables.Fmglflext;
import general.tables.Hkont;

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

@ManagedBean(name = "fkintBean", eager = true)
@ViewScoped
public class Fkint implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FKINT";
	private final static Long transaction_id = (long) 118;
	public static Long getTransactionId() {
		return transaction_id;
	}
	//private final static Long read = (long) 1;
	//private final static Long write = (long) 2;
	
	
	
	//***************************Application Context***************************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
	  return appContext;
	}

	public void setAppContext(AppContext appContext) {
	  this.appContext = appContext;
	}
	//*************************************************************************
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

	//******************************************************************
	//***************************ExchangeRateF4*******************************
	@ManagedProperty(value="#{exchangeRateF4Bean}")
	private ExchangeRateF4 p_exchangeRateF4;
	public ExchangeRateF4 getP_exchangeRateF4() {
		return p_exchangeRateF4;
	}

	public void setP_exchangeRateF4(ExchangeRateF4 p_exchangeRateF4) {
		this.p_exchangeRateF4 = p_exchangeRateF4;
	}
	//******************************************************************
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
  	public List<Branch> geBranch_list(){
		return branch_list;
	}
   //********************************************************************** 
	// *****************************Hkont**********************************
	@ManagedProperty(value = "#{hkontF4Bean}")
	private HkontF4 p_hkontF4Bean;
	public void setP_hkontF4Bean(HkontF4 p_hkontF4) {
		this.p_hkontF4Bean = p_hkontF4;
	}

	List<Hkont> hkont_list = new ArrayList<Hkont>();
	public List<Hkont> getHkont_list() {
		return hkont_list;
	}

	private Hkont selectedHkont;
	public Hkont getSelectedHkont() {
		return selectedHkont;
	}
	public void setSelectedHkont(Hkont p_selectedHkont) {
		this.selectedHkont = p_selectedHkont;
	}
	public void assignSelectedHkont(){			
		try{ 
			if (p_bseg.getT_bseg().getHkont()==null || p_bseg.getT_bseg().getHkont().startsWith("1010")) 
			{
				p_bseg.getT_bseg().setHkont(selectedHkont.getHkont());
				//p_bseg.setHkontName(selectedHkont.getText45());
				
				RequestContext reqCtx = RequestContext.getCurrentInstance(); 
				reqCtx.update("form:bsegTable:"+p_bseg.getIndex()+":b_hkont");
			}
			else
			{
				throw new DAOException("You can't change GL account " + p_bseg.getT_bseg().getHkont());
			}
			
			
		}	
		catch(DAOException ex)
		{ 
			addMessage("Info",ex.getMessage());
		}
	}
	// ******************************************************************
	
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Fkint.transaction_id);
			Calendar curDate = Calendar.getInstance();
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime()); 
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fkint.transaction_code); 
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
			p_bkpf.setBlart("ZK");

			
			
			l_bseg.add(new BsegTypeTable(0));
			dmbtrDisable = true;
			wrbtrDisable = true;

		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	private boolean dmbtrDisable = true;
	private boolean wrbtrDisable = true;	
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
	//*************************************************************************
	//**********Currency****************************************************************************
	private Currency selectedCurrency;
	public Currency getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(Currency selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}
	public void assignSelectedCurrency(){
		
		
		
		try{
			p_bkpf.setWaers(selectedCurrency.getCurrency());
			l_bseg = new ArrayList<BsegTypeTable>();
			l_bseg.add(new BsegTypeTable(0));
			for(BsegTypeTable wa_bseg_table:l_bseg)
			{
				wa_bseg_table.getT_bseg().setLifnr(p_bkpf.getCustomer_id());
			}
			
			if (!(selectedCurrency.getCurrency().equals("USD")))
			{	
				ExchangeRate wa_er = new ExchangeRate(); 
				//ExchangeRateDao exchangeRateDao = (ExchangeRateDao) appContext.getContext().getBean("exchangeRateDao"); 
				wa_er = p_exchangeRateF4.getL_er_map_national().get('1'+selectedCurrency.getCurrency());
				if (wa_er!=null)
				{
					p_bkpf.setKursf(wa_er.getSc_value());
					dmbtrDisable = true;
					wrbtrDisable = false;
				}			
				else{
					p_bkpf.setKursf(0); 
					throw new DAOException("Exchange rate not found");
				}
			}
			else
			{
				dmbtrDisable = false;
				wrbtrDisable = true;
				p_bkpf.setKursf(1);
			}
			
			
			
			for(Hkont wa_hkont:p_hkontF4Bean.getTypeHkontBranchWaers(p_bkpf.getBukrs(), p_bkpf.getBrnch()))
			{
				if (selectedCurrency.getCurrency().equals(wa_hkont.getWaers()))
				{
					hkont_list.add(wa_hkont);
				}
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bsegScrollPanel");
			reqCtx.update("form:bkpf_waers");
			reqCtx.update("form:bkpf_kursf");
			reqCtx.update("form:b_wrbtr");
			reqCtx.update("form:b_dmbtr");
			//reqCtx.update("form:bsegTable:bkpf_waers2");
			//reqCtx.update("form:singleMatnr");
			//reqCtx.update("form:bkpf_waers");
			
			//for(int i = 0; i <= l_bseg.size()-1; i++)
			//{
				//reqCtx.update("form:bsegTable:"+i+":b_matnr");
			//}
			
		}	
		catch(Exception ex)
		{
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			dmbtrDisable = true;
			wrbtrDisable = true;
			reqCtx.update("form:b_wrbtr");
			reqCtx.update("form:b_dmbtr");
			addMessage("Info",ex.getMessage());
		}
	}
	
	//************************************************************************************************
	//**************************Customer*******************************
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
  	
  	public void to_search_customer(){
		try {
				if (p_bkpf.getBukrs()!=null && !p_bkpf.getBukrs().equals("0"))
				{
					CustomerService personService = (CustomerService) appContext.getContext().getBean("customerService");
					p_customer_list = personService.searchInternalCompanies(p_bkpf.getBukrs());
				}
				else
				{
					throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
				}
				
				
				if (p_customer_list.size()==0){
					throw new DAOException(Helper.getErrorMessage(100L, userData.getU_language()));
				}
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:customerTable");
				
			}
			catch (DAOException ex)
			{
				p_customer_list = new ArrayList<Customer>();
				selectedCustomer = null;
				assignFoundCustomer();
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:customerTable");
				addMessage("Инфо",ex.getMessage()); 
			}
	}
  	
  	public void assignFoundCustomer(){   	 
  		if (selectedCustomer!= null && selectedCustomer.getId()!=null)
		{				
  			p_bkpf.setCustomer_id(selectedCustomer.getId());
			for(BsegTypeTable wa_bseg:l_bseg)
	  		{
	  			wa_bseg.getT_bseg().setLifnr(selectedCustomer.getId());
			}
			
		}
		else{		
			p_bkpf.setCustomer_id(null);
			for(BsegTypeTable wa_bseg:l_bseg)
	  		{
				wa_bseg.getT_bseg().setLifnr(null);
	  		}
		}  
			
		selectedCustomer = new Customer(); 
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bkpf_customer_id");
		reqCtx.update("form:bkpf_customer_id_href");
		for(int i = 0; i <= l_bseg.size()-1; i++)
		{
			reqCtx.update("form:bsegTable:"+i+":b_lifnr");
		}
		
		
		
				
  	}
  	//******************************************************************************************* 
  
    //***********************************Others***********************************************************
	public void refreshUSD(){
  		try
  		{
  			if (p_bkpf.getKursf() == 0 || p_bkpf.getKursf() < 0 ){
				throw new DAOException(Helper.getErrorMessage(2L, userData.getU_language()));
			}
  			
  			if (p_bkpf.getWaers()!=null  && (!p_bkpf.getWaers().equals("USD")) )
  			{
  				for(BsegTypeTable wa_bseg:l_bseg)
  		  		{
  					if (wa_bseg.getT_bseg().getWrbtr()>0 )
  					{ 
  						wa_bseg.getT_bseg().setDmbtr(wa_bseg.getT_bseg().getWrbtr()/p_bkpf.getKursf()); 
  					}
  		  		}
  				RequestContext reqCtx = RequestContext.getCurrentInstance();
  				
  				for(int i = 0; i <= l_bseg.size()-1; i++)
  				{
  					reqCtx.update("form:bsegTable:"+i+":b_dmbtr");
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
	//******************************************************************
	private Bkpf p_bkpf = new Bkpf();
	public Bkpf getP_bkpf() {
		return p_bkpf;
	}
	public void setP_bkpf(Bkpf p_bkpf) {
		this.p_bkpf = p_bkpf;
	}
	
	private List<BsegTypeTable> l_bseg = new ArrayList<BsegTypeTable>();
	public List<BsegTypeTable> getL_bseg() {
		return l_bseg;
	}

	public void setL_bseg(List<BsegTypeTable> l_bseg) {
		this.l_bseg = l_bseg;
	}
	
	private BsegTypeTable p_bseg = new BsegTypeTable();
	public BsegTypeTable getP_bseg() {
		return p_bseg;
	}
	public void setP_bseg(BsegTypeTable selectedBseg) {
		this.p_bseg = selectedBseg; 
	}
	
	public class BsegTypeTable {
		public BsegTypeTable()
		{
			
		};
		public BsegTypeTable(int a_index){ 
			this.index = a_index;
			this.t_bseg.setShkzg("S");
			this.t_bseg.setBschl("40");
		}
		
		private Bseg t_bseg = new Bseg();
		
		private String clientName;
		private String matnrName;
		private String werksName;
		private String meinsName;
		private int index;

		public String getClientName() {
			return clientName;
		}

		public void setClientName(String clientName) {
			this.clientName = clientName;
		}

		public String getMatnrName() {
			return matnrName;
		}

		public void setMatnrName(String matnrName) {
			this.matnrName = matnrName;
		}

		public String getWerksName() {
			return werksName;
		}

		public void setWerksName(String werksName) {
			this.werksName = werksName;
		}

		public String getMeinsName() {
			return meinsName;
		}

		public void setMeinsName(String meinsName) {
			this.meinsName = meinsName;
		}

		public Bseg getT_bseg() {
			return t_bseg;
		}

		public void setT_bseg(Bseg t_bseg) {
			this.t_bseg = t_bseg;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		
	}
	
	
	
	
	//*****************************************************************************************************
	
	public void addRow(){
  		if (l_bseg.size()>0)
  		{
  	  		l_bseg.add(new BsegTypeTable(l_bseg.size()));

  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  	  		reqCtx.update("form:bsegScrollPanel"); 
  		}
  		
  	} 
	//***************************Save method********************************
	public void to_save() throws IOException{
		try { 
			PermissionController.canWrite(userData, Fkint.transaction_id);
			
			if (p_bkpf.getKursf() == 0 || p_bkpf.getKursf() < 0 ){
				throw new DAOException(Helper.getErrorMessage(2L, userData.getU_language()));
			}
			
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
			}

			if (p_bkpf.getBukrs() == null || p_bkpf.getBukrs().isEmpty())
			{
				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
			}  

			if (selectedBranch==null)
			{
				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
			}
			
			
			FinanceService financeService = (FinanceService) appContext.getContext().getBean("financeService");
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			Bseg p_bsegKredit = new Bseg();
			Calendar cal = Calendar.getInstance(); 	 
			cal.setTime(p_bkpf.getBudat());
			
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fkint.transaction_code); 
			p_bkpf.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf.setBrnch(getSelectedBranch().getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			p_bkpf.setAwtyp(2);
			p_bkpf.setClosed(1);
			
			int wa_buzei = 0;
			wa_buzei++;
			p_bsegKredit.setBuzei(wa_buzei);
			if (selectedCustomer.getIin_bin().equals("1000")) p_bsegKredit.setHkont("33101000");
			else if (selectedCustomer.getIin_bin().equals("2000")) p_bsegKredit.setHkont("33102000");
			else if (selectedCustomer.getIin_bin().equals("3000")) p_bsegKredit.setHkont("33103000");
			else if (selectedCustomer.getIin_bin().equals("0001")) p_bsegKredit.setHkont("33104000");
			else if (selectedCustomer.getIin_bin().equals("5000")) p_bsegKredit.setHkont("33105000");
			else if (selectedCustomer.getIin_bin().equals("6000")) p_bsegKredit.setHkont("33106000");
			else if (selectedCustomer.getIin_bin().equals("7000")) p_bsegKredit.setHkont("33107000");
			else if (selectedCustomer.getIin_bin().equals("8000")) p_bsegKredit.setHkont("33108000");
			p_bsegKredit.setBukrs(p_bkpf.getBukrs());
			p_bsegKredit.setGjahr(p_bkpf.getGjahr());
			p_bsegKredit.setBschl("31");
			p_bsegKredit.setShkzg("H");
			p_bsegKredit.setLifnr(selectedCustomer.getId());
			
			if (p_bkpf.getCustomer_id() == null)
			{
				throw new DAOException(Helper.getErrorMessage(9L, userData.getU_language()));
			}
			

			
			l_bsegFinal.add(p_bsegKredit);
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 
				wa_buzei++;
				wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getT_bseg().setGjahr(p_bkpf.getGjahr());
				wa_bseg.getT_bseg().setBuzei(wa_buzei);
				
				if (p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") ){
					if (wa_bseg.getT_bseg().getDmbtr()==0 ||wa_bseg.getT_bseg().getDmbtr()<0){
						throw new DAOException(Helper.getErrorMessage(8L, userData.getU_language()));
					}
				}
				else if (p_bkpf.getWaers()!=null  && (!p_bkpf.getWaers().equals("USD")) ){
					if (wa_bseg.getT_bseg().getWrbtr()==0 ||wa_bseg.getT_bseg().getWrbtr()<0){
						
						throw new DAOException(Helper.getErrorMessage(8L, userData.getU_language()));
					}
					else{
						//wa_bseg.getT_bseg().setWrbtr(wa_bseg.getT_bseg().getDmbtr());
						//wa_bseg.getT_bseg().setDmbtr(0);
						wa_bseg.getT_bseg().setDmbtr(wa_bseg.getT_bseg().getWrbtr()/p_bkpf.getKursf()); 
					}
				}
				
		  	}
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 				
				p_bsegKredit.setDmbtr(p_bsegKredit.getDmbtr()+wa_bseg.getT_bseg().getDmbtr());
				p_bsegKredit.setWrbtr(p_bsegKredit.getWrbtr()+wa_bseg.getT_bseg().getWrbtr());
				l_bsegFinal.add(wa_bseg.getT_bseg());
		  	}
			
			for (Bseg wa_bseg:l_bsegFinal)
			{
				System.out.println(wa_bseg.getBukrs());
				System.out.println(wa_bseg.getBuzei());
			}

			
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bsegTable");
			
			//for(Bseg wa_bseg:l_bsegFinal)
		  		//{ 
			//	System.out.println(wa_bseg.getBukrs());
			//	System.out.println(wa_bseg.getDmbtr());
			//	System.out.println(wa_bseg.getWrbtr()); 
		  		//}
			
			userData.setNew_fi_doc(true);
			userData.setTrans_id(Fkint.transaction_id);
//			Long newDocBelnr = financeService.createAccountPayableDocs(p_bkpf, l_bsegFinal);
			Long newDocBelnr = financeService.createFkint(p_bkpf, l_bsegFinal);
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath()  + "/accounting/assess/fa02.xhtml?belnr="+newDocBelnr+"&gjahr="+p_bkpf.getGjahr()+"&bukrs="+p_bkpf.getBukrs());
			
			
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		}  
		
	}
	
	public void changeBukrs()
	{
		selectedCustomer = null;
		assignFoundCustomer();
		to_search_customer();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:singleWerks");
		reqCtx.update("form:business_area");
		reqCtx.update("form:branch");
		 
	}
	
	public void changeBranch()
	{
		selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());
		getCashOfficeHkont();
	}
	
	//**********************************************************************		
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

	public List<Branch> getUserBranchList(String a_bukrs){
		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		if (userData.isSys_admin()) return brDao.findUserBranchesOfficesAdmin(a_bukrs, userData.getUserid());
		else return brDao.findUserBranchesOffices(a_bukrs, userData.getUserid());		
		 
	}
}
