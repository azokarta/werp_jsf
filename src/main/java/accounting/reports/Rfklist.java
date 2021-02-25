package accounting.reports;



import f4.BranchF4;
import f4.BukrsF4;
import f4.HkontF4;
import general.AppContext; 
import general.PermissionController;
import general.Validation;
import general.comparators.BkpfCompareAwkey; 
import general.comparators.BsikCompareBelnrGjahr;
import general.dao.BkpfDao;
import general.dao.BsikDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.services.CustomerService;
import general.tables.Bkpf;
import general.tables.Branch; 
import general.tables.Bsik;
import general.tables.Bukrs;
import general.tables.Customer; 
import general.tables.Hkont;
 









import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set; 

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import java.io.Serializable; 

import user.User;

@ManagedBean(name = "rfklistBean", eager = true)
@ViewScoped
public class Rfklist implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "RFKLIST";
	private final static Long transaction_id = (long) 56;
	public static Long getTransactionId() {
		return transaction_id;
	}
	
	
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
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{ 
			//Calendar curDate = Calendar.getInstance();
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Rfklist.transaction_id);

			for(Branch wa_branch:p_branchF4Bean.getBranch_list())
			{
				if (wa_branch.getBranch_id() == userData.getBranch_id())
				{
					selectedBranch = wa_branch;
				}
			}
			
			Calendar curDate = Calendar.getInstance(); 
			p_searchTable.setBldat_to(curDate.getTime());
			curDate.set(2016, 0, 01);
			p_searchTable.setBldat_from(curDate.getTime());
			
			for (Bukrs wa_bukrs: p_bukrsF4Bean.getBukrs_list()){
				if (!wa_bukrs.getBukrs().equals("0001")){
					bukrs_list.add(wa_bukrs);
				}
			}
			for(Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
			{
				if (wa_hkont.getHkont().startsWith("3310015") || wa_hkont.getHkont().startsWith("3310016"))
				{
					hkont_list.add(wa_hkont);
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
		try 
		{
			CustomerService personService = (CustomerService) appContext.getContext().getBean("customerService");
			p_customer_list = personService.dynamicSearch(searchCustomer);
				
			if (p_customer_list.size()==0)
			{
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
			addMessage("Error",ex.getMessage()); 
		}
	}
  	
  	public void assignFoundCustomer(){   	 
		if (selectedCustomer!= null && selectedCustomer.getId()!=null)
		{
			p_searchTable.setKreditor(selectedCustomer);
	  		if (selectedCustomer.getFiz_yur() == 1)
			{
	  			p_searchTable.setKreditorName(selectedCustomer.getName());
			}
	  		else
	  		{
	  			p_searchTable.setKreditorName(Validation.returnFioInitials(selectedCustomer.getFirstname(), selectedCustomer.getLastname(), selectedCustomer.getMiddlename()));
	  		}
			
		}
		else{				  
			p_searchTable.setKreditor(new Customer());
			p_searchTable.setKreditorName("");	  		
		}  
			
		//selectedCustomer = new Customer(); 
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView:kreditorName");
		
  	}
  	//******************************************************************************************* 
    //***********************************Others***********************************************************

  	public void toMainPage() {
		try {
			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath()  + "/general/mainPage.xhtml");
		}
		catch (Exception ex)
		{
			 
			addMessage("Error",ex.getMessage());  
		}
	}	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    } 	
	
	//*****************************************************************	
	//****************************Search Parameters**********************************
  	private int tabindex = 0;
  	public int getTabindex() {
		return tabindex;
	}
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}
  	public void to_search()
  	{
  		try 
		{
  			l_outputTable.clear();
  			Calendar cal_budat_from = Calendar.getInstance();
  			Calendar cal_budat_to = Calendar.getInstance();  
  			if(p_searchTable.bldat_from!=null)
  			{
  				cal_budat_from.setTime(p_searchTable.bldat_from);
  			}
  			if(p_searchTable.bldat_to!=null)
  			{
  				cal_budat_to.setTime(p_searchTable.bldat_to);
  			}
  			//Calendar cal_bldat_from = Calendar.getInstance();
  			//Calendar cal_bldat_to = Calendar.getInstance();
  			//cal_bldat_from.setTime(p_searchTable.bldat_from);
  			//cal_bldat_to.setTime(p_searchTable.bldat_to);
  			List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			
  			BsikDao bsikDao = (BsikDao) appContext.getContext().getBean("bsikDao");
  			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
  			CustomerDao customerDao = (CustomerDao) appContext.getContext().getBean("customerDao");
  			
  			
  			dynamicWhereClause = "";
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + "bukrs = '"+p_searchTable.getBukrs()+"' and ";
  			}
  			else
  				throw new DAOException("Выберите компанию");
//  			if (r_blart_types==1) dynamicWhereClause = dynamicWhereClause + "blart = 'GE'";
//  			else if (r_blart_types==2) dynamicWhereClause = dynamicWhereClause + "blart = 'TE'";	
//  			else if (r_blart_types==3) dynamicWhereClause = dynamicWhereClause + "blart = 'CE'";	
//  			else if (r_blart_types==4) dynamicWhereClause = dynamicWhereClause + "blart = 'AE'";
//  			else if (r_blart_types==5) dynamicWhereClause = dynamicWhereClause + "blart = 'SE'";
//  			else if (r_blart_types==6) dynamicWhereClause = dynamicWhereClause + "blart = 'RP'";
  			//dynamicWhereClause = dynamicWhereClause + "blart in ('GE','TE','CE','AE','SE')";
  			//System.out.println(dynamicWhereClause);
  			if(p_searchTable.bldat_from!=null && p_searchTable.bldat_to!=null && (p_searchTable.bldat_from.before(p_searchTable.bldat_to) || p_searchTable.bldat_from.equals(p_searchTable.bldat_to)))
  			{  			
  				if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and "; 
  				dynamicWhereClause = dynamicWhereClause + "bldat between '"+ new java.sql.Date(cal_budat_from.getTimeInMillis()) +"' and '" +new java.sql.Date(cal_budat_to.getTimeInMillis())+"'";
  			}  	 
  			dynamicWhereClause = dynamicWhereClause + " and ((waers = 'USD' and dmbtr <> dmbtr_paid) or (waers <> 'USD' and wrbtr <> wrbtr_paid))";
  			//System.out.println(dynamicWhereClause);
  			l_bkpf = bkpfDao.dynamicFindBkpf(dynamicWhereClause);
  			
  			dynamicWhereClause = "";
  			dynamicWhereClause = dynamicWhereClause + "blart in ('KP')";
  			if(p_searchTable.bldat_from!=null && p_searchTable.bldat_to!=null && (p_searchTable.bldat_from.before(p_searchTable.bldat_to) || p_searchTable.bldat_from.equals(p_searchTable.bldat_to)))
  			{  			
  				if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and "; 
  				dynamicWhereClause = dynamicWhereClause + "budat >= '"+ new java.sql.Date(cal_budat_from.getTimeInMillis()) +"'";
  			}
  			l_bkpfPayments = bkpfDao.dynamicFindBkpf(dynamicWhereClause);
  			
  			
  			dynamicWhereClause = "";
  			if (p_searchTable.kreditor!=null && p_searchTable.kreditor.getId()!=null)
  			{
  				if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and ";
  				dynamicWhereClause = dynamicWhereClause + "lifnr = " + p_searchTable.kreditor.getId(); 
  			}   
  			
  			
  			
			
			
			
  			
  			
			
  			tabindex = 1;
  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView");				
		}
		catch (DAOException ex)
		{			
			addMessage("Error",ex.getMessage()); 
		}  		 
  		
  	}
  	//*******************************************************************************
	//******************************************************************
