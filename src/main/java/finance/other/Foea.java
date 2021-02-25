package finance.other;


import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext; 
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.dao.BranchDao;
import general.dao.DAOException; 
import general.dao.RfcolDao;
import general.services.PrebkpfService;
import general.tables.Branch;

import java.io.IOException;
import java.io.Serializable;

 




import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

@ManagedBean(name = "foeaBean", eager = true)
@ViewScoped
public class Foea implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FOEA";
	private final static Long transaction_id = (long) 517;
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
	 //*****************************Branch**********************************
  	@ManagedProperty(value="#{branchF4Bean}")
  	private BranchF4 p_branchF4Bean;
  	public void setP_branchF4Bean(BranchF4 p_branchF4) {
  	      this.p_branchF4Bean = p_branchF4;
  	}
  	public BranchF4 getP_branchF4Bean() {
		  return p_branchF4Bean;
	}
	 //*****************************Branch**********************************
  	@ManagedProperty(value="#{exchangeRateF4Bean}")
  	private ExchangeRateF4 p_exchangeRateF4Bean;
  	
	public ExchangeRateF4 getP_exchangeRateF4Bean() {
		return p_exchangeRateF4Bean;
	}
	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
		this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
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
			PermissionController.canRead(userData,Foea.transaction_id);
			Calendar curDate = Calendar.getInstance();
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 1);
			lastDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			p_searchTable.setBldat_from(firstDay.getTime());
			p_searchTable.setBldat_to(lastDay.getTime());
			p_searchTable.getL_type().add("0");
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	

	//*************************************************************************
	
	
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
  			l_selected_outputTable.clear();
  			l_outputTable.clear();
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			
  			
  			
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and pre.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
  			
  			if (p_searchTable.getSelectedBranches().size()!=0)
  			{
  				int count=0;
  				String br_list = "(";
  				for(String a_br_id:p_searchTable.getSelectedBranches())
  				{
  					count++;
  					if (count==1) br_list = br_list+a_br_id;
  					else br_list = br_list+", "+a_br_id;	
  				}
  				br_list = br_list+")";
  				dynamicWhereClause = dynamicWhereClause + " and pre.brnch in "+br_list;
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
  			
  			if (p_searchTable.bldat_from==null)
	  		{
	  			throw new DAOException(Helper.getErrorMessage(13L, userData.getU_language()));
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and pre.bldat between '"+GeneralUtil.getSQLDate(p_searchTable.bldat_from)+"' ";
	  			
	  		
	  		if (p_searchTable.bldat_to==null)
	  		{
	  			throw new DAOException(Helper.getErrorMessage(14L, userData.getU_language()));
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and '"+GeneralUtil.getSQLDate(p_searchTable.bldat_to)+"' ";
	  		
	  		
	  		if (p_searchTable.getL_type().size()!=0)
  			{
  				int count=0;
  				String l_type = "(";
  				for(String wa:p_searchTable.getL_type())
  				{
  					count++;
  					if (count==1) l_type = l_type+wa;
  					else l_type = l_type+", "+wa;	
  				}
  				l_type = l_type+")";
  				dynamicWhereClause = dynamicWhereClause + " and pre.status in "+l_type;
  			}
	  		
	  		if (p_searchTable.getOper_type().size()!=0 && p_searchTable.getOper_type().size()<6)
  			{
  				int count=0;
  				String l_oper_type = "(";
  				for(String wa:p_searchTable.getOper_type())
  				{
  					count++;
  					if (count>1) l_oper_type = l_oper_type+" or ";
  					
  					if (wa.equals("0"))
  					{
  						l_oper_type = l_oper_type + " (pre.blart = 'AE' and pre.customer_id is not null) ";
  					}
  					else if (wa.equals("1"))
  					{
  						l_oper_type = l_oper_type + " (pre.blart in 'KP' and pre.customer_id is not null) ";
  					}
  					else if (wa.equals("2"))
  					{
  						l_oper_type = l_oper_type + " (pre.blart = 'OR' and pre.customer_id is null) ";
  					}
  					else if (wa.equals("3"))
  					{
  						l_oper_type = l_oper_type + " (pre.blart = 'DP' and pre.customer_id is not null) ";
  					}
  					else if (wa.equals("4"))
  					{
  						l_oper_type = l_oper_type + " (pre.blart = 'G2') ";
  					}
  				}
  				l_oper_type = l_oper_type+")";
  				dynamicWhereClause = dynamicWhereClause + " and "+l_oper_type;
  			}
	  		
//			<f:selectItem itemValue="0" itemLabel="Расходы без СФ"/>
//            <f:selectItem itemValue="1" itemLabel="Расходы со СФ" />
//            <f:selectItem itemValue="2" itemLabel="Доходы без СФ" />
//            <f:selectItem itemValue="3" itemLabel="Доходы со СФ" />
//            <f:selectItem itemValue="4" itemLabel="Продажа запчастей" />
//            <f:selectItem itemValue="5" itemLabel="Счет фактура от поставщика" />
	  		
  			RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = rfcolDao.dynamicFoeaResult(dynamicWhereClause, userData.getU_language());
  			for (Object[] wa_result:results)
  			{
//				br.text45 branchname,pre.blart,pre.bldat,pre.waers,pre.dmbtr"
//			    +" ,d.text45 hkont_d,k.text45||' '||k.waers hkont_k,u.username,pre.status,pre.bktxt,pre.awkey,pre.customer_id
  				
  				OutputTable wa_out = new OutputTable();
  				if (wa_result[0]!=null) wa_out.setBranchName(String.valueOf(wa_result[0]));
  				if (wa_result[1]!=null) wa_out.setBlart(String.valueOf(wa_result[1]));
  				if (wa_result[2]!=null) wa_out.setBldat(String.valueOf(wa_result[2]));
  				
  				if (wa_result[3]!=null) wa_out.setWaers(String.valueOf(wa_result[3]));
  				if (wa_result[4]!=null) wa_out.setDmbtr(Double.parseDouble(String.valueOf(wa_result[4])));
  				if (wa_result[5]!=null) wa_out.setHkont_d(String.valueOf(wa_result[5]));
  				if (wa_result[6]!=null) wa_out.setHkont_k(String.valueOf(wa_result[6]));
  				if (wa_result[7]!=null) wa_out.setUname(String.valueOf(wa_result[7]));
  				
  				wa_out.setStatus(Integer.parseInt(String.valueOf(wa_result[8])));
  				wa_out.setStatus_name(getStatus(userData.getU_language(),Integer.parseInt(String.valueOf(wa_result[8]))));
  				if (wa_result[9]!=null) wa_out.setBktxt(String.valueOf(wa_result[9]));
  				if (wa_result[10]!=null) wa_out.setAwkey(Long.parseLong(String.valueOf(wa_result[10])));
  				if (wa_result[12]!=null) wa_out.setPrebkpf_id(Long.parseLong(String.valueOf(wa_result[12])));
  				if (wa_result[13]!=null) wa_out.setBelnr(Long.parseLong(String.valueOf(wa_result[13])));
  				if (wa_result[14]!=null) wa_out.setGjahr(Integer.parseInt(String.valueOf(wa_result[14])));

  				if (wa_result[15]!=null) wa_out.setCreated_date(String.valueOf(wa_result[15]));
  				if (wa_result[16]!=null) wa_out.setCname(String.valueOf(wa_result[16]));
  				if (wa_result[17]!=null) wa_out.setCustomer_id(Long.parseLong(String.valueOf(wa_result[17])));


					
  				if (wa_out.getBlart().equals("AE") && wa_out.getCustomer_id()!=null)
  				{
  					wa_out.setOper_type_name(getOperType("0",userData.getU_language()));  				
  				}				
				else if (wa_out.getBlart().equals("KP") && wa_out.getCustomer_id()!=null)
				{
					wa_out.setOper_type_name(getOperType("1",userData.getU_language()));
				}
				else if (wa_out.getBlart().equals("OR") && wa_out.getCustomer_id()!=null)
				{
					wa_out.setOper_type_name(getOperType("2",userData.getU_language()));
				}
				else if (wa_out.getBlart().equals("DP") && wa_out.getCustomer_id()!=null)
				{
					wa_out.setOper_type_name(getOperType("3",userData.getU_language()));
				}
				else if (wa_out.getBlart().equals("G2"))
				{
					wa_out.setOper_type_name(getOperType("4",userData.getU_language()));
				}	
  				//a2.waers,a2.brnch,a2.hkont,a2.dmbtr,a2.wrbtr,a2.brname,s.text45 as hname
  				
//  				if (wa_result[1]!=null) wa_out.setBranch_id(Long.parseLong(String.valueOf(wa_result[1])));
//  				if (wa_result[2]!=null) wa_out.setWaers(String.valueOf(wa_result[2]));
//  				
//  				if (wa_result[3]!=null) wa_out.setNal_kol(Integer.parseInt(String.valueOf(wa_result[3])));
//  				if (wa_result[4]!=null) wa_out.setNal_dmbtr(Double.parseDouble(String.valueOf(wa_result[4])));
//  				if (wa_result[5]!=null) wa_out.setNal_wrbtr(Double.parseDouble(String.valueOf(wa_result[5])));
//  				
//  				if (wa_result[6]!=null) wa_out.setRas_kol(Integer.parseInt(String.valueOf(wa_result[6])));
//  				if (wa_result[7]!=null) wa_out.setRas_dmbtr(Double.parseDouble(String.valueOf(wa_result[7])));
//  				if (wa_result[8]!=null) wa_out.setRas_wrbtr(Double.parseDouble(String.valueOf(wa_result[8])));
//  				
//  				if (wa_result[9]!=null) wa_out.setTot_kol(Integer.parseInt(String.valueOf(wa_result[9])));
//  				if (wa_result[10]!=null) wa_out.setTot_dmbtr(Double.parseDouble(String.valueOf(wa_result[10])));
//  				if (wa_result[11]!=null) wa_out.setTot_wrbtr(Double.parseDouble(String.valueOf(wa_result[11])));
  				

  				
  				
  				l_outputTable.add(wa_out);
  				
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
  	
  	public void to_save()
  	{
  		try 
		{
  			PermissionController.canWrite(userData,Foea.transaction_id);
  			PrebkpfService prebkpfService = (PrebkpfService) appContext.getContext().getBean("prebkpfService");
  			String  sl_prebkpf_id="";
  			int count = 0;
  			int size = l_selected_outputTable.size();
  			if (size>0)
  			{
  				for(OutputTable wa:l_selected_outputTable)
  	  			{
  	  				count++;
  	  				if (count>1)
  	  				{
  	  					sl_prebkpf_id = sl_prebkpf_id +", "+ wa.getPrebkpf_id();
  	  				} else sl_prebkpf_id = sl_prebkpf_id + wa.getPrebkpf_id();				
  	  			}
  	  			sl_prebkpf_id = "(" +sl_prebkpf_id + ")";	
  	  			prebkpfService.saveFinance(p_searchTable.getBukrs(),sl_prebkpf_id,userData.getUserid());
  	  			
	  	  		addMessage("Info", Helper.getErrorMessage(101L, userData.getU_language()));
	  	  		
	  	  		
	  	  	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	try {
				context.redirect(context.getRequestContextPath()  + "/finance/other/foea.xhtml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	 	
//	  			to_search();
  			}
  			
  						
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  		 
  		
  	}
  	public void to_cancel()
  	{
  		try 
		{
  			PermissionController.canWrite(userData,Foea.transaction_id);
  			PrebkpfService prebkpfService = (PrebkpfService) appContext.getContext().getBean("prebkpfService");
  			String  sl_prebkpf_id="";
  			int count = 0;
  			int size = l_selected_outputTable.size();
  			if (size>0)
  			{
  				for(OutputTable wa:l_selected_outputTable)
  	  			{
  	  				count++;
  	  				if (count>1)
  	  				{
  	  					sl_prebkpf_id = sl_prebkpf_id +", "+ wa.getPrebkpf_id();
  	  				} else sl_prebkpf_id = sl_prebkpf_id + wa.getPrebkpf_id();				
  	  			}
  	  			sl_prebkpf_id = "(" +sl_prebkpf_id + ")";	
  	  			prebkpfService.cancelStatus(sl_prebkpf_id,userData.getUserid());
  	  			
	  	  		addMessage("Info", Helper.getErrorMessage(101L, userData.getU_language()));
	  			to_search();
  			}	
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  		 
  		
  	}
  	//*******************************************************************************
	//******************************************************************
	

	
	//private List<bsid> l_bsid2 = new ArrayList<bsid>();

  	private List<OutputTable> l_selected_outputTable = new ArrayList<OutputTable>();
  	public List<OutputTable> getL_selected_outputTable() {
		return l_selected_outputTable;
	}
	public void setL_selected_outputTable(List<OutputTable> l_selected_outputTable) {
		this.l_selected_outputTable = l_selected_outputTable;
	}



	
	private List<OutputTable> l_outputTable = new ArrayList<OutputTable>();
	public List<OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
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
		private String bukrs = "";
		private List<Branch> l_branch = new ArrayList<Branch>();
		private List<String> selectedBranches = new ArrayList<String>();
		
		private List<String> l_type = new ArrayList<String>();
		private Long branch_id = 0L;
		private Date bldat_from;
		private Date bldat_to;
		
		private List<String> oper_type = new ArrayList<String>();
		
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
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
		public List<String> getL_type() {
			return l_type;
		}
		public void setL_type(List<String> l_type) {
			this.l_type = l_type;
		}
		public List<String> getOper_type() {
			return oper_type;
		}
		public void setOper_type(List<String> oper_type) {
			this.oper_type = oper_type;
		}
		
	}
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private Long prebkpf_id;
		private String bukrs = "";
		private String branchName;
		private String blart;
		private String waers;
		private String bldat;
		private String uname;
		private String cname;
		private double dmbtr;
		private String status_name;
		private int status;
		private String hkont_d;
		private String hkont_k;
		private String bktxt;
		private Long awkey;
		private Long belnr;
		private int gjahr;
		private Long customer_id;
		private String oper_type_name;
		private String created_date;
		
		
		
		public String getCreated_date() {
			return created_date;
		}
		public void setCreated_date(String created_date) {
			this.created_date = created_date;
		}
		public String getOper_type_name() {
			return oper_type_name;
		}
		public void setOper_type_name(String oper_type_name) {
			this.oper_type_name = oper_type_name;
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
		public int getGjahr() {
			return gjahr;
		}
		public void setGjahr(int gjahr) {
			this.gjahr = gjahr;
		}
		public Long getPrebkpf_id() {
			return prebkpf_id;
		}
		public void setPrebkpf_id(Long prebkpf_id) {
			this.prebkpf_id = prebkpf_id;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		public String getBlart() {
			return blart;
		}
		public void setBlart(String blart) {
			this.blart = blart;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public String getBldat() {
			return bldat;
		}
		public void setBldat(String bldat) {
			this.bldat = bldat;
		}
		public String getUname() {
			return uname;
		}
		public void setUname(String uname) {
			this.uname = uname;
		}
		public String getCname() {
			return cname;
		}
		public void setCname(String cname) {
			this.cname = cname;
		}
		public double getDmbtr() {
			return dmbtr;
		}
		public void setDmbtr(double dmbtr) {
			this.dmbtr = dmbtr;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getHkont_d() {
			return hkont_d;
		}
		public void setHkont_d(String hkont_d) {
			this.hkont_d = hkont_d;
		}
		public String getHkont_k() {
			return hkont_k;
		}
		public void setHkont_k(String hkont_k) {
			this.hkont_k = hkont_k;
		}
		public String getBktxt() {
			return bktxt;
		}
		public void setBktxt(String bktxt) {
			this.bktxt = bktxt;
		}
		public Long getAwkey() {
			return awkey;
		}
		public void setAwkey(Long awkey) {
			this.awkey = awkey;
		}
		public String getStatus_name() {
			return status_name;
		}
		public void setStatus_name(String status_name) {
			this.status_name = status_name;
		}
		
	
		
	}
	
	private List<DetailTable> l_detailTable = new ArrayList<DetailTable>();
	public List<DetailTable> getL_detailTable() {
		return l_detailTable;
	}
	public void setL_detailTable(List<DetailTable> l_detailTable) {
		this.l_detailTable = l_detailTable;
	}
	public class DetailTable {
		public DetailTable()
		{
			
		};
		private Long prebkpf_id;
		private String waers;
		private double dmbtr;
		private double wrbtr;		
		private String shkzg;
		private double menge;
		private String hkont;
		
		private String matnr;
		private String werks;
		public Long getPrebkpf_id() {
			return prebkpf_id;
		}
		public void setPrebkpf_id(Long prebkpf_id) {
			this.prebkpf_id = prebkpf_id;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
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
		public String getShkzg() {
			return shkzg;
		}
		public void setShkzg(String shkzg) {
			this.shkzg = shkzg;
		}
		public double getMenge() {
			return menge;
		}
		public void setMenge(double menge) {
			this.menge = menge;
		}
		public String getHkont() {
			return hkont;
		}
		public void setHkont(String hkont) {
			this.hkont = hkont;
		}
		public String getMatnr() {
			return matnr;
		}
		public void setMatnr(String matnr) {
			this.matnr = matnr;
		}
		public String getWerks() {
			return werks;
		}
		public void setWerks(String werks) {
			this.werks = werks;
		}
		
		
		
	}
	public void detailInfo(String a_prebkpf_id)
	{
		try{
			l_detailTable = new ArrayList<DetailTable>();
			
			if (a_prebkpf_id!=null)
			{
				RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
	  			List<Object[]> results = new ArrayList<Object[]>();
	  			results = rfcolDao.dynamicFoeaDetail(" pbf.prebkpf_id="+a_prebkpf_id);
	  			
//	  			a1.prebkpf_id,a1.waers,a1.dmbtr,a1.wrbtr,a1.shkzg,a1.menge,a1.hkont,a1.werks,m.text45 matnr 
	  			
	  			for (Object[] wa_result:results)
	  			{
	  				
	  				DetailTable wa_out = new DetailTable();
	  				if (wa_result[0]!=null) wa_out.setPrebkpf_id(Long.parseLong(String.valueOf(wa_result[0])));
	  				if (wa_result[1]!=null) wa_out.setWaers(String.valueOf(wa_result[1]));
	  				if (wa_result[2]!=null) wa_out.setDmbtr(Double.parseDouble(String.valueOf(wa_result[2])));
	  				if (wa_result[3]!=null) wa_out.setWrbtr(Double.parseDouble(String.valueOf(wa_result[3])));
	  				if (wa_result[4]!=null) wa_out.setShkzg(String.valueOf(wa_result[4]));
	  				if (wa_result[5]!=null) wa_out.setMenge(Double.parseDouble(String.valueOf(wa_result[5])));
	  				if (wa_result[6]!=null) wa_out.setHkont(String.valueOf(wa_result[6]));
	  				if (wa_result[7]!=null) wa_out.setWerks(String.valueOf(wa_result[7]));
	  				if (wa_result[8]!=null) wa_out.setMatnr(String.valueOf(wa_result[8]));
	  				l_detailTable.add(wa_out);
	  			}	
			}
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:detailTable");
//  			reqCtx.update("form:j_idt154");
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		} 
	}
	
	public void updateBranch()
	{
		try{
			Long ba = 0L;
			Long br = 0L;
			p_searchTable.getL_branch().clear();
			p_searchTable.getL_branch().addAll(getUserBranchList(p_searchTable.bukrs));
			
//			for(Branch wa:p_branchF4Bean.getByBukrsOrBusinessAreaOfficesOnly(p_searchTable.bukrs,userData.getBukrs(),ba,ba, br,userData.getBranch_id()))
//			{
//				p_searchTable.getL_branch().add(wa);
////				if (wa.getBusiness_area_id()!=null && wa.getBusiness_area_id().equals(1L) || wa.getBusiness_area_id().equals(2L) || wa.getBusiness_area_id().equals(3L) || wa.getBusiness_area_id().equals(4L))
////				{
////					p_searchTable.getL_branch().add(wa);
////				}
//			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView:p_branch_id");	
  			reqCtx.update("branchForm:branchFormTable");	
  			
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		} 
	}
	
	public List<Branch> getUserBranchList(String a_bukrs){
		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		if (userData.isSys_admin()) return brDao.findUserBranchesOfficesAdmin(a_bukrs, userData.getUserid());
		else return brDao.findUserBranchesOffices(a_bukrs, userData.getUserid());		
		 
	}

	public String getStatus(String a_lang,int a_status) {
		if (a_lang!=null)
		{
			if (a_lang.equals("ru"))
			{
				if (0==a_status) return "Cоздано";
				else if (2==a_status) return "Отказано";
				else if (1==a_status) return "Одобрено";
				else return "Cоздано";
			}
			else if (a_lang.equals("tr"))
			{
				if (0==a_status) return "Oluşturuldu";
				else if (2==a_status) return "Reddedildi";
				else if (1==a_status) return "Onayli";
				else return "Cоздано";
			}
			else if (a_lang.equals("en"))
			{
				if (0==a_status) return "Created";
				else if (2==a_status) return "Denied";
				else if (1==a_status) return "Approved";
				else return "Cоздано";
			}
		}
		return "";
	}
	public String getOperType(String a_oper_type,String a_lang) {
		if (a_lang!=null)
		{
			if (a_lang.equals("ru"))
			{
				if (a_oper_type.equals("0")) return "Расходы без СФ";
				else if (a_oper_type.equals("1")) return "Расходы со СФ";
				else if (a_oper_type.equals("2")) return "Приходы без СФ";
				else if (a_oper_type.equals("3")) return "Приходы со СФ";
				else if (a_oper_type.equals("4")) return "Продажа запчастей";
				else if (a_oper_type.equals("5")) return "Счет фактура";
				else if (a_oper_type.equals("6")) return "Налогообложение";
			}
			else if (a_lang.equals("tr"))
			{
				if (a_oper_type.equals("0")) return "Payments without Invoice";
				else if (a_oper_type.equals("1")) return "Payment Order";
				else if (a_oper_type.equals("2")) return "Fond Receipts Without Inv.";
				else if (a_oper_type.equals("3")) return "Cash Reciepts";
				else if (a_oper_type.equals("4")) return "Spare Parts Sales";
				else if (a_oper_type.equals("5")) return "Services (Recieved) Invoice";
				else if (a_oper_type.equals("6")) return "Tax Accruals";
			}
			else if (a_lang.equals("en"))
			{
				if (a_oper_type.equals("0")) return "Payments without Invoice";
				else if (a_oper_type.equals("1")) return "Payment Order";
				else if (a_oper_type.equals("2")) return "Fond Receipts Without Inv.";
				else if (a_oper_type.equals("3")) return "Cash Reciepts";
				else if (a_oper_type.equals("4")) return "Spare Parts Sales";
				else if (a_oper_type.equals("5")) return "Services (Recieved) Invoice";
				else if (a_oper_type.equals("6")) return "Tax Accruals";
			}
		}
		return "";
	}
	
		
	
	
		
		
		
		
	
	//*****************************************************************************************************
		
}

