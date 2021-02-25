package finance.cashbankoperations;


import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import f4.WerksF4;
import general.AppContext; 
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.FmglflextDao;
import general.dao.PriceListDao;
import general.dao.StaffDao;
import general.dao.WerksBranchDao;
import general.output.tables.CashBankAccountStatement;
import general.services.FinanceServiceLogistics;
import general.services.InvoiceService;
import general.services.PrebkpfService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Currency;
import general.tables.ExchangeRate;
import general.tables.Fmglflext;
import general.tables.Prebkpf;
import general.tables.Prebseg;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.Werks;

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

@ManagedBean(name = "fksgBean", eager = true)
@ViewScoped
public class Fksg implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private final static String transaction_code = "FKSG";
	private final static Long transaction_id = (long) 535;
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
	
//	List<Hkont> hkont_list = new ArrayList<Hkont>();	
//	public List<Hkont> getHkont_list(){
//		return hkont_list;
//	}
//	

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
	
	public List<Branch> getBranch_list() {
		return branch_list;
	}
	public void setBranch_list(List<Branch> branch_list) {
		this.branch_list = branch_list;
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
  	//*****************************************************************************************************
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{  
			
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) { 
	            return; // Skip ajax requests.
	        }
			PermissionController.canRead(userData,Fksg.transaction_id);
			Calendar curDate = Calendar.getInstance();
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime()); 
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fksg.transaction_code); 
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
			p_bkpf.setBlart("G2");
			

			
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
	List<PriceListTypeTable> l_pricelist = new ArrayList<PriceListTypeTable>();	
	public List<PriceListTypeTable> getL_pricelist() {
		return l_pricelist;
	}

	public void setL_pricelist(List<PriceListTypeTable> l_pricelist) {
		this.l_pricelist = l_pricelist;
	}

	List<PriceListTypeTable> l_pricelist_filtered = new ArrayList<PriceListTypeTable>();	
	
	public List<PriceListTypeTable> getL_pricelist_filtered() {
		return l_pricelist_filtered;
	}

	public void setL_pricelist_filtered(List<PriceListTypeTable> l_pricelist_filtered) {
		this.l_pricelist_filtered = l_pricelist_filtered;
	}



	PriceListTypeTable selected_pricelist = new PriceListTypeTable();
	public PriceListTypeTable getSelected_pricelist() {
		return selected_pricelist;
	}

	public void setSelected_pricelist(PriceListTypeTable selected_pricelist) {
		this.selected_pricelist = selected_pricelist;
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
			
			l_bseg = new ArrayList<BsegTypeTable>();
			l_bseg.add(new BsegTypeTable(0));
			l_pricelist = new ArrayList<PriceListTypeTable>();
			l_pricelist_filtered = new ArrayList<PriceListTypeTable>();
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
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
					List<Object[]> l_obj = new ArrayList<>();
					l_obj = priceListDao.findMatnrWithLastAmounts(p_bkpf.getBukrs(), p_bkpf.getWaers(),userData.getU_language());
					for (Object[] result:l_obj)
					{
						//p.matnr,p.waers,p.dmbtr,m.text45,m.code
						PriceListTypeTable wa_pl =  new PriceListTypeTable();
						if (result[0]!=null) wa_pl.setMatnr(Long.parseLong(String.valueOf(result[0])));
						if (result[1]!=null) wa_pl.setWaers(String.valueOf(result[1]));
						if (result[2]!=null) wa_pl.setPrice(Double.parseDouble(String.valueOf(result[2]))); 
						if (result[3]!=null) wa_pl.setText45(String.valueOf(result[3]));
						if (result[4]!=null) wa_pl.setCode(String.valueOf(result[4]));
						
						l_pricelist.add(wa_pl);
//						l_pricelist_filtered.add(wa_pl);
					}
					addWerks(); 
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
				List<Object[]> l_obj = new ArrayList<>();
				l_obj = priceListDao.findMatnrWithLastAmounts(p_bkpf.getBukrs(), p_bkpf.getWaers(),userData.getU_language());
				for (Object[] result:l_obj)
				{
					//p.matnr,p.waers,p.dmbtr,m.text45,m.code
					PriceListTypeTable wa_pl =  new PriceListTypeTable();
					if (result[0]!=null) wa_pl.setMatnr(Long.parseLong(String.valueOf(result[0])));
					if (result[1]!=null) wa_pl.setWaers(String.valueOf(result[1]));
					if (result[2]!=null) wa_pl.setPrice(Double.parseDouble(String.valueOf(result[2]))); 
					if (result[3]!=null) wa_pl.setText45(String.valueOf(result[3]));
					if (result[4]!=null) wa_pl.setCode(String.valueOf(result[4]));
					
//					l_pricelist.add(wa_pl);
//					l_pricelist_filtered.add(wa_pl);
				}
				addWerks();
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
			//#{fksgBean.searchSalary.branch_id}
			System.out.println(searchSalary.getBranch_id());
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			//reqCtx.execute("PF('priceListType').show();");
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
		private double unitprice;
		
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
		public double getUnitprice() {
			return unitprice;
		}
		public void setUnitprice(double unitprice) {
			this.unitprice = unitprice;
		}
		
		
		
	}
	
	
	public class PriceListTypeTable {
		
		public PriceListTypeTable() {

	    }
		private Long matnr;
		private double price;
		private String waers;
		private String text45;
		private String code;
		public Long getMatnr() {
			return matnr;
		}
		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public String getText45() {
			return text45;
		}
		public void setText45(String text45) {
			this.text45 = text45;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		
		
	}
	
	//*****************************************************************************************************
	//***************************Save method********************************
	public void to_save() throws IOException{
		try { 
//			System.out.println(selectedStaff.getFullFIO());;
			//System.out.println(p_bkpf.getBrnch());
			
			PermissionController.canWrite(userData, Fksg.transaction_id);
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
			}
			
			if (podotchetnikId==null || podotchetnikId.equals(0L))
			{
				throw new DAOException(Helper.getErrorMessage(90L, userData.getU_language()));
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
			
			
			FinanceServiceLogistics financeServiceLogistics  = (FinanceServiceLogistics) appContext.getContext().getBean("financeServiceLogistics");
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			Bseg p_bsegKredit = new Bseg();
			Calendar cal = Calendar.getInstance(); 	 
			cal.setTime(p_bkpf.getBudat());
			
			p_bkpf.setUsnam((long) userData.getUserid()); 
			p_bkpf.setTcode(Fksg.transaction_code); 
			p_bkpf.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf.setBrnch(getSelectedBranch().getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			p_bkpf.setAwtyp(0);
			
			
			p_bsegKredit.setBuzei(1);
			p_bsegKredit.setHkont(selectedCashOffice.getHkont());
			p_bsegKredit.setBukrs(p_bkpf.getBukrs());
			p_bsegKredit.setGjahr(p_bkpf.getGjahr());
			p_bsegKredit.setBschl("40");
			p_bsegKredit.setShkzg("S");

			
			
			for(BsegTypeTable wa_bseg:l_bseg)
		  		{ 
				wa_bseg.getT_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getT_bseg().setGjahr(p_bkpf.getGjahr());
				wa_bseg.getT_bseg().setBschl("50");
				wa_bseg.getT_bseg().setShkzg("H");
				wa_bseg.getT_bseg().setSgtxt(podotchetnikFio+" ID:"+podotchetnikId);
				wa_bseg.getT_bseg().setHkont(financeServiceLogistics.get6010MatnrHkont(p_bkpf.getBukrs(), wa_bseg.getT_bseg().getMatnr(), "FinanceServiceImpl-->IncomeNoInvoiceFKSG"));
				
				
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
//				userData.setNew_fi_doc(true);
//				userData.setTrans_id(Fksg.transaction_id);
				
				Calendar curDate = Calendar.getInstance();
				PrebkpfService prebkpfService = (PrebkpfService) appContext.getContext().getBean("prebkpfService");
				Prebkpf p_prebkpf = new Prebkpf();
				BeanUtils.copyProperties(p_bkpf, p_prebkpf);
				p_prebkpf.setStatus(0);
				p_prebkpf.setCreated_date(curDate.getTime());
				p_prebkpf.setHkont_d(selectedCashOffice.getHkont());
				p_prebkpf.setHkont_k(l_bseg.get(0).getT_bseg().getHkont());
				if (p_prebkpf.getWaers().equals("USD"))
				{
					p_prebkpf.setSumma(p_bsegKredit.getDmbtr());
				}
				else
				{
					p_prebkpf.setSumma(p_bsegKredit.getWrbtr());
				}
				p_prebkpf.setPodotchetnik_id(podotchetnikId);
				List<Prebseg> l_prebseg = new ArrayList<Prebseg>();
				for(Bseg wa_bseg:l_bsegFinal)
				{
					Prebseg wa_prebseg = new Prebseg();
					BeanUtils.copyProperties(wa_bseg, wa_prebseg);
					l_prebseg.add(wa_prebseg);
				}
				prebkpfService.save(p_prebkpf,l_prebseg);
				
				
//				Long newDocBelnr = financeService.IncomeNoInvoiceFKSG(p_bkpf, l_bsegFinal, userData, podotchetnikId);		
				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//				context.redirect(context.getRequestContextPath()  + "/accounting/reports/fa03.xhtml?belnr="+newDocBelnr+"&gjahr="+p_bkpf.getGjahr()+"&bukrs="+p_bkpf.getBukrs());
				context.redirect(context.getRequestContextPath()  + "/finance/cashbankoperations/fksg.xhtml");
	
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
	public boolean editdisabled = false;	
	public boolean isEditdisabled() {
		return editdisabled;
	}
	public void setEditdisabled(boolean editdisabled) {
		this.editdisabled = editdisabled;
	}

	public void to_check() throws IOException{
		try { 
			
			PermissionController.canWrite(userData, Fksg.transaction_id);
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
			}
			if (podotchetnikId==null || podotchetnikId.equals(0L))
			{
				throw new DAOException(Helper.getErrorMessage(90L, userData.getU_language()));
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
			InvoiceService invoiceService = (InvoiceService) appContext.getContext().getBean("invoiceService");
			
			Long a_werks = 0L;
			Map<Long, Double> matnrMengeMap = new HashMap<Long, Double>();
			for(BsegTypeTable wa:l_bseg)
			{
				if (wa.getT_bseg().getWerks() == null || wa.getT_bseg().getWerks().equals(0L))
				{
					throw new DAOException(Helper.getErrorMessage(10L, userData.getU_language()));
				}
				a_werks = wa.getT_bseg().getWerks();
				
				if(wa.getT_bseg().getMenge()>=1)
				{
					if (p_bkpf.getWaers().equals("USD"))
					{
						if (p_bseg.getT_bseg().getDmbtr()==0)
						{
							throw new DAOException(Helper.getErrorMessage(8L, userData.getU_language()));							
						}
						p_bseg.getT_bseg().setWrbtr(0);
						p_bseg.getT_bseg().setDmbtr(p_bseg.getT_bseg().getDmbtr()*p_bseg.getT_bseg().getMenge());
					}
					else
					{
						if (p_bseg.getT_bseg().getWrbtr()==0)
						{
							throw new DAOException(Helper.getErrorMessage(8L, userData.getU_language()));							
						}
						p_bseg.getT_bseg().setWrbtr(p_bseg.getT_bseg().getWrbtr()*p_bseg.getT_bseg().getMenge());
						p_bseg.getT_bseg().setDmbtr(GeneralUtil.round(p_bseg.getT_bseg().getWrbtr()/p_bkpf.getKursf(), 2));
					}
					
					
					
				}
				else
				{
					throw new DAOException(Helper.getErrorMessage(11L, userData.getU_language()));
				}
				matnrMengeMap = new HashMap<Long, Double>();
				matnrMengeMap.put(wa.getT_bseg().getMatnr(), wa.getT_bseg().getMenge());
				invoiceService.checkStaffAccountabilityMatnr(podotchetnikId, a_werks, matnrMengeMap);
			}
			
			
//			System.out.println(selectedStaff.getFullFIO());
			editdisabled = true;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		}  
		
	}
	public boolean summaBool(String a_type){
		if (editdisabled==true)
		{
			return true;			
		}
		else if (a_type.equals("DMBTR") && p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD"))
		{
			return false;
		}
		else if (a_type.equals("WRBTR") && p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD"))
		{
			return false;
		}
		return true;
	}
	public void to_edit() throws IOException{
		try { 
//			System.out.println(selectedStaff.getFullFIO());
			editdisabled = false;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		} 
		catch (DAOException ex)
		{   
			addMessage("Info",ex.getMessage()); 
		}  
		
	}
	public void changeBukrs()
	{
		//hkont_list.clear();
		selectedBranch = new Branch();
		selectedCashOfficeIndex = 0;
		selectedCurrency = new Currency();
		l_as2.clear();
		l_as.clear();
		p_bkpf.setWaers(null);
		p_bkpf.setKursf(0);
		p_bkpf.setBrnch(null);
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
//		reqCtx.update("form:business_area");
//		reqCtx.update("form:bkpf_kursf");
//		reqCtx.update("form:bkpf_waers");
//		reqCtx.update("form:branch");
//		reqCtx.update("form:singleCurrency");
//		reqCtx.update("form:selectedCashOffice");
	}

	public void addWerks()
	{
		werks_list.clear();
		
		WerksBranchDao werksBranchDao = (WerksBranchDao) appContext.getContext().getBean("werksBranchDao");
		werks_list = werksBranchDao.findAllWerksByBranch(p_bkpf.getBrnch());
		if (werks_list.size()==1)
		{
			selectedWerks = werks_list.get(0);
			for(BsegTypeTable wa_bseg:l_bseg)
	  		{
	  			wa_bseg.getT_bseg().setWerks(selectedWerks.getWerks());
	  			wa_bseg.setWerksName(selectedWerks.getText45()); 
	  		} 
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		for(int i = 0; i <= l_bseg.size()-1; i++)
		{
			reqCtx.update("form:bsegTable:"+i+":b_werks");
		}
	}
	public void changeBranch()
	{
		l_bseg = new ArrayList<BsegTypeTable>();
		l_bseg.add(new BsegTypeTable(0));
		selectedCashOfficeIndex = 0;
		selectedCurrency = new Currency();
		l_as2.clear();
		l_as.clear();
		p_bkpf.setWaers(null);
		p_bkpf.setKursf(0);
		
		
	
			
			
		
		
		
		selectedBranch = p_branchF4Bean.getL_branch_map().get(p_bkpf.getBrnch());
		clearCollector();
		branch_list.clear();
		branch_list = p_branchF4Bean.getByBukrsOrBusinessAreaOfficesOnly(p_bkpf.getBukrs(),"",null,null,p_bkpf.getBrnch(),userData.getBranch_id());

		searchSalary.setBranch_id(p_bkpf.getBrnch());

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
//		reqCtx.update("form:business_area");
//		reqCtx.update("form:bkpf_kursf");
//		reqCtx.update("form:bkpf_waers");
//		reqCtx.update("form:branch");
//		reqCtx.update("form:singleCurrency");
//		reqCtx.update("form:selectedCashOffice");
//		reqCtx.update("form:staffBranch");
		
		//reqCtx.update("form");
		//reqCtx.update("form:staffBranch");
		

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
	public void onRowDblClckSelect() {
		try
		{	
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('priceListType').hide();");
			
			if (p_bkpf.getWaers()!=null)
			{	
				p_bseg.setMatnrName(selected_pricelist.getText45());
				p_bseg.getT_bseg().setMatnr(selected_pricelist.getMatnr());
				p_bseg.getT_bseg().setMenge(1);
				if (p_bkpf.getWaers().equals("USD"))
				{
					p_bseg.getT_bseg().setWrbtr(0);
					p_bseg.getT_bseg().setDmbtr(selected_pricelist.getPrice());
					p_bseg.setUnitprice(selected_pricelist.getPrice());
				}
				else
				{
					p_bseg.getT_bseg().setWrbtr(selected_pricelist.getPrice());
					p_bseg.getT_bseg().setDmbtr(GeneralUtil.round(selected_pricelist.getPrice()/p_bkpf.getKursf(), 2));
					p_bseg.setUnitprice(selected_pricelist.getPrice());
				}
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				for(int i = 0; i <= l_bseg.size()-1; i++)
				{
					reqCtx.update("form:bsegTable:"+i+":b_menge");
					reqCtx.update("form:bsegTable:"+i+":b_matnr");
					reqCtx.update("form:bsegTable:"+i+":b_dmbtr");
					reqCtx.update("form:bsegTable:"+i+":b_wrbtr");
				}
			}
			else
			{
				throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
			}
		} 
		catch (DAOException ex)
		{   
			
			
			addMessage("Info",ex.getMessage());
		}
	}
	
	
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
	public void assignSelectedWerks(){
  		try
  		{
  			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('werksWidget').hide();");
			
  			
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
				
				dynamicWhereClause = dynamicWhereClause + " and (sal.end_date >= '"+GeneralUtil.getSQLDate(curDate)+"' or sal.payroll_date  >= '"+GeneralUtil.getSQLDate(curDate)+"')";
				
				
				if (searchSalary!=null && searchSalary.getPosition_id()!=null)
				{
					dynamicWhereClause = dynamicWhereClause + " and sal.position_id = "+searchSalary.getPosition_id();
				}
				
				if (searchSalary.getBranch_id() != null && searchSalary.getBranch_id() > 0) {
					dynamicWhereClause = dynamicWhereClause + " and sal.branch_id = "
							+ searchSalary.getBranch_id();
				} else {
					throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
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
		private Long podotchetnikId;
		private String podotchetnikFio;
		public String getPodotchetnikFio() {
			return podotchetnikFio;
		}
		public void setPodotchetnikFio(String podotchetnikFio) {
			this.podotchetnikFio = podotchetnikFio;
		}
		public Long getPodotchetnikId() {
			return podotchetnikId;
		}
		public void setPodotchetnikId(Long podotchetnikId) {
			this.podotchetnikId = podotchetnikId;
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
			
			podotchetnikId = selectedStaff.getStaff_id();
			podotchetnikFio = Validation.returnFioInitials(selectedStaff.getFirstname(), selectedStaff.getLastname(), selectedStaff.getMiddlename());
			
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
		
		public void clearCollector()
	  	{
	  		selectedStaff = new Staff();
	  		podotchetnikId = null;
			podotchetnikFio = "";
	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:selectedStaff");
			reqCtx.update("form:staffTable");
			
	  	}
		public void addAllPriceList()
		{
			if (l_pricelist_filtered.size()==0)
			{
				l_pricelist_filtered.addAll(l_pricelist);
			}
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:singlePriceList");
		}
		
		public void assignSearchCollector()
		{
			searchSalary.setBranch_id(p_bkpf.getBrnch());
			searchSalary.setPosition_id(4L);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:staffBranch");
			reqCtx.update("form:se_pos");
			

		}
			
}
