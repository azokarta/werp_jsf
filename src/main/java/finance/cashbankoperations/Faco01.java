package finance.cashbankoperations;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4; 
import general.AppContext;
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.SystemLocale;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.DAOException;
import general.dao.FmglflextDao;
import general.dao.TransactionDao;
import general.output.tables.CashBankAccountStatement;
import general.output.tables.Podotchet;
import general.services.CustomerService;
import general.services.FinancePodotchet;
import general.services.PrebkpfService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Currency; 
import general.tables.Customer;
import general.tables.ExchangeRate;
import general.tables.Fmglflext;
import general.tables.Prebkpf;
import general.tables.Prebseg;
import general.tables.Transaction;

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
import org.springframework.beans.BeanUtils;

import user.User;

@ManagedBean(name = "faco01Bean", eager = true)
@ViewScoped
public class Faco01 implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FACO01";
	private final static Long transaction_id = (long) 24;
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
	
	@ManagedProperty(value="#{systemLocale}")
  	private SystemLocale p_sysLoc;
    public SystemLocale getP_sysLoc() {
		return p_sysLoc;
	}
	public void setP_sysLoc(SystemLocale p_sysLoc) {
		this.p_sysLoc = p_sysLoc;
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
  		if (voznag)
  		{
  			return true;
  		}
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
  		if (voznag)
  		{
  			return true;
  		}
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
  	
  	private boolean voznag = false;  	
	public boolean isVoznag() {
		return voznag;
	}
	public void setVoznag(boolean voznag) {
		this.voznag = voznag;
	}

	//***************************MatnrF4*****************************************************
  	//*****************************************************************************************************
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{  
			
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) { 
	            return; // Skip ajax requests.
	        }
			PermissionController.canRead(userData,Faco01.transaction_id);
			Calendar curDate = Calendar.getInstance();
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime()); 
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Faco01.transaction_code); 
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
			p_bkpf.setBlart("KP");
			searchCustomer.setFiz_yur(0);
			trName = getTransactionName(p_sysLoc.getCurrentLanguage(),Faco01.getTransactionId());
			if (p_bkpf_VZ!=null && p_bkpf_VZ.getBukrs()!=null && p_bkpf_VZ.getGjahr()>2004 && p_bkpf_VZ.getBlart()!=null)
			{
				BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
				p_bkpf_VZ = bkpfDao.findOriginalSingleBkpf(p_bkpf_VZ.getBelnr(),p_bkpf_VZ.getGjahr(),p_bkpf_VZ.getBukrs());
				if (p_bkpf_VZ!=null && p_bkpf_VZ.getBelnr()!=null && p_bkpf_VZ.getGjahr()>2004 && p_bkpf_VZ.getClosed()==0)
				{
					clearAndAddBseg();
					voznag = true;
					p_bkpf.setBukrs(p_bkpf_VZ.getBukrs());
//					p_bkpf.setDmbtr(p_bkpf_VZ.getDmbtr());
//					p_bkpf.setWrbtr(p_bkpf_VZ.getWrbtr());
					p_bkpf.setCustomer_id(p_bkpf_VZ.getCustomer_id());
					p_bkpf.setWaers(p_bkpf_VZ.getWaers());
					selectedCurrency.setCurrency(p_bkpf_VZ.getWaers());
					if (!(selectedCurrency.getCurrency().equals("USD")))
					{	
						ExchangeRate wa_er = new ExchangeRate(); 
						//ExchangeRateDao exchangeRateDao = (ExchangeRateDao) appContext.getContext().getBean("exchangeRateDao"); 
						wa_er = p_exchangeRateF4.getL_er_map_national().get('1'+selectedCurrency.getCurrency());
						if (wa_er!=null)
						{
							p_bkpf.setKursf(wa_er.getSc_value()); 
							p_bkpf.setWaers(selectedCurrency.getCurrency());
						}			
						else{
							
							throw new DAOException("Exchange rate not found");
						}				
						
					}
					else
					{
						p_bkpf.setWaers(selectedCurrency.getCurrency());
						p_bkpf.setKursf(1);
					}
					
					for(BsegTypeTable wa_bseg:l_bseg)
					{
						wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
						wa_bseg.getT_bseg().setDmbtr(p_bkpf_VZ.getDmbtr());
						wa_bseg.getT_bseg().setWrbtr(p_bkpf_VZ.getWrbtr());
						wa_bseg.getT_bseg().setLifnr(p_bkpf_VZ.getCustomer_id());
					}
					trName = "Оплата за вознаграждение";
				}
			}
			
			

			
			
			
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	

	//*************************************************************************
	//**********Currency****************************************************************************
	
	private String trName = "";	
	public String getTrName() {
		return trName;
	}
	public void setTrName(String trName) {
		this.trName = trName;
	}

	private Currency selectedCurrency;
	public Currency getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(Currency selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}
	public void assignSelectedCurrency(){
		
		
		
		try{ 
			if (checkFilling(3))
			{
				List<Podotchet> l_pdo = new ArrayList<Podotchet>();	
				l_bseg = new ArrayList<BsegTypeTable>();
				l_bseg.add(new BsegTypeTable(0));
				
				
				if (!(selectedCurrency.getCurrency().equals("USD")))
				{	
					ExchangeRate wa_er = new ExchangeRate(); 
					//ExchangeRateDao exchangeRateDao = (ExchangeRateDao) appContext.getContext().getBean("exchangeRateDao"); 
					wa_er = p_exchangeRateF4.getL_er_map_national().get('1'+selectedCurrency.getCurrency());
					if (wa_er!=null)
					{
						getCashOfficeHkont();
						p_bkpf.setKursf(wa_er.getSc_value()); 
						p_bkpf.setWaers(selectedCurrency.getCurrency());
						FinancePodotchet financePodotchet = (FinancePodotchet) appContext.getContext().getBean("financePodotchet");
						l_pdo = financePodotchet.getPodotchetList(p_bkpf.getBukrs(), p_bkpf.getBrnch(), p_bkpf.getWaers());
					}			
					else{
						
						throw new DAOException("Exchange rate not found");
					}
					
					
				}
				else
				{
					getCashOfficeHkont();
					p_bkpf.setWaers(selectedCurrency.getCurrency());
					p_bkpf.setKursf(1);
					FinancePodotchet financePodotchet = (FinancePodotchet) appContext.getContext().getBean("financePodotchet");
					l_pdo = financePodotchet.getPodotchetList(p_bkpf.getBukrs(), p_bkpf.getBrnch(), p_bkpf.getWaers());
				}
				
				l_as2 = new ArrayList<CashBankAccountStatement>();
				int index = 0;
				for(CashBankAccountStatement wa_as:l_as)
				{
					if (p_bkpf.getWaers()!=null && wa_as.getWaers().equals(p_bkpf.getWaers()))
					{
						wa_as.setHkont_name(wa_as.getHkont_name()+" "+wa_as.getWaers());
						wa_as.setIndex(index);
						l_as2.add(wa_as);
						index++;
					}
				}
				if (l_pdo!=null && l_pdo.size()>0)
				{
					for(Podotchet wa_pdo:l_pdo)
					{
						CashBankAccountStatement wa_as = new CashBankAccountStatement();
						wa_as.setAmount(wa_pdo.getSumma());
						wa_as.setHkont("12500001");
						wa_as.setHkont_name(Validation.returnFioInitials(wa_pdo.getFirstname(), wa_pdo.getLastname(), wa_pdo.getMiddlename()));
						wa_as.setWaers(wa_pdo.getWaers());
						wa_as.setCustomer_id(wa_pdo.getCustomer_id());
						wa_as.setHkont_name(wa_as.getHkont_name()+" "+wa_as.getWaers()+" "+wa_as.getAmount());
						wa_as.setIndex(index);
						l_as2.add(wa_as);
						index++;
					}
				}
			}
			else
			{
				selectedCurrency = null;
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
	public void assignSelectedCurrencyVoznag(){
		
		
		
		try{ 
			if (checkFilling(3))
			{
				
				
				l_as2 = new ArrayList<CashBankAccountStatement>();
				int index = 0;
				getCashOfficeHkont();
				for(CashBankAccountStatement wa_as:l_as)
				{
					if (p_bkpf.getWaers()!=null && wa_as.getWaers().equals(p_bkpf.getWaers()))
					{
						wa_as.setHkont_name(wa_as.getHkont_name()+" "+wa_as.getWaers());
						wa_as.setIndex(index);
						l_as2.add(wa_as);
						index++;
					}
				}
			}
			else
			{
				selectedCurrency = null;
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
	//***************************************************************************************************
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
	private Bkpf p_bkpf_VZ = new Bkpf();	
	public Bkpf getP_bkpf_VZ() {
		return p_bkpf_VZ;
	}
	public void setP_bkpf_VZ(Bkpf p_bkpf_VZ) {
		this.p_bkpf_VZ = p_bkpf_VZ;
	}

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
			this.getT_bseg().setHkont("33100001");
			this.getT_bseg().setShkzg("S");
			this.getT_bseg().setGjahr(p_bkpf.getGjahr());
			this.getT_bseg().setBukrs(p_bkpf.getBukrs());
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

			PermissionController.canWrite(userData, Faco01.transaction_id);
			Calendar curDate = Calendar.getInstance();
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
			}
			
			if (p_bkpf.getKursf() == 0 || p_bkpf.getKursf() < 0 ){
				throw new DAOException(Helper.getErrorMessage(2L, userData.getU_language()));
			}

			if (p_bkpf.getBukrs() == null || p_bkpf.getBukrs().isEmpty())
			{
				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
			}
			CashBankAccountStatement selectedCashOffice = null;
			for(CashBankAccountStatement wa_as:l_as2)
			{
				if (selectedCashOfficeIndex==wa_as.getIndex())
				{
					selectedCashOffice = wa_as;
				}
			}
			if (selectedCashOffice == null || selectedCashOffice.getHkont()==null || selectedCashOffice.getHkont().length()==0)
			{
				throw new DAOException(Helper.getErrorMessage(3L, userData.getU_language()));
			}
			if (selectedCashOffice.getHkont().equals("12500001"))
			{
				if (selectedCashOffice.getCustomer_id()==null || selectedCashOffice.getCustomer_id()==0L)
				{
					throw new DAOException(Helper.getErrorMessage(90L, userData.getU_language()));
				}
			}
			if (p_bkpf.getDep()==null || p_bkpf.getDep().equals(0L))
			{
				throw new DAOException(Helper.getErrorMessage(4L, userData.getU_language()));
			}
			
//			FinanceService financeService = (FinanceService) appContext.getContext().getBean("financeService");
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			Bseg p_bsegKredit = new Bseg();
			Calendar cal = Calendar.getInstance(); 	 
			cal.setTime(p_bkpf.getBudat());
			
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Faco01.transaction_code); 
			p_bkpf.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf.setBrnch(getSelectedBranch().getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			p_bkpf.setAwtyp(2);
			
			
			p_bsegKredit.setBuzei(1);
			p_bsegKredit.setHkont(selectedCashOffice.getHkont());
			p_bsegKredit.setBukrs(p_bkpf.getBukrs());
			p_bsegKredit.setGjahr(p_bkpf.getGjahr());
			p_bsegKredit.setBschl("50");
			p_bsegKredit.setShkzg("H");
			if (selectedCashOffice.getHkont().equals("12500001"))
			{
				p_bsegKredit.setBschl("11");
				p_bsegKredit.setLifnr(selectedCashOffice.getCustomer_id());
			}
			
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  		{ 
				wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getT_bseg().setGjahr(p_bkpf.getGjahr());
				wa_bseg.getT_bseg().setBschl("25");
				wa_bseg.getT_bseg().setShkzg("S");
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
						wa_bseg.getT_bseg().setDmbtr(GeneralUtil.round(wa_bseg.getT_bseg().getWrbtr()/p_bkpf.getKursf(), 2)); 
					}
				}
				
		  	}
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  	{ 
				p_bsegKredit.setDmbtr(p_bsegKredit.getDmbtr()+wa_bseg.getT_bseg().getDmbtr());
				p_bsegKredit.setWrbtr(p_bsegKredit.getWrbtr()+wa_bseg.getT_bseg().getWrbtr());
				
		  	}
			l_bsegFinal.add(p_bsegKredit);
			
			int wa_buzei = 1;
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
			
			if (selectedCashOffice != null && selectedCashOffice.getHkont()!=null && selectedCashOffice.getHkont().length()>0)
			{
				//userData.setNew_fi_doc(true);
				//userData.setTrans_id(Faco01.transaction_id);
				PrebkpfService prebkpfService = (PrebkpfService) appContext.getContext().getBean("prebkpfService");
				Prebkpf p_prebkpf = new Prebkpf();
				BeanUtils.copyProperties(p_bkpf, p_prebkpf);
				p_prebkpf.setStatus(0);
				p_prebkpf.setCreated_date(curDate.getTime());
				p_prebkpf.setHkont_d(l_bseg.get(0).getT_bseg().getHkont());
				p_prebkpf.setHkont_k(selectedCashOffice.getHkont());
				if (p_prebkpf.getWaers().equals("USD"))
				{
					p_prebkpf.setSumma(p_bsegKredit.getDmbtr());
				}
				else
				{
					p_prebkpf.setSumma(p_bsegKredit.getWrbtr());
				}
				if (p_bkpf_VZ!=null && p_bkpf_VZ.getBelnr()!=null && p_bkpf_VZ.getGjahr()>0)
				{
					p_prebkpf.setAwkey(GeneralUtil.getPreparedAwkey(p_bkpf_VZ.getBelnr(), p_bkpf_VZ.getGjahr()));
				}
				
				List<Prebseg> l_prebseg = new ArrayList<Prebseg>();
				for(Bseg wa_bseg:l_bsegFinal)
				{
					Prebseg wa_prebseg = new Prebseg();
					BeanUtils.copyProperties(wa_bseg, wa_prebseg);
					l_prebseg.add(wa_prebseg);
				}
				prebkpfService.save(p_prebkpf,l_prebseg);
				
//				wa_prebkpf.setBukrs(p_bkpf.getBukrs());
//				wa_prebkpf.setBlart(p_bkpf.getBlart());
//				wa_prebkpf.setBldat(p_bkpf.getBldat());
//				wa_prebkpf.setBusiness_area_id(p_bkpf.getBusiness_area_id());
//				wa_prebkpf.setUsnam(p_bkpf.getUsnam());
//				wa_prebkpf.setTcode(p_bkpf.getTcode());
//				wa_prebkpf.setContract_number(p_bkpf.getContract_number());
//				wa_prebkpf.setBktxt(p_bkpf.getBktxt());
//				wa_prebkpf.setWaers(p_bkpf.getWaers());
//				wa_prebkpf.setBrnch(p_bkpf.getBrnch());
//				wa_prebkpf.setCustomer_id(p_bkpf.getCustomer_id());				
//				wa_prebkpf.setDep(p_bkpf.getDep());
//				wa_prebkpf.setStatus(0);
//				wa_prebkpf.setHkont_d(l_bseg.get(0).getT_bseg().getHkont());
//				wa_prebkpf.setHkont_k(selectedCashOffice.getHkont());
//				wa_prebkpf.setCreated_date(curDate.getTime());
//				if (p_bkpf_VZ!=null && p_bkpf_VZ.getBelnr()!=null && p_bkpf_VZ.getGjahr()>0)
//				{
//					wa_prebkpf.setAwkey(GeneralUtil.getPreparedAwkey(p_bkpf_VZ.getBelnr(), p_bkpf_VZ.getGjahr()));
//				}
//				
//				if (wa_prebkpf.getWaers().equals("USD"))
//				{
//					wa_prebkpf.setDmbtr(p_bsegKredit.getDmbtr());
//				}
//				else
//				{
//					wa_prebkpf.setDmbtr(p_bsegKredit.getWrbtr());
//				}
//				prebkpfService.save(wa_prebkpf);
				
//				Long newDocBelnr = financeService.createFACO01(p_bkpf,l_bsegFinal,p_bkpf_VZ);
				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
				context.redirect(context.getRequestContextPath()  + "/finance/cashbankoperations/faco01.xhtml");
		   	 	//context.redirect(context.getRequestContextPath()  + "/accounting/assess/fa02.xhtml?belnr="+newDocBelnr+"&gjahr="+p_bkpf.getGjahr()+"&bukrs="+p_bkpf.getBukrs());
				
				
			}
			else
			{
				throw new DAOException(Helper.getErrorMessage(6L, userData.getU_language()));
			}
			
			
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		}  
		
	}
	public void changeBukrs()
	{
		
		clearAndAddBseg();
	}
	
	
	public void changeBranch()
	{
		
		try{
			if (voznag)
			{
				selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());
				assignSelectedCurrencyVoznag();
			}
			else if (checkFilling(2))
			{
				long wa_br_id = p_bkpf.getBrnch();
				clearAndAddBseg();
				p_bkpf.setBrnch(wa_br_id);
				selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());				
			}
			else
			{
				p_bkpf.setBrnch(0L);				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:branch");
			}
  			
  		}
  		catch (DAOException ex)
		{
  			
			addMessage("Info",ex.getMessage()); 
		}
	}
	public void clearAndAddBseg()
	{
		selectedBranch = new Branch();
		selectedCashOfficeIndex = 0;
		selectedCurrency = new Currency();
		l_as2.clear();
		l_as.clear();
		l_bseg.clear();
		p_bkpf.setWaers(null);
		p_bkpf.setKursf(0);
		p_bkpf.setBrnch(null);
		l_bseg.add(new BsegTypeTable(0));
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bsegTable");
		reqCtx.update("form:business_area");
		reqCtx.update("form:bkpf_kursf");
		reqCtx.update("form:bkpf_waers");
		reqCtx.update("form:branch");
		reqCtx.update("form:singleCurrency");
		reqCtx.update("form:selectedCashOffice");
	}
	//**********************************************************************

	private int selectedCashOfficeIndex=0;
	public int getSelectedCashOfficeIndex() {
		return selectedCashOfficeIndex;
	}
	public void setSelectedCashOfficeIndex(int selectedCashOfficeIndex) {
		this.selectedCashOfficeIndex = selectedCashOfficeIndex;
	}

	public void getCashOfficeHkont() throws DAOException
	{
		try{
			l_as.clear();
			l_as2.clear();
			selectedCashOfficeIndex = 0;
			if ((p_bkpf.getBrnch()!=null && p_bkpf.getBrnch()!=0L))
			{
				l_as = p_hkontF4Bean.getHkontBranchWaers(p_bkpf.getBukrs(), p_bkpf.getBrnch());
				if (l_as==null || l_as.size()==0)
				{
					throw new DAOException("Касса/Банк отсутствует.");
				}
			}
			else if ((selectedBranch==null || selectedBranch.getBranch_id()==null))
			{
				throw new DAOException("Выберите филиал");
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
			reqCtx.update("form:selectedCashOffice");
			reqCtx.update("form:outputTableBranchCash");
		} 
		catch (DAOException ex)
		{   
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:selectedCashOffice");
			throw new DAOException(ex.getMessage());
		} 
		

	}

	

	private List<CashBankAccountStatement> l_as = new ArrayList<CashBankAccountStatement>();
	public List<CashBankAccountStatement> getL_as() {
		return l_as;
	}
	public void setL_as(List<CashBankAccountStatement> l_as) {
		this.l_as = l_as;
	}

	private List<CashBankAccountStatement> l_as2 = new ArrayList<CashBankAccountStatement>();
	public List<CashBankAccountStatement> getL_as2() {
		return l_as2;
	}

	public void setL_as2(List<CashBankAccountStatement> l_as2) {
		this.l_as2 = l_as2;
	}
	

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
		if (selectedCustomer!= null && selectedCustomer.getId()!=null && l_bseg!=null && l_bseg.size()>0)
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
  	public boolean checkFilling(int a_step)
  	{
  		// step 1 Bukrs
  		// step 2 Branch
  		// step 3 Waers
  		// step 4 Cash Office
  		// step 5 Kontragent
  		try{
  			if (a_step==2)
  			{
  				if ((p_bkpf.getBukrs()== null || p_bkpf.getBukrs().equals("0") || p_bkpf.getBukrs().length()<4)) throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));  				
  			}
  			else if (a_step==3)
  			{
  				if ((p_bkpf.getBukrs()== null || p_bkpf.getBukrs().equals("0") || p_bkpf.getBukrs().length()<4)) throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
  				if (p_bkpf.getBrnch()== null || p_bkpf.getBrnch().equals(0L)) throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));  	
  			}
  			else if (a_step==4)
  			{
  				if ((p_bkpf.getBukrs()== null || p_bkpf.getBukrs().equals("0") || p_bkpf.getBukrs().length()<4)) throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
  				if (p_bkpf.getBrnch()== null || p_bkpf.getBrnch().equals(0L)) throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
  				if ((p_bkpf.getWaers()== null || p_bkpf.getWaers().equals("0") || p_bkpf.getBukrs().length()<3)) throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
  			}
  				
  			return true;
  		}
  		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			return false;
		}
  	}
  	
  	public String getTransactionName(String a_lang, Long a_transaction_id)
	{
		String name="";
		TransactionDao transactionDao = (TransactionDao) appContext
				.getContext().getBean("transactionDao");
		Transaction t = transactionDao.find(a_transaction_id);
		if (t==null)
		{
			return "";
		}
		if (a_lang == null || a_lang.equals("ru"))
		{
			return t.getName_ru();
		}
		else if (a_lang.equals("en"))
		{
			return t.getName_en();
		}
		else if (a_lang.equals("tr"))
		{
			return t.getName_tr();
		}
		return name;
	}
  	//******************************************************************************************* 
}
