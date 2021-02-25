package accounting.assess;

import f4.BranchF4;
import f4.ExchangeRateF4;
import f4.MeinsF4;
import f4.PriceListF4;
import f4.WerksF4;
import general.AppContext; 
import general.PermissionController;
import general.dao.DAOException;
import general.dao.PriceListDao;
import general.services.CustomerService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Currency;
import general.tables.Customer; 
import general.tables.ExchangeRate;
import general.tables.Meins;
import general.tables.PriceList;
import general.tables.Werks;
 













import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "fdsogBean", eager = true)
@ViewScoped
public class Fdsog implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FDSOG";
	private final static Long transaction_id = (long) 45;
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
	//*****************************Werks**********************************
  	@ManagedProperty(value="#{werksF4Bean}")
  	private WerksF4 p_werksF4Bean;
  	public void setP_werksF4Bean(WerksF4 p_werksF4) {
  	      this.p_werksF4Bean = p_werksF4;
  	}
  	public WerksF4 getP_werksF4Bean() {
		  return p_werksF4Bean;
		}
  	List<Werks> werks_list = new ArrayList<Werks>();
  	public List<Werks> getWerks_list(){
		return werks_list;
	}
  	
  	private Werks selectedWerks = new Werks();  	
    public Werks getSelectedWerks() {
		return selectedWerks;
	}
	public void setSelectedWerks(Werks selectedWerks) {
		this.selectedWerks = selectedWerks;
	}
	//**********************************************************************
	//*****************************Werks**********************************
  	@ManagedProperty(value="#{meinsF4Bean}")
  	private MeinsF4 p_meinsF4Bean;
  	public void setP_meinsF4Bean(MeinsF4 p_meinsF4) {
  	      this.p_meinsF4Bean = p_meinsF4;
  	}
  	public MeinsF4 getP_meinsF4Bean() {
		  return p_meinsF4Bean;
		}
  	Meins p_meins = new Meins();
  	public Meins getP_meins(){
		return p_meins;
	}
    //**********************************************************************	
	//***************************PriceListF4*******************************
	@ManagedProperty(value="#{priceListF4Bean}")
	private PriceListF4 p_priceListF4Bean;			
	
	public PriceListF4 getP_priceListF4Bean() {
	  return p_priceListF4Bean;
	}

	public void setP_priceListF4Bean(PriceListF4 p_priceListF4Bean) {
	  this.p_priceListF4Bean = p_priceListF4Bean;
	} 
	List<PriceList> priceList_list = new ArrayList<PriceList>(); 
	public List<PriceList> getPriceList_list(){
		return priceList_list;
	} 
	 
	PriceList p_priceList = new PriceList();	
	public PriceList getP_priceList() {
		return p_priceList;
	}

	public void setP_priceList(PriceList p_priceList) {
		this.p_priceList = p_priceList;
	}
	
	private int mwMenge = 1;	
	public int getMwMenge() {
		return mwMenge;
	}
	public void setMwMenge(int mwMenge) {
		this.mwMenge = mwMenge;
	}

	public void assignFoundMatnr()
	{
		if (p_priceList!= null && p_priceList.getMatnr()!=null)
		{
			
			p_bseg.getT_bseg().setMatnr(p_priceList.getMatnr());
			//p_bseg.setMatnrName(p_priceList.getRmatnr().getText45().toLowerCase()); 
			
			p_bseg.getT_bseg().setMenge(mwMenge);
			
			if (p_bkpf.getWaers().equals("USD") && p_priceList.getWaers().equals(p_bkpf.getWaers()))
			{
				p_bseg.getT_bseg().setDmbtr(p_bseg.getT_bseg().getMenge() * p_priceList.getPrice());
			}
			else if (p_priceList.getWaers().equals(p_bkpf.getWaers()))
			{
				p_bseg.getT_bseg().setWrbtr(p_bseg.getT_bseg().getMenge() * p_priceList.getPrice());  
				p_bseg.getT_bseg().setDmbtr(p_bseg.getT_bseg().getMenge() * p_priceList.getPrice()/p_bkpf.getKursf());
			}
			
		}
		else{		
			mwMenge = 1;
			p_bseg.getT_bseg().setMatnr(null);
			p_bseg.setMatnrName(""); 
			p_bseg.getT_bseg().setMenge(0);
			p_bseg.getT_bseg().setDmbtr(0);
			p_bseg.getT_bseg().setWrbtr(0);
		}  
		mwMenge = 1;
		p_priceList = new PriceList(); 
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		int pos = p_bseg.getIndex();
		reqCtx.update("form:bsegTable:"+pos+":b_matnr");
		reqCtx.update("form:bsegTable:"+pos+":b_menge");
		reqCtx.update("form:bsegTable:"+pos+":b_dmbtr");
		reqCtx.update("form:bsegTable:"+pos+":b_wrbtr");
		reqCtx.update("form:mwMenge");
	}
	
	//******************************************************************	
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Fdsog.transaction_id);
			Calendar curDate = Calendar.getInstance();
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime()); 
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fdsog.transaction_code); 
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
			p_bkpf.setBlart("GS");
			
			for(Meins wa_meins:p_meinsF4Bean.getMeins_list())
			{
				if (wa_meins.getMeins() == 1)
				{
					p_meins = wa_meins;
				}
			}
			
			
			l_bseg.add(new BsegTypeTable(0,p_meins.getMeins(),p_meins.getText45()));
		

		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
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
			priceList_list = new ArrayList<PriceList>(); 
			p_bkpf.setWaers(selectedCurrency.getCurrency());
			l_bseg = new ArrayList<BsegTypeTable>();
			l_bseg.add(new BsegTypeTable(0,p_meins.getMeins(),p_meins.getText45()));
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
				}			
				else{
					p_bkpf.setKursf(0); 
					throw new DAOException("Exchange rate not found");
				}
			}
			else
			{
				p_bkpf.setKursf(1);
			}
			priceList_list = new ArrayList<PriceList>();
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			priceList_list = priceListDao.findMatnrWithLastAmounts2(p_bkpf.getBukrs(),p_bkpf.getWaers());
			
			
			/*for(PriceList wa_priceList:p_priceListF4Bean.getPriceList_list())
			{ 
				if (wa_priceList.getBukrs().equals(p_bkpf.getBukrs()) && wa_priceList.getWaers().equals(p_bkpf.getWaers()) && wa_priceList.getRmatnr().getType() >=4)
				{					
					priceList_list.add(wa_priceList);
				}
				
			}*/
			
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bsegScrollPanel");
			reqCtx.update("form:bkpf_waers");
			reqCtx.update("form:bkpf_kursf");
			//reqCtx.update("form:bsegTable:bkpf_waers2");
			reqCtx.update("form:singleMatnr");
			//reqCtx.update("form:bkpf_waers");
			
			//for(int i = 0; i <= l_bseg.size()-1; i++)
			//{
				//reqCtx.update("form:bsegTable:"+i+":b_matnr");
			//}
			
		}	
		catch(Exception ex)
		{
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
				CustomerService personService = (CustomerService) appContext.getContext().getBean("customerService");
				p_customer_list = personService.dynamicSearch(searchCustomer);
				
				if (p_customer_list.size()==0){
					p_customer_list = new ArrayList<Customer>();
					addMessage("Инфо","Не найдено."); 
				}
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:customerTable");
				
			}
			catch (DAOException ex)
			{
				p_customer_list = new ArrayList<Customer>();
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:customerTable");
				addMessage("Info",ex.getMessage()); 
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
			reqCtx.update("form:bsegTable:"+i+":b_kunnr");
		}
		
		
		//int pos = p_bseg.getBuzei() -2;
		//System.out.println(pos);
		//reqCtx.update("form:bsegTable:"+pos+":b_kunnr");
		
		
				
  	}
  	//******************************************************************************************* 
  	public void assignSelectedWerks(){
  		try
  		{
  			if (selectedWerks!=null && selectedWerks.getWerks()!=null)
  			{
  				for(BsegTypeTable wa_bseg:l_bseg)
  		  		{
  		  			wa_bseg.getT_bseg().setWerks(selectedWerks.getWerks());
  		  			wa_bseg.setWerksName(selectedWerks.getText45()); 
  		  		}  
  			}
  			else{				  
  				for(BsegTypeTable wa_bseg:l_bseg)
  		  		{
  					wa_bseg.getT_bseg().setWerks(null);
  		  			wa_bseg.setWerksName(""); 
  		  		}
  			} 
  			
  			selectedWerks = new Werks(); 
  			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			
  			for(int i = 0; i <= l_bseg.size()-1; i++)
  			{
  				reqCtx.update("form:bsegTable:"+i+":b_werks");
  			}
  		}
  		catch(DAOException ex)
  		{
  			addMessage("Info",ex.getMessage());
  		}
  	}
    //***********************************Others***********************************************************
	public void refreshUSD(){
  		try
  		{
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
  	public void addRow(){
  		if (l_bseg.size()>0)
  		{
  			BsegTypeTable wa_bseg = l_bseg.get(l_bseg.size() - 1);	
  	  		l_bseg.add(new BsegTypeTable(l_bseg.size(),p_meins.getMeins(),p_meins.getText45()));
  	  		BsegTypeTable wa_bseg2 = l_bseg.get(l_bseg.size() - 1);
  	  		
  	  		wa_bseg2.getT_bseg().setLifnr(wa_bseg.getT_bseg().getLifnr());
  	  		wa_bseg2.setClientName(wa_bseg.getClientName());
  	  		wa_bseg2.getT_bseg().setWerks(wa_bseg.getT_bseg().getWerks());
  	  		wa_bseg2.setWerksName(wa_bseg.getWerksName());
  	  		
  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  	  		reqCtx.update("form:bsegScrollPanel"); 
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
		public BsegTypeTable(int a_index, Long a_meins, String a_meinsName){ 
			this.t_bseg.setMeins(a_meins);
			this.setMeinsName(a_meinsName);
			this.index = a_index;
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
	//***************************Save method********************************
	public void to_save() throws IOException{
		try { 
			PermissionController.canWrite(userData, Fdsog.transaction_id);
			throw new DAOException("У вас нет прав. Обратитесь  к администратору.");
			/*if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Select currency");
			}

			if (p_bkpf.getBukrs() == null || p_bkpf.getBukrs().isEmpty())
			{
				throw new DAOException("Select company");
			}  

			if (selectedBranch==null)
			{
				throw new DAOException("Select branch");
			}
			
			
			FinanceService financeService = (FinanceService) appContext.getContext().getBean("financeService");
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			Bseg p_bsegKredit = new Bseg();	
			Calendar cal = Calendar.getInstance(); 	 
			cal.setTime(p_bkpf.getBudat());
			
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fdsog.transaction_code); 
			p_bkpf.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf.setBrnch(getSelectedBranch().getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			p_bkpf.setAwtyp(1);
			
			int wa_buzei = 1;
			p_bsegKredit.setBuzei(wa_buzei);
			p_bsegKredit.setHkont("60100013");
			p_bsegKredit.setBukrs(p_bkpf.getBukrs());
			p_bsegKredit.setGjahr(p_bkpf.getGjahr());
			p_bsegKredit.setBschl("50");
			p_bsegKredit.setShkzg("H");
			
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 
				wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getT_bseg().setGjahr(p_bkpf.getGjahr());
				wa_bseg.getT_bseg().setHkont("12100001");
				
				wa_bseg.getT_bseg().setBschl("1");
				wa_bseg.getT_bseg().setShkzg("S");
				
				if (wa_bseg.getT_bseg().getHkont() == null || wa_bseg.getT_bseg().getHkont().isEmpty())
				{
					throw new DAOException("Choose GL account");
				} 
				if (wa_bseg.getT_bseg().getLifnr() == null)
				{
					throw new DAOException("Choose Creditor");
				}
				if (wa_bseg.getT_bseg().getMenge() <= 0)
				{
					throw new DAOException("Enter material amount");
				}
				if (wa_bseg.getT_bseg().getWerks() == null || wa_bseg.getT_bseg().getWerks() == 0)
				{
					throw new DAOException("Select werks");
				}
				if (wa_bseg.getT_bseg().getMatnr() == null || wa_bseg.getT_bseg().getMatnr() == 0)
				{
					throw new DAOException("Select matnr");
				}
				if (p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") ){
					if (wa_bseg.getT_bseg().getDmbtr()==0 ||wa_bseg.getT_bseg().getDmbtr()<0){
						throw new DAOException("Enter the amount");
					}
				}
				else if (p_bkpf.getWaers()!=null  && (!p_bkpf.getWaers().equals("USD")) ){
					if (wa_bseg.getT_bseg().getWrbtr()==0 ||wa_bseg.getT_bseg().getWrbtr()<0){
						
						throw new DAOException("Enter the amount");
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
		  	}
			l_bsegFinal.add(p_bsegKredit);
			 
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 
				wa_buzei = wa_buzei + 1;
				wa_bseg.getT_bseg().setBuzei(wa_buzei);
				l_bsegFinal.add(wa_bseg.getT_bseg());
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
			userData.setTrans_id(Fdsog.transaction_id);
			
			Long newDocBelnr = financeService.createAccountReceivableDocs(p_bkpf, l_bsegFinal, selectedBranch);
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath()  + "/accounting/assess/fa02.xhtml?belnr="+newDocBelnr+"&gjahr="+p_bkpf.getGjahr()+"&bukrs="+p_bkpf.getBukrs());
			
			*/
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		}  
		
	}
	
	public void changeBukrs()
	{
		werks_list.clear();
		werks_list.addAll(p_werksF4Bean.getByBukrs(p_bkpf.getBukrs()));
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:singleWerks");
		reqCtx.update("form:business_area");
		reqCtx.update("form:branch");
		 
	}
	
	public void changeBranch()
	{
		System.out.println(p_bkpf.getBrnch());
		selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());

	}
	
	//**********************************************************************		
}
