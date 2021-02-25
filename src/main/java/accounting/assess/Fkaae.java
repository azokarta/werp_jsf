package accounting.assess;
 

import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import f4.MatnrF4;
import general.AppContext; 
import general.PermissionController;
import general.dao.DAOException;
import general.services.CustomerService;
import general.services.PrebkpfService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Currency;
import general.tables.Customer; 
import general.tables.ExchangeRate;
import general.tables.Hkont;
import general.tables.Matnr;
import general.tables.Prebkpf;
import general.tables.Prebseg;

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
import org.springframework.beans.BeanUtils;

import user.User;

@ManagedBean(name = "fkaaeBean", eager = true)
@ViewScoped
public class Fkaae implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private final static String transaction_code = "FKAAE";
	private final static Long transaction_id = (long) 49;
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
			PermissionController.canRead(userData,Fkaae.transaction_id);
			
			Calendar curDate = Calendar.getInstance();
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime()); 
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fkaae.transaction_code); 
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
			
			


			
			
			l_bseg.add(new BsegTypeTable(0));

			
			
			
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

		
		//int pos = p_bseg.getBuzei() -2;
		//System.out.println(pos);
		//reqCtx.update("form:bsegTable:"+pos+":b_lifnr");
  	}
  	//******************************************************************************************* 

    //***********************************Others***********************************************************
	public void refreshUSD(){
  		try
  		{
  			if (p_bkpf.getKursf() == 0 || p_bkpf.getKursf() < 0 ){
				throw new DAOException("Курсе равен 0 или меньше.");
			}
  			
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
		private int oper_type = 0;
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
		public int getOper_type() {
			return oper_type;
		}
		public void setOper_type(int oper_type) {
			this.oper_type = oper_type;
		}
		
	}
	
	
	
	
	//*****************************************************************************************************
	//***************************Save method********************************
	public void to_save() throws IOException{
		try { 
			PermissionController.canWrite(userData, Fkaae.transaction_id);
			
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Select currency");
			}

			if (p_bkpf.getBukrs() == null || p_bkpf.getBukrs().isEmpty())
			{
				throw new DAOException("Select company");
			}  

			
			
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			Bseg p_bsegKredit = new Bseg();
			Calendar cal = Calendar.getInstance(); 	 
			cal.setTime(p_bkpf.getBudat());
			
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fkaae.transaction_code); 
			p_bkpf.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf.setBrnch(getSelectedBranch().getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			p_bkpf.setAwtyp(2);
			int wa_buzei = 1;
			p_bsegKredit.setBuzei(wa_buzei);
			
			String hkontKredit="33100001";
			p_bsegKredit.setHkont(hkontKredit);
			p_bsegKredit.setBukrs(p_bkpf.getBukrs());
			p_bsegKredit.setGjahr(p_bkpf.getGjahr());
			p_bsegKredit.setBschl("31");
			p_bsegKredit.setShkzg("H");
			p_bsegKredit.setLifnr(p_bkpf.getCustomer_id());
			if (p_bsegKredit.getLifnr() == null)
			{
				throw new DAOException("Choose Creditor");
			}
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  		{ 
				wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getT_bseg().setGjahr(p_bkpf.getGjahr());
				wa_bseg.getT_bseg().setBschl("40");
				wa_bseg.getT_bseg().setShkzg("S");
				if (wa_bseg.getT_bseg().getHkont() == null || wa_bseg.getT_bseg().getHkont().isEmpty())
				{
					throw new DAOException("Choose GL account");
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
			
			//for(Bseg wa_bseg:l_bsegFinal)
		  		//{ 
			//	System.out.println(wa_bseg.getBukrs());
			//	System.out.println(wa_bseg.getDmbtr());
			//	System.out.println(wa_bseg.getWrbtr()); 
		  		//}
			
//			userData.setNew_fi_doc(true);
//			userData.setTrans_id(Fkaae.transaction_id);
			
			Calendar curDate = Calendar.getInstance();
			PrebkpfService prebkpfService = (PrebkpfService) appContext.getContext().getBean("prebkpfService");
			Prebkpf p_prebkpf = new Prebkpf();
			BeanUtils.copyProperties(p_bkpf, p_prebkpf);
			p_prebkpf.setStatus(0);
			p_prebkpf.setCreated_date(curDate.getTime());
			p_prebkpf.setHkont_d(l_bseg.get(0).getT_bseg().getHkont());
			p_prebkpf.setHkont_k("33100001");
			if (p_prebkpf.getWaers().equals("USD"))
			{
				p_prebkpf.setSumma(p_bsegKredit.getDmbtr());
			}
			else
			{
				p_prebkpf.setSumma(p_bsegKredit.getWrbtr());
			}
			List<Prebseg> l_prebseg = new ArrayList<Prebseg>();
			for(Bseg wa_bseg:l_bsegFinal)
			{
				Prebseg wa_prebseg = new Prebseg();
				BeanUtils.copyProperties(wa_bseg, wa_prebseg);
				l_prebseg.add(wa_prebseg);
			}
			prebkpfService.save(p_prebkpf,l_prebseg);
			
//			Long newDocBelnr = financeService.createAccountPayableDocs(p_bkpf, l_bsegFinal, selectedBranch);
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//	   	 	context.redirect(context.getRequestContextPath()  + "/accounting/assess/fa02.xhtml?belnr="+newDocBelnr+"&gjahr="+p_bkpf.getGjahr()+"&bukrs="+p_bkpf.getBukrs());
			context.redirect(context.getRequestContextPath()  + "/accounting/assess/fkaae.xhtml");
			
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		}  
		
	}
	public void changeBukrs()
	{
		hkont_list.clear();
		selectedBranch = new Branch();
		p_bkpf.setBlart(null);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		for(int i = 0; i <= l_bseg.size()-1; i++)
		{
			l_bseg.get(i).setOper_type(0);
			reqCtx.update("form:bsegTable:"+i+":b_oper_type");
			reqCtx.update("form:bsegTable:"+i+":b_hkont");
		}
		reqCtx.update("form:bkpf_blart");
		reqCtx.update("form:business_area");
		reqCtx.update("form:branch");
	}
	
	public void changeOperType()
	{
		hkont_list.clear();
		//Адм. расходы
		if (l_bseg.get(0).getOper_type()==1)
		{
			p_bkpf.setBlart("AE");
		}		
		//Соц. расходы
		else if (l_bseg.get(0).getOper_type()==2)
		{
			p_bkpf.setBlart("SE");
		}
		//Тран. расходы
		else if (l_bseg.get(0).getOper_type()==3)
		{
			p_bkpf.setBlart("TE");
		}
		//Пошлины
		else if (l_bseg.get(0).getOper_type()==4)
		{
			p_bkpf.setBlart("CE");
		}
		//Пенсия, Соц. налог, ИПН
		else if (l_bseg.get(0).getOper_type()==5)
		{
			p_bkpf.setBlart("AE");
		}
		
		
		
		
		for(Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
		{
			Long wa_hkont_long = Long.parseLong( wa_hkont.getHkont());
			//Адм. расходы
			if ((wa_hkont_long == 74700001 || (wa_hkont_long >= 72100005 && wa_hkont_long<= 72100030))&& wa_hkont.getBukrs().equals(p_bkpf.getBukrs()) && l_bseg.get(0).getOper_type()==1)
			{
				hkont_list.add(wa_hkont);				
			}
			//Соц. расходы
			else if (wa_hkont_long >= 74700003 && wa_hkont_long<= 74700030 && wa_hkont.getBukrs().equals(p_bkpf.getBukrs()) && l_bseg.get(0).getOper_type()==2)
			{
				hkont_list.add(wa_hkont);
			}
			//Тран. расходы
			else if (wa_hkont_long >= 74700101 && wa_hkont_long<= 74700104 && wa_hkont.getBukrs().equals(p_bkpf.getBukrs()) && l_bseg.get(0).getOper_type()==3)
			{
				hkont_list.add(wa_hkont);
			}
			//Пошлины
			else if (wa_hkont_long == 74700105 && wa_hkont.getBukrs().equals(p_bkpf.getBukrs()) && l_bseg.get(0).getOper_type()==4)
			{
				hkont_list.add(wa_hkont);
			}
			//Пенсия, Соц. налог, ИПН
			else if (wa_hkont_long >= 71100002 && wa_hkont_long<= 71100004 && wa_hkont.getBukrs().equals(p_bkpf.getBukrs()) && l_bseg.get(0).getOper_type()==5)
			{
				hkont_list.add(wa_hkont);
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		for(int i = 0; i <= l_bseg.size()-1; i++)
		{
			reqCtx.update("form:bsegTable:"+i+":b_hkont");
		}
		reqCtx.update("form:bkpf_blart");
		reqCtx.update("form:business_area");
		reqCtx.update("form:branch");
		reqCtx.update("form:selectedCashOffice");
	}
	
	public void changeBranch()
	{
		selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());
	}
	
	//**********************************************************************
	

	
}
