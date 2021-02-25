package accounting.reports;

import f4.BranchF4;
import f4.BukrsF4;
import f4.HkontF4; 
import general.AppContext; 
import general.PermissionController;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.BranchTreeDao;
import general.dao.CustomerDao;
import general.dao.DAOException; 
import general.services.CustomerService; 
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.BranchTree;
import general.tables.Bseg;
import general.tables.Bukrs; 
import general.tables.Customer;  
import general.tables.Hkont; 

import java.io.Serializable;

 






import java.util.ArrayList;
import java.util.Calendar;
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
 





import user.User;

@ManagedBean(name = "rfdlistBean", eager = true)
@ViewScoped
public class Rfdlist implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "RFDLIST";
	private final static Long transaction_id = (long) 55;
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
			PermissionController.canRead(userData,Rfdlist.transaction_id);
			
			for(Branch wa_branch:p_branchF4Bean.getBranch_list())
			{
				if (wa_branch.getBranch_id() == userData.getBranch_id())
				{
					selectedBranch = wa_branch;
				}
			}
			
			Calendar curDate = Calendar.getInstance(); 
			p_searchTable.setBudat_to(curDate.getTime());
		
			
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
			BranchTreeDao branchTreeDao = (BranchTreeDao) appContext.getContext().getBean("branchTreeDao");
			for(BranchTree bt:branchTreeDao.findByBukrs("1000"))
			{
				System.out.println(bt.getText45());
			}
			
			System.out.println(curDate.get(Calendar.MONTH)+1);
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
			p_searchTable.setDebitor(selectedCustomer);
	  		if (selectedCustomer.getFiz_yur() == 1)
			{
	  			p_searchTable.setDebitorName(selectedCustomer.getName());
			}
	  		else
	  		{
	  			p_searchTable.setDebitorName(Validation.returnFioInitials(selectedCustomer.getFirstname(), selectedCustomer.getLastname(), selectedCustomer.getMiddlename()));
	  		}
			
		}
		else{				  
			p_searchTable.setDebitor(new Customer());
			p_searchTable.setDebitorName("");	  		
		}  
			
		//selectedCustomer = new Customer(); 
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView:debitorName");
		
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
  			if(p_searchTable.budat_from!=null)
  			{
  				cal_budat_from.setTime(p_searchTable.budat_from);
  			}
  			if(p_searchTable.budat_to!=null)
  			{
  				cal_budat_to.setTime(p_searchTable.budat_to);
  			}
  			//Calendar cal_bldat_from = Calendar.getInstance();
  			//Calendar cal_bldat_to = Calendar.getInstance();
  			//cal_bldat_from.setTime(p_searchTable.bldat_from);
  			//cal_bldat_to.setTime(p_searchTable.bldat_to);
  			List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			
  			//BsidDao bsidDao = (BsidDao) appContext.getContext().getBean("bsidDao");
  			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
  			CustomerDao customerDao = (CustomerDao) appContext.getContext().getBean("customerDao");
  			
  			
  			
  			dynamicWhereClause = "";
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + "bukrs = '"+p_searchTable.getBukrs()+"' ";
  			}
  			else
  				throw new DAOException("Выберите компанию");
  			
  			if (r_blart_types==1) dynamicWhereClause = dynamicWhereClause + " and blart = 'GS' ";
  			
  			if(p_searchTable.budat_from!=null && p_searchTable.budat_to!=null && (p_searchTable.budat_from.before(p_searchTable.budat_to) || p_searchTable.budat_from.equals(p_searchTable.budat_to)))
  			{  			
  				if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and "; 
  				dynamicWhereClause = dynamicWhereClause + "budat between '"+ new java.sql.Date(cal_budat_from.getTimeInMillis()) +"' and '" +new java.sql.Date(cal_budat_to.getTimeInMillis())+"'";
  			}
  			dynamicWhereClause = dynamicWhereClause + " and closed=0";
  			l_bkpf = bkpfDao.dynamicFindBkpf(dynamicWhereClause);
  			/*
  			dynamicWhereClause = "";
  			dynamicWhereClause = dynamicWhereClause + "blart in ('DP','CP')";
  			if(p_searchTable.budat_from!=null && p_searchTable.budat_to!=null && (p_searchTable.budat_from.before(p_searchTable.budat_to) || p_searchTable.budat_from.equals(p_searchTable.budat_to)))
  			{  			
  				if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and "; 
  				dynamicWhereClause = dynamicWhereClause + "budat >= '"+ new java.sql.Date(cal_budat_from.getTimeInMillis()) +"'";
  			} 
  			
  			l_bkpfPayments = bkpfDao.dynamicFindBkpf(dynamicWhereClause);
  			
  			dynamicWhereClause = "";
  			if (p_searchTable.debitor!=null && p_searchTable.debitor.getId()!=null)
  			{
  				if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and ";
  				dynamicWhereClause = dynamicWhereClause + "kunnr = " + p_searchTable.debitor.getId(); 
  			}   
  			if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and ";
			dynamicWhereClause = dynamicWhereClause + "shkzg = 'S'"; 
			l_bsid = bsidDao.dynamicSearch(dynamicWhereClause);
			
			Collections.sort(l_bsid,new BsidCompareBelnrGjahr());	*/		
			if (l_bkpf.size()>0)
			{
				for(Bkpf wa_bkpf: l_bkpf)
	  			{ 
	  				OutputTable wa_out = new OutputTable();  
	  				wa_out.setBukrs(wa_bkpf.getBukrs());
		  			wa_out.setBelnr(wa_bkpf.getBelnr());	  			
		  			wa_out.setGjahr(wa_bkpf.getGjahr());
		  			wa_out.setWaers(wa_bkpf.getWaers());
		  			wa_out.setBudat(wa_bkpf.getBudat());
		  			wa_out.setCustomer_id(wa_bkpf.getCustomer_id());
		  			wa_out.setDmbtr(wa_bkpf.getDmbtr()-wa_bkpf.getDmbtr_paid());
		  			wa_out.setWrbtr(wa_bkpf.getWrbtr()-wa_bkpf.getWrbtr_paid());
		  			l_customer_id.add(wa_bkpf.getCustomer_id());
		  			l_outputTable.add(wa_out);
		  			/*
		  			Bsid searchKey = new Bsid();
		  			searchKey.setBelnr(wa_bkpf.getBelnr()); 
		  			searchKey.setGjahr(wa_bkpf.getGjahr()); 
		  				
		  			int index = Collections.binarySearch(l_bsid, searchKey, new BsidCompareBelnrGjahr());
		  			while (index > 0 && new BsidCompareBelnrGjahr().compare(l_bsid.get(index), l_bsid.get(index-1)) == 0) {
					    index--;
					}
		  			//System.out.println(wa_bkpf.getBelnr()+" "+index);
		  			if (index>=0)
		  			{
		  				//System.out.println(wa_bkpf.getBelnr()+" "+wa_bkpf.getGjahr()+" "+index);
		  				boolean loop = true;
		  				while (loop)
		  				{
		  					Bsid wa_bsid = l_bsid.get(index);
		  					//System.out.println(wa_bsik.getBelnr()+" "+wa_bsik.getGjahr()+" "+ wa_bkpf.getBelnr()+" "+wa_bkpf.getGjahr());
		  					if (wa_bsid.getBelnr().equals(wa_bkpf.getBelnr())  && wa_bsid.getGjahr() == wa_bkpf.getGjahr())
		  					{
		  						wa_out.setDmbtr(wa_out.getDmbtr()+wa_bsid.getDmbtr());
		  						wa_out.setWrbtr(wa_out.getWrbtr()+wa_bsid.getWrbtr());
		  						if (wa_bsid.getKunnr() != null)
		  						{
		  							wa_out.setCustomer_id(wa_bsid.getKunnr());
		  							l_customer_id.add(wa_bsid.getKunnr());
		  						}
		  						//System.out.println(index);
		  						if (l_bsid.size()-2>index) index = index + 1;
		  						else{
		  							loop = false;
		  						}
		  							
		  					}
		  					else
		  					{
		  						loop = false;
		  					}
		  				}
		  				l_outputTable.add(wa_out);
		  			}
		  			
	  				*/
	  				
	  				
	  			} 
			}
			/*
			Collections.sort(l_bkpfPayments,new BkpfCompareAwkey());
			for (OutputTable wa_out:l_outputTable)
			{
				Bkpf searchKey = new Bkpf();
				long awkey = wa_out.getBelnr() * 10000; 
				awkey = awkey + wa_out.getGjahr(); 
	  			searchKey.setAwkey(awkey);  
				int index = Collections.binarySearch(l_bkpfPayments, searchKey, new BkpfCompareAwkey()); 
				while (index > 0 && new BkpfCompareAwkey().compare(l_bkpfPayments.get(index), l_bkpfPayments.get(index-1)) == 0) {
				    index--;
				}
	  			if (index>=0)
	  			{ 
	  				boolean loop = true;
	  				while (loop)
	  				{
	  					Bkpf wa_bkpf = l_bkpfPayments.get(index); 
	  					if (wa_bkpf.getAwkey().equals(awkey))
	  					{
	  						wa_out.getL_bkpfPayments().add(wa_bkpf);  
	  						if (l_bkpfPayments.size()-2 > index)
	  						{
	  							index = index + 1;
	  						}
	  						else
	  						{
	  							loop = false;
	  						}
	  							
	  					}
	  					else
	  					{
	  						loop = false;
	  					}
	  				} 
	  			}
			}
			
			
			for (OutputTable wa_out:l_outputTable)
			{
				for (Bkpf wa_bkpf:wa_out.getL_bkpfPayments())
				{
					Bsid searchKey = new Bsid();
		  			searchKey.setBelnr(wa_bkpf.getBelnr()); 
		  			searchKey.setGjahr(wa_bkpf.getGjahr()); 
		  				
		  			int index = Collections.binarySearch(l_bsid, searchKey, new BsidCompareBelnrGjahr());
		  			while (index > 0 && new BsidCompareBelnrGjahr().compare(l_bsid.get(index), l_bsid.get(index-1)) == 0) {
					    index--;
					}
		  			//System.out.println(wa_bkpf.getBelnr()+" "+index);
		  			if (index>=0)
		  			{ 
		  				boolean loop = true;
		  				while (loop)
		  				{
		  					Bsid wa_bsid = l_bsid.get(index); 
		  					if (wa_bsid.getBelnr().equals(wa_bkpf.getBelnr())  && wa_bsid.getGjahr() == wa_bkpf.getGjahr() && (wa_bsid.getHkont().startsWith("1010")||wa_bsid.getHkont().startsWith("1030")))
		  					{
		  						wa_out.setDmbtr(wa_out.getDmbtr()-wa_bsid.getDmbtr());
		  						wa_out.setWrbtr(wa_out.getWrbtr()-wa_bsid.getWrbtr());
		  						//System.out.println(index);
		  						if (l_bsid.size()-2>index) index = index + 1;
		  						else{
		  							loop = false;
		  						}
		  							
		  					}
		  					else
		  					{
		  						loop = false;
		  					}
		  				} 
		  			}
				}
				if (wa_out.getWrbtr()<0)
				{
					wa_out.setWrbtr(0);
				}
				if (wa_out.getDmbtr()<0)
				{
					wa_out.setDmbtr(0);
				}
				
			}
  			*/
  			// Deleting duplicates
  			Set<Long> hs_customer_id = new HashSet<Long>();
  			hs_customer_id.addAll(l_customer_id);
  			l_customer_id.clear();
  			l_customer_id.addAll(hs_customer_id);
  			for(Long wa_customer_id:l_customer_id)
  			{ 
  				Customer wa_customer = customerDao.find(wa_customer_id);
				if (wa_customer!=null)
				{
					for(OutputTable wa_out:l_outputTable)
		  			{
						if(wa_out.getCustomer_id().equals(wa_customer_id))
						{
							wa_out.setIin_bin(wa_customer.getIin_bin()); 
							if (wa_customer.getFiz_yur() == 1)
							{
								wa_out.setClientName(wa_customer.getName());
							}
			  		  		else
			  		  		{
			  		  			wa_out.setClientName(Validation.returnFioInitials(wa_customer.getFirstname(), wa_customer.getLastname(), wa_customer.getMiddlename()));
			  		  		} 
						}
		  			} 
						  					
	  					
				} 
  			}
  			
  			
			
  			tabindex = 1;
  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView");				
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  		 
  		
  	}
  	//*******************************************************************************
	//******************************************************************
  	private int r_blart_types = 1;  	
	public int getR_blart_types() {
		return r_blart_types;
	}
	public void setR_blart_types(int r_blart_types) {
		this.r_blart_types = r_blart_types;
	}
	
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
	
	private List<Bseg> l_bseg = new ArrayList<Bseg>();
	public List<Bseg> getL_bseg() {
		return l_bseg;
	}
	public void setL_bseg(List<Bseg> l_bseg) {
		this.l_bseg = l_bseg;
	}
	
	//private List<bsid> l_bsid2 = new ArrayList<bsid>();

	
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
		private Customer debitor = new Customer();
		private String bukrs = "";
		private String debitorName;
		private Date budat_from;
		private Date budat_to;
		private Date bldat_from;
		private Date bldat_to;	
		
		public Date getBudat_from() {
			return budat_from;
		}
		public void setBudat_from(Date budat_from) {
			this.budat_from = budat_from;
		}
		public Date getBudat_to() {
			return budat_to;
		}
		public void setBudat_to(Date budat_to) {
			this.budat_to = budat_to;
		}
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
		public Customer getDebitor() {
			return debitor;
		}
		public void setDebitor(Customer debitor) {
			this.debitor = debitor;
		}
		public String getDebitorName() {
			return debitorName;
		}
		public void setDebitorName(String debitorName) {
			this.debitorName = debitorName;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
	}	
	
	
	
	
	//*****************************************************************************************************
		
}
