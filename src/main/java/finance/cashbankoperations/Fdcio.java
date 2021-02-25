package finance.cashbankoperations;


import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.FmglflextDao;
import general.output.tables.CashBankAccountStatement;
import general.services.PrebkpfService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Currency;
import general.tables.ExchangeRate;
import general.tables.Fmglflext;
import general.tables.Hkont;
import general.tables.Prebkpf;
import general.tables.Prebseg;

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

@ManagedBean(name = "fdcioBean", eager = true)
@ViewScoped
public class Fdcio implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private final static String transaction_code = "FDCIO";
	private final static Long transaction_id = (long) 123;
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
  	
  	

	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{  
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Fdcio.transaction_id);
			
			Calendar curDate = Calendar.getInstance();
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime()); 
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fdcio.transaction_code); 
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
			p_bkpf.setBlart("OR");
			


			
			
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
			 
			
			l_as2 = new ArrayList<CashBankAccountStatement>();
			for(CashBankAccountStatement wa_as:l_as)
			{
				if (p_bkpf.getWaers()!=null && wa_as.getWaers().equals(p_bkpf.getWaers()))
				{
					l_as2.add(wa_as);	
				}
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

			PermissionController.canWrite(userData, Fdcio.transaction_id);
			
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
			}

			if (p_bkpf.getBukrs() == null || p_bkpf.getBukrs().isEmpty())
			{
				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
			}
			if (selectedCashOffice == null || selectedCashOffice.length()==0)
			{
				throw new DAOException(Helper.getErrorMessage(3L, userData.getU_language()));
			}

			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			Bseg p_bsegDebet = new Bseg();
			Calendar cal = Calendar.getInstance(); 	 
			cal.setTime(p_bkpf.getBudat());
			
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fdcio.transaction_code); 
			p_bkpf.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf.setBrnch(getSelectedBranch().getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			p_bkpf.setAwtyp(0);
			int wa_buzei = 1;
			p_bsegDebet.setBuzei(wa_buzei);
			
			
			
			p_bsegDebet.setHkont(selectedCashOffice);
			p_bsegDebet.setBukrs(p_bkpf.getBukrs());
			p_bsegDebet.setGjahr(p_bkpf.getGjahr());
			p_bsegDebet.setBschl("40");
			p_bsegDebet.setShkzg("S");

			
			for(BsegTypeTable wa_bseg:l_bseg)
		  		{ 
				wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getT_bseg().setGjahr(p_bkpf.getGjahr());
				wa_bseg.getT_bseg().setBschl("50");
				wa_bseg.getT_bseg().setShkzg("H");
				if (wa_bseg.getT_bseg().getHkont() == null || wa_bseg.getT_bseg().getHkont().isEmpty())
				{
					throw new DAOException(Helper.getErrorMessage(12L, userData.getU_language()));
				} 
				
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
				p_bsegDebet.setDmbtr(p_bsegDebet.getDmbtr()+wa_bseg.getT_bseg().getDmbtr());
				p_bsegDebet.setWrbtr(p_bsegDebet.getWrbtr()+wa_bseg.getT_bseg().getWrbtr());
		  	}
			l_bsegFinal.add(p_bsegDebet);
			wa_buzei = 1;
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
			
			if (selectedCashOffice!=null && selectedCashOffice.length()>0)
			{	
//		   	 	userData.setNew_fi_doc(true);
//				userData.setTrans_id(Fdcio.transaction_id);
				Calendar curDate = Calendar.getInstance();
				PrebkpfService prebkpfService = (PrebkpfService) appContext.getContext().getBean("prebkpfService");
				Prebkpf p_prebkpf = new Prebkpf();
				BeanUtils.copyProperties(p_bkpf, p_prebkpf);
				p_prebkpf.setStatus(0);
				p_prebkpf.setCreated_date(curDate.getTime());
				p_prebkpf.setHkont_d(selectedCashOffice);
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
//				Long newDocBelnr = financeService.IncomeNoInvoice(p_bkpf, l_bsegFinal);
				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//		   	 	context.redirect(context.getRequestContextPath()  + "/accounting/assess/fa02.xhtml?belnr="+newDocBelnr+"&gjahr="+p_bkpf.getGjahr()+"&bukrs="+p_bkpf.getBukrs());
		   	 	
				context.redirect(context.getRequestContextPath()  + "/finance/cashbankoperations/fdcio.xhtml");
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
		hkont_list.clear();
		selectedBranch = new Branch();
		selectedCashOffice = new String();
		for(Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
		{
			Long wa_hkont_long = Long.parseLong( wa_hkont.getHkont());
			if (wa_hkont_long == 60100030 && wa_hkont.getBukrs().equals(p_bkpf.getBukrs()))
			{
				hkont_list.add(wa_hkont);
			}
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		for(int i = 0; i <= l_bseg.size()-1; i++)
		{
			reqCtx.update("form:bsegTable:"+i+":b_hkont");
		}
		reqCtx.update("form:business_area");
		reqCtx.update("form:branch");
		reqCtx.update("form:selectedCashOffice");
	}
	
	public void changeBranch()
	{
		selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());
		getCashOfficeHkont();
	}
	
	//**********************************************************************

	private String selectedCashOffice = new String();
	public String getSelectedCashOffice() {
		return selectedCashOffice;
	}

	public void setSelectedCashOffice(String selectedCashOffice) {
		this.selectedCashOffice = selectedCashOffice;
	}


	public void getCashOfficeHkont()
	{
		try{
			selectedCashOffice = new String();
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
			reqCtx.update("form:selectedCashOffice");
			reqCtx.update("form:outputTableBranchCash");
		} 
		catch (DAOException ex)
		{   
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:selectedCashOffice");
			addMessage("Info",ex.getMessage()); 
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
	
	
	
}
