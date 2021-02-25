package accounting.reports;



import f4.BranchF4;
import f4.BukrsF4;
import f4.HkontF4;
import general.AppContext; 
import general.PermissionController;
import general.dao.DAOException;
import general.dao.FmglflextDao;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Fmglflext;
import general.tables.Hkont;
 






















import java.io.Serializable;
import java.util.ArrayList;
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

@ManagedBean(name = "rfbalBean", eager = true)
@ViewScoped
public class Rfbal implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "RFBAL";
	private final static Long transaction_id = (long) 58;
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
			PermissionController.canRead(userData,Rfbal.transaction_id);
			
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	

	//*************************************************************************
	
	
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
  			if(p_searchTable.month_from>p_searchTable.month_to)
  			{
  				throw new DAOException("Choose months correctly");
  			}
  			
  			l_outputTable.clear(); 
  			String dynamicWhereClause = "";
  			dynamicWhereClause = dynamicWhereClause + " bukrs = "+ p_searchTable.getBukrs();
  			dynamicWhereClause = dynamicWhereClause + " and gjahr = "+ p_searchTable.getGjahr();
  			dynamicWhereClause = dynamicWhereClause + " order by hkont desc";
  			FmglflextDao fmglflextDao = (FmglflextDao) appContext.getContext().getBean("fmglflextDao");
  			l_fmglflext = fmglflextDao.dynamicSearch(dynamicWhereClause);
  			
  			int count = 0;
  			String last_grp = "start";
  			String current_grp = "start";
  			for(Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
  			{  		
  				/*
  				current_grp = wa_hkont.getHkont().substring(0, 4);
  				if ((!last_grp.equals(current_grp)) && last_grp.equals("1010"))
  				{
  					OutputTable wa_out = new OutputTable();
  	  				wa_out.setIndex(count);
  	  				wa_out.setHkont("1010");
  	  				wa_out.setHkont_name("Касса");
  	  				wa_out.setHkont_grp(current_grp);  				
  	  				l_outputTable.add(wa_out);
  	  				count = count + 1;
  				}*/
  				if (p_searchTable.getBukrs().equals(wa_hkont.getBukrs()))
  				{
  					OutputTable wa_out = new OutputTable();
  	  				wa_out.setIndex(count);
  	  				wa_out.setHkont(wa_hkont.getHkont());
  	  				wa_out.setHkont_name(wa_hkont.getText45());
  	  				wa_out.setHkont_grp(current_grp);  		
  	  				if (wa_hkont.getWaers()==null)
  	  				{
  	  					wa_out.setWaers("USD");
  	  				}
  					else
  					{
  						wa_out.setWaers(wa_hkont.getWaers());
  					}
  	  				l_outputTable.add(wa_out);
  	  				
  	  				
  	  				
  	  				count = count + 1;
  	  				last_grp = current_grp;
  				}
  				
  			}
  			
  			for(Fmglflext wa_fmgl: l_fmglflext)
  			{
  				for(OutputTable wa_out:l_outputTable)
  	  			{
  	  				if (wa_out.getHkont().equals(wa_fmgl.getHkont()))
  	  				{
  	  					double beg_amount = 0;
  	  					double oborot = 0;
  	  					beg_amount  = beg_amount + wa_fmgl.getBeg_amount();
  	  					for(int i = 1; i<=p_searchTable.month_to;i++)
  	  					{ 
	  	  					switch (i) {
	  	  					case 1:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth1(); else oborot = oborot + wa_fmgl.getMonth1();
	  	  	                     break;
	  	  					case 2:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth2(); else oborot = oborot + wa_fmgl.getMonth2();
	  	  	                     break;
	  	  					case 3:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth3(); else oborot = oborot + wa_fmgl.getMonth3();
	  	  	                     break;
	  	  					case 4:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth4(); else oborot = oborot + wa_fmgl.getMonth4();
	  	  	                     break;
	  	  					case 5:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth5(); else oborot = oborot + wa_fmgl.getMonth5();
	  	  	                     break;
	  	  					case 6:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth6(); else oborot = oborot + wa_fmgl.getMonth6();
	  	  	                     break;
	  	  					case 7:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth7(); else oborot = oborot + wa_fmgl.getMonth7();
	  	  	                     break;
	  	  					case 8:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth8(); else oborot = oborot + wa_fmgl.getMonth8();
	  	  	                     break;
	  	  					case 9:  if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth9(); else oborot = oborot + wa_fmgl.getMonth9();
	  	  	                     break;
	  	  					case 10: if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth10(); else oborot = oborot + wa_fmgl.getMonth10();
	  	  	                     break;
	  	  					case 11: if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth11(); else oborot = oborot + wa_fmgl.getMonth11();
	  	  	                     break;
	  	  					case 12: if (i<p_searchTable.month_from) beg_amount  = beg_amount + wa_fmgl.getMonth12(); else oborot = oborot + wa_fmgl.getMonth12();
	  	  	                     break;
	  	  						
	  	  					}
  	  					}
  	  					
  	  					if (wa_fmgl.getDrcrk().equals("S"))
	  					{
  	  						wa_out.setBeg_debet(beg_amount);
	  						wa_out.setDebet(oborot);	  						
	  					}
	  					else if (wa_fmgl.getDrcrk().equals("H"))
	  					{
	  						wa_out.setBeg_kredit(beg_amount);
	  						wa_out.setKredit(oborot);
	  					}
  	  					
  	  					
  	  					
  	  					
  	  					
  	  					
  	  				}
  	  			}
  			}
  			
  			for(OutputTable wa_out:l_outputTable)
	  		{
  				if (wa_out.getBeg_debet()+wa_out.getDebet()-wa_out.getKredit()-wa_out.getBeg_kredit()<0)
  				{
  					wa_out.setEnd_kredit(wa_out.getKredit()+wa_out.getBeg_kredit()-wa_out.getBeg_debet()-wa_out.getDebet());
  				}
  				else if (wa_out.getBeg_debet()+wa_out.getDebet()-wa_out.getKredit()-wa_out.getBeg_kredit()>0)
  				{
  					wa_out.setEnd_debet(wa_out.getBeg_debet()+wa_out.getDebet()-wa_out.getKredit()-wa_out.getBeg_kredit());
  				}
  				
  				if (wa_out.getBeg_debet()-wa_out.getBeg_kredit()<0)
  				{
  					wa_out.setBeg_kredit(wa_out.getBeg_kredit() - wa_out.getBeg_debet());
  					wa_out.setBeg_debet(0);
  				}
  				else if (wa_out.getBeg_debet()-wa_out.getBeg_kredit()>0)
  				{
  					wa_out.setBeg_debet(wa_out.getBeg_debet()-wa_out.getBeg_kredit());
  					wa_out.setBeg_kredit(0);
  				}
	  		}
  			
  			//System.out.println(l_fmglflext.size());	
  			
  			// Deleting duplicates
  			//Set<Long> hs_customer_id = new HashSet<Long>();
  			
			
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
  	private List<Fmglflext> l_fmglflext = new ArrayList<Fmglflext>();
	public List<Fmglflext> getL_fmglflext() {
		return l_fmglflext;
	}
	public void setL_fmglflext(List<Fmglflext> l_fmglflext) {
		this.l_fmglflext = l_fmglflext;
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
		private String hkont;
		private String hkont_name;
		private double debet;
		private double kredit;
		private double beg_debet;
		private double beg_kredit;
		private double end_debet;
		private double end_kredit;
		private String waers;
		public double getBeg_debet() {
			return beg_debet;
		}
		public void setBeg_debet(double beg_debet) {
			this.beg_debet = beg_debet;
		}
		public double getBeg_kredit() {
			return beg_kredit;
		}
		public void setBeg_kredit(double beg_kredit) {
			this.beg_kredit = beg_kredit;
		}
		public double getEnd_debet() {
			return end_debet;
		}
		public void setEnd_debet(double end_debet) {
			this.end_debet = end_debet;
		}
		public double getEnd_kredit() {
			return end_kredit;
		}
		public void setEnd_kredit(double end_kredit) {
			this.end_kredit = end_kredit;
		}
		private String hkont_grp;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getHkont() {
			return hkont;
		}
		public void setHkont(String hkont) {
			this.hkont = hkont;
		}
		public String getHkont_name() {
			return hkont_name;
		}
		public void setHkont_name(String hkont_name) {
			this.hkont_name = hkont_name;
		}
		public double getDebet() {
			return debet;
		}
		public void setDebet(double debet) {
			this.debet = debet;
		}
		public double getKredit() {
			return kredit;
		}
		public void setKredit(double kredit) {
			this.kredit = kredit;
		}
		public String getHkont_grp() {
			return hkont_grp;
		}
		public void setHkont_grp(String hkont_grp) {
			this.hkont_grp = hkont_grp;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
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
		private String bukrs;
		private int month_from;
		private int month_to;
		private int gjahr;
		
		public int getMonth_from() {
			return month_from;
		}
		public void setMonth_from(int month_from) {
			this.month_from = month_from;
		}
		public int getMonth_to() {
			return month_to;
		}
		public void setMonth_to(int month_to) {
			this.month_to = month_to;
		}
		public int getGjahr() {
			return gjahr;
		}
		public void setGjahr(int gjahr) {
			this.gjahr = gjahr;
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
