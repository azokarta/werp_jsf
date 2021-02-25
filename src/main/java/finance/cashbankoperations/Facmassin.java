package finance.cashbankoperations;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ContractStatusF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import general.AppContext;
import general.GeneralUtil;
import general.Paginator;
import general.PermissionController;
import general.Validation;
import general.dao.ContractDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.output.tables.FacmassinOutputTable;
import general.services.CustomerService;
import general.services.FinanceServiceDms;
import general.universal.SaveFacmassinTable;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.Country;
import general.tables.Customer;
import general.tables.ExchangeRate;
import general.tables.Hkont;
import general.tables.Salary;
import general.tables.Staff;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "facmassinBean", eager = true)
@ViewScoped
public class Facmassin implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FACMASSIN";
	private final static Long transaction_id = (long) 44;
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
	
	List<Bukrs> bukrs_list = new ArrayList<Bukrs>();
	public List<Bukrs> getBukrs_list(){
		return bukrs_list;
	}
	//***************************ContractStatusF4*******************************
	@ManagedProperty(value="#{contractStatusF4Bean}")
	private ContractStatusF4 p_contractStatusF4Bean;
	public ContractStatusF4 getP_contractStatusF4Bean() {
		return p_contractStatusF4Bean;
	}
	public void setP_contractStatusF4Bean(ContractStatusF4 p_contractStatusF4Bean) {
		this.p_contractStatusF4Bean = p_contractStatusF4Bean;
	}
	//******************************************************************
	//***************************ContractTypeF4*******************************
	@ManagedProperty(value="#{contractTypeF4Bean}")
	private ContractTypeF4 p_contractTypeF4Bean;
	public ContractTypeF4 getP_contractTypeF4Bean() {
		return p_contractTypeF4Bean;
	}
	public void setP_contractTypeF4Bean(ContractTypeF4 p_contractTypeF4Bean) {
		this.p_contractTypeF4Bean = p_contractTypeF4Bean;
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
  	
  			 
  	List<Branch> branch_list = new ArrayList<Branch>();
  	public List<Branch> getBranch_list(){
		return branch_list;
	} 
  	public void setBranch_list(List<Branch> branch_list) {
		this.branch_list = branch_list;
	}
  	@ManagedProperty(value="#{countryF4Bean}")
  	private CountryF4 p_countryF4Bean;
  	
	public CountryF4 getP_countryF4Bean() {
		return p_countryF4Bean;
	}

	public void setP_countryF4Bean(CountryF4 p_countryF4Bean) {
		this.p_countryF4Bean = p_countryF4Bean;
	}
	//*************************** Exchange Rate F4 ***************************
	@ManagedProperty(value="#{exchangeRateF4Bean}")
	private ExchangeRateF4 p_exchangeRateF4Bean;	
	public ExchangeRateF4 getP_exchangeRateF4Bean() {
		return p_exchangeRateF4Bean;
	}
	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
		this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
	}
	//***************************Exchange Rate**************************
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
	Map<String,Hkont> l_hkont_iptoo_map = new HashMap<String,Hkont>();
	private List<Hkont> l_hkont_iptoo = new ArrayList<Hkont>();
	private Hkont p_hkont_dmbtr = new Hkont();
	private Hkont p_hkont_wrbtr = new Hkont();
	
	//**********************************************************************
   //**********************************************************************	
	private String p_bukrs;	
	public String getP_bukrs() {
		return p_bukrs;
	}
	public void setP_bukrs(String p_bukrs) {
		this.p_bukrs = p_bukrs;
	}
	
	private Long p_branch_id;
	public Long getP_branch_id() {
		return p_branch_id;
	}
	public void setP_branch_id(Long p_branch_id) {
		this.p_branch_id = p_branch_id;
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
			PermissionController.canRead(userData,Facmassin.transaction_id);
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(firstDay.get(Calendar.YEAR), firstDay.get(Calendar.MONTH), 1);
			lastDay.set(lastDay.get(Calendar.YEAR), lastDay.get(Calendar.MONTH), lastDay.getActualMaximum(Calendar.DAY_OF_MONTH)); 
			
			setPayment_date_from(firstDay.getTime());
			setPayment_date_to(lastDay.getTime());
			
		} 
		catch (DAOException ex)
		{   
			toMainPage();
		}	
	}
	//*************************************************************************	
	//***************************Save method********************************
	public void to_save()
	{
		try 
		{ 
			PermissionController.canWrite(userData, Facmassin.transaction_id);
			
			if (p_page.getL_all().size()>0)
			{
				to_check(1);
				
				if (p_branch_id<1)
				{
					throw new DAOException("Выберите филиал");
				}
				/*int z=0;
				if (z==0)
				return;*/
					
				Branch wa_branch = p_branchF4Bean.getL_branch_map().get(p_branch_id);
				if (wa_branch==null)
				{
					throw new DAOException("Филиал отсутствует.");
				}
				if (p_hkont_dmbtr==null)
				{
					throw new DAOException("Касса USD не найдена.");
				}
				if (p_hkont_wrbtr==null)
				{
					throw new DAOException(p_bkpf.getWaers()+" Касса не найдена.");
				}
				Calendar curDate = Calendar.getInstance(); 
				//Time cputm = new Time(curDate.getTimeInMillis());

				List<SaveFacmassinTable> l_sft = new ArrayList<SaveFacmassinTable>();
				for(FacmassinOutputTable wa_out: p_page.getL_all())
				{
					SaveFacmassinTable wa_sft = new SaveFacmassinTable();
					double wa_dmbtr = 0;
					double wa_wrbtr = 0;
					Bkpf wa_bkpf = new Bkpf();
					
					wa_sft.setPayment_schedule_id(wa_out.getPayment_schedule_id());
					
					
					wa_bkpf.setBukrs(p_bukrs);
					wa_bkpf.setGjahr(curDate.get(Calendar.YEAR));
					if (wa_out.isFirst_payment()) wa_bkpf.setBlart("CF");
					else wa_bkpf.setBlart("CP");
					wa_bkpf.setBudat(curDate.getTime());
					wa_bkpf.setBldat(curDate.getTime());
		            wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
		            wa_bkpf.setCpudt(curDate.getTime());
		            wa_bkpf.setUsnam(userData.getUserid());
		            wa_bkpf.setTcode(Facmassin.transaction_code);
		            wa_bkpf.setWaers(wa_out.getWaers());
		            wa_bkpf.setKursf(p_bkpf.getKursf()); 
		            wa_bkpf.setBrnch(wa_branch.getBranch_id());
					wa_bkpf.setContract_number(wa_out.getContract_number()); 
					wa_bkpf.setCustomer_id(wa_out.getCustomer_id());
					wa_bkpf.setAwtyp(1);
					wa_bkpf.setAwkey(wa_out.getAwkey());
					if (wa_out.getWaers().equals("USD"))
					{
						wa_dmbtr = wa_out.getDmbtr()+wa_out.getWrbtr_dmbtr()+wa_out.getIptoo_dmbtr();
						wa_bkpf.setDmbtr(wa_dmbtr);
					}
					else
					{
						wa_wrbtr = wa_out.getWrbtr()+wa_out.getDmbtr_wrbtr()+wa_out.getIptoo_wrbtr();
						wa_bkpf.setWrbtr(wa_wrbtr);
						wa_bkpf.setDmbtr(wa_out.getDmbtr()+wa_out.getWrbtr_dmbtr()+wa_out.getIptoo_dmbtr());
					}
					wa_sft.setP_bkpf(wa_bkpf);
					
					
					Bseg wa_bseg1 = new Bseg();
					wa_bseg1.setBukrs(wa_bkpf.getBukrs());
					wa_bseg1.setGjahr(wa_bkpf.getGjahr());
					wa_bseg1.setBuzei(1);
					wa_bseg1.setBschl("15");
					wa_bseg1.setShkzg("H");
					wa_bseg1.setLifnr(wa_out.getCustomer_id());
					wa_bseg1.setHkont("12100001");
					wa_bseg1.setMatnr(wa_out.getMatnr());
					wa_bseg1.setDmbtr(wa_out.getDmbtr()+wa_out.getWrbtr_dmbtr()+wa_out.getIptoo_dmbtr());
					wa_bseg1.setWrbtr(wa_out.getWrbtr()+wa_out.getDmbtr_wrbtr()+wa_out.getIptoo_wrbtr());
					wa_sft.getL_bseg().add(wa_bseg1);
					
					Bseg wa_bseg2 = new Bseg();
					wa_bseg2.setBukrs(wa_bkpf.getBukrs());
					wa_bseg2.setGjahr(wa_bkpf.getGjahr());
					wa_bseg2.setBuzei(2);
					wa_bseg2.setBschl("40");
					wa_bseg2.setShkzg("S");
					if (wa_out.getWaers().equals("USD"))
					{
						wa_bseg2.setDmbtr(wa_out.getDmbtr());						
						wa_bseg2.setHkont(p_hkont_dmbtr.getHkont());
					}
					else
					{
						wa_bseg2.setDmbtr(wa_out.getWrbtr_dmbtr());
						wa_bseg2.setWrbtr(wa_out.getWrbtr());
						wa_bseg2.setHkont(p_hkont_wrbtr.getHkont());
					}
					wa_sft.getL_bseg().add(wa_bseg2);
					
					if (wa_out.getWaers().equals("USD") && wa_out.getWrbtr()>0)
					{
						Bseg wa_bseg3 = new Bseg();
						wa_bseg3.setBukrs(wa_bkpf.getBukrs());
						wa_bseg3.setGjahr(wa_bkpf.getGjahr());
						wa_bseg3.setBuzei(3);
						wa_bseg3.setBschl("40");
						wa_bseg3.setShkzg("S");
						wa_bseg3.setDmbtr(wa_out.getWrbtr_dmbtr());
						wa_bseg3.setWrbtr(wa_out.getWrbtr());
						wa_bseg3.setHkont(p_hkont_wrbtr.getHkont());
						wa_sft.getL_bseg().add(wa_bseg3);
					}
					else if (!wa_out.getWaers().equals("USD") && wa_out.getDmbtr()>0)
					{
						Bseg wa_bseg3 = new Bseg();
						wa_bseg3.setBukrs(wa_bkpf.getBukrs());
						wa_bseg3.setGjahr(wa_bkpf.getGjahr());
						wa_bseg3.setBuzei(3);
						wa_bseg3.setBschl("40");
						wa_bseg3.setShkzg("S");
						wa_bseg3.setDmbtr(wa_out.getDmbtr());
						wa_bseg3.setWrbtr(wa_out.getDmbtr_wrbtr());
						wa_bseg3.setHkont(p_hkont_dmbtr.getHkont());
						wa_sft.getL_bseg().add(wa_bseg3);
					}
					
					if (!wa_out.getIptoo_waers().equals("0") && wa_out.getIptoo_summa()>0)
					{
						Bseg wa_bseg3 = new Bseg();
						wa_bseg3.setBukrs(wa_bkpf.getBukrs());
						wa_bseg3.setGjahr(wa_bkpf.getGjahr());
						wa_bseg3.setBuzei(4);
						wa_bseg3.setBschl("40");
						wa_bseg3.setShkzg("S");
						wa_bseg3.setDmbtr(wa_out.getIptoo_dmbtr());
						wa_bseg3.setWrbtr(wa_out.getIptoo_wrbtr());
						wa_bseg3.setHkont(wa_out.getIptoo_hkont());
						wa_sft.getL_bseg().add(wa_bseg3);
					}
					for (Bseg wa_bsegTemp:wa_sft.getL_bseg())
					{
						if (wa_bsegTemp.getDmbtr()==0 && wa_bsegTemp.getWrbtr()==0)
						{
							wa_sft.getL_bseg().remove(wa_bsegTemp);
						}
					}
					if (wa_sft.getL_bseg().size()>0)
					{
						
						l_sft.add(wa_sft);
					}
					
					
				}
				//System.out.println(3);
				FinanceServiceDms fsd = (FinanceServiceDms) appContext.getContext().getBean("financeServiceDms");
				if (fsd.massContractPayments(l_sft))
				{
					search();
					addMessage("Info","Взнос сохранен.");
				}
			}
			//				 
			//toMainPage();
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		}	
	}		
	//**********************************************************************		
	//***********************************Others***********************************************************

  	public void toMainPage() {
		try {
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath() +  "/general/mainPage.xhtml");
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
	
	//*****************************************************************************************************
	//*****************************************************************************************************
	List<Contract> l_contracts = new ArrayList<Contract>();
	public void to_check(int call) throws DAOException
	{
		try 
		{
			if (p_page.getL_all().size()>0)
			{	
			List<FacmassinOutputTable> wa_outputTable =  new ArrayList<FacmassinOutputTable>();
			for (FacmassinOutputTable wa_out:p_page.getL_all())
			{
				if (wa_out.getDmbtr()>0 || wa_out.getWrbtr()>0 || wa_out.getIptoo_summa()>0)
				{
					if (wa_out.getWaers().equals("USD"))
					{
						if (wa_out.getWrbtr()>0) wa_out.setWrbtr_dmbtr(GeneralUtil.round(wa_out.getWrbtr()/p_bkpf.getKursf(), 2));
						else wa_out.setWrbtr_dmbtr(0);
						
						if(wa_out.getIptoo_summa()>0 && !wa_out.getIptoo_hkont().equals("0"))							
						{
							Hkont wa_hkont = l_hkont_iptoo_map.get(wa_out.getIptoo_hkont());
							wa_out.setIptoo_waers(wa_hkont.getWaers());
							if (wa_out.getIptoo_waers().equals("USD"))
							{
								wa_out.setIptoo_dmbtr(GeneralUtil.round(wa_out.getIptoo_summa(), 2));
							}
							else if (!wa_out.getIptoo_waers().equals("USD") )
							{
								wa_out.setIptoo_wrbtr(GeneralUtil.round(wa_out.getIptoo_summa(), 2));
								wa_out.setIptoo_dmbtr(GeneralUtil.round(wa_out.getIptoo_summa()/p_bkpf.getKursf(), 2));									
							}
						}
						else
						{
							wa_out.setIptoo_dmbtr(0);
							wa_out.setIptoo_summa(0);
							wa_out.setIptoo_wrbtr(0);
							wa_out.setIptoo_hkont("0");
						}
						
					}
					else if (!wa_out.getWaers().equals("USD") )
					{
						if (wa_out.getDmbtr()>0) wa_out.setDmbtr_wrbtr(GeneralUtil.round(wa_out.getDmbtr()*p_bkpf.getKursf(), 2));
						else wa_out.setDmbtr_wrbtr(0);
						if (wa_out.getWrbtr()>0) wa_out.setWrbtr_dmbtr(GeneralUtil.round(wa_out.getWrbtr()/p_bkpf.getKursf(), 2));
						
						if(wa_out.getIptoo_summa()>0 && !wa_out.getIptoo_hkont().equals("0"))							
						{
							Hkont wa_hkont = l_hkont_iptoo_map.get(wa_out.getIptoo_hkont());
							wa_out.setIptoo_waers(wa_hkont.getWaers());
							if (wa_out.getIptoo_waers().equals("USD"))
							{
								wa_out.setIptoo_dmbtr(GeneralUtil.round(wa_out.getIptoo_summa(), 2));
								wa_out.setIptoo_wrbtr(GeneralUtil.round(wa_out.getIptoo_summa()*p_bkpf.getKursf(), 2));
							}
							else if (!wa_out.getIptoo_waers().equals("USD") )
							{
								wa_out.setIptoo_wrbtr(GeneralUtil.round(wa_out.getIptoo_summa(), 2));
								wa_out.setIptoo_dmbtr(GeneralUtil.round(wa_out.getIptoo_summa()/p_bkpf.getKursf(), 2));									
							}
						}
						else
						{
							wa_out.setIptoo_dmbtr(0);
							wa_out.setIptoo_summa(0);
							wa_out.setIptoo_wrbtr(0);
							wa_out.setIptoo_hkont("0");
						}
							
					}
					
					wa_outputTable.add(wa_out);
				}
				
			}
			
			for (FacmassinOutputTable wa_out:p_page.getL_all())
			{
				if (wa_out.isFirst_payment())
				{
					if (wa_out.getWaers().equals("USD"))
					{
						if (wa_out.getDmbtr()+wa_out.getWrbtr_dmbtr()+wa_out.getIptoo_dmbtr()!=wa_out.getPayment_due())
						{
							wa_out.setError(true);
							wa_out.setError_message("Первоначальный взнос не равен!");
						}
						else
						{
							wa_out.setError(false);
							wa_out.setError_message("Правильно!");
						}
					}
					else
					{
						if (wa_out.getWrbtr()+wa_out.getDmbtr_wrbtr()+wa_out.getIptoo_wrbtr()!=wa_out.getPayment_due())
						{
							wa_out.setError(true);
							wa_out.setError_message("Первоначальный взнос не равен!");
						}
						else
						{
							wa_out.setError(false);
							wa_out.setError_message("Правильно!");
						}
					}
				}
				else
				{
					if (wa_out.getWaers().equals("USD"))
					{
						if (wa_out.getDmbtr()+wa_out.getWrbtr_dmbtr()+wa_out.getIptoo_dmbtr()>wa_out.getPayment_due())
						{
							wa_out.setError(true);
							wa_out.setError_message("Сумма взноса больше суммы к оплате!");
						}
						else
						{
							wa_out.setError(false);
							wa_out.setError_message("Правильно!");
						}
					}
					else
					{
						if (wa_out.getWrbtr()+wa_out.getDmbtr_wrbtr()+wa_out.getIptoo_wrbtr()>wa_out.getPayment_due())
						{
							wa_out.setError(true);
							wa_out.setError_message("Сумма взноса больше суммы к оплате!");
						}
						else
						{
							wa_out.setError(false);
							wa_out.setError_message("Правильно!");
						}
					}
					
				}
				
			}
			p_page = new Paginator<FacmassinOutputTable>();
			for (FacmassinOutputTable wa_out:wa_outputTable)
			{
				p_page.add(wa_out);
			}
			boolean any_Info = false;
			for(FacmassinOutputTable wa_out:p_page.getL_all())
  			{
				if (wa_out.isError())
				{
					any_Info = true;
					break;
				}
  			}
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:outputTable");
  			
	  			if (p_page.getL_all().size()>0)
	  			{	
		  			if (any_Info)
					{
		  				saveDisable = true;
						reqCtx.update("form:save_button");
						throw new DAOException("Ошибка");
					}
					else
					{
						for(FacmassinOutputTable wa_out:p_page.getL_all())
			  			{
							dm_total=dm_total+wa_out.getDmbtr();
							wr_total=wr_total+wa_out.getWrbtr();
							cash_total=cash_total+wa_out.getIptoo_summa();
			  			}
						saveDisable = false;
						amountDisable = true;
						reqCtx.update("form:save_button");
			  	  		int i = 0;
			  	  		for(i=0;i<p_page.getL_all().size();i++)
			  			{
			  				reqCtx.update("form:outputTable:"+i+"dmbtr");
			  				reqCtx.update("form:outputTable:"+i+"wrbtr");
			  				reqCtx.update("form:outputTable:"+i+"iptoo_summa");
			  			}
			  	  	
					}
				}
			}
			outputTable2.clear();
		}
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage());
			if (call==1)
			{
				throw new DAOException("Ошибка");
			}
			
		}
	}
	public void search()
	{
		try 
		{
			p_page = new Paginator<FacmassinOutputTable>();
			saveDisable = true;
			amountDisable = false;
			l_hkont.clear();
			l_hkont_iptoo.clear();
			l_hkont_iptoo_map.clear();
			l_contracts.clear();
			outputTable2.clear();
			p_hkont_dmbtr = new Hkont();
			p_hkont_wrbtr = new Hkont();
			String dynamicWhereClause = "";
			
			if (p_bukrs.equals("0") || p_bukrs.equals(null))
			{
				RequestContext reqCtx = RequestContext.getCurrentInstance();
	  			reqCtx.update("form:outputTable");
				throw new DAOException("Выберите компанию");
			}
			dynamicWhereClause = dynamicWhereClause+" c.bukrs = "+p_bukrs;
			
			
			/*
			int count = 0;
			for(Branch wa_branch:p_branchF4Bean.getBranch_list())
			{
				if (wa_branch.getParent_branch_id()==p_branch_id && wa_branch.getType() == 3)
				{
					count = count + 1;
					if (count == 1)
					{
						dynamicWhereClause = dynamicWhereClause+wa_branch.getBranch_id();
					}
					else
					{
						dynamicWhereClause = dynamicWhereClause+","+wa_branch.getBranch_id(); 
					}
					
				}
				
			}
			if (count>1)
			{
				dynamicWhereClause = dynamicWhereClause+") and ";
			}
			else
			{
				RequestContext reqCtx = RequestContext.getCurrentInstance();
	  			reqCtx.update("form:outputTable");
				throw new DAOException("No branch selected");
			}
			*/
			
			dynamicWhereClause = dynamicWhereClause + " and c.contract_status_id not in (2,3,5,7)";
			
			if (p_branch_id == 0)
			{
				RequestContext reqCtx = RequestContext.getCurrentInstance();
	  			reqCtx.update("form:outputTable");
				throw new DAOException("Выберите филиал");
			}
			else
			{
				dynamicWhereClause = dynamicWhereClause+" and c.fin_branch_id = " + p_branch_id;
//				int ba_id = Integer.parseInt(String.valueOf(p_branchF4Bean.getL_branch_map().get(p_branch_id).getBusiness_area_id()));
//				if (ba_id==1 || ba_id==3)
//					dynamicWhereClause = dynamicWhereClause + " and c.contract_type_id in (1,3,11)";
//				else if (ba_id==2 || ba_id==4)	
//					dynamicWhereClause = dynamicWhereClause + " and c.contract_type_id in (2,8,9,7,14,13,12,15,4,5,6,10)";
				
			}
				
			if (selectedStaff!=null && selectedStaff.getStaff_id()!=null && selectedStaff.getStaff_id()>0)
			{
				dynamicWhereClause = dynamicWhereClause + " and c.collector = " + selectedStaff.getStaff_id();
			}
			
			if (selectedConNum!=null && selectedConNum >0)
			{
				dynamicWhereClause = dynamicWhereClause + " and c.contract_number = "+selectedConNum;
			}
			
			if (selectedOldSn!=null && selectedOldSn >0)
			{
				dynamicWhereClause = dynamicWhereClause + " and c.old_sn = "+selectedOldSn;
			}
			
			
			
			
			
			/*if (p_collector_id==-1 && l_collector.size()>0)
			{
				dynamicWhereClause = dynamicWhereClause + " and collector not in (";
				int count = 0;
				for(Staff wa_staff:l_collector)
				{
					count = count + 1;
					if (count == 1)
					{
						dynamicWhereClause = dynamicWhereClause + wa_staff.getStaff_id();
					}
					else
					{
						dynamicWhereClause = dynamicWhereClause + "," + wa_staff.getStaff_id();
					}
					
				}
				dynamicWhereClause = dynamicWhereClause + ")";
			}
			else if(p_collector_id>0 && l_collector.size()>0)
			{
				dynamicWhereClause = dynamicWhereClause + " and collector = " + p_collector_id;
			}*/
			List<Hkont> wal_hkont = new ArrayList<Hkont>();
			wal_hkont = p_hkontF4Bean.getTypeHkontBranchWaers(p_bukrs, p_branch_id);
			
			
			for (Hkont wa_hkont: wal_hkont)
			{
				if (wa_hkont.getHkont().startsWith("1010"))
				{
					l_hkont.add(wa_hkont);
				}
				else if (wa_hkont.getHkont().startsWith("1030"))
				{
					l_hkont_iptoo_map.put(wa_hkont.getHkont(), wa_hkont);
					l_hkont_iptoo.add(wa_hkont);
				}
			}
			Country wa_country = new Country();
			wa_country =  p_countryF4Bean.getL_country_map().get(p_branchF4Bean.getL_branch_map().get(p_branch_id).getCountry_id());
			if (wa_country==null || wa_country.getCurrency()==null)
			{
				throw new DAOException("Филиал, Страна и валюта не привязаны.");
			}
			
			
			ExchangeRate wa_er = p_exchangeRateF4Bean.getL_er_map_national().get("1"+wa_country.getCurrency());
			p_bkpf.setWaers(wa_er.getSecondary_currency());
			p_bkpf.setKursf(wa_er.getSc_value());
			for (Hkont wa_hkont: l_hkont)
			{
				
				if (!wa_hkont.getWaers().equals("USD") && wa_country.getCurrency().equals(wa_hkont.getWaers()))
				{
					p_hkont_wrbtr = wa_hkont;				
				}
				else
				{
					p_hkont_dmbtr = wa_hkont;
				}
			}
			if (p_hkont_wrbtr==null || p_hkont_wrbtr.getHkont()==null || p_hkont_dmbtr==null || p_hkont_dmbtr.getHkont()==null)
			{
				throw new DAOException("Кассы отсутвуют.");
			}
			
			if (selectedCustomer!=null)
			{
				dynamicWhereClause = dynamicWhereClause + " and c.customer_id="+selectedCustomer.getId();
			}
			
			
			
			
			
			String dynamicWhereClause2 = "";
			Calendar date_to = Calendar.getInstance();
			date_to.setTime(payment_date_to);
			dynamicWhereClause2 = dynamicWhereClause2 + " and p.payment_date<='"+GeneralUtil.getSQLDate(date_to)+"' ";
			
			ContractDao contractDao = (ContractDao) appContext.getContext().getBean("contractDao");			
			List<FacmassinOutputTable> temp_outputTable = new ArrayList<FacmassinOutputTable>();
			temp_outputTable = contractDao.dynamicFindFacmassin(dynamicWhereClause,dynamicWhereClause2);
			
			
			//System.out.println(p_bukrs);
			//System.out.println(dynamicWhereClause);
			if (temp_outputTable.size()==0)
			{
				RequestContext reqCtx = RequestContext.getCurrentInstance();
	  			reqCtx.update("form:outputTable");
				throw new DAOException("No contract found");
			}
			/*PaymentScheduleDao paymentScheduleDao = (PaymentScheduleDao) appContext.getContext().getBean("paymentScheduleDao");
			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
			StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
			CustomerDao customerDao = (CustomerDao) appContext.getContext().getBean("customerDao");
			int index = 0;
			for (Contract wa_contract:l_contracts)
			{
				Customer wa_customer = new Customer();
				Staff wa_staff = new Staff();
				FacmassinOutputTable wa_FacmassinOutputTable = new FacmassinOutputTable();
				wa_FacmassinOutputTable.setContract_number(wa_contract.getContract_number());
				wa_FacmassinOutputTable.setContract_date(wa_contract.getContract_date());
				wa_FacmassinOutputTable.setPrice(wa_contract.getPrice());
				wa_FacmassinOutputTable.setCustomer_id(wa_contract.getCustomer_id());
				
				//Find client name
				wa_customer = customerDao.find(wa_contract.getCustomer_id());
				if (wa_customer!= null && wa_customer.getId()!=null)
				{
					
					if (wa_customer.getFiz_yur() == 2){
						if (wa_customer.getLastname()==null) wa_customer.setLastname("");
						if (wa_customer.getFirstname()==null) wa_customer.setFirstname("");
						if (wa_customer.getMiddlename()==null) wa_customer.setMiddlename("");
						wa_FacmassinOutputTable.setClientFio(wa_customer.getLastname()+" "+wa_customer.getFirstname()+" "+wa_customer.getMiddlename()); 
					}
					else if(wa_customer.getFiz_yur() == 1){
						wa_FacmassinOutputTable.setClientFio(wa_customer.getName()); 
					}			  
				}
				else{		
					wa_FacmassinOutputTable.setClientFio("No customer found"); 
				}  
				
				
				
				
				//Find collector name
				if (wa_contract.getCollector()!=null)
				{	
					wa_staff = staffDao.find(wa_contract.getCollector());
				}
				if (wa_staff!= null && wa_staff.getStaff_id()!=null)
				{
					
					if (wa_staff.getLastname()==null) wa_staff.setLastname("");
					if (wa_staff.getFirstname()==null) wa_staff.setFirstname("");
					if (wa_staff.getMiddlename()==null) wa_staff.setMiddlename("");
					wa_FacmassinOutputTable.setCollectorFio(wa_staff.getLastname()+" "+wa_staff.getFirstname()+" "+wa_staff.getMiddlename()); 
								  
				}
				else{		
					wa_FacmassinOutputTable.setCollectorFio("No collector found"); 
				} 
				
				//Find contract Status
				for(ContractStatus wa_contractStatus:p_contractStatusF4Bean.getContractStatus_list())
				{
					if (wa_contract.getContract_status_id() == wa_contractStatus.getContract_status_id() && wa_contractStatus.getBukrs().equals(p_bukrs))
					{
						wa_FacmassinOutputTable.setStatus(wa_contractStatus.getName());
					}
				}
				
				//Find contract type
				for(ContractType wa_contractType:p_contractTypeF4Bean.getContractType_list())
				{
					if (wa_contract.getContract_type_id() == wa_contractType.getContract_type_id() && wa_contractType.getBukrs().equals(p_bukrs))
					{
						wa_FacmassinOutputTable.setType(wa_contractType.getName());
						wa_FacmassinOutputTable.setMatnr(wa_contractType.getMatnr());
					}
				}
				
				
				
				
				
				
				//Find contract currency
				wa_FacmassinOutputTable.setWaers(bkpfDao.getWaersSingleBkpf(" belnr = "+GeneralUtil.getPreparedBelnr(wa_contract.getAwkey())
						   +" and gjahr = "+GeneralUtil.getPreparedGjahr(wa_contract.getAwkey())+" and storno=0"));
				wa_FacmassinOutputTable.setAwkey(wa_contract.getAwkey());
				
				//Find payment schedule
				List<PaymentSchedule> l_ps = paymentScheduleDao.findAll(" awkey = "+wa_contract.getAwkey()+" order by payment_date");
				int count = 0;
				for (PaymentSchedule wa_ps:l_ps)
				{
					
					
					FacmassinOutputTable wa_FacmassinOutputTable2 = new FacmassinOutputTable();
					wa_FacmassinOutputTable2.setIndex(index);
					wa_FacmassinOutputTable2.setContract_number(wa_FacmassinOutputTable.getContract_number());
					wa_FacmassinOutputTable2.setContract_date(wa_FacmassinOutputTable.getContract_date());
					wa_FacmassinOutputTable2.setCustomer_id(wa_FacmassinOutputTable.getCustomer_id());
					wa_FacmassinOutputTable2.setClientFio(wa_FacmassinOutputTable.getClientFio()); 
					wa_FacmassinOutputTable2.setCollectorFio(wa_FacmassinOutputTable.getCollectorFio()); 
					wa_FacmassinOutputTable2.setStatus(wa_FacmassinOutputTable.getStatus());
					wa_FacmassinOutputTable2.setType(wa_FacmassinOutputTable.getType());
					wa_FacmassinOutputTable2.setPrice(wa_FacmassinOutputTable.getPrice());
					wa_FacmassinOutputTable2.setWaers(wa_FacmassinOutputTable.getWaers());
					wa_FacmassinOutputTable2.setAwkey(wa_FacmassinOutputTable.getAwkey());
					wa_FacmassinOutputTable2.setMatnr(wa_FacmassinOutputTable.getMatnr());
					wa_FacmassinOutputTable2.setPayment_schedule_id(wa_ps.getPayment_schedule_id());
					wa_FacmassinOutputTable2.setPayment_date( wa_ps.getPayment_date());
					wa_FacmassinOutputTable2.setPaid(wa_ps.getPaid());
					wa_FacmassinOutputTable2.setPayment_due(wa_ps.getSum()-wa_ps.getPaid());
					if (count == 0)
					{
						wa_FacmassinOutputTable2.setFirst_payment(true);
					}
					else
					{
						wa_FacmassinOutputTable2.setFirst_payment(false);;
					}
					count++;
					
					if (wa_ps.getSum()!=wa_ps.getPaid())
					{	
						
						outputTable.add(wa_FacmassinOutputTable2);
						index++;
						if (wa_FacmassinOutputTable2.isFirst_payment())
						{
							break;
						}
					}
				}
				*/
				/*
				Calendar curDate = Calendar.getInstance();  
				Calendar conDate = Calendar.getInstance();
				conDate.setTime(wa_contract.getContract_date());
				int conMonth = conDate.get(Calendar.MONTH) + 1;
				int curMonth = curDate.get(Calendar.MONTH) + 1;
				double wa_Payment_due = 0; 
				
				wa_FacmassinOutputTable.setPayment_due(wa_Payment_due-wa_contract.getPaid());
				wa_FacmassinOutputTable.setRemain(wa_FacmassinOutputTable.getPrice()-wa_Payment_due); 
	  			outputTable.add(wa_FacmassinOutputTable);
	  			*/ 
			//}
			
			
			//temp_outputTable.addAll(outputTable);
			//outputTable = new ArrayList<FacmassinOutputTable>();
			
			for (FacmassinOutputTable wa_out:temp_outputTable)
			{	
				
				if (first_pay_boolean && wa_out.isFirst_payment())
				{					
					if (wa_out.getPayment_date().before(payment_date_to)||wa_out.getPayment_date().equals(payment_date_to))
					{
						//outputTable.add(wa_out);
						outputTable2.add(wa_out);
						p_page.add(wa_out);
					}
				}
				else if (!first_pay_boolean)
				{
					if (!(wa_out.isFirst_payment()))
					{
						if (wa_out.getPayment_date().before(payment_date_to)||wa_out.getPayment_date().equals(payment_date_to))
						{
							//outputTable.add(wa_out);
							outputTable2.add(wa_out);
							p_page.add(wa_out);
						}
					}
				}
			}
			//System.out.println(outputTable.size()+" size");
  			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:outputTable");
  			reqCtx.update("form:waers");
  			reqCtx.update("form:kursf");
  			reqCtx.update("form:save_button");
		}
		catch (DAOException ex)
		{   
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:save_button");
  			reqCtx.update("form:outputTable");
  			reqCtx.update("form:waers");
  			reqCtx.update("form:kursf");
			addMessage("Info",ex.getMessage()); 
		}	
	}
		 
	//*****************************************************************************************************
	//**************************Employee*******************************
	private List<Staff> l_collector = new ArrayList<Staff>();
	public List<Staff> getL_collector() {
		return l_collector;
	}
	public void setL_collector(List<Staff> l_collector) {
		this.l_collector = l_collector;
	}
	
	
	private Long p_collector_id;
	public Long getP_collector_id() {
		return p_collector_id;
	}
	public void setP_collector_id(Long p_collector_id) {
		this.p_collector_id = p_collector_id;
	}
		
	private boolean first_pay_boolean;
	public boolean isFirst_pay_boolean() {
		return first_pay_boolean;
	}
	public void setFirst_pay_boolean(boolean first_pay_boolean) {
		this.first_pay_boolean = first_pay_boolean;
	}
	
	
	
	
	private Date payment_date_from;
	public Date getPayment_date_from() {
		return payment_date_from;
	}
	public void setPayment_date_from(Date payment_date_from) {
		this.payment_date_from = payment_date_from;
	}
	
	private Date payment_date_to;
	public Date getPayment_date_to() {
		return payment_date_to;
	}

	public void setPayment_date_to(Date payment_date_to) {
		this.payment_date_to = payment_date_to;
	}

	
	//*********************************************************************	
	//*****************************************************************	
	private boolean saveDisable = true;	
	public boolean isSaveDisable() {
		return saveDisable;
	}
	public void setSaveDisable(boolean saveDisable) {
		this.saveDisable = saveDisable;
	}
	
	private boolean amountDisable = false;	
	public boolean isAmountDisable() {
		return amountDisable;
	}
	public void setAmountDisable(boolean amountDisable) {
		this.amountDisable = amountDisable;
	}
	
	public void enableAmounts()
	{
		
		amountDisable = false;
		saveDisable = true;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
  		reqCtx.update("form:save_button");
  		int i = 0;
		for(i=0;i<p_page.getL_all().size();i++)
		{
			reqCtx.update("form:outputTable:"+i+":dmbtr");
			reqCtx.update("form:outputTable:"+i+":wrbtr");
			reqCtx.update("form:outputTable:"+i+":iptoo_summa");
		}
		
		//reqCtx.update("form:outputTable");
	}
	
	private Bkpf p_bkpf = new Bkpf();	
	public Bkpf getP_bkpf() {
		return p_bkpf;
	}
	public void setP_bkpf(Bkpf p_bkpf) {
		this.p_bkpf = p_bkpf;
	}
	 

	
	
	
	 

	//*****************************************************************
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
				p_customer_list = new ArrayList<Customer>();
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
  		if (selectedCustomer!=null && selectedCustomer.getFirstname()!=null && selectedCustomer.getFirstname().length()>0)
		{
  			selectedCustomer.setFirstname(selectedCustomer.getFirstname().substring(0, 1).toUpperCase() + selectedCustomer.getFirstname().substring(1).toLowerCase());			
		}
		
		if (selectedCustomer!=null && selectedCustomer.getLastname()!=null && selectedCustomer.getLastname().length()>0)
		{
			selectedCustomer.setLastname(selectedCustomer.getLastname().substring(0, 1).toUpperCase() + selectedCustomer.getLastname().substring(1).toLowerCase());			
		}
		
		if (selectedCustomer!=null && selectedCustomer.getMiddlename()!=null && selectedCustomer.getMiddlename().length()>0)
		{
			selectedCustomer.setMiddlename(selectedCustomer.getMiddlename().substring(0, 1).toUpperCase() + selectedCustomer.getMiddlename().substring(1).toLowerCase());			
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:selectedCustomer");
		
		
		//int pos = p_bseg.getBuzei() -2;
		//System.out.println(pos);
		//reqCtx.update("form:bsegTable:"+pos+":b_kunnr");
		
		
				
  	}
  	//*******************************************************************************************
	// **************************Employee*******************************
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
	
	private Salary searchSalary = new Salary();	
	public Salary getSearchSalary() {
		return searchSalary;
	}
	public void setSearchSalary(Salary searchSalary) {
		this.searchSalary = searchSalary;
	}
	
	public void to_search_staff() {
		try {
			
			String dynamicWhereClause = "";
			StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
			//"+p_bukrs+"
			Calendar curDate = Calendar.getInstance();
			
			dynamicWhereClause = dynamicWhereClause + " and sal.end_date >= '"+GeneralUtil.getSQLDate(curDate)+"'";
			if (searchSalary!=null && searchSalary.getPosition_id()!=null)
			{
				dynamicWhereClause = dynamicWhereClause + " and sal.position_id = "+searchSalary.getPosition_id();
			}
			
			if (searchSalary.getBranch_id() != null && searchSalary.getBranch_id() > 0) {
				dynamicWhereClause = dynamicWhereClause + " and sal.branch_id = "
						+ searchSalary.getBranch_id();
			} else {
				throw new DAOException("Выберите филиал");
			}
			
			searchStaff.setFirstname(searchStaff.getFirstname().replaceAll("\\s", ""));
			if (searchStaff.getFirstname().length() > 0) {
				dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(
						dynamicWhereClause, "and", "=", "stf.firstname",
						searchStaff.getFirstname());
			}
			searchStaff.setLastname(searchStaff.getLastname().replaceAll("\\s", ""));
			if (searchStaff.getLastname().length() > 0) {
				dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(
						dynamicWhereClause, "and", "=", "stf.lastname",
						searchStaff.getLastname());
			}
			searchStaff.setMiddlename(searchStaff.getMiddlename().replaceAll("\\s", ""));
			if (searchStaff.getMiddlename().length() > 0) {
				dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(
						dynamicWhereClause, "and", "=", "stf.middlename",
						searchStaff.getMiddlename());
			}
			String regx = ";";
			char[] ca = regx.toCharArray();
			for (char c : ca) {
				dynamicWhereClause = dynamicWhereClause.replace("" + c, "");
			}
			p_staff_list = staffDao.dynamicFindStaffSalary2(dynamicWhereClause);
			
			/*Salary p_salary = new Salary();
			p_salary.setBukrs(p_contract.getBukrs());
			p_salary.setBranch_id(searchStaff.getBranch_id());
			p_salary.setPosition_id((long) p_search_position_id);
			if (p_contract.getContract_status_id() != 1) {
				p_contract.setPrice_list_id((long) 0);
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:price_list_id");
				addMessage("Info",
						"Employee not applicable for the chosen contract status");
				return;
			}

			StaffService staffService = (StaffService) appContext.getContext()
					.getBean("staffService");
			p_staff_list = staffService.dynamicSearchStaffSalary(searchStaff,
					p_salary);

			if (p_staff_list.size() == 0) {
				p_staff_list = new ArrayList<Staff>();
				addMessage("Инфо", "Не найдено.");
			}*/

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
		if (selectedStaff!=null && selectedStaff.getFirstname()!=null && selectedStaff.getFirstname().length()>0)
		{
			selectedStaff.setFirstname(selectedStaff.getFirstname().substring(0, 1).toUpperCase() + selectedStaff.getFirstname().substring(1).toLowerCase());			
		}
		
		if (selectedStaff!=null && selectedStaff.getLastname()!=null && selectedStaff.getLastname().length()>0)
		{
			selectedStaff.setLastname(selectedStaff.getLastname().substring(0, 1).toUpperCase() + selectedStaff.getLastname().substring(1).toLowerCase());			
		}
		
		if (selectedStaff!=null && selectedStaff.getMiddlename()!=null && selectedStaff.getMiddlename().length()>0)
		{
			selectedStaff.setMiddlename(selectedStaff.getMiddlename().substring(0, 1).toUpperCase() + selectedStaff.getMiddlename().substring(1).toLowerCase());			
		}
		
		
		/*if (selectedStaff != null && selectedStaff.getStaff_id() != null) {
			if (p_selected_position_id == 4) {
				p_contract.setDealer(selectedStaff.getStaff_id());
				p_fioDealer = selectedStaff.getFullFIO();
			} else if (p_selected_position_id == 8) {
				p_contract.setDemo_sc(selectedStaff.getStaff_id());
				p_fioDemoSec = selectedStaff.getFullFIO();
			} else if (p_selected_position_id == 9) {
				p_contract.setCollector(selectedStaff.getStaff_id());
				p_fioColl = selectedStaff.getFullFIO();
			} else if (p_selected_position_id == 11) {
				p_contract.setFitter(selectedStaff.getStaff_id());
				p_fioFitter = selectedStaff.getFullFIO();
			}
		} else {
			if (p_selected_position_id == 4) {
				p_contract.setDealer(null);
				p_fioDealer = " ";
			} else if (p_selected_position_id == 8) {
				p_contract.setDemo_sc(null);
				p_fioDemoSec = " ";
			} else if (p_selected_position_id == 9) {
				p_contract.setCollector(null);
				p_fioColl = " ";
			} else if (p_selected_position_id == 11) {
				p_contract.setFitter(null);
				p_fioFitter = " ";
			}
		}*/

		//selectedStaff = new Staff();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:selectedStaff");
	}
	
	// *********************************************************************
  	public void clearCustomer()
  	{
  		selectedCustomer = new Customer();
  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  		reqCtx.update("form:selectedCustomer");
  		reqCtx.update("form:customerTable");
  	}
  	public void clearCollector()
  	{
  		selectedStaff = new Staff();
  		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:selectedStaff");
		reqCtx.update("form:staffTable");
		
  	}
  	public Long getSelectedConNum() {
		return selectedConNum;
	}

	public void setSelectedConNum(Long selectedConNum) {
		this.selectedConNum = selectedConNum;
	}
	private Long selectedConNum;
	private Long selectedOldSn;
	public Long getSelectedOldSn() {
		return selectedOldSn;
	}

	public void setSelectedOldSn(Long selectedOldSn) {
		this.selectedOldSn = selectedOldSn;
	}
	
	

	List<FacmassinOutputTable> outputTable2 = new ArrayList<FacmassinOutputTable>();
	public void filter_old_sn()
	{
		
		if (outputTable2.size()==0)
		{
			return;
		}
		p_page = new Paginator<FacmassinOutputTable>();
		//outputTable.clear();
		
		if (selectedOldSn!=null && selectedOldSn>0)
		{
			for(FacmassinOutputTable wa_out:outputTable2)
			{
				String old_sn_str = "";
				String selectedOldSn_str = "";
				old_sn_str = String.valueOf(wa_out.getOld_sn());
				selectedOldSn_str = String.valueOf(selectedOldSn);
				if (old_sn_str.startsWith(selectedOldSn_str))
				{
					p_page.add(wa_out);
					//outputTable.add(wa_out);
				}
			}
		}
		else
		{
			for(FacmassinOutputTable wa_out:outputTable2)
			{
				p_page.add(wa_out);
			}
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:outputTable");
	}

	public List<Hkont> getL_hkont_iptoo() {
		return l_hkont_iptoo;
	}

	public void setL_hkont_iptoo(List<Hkont> l_hkont_iptoo) {
		this.l_hkont_iptoo = l_hkont_iptoo;
	}
	
	private Paginator<general.output.tables.FacmassinOutputTable> p_page = new Paginator<general.output.tables.FacmassinOutputTable>();
	public Paginator<general.output.tables.FacmassinOutputTable> getP_page() {
		return p_page;
	}

	public void setP_page(
			Paginator<general.output.tables.FacmassinOutputTable> p_page) {
		this.p_page = p_page;
	}
	
	private double dm_total;
	private double wr_total;
	private double cash_total;
	
	public double getDm_total() {
		return dm_total;
	}

	public void setDm_total(double dm_total) {
		this.dm_total = dm_total;
	}

	public double getWr_total() {
		return wr_total;
	}

	public void setWr_total(double wr_total) {
		this.wr_total = wr_total;
	}

	public double getCash_total() {
		return cash_total;
	}

	public void setCash_total(double cash_total) {
		this.cash_total = cash_total;
	}

	public void changeBukrs()
	{
		searchSalary.setBranch_id(p_branch_id);
		searchSalary.setPosition_id(9L);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
  		reqCtx.update("form:branch");
  		reqCtx.update("form:staffBranch");
  		reqCtx.update("form:se_pos");
	}




	
}
