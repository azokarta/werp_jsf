package accounting.assess;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
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
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.springframework.beans.BeanUtils;

import user.User;

@ManagedBean(name = "ftaxkBean", eager = true)
@ViewScoped
public class Ftaxk implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FTAXK";
	private final static Long transaction_id = (long) 57;
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
  	
  	
	
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{ 
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Ftaxk.transaction_id);
			Calendar curDate = Calendar.getInstance();
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime()); 
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Ftaxk.transaction_code); 
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
			//p_bkpf.setBlart("AE");
			

			
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
		
		
		//int pos = p_bseg.getBuzei() -2;
		//System.out.println(pos);
		//reqCtx.update("form:bsegTable:"+pos+":b_lifnr");
  	}
  	//******************************************************************************************* 

    //***********************************Others***********************************************************
  	public void selectBlart(final AjaxBehaviorEvent event){
  		if (((UIOutput)event.getSource()).getValue().equals("31100001"))
  		{
  			p_bkpf.setBlart("KT");
  		}
  		else if (((UIOutput)event.getSource()).getValue().equals("31300001"))
  		{
  			p_bkpf.setBlart("NT");
  		}
  		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bkpf_blart");
  		
  	}
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
			PermissionController.canWrite(userData, Ftaxk.transaction_id);
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Select currency");
			}

			if (p_bkpf.getBukrs() == null || p_bkpf.getBukrs().isEmpty())
			{
				throw new DAOException("Select company");
			}  
			
			if (p_bkpf.getKursf() == 0 || p_bkpf.getKursf() < 0 ){
				throw new DAOException("Курсе равен 0 или меньше.");
			}
			
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			Bseg p_bsegDebet = new Bseg();
			Calendar cal = Calendar.getInstance(); 	 
			cal.setTime(p_bkpf.getBudat());
			System.out.println(p_bkpf.getBukrs());
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Ftaxk.transaction_code); 
			p_bkpf.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf.setBrnch(getSelectedBranch().getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			if (p_bkpf.getCustomer_id()==null)
			{
				throw new DAOException("Choose Creditor");
			}
			
			int wa_buzei = 1;
			p_bsegDebet.setBuzei(wa_buzei); 
			p_bsegDebet.setBukrs(p_bkpf.getBukrs());
			p_bsegDebet.setGjahr(p_bkpf.getGjahr());
			p_bsegDebet.setBschl("40");
			p_bsegDebet.setShkzg("S");
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 
				
				wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getT_bseg().setGjahr(p_bkpf.getGjahr());
				wa_bseg.getT_bseg().setBschl("31");
				wa_bseg.getT_bseg().setShkzg("H");
				wa_bseg.getT_bseg().setLifnr(p_bkpf.getCustomer_id());
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
				
				if (wa_bseg.getT_bseg().getHkont().equals("31100001"))
		  		{
					p_bsegDebet.setHkont("77100001");
		  		}
		  		else if (wa_bseg.getT_bseg().getHkont().equals("31300001"))
		  		{
		  			p_bsegDebet.setHkont("77100002");
		  		}
				
		  	}
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 
				p_bsegDebet.setDmbtr(p_bsegDebet.getDmbtr()+wa_bseg.getT_bseg().getDmbtr());
				p_bsegDebet.setWrbtr(p_bsegDebet.getWrbtr()+wa_bseg.getT_bseg().getWrbtr());
		  	}
			l_bsegFinal.add(p_bsegDebet);
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
//			userData.setTrans_id(Ftaxk.transaction_id);
			
			Calendar curDate = Calendar.getInstance();
			PrebkpfService prebkpfService = (PrebkpfService) appContext.getContext().getBean("prebkpfService");
			Prebkpf p_prebkpf = new Prebkpf();
			BeanUtils.copyProperties(p_bkpf, p_prebkpf);
			p_prebkpf.setStatus(0);
			p_prebkpf.setCreated_date(curDate.getTime());
			p_prebkpf.setHkont_d(p_bsegDebet.getHkont());
			p_prebkpf.setHkont_k(l_bseg.get(0).getT_bseg().getHkont());
			if (p_prebkpf.getWaers().equals("USD"))
			{
				p_prebkpf.setSumma(p_bsegDebet.getDmbtr());
			}
			else
			{
				p_prebkpf.setSumma(p_bsegDebet.getWrbtr());
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
	   	 	context.redirect(context.getRequestContextPath()  + "/finance/cashbankoperations/faco01.xhtml");
			
			
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		}  
		
	}
	public void changeBukrs()
	{
		hkont_list.clear();
		for(Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
		{
			if ((wa_hkont.getHkont().equals("31100001") || wa_hkont.getHkont().equals("31300001")) && wa_hkont.getBukrs().equals(p_bkpf.getBukrs()))
			{
				hkont_list.add(wa_hkont);
			}
		}
		

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		for(int i = 0; i <= l_bseg.size()-1; i++)
		{
			reqCtx.update("form:bsegTable:"+i+":b_hkont");
		}
		reqCtx.update("form:bukrs");
		reqCtx.update("form:business_area");
		reqCtx.update("form:branch");
		 
	}
	
	public void changeBranch()
	{
		selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());

	}
	//**********************************************************************	
}
