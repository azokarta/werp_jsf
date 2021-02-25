package finance.cashbankoperations;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.HkontDao;
import general.services.FinanceService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.ExchangeRate;
import general.tables.Hkont;

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


@ManagedBean(name = "faicfp2Bean", eager = true)
@ViewScoped
public class Faicfp2 implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FAICFP2";
	private final static Long transaction_id = (long) 356;
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
  	public List<Branch> geBranch_list(){
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
			PermissionController.canRead(userData,Faicfp2.transaction_id);
			Calendar curDate = Calendar.getInstance();
					
			for(ExchangeRate wa_er: p_exchangeRateF4Bean.getExchageRate_list())
			{
				if (wa_er.getType()==1)
				{
					l_er.add(wa_er);
				}
			}

			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBlart("I2");
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
			if (buzei==1)
			{
				this.p_bseg.setShkzg("H");
			}
			else if(buzei==2)
			{
				this.p_bseg.setShkzg("S");
			}
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
  				for(BsegClass wa_bseg:l_bseg)
  		  		{
  					if (wa_bseg.getHkont_waers()!=null && !wa_bseg.getHkont_waers().equals("USD"))
  					{ 
  						wa_bseg.getP_bseg().setDmbtr(wa_bseg.getP_bseg().getWrbtr()/p_bkpf.getKursf()); 
  					}
  					else if (wa_bseg.getHkont_waers()!=null && wa_bseg.getHkont_waers().equals("USD"))
  					{ 
  						wa_bseg.getP_bseg().setWrbtr(wa_bseg.getP_bseg().getDmbtr()*p_bkpf.getKursf()); 
  					}
  		  		}
  				RequestContext reqCtx = RequestContext.getCurrentInstance();
  				
  				for(int i = 0; i <= l_bseg.size()-1; i++)
  				{
  					reqCtx.update("form:bsegTable:"+i+":dmbtr");
  					reqCtx.update("form:bsegTable:"+i+":wrbtr");
  				}
  				saveDisable = true;
  				reqCtx.update("form:saveButton");
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

	public void assignSelectedExchangeRate()
	{
		try
		{	
			clearAndAddBseg();
			
			//selecting appropriate general ledger accounts
			l_hkont.clear();
			List<Hkont> l_hkont2 = new ArrayList<Hkont>();
			if (p_bkpf.getBukrs()==null || p_bkpf.getBukrs().equals("0"))
			{
				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
			}
			if (p_bkpf.getBrnch()==null || p_bkpf.getBrnch().equals(0L))
			{
				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
			}
			HkontDao hkontDao = (HkontDao) appContext.getContext().getBean("hkontDao");		
			l_hkont = hkontDao.findHkontBranchTree(p_bkpf.getBukrs(), p_bkpf.getBrnch(), selectedExchangeRate.getSecondary_currency());
			if (p_bkpf.getBrnch().equals(60L)|| p_bkpf.getBrnch().equals(102L) || p_bkpf.getBrnch().equals(214L))
			{
				for(Hkont wa:l_hkont)
				{
					if (wa.getBranch_id().equals(p_bkpf.getBrnch()))
					{
						l_hkont2.add(wa);
					}
					
				}
				l_hkont = l_hkont2;
			}
			
			
			if (l_hkont.size()>0)
			{
				p_bkpf.setWaers(selectedExchangeRate.getSecondary_currency());
				p_bkpf.setKursf(selectedExchangeRate.getSc_value());
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
			saveDisable = true;
			reqCtx.update("form:saveButton");
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
			clearAndAddBseg();
			List<Hkont> l_hkont2 = new ArrayList<Hkont>();
			//selecting appropriate general ledger accounts
			l_hkont.clear();
			if (p_bkpf.getBukrs()==null || p_bkpf.getBukrs().equals("0"))
			{
				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
			}
			if (p_bkpf.getBrnch()==null || p_bkpf.getBrnch().equals(0L))
			{
				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
			}
			HkontDao hkontDao = (HkontDao) appContext.getContext().getBean("hkontDao");		
			l_hkont = hkontDao.findHkontBranchTree(p_bkpf.getBukrs(), p_bkpf.getBrnch(), "USD");
			if (p_bkpf.getBrnch().equals(60L) || p_bkpf.getBrnch().equals(102L)  || p_bkpf.getBrnch().equals(214L))
			{
				for(Hkont wa:l_hkont)
				{
					if (wa.getBranch_id().equals(p_bkpf.getBrnch()))
					{
						l_hkont2.add(wa);
					}
					
				}
				l_hkont = l_hkont2;
			}
			if (l_hkont.size()>0)
			{
				p_bkpf.setWaers("USD");
				p_bkpf.setKursf(1);
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
			saveDisable = true;
			reqCtx.update("form:saveButton");
			
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
			p_bseg.setHkont_name(selectedHkont.getHkont()+" "+selectedHkont.getWaers());
			p_bseg.getP_bseg().setHkont(selectedHkont.getHkont());
			p_bseg.setHkont_waers(selectedHkont.getWaers());
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:bsegTable:"+p_bseg.getIndex()+":hkont");
			if (selectedHkont.getWaers()!=null && selectedHkont.getWaers().equals("USD") && p_bseg.getP_bseg().getBuzei()==1)
			{
				p_bseg.getP_bseg().setWrbtr(0);
				p_bseg.setWrbtrDisable(true);
				p_bseg.setDmbtrDisable(false);
			}
			else if (p_bseg.getP_bseg().getBuzei()==1)
			{
				p_bseg.getP_bseg().setDmbtr(0);
				p_bseg.setWrbtrDisable(false);
				p_bseg.setDmbtrDisable(true);
			}
			reqCtx.update("form:bsegTable:"+p_bseg.getIndex()+":dmbtr");
			reqCtx.update("form:bsegTable:"+p_bseg.getIndex()+":wrbtr");
			saveDisable = true;
			reqCtx.update("form:saveButton");
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}
	//********************************************************************************************
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
			PermissionController.canWrite(userData, Faicfp2.transaction_id);
			
			List<Bseg> wal_bseg = new ArrayList<Bseg>();
			wal_bseg.add(l_bseg.get(0).getP_bseg());
			wal_bseg.add(l_bseg.get(1).getP_bseg());

			FinanceService financeService = (FinanceService) appContext.getContext().getBean("financeService");			
			Long newDocBelnr = financeService.createFAICF(p_bkpf, wal_bseg);
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			try {
				userData.setNew_fi_doc(true);
				userData.setTrans_id(Faicfp2.transaction_id);
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
			if (p_bkpf.getWaers()==null)
			{
				throw new DAOException(Helper.getErrorMessage(1L, userData.getU_language()));
			}
			Calendar cal_budat = Calendar.getInstance();
			Calendar curDate = Calendar.getInstance(); 
			cal_budat.setTime(p_bkpf.getBudat());
			p_bkpf.setUsnam((long) userData.getUserid());
			p_bkpf.setTcode(Faicfp2.transaction_code);
			p_bkpf.setGjahr(cal_budat.get(Calendar.YEAR));
			p_bkpf.setMonat(cal_budat.get(Calendar.MONTH) + 1);
			p_bkpf.setBrnch(userData.getBranch_id());
			p_bkpf.setCpudt(curDate.getTime());
			
			l_bseg.get(1).getP_bseg().setWrbtr(l_bseg.get(0).getP_bseg().getWrbtr());
			l_bseg.get(1).getP_bseg().setDmbtr(l_bseg.get(0).getP_bseg().getDmbtr());
			double dmbtr_s = 0;
			double wrbtr_s = 0;
			
			p_bkpf.setDmbtr(dmbtr_s);
			p_bkpf.setWrbtr(wrbtr_s);
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();			
			for (int i = 0; i<l_bseg.size();i++)
			{
				BsegClass wa_bseg = l_bseg.get(i);
				wa_bseg.getP_bseg().setBukrs(p_bkpf.getBukrs());
				wa_bseg.getP_bseg().setGjahr(p_bkpf.getGjahr());
				if (wa_bseg.getP_bseg().getShkzg()!=null && wa_bseg.getP_bseg().getShkzg().equals("H"))
				{
					wa_bseg.getP_bseg().setBschl("50");
				}
				else if (wa_bseg.getP_bseg().getShkzg()!=null && wa_bseg.getP_bseg().getShkzg().equals("S"))
				{
					wa_bseg.getP_bseg().setBschl("40");
				}
				wa_bseg.setWrbtrDisable(true);
				wa_bseg.setDmbtrDisable(true);
				reqCtx.update("form:bsegTable:"+i+":dmbtr");
				reqCtx.update("form:bsegTable:"+i+":wrbtr");
				reqCtx.update("form:bsegTable:"+i+":hkont");
				reqCtx.update("form:bsegTable:"+i+":shkzg");
			}
			refreshUSD();
			saveDisable = false;
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
			for (int i = 0; i<2;i++)
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
		l_bseg.get(1).setDmbtrDisable(true);
		l_bseg.get(1).setWrbtrDisable(true);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bsegTable:1:hkont");
		saveDisable = true;
		reqCtx.update("form:saveButton");
	
	}
	public void changeBukrs()
	{
		
		p_bkpf.setWaers(null);
		p_bkpf.setKursf(0);
		l_hkont.clear();
		clearAndAddBseg();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
		saveDisable = true;
		reqCtx.update("form:saveButton");
	}
	public void changeBranch()
	{
		
		p_bkpf.setWaers(null);
		p_bkpf.setKursf(0);
		l_hkont.clear();
		clearAndAddBseg();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
		saveDisable = true;
		reqCtx.update("form:saveButton");
	}
	public void changeShkzg()
	{
		if(l_bseg.get(0).getP_bseg().getShkzg().equals("S"))
		{
			l_bseg.get(1).getP_bseg().setShkzg("H");
		}
		
		if(l_bseg.get(0).getP_bseg().getShkzg().equals("H"))
		{
			l_bseg.get(1).getP_bseg().setShkzg("S");
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bsegTable:1:shkzg");
		saveDisable = true;
		reqCtx.update("form:saveButton");
	
	}
	
	//*****************************************************************************************************		
}
