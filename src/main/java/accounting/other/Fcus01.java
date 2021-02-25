package accounting.other;


import f4.BranchF4;
import f4.BukrsF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import f4.MatnrF4;
import general.AppContext; 
import general.GeneralUtil;
import general.PermissionController;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.output.tables.Fcus01ResultTable;
import general.services.ZreportService;
import general.tables.Branch;
import general.tables.Customer; 
import general.tables.Hkont;
 














import general.tables.Staff;
import general.tables.Zreport;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "fcus01Bean", eager = true)
@ViewScoped
public class Fcus01 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private final static String transaction_code = "FCUS01";
	private final static Long transaction_id = (long) 235;
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
  
  	
  	
	//***************************MatnrF4*****************************************************
  	@ManagedProperty(value="#{matnrF4Bean}")
  	private MatnrF4 p_matnrF4Bean;			
  	
  	public MatnrF4 getP_matnrF4Bean() {
  	  return p_matnrF4Bean;
  	}

  	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
  	  this.p_matnrF4Bean = p_matnrF4Bean;
  	} 
	
  	private int tabindex = 0;
  	public int getTabindex() {
		return tabindex;
	}
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}
  
  	//*****************************************************************************************************
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{  
			
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Fcus01.transaction_id);
			Calendar curDate = Calendar.getInstance();
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 1);
			lastDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			firstDay.set(2016, 0, 01);
			p_searchTable.setBldat_from(firstDay.getTime());
			p_searchTable.setBldat_to(lastDay.getTime());
			CustomerDao customerDao = (CustomerDao) appContext.getContext().getBean("customerDao");
			StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
			if(p_searchTable.customer_id!=null && p_searchTable.customer_id>0)
			{
				p_searchTable.setCustomer(customerDao.find(p_searchTable.customer_id));
				if (p_searchTable.getCustomer()==null)
				{
					p_searchTable.setCustomer(new Customer());
					p_searchTable.getCustomer().setFiz_yur(1);
					p_searchTable.getCustomer().setName("Контрагент не найден.");
					p_searchTable.setCustomer_id(null);
					p_searchTable.getCustomer().setIin_bin("");
				}
				else
				{//if customer found
					
					
					p_searchTable.setStaff(staffDao.findByCustomerId(p_searchTable.customer_id));
					if (p_searchTable.getStaff()!=null)
					{
						p_searchTable.setOur_staff(true);
					}
					
				}
//				/getFullFIO()
			}
			
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
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
	
	Map<String,List<Fcus01ResultTable>> l_client_map = new HashMap<String,List<Fcus01ResultTable>>();
//	Map<String,List<Fcus01ResultTable>> l_supplier_map = new HashMap<String,List<Fcus01ResultTable>>();
	Map<String,List<Fcus01ResultTable>> l_podotchet_map = new HashMap<String,List<Fcus01ResultTable>>();
	Map<String,List<Fcus01ResultTable>> l_salary_map = new HashMap<String,List<Fcus01ResultTable>>();
	
	public void to_search() throws DAOException
	{
		try
		{
			//Dauren abi
			if (userData.getUserid().equals(105L))
			{
				if (!p_searchTable.getCustomer_id().equals(139811L))
				{
					return;
				}
			}
				
			l_rt_client = new ArrayList<Fcus01ResultTable>();
//			l_rt_supplier = new ArrayList<Fcus01ResultTable>();
			l_rt_podotchet = new ArrayList<Fcus01ResultTable>();
			l_rt_salary = new ArrayList<Fcus01ResultTable>();
			l_client_map = new HashMap<String,List<Fcus01ResultTable>>();
			Map<String,List<Fcus01ResultTable>> l_supplier_map = new HashMap<String,List<Fcus01ResultTable>>();
			l_podotchet_map = new HashMap<String,List<Fcus01ResultTable>>();
			l_salary_map = new HashMap<String,List<Fcus01ResultTable>>();
			p_searchTable.clearAmounts();
			if(p_searchTable.getBukrs()==null)
			{
				throw new DAOException("Выберите компанию.");				
			}
			
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and b.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			}
  			else
  				throw new DAOException("Выберите компанию");
  			
  			
  			if (p_searchTable.getCustomer()!=null && p_searchTable.getCustomer().getId()!=null)
  			{
  				dynamicWhereClause = dynamicWhereClause + " and bs.lifnr = '"+p_searchTable.getCustomer().getId()+"' ";
  			}
  			else
  				throw new DAOException("Выберите контрагента");
  			
  			
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
  				dynamicWhereClause = dynamicWhereClause + " and b.brnch in "+br_list;
  			}
  			else
  				throw new DAOException("Выберите филиал");
  			
  			
  			Calendar cal_budat_from = Calendar.getInstance();
  			Calendar cal_budat_to = Calendar.getInstance();
  			if(p_searchTable.bldat_from!=null)
  			{
  				cal_budat_from.setTime(p_searchTable.bldat_from);
  			}
  			else
  			{
  				throw new DAOException("Выберите дату документа");
  			}
  			if(p_searchTable.bldat_to!=null)
  			{
  				cal_budat_to.setTime(p_searchTable.bldat_to);
  			}
  			if(p_searchTable.bldat_from!=null && p_searchTable.bldat_to!=null && (p_searchTable.bldat_from.before(p_searchTable.bldat_to) || p_searchTable.bldat_from.equals(p_searchTable.bldat_to)))
  			{  			
  				if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and "; 
  				dynamicWhereClause = dynamicWhereClause + "bldat between '"+ new java.sql.Date(cal_budat_from.getTimeInMillis()) +"' and '" +new java.sql.Date(cal_budat_to.getTimeInMillis())+"'";
  			}
  			else if (p_searchTable.bldat_from!=null)
  			{
  				if (dynamicWhereClause.length()>0) dynamicWhereClause = dynamicWhereClause + " and "; 
  				dynamicWhereClause = dynamicWhereClause + "bldat = '"+ new java.sql.Date(cal_budat_from.getTimeInMillis()) +"'";
  			}
  			
  			dynamicWhereClause = dynamicWhereClause + " and storno = 0 and b.blart<>'ST'";
  			//BsidDao bsidDao = (BsidDao) appContext.getContext().getBean("bsidDao");
  			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
  			
  			
  			
  			
  		
  			Map<String,String> l_currency_map = new HashMap<String,String>();
  			bkpfDao.findResultTableFcus01(l_client_map, l_supplier_map, l_podotchet_map, l_salary_map, l_currency_map, dynamicWhereClause,p_searchTable);
  			
			for (Map.Entry<String, List<Fcus01ResultTable>> entry : l_client_map.entrySet()) {
				List<Fcus01ResultTable> wal_rt = new ArrayList<Fcus01ResultTable>();
				wal_rt = (List<Fcus01ResultTable>) entry.getValue();
				l_rt_client.addAll(wal_rt);
			}
			
			for (Map.Entry<String, List<Fcus01ResultTable>> entry : l_podotchet_map.entrySet()) {
				List<Fcus01ResultTable> wal_rt = new ArrayList<Fcus01ResultTable>();
				wal_rt = (List<Fcus01ResultTable>) entry.getValue();
				l_rt_podotchet.addAll(wal_rt);
			}
			for (Map.Entry<String, List<Fcus01ResultTable>> entry : l_salary_map.entrySet()) {
				List<Fcus01ResultTable> wal_rt = new ArrayList<Fcus01ResultTable>();
				wal_rt = (List<Fcus01ResultTable>) entry.getValue();
				l_rt_salary.addAll(wal_rt);
			}
			if (p_searchTable.getTotal_client_wrbtr_h()==p_searchTable.getTotal_client_wrbtr_s()) {
				p_searchTable.setTotal_client_wrbtr_h(0); p_searchTable.setTotal_client_wrbtr_s(0);
			}else if (p_searchTable.getTotal_client_wrbtr_h()>p_searchTable.getTotal_client_wrbtr_s()){
				p_searchTable.setTotal_client_wrbtr_h(p_searchTable.getTotal_client_wrbtr_h()-p_searchTable.getTotal_client_wrbtr_s()); p_searchTable.setTotal_client_wrbtr_s(0);
			}else if (p_searchTable.getTotal_client_wrbtr_h()<p_searchTable.getTotal_client_wrbtr_s()){
				p_searchTable.setTotal_client_wrbtr_s(p_searchTable.getTotal_client_wrbtr_s()-p_searchTable.getTotal_client_wrbtr_h()); p_searchTable.setTotal_client_wrbtr_h(0);
			}
			
			if (p_searchTable.getTotal_client_dmbtr_h()==p_searchTable.getTotal_client_dmbtr_s()) {
				p_searchTable.setTotal_client_dmbtr_h(0); p_searchTable.setTotal_client_dmbtr_s(0);
			}else if (p_searchTable.getTotal_client_dmbtr_h()>p_searchTable.getTotal_client_dmbtr_s()){
				p_searchTable.setTotal_client_dmbtr_h(p_searchTable.getTotal_client_dmbtr_h()-p_searchTable.getTotal_client_dmbtr_s()); p_searchTable.setTotal_client_dmbtr_s(0);
			}else if (p_searchTable.getTotal_client_dmbtr_h()<p_searchTable.getTotal_client_dmbtr_s()){
				p_searchTable.setTotal_client_dmbtr_s(p_searchTable.getTotal_client_dmbtr_s()-p_searchTable.getTotal_client_dmbtr_h()); p_searchTable.setTotal_client_dmbtr_h(0);
			}
			
			if (p_searchTable.getTotal_podotchet_wrbtr_h()==p_searchTable.getTotal_podotchet_wrbtr_s()) {
				p_searchTable.setTotal_podotchet_wrbtr_h(0); p_searchTable.setTotal_podotchet_wrbtr_s(0);
			}else if (p_searchTable.getTotal_podotchet_wrbtr_h()>p_searchTable.getTotal_podotchet_wrbtr_s()){
				p_searchTable.setTotal_podotchet_wrbtr_h(p_searchTable.getTotal_podotchet_wrbtr_h()-p_searchTable.getTotal_podotchet_wrbtr_s()); p_searchTable.setTotal_podotchet_wrbtr_s(0);
			}else if (p_searchTable.getTotal_podotchet_wrbtr_h()<p_searchTable.getTotal_podotchet_wrbtr_s()){
				p_searchTable.setTotal_podotchet_wrbtr_s(p_searchTable.getTotal_podotchet_wrbtr_s()-p_searchTable.getTotal_podotchet_wrbtr_h()); p_searchTable.setTotal_podotchet_wrbtr_h(0);
			}
			
			if (p_searchTable.getTotal_podotchet_dmbtr_h()==p_searchTable.getTotal_podotchet_dmbtr_s()) {
				p_searchTable.setTotal_podotchet_dmbtr_h(0); p_searchTable.setTotal_podotchet_dmbtr_s(0);
			}else if (p_searchTable.getTotal_podotchet_dmbtr_h()>p_searchTable.getTotal_podotchet_dmbtr_s()){
				p_searchTable.setTotal_podotchet_dmbtr_h(p_searchTable.getTotal_podotchet_dmbtr_h()-p_searchTable.getTotal_podotchet_dmbtr_s()); p_searchTable.setTotal_podotchet_dmbtr_s(0);
			}else if (p_searchTable.getTotal_podotchet_dmbtr_h()<p_searchTable.getTotal_podotchet_dmbtr_s()){
				p_searchTable.setTotal_podotchet_dmbtr_s(p_searchTable.getTotal_podotchet_dmbtr_s()-p_searchTable.getTotal_podotchet_dmbtr_h()); p_searchTable.setTotal_podotchet_dmbtr_h(0);
			}
			
			if (p_searchTable.getTotal_salary_wrbtr_h()==p_searchTable.getTotal_salary_wrbtr_s()) {
				p_searchTable.setTotal_salary_wrbtr_h(0); p_searchTable.setTotal_salary_wrbtr_s(0);
			}else if (p_searchTable.getTotal_salary_wrbtr_h()>p_searchTable.getTotal_salary_wrbtr_s()){
				p_searchTable.setTotal_salary_wrbtr_h(p_searchTable.getTotal_salary_wrbtr_h()-p_searchTable.getTotal_salary_wrbtr_s()); p_searchTable.setTotal_salary_wrbtr_s(0);
			}else if (p_searchTable.getTotal_salary_wrbtr_h()<p_searchTable.getTotal_salary_wrbtr_s()){
				p_searchTable.setTotal_salary_wrbtr_s(p_searchTable.getTotal_salary_wrbtr_s()-p_searchTable.getTotal_salary_wrbtr_h()); p_searchTable.setTotal_salary_wrbtr_h(0);
			}
			
			if (p_searchTable.getTotal_salary_dmbtr_h()==p_searchTable.getTotal_salary_dmbtr_s()) {
				p_searchTable.setTotal_salary_dmbtr_h(0); p_searchTable.setTotal_salary_dmbtr_s(0);
			}else if (p_searchTable.getTotal_salary_dmbtr_h()>p_searchTable.getTotal_salary_dmbtr_s()){
				p_searchTable.setTotal_salary_dmbtr_h(p_searchTable.getTotal_salary_dmbtr_h()-p_searchTable.getTotal_salary_dmbtr_s()); p_searchTable.setTotal_salary_dmbtr_s(0);
			}else if (p_searchTable.getTotal_salary_dmbtr_h()<p_searchTable.getTotal_salary_dmbtr_s()){
				p_searchTable.setTotal_salary_dmbtr_s(p_searchTable.getTotal_salary_dmbtr_s()-p_searchTable.getTotal_salary_dmbtr_h()); p_searchTable.setTotal_salary_dmbtr_h(0);
			}
			p_searchTable.round();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
		}
		catch (Exception ex)
		{			 
			addMessage("Info",ex.getMessage());  
		}
	}
	private List<Fcus01ResultTable> l_rt_client = new ArrayList<Fcus01ResultTable>();
//	private List<Fcus01ResultTable> l_rt_supplier = new ArrayList<Fcus01ResultTable>();
	private List<Fcus01ResultTable> l_rt_podotchet = new ArrayList<Fcus01ResultTable>();
	private List<Fcus01ResultTable> l_rt_salary = new ArrayList<Fcus01ResultTable>();
	
	

	public List<Fcus01ResultTable> getL_rt_client() {
		return l_rt_client;
	}

	public void setL_rt_client(List<Fcus01ResultTable> l_rt_client) {
		this.l_rt_client = l_rt_client;
	}

//	public List<Fcus01ResultTable> getL_rt_supplier() {
//		return l_rt_supplier;
//	}
//
//	public void setL_rt_supplier(List<Fcus01ResultTable> l_rt_supplier) {
//		this.l_rt_supplier = l_rt_supplier;
//	}

	public List<Fcus01ResultTable> getL_rt_podotchet() {
		return l_rt_podotchet;
	}

	public void setL_rt_podotchet(List<Fcus01ResultTable> l_rt_podotchet) {
		this.l_rt_podotchet = l_rt_podotchet;
	}

	public List<Fcus01ResultTable> getL_rt_salary() {
		return l_rt_salary;
	}

	public void setL_rt_salary(List<Fcus01ResultTable> l_rt_salary) {
		this.l_rt_salary = l_rt_salary;
	}
	private SearchTable p_searchTable = new SearchTable();
	public SearchTable getP_searchTable() {
		return p_searchTable;
	}
	public void setP_searchTable(SearchTable p_searchTable) {
		this.p_searchTable = p_searchTable;
	}

	public void updateBranch()
	{
		try{
			Long ba = 0L;
			Long br = 0L;
			p_searchTable.getL_branch().clear();

			p_searchTable.getL_branch().addAll(getUserBranchList(p_searchTable.bukrs));
////			                branchF4Bean.getByBukrsOrBusinessAreaOfficesOnly(faco01Bean.p_bkpf.bukrs,userinfo.bukrs,faco01Bean.p_bkpf.business_area_id,faco01Bean.p_bkpf.business_area_id,0,userinfo.branch_id)
//			for(Branch wa:p_branchF4Bean.getByBukrsOrBusinessAreaOfficesOnly(p_searchTable.bukrs,userData.getBukrs(),ba,ba, br,userData.getBranch_id()))
//			{
////				if (wa.getBusiness_area_id()!=null && wa.getBusiness_area_id().equals(1L) || wa.getBusiness_area_id().equals(2L) || wa.getBusiness_area_id().equals(3L) || wa.getBusiness_area_id().equals(4L))
////				{
//					p_searchTable.getL_branch().add(wa);
////				}
//			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:p_branch_id");	
  			
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

	public class SearchTable {
		public SearchTable()
		{
			
		};
		private Long customer_id;
		private Customer customer = new Customer();
		private Staff staff = new Staff();
		private String bukrs = "";
		private Date bldat_from;
		private Date bldat_to;
//		private Date budat_from;
//		private Date budat_to;
		private String waers;
		private Long branch_id;
		private List<Branch> l_branch = new ArrayList<Branch>();
		private List<String> selectedBranches = new ArrayList<String>();
		private boolean our_staff = false;
		private double total_client_dmbtr_s;
		private double total_client_dmbtr_h;
		private double total_client_wrbtr_s;
		private double total_client_wrbtr_h;
		private double total_supplier_dmbtr_s;
		private double total_supplier_dmbtr_h;
		private double total_supplier_wrbtr_s;
		private double total_supplier_wrbtr_h;
		private double total_podotchet_dmbtr_s;
		private double total_podotchet_dmbtr_h;
		private double total_podotchet_wrbtr_s;
		private double total_podotchet_wrbtr_h;
		private double total_salary_dmbtr_s;
		private double total_salary_dmbtr_h;
		private double total_salary_wrbtr_s;
		private double total_salary_wrbtr_h;
		
		public void clearAmounts()
		{
			total_client_dmbtr_s=0;
			total_client_dmbtr_h=0;
			total_client_wrbtr_s=0;
			total_client_wrbtr_h=0;
			total_supplier_dmbtr_s=0;
			total_supplier_dmbtr_h=0;
			total_supplier_wrbtr_s=0;
			total_supplier_wrbtr_h=0;
			total_podotchet_dmbtr_s=0;
			total_podotchet_dmbtr_h=0;
			total_podotchet_wrbtr_s=0;
			total_podotchet_wrbtr_h=0;
			total_salary_dmbtr_s=0;
			total_salary_dmbtr_h=0;
			total_salary_wrbtr_s=0;
			total_salary_wrbtr_h=0;
		}
		public void round()
		{
			total_client_dmbtr_s		=GeneralUtil.round(total_client_dmbtr_s	, 2);
			total_client_dmbtr_h		=GeneralUtil.round(total_client_dmbtr_h	, 2);
			total_client_wrbtr_s		=GeneralUtil.round(total_client_wrbtr_s	, 2);
			total_client_wrbtr_h		=GeneralUtil.round(total_client_wrbtr_h	, 2);
			total_supplier_dmbtr_s		=GeneralUtil.round(total_supplier_dmbtr_s	, 2);
			total_supplier_dmbtr_h		=GeneralUtil.round(total_supplier_dmbtr_h	, 2);
			total_supplier_wrbtr_s		=GeneralUtil.round(total_supplier_wrbtr_s	, 2);
			total_supplier_wrbtr_h		=GeneralUtil.round(total_supplier_wrbtr_h	, 2);
			total_podotchet_dmbtr_s		=GeneralUtil.round(total_podotchet_dmbtr_s	, 2);
			total_podotchet_dmbtr_h		=GeneralUtil.round(total_podotchet_dmbtr_h	, 2);
			total_podotchet_wrbtr_s		=GeneralUtil.round(total_podotchet_wrbtr_s	, 2);
			total_podotchet_wrbtr_h		=GeneralUtil.round(total_podotchet_wrbtr_h	, 2);
			total_salary_dmbtr_s		=GeneralUtil.round(total_salary_dmbtr_s	, 2);
			total_salary_dmbtr_h		=GeneralUtil.round(total_salary_dmbtr_h	, 2);
			total_salary_wrbtr_s		=GeneralUtil.round(total_salary_wrbtr_s	, 2);
			total_salary_wrbtr_h		=GeneralUtil.round(total_salary_wrbtr_h	, 2);
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
		public double getTotal_client_dmbtr_s() {
			return total_client_dmbtr_s;
		}
		public void setTotal_client_dmbtr_s(double total_client_dmbtr_s) {
			this.total_client_dmbtr_s = total_client_dmbtr_s;
		}
		public double getTotal_client_dmbtr_h() {
			return total_client_dmbtr_h;
		}
		public void setTotal_client_dmbtr_h(double total_client_dmbtr_h) {
			this.total_client_dmbtr_h = total_client_dmbtr_h;
		}
		public double getTotal_client_wrbtr_s() {
			return total_client_wrbtr_s;
		}
		public void setTotal_client_wrbtr_s(double total_client_wrbtr_s) {
			this.total_client_wrbtr_s = total_client_wrbtr_s;
		}
		public double getTotal_client_wrbtr_h() {
			return total_client_wrbtr_h;
		}
		public void setTotal_client_wrbtr_h(double total_client_wrbtr_h) {
			this.total_client_wrbtr_h = total_client_wrbtr_h;
		}
		public double getTotal_supplier_dmbtr_s() {
			return total_supplier_dmbtr_s;
		}
		public void setTotal_supplier_dmbtr_s(double total_supplier_dmbtr_s) {
			this.total_supplier_dmbtr_s = total_supplier_dmbtr_s;
		}
		public double getTotal_supplier_dmbtr_h() {
			return total_supplier_dmbtr_h;
		}
		public void setTotal_supplier_dmbtr_h(double total_supplier_dmbtr_h) {
			this.total_supplier_dmbtr_h = total_supplier_dmbtr_h;
		}
		public double getTotal_supplier_wrbtr_s() {
			return total_supplier_wrbtr_s;
		}
		public void setTotal_supplier_wrbtr_s(double total_supplier_wrbtr_s) {
			this.total_supplier_wrbtr_s = total_supplier_wrbtr_s;
		}
		public double getTotal_supplier_wrbtr_h() {
			return total_supplier_wrbtr_h;
		}
		public void setTotal_supplier_wrbtr_h(double total_supplier_wrbtr_h) {
			this.total_supplier_wrbtr_h = total_supplier_wrbtr_h;
		}
		public double getTotal_podotchet_dmbtr_s() {
			return total_podotchet_dmbtr_s;
		}
		public void setTotal_podotchet_dmbtr_s(double total_podotchet_dmbtr_s) {
			this.total_podotchet_dmbtr_s = total_podotchet_dmbtr_s;
		}
		public double getTotal_podotchet_dmbtr_h() {
			return total_podotchet_dmbtr_h;
		}
		public void setTotal_podotchet_dmbtr_h(double total_podotchet_dmbtr_h) {
			this.total_podotchet_dmbtr_h = total_podotchet_dmbtr_h;
		}
		public double getTotal_podotchet_wrbtr_s() {
			return total_podotchet_wrbtr_s;
		}
		public void setTotal_podotchet_wrbtr_s(double total_podotchet_wrbtr_s) {
			this.total_podotchet_wrbtr_s = total_podotchet_wrbtr_s;
		}
		public double getTotal_podotchet_wrbtr_h() {
			return total_podotchet_wrbtr_h;
		}
		public void setTotal_podotchet_wrbtr_h(double total_podotchet_wrbtr_h) {
			this.total_podotchet_wrbtr_h = total_podotchet_wrbtr_h;
		}
		public double getTotal_salary_dmbtr_s() {
			return total_salary_dmbtr_s;
		}
		public void setTotal_salary_dmbtr_s(double total_salary_dmbtr_s) {
			this.total_salary_dmbtr_s = total_salary_dmbtr_s;
		}
		public double getTotal_salary_dmbtr_h() {
			return total_salary_dmbtr_h;
		}
		public void setTotal_salary_dmbtr_h(double total_salary_dmbtr_h) {
			this.total_salary_dmbtr_h = total_salary_dmbtr_h;
		}
		public double getTotal_salary_wrbtr_s() {
			return total_salary_wrbtr_s;
		}
		public void setTotal_salary_wrbtr_s(double total_salary_wrbtr_s) {
			this.total_salary_wrbtr_s = total_salary_wrbtr_s;
		}
		public double getTotal_salary_wrbtr_h() {
			return total_salary_wrbtr_h;
		}
		public void setTotal_salary_wrbtr_h(double total_salary_wrbtr_h) {
			this.total_salary_wrbtr_h = total_salary_wrbtr_h;
		}
		public Customer getCustomer() {
			return customer;
		}
		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
		public Staff getStaff() {
			return staff;
		}
		public void setStaff(Staff staff) {
			this.staff = staff;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
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
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}
		public Long getCustomer_id() {
			return customer_id;
		}
		public void setCustomer_id(Long customer_id) {
			this.customer_id = customer_id;
		}
		public boolean isOur_staff() {
			return our_staff;
		}
		public void setOur_staff(boolean our_staff) {
			this.our_staff = our_staff;
		}

		
	}	
	
	public void downloadExcelResult(int a) {
        try {
        	Long a_rep_id=92L;
        	List<Fcus01ResultTable> l_outputTable = new ArrayList<Fcus01ResultTable>();
        	double dmbtr_s=0;
        	double dmbtr_h=0;
        	double wrbtr_s=0;
        	double wrbtr_h=0;
        	if (a==1){
        		l_outputTable = l_rt_client;
        		dmbtr_s=p_searchTable.getTotal_client_dmbtr_s();
            	dmbtr_h=p_searchTable.getTotal_client_dmbtr_h();
            	wrbtr_s=p_searchTable.getTotal_client_wrbtr_s();
            	wrbtr_h=p_searchTable.getTotal_client_wrbtr_h();
        	}
        	else if (a==2){
        		l_outputTable = l_rt_podotchet;
        		dmbtr_s=p_searchTable.getTotal_podotchet_dmbtr_s();
            	dmbtr_h=p_searchTable.getTotal_podotchet_dmbtr_h();
            	wrbtr_s=p_searchTable.getTotal_podotchet_wrbtr_s();
            	wrbtr_h=p_searchTable.getTotal_podotchet_wrbtr_h();
        	}
        	else if (a==3){
        		l_outputTable = l_rt_salary;
        		dmbtr_s=p_searchTable.getTotal_salary_dmbtr_s();
            	dmbtr_h=p_searchTable.getTotal_salary_dmbtr_h();
            	wrbtr_s=p_searchTable.getTotal_salary_wrbtr_s();
            	wrbtr_h=p_searchTable.getTotal_salary_wrbtr_h();
        	}
        	
        	if (!(l_outputTable.size()>0))
        	{
        		return;
        	}
        	
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_rep_id);
            //changing blob to inputstream
            InputStream in = wa_zr.getFileu().getBinaryStream();
            
            //changing inputstream to HSSFWorkbook
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
                     
  
            
            
            HSSFRow row2 = sheet.getRow(2);
            HSSFCell stringCell = row2.getCell(1); HSSFCellStyle stringStyle = stringCell.getCellStyle();
        	HSSFCell doubleCellDebet = row2.getCell(7); HSSFCellStyle doubleStyleDebet = doubleCellDebet.getCellStyle();
        	HSSFCell doubleCellCredit = row2.getCell(8); HSSFCellStyle doubleStyleCredit = doubleCellCredit.getCellStyle();
        	
        	HSSFRow row3 = sheet.getRow(3);        	
        	HSSFCell totstringCell = row3.getCell(1); HSSFCellStyle totstringStyle = totstringCell.getCellStyle();
        	HSSFCell totdoubleCell = row3.getCell(7); HSSFCellStyle totdoubleStyle = totdoubleCell.getCellStyle();
        	
      	
            int rowNum = 2;
            for (Fcus01ResultTable wa_out:l_outputTable)
			{
            	HSSFRow row = sheet.getRow(rowNum);
            	if (row==null)
            	{
            		sheet.createRow(rowNum);
            		row = sheet.getRow(rowNum);
            	}
            	HSSFCell cell1 = row.createCell(1); 
            	HSSFCell cell2 = row.createCell(2); 
            	HSSFCell cell3 = row.createCell(3); 
            	HSSFCell cell4 = row.createCell(4); 
            	HSSFCell cell5 = row.createCell(5); 
            	HSSFCell cell6 = row.createCell(6); 
            	HSSFCell cell7 = row.createCell(7); 
            	HSSFCell cell8 = row.createCell(8); 
            	HSSFCell cell9 = row.createCell(9); 
            	HSSFCell cell10 = row.createCell(10);
            	HSSFCell cell11 = row.createCell(11);
            	
            	cell1.setCellStyle(stringStyle);
            	cell2.setCellStyle(stringStyle);
            	cell3.setCellStyle(stringStyle);
            	cell4.setCellStyle(stringStyle);
            	cell5.setCellStyle(stringStyle); 
            	cell6.setCellStyle(stringStyle);
            	cell7.setCellStyle(doubleStyleDebet);
            	cell8.setCellStyle(doubleStyleCredit);
            	cell9.setCellStyle(doubleStyleDebet);
            	cell10.setCellStyle(doubleStyleCredit);
            	cell11.setCellStyle(stringStyle);	
            	
            	if (wa_out.getBranchName()!=null) cell1.setCellValue(wa_out.getBranchName());
            	if (wa_out.getBelnr()!=null) cell2.setCellValue(wa_out.getBelnr());            	
            	if (wa_out.getBlart()!=null) cell3.setCellValue(wa_out.getBlart());
            	if (wa_out.getBldat()!=null) cell4.setCellValue(wa_out.getBldat());
            	if (wa_out.getHkont()!=null) cell5.setCellValue(wa_out.getHkont());
            	if (wa_out.getWaers()!=null) cell6.setCellValue(wa_out.getWaers());
            	cell7.setCellValue(wa_out.getWrbtr_s());
            	cell8.setCellValue(wa_out.getWrbtr_h());
            	cell9.setCellValue(wa_out.getDmbtr_s());
            	cell10.setCellValue(wa_out.getDmbtr_h()); 
            	if (wa_out.getBktxt()!=null) cell11.setCellValue(wa_out.getBktxt());   
            	
            	rowNum++;
			}
            
            HSSFRow row = sheet.getRow(rowNum);
        	if (row==null)
        	{
        		sheet.createRow(rowNum);
        		row = sheet.getRow(rowNum);
        	}
        	HSSFCell cell1 = row.createCell(1); 
        	HSSFCell cell2 = row.createCell(2); 
        	HSSFCell cell3 = row.createCell(3); 
        	HSSFCell cell4 = row.createCell(4); 
        	HSSFCell cell5 = row.createCell(5); 
        	HSSFCell cell6 = row.createCell(6); 
        	HSSFCell cell7 = row.createCell(7); 
        	HSSFCell cell8 = row.createCell(8); 
        	HSSFCell cell9 = row.createCell(9); 
        	HSSFCell cell10 = row.createCell(10);
        	HSSFCell cell11 = row.createCell(11);

        	cell1.setCellStyle(totstringStyle);
        	cell2.setCellStyle(totstringStyle);
        	cell3.setCellStyle(totstringStyle);
        	cell4.setCellStyle(totstringStyle);
        	cell5.setCellStyle(totstringStyle); 
        	cell6.setCellStyle(totstringStyle);
        	cell7.setCellStyle(totdoubleStyle);
        	cell8.setCellStyle(totdoubleStyle);
        	cell9.setCellStyle(totdoubleStyle);
        	cell10.setCellStyle(totdoubleStyle);
        	cell11.setCellStyle(totstringStyle);
        		
        	cell1.setCellValue("Итого");
        	cell7.setCellValue(wrbtr_s);
        	cell8.setCellValue(wrbtr_h);
        	cell9.setCellValue(dmbtr_s);
        	cell10.setCellValue(dmbtr_h); 

        	
        	rowNum++;
   
            //calling servlet to download
            String contentType = "application/vnd.ms-excel";
            FacesContext fc = FacesContext.getCurrentInstance();                     
            HttpServletResponse response = (HttpServletResponse)fc.getExternalContext().getResponse();
            response.setHeader("Content-disposition", "attachment; filename=" + wa_zr.getName());
            response.setContentType(contentType);
                    
            //writing excel to outputstream
            ServletOutputStream out = response.getOutputStream(); 
            wb.write(out); 
            
            //flushing and closing
            out.flush(); 
            out.close(); 
            fc.responseComplete();
//        	}
//        	else throw new DAOException("Нет записей");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
    
    
	}
}
