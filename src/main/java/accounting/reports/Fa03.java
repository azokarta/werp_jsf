package accounting.reports;

import f4.BukrsF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.BsegDao; 
import general.dao.CustomerDao;
import general.dao.DAOException; 
import general.tables.Bukrs; 
import general.tables.Bkpf;
import general.tables.Bseg;
import general.tables.Customer;

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

@ManagedBean(name = "fa03Bean", eager = true)
@ViewScoped
public class Fa03 implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FA03";
	private final static Long transaction_id = (long) 3;
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
	private String bkpf_customer_name = "";
	public String getBkpf_customer_name() {
		return bkpf_customer_name;
	}
	public void setBkpf_customer_name(String bkpf_customer_name) {
		this.bkpf_customer_name = bkpf_customer_name;
	}
	
	Bkpf p_bkpf_storno = new Bkpf();
	public Bkpf getP_bkpf_storno() {
		return p_bkpf_storno;
	}
	public void setP_bkpf_storno(Bkpf p_bkpf_storno) {
		this.p_bkpf_storno = p_bkpf_storno;
	}
	
	Bkpf p_bkpf_original = new Bkpf();
	public Bkpf getP_bkpf_original() {
		return p_bkpf_original;
	}
	public void setP_bkpf_original(Bkpf p_bkpf_original) {
		this.p_bkpf_original = p_bkpf_original;
	}
	
	Bkpf p_bkpf = new Bkpf();
	public Bkpf getP_bkpf() {
		return p_bkpf;
	}
	public void setP_bkpf(Bkpf p_bkpf) {
		this.p_bkpf = p_bkpf;
	}
	
	List<Bkpf> l_bkpf = new ArrayList<Bkpf>();	
	public List<Bkpf> getL_bkpf() {
		return l_bkpf;
	}
	public void setL_bkpf(List<Bkpf> l_bkpf) {
		this.l_bkpf = l_bkpf;
	}
	
	List<Bseg> l_bseg = new ArrayList<Bseg>();
	public List<Bseg> getL_bseg() {
		return l_bseg;
	}
	public void setL_bseg(List<Bseg> l_bseg) {
		this.l_bseg = l_bseg;
	}
	
	Long belnr;	
	public Long getBelnr() {
		return belnr;
	}
	public void setBelnr(Long belnr) {
		this.belnr = belnr;
	}
	
	int gjahr;
	public int getGjahr() {
		return gjahr;
	}
	public void setGjahr(int gjahr) {
		this.gjahr = gjahr;
	}

	String bukrs;
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	
	Long belnr_search;
	public Long getBelnr_search() {
		return belnr_search;
	}
	public void setBelnr_search(Long belnr_search) {
		this.belnr_search = belnr_search;
	}

	int gjahr_search;
	public int getGjahr_search() {
		return gjahr_search;
	}
	public void setGjahr_search(int gjahr_search) {
		this.gjahr_search = gjahr_search;
	}

	String bukrs_search;
	public String getBukrs_search() {
		return bukrs_search;
	}
	public void setBukrs_search(String bukrs_search) {
		this.bukrs_search = bukrs_search;
	}
	
	
	// ******************************************************************
	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;
	public BukrsF4 getP_bukrsF4Bean() {
		return p_bukrsF4Bean;
	}
	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
		this.p_bukrsF4Bean = p_bukrsF4Bean;
	}

	List<Bukrs> bukrs_list = new ArrayList<Bukrs>();
	public List<Bukrs> getBukrs_list() {
		return bukrs_list;
	}
	// ******************************************************************	
	//***************************User session***************************
	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
	//******************************************************************	
	@PostConstruct
	public void init() {
		
		try {				
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			Calendar curDate = Calendar.getInstance();
			PermissionController.canRead(userData,Fa03.transaction_id);
			if (bukrs_list.size()==0){
				for (Bukrs wa_bukrs: p_bukrsF4Bean.getBukrs_list()){
					
						bukrs_list.add(wa_bukrs);
					
				}
			} 
			
			if (belnr!=null && gjahr>0 && bukrs !=null){
				belnr_search = belnr;
				bukrs_search = bukrs;
				gjahr_search = gjahr;
				to_search();
				belnr = null;
				gjahr = 0;
				bukrs = null;
			}  
			else{
				gjahr_search = curDate.get(Calendar.YEAR); 
			}
				
			if (p_bkpf.getStorno()==1 || (p_bkpf.getBlart()!=null && p_bkpf.getBlart().equals("ST")))
			{
				bgcolor = "#F6D8CE";
			}
		}
		catch (DAOException ex)
		{
			belnr = null;
			gjahr = 0;
			bukrs = null;
			addMessage("Info",ex.getMessage()); 
			toMainPage();
		}
	}
	
	
	public void to_search(){
		try {

			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao"); 
			BsegDao bsegDao = (BsegDao) appContext.getContext().getBean("bsegDao");
			CustomerDao customerDao = (CustomerDao) appContext.getContext().getBean("customerDao");
			String dynamicWhereClauseForBkpf = "";
			String dynamicWhereClauseForBseg = "";
			
			
			dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf+" bukrs = '"+bukrs_search+"'";
			dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf+" and belnr = "+belnr_search;
			dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf+" and gjahr = "+gjahr_search;
			l_bkpf = bkpfDao.dynamicFindBkpf(dynamicWhereClauseForBkpf);
			
			if (l_bkpf.size()==1)
			{
				p_bkpf = l_bkpf.get(0);
			}
			else
			{
				p_bkpf = new Bkpf();
				l_bkpf.clear();
				l_bseg.clear();
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
				throw new DAOException("Документ не найден.");
				
				
			}
			if (p_bkpf.getCustomer_id()!=null)
			{
				if (p_bkpf.getCustomer_id()!=null)
				{
					Customer wa_customer = customerDao.find(p_bkpf.getCustomer_id());
					if (wa_customer != null && wa_customer.getId() != null) {
						if (wa_customer.getFiz_yur()==1)
							bkpf_customer_name = wa_customer.getName();
						else
							bkpf_customer_name = Validation.returnFioInitials(wa_customer.getFirstname(), wa_customer.getLastname(), wa_customer.getMiddlename());
						
						
					} else {
						bkpf_customer_name = null;
					}

				}

			}
			if (p_bkpf.getStorno()==1)
			{
				p_bkpf_storno = bkpfDao.findStornoSingleBkpf(p_bkpf.getBelnr(), p_bkpf.getGjahr(),p_bkpf.getBukrs());
			}
			if (p_bkpf.getBlart().equals("ST"))
			{
				p_bkpf_original = bkpfDao.findOriginalSingleBkpf(p_bkpf.getStblg(), p_bkpf.getStjah(),p_bkpf.getBukrs());
			}
			
			dynamicWhereClauseForBseg = dynamicWhereClauseForBseg+" bukrs = "+bukrs_search;
			dynamicWhereClauseForBseg = dynamicWhereClauseForBseg+" and belnr = "+belnr_search;
			dynamicWhereClauseForBseg = dynamicWhereClauseForBseg+" and gjahr = "+gjahr_search;
			l_bseg = bsegDao.dynamicFindBseg(dynamicWhereClauseForBseg);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
			  
		}
		catch (DAOException ex)
		{  
			addMessage("Инфо",ex.getMessage()); 
		}
	}
	
	
 
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");
    } 
	
	public void toMainPage() {
		try {
			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath() + "/general/mainPage.xhtml");
		}
		catch (Exception ex)
		{
			 
			addMessage("Info",ex.getMessage());  
		}
	}
	public void to_fa02() {
		try {
			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath() + "/accounting/assess/fa02.xhtml?"+"belnr="+p_bkpf.getBelnr()+"&gjahr="+p_bkpf.getGjahr()+"&bukrs="+p_bkpf.getBukrs());
		}
		catch (Exception ex)
		{
			 
			addMessage("Info",ex.getMessage());  
		}
	}
	
	//**************************Search related documents**************************************************
	private List<Bkpf> related_docs_bkpf = new ArrayList<Bkpf>();
	public List<Bkpf> getRelated_docs_bkpf() {
		return related_docs_bkpf;
	}
	public void setRelated_docs_bkpf(List<Bkpf> related_docs_bkpf) {
		this.related_docs_bkpf = related_docs_bkpf;
	}
	public void search_related_docs() 
	{
		try{
			
			if (p_bkpf !=null && p_bkpf.getBelnr()!=null)
			{
				related_docs_bkpf.clear();
				Long wa_awkey = Helper.returnAwkey(p_bkpf.getBelnr(), p_bkpf.getGjahr());
				BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
				related_docs_bkpf = bkpfDao.dynamicFindBkpf("blart <> 'ST' and awkey = "+wa_awkey);
				if (p_bkpf.getAwkey()!=null)
				{
					related_docs_bkpf.addAll(bkpfDao.dynamicFindBkpf("blart <> 'ST' and (awkey = "+p_bkpf.getAwkey() + 
							" and NOT(belnr = "+ p_bkpf.getBelnr() + 
							" and gjahr = "+ p_bkpf.getGjahr() +"))"));
					related_docs_bkpf.addAll(bkpfDao.dynamicFindBkpf("blart <> 'ST' and belnr = " +String.valueOf(p_bkpf.getAwkey()).substring(0,10) + " and gjahr = " 
							+String.valueOf(p_bkpf.getAwkey()).substring(10,14)));
				}
				if (p_bkpf.getAwkey2()!=null)
				{
					related_docs_bkpf.addAll(bkpfDao.dynamicFindBkpf("blart <> 'ST' and (awkey = "+p_bkpf.getAwkey2() + 
							" and NOT(belnr = "+ p_bkpf.getBelnr() + 
							" and gjahr = "+ p_bkpf.getGjahr() +"))"));
					related_docs_bkpf.addAll(bkpfDao.dynamicFindBkpf("blart <> 'ST' and belnr = " +String.valueOf(p_bkpf.getAwkey2()).substring(0,10) + " and gjahr = " 
							+String.valueOf(p_bkpf.getAwkey2()).substring(10,14)));
				}
				
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:relatedDocs");	
		}
		catch (DAOException ex)
		{			 
			addMessage("Info",ex.getMessage());  
		}
	}
	private List<Bkpf> related_docs_contract_bkpf = new ArrayList<Bkpf>();
	public List<Bkpf> getRelated_docs_contract_bkpf() {
		return related_docs_contract_bkpf;
	}

	public void setRelated_docs_contract_bkpf(
			List<Bkpf> related_docs_contract_bkpf) {
		this.related_docs_contract_bkpf = related_docs_contract_bkpf;
	}
	public void search_contract_docs() 
	{
		try{
			if (p_bkpf !=null && p_bkpf.getBelnr()!=null)
			{
				related_docs_contract_bkpf.clear();
				BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
				if (p_bkpf.getContract_number()!=null)
				{
					related_docs_contract_bkpf.addAll(bkpfDao.dynamicFindBkpf("contract_number="+p_bkpf.getContract_number()));
				}
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:relatedConDocs");
				
			}
				
			
		}
		catch (DAOException ex)
		{			 
			addMessage("Info",ex.getMessage());  
		}
	}
	//*****************************************************************************************************	
	private String bgcolor = "";
	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
}