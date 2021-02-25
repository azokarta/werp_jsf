package accounting.assess;
 

import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import f4.MatnrF4;
import f4.WerksF4; 
import general.AppContext; 
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.output.tables.InvoiceListItemFkage;
import general.services.CustomerService;
import general.services.FinanceServiceLogistics;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Currency;
import general.tables.Customer; 
import general.tables.ExchangeRate;
import general.tables.Hkont;
import general.tables.Invoice;
import general.tables.Matnr;
import general.tables.Werks;
 

























import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "fkageBean", eager = true)
@ViewScoped
public class Fkage implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private final static String transaction_code = "FKAGE";
	private final static Long transaction_id = (long) 46;
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
	//***************************BukrsF4*******************************
	@ManagedProperty(value="#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;			
	
	public BukrsF4 getP_bukrsF4Bean() {
	  return p_bukrsF4Bean;
	}

	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
	  this.p_bukrsF4Bean = p_bukrsF4Bean;
	}

	
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
	//*****************************Hkont**********************************
	@ManagedProperty(value="#{hkontF4Bean}")
	private HkontF4 p_hkontF4Bean;
	public void setP_hkontF4Bean(HkontF4 p_hkontF4) {
	      this.p_hkontF4Bean = p_hkontF4;
	}
	
	List<Hkont> hkont_list = new ArrayList<Hkont>();	
	public List<Hkont> getHkont_list(){
		return hkont_list;
	}
	
	private Hkont selectedHkont;
	public Hkont getSelectedHkont() {
        return selectedHkont; 
    } 
    public void setSelectedHkont(Hkont p_selectedHkont) {
        this.selectedHkont = p_selectedHkont;
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
    //**********************************************************************	
  	public boolean isDisableDmbtr(){
  		//true Show, false hide
  		if (p_bkpf != null && p_bkpf.getWaers() != null)
  		{
  			if (!(p_bkpf.getWaers().equals("USD"))){
  	  			return true;
  	  		}
  			else
  			{
  				return false;
  			}
  		}
  		else
  		{
  			return true;
  		} 
  	}
  	public boolean isDisableWrbtr(){
  		//true Show, false hide
  		if (p_bkpf != null && p_bkpf.getWaers() != null)
  		{
  			if (p_bkpf.getWaers().equals("USD")){
  	  			return true;
  	  		}
  			else
  			{
  				return false;
  			}
  		}
  		else
  		{
  			return true;
  		} 
  	}
  	
  	
	//***************************MatnrF4*****************************************************
  	@ManagedProperty(value="#{matnrF4Bean}")
  	private MatnrF4 p_matnrF4Bean;			
  	
  	public MatnrF4 getP_matnrF4Bean() {
  	  return p_matnrF4Bean;
  	}

  	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
  	  this.p_matnrF4Bean = p_matnrF4Bean;
  	} 
	
  	public List<Matnr> p_matnr_list = new ArrayList<Matnr>();
  	public List<Matnr> getP_matnr_list() {
    	  return p_matnr_list;
    } 
  	
  	Map<Long,Matnr> l_matnr_map = new HashMap<Long,Matnr>();
  	
  	private Matnr selectedMatnr = new Matnr();
  	public Matnr getSelectedMatnr() {
  		return selectedMatnr;
  	}
  	public void setSelectedMatnr(Matnr selectedMatnr) {
  		this.selectedMatnr = selectedMatnr;
  	}
  	
  	public void assignSelectedMatnr(){
  		p_bseg.getT_bseg().setMatnr(selectedMatnr.getMatnr());
  		p_bseg.setMatnrName(selectedMatnr.getText45());
  		p_bseg.setMatnrType(selectedMatnr.getType());
  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  		int pos = p_bseg.getIndex();
  		reqCtx.update("form:bsegTable:"+pos+":b_matnr");
  		selectedMatnr = null; 
  	} 
  	//*****************************************************************************************************
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{ 
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Fkage.transaction_id);
			Calendar curDate = Calendar.getInstance();  
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime()); 
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fkage.transaction_code); 
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
			p_bkpf.setBlart("GE");
			
			
			InvoiceDao invoiceDao = (InvoiceDao) appContext.getContext().getBean("invoiceDao");
			l_invoice = invoiceDao.findInvoiceNotPricedFkage(" type_id = 1 and status_id = 2");

			
			
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
			p_bkpf.setWaers(selectedCurrency.getCurrency());
			 
			l_bseg = new ArrayList<BsegTypeTable>();
			l_bseg.add(new BsegTypeTable(0));
			
			
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
			 
			
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
			//reqCtx.update("form:bkpf_waers");
			//reqCtx.update("form:bsegTable:bkpf_waers2");
			//reqCtx.update("form:singleMatnr");
			//reqCtx.update("form:bkpf_waers");
			
			//for(int i = 0; i <= l_bseg.size()-1; i++)
			//{
				//reqCtx.update("form:bsegTable:"+i+":b_matnr");
			//}
			
		}	
		catch(DAOException ex)
		{
			System.out.println(ex.getMessage());
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
		}
		else{				  
			p_bkpf.setCustomer_id(null);
		}  
			
		selectedCustomer = new Customer(); 
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bkpf_customer_id");
		reqCtx.update("form:bkpf_customer_id_href");
		System.out.println(p_bkpf.getBukrs());
		
		//int pos = p_bseg.getBuzei() -2;
		//System.out.println(pos);
		//reqCtx.update("form:bsegTable:"+pos+":b_lifnr");
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
  			if (p_bkpf.getKursf() == 0 || p_bkpf.getKursf() < 0 ){
				throw new DAOException("Курсе равен 0 или меньше.");
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
  	public void addRow(){
  		if (l_bseg.size()>0)
  		{
  			BsegTypeTable wa_bseg = l_bseg.get(l_bseg.size() - 1);	
  	  		l_bseg.add(new BsegTypeTable(l_bseg.size()));
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
		public BsegTypeTable(int a_index){ 
			this.setIndex(a_index);
		}
		
		private Bseg t_bseg = new Bseg();
		private int index;
		
		private String clientName;
		private String matnrName;
		private String werksName;
		private String meinsName;
		private int matnrType;
		private Long parent_matnr;

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
		public int getMatnrType() {
			return matnrType;
		}
		public void setMatnrType(int matnrType) {
			this.matnrType = matnrType;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public Long getParent_matnr() {
			return parent_matnr;
		}
		public void setParent_matnr(Long parent_matnr) {
			this.parent_matnr = parent_matnr;
		}
		
	}
	
	
	
	
	//*****************************************************************************************************
	//***************************Save method********************************
	public void to_save() throws IOException{
		try { 
			PermissionController.canWrite(userData, Fkage.transaction_id);
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Select currency");
			}
			//System.out.println(p_bkpf.getBukrs());
 

			if (p_bkpf.getKursf() == 0 || p_bkpf.getKursf() < 0 ){
				throw new DAOException("Курсе равен 0 или меньше.");
			}
			
			//FinanceService financeService = (FinanceService) appContext.getContext().getBean("financeService");
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			List<Bseg> l_bsegDebet = new ArrayList<Bseg>();	
			Calendar cal = Calendar.getInstance(); 	 
			cal.setTime(p_bkpf.getBudat());
			
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fkage.transaction_code); 
			p_bkpf.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf.setBrnch(getSelectedBranch().getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			p_bkpf.setAwtyp(2);
			if (p_bkpf.getCustomer_id() == null)
			{
				throw new DAOException("Choose Creditor");
			}
			String hkontKredit="33100001";
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 
				wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getT_bseg().setGjahr(p_bkpf.getGjahr());
				wa_bseg.getT_bseg().setBschl("31");
				wa_bseg.getT_bseg().setShkzg("H");
				wa_bseg.getT_bseg().setHkont(hkontKredit);
				wa_bseg.getT_bseg().setLifnr(p_bkpf.getCustomer_id());
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
						wa_bseg.getT_bseg().setDmbtr( GeneralUtil.round(wa_bseg.getT_bseg().getWrbtr()/p_bkpf.getKursf(), 2));
					}
				}
				
		  	}
			
			
			for(BsegTypeTable wa_bsegType:l_bseg)
		  	{ 
				String wa_hkont = "";
				if (wa_bsegType.getT_bseg().getMatnr()==1 && p_bkpf.getBukrs().equals("1000")){wa_hkont = "13300001";}
				else if (wa_bsegType.getT_bseg().getMatnr()==4 && p_bkpf.getBukrs().equals("1000")){wa_hkont = "13300001";}//Roboclean
				else if (wa_bsegType.getT_bseg().getMatnr()==2 && p_bkpf.getBukrs().equals("1000")){wa_hkont = "13300002";}
				//else if (wa_bsegType.getT_bseg().getMatnr()==3 && p_bkpf.getBukrs().equals("1000")){wa_hkont = "13300004";}
				//else if (wa_bsegType.getT_bseg().getMatnr()==5 && p_bkpf.getBukrs().equals("1000")){wa_hkont = "13300005";}
				else if (wa_bsegType.getT_bseg().getMatnr()==815 && p_bkpf.getBukrs().equals("2000")){wa_hkont = "13300001";} //RAINBOW
				else if (wa_bsegType.getT_bseg().getMatnr()==817 && p_bkpf.getBukrs().equals("2000")){wa_hkont = "13300002";} //Rexwat ECO
				else if ((wa_bsegType.getT_bseg().getMatnr()==816||wa_bsegType.getT_bseg().getMatnr()==910 ) && p_bkpf.getBukrs().equals("2000")){wa_hkont = "13300003";} //Rexwat Atlas
				else if (p_bkpf.getBukrs().equals("1000") ||  p_bkpf.getBukrs().equals("2000"))
				{
					//System.out.println(wa_bsegType.getT_bseg().getMatnr()+" zzzz");
					Matnr wa_matnr = l_matnr_map.get(wa_bsegType.getT_bseg().getMatnr());
					if (wa_bsegType.getParent_matnr()!=null)
					{
						if (wa_bsegType.getParent_matnr()==1  && p_bkpf.getBukrs().equals("1000")) wa_hkont = "13300003"; //13300003 Roboclean parts 1
						else if (wa_bsegType.getParent_matnr()==4  && p_bkpf.getBukrs().equals("1000")) wa_hkont = "13300003"; //13300003 Roboclean parts 1
						else if (wa_bsegType.getParent_matnr()==2  && p_bkpf.getBukrs().equals("1000")) wa_hkont = "13300004"; // 13300004 Cebilon parts 2
						else if (wa_bsegType.getParent_matnr()==815 && p_bkpf.getBukrs().equals("2000")) wa_hkont = "13300004"; // 13300004 Rainbow parts 815
						else if ((wa_bsegType.getParent_matnr()==816||wa_matnr.getParent_matnr()==910) && p_bkpf.getBukrs().equals("2000")) wa_hkont = "13300006"; // 13300006 Rexwat Atlas parts 816
						else if (wa_bsegType.getParent_matnr()==817 && p_bkpf.getBukrs().equals("2000")) wa_hkont = "13300005"; // 13300005 Rexwat ECO parts 817
						 
					}
					else if (p_bkpf.getBukrs().equals("1000") ||  p_bkpf.getBukrs().equals("2000")) wa_hkont = "13300030"; //Others
					
				}
				else if (p_bkpf.getBukrs().equals("3000")){
					wa_hkont = "13300001";
				}
				else if (p_bkpf.getBukrs().equals("6000")){
					wa_hkont = "13300001";
				}
				//else if (wa_bsegType.getT_bseg().getMatnr()==5 && p_bkpf.getBukrs().equals("2000")){wa_hkont = "13300006";}
				else throw new DAOException("Счет главной книги для материала не указан.");
				//13300001	Roboclean
				//13300002	Cebilon
				//13300003	Roboclean parts
				//13300004	Cebilon parts
				//13300001	Rainbow 815	RAINBOW
				//13300002	Rexwat ECO 817	REXWAT ECO
				//13300003	Rexwat Atlas 816	REXWAT ATLAS
				//13300004	Rainbow parts
				//13300005	Rexwat ECO parts
				//13300006	Rexwat Atlas parts

				
				
				//System.out.println(wa_hkont);
				//System.out.println(wa_hkont +" "+wa_bsegType.getMatnrType()+" "+wa_bsegType.getT_bseg().getDmbtr());
				int count = 0;
				for(Bseg wa_bseg:l_bsegDebet)
				{
					if (wa_bseg.getHkont().equals(wa_hkont))
					{
						//System.out.print("found "+wa_bseg.getDmbtr()+" "+wa_bsegType.getT_bseg().getDmbtr());
						count = count + 1;													
						wa_bseg.setDmbtr(GeneralUtil.round(wa_bseg.getDmbtr()+wa_bsegType.getT_bseg().getDmbtr(),2));
						wa_bseg.setWrbtr(GeneralUtil.round(wa_bseg.getWrbtr()+wa_bsegType.getT_bseg().getWrbtr(),2));
						
						
					}
				}
				if (count==0)
				{
					//System.out.println("new "+ wa_hkont);
					Bseg wa_bseg = new Bseg(); 
					wa_bseg.setHkont(wa_hkont);
					wa_bseg.setBukrs(p_bkpf.getBukrs());
					wa_bseg.setGjahr(p_bkpf.getGjahr());
					wa_bseg.setBschl("40");
					wa_bseg.setShkzg("S");
					wa_bseg.setDmbtr(GeneralUtil.round(wa_bseg.getDmbtr()+wa_bsegType.getT_bseg().getDmbtr(),2));
					wa_bseg.setWrbtr(GeneralUtil.round(wa_bseg.getWrbtr()+wa_bsegType.getT_bseg().getWrbtr(),2));
					l_bsegDebet.add(wa_bseg);									
		  		}
				
		  	}
			
			int wa_buzei = 0;
			
			for(Bseg wa_bseg:l_bsegDebet)
			{
				//System.out.println(111);
				wa_buzei = wa_buzei + 1;
				wa_bseg.setBuzei(wa_buzei);
				l_bsegFinal.add(wa_bseg);
			}
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 
				wa_buzei = wa_buzei + 1;
				wa_bseg.getT_bseg().setBuzei(wa_buzei);
				l_bsegFinal.add(wa_bseg.getT_bseg());
		  	}
			//System.out.println(l_bsegFinal.size());
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bsegTable");
			/*
			for(Bseg wa_bseg:l_bsegFinal)
		  	{ 
				System.out.print(wa_bseg.getBukrs()+" ");
				System.out.print(wa_bseg.getDmbtr()+" ");
				System.out.println(wa_bseg.getWrbtr()); 
		  	}
			*/
			//throw new DAOException("ZZ");
			
			userData.setNew_fi_doc(true);
			userData.setTrans_id(Fkage.transaction_id);
			FinanceServiceLogistics financeServiceLogistics = (FinanceServiceLogistics) appContext.getContext().getBean("financeServiceLogistics");
			Long newDocBelnr = financeServiceLogistics.createFkage(p_bkpf, l_bsegFinal, selectedBranch);
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
		p_matnr_list.clear();
		l_matnr_map.clear();
		for(Matnr wa_matnr:p_matnrF4Bean.getMatnr_list())
		{
			p_matnr_list.add(wa_matnr);
			l_matnr_map.put(wa_matnr.getMatnr(), wa_matnr);
		}
		
		werks_list.clear();
		werks_list.addAll(p_werksF4Bean.getByBukrs(p_bkpf.getBukrs()));
		l_bseg.clear();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:singleWerks");
		reqCtx.update("form:singleMatnr");
		reqCtx.update("form:business_area");
		reqCtx.update("form:branch");
	  	reqCtx.update("form:bsegScrollPanel");
		 
	}
	public void changeBranch()
	{
		selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());

	}
	
	//**********************************************************************	
	//************************Invoice***************************************
	List<Invoice> l_invoice = new ArrayList<Invoice>();
	
	public List<Invoice> getL_invoice() {
		return l_invoice;
	}

	public void setL_invoice(List<Invoice> l_invoice) {
		this.l_invoice = l_invoice;
	}

	List<Invoice> l_selected_invoice = new ArrayList<Invoice>();
	public List<Invoice> getL_selected_invoice() {
		return l_selected_invoice;
	}

	public void setL_selected_invoice(List<Invoice> l_selected_invoice) {
		this.l_selected_invoice = l_selected_invoice;
	}
	
	List<InvoiceListItemFkage> l_selected_invoice_item = new ArrayList<InvoiceListItemFkage>();
	public List<InvoiceListItemFkage> getL_selected_invoice_item() {
		return l_selected_invoice_item;
	}
	public void setL_selected_invoice_item(
			List<InvoiceListItemFkage> l_selected_invoice_item) {
		this.l_selected_invoice_item = l_selected_invoice_item;
	}
	
	Invoice selectedInvoice = new Invoice();
	public Invoice getSelectedInvoice() {
		return selectedInvoice;
	}
	public void setSelectedInvoice(Invoice selectedInvoice) {
		this.selectedInvoice = selectedInvoice;
	}
	
	public void assignInvoice()
	{
		try{
			if (selectedInvoice!=null)
			{	
				
				
				FinanceServiceLogistics financeServiceLogistics = (FinanceServiceLogistics) appContext.getContext().getBean("financeServiceLogistics");
				//l_selected_invoice_item = ;
				List<InvoiceListItemFkage> l_selected_invoice_item2 = new ArrayList<InvoiceListItemFkage>();
				l_selected_invoice_item2.addAll(financeServiceLogistics.getInvoiceListToFkage(selectedInvoice));
				
				if (l_selected_invoice_item!=null && l_selected_invoice_item.size()>1 && l_selected_invoice_item2!=null && l_selected_invoice_item2.size()>1)
				{
					if (!p_bkpf.getBukrs().equals(l_selected_invoice_item2.get(0).getBukrs()))
					{
						throw new DAOException("Заказ "+l_selected_invoice_item.get(0).getInvoice_id()+" и заказ "+l_selected_invoice_item.get(0).getInvoice_id()+" разные компании.");
					}
					
					if (!p_bkpf.getWaers().equals(l_selected_invoice_item2.get(0).getWaers()))
					{
						throw new DAOException("Заказ "+l_selected_invoice_item.get(0).getInvoice_id()+" и заказ "+l_selected_invoice_item.get(0).getInvoice_id()+" разные валюты.");
					}
					
					if (!p_bkpf.getCustomer_id().equals(l_selected_invoice_item2.get(0).getCustomer_id()))
					{
						throw new DAOException("Заказ "+l_selected_invoice_item.get(0).getInvoice_id()+" и заказ "+l_selected_invoice_item.get(0).getInvoice_id()+" разные контрагенты.");
					}
				}
				l_selected_invoice_item.addAll(l_selected_invoice_item2);
				
				
				for(int i =l_invoice.size()-1; i>=0;i--)
				{
					if (l_invoice.get(i).getId().equals(selectedInvoice.getId()))
					{
						l_invoice.remove(i);
					}
				}
				
				
				
				
				l_selected_invoice.add(selectedInvoice);
				
				if (l_selected_invoice.size()==1)
				{
					p_bkpf.setWaers(l_selected_invoice_item.get(0).getWaers());
					p_bkpf.setBukrs(l_selected_invoice_item.get(0).getBukrs());
					p_bkpf.setCustomer_id(l_selected_invoice_item.get(0).getCustomer_id());
					
					//p_bkpf.setInvoice_id(selectedInvoice.getId());
					changeBukrs();
					if (!(p_bkpf.getWaers().equals("USD")))
					{	
						ExchangeRate wa_er = new ExchangeRate(); 
						wa_er = p_exchangeRateF4.getL_er_map_national().get('1'+p_bkpf.getWaers());
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
				}
				
				l_bseg = new ArrayList<BsegTypeTable>();
				//InvoiceItemDao invoiceItemDao = (InvoiceItemDao) appContext.getContext().getBean("invoiceItemDao");
				//l_invoice_item = invoiceItemDao.findAll(" invoice_id =" +selectedInvoice.getId());
				for (InvoiceListItemFkage wa_ii:l_selected_invoice_item)
				{
					BsegTypeTable wa_bseg = new BsegTypeTable();
					wa_bseg.getT_bseg().setMatnr(wa_ii.getMatnr());
					wa_bseg.getT_bseg().setMenge(wa_ii.getMenge());
					wa_bseg.getT_bseg().setLifnr(wa_ii.getCustomer_id());
					//Matnr wa_matnr = p_matnrF4Bean.getL_matnr_map().get(wa_ii.getMatnr_id());
					wa_bseg.setMatnrType(wa_ii.getMatnr_type());
					wa_bseg.setMatnrName(wa_ii.getMatnr_name());
					wa_bseg.setParent_matnr(wa_ii.getParent_matnr());
					wa_bseg.getT_bseg().setInvoice_id(wa_ii.getInvoice_id());
					if (p_bkpf.getWaers().equals("USD"))
					{
						wa_bseg.getT_bseg().setDmbtr(wa_ii.getTotal_price());
					}
					else
					{
						
						wa_bseg.getT_bseg().setWrbtr(wa_ii.getTotal_price());
						wa_bseg.getT_bseg().setDmbtr(GeneralUtil.round(wa_ii.getTotal_price()/p_bkpf.getKursf(), 2));
					}
					l_bseg.add(wa_bseg);
					
					
				}
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
			}
		}
		catch(DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
		
	}
	public void clearInvoice(Long a_invoice_id)
	{
		try{
			
			p_bkpf.setInvoice_id(null);
			
			for(int i =l_selected_invoice.size()-1; i>=0;i--)
			{
				if (l_selected_invoice.get(i).getId().equals(a_invoice_id))
				{
					l_invoice.add(l_selected_invoice.get(i));
					l_selected_invoice.remove(i);
				}
			}
			
			for(int i =l_selected_invoice_item.size()-1; i>=0;i--)
			{
				if (l_selected_invoice_item.get(i).getInvoice_id().equals(a_invoice_id))
				{
					l_selected_invoice_item.remove(i);
				}
			}
			
			for(int i =l_bseg.size()-1; i>=0;i--)
			{
				if (l_bseg.get(i).getT_bseg().getInvoice_id().equals(a_invoice_id))
				{
					//l_bseg.remove(i);
					l_bseg.remove(l_bseg.get(i));
				}
			}
			System.out.println(l_bseg.size()+" size");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		}
		catch(DAOException ex)
		{
			
		}
		
	}
	//**********************************************************************
	
	
}