//  	private int r_blart_types = 1;  	
//	public int getR_blart_types() {
//		return r_blart_types;
//	}
//	public void setR_blart_types(int r_blart_types) {
//		this.r_blart_types = r_blart_types;
//	}
  	
	private Bkpf p_bkpf = new Bkpf();
	public Bkpf getP_bkpf() {
		return p_bkpf;
	}
	public void setP_bkpf(Bkpf p_bkpf) {
		this.p_bkpf = p_bkpf;
	}
	
	private List<Bkpf> l_bkpf = new ArrayList<Bkpf>();
	public List<Bkpf> getL_bkpf() {
		return l_bkpf;
	}
	public void setL_bkpf(List<Bkpf> l_bkpf) {
		this.l_bkpf = l_bkpf;
	}
	
	private List<Bkpf> l_bkpfPayments = new ArrayList<Bkpf>();	
	public List<Bkpf> getL_bkpfPayments() {
		return l_bkpfPayments;
	}
	public void setL_bkpfPayments(List<Bkpf> l_bkpfPayments) {
		this.l_bkpfPayments = l_bkpfPayments;
	}
	
	
	
	
	//private List<Bsik> l_bsik2 = new ArrayList<Bsik>();
	private List<Bsik> l_bsik = new ArrayList<Bsik>();
	public List<Bsik> getL_bsik() {
		return l_bsik;
	}
	public void setL_bsik(List<Bsik> l_bsik) {
		this.l_bsik = l_bsik;
	}
	
	private List<Bsik> l_bsikPayments = new ArrayList<Bsik>();		
	public List<Bsik> getL_bsikPayments() {
		return l_bsikPayments;
	}
	public void setL_bsikPayments(List<Bsik> l_bsikPayments) {
		this.l_bsikPayments = l_bsikPayments;
	}
	
	private List<OutputTable> l_outputTable = new ArrayList<OutputTable>();
	public List<OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
	}
	
	
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private int index;
		private String waers;
		private String clientName;
		private String iin_bin;
		private String bukrs;
		private Long customer_id;
		private Long belnr;
		private Date budat;
		private int gjahr;
		private double dmbtr;
		private double wrbtr;
		private List<Bkpf> l_bkpfPayments = new ArrayList<Bkpf>();
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getClientName() {
			return clientName;
		}
		public void setClientName(String clientName) {
			this.clientName = clientName;
		}
		public String getIin_bin() {
			return iin_bin;
		}
		public void setIin_bin(String iin_bin) {
			this.iin_bin = iin_bin;
		}
		public Long getCustomer_id() {
			return customer_id;
		}
		public void setCustomer_id(Long customer_id) {
			this.customer_id = customer_id;
		}
		public Long getBelnr() {
			return belnr;
		}
		public void setBelnr(Long belnr) {
			this.belnr = belnr;
		}
		public Date getBudat() {
			return budat;
		}
		public void setBudat(Date budat) {
			this.budat = budat;
		}
		public int getGjahr() {
			return gjahr;
		}
		public void setGjahr(int gjahr) {
			this.gjahr = gjahr;
		}
		public double getDmbtr() {
			return dmbtr;
		}
		public void setDmbtr(double dmbtr) {
			this.dmbtr = dmbtr;
		}
		public double getWrbtr() {
			return wrbtr;
		}
		public void setWrbtr(double wrbtr) {
			this.wrbtr = wrbtr;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public List<Bkpf> getL_bkpfPayments() {
			return l_bkpfPayments;
		}
		public void setL_bkpfPayments(List<Bkpf> l_bkpfPayments) {
			this.l_bkpfPayments = l_bkpfPayments;
		}		
		
	}
	
	private SearchTable p_searchTable = new SearchTable();
	public SearchTable getP_searchTable() {
		return p_searchTable;
	}
	public void setP_searchTable(SearchTable p_searchTable) {
		this.p_searchTable = p_searchTable;
	}

	


	public class SearchTable {
		public SearchTable()
		{
			
		};
		private Customer kreditor = new Customer();
		private String kreditorName;
//		private Date budat_from;
//		private Date budat_to;
		private Date bldat_from;
		private Date bldat_to;	
		private String bukrs = "";
		private List<Branch> l_branch = new ArrayList<Branch>();
		private List<String> selectedBranches = new ArrayList<String>();
		public Customer getKreditor() {
			return kreditor;
		}
		public void setKreditor(Customer kreditor) {
			this.kreditor = kreditor;
		}
//		public Date getBudat_from() {
//			return budat_from;
//		}
//		public void setBudat_from(Date budat_from) {
//			this.budat_from = budat_from;
//		}
//		public Date getBudat_to() {
//			return budat_to;
//		}
//		public void setBudat_to(Date budat_to) {
//			this.budat_to = budat_to;
//		}
		public Date getBldat_from() {
			return bldat_from;
		}
		public void setBldat_from(Date bldat_from) {
			this.bldat_from = bldat_from;
		}
		public Date getBldat_to() {
			return bldat_to;
		}
		public void setBldat_to(Date bldat_to) {
			this.bldat_to = bldat_to;
		}
		public String getKreditorName() {
			return kreditorName;
		}
		public void setKreditorName(String kreditorName) {
			this.kreditorName = kreditorName;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public List<Branch> getL_branch() {
			return l_branch;
		}
		public void setL_branch(List<Branch> l_branch) {
			this.l_branch = l_branch;
		}
		public List<String> getSelectedBranches() {
			return selectedBranches;
		}
		public void setSelectedBranches(List<String> selectedBranches) {
			this.selectedBranches = selectedBranches;
		}
	}	
	
	public void updateBranch()
	{
		try{
			Long ba = 0L;
			Long br = 0L;
			p_searchTable.getL_branch().clear();
			for(Branch wa:p_branchF4Bean.getByBukrsOrBusinessAreaOfficesOnly(p_searchTable.bukrs,userData.getBukrs(),ba,ba, br,userData.getBranch_id()))
			{
				if (wa.getBusiness_area_id()!=null && wa.getBusiness_area_id().equals(1L) || wa.getBusiness_area_id().equals(2L) || wa.getBusiness_area_id().equals(3L) || wa.getBusiness_area_id().equals(4L))
				{
					p_searchTable.getL_branch().add(wa);
				}
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView:p_branch_id");	
  			reqCtx.update("branchForm:branchFormTable");	
  			
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		} 
	}
	
	
	
	//*****************************************************************************************************
		
}


class BsikComp implements Comparator<Bsik>{
	 
    public int compare(Bsik a1, Bsik a2) {
        if(a1.getBelnr() == a2.getBelnr()){
            return 0;
        } else if(a1.getBelnr() > a2.getBelnr()) {
            return 1;
        }
        else{
        	return -1;
        }
    }
} 