package accounting.assess;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import f4.BranchF4;
import f4.BukrsF4;
import f4.HkontF4;
import general.AppContext;
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.dao.BkpfDao; 
import general.dao.BsegDao; 
import general.dao.DAOException;
import general.dao.FmglflextDao;
import general.dao.PayrollDao;
import general.output.tables.FaeaOutputTable;
import general.services.PayrollService;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Bukrs;
import general.tables.Fmglflext;
import general.tables.Hkont;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "faeaBean", eager = true)
@ViewScoped
public class Faea implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FAEA";
	private final static Long transaction_id = (long) 257;
	public static Long getTransactionId() {
		return transaction_id;
	}
	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ******************************************************************
	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;
	public User getUserData() {
		return userData;
	}
	public void setUserData(User userData) {
		this.userData = userData;
	}
	// ******************************************************************
	// ***************************Bukrs**********************************	
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
	//***************************HkontF4***************************
	@ManagedProperty(value="#{hkontF4Bean}")
	private HkontF4 p_hkontF4Bean;
	public HkontF4 getP_hkontF4Bean() {
	    return p_hkontF4Bean;
	}
	public void setP_hkontF4Bean(HkontF4 p_hkontF4Bean) {
	    this.p_hkontF4Bean = p_hkontF4Bean;
	}
	//******************************************************************	
    //*****************************Branch********************************
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
	// ******************************************************************  	
	// *************************Init*************************************
	@PostConstruct
	public void init(){
		try {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Faea.transaction_id);
			for (Bukrs wa_bukrs: p_bukrsF4Bean.getBukrs_list()){
				if (!(wa_bukrs.getBukrs().equals("0001")))
				{
					bukrs_list.add(wa_bukrs);
				}
				
			}
		
			for(Branch wa_branch:p_branchF4Bean.getBranch_list())
			{
				if (wa_branch.getType()!=null && wa_branch.getType()==3)
				{
					branch_list.add(wa_branch);
				}
				
			}
			if (p_bukrs!=null && p_bukrs.getBukrs()!=null && p_branch!=null)
			{
				loadCashOffices();
			}
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
		}
	}
	private double dmbtr_total=0;
	private double wrbtr_total=0;
	
	public double getDmbtr_total() {
		return dmbtr_total;
	}

	public void setDmbtr_total(double dmbtr_total) {
		this.dmbtr_total = dmbtr_total;
	}

	public double getWrbtr_total() {
		return wrbtr_total;
	}

	public void setWrbtr_total(double wrbtr_total) {
		this.wrbtr_total = wrbtr_total;
	}
	private int p_approve = 0;
	public int getP_approve() {
		return p_approve;
	}

	public void setP_approve(int p_approve) {
		this.p_approve = p_approve;
	}

	public void show(){
		try {
			dmbtr_total=0;
			wrbtr_total=0;
			rt_list.clear();
			Calendar cal_start = Calendar.getInstance();
			Calendar cal_end = Calendar.getInstance();
			Calendar curDate = Calendar.getInstance();
			String dynamicWhereClause = "";

			if (p_bukrs==null || p_bukrs.getBukrs().length()<4 || p_bukrs.getBukrs().equals("0000") || p_bukrs.getBukrs().equals("0"))
			{
				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
			}
			dynamicWhereClause = dynamicWhereClause+" p.bukrs = "+p_bukrs.getBukrs();
			
			if (p_branch!=null && p_branch.getBranch_id()!=null && p_branch.getBranch_id()>0)
			{
				dynamicWhereClause = dynamicWhereClause+" and p.branch_id = "+p_branch.getBranch_id();
			}
			if (p_approve>0)
			{
				dynamicWhereClause = dynamicWhereClause+" and p.approve = "+p_approve;
			}
			else
			{
				dynamicWhereClause = dynamicWhereClause+" and p.approve in (2,3,4,6,9) ";
			}
			if (s_date_from != null){
				cal_start.setTime(s_date_from);
				dynamicWhereClause = dynamicWhereClause+" and p.payroll_date between '"+GeneralUtil.getSQLDate(cal_start)+"' ";
				if (s_date_to != null)
				{
					cal_end.setTime(s_date_to);
				}
				else
				{
					cal_end.setTime(curDate.getTime());
					
				}
				dynamicWhereClause = dynamicWhereClause+"  and '"+GeneralUtil.getSQLDate(cal_end)+"'";
			} 
			
			
			
			PayrollDao payrollDao = (PayrollDao) appContext.getContext().getBean("payrollDao");
			rt_list = payrollDao.dynamicFindFaea(dynamicWhereClause);
			//System.out.println(dynamicWhereClauseForBkpf);
			
			/**/
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:table1");
		} catch (DAOException ex) {
			addMessage("Инфо",ex.getMessage()); 
		}
	}
	public void to_save(){
		try {
			PermissionController.canWrite(userData,Faea.transaction_id);
			PayrollService payrollService = (PayrollService) appContext.getContext().getBean("payrollService");
			payrollService.saveFaea(rt_list,userData.getUserid(),Faea.transaction_code);
			show();
			addMessage("Инфо",Helper.getErrorMessage(101L, userData.getU_language()));
		
		
		} catch (DAOException ex) {
			addMessage("Инфо",ex.getMessage()); 
		}
	}
	/*
	public void getBsegBkpf(){
		try {
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(startDate);
			cal2.setTime(finishDate);
			
			BsegDao bsegDao = (BsegDao) appContext.getContext().getBean("bsegDao");
			bseg_list = bsegDao.getByBukrs(a_bukrs);
			abseg_list = bsegDao.getByNotLike(a_bukrs);
			
			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
			bkpf_list = bkpfDao.getByBukrsAndDate(a_bukrs, new java.sql.Date(cal1.getTimeInMillis()), new java.sql.Date(cal2.getTimeInMillis()));
		} catch (DAOException ex) {
			addMessage("Инфо",ex.getMessage());
		}
	}
	*/
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");
    }
	
	public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);
         
        HSSFCellStyle cellStyle = wb.createCellStyle();  
        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
         
        for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
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
	// ******************************************************************
	// *************************Fmglflext********************************
	private List<AccountStatement> l_as = new ArrayList<AccountStatement>();	
	public List<AccountStatement> getL_as() {
		return l_as;
	}
	public void setL_as(List<AccountStatement> l_as) {
		this.l_as = l_as;
	}

	public class AccountStatement
	{
		public AccountStatement()
		{
			
		}
		private String hkont = "";
		private String hkont_name = "";
		private String waers = "";
		private double amount = 0; 
		 
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
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		
	}
	public void loadCashOffices()
	{
		try {
			l_as.clear();
			if (p_branch!=null && p_branch.getBranch_id()!=null && p_branch.getBranch_id()!=0)
			{	
				
				FmglflextDao fmglflextDao = (FmglflextDao) appContext.getContext().getBean("fmglflextDao");
				Calendar curDate = Calendar.getInstance();	
				int curMonth = curDate.get(Calendar.MONTH) + 1;
				List<Fmglflext> l_fgl = new ArrayList<Fmglflext>();
				List<String> sl_hkont = new ArrayList<String>();
				Map<String,AccountStatement> l_as_map = new HashMap<String,AccountStatement>();
				//System.out.println(p_bukrs.getBukrs());
				for (Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
				{
					//System.out.println(wa_hkont.getHkont());
					
					
					if (wa_hkont.getBranch_id()!=null && wa_hkont.getBranch_id().equals(p_branch.getBranch_id())  && wa_hkont.getHkont().startsWith("1010"))
					{ 
						//System.out.println(wa_hkont.getHkont());
						AccountStatement wa_as = new AccountStatement();
						wa_as.setHkont(wa_hkont.getHkont());
						wa_as.setHkont_name(wa_hkont.getText45());
						wa_as.setWaers(wa_hkont.getWaers());
						l_as.add(wa_as);
						sl_hkont.add(wa_hkont.getHkont());
						l_as_map.put(wa_hkont.getHkont(), wa_as);
						//System.out.println(wa_hkont.getHkont());
					}
				
					//
					if (p_branch.getBranch_id().equals(102L) && wa_hkont.getHkont().startsWith("1030") && p_bukrs.getBukrs().equals("2000") && wa_hkont.getBukrs().equals(p_bukrs.getBukrs()))
					{
						AccountStatement wa_as = new AccountStatement();
						wa_as.setHkont(wa_hkont.getHkont());
						wa_as.setHkont_name(wa_hkont.getText45());
						wa_as.setWaers(wa_hkont.getWaers());
						l_as.add(wa_as);
						sl_hkont.add(wa_hkont.getHkont());
						l_as_map.put(wa_hkont.getHkont(), wa_as);
					}
					
					if (p_branch.getBranch_id().equals(60L) && wa_hkont.getHkont().startsWith("1030") && p_bukrs.getBukrs().equals("1000") && wa_hkont.getBukrs().equals(p_bukrs.getBukrs()))
					{
						AccountStatement wa_as = new AccountStatement();
						wa_as.setHkont(wa_hkont.getHkont());
						wa_as.setHkont_name(wa_hkont.getText45());
						wa_as.setWaers(wa_hkont.getWaers());
						l_as.add(wa_as);
						sl_hkont.add(wa_hkont.getHkont());
						l_as_map.put(wa_hkont.getHkont(), wa_as);
					}
				}
				// removing duplicates
				Set<String> hs = new HashSet<>();
				hs.addAll(sl_hkont);
				sl_hkont.clear();
				sl_hkont.addAll(hs);
				
							
				l_fgl = fmglflextDao.findByBukrsGjahrHkontList(p_bukrs.getBukrs(),curDate.get(Calendar.YEAR),sl_hkont);
				//System.out.println(l_fgl.size());
				//System.out.println(sl_hkont.size());
				for(Fmglflext wa_fmgl: l_fgl)
	  			{
					AccountStatement wa_as = l_as_map.get(wa_fmgl.getHkont());
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
			reqCtx.update("form:outputTableBranchCash");
			reqCtx.update("form:bracnhCashScrollPanel");
		}
		catch (DAOException ex)		{
			 
			addMessage("Info",ex.getMessage());  
		}
	}
	// ******************************************************************
	// *************************Local variables**************************

	
	private Date s_date_from;
	public Date getS_date_from() {
		return s_date_from;
	}
	public void setS_date_from(Date s_date_from) {
		this.s_date_from = s_date_from;
	}
	
	private Date s_date_to;
	public Date getS_date_to() {
		return s_date_to;
	}
	public void setS_date_to(Date s_date_to) {
		this.s_date_to = s_date_to;
	}
	
	private Branch p_branch = new Branch();
	public Branch getP_branch() {
		return p_branch;
	}
	public void setP_branch(Branch p_branch) {
		this.p_branch = p_branch;
	}
	
	private Bukrs p_bukrs = new Bukrs();	
	public Bukrs getP_bukrs() {
		return p_bukrs;
	}
	public void setP_bukrs(Bukrs p_bukrs) {
		this.p_bukrs = p_bukrs;
	}

	List<FaeaOutputTable> rt_list = new ArrayList<FaeaOutputTable>();
	public List<FaeaOutputTable> getRt_list() {
		return rt_list;
	}
	public void setRt_list(List<FaeaOutputTable> rt_list) {
		this.rt_list = rt_list;
	}
	
	// ******************************************************************	
	public void changeBukrs()
	{
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bukrs");
		reqCtx.update("form:branch");
		 
	}
	
}
