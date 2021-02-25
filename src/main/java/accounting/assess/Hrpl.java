package accounting.assess;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import f4.BranchF4;
import f4.BukrsF4;
import f4.MonthF4;
import general.AppContext; 
import general.PermissionController;
import general.dao.DAOException; 
import general.dao.StaffDao;
import general.dao.TempPayrollArchiveDao;
import general.dao.TempPayrollDao;
import general.services.FinanceServicePayroll;
import general.services.PayrollService;
import general.services.StaffService;
import general.tables.Branch;
import general.tables.Bukrs; 
import general.tables.Month;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.TempPayroll;
import general.tables.TempPayrollArchive;

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

@ManagedBean(name = "hrplBean", eager = true)
@ViewScoped
public class Hrpl implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private final static String transaction_code = "HRPL";
	private final static Long transaction_id = (long) 31;
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
	// ***************************BranchF4*******************************
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 p_branchF4Bean;
	public BranchF4 getP_branchF4Bean() {
		return p_branchF4Bean;
	}
	public void setP_branchF4Bean(BranchF4 p_branchF4Bean) {
		this.p_branchF4Bean = p_branchF4Bean;
	}

	// ******************************************************************
	//***************************MonthF4***************************
	@ManagedProperty(value="#{monthF4Bean}")
	private MonthF4 p_monthF4Bean;
	public MonthF4 getP_monthF4Bean() {
	    return p_monthF4Bean;
	}
	public void setP_monthF4Bean(MonthF4 p_monthF4Bean) {
	    this.p_monthF4Bean = p_monthF4Bean;
	}
	
	List<Month> p_month_list = new ArrayList<Month>();
	public List<Month> getP_month_list(){
		return p_month_list;
	}
	//******************************************************************
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
	private boolean thisMonth = false;
	
	
	//******************************************************************	
	Map<Long,TempPayroll> l_tp_map = new HashMap<Long,TempPayroll>();
	//**********************PostConstruct******************************
	@PostConstruct
	public void init() {
		try 
		{			
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Hrpl.transaction_id);
			Calendar curDate = Calendar.getInstance();
			p_searchTable.setGjahr(curDate.get(Calendar.YEAR));
			
			for(Month wa_month:p_monthF4Bean.getP_month_list())
			{
				
				if(userData.getU_language().equals(wa_month.getLanguage()))
				{ 
					p_month_list.add(wa_month);
				}
			}
			saveDisabled = true;
			chargeDisabled = false;
			
			
			
			
			/*
			if (appContext.getHrplUserId()== null || appContext.getHrplUserId()==0)
			{
				appContext.setHrplUserId(userData.getUserid());
				chargeDisabled = false;
				saveDisabled = false;
				freeDisabled = false;
			}
			else if (appContext.getHrplUserId()==userData.getUserid())
			{
				chargeDisabled = false;
				saveDisabled = true;
				freeDisabled = false;
			}
			else{
				chargeDisabled = true;
				saveDisabled = true;
				freeDisabled = true;
			}
			
			double a1 = 845235.1515215;
			double a2 = 845235.1515215;
			if(a1==a2)
			{
				System.out.println("true");
			}
			else{
				System.out.println("false");
			}
			
			DecimalFormat df2 = new DecimalFormat(".#####");  
			double salary = 1500;
			int days = 7;						
			double remainder = salary%days;
			double amount = (salary-remainder)/days;
			double formated_amount = Math.round((salary*275.35/days)*100000);
			formated_amount = formated_amount/100000;
			System.out.println("**************************");
			System.out.println("Salary USD: "+salary);
			System.out.println("Salary days: "+days);
			System.out.println("Remainder: "+remainder);
			System.out.println("Amount: "+amount*220);
			System.out.println("Amount: "+formated_amount);
			
			
			System.out.println("**************************");
			
			
			
			double wsalary = salary*220; 	
			double wremainder = wsalary%days;
			double wamount = (wsalary-wremainder)/days;
			System.out.println(amount*220+" "+wamount+" "+(wsalary)/days+" "+wamount/220+" "+wsalary/days+" "+wremainder);
			System.out.println((47142.85709*100000)%7);
			System.out.println(47142.85709/7);
			599963
			List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();	 
			List<Currency> l_currency = new ArrayList<Currency>();
			 ExchangeRateDao erDao = (ExchangeRateDao) appContext.getContext().getBean("exchangeRateDao");
			 CurrencyDao currencyDao = (CurrencyDao) appContext.getContext().getBean("currencyDao");
			l_er = erDao.getLastCurrencyRates(); 
			//System.out.println(555);
			l_currency = currencyDao.findAllWithCountries();
			
			Map<Long, ExchangeRate> map_er = new HashMap<Long, ExchangeRate>();
			for (ExchangeRate wa_er:l_er)
			{
				for(Currency wa_cur:l_currency)
				{
					if (wa_er.getSecondary_currency().equals(wa_cur.getCurrency()))
					{ 
						System.out.println(wa_cur.getP_country().getCountry()+" "+wa_cur.getP_country().getCountry_id());
						map_er.put(wa_cur.getP_country().getCountry_id(), wa_er);
					}
				}
				 
			}
			
			Set set = map_er.entrySet();
		      Iterator iterator = set.iterator();
		      while(iterator.hasNext()) {
		    	  
		         Map.Entry mentry = (Map.Entry)iterator.next();
		         ExchangeRate wa_er = (ExchangeRate) mentry.getValue();
		         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
		         System.out.println(wa_er.getSecondary_currency());
		      }
			
			//ExchangeRate wa_er = map_er.get((long)1); 
			//System.out.println(wa_er.getSecondary_currency());
			Calendar firstDay = Calendar.getInstance(); 
			firstDay.set(2015, 10, 1);
			SalaryDao salaryDao = (SalaryDao) appContext.getContext().getBean("salaryDao"); 
			List<Salary> l_salary = salaryDao.findByBukrs(new java.sql.Date(firstDay.getTimeInMillis()), "1000");
			//List<Salary> l_salary = salaryDao.findByStaffId((long) 50);
			int i = 0;
			for (Salary wa_salary:l_salary)
			{
				i++;
				System.out.println(i+" "+ wa_salary.getSalary_id()+" "+wa_salary.getP_staff().getFirstname());
			}*/
			//PayrollBonusPaymentDao pbpDao = (PayrollBonusPaymentDao) appContext.getContext().getBean("payrollBonusPaymentDao");
			//l_pbp = pbpDao.findAll(); 
					
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			toMainPage();
		}
				
	}
    //*****************************************************************
	//***********************Salary Charge*****************************	 
	public void to_charge() {
		try
		{
			PermissionController.canWrite(userData,Hrpl.transaction_id);
			if (!(userData.getUserid()==1L))
			{
				chargeDisabled = true;
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:tabView:charge_button");
				throw new DAOException("У вас нет прав. Обратитесь  к администратору.");
			}
				
			l_outputTable.clear();
			l_outputTable2.clear();
			l_outputTable3.clear();
			
			
			Calendar curDate =  Calendar.getInstance();
			if (p_searchTable.getMonat()==curDate.get(Calendar.MONTH)+1)
			{
				thisMonth = true;
			}
			
			if (p_searchTable.getBukrs() == null || p_searchTable.getBukrs().equals("0")){
				throw new DAOException("Select company");
			}
			if (p_searchTable.getMonat() > 12 || p_searchTable.getMonat() < 1){
				throw new DAOException("Select month");
			}
			if (curDate.get(Calendar.YEAR) < p_searchTable.getGjahr() || p_searchTable.getGjahr() < (curDate.get(Calendar.YEAR)-1)){
				throw new DAOException("Select year");
			}
			//if (p_operation_type == 1){
				l_tp_map.clear();
				List<TempPayroll> l_tp = new ArrayList<TempPayroll>();
				List<TempPayroll> l_tp2 = new ArrayList<TempPayroll>();
				PayrollService payrollService = (PayrollService) appContext.getContext().getBean("payrollService");	
				l_tp2 = payrollService.applySalary(p_searchTable.getMonat(), p_searchTable.getGjahr(), p_searchTable.getBukrs());
				
				TempPayrollDao tmpPrlDao = (TempPayrollDao) appContext.getContext().getBean("tmpPrlDao");	
				l_tp = tmpPrlDao.dynamicSearchGroupByStaffBranchWaers(" bukrs = '"+p_searchTable.getBukrs()+"' and gjahr= "+p_searchTable.getGjahr()+" and monat="+p_searchTable.getMonat());
				
				int i = 0;
				int i2 = 0;
				
				for(TempPayroll wa_tp: l_tp)
				{	
						OutputTable wa_out = new OutputTable();
						wa_out.setIndex(i);
						wa_out.setTp_id(wa_tp.getTp_id());
						wa_out.setWaers(wa_tp.getWaers());
						wa_out.setAmount(wa_tp.getAmount());
						wa_out.setBranch_id(wa_tp.getBranch_id());
						wa_out.setBranch_name(wa_tp.getBranch_name());
						wa_out.setStaff_id(wa_tp.getStaff_id());
						wa_out.setStaff_name(wa_tp.getStaff_name());
						wa_out.setSalary_id(wa_tp.getSalary_id());
						wa_out.setType(wa_tp.getType());
						wa_out.setBonus_id(wa_tp.getBonus_id());
						wa_out.setCustomer_id(wa_tp.getCustomer_id());
						wa_out.setText45(wa_tp.getText45());
						wa_out.setMatnr(wa_tp.getMatnr());
						wa_out.setMatnr_count(wa_tp.getMatnr_count());
						wa_out.setPosition_id(wa_tp.getPosition_id());
						wa_out.setPlan_amount(wa_tp.getPlan_amount());
						wa_out.setFact_amount(wa_tp.getFact_amount());
						l_outputTable.add(wa_out);
						l_tp_map.put(wa_tp.getTp_id(), wa_tp);
						i++;
					
				}
				
				for(TempPayroll wa_tp: l_tp2)
				{
					OutputTable wa_out3 = new OutputTable();
					BeanUtils.copyProperties(wa_tp, wa_out3);
					if (wa_tp.getDrcrk().equals("H"))
					{
						wa_out3.setAmount(wa_out3.getAmount()*-1);
					}
					l_outputTable3.add(wa_out3);
					
					if (wa_tp.getType()>3 && wa_tp.getType()<7)
					{
						OutputTable wa_out = new OutputTable();
						wa_out.setIndex(i2);
						wa_out.setTp_id(wa_tp.getTp_id());
						wa_out.setWaers(wa_tp.getWaers());
						wa_out.setAmount(wa_tp.getAmount());
						wa_out.setStaff_name(wa_tp.getStaff_name());
						wa_out.setType(wa_tp.getType());
						wa_out.setCustomer_id(wa_tp.getCustomer_id());
						wa_out.setText45(wa_tp.getText45());
						l_outputTable2.add(wa_out);
						l_tp_map.put(wa_tp.getTp_id(), wa_tp);
						i2++;
					}
					
				}
				
				
				
				//PayrollBonusPaymentDao pbpDao = (PayrollBonusPaymentDao) appContext.getContext().getBean("payrollBonusPaymentDao");
				//l_pbp = new ArrayList<PayrollBonusPayment>();
				//l_pbp = pbpDao.findAll();
				tabindex = 0;
				if (l_outputTable.size()>0)
				{
					if (userData.getUserid()==1L)
					{
						saveDisabled = false;
					}
					
				}
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:tabView");
				 
			//}
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			System.out.println(ex.getMessage());
			//toMainPage();
		}
	}
	public void charge_view() {
		try
		{
			PermissionController.canRead(userData,Hrpl.transaction_id);
			l_outputTable.clear();
			l_outputTable2.clear();
			l_outputTable3.clear();
			Calendar curDate =  Calendar.getInstance();
			if (p_searchTable.getMonat()==curDate.get(Calendar.MONTH)+1)
			{
				thisMonth = true;
			}
			
			if (p_searchTable.getBukrs() == null || p_searchTable.getBukrs().equals("0")){
				throw new DAOException("Select company");
			}
			if (p_searchTable.getMonat() > 12 || p_searchTable.getMonat() < 1){
				throw new DAOException("Select month");
			}
			if (curDate.get(Calendar.YEAR) < p_searchTable.getGjahr() || p_searchTable.getGjahr() < (curDate.get(Calendar.YEAR)-1)){
				throw new DAOException("Select year");
			}
			//if (p_operation_type == 1){
				l_tp_map.clear();
				List<TempPayroll> l_tp = new ArrayList<TempPayroll>();
				List<TempPayroll> l_tp2 = new ArrayList<TempPayroll>();
				TempPayrollDao tmpPrlDao = (TempPayrollDao) appContext.getContext().getBean("tmpPrlDao");
				String dynamicWhereClause = "";
				dynamicWhereClause = dynamicWhereClause + " bukrs = '"+p_searchTable.getBukrs()+"'";
				dynamicWhereClause = dynamicWhereClause + " and gjahr = "+p_searchTable.getGjahr();
				dynamicWhereClause = dynamicWhereClause + " and monat = "+p_searchTable.getMonat();
				if (p_searchTable.getBranch_id()!=null && p_searchTable.getBranch_id()>0)
				{
					dynamicWhereClause = dynamicWhereClause + " and branch_id = "+p_searchTable.getBranch_id();
				}
				if (p_searchTable.getStaff_id()!=null && p_searchTable.getStaff_id()>0)
				{
					dynamicWhereClause = dynamicWhereClause + " and staff_id = "+p_searchTable.getStaff_id();
				}
				l_tp2 = tmpPrlDao.dynamicSearch(dynamicWhereClause);
				l_tp = tmpPrlDao.dynamicSearchGroupByStaffBranchWaers(" bukrs = '"+p_searchTable.getBukrs()+"' and gjahr= "+p_searchTable.getGjahr()+" and monat="+p_searchTable.getMonat());
				
				int i = 0;
				int i2 = 0;
				
				for(TempPayroll wa_tp: l_tp)
				{	
						OutputTable wa_out = new OutputTable();
						wa_out.setIndex(i);
						wa_out.setTp_id(wa_tp.getTp_id());
						wa_out.setWaers(wa_tp.getWaers());
						wa_out.setAmount(wa_tp.getAmount());
						wa_out.setBranch_id(wa_tp.getBranch_id());
						wa_out.setBranch_name(wa_tp.getBranch_name());
						wa_out.setStaff_id(wa_tp.getStaff_id());
						wa_out.setStaff_name(wa_tp.getStaff_name());
						wa_out.setSalary_id(wa_tp.getSalary_id());
						wa_out.setType(wa_tp.getType());
						wa_out.setBonus_id(wa_tp.getBonus_id());
						wa_out.setCustomer_id(wa_tp.getCustomer_id());
						wa_out.setText45(wa_tp.getText45());
						wa_out.setMatnr(wa_tp.getMatnr());
						wa_out.setMatnr_count(wa_tp.getMatnr_count());
						wa_out.setPosition_id(wa_tp.getPosition_id());
						wa_out.setPlan_amount(wa_tp.getPlan_amount());
						wa_out.setFact_amount(wa_tp.getFact_amount());
						l_outputTable.add(wa_out);
						l_tp_map.put(wa_tp.getTp_id(), wa_tp);
						i++;
					
				}
				
				
				i = 0;
				i2 = 0;
				for(TempPayroll wa_tp: l_tp2)
				{
					OutputTable wa_out3 = new OutputTable();
					BeanUtils.copyProperties(wa_tp, wa_out3);
					if (wa_tp.getDrcrk().equals("H"))
					{
						wa_out3.setAmount(wa_out3.getAmount()*-1);
					}
					l_outputTable3.add(wa_out3);
					
					if (wa_tp.getType()>3 && wa_tp.getType()<7)
					{
						OutputTable wa_out = new OutputTable();
						wa_out.setIndex(i2);
						wa_out.setTp_id(wa_tp.getTp_id());
						wa_out.setWaers(wa_tp.getWaers());
						wa_out.setAmount(wa_tp.getAmount());
						wa_out.setStaff_name(wa_tp.getStaff_name());
						wa_out.setType(wa_tp.getType());
						wa_out.setCustomer_id(wa_tp.getCustomer_id());
						wa_out.setText45(wa_tp.getText45());
						l_outputTable2.add(wa_out);
						l_tp_map.put(wa_tp.getTp_id(), wa_tp);
						i2++;
					}
				}
				//PayrollBonusPaymentDao pbpDao = (PayrollBonusPaymentDao) appContext.getContext().getBean("payrollBonusPaymentDao");
				//l_pbp = new ArrayList<PayrollBonusPayment>();
				//l_pbp = pbpDao.findAll();
				if (l_tp.size()>0)
				{
					saveDisabled = false;
				}
				
				tabindex = 0;
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:tabView");
				 
			//}
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			System.out.println(ex.getMessage());
			//toMainPage();
		}
	}
	public void removeStaff()
	{
		try{
			p_searchTable.setStaff_id(null);
			p_searchTable.setStaff_name("");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tabView:p_staff");
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			System.out.println(ex.getMessage());
			//toMainPage();
		}
	}
	public void removeStaff2()
	{
		try{
			p_searchTable2.setStaff_id(null);
			p_searchTable2.setStaff_name("");
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tabView:p_staff2");
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			System.out.println(ex.getMessage());
			//toMainPage();
		}
	}
	
	//*****************************************************************
	//************************Save Bonus*****************************
	public void to_save(){		
		try{
			if (l_outputTable.size()>0)
			{
				PermissionController.canWrite(userData,Hrpl.transaction_id);
				if (!(userData.getUserid()==1L))
				{
					saveDisabled = true;
					RequestContext reqCtx = RequestContext.getCurrentInstance();
					reqCtx.update("form:tabView:save_button");
					throw new DAOException("У вас нет прав. Обратитесь  к администратору.");
				}
				l_tp_map.clear();
				List<TempPayroll> l_tp = new ArrayList<TempPayroll>();
				TempPayrollDao tmpPrlDao = (TempPayrollDao) appContext.getContext().getBean("tmpPrlDao");	
				l_tp = tmpPrlDao.dynamicSearch(" bukrs = '"+p_searchTable.getBukrs()+"' and monat = "+p_searchTable.getMonat()+" and gjahr = "+p_searchTable.getGjahr());
				FinanceServicePayroll financeServicePayroll =  (FinanceServicePayroll) appContext.getContext().getBean("financeServicePayroll");
				financeServicePayroll.createSalaryPayments(l_tp, p_searchTable.getBukrs(), p_searchTable.getGjahr(), p_searchTable.getMonat(),userData.getUserid());
				p_searchTable2 = new SearchTable();
				p_searchTable2.setBukrs(p_searchTable.getBukrs());
				p_searchTable2.setGjahr(p_searchTable.getGjahr());
				p_searchTable2.setMonat(p_searchTable.getMonat());
				p_searchTable2.setMonat_name(p_searchTable.getMonat_name());
				saveDisabled = true;
				to_search();
				
			}
			//l_outputTable.clear();
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			System.out.println(ex.getMessage());
			//toMainPage();
		}
	}
	//*****************************************************************	
	//***********************Salary Bonus******************************	
	public void to_search(){
		try{
			PermissionController.canRead(userData,Hrpl.transaction_id);
			l_outputTable.clear();
			l_outputTable2.clear();
			l_outputTable3.clear();
			l_outputTableArchive.clear();
			l_outputTableArchive2.clear();
			List<TempPayrollArchive> wa_tempPayrollArchive_list = new ArrayList<TempPayrollArchive>();
			List<TempPayrollArchive> wa_tempPayrollArchive_list2 = new ArrayList<TempPayrollArchive>();
			TempPayrollArchiveDao tmpPrlArcDao = (TempPayrollArchiveDao) appContext.getContext().getBean("tmpPrlArcDao");	
			
			
			if (p_searchTable2.getBukrs()==null || p_searchTable2.getBukrs().length()==0)
			{
				throw new DAOException("Select company");
			}
			if (p_searchTable2.getGjahr()==0)
			{
				throw new DAOException("Select year");
			}
			if (p_searchTable2.getMonat()<1 || p_searchTable.getMonat()>12)
			{
				throw new DAOException("Select month");
			}
			
			Long count;
			String dynamicWhereClause = "";
			dynamicWhereClause = dynamicWhereClause + " bukrs = '"+p_searchTable2.getBukrs()+"'";
			dynamicWhereClause = dynamicWhereClause + " and gjahr = '"+p_searchTable2.getGjahr()+"'"; 
			dynamicWhereClause = dynamicWhereClause + " and monat = '"+p_searchTable2.getMonat()+"'";
			
			if (p_searchTable2.getBranch_id()!=null && p_searchTable2.getBranch_id()>0)
			{
				dynamicWhereClause = dynamicWhereClause + " and branch_id = "+p_searchTable2.getBranch_id();
			}
			if (p_searchTable2.getStaff_id()!=null && p_searchTable2.getStaff_id()>0)
			{
				dynamicWhereClause = dynamicWhereClause + " and staff_id = "+p_searchTable2.getStaff_id();
			}
			
		
			count = tmpPrlArcDao.countDynamicSearch(dynamicWhereClause);
			if (count>0)
			{
				int wa_index = 0;
				int i2 = 0;
				wa_tempPayrollArchive_list2 = tmpPrlArcDao.dynamicSearch(dynamicWhereClause);
				wa_tempPayrollArchive_list = tmpPrlArcDao.dynamicSearchGroupByStaffBranchWaers(" bukrs = '"+p_searchTable2.getBukrs()+"' and gjahr= "+p_searchTable2.getGjahr()+" and monat="+p_searchTable2.getMonat());

				
				for(TempPayrollArchive wa_tempPayrollArchive: wa_tempPayrollArchive_list)
				{	
					
						OutputTable wa_out = new OutputTable();
						wa_out.setIndex(wa_index);
						wa_out.setTp_id(wa_tempPayrollArchive.getTp_id());
						wa_out.setWaers(wa_tempPayrollArchive.getWaers());
						wa_out.setAmount(wa_tempPayrollArchive.getAmount());
						wa_out.setBranch_id(wa_tempPayrollArchive.getBranch_id());
						wa_out.setStaff_id(wa_tempPayrollArchive.getStaff_id());
						wa_out.setSalary_id(wa_tempPayrollArchive.getSalary_id());
						wa_out.setType(wa_tempPayrollArchive.getType());
						wa_out.setCustomer_id(wa_tempPayrollArchive.getCustomer_id());
						wa_out.setBranch_name(wa_tempPayrollArchive.getBranch_name());
						wa_out.setStaff_name(wa_tempPayrollArchive.getStaff_name());
						wa_out.setText45(wa_tempPayrollArchive.getText45());
						wa_out.setMatnr(wa_tempPayrollArchive.getMatnr());
						wa_out.setMatnr_count(wa_tempPayrollArchive.getMatnr_count());
						wa_out.setPosition_id(wa_tempPayrollArchive.getPosition_id());
						wa_out.setPlan_amount(wa_tempPayrollArchive.getPlan_amount());
						wa_out.setFact_amount(wa_tempPayrollArchive.getFact_amount());
						l_outputTableArchive.add(wa_out);
						wa_index ++;
					
				}
				
				wa_index = 0;
				for (TempPayrollArchive wa_tempPayrollArchive: wa_tempPayrollArchive_list2)
				{ 
					OutputTable wa_out3 = new OutputTable();
					BeanUtils.copyProperties(wa_tempPayrollArchive, wa_out3);
					if (wa_tempPayrollArchive.getDrcrk().equals("H"))
					{
						wa_out3.setAmount(wa_out3.getAmount()*-1);
					}
					l_outputTable3.add(wa_out3);
					if (wa_tempPayrollArchive.getType()>3 && wa_tempPayrollArchive.getType()<7)
					{
						OutputTable wa_out = new OutputTable();
						wa_out.setIndex(i2);
						wa_out.setTp_id(wa_tempPayrollArchive.getTp_id());
						wa_out.setWaers(wa_tempPayrollArchive.getWaers());
						wa_out.setAmount(wa_tempPayrollArchive.getAmount());
						wa_out.setStaff_name(wa_tempPayrollArchive.getStaff_name());
						wa_out.setType(wa_tempPayrollArchive.getType());
						wa_out.setCustomer_id(wa_tempPayrollArchive.getCustomer_id());
						wa_out.setText45(wa_tempPayrollArchive.getText45());
						l_outputTableArchive2.add(wa_out);
						i2++;
					}
				}
			}
			tabindex = 1;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");
			//System.out.println(count);
		}
		catch (DAOException ex)
		{
			addMessage("Info  ",ex.getMessage());  
		}
		
	}
	//*****************************************************************	
	//***********************Others************************************	 
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
	
	public void addMessage(String summary, String detail) {
       FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
       FacesContext.getCurrentInstance().addMessage(null, message);
       RequestContext reqCtx = RequestContext.getCurrentInstance();
       reqCtx.update("form:messages");
   }
	

	//*****************************************************************
	//****************************Search Parameters**********************************
	/*
	public void valueChanged(String ind, String a_tpId, String col) {
		try
		{			
			OutputTable wa_out = new OutputTable();
			int index = Integer.parseInt(ind);
			Long tpId = Long.parseLong(a_tpId);
		    int column = Integer.parseInt(col);
		    if (tpId>0 && column ==1 && index>=0)
		    {
		    	wa_out = l_outputTable.get(index);	
		    	//if (wa_out.getAmount()<-500000000 || wa_out.getAmount()>500000000)
		    	//{
		    	//System.out.println(wa_out.getAmount());
		    		//throw new DAOException("incorrect value");
		    	//}
		    	PayrollService payrollService = (PayrollService) appContext.getContext().getBean("payrollService"); 
		    	TempPayroll wa_prl = l_tp_map.get(tpId); 
		    	if (wa_out.getAmount()<=0)
		    	{
		    		wa_out.setAmount(wa_prl.getAmount());
		    		throw new DAOException("Amount can't be equal to 0 or less");
		    	}
		    	else
		    	{
		    		wa_prl.setAmount(wa_out.getAmount());
			    	payrollService.changeTempPayroll(wa_prl); 		    	
			    	wa_out.setStatus("Saved");
		    	}
		    	
		    }
		    else if (tpId>0 && column ==2 && index>=0)
		    {
		    	wa_out = l_outputTable.get(index);		    	
		    	PayrollService payrollService = (PayrollService) appContext.getContext().getBean("payrollService"); 
		    	TempPayroll wa_prl = l_tp_map.get(tpId); 
		    	wa_prl.setText45(wa_out.getText45());
		    	payrollService.changeTempPayroll(wa_prl); 		    	
		    	wa_out.setStatus("Saved");
		    }
		    RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView:outputTable:"+index+":status");
  			reqCtx.update("form:tabView:outputTable:"+index+":mess"); 
  			reqCtx.update("form:tabView:outputTable:"+index+":amount1"); 
		}
		catch (DAOException ex)
		{
			int index = Integer.parseInt(ind);
			RequestContext reqCtx = RequestContext.getCurrentInstance();
	  		reqCtx.update("form:tabView:outputTable:"+index+":status");
	  		reqCtx.update("form:tabView:outputTable:"+index+":mess"); 
	  		reqCtx.update("form:tabView:outputTable:"+index+":amount1"); 
			addMessage("Info  ",ex.getMessage());  
		}
		catch (Exception ex)
		{
			int index = Integer.parseInt(ind); 
			RequestContext reqCtx = RequestContext.getCurrentInstance();
	  		reqCtx.update("form:tabView:outputTable:"+index+":status");
	  		reqCtx.update("form:tabView:outputTable:"+index+":mess"); 
	  		reqCtx.update("form:tabView:outputTable:"+index+":amount1"); 
			addMessage("Info  ",ex.getMessage());  
		}
	}	*/
	private List<OutputTable> l_outputTableArchive = new ArrayList<OutputTable>();	
	public List<OutputTable> getL_outputTableArchive() {
		return l_outputTableArchive;
	}
	public void setL_outputTableArchive(List<OutputTable> l_outputTableArchive) {
		this.l_outputTableArchive = l_outputTableArchive;
	}
	private List<OutputTable> l_outputTableDetailed = new ArrayList<OutputTable>();
	
	private List<OutputTable> l_outputTable = new ArrayList<OutputTable>();
	public List<OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
	}
	
	private List<OutputTable> l_outputTable2 = new ArrayList<OutputTable>();
	private List<OutputTable> l_outputTable3 = new ArrayList<OutputTable>();
	private List<OutputTable> l_outputTableArchive2 = new ArrayList<OutputTable>();
	private OutputTable selectedStaffOut = new OutputTable();
	
	public List<OutputTable> getL_outputTable2() {
		return l_outputTable2;
	}

	public void setL_outputTable2(List<OutputTable> l_outputTable2) {
		this.l_outputTable2 = l_outputTable2;
	}

	public List<OutputTable> getL_outputTableArchive2() {
		return l_outputTableArchive2;
	}

	public void setL_outputTableArchive2(List<OutputTable> l_outputTableArchive2) {
		this.l_outputTableArchive2 = l_outputTableArchive2;
	}

	public class OutputTable {
		public OutputTable()
		{
			
		};
		private int index;
		private Long tp_id;
		private String waers;
		private double amount; 
		private Long branch_id;
		private String branch_name;
		private Long staff_id;
		private String staff_name;
		private Long salary_id; 
		private int type;
		private Long bonus_id;
		private Long customer_id;
		private String text45;
		private String status;
		private Long matnr;
		private int matnr_count;
		private Long position_id;
		private double plan_amount; 
		private double fact_amount; 
		
		public double getPlan_amount() {
			return plan_amount;
		}
		public void setPlan_amount(double plan_amount) {
			this.plan_amount = plan_amount;
		}
		public double getFact_amount() {
			return fact_amount;
		}
		public void setFact_amount(double fact_amount) {
			this.fact_amount = fact_amount;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getText45() {
			return text45;
		}
		public void setText45(String text45) {
			this.text45 = text45;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public Long getTp_id() {
			return tp_id;
		}
		public void setTp_id(Long tp_id) {
			this.tp_id = tp_id;
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
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}
		public String getBranch_name() {
			return branch_name;
		}
		public void setBranch_name(String branch_name) {
			this.branch_name = branch_name;
		}
		public Long getStaff_id() {
			return staff_id;
		}
		public void setStaff_id(Long staff_id) {
			this.staff_id = staff_id;
		}
		public String getStaff_name() {
			return staff_name;
		}
		public void setStaff_name(String staff_name) {
			this.staff_name = staff_name;
		}
		public Long getSalary_id() {
			return salary_id;
		}
		public void setSalary_id(Long salary_id) {
			this.salary_id = salary_id;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public Long getBonus_id() {
			return bonus_id;
		}
		public void setBonus_id(Long bonus_id) {
			this.bonus_id = bonus_id;
		}
		
		public Long getCustomer_id() {
			return customer_id;
		}
		public void setCustomer_id(Long customer_id) {
			this.customer_id = customer_id;
		}
		public Long getMatnr() {
			return matnr;
		}
		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}
		public int getMatnr_count() {
			return matnr_count;
		}
		public void setMatnr_count(int matnr_count) {
			this.matnr_count = matnr_count;
		}
		public Long getPosition_id() {
			return position_id;
		}
		public void setPosition_id(Long position_id) {
			this.position_id = position_id;
		}
		
		
	}
	
	private List<OutputTableTax> l_outputTableTaxArchive = new ArrayList<OutputTableTax>();	
	public List<OutputTableTax> getL_outputTableTaxArchive() {
		return l_outputTableTaxArchive;
	}
	public void setL_outputTableTaxArchive(List<OutputTableTax> l_outputTableTaxArchive) {
		this.l_outputTableTaxArchive = l_outputTableTaxArchive;
	}
	
	private List<OutputTableTax> l_outputTableTax = new ArrayList<OutputTableTax>();
	public List<OutputTableTax> getL_outputTableTax() {
		return l_outputTableTax;
	}
	public void setL_outputTableTax(List<OutputTableTax> l_outputTableTax) {
		this.l_outputTableTax = l_outputTableTax;
	}

	public class OutputTableTax {
		public OutputTableTax()
		{
			
		};
		private int index;
		private String official_waers;
		private double pii_tax;
		private double soc_tax;
		private Long sub_company_id;
		private String sub_company_name;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getOfficial_waers() {
			return official_waers;
		}
		public void setOfficial_waers(String official_waers) {
			this.official_waers = official_waers;
		}
		public double getPii_tax() {
			return pii_tax;
		}
		public void setPii_tax(double pii_tax) {
			this.pii_tax = pii_tax;
		}
		public double getSoc_tax() {
			return soc_tax;
		}
		public void setSoc_tax(double soc_tax) {
			this.soc_tax = soc_tax;
		}
		public Long getSub_company_id() {
			return sub_company_id;
		}
		public void setSub_company_id(Long sub_company_id) {
			this.sub_company_id = sub_company_id;
		}
		public String getSub_company_name() {
			return sub_company_name;
		}
		public void setSub_company_name(String sub_company_name) {
			this.sub_company_name = sub_company_name;
		}
		
		
	}
	
	
  	private int tabindex = 0;
  	public int getTabindex() {
		return tabindex;
	}
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}
	
	
	private HeaderPanelGrid p_headerPanelGrid2 = new HeaderPanelGrid();	
	public HeaderPanelGrid getP_headerPanelGrid2() {
		return p_headerPanelGrid2;
	}
	public void setP_headerPanelGrid2(HeaderPanelGrid p_headerPanelGrid2) {
		this.p_headerPanelGrid2 = p_headerPanelGrid2;
	}
	
	private HeaderPanelGrid p_headerPanelGrid1 = new HeaderPanelGrid();	
	public HeaderPanelGrid getP_headerPanelGrid1() {
		return p_headerPanelGrid1;
	}

	public void setP_headerPanelGrid1(HeaderPanelGrid p_headerPanelGrid1) {
		this.p_headerPanelGrid1 = p_headerPanelGrid1;
	}
	public class HeaderPanelGrid {
		public HeaderPanelGrid()
		{
			
		};
		private String bukrs = "";
		private int gjahr;
		private int monat;
		private String monat_name; 
		private Long branch_id;
		private Long staff_id; 
		private String branch_name;
		private String staff_name;
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public int getGjahr() {
			return gjahr;
		}
		public void setGjahr(int gjahr) {
			this.gjahr = gjahr;
		}
		public int getMonat() {
			return monat;
		}
		public void setMonat(int monat) {
			this.monat = monat;
		}
		public String getMonat_name() {
			return monat_name;
		}
		public void setMonat_name(String monat_name) {
			this.monat_name = monat_name;
		}
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}
		public Long getStaff_id() {
			return staff_id;
		}
		public void setStaff_id(Long staff_id) {
			this.staff_id = staff_id;
		}
		public String getBranch_name() {
			return branch_name;
		}
		public void setBranch_name(String branch_name) {
			this.branch_name = branch_name;
		}
		public String getStaff_name() {
			return staff_name;
		}
		public void setStaff_name(String staff_name) {
			this.staff_name = staff_name;
		}
		
	}
	private SearchTable p_searchTable = new SearchTable();
	public SearchTable getP_searchTable() {
		return p_searchTable;
	}
	public void setP_searchTable(SearchTable p_searchTable) {
		this.p_searchTable = p_searchTable;
	}
	
	private SearchTable p_searchTable2 = new SearchTable();
	public SearchTable getP_searchTable2() {
		return p_searchTable2;
	}
	public void setP_searchTable2(SearchTable p_searchTable2) {
		this.p_searchTable2 = p_searchTable2;
	}

	public class SearchTable {
		public SearchTable()
		{
			
		};
		private String bukrs = "";
		private int gjahr;
		private int monat;
		private String monat_name; 
		private Long branch_id;
		private Long staff_id; 
		private String branch_name;
		private String staff_name;
		
		public Long getStaff_id() {
			return staff_id;
		}
		public void setStaff_id(Long staff_id) {
			this.staff_id = staff_id;
		}
		public String getBranch_name() {
			return branch_name;
		}
		public void setBranch_name(String branch_name) {
			this.branch_name = branch_name;
		}
		public String getStaff_name() {
			return staff_name;
		}
		public void setStaff_name(String staff_name) {
			this.staff_name = staff_name;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public int getGjahr() {
			return gjahr;
		}
		public void setGjahr(int gjahr) {
			this.gjahr = gjahr;
		}
		public int getMonat() {
			return monat;
		}
		public void setMonat(int monat) {
			this.monat = monat;
		}
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}
		public String getMonat_name() {
			return monat_name;
		}
		public void setMonat_name(String monat_name) {
			this.monat_name = monat_name;
		}	 
		
	}	
	public void assignMonatName(){
		for(Month wa_month:p_month_list)
		{
			if(p_searchTable.getMonat()==wa_month.getId())
			{
				p_searchTable.setMonat_name(wa_month.getName());
			}
		}
		
	}

	public void assignBranchName(){ 
		for(Branch wa_branch:p_branchF4Bean.getBranch_list())
		{
			if(p_searchTable.getBranch_id()==wa_branch.getBranch_id())
			{
				p_searchTable.setBranch_name(wa_branch.getText45());
				
			}
		}	
		
	}
	//************************Variables local***************************
	private boolean saveDisabled = true;
	private boolean chargeDisabled = true;
	private boolean freeDisabled = true; 
	public boolean isSaveDisabled() {
		return saveDisabled;
	}

	public void setSaveDisabled(boolean saveDisabled) {
		this.saveDisabled = saveDisabled;
	}
	public boolean isChargeDisabled() {
		return chargeDisabled;
	}

	public void setChargeDisabled(boolean chargeDisabled) {
		this.chargeDisabled = chargeDisabled;
	}

	public boolean isFreeDisabled() {
		return freeDisabled;
	}

	public void setFreeDisabled(boolean freeDisabled) {
		this.freeDisabled = freeDisabled;
	}
	public void free_charge()
	{
		/*if (appContext.getHrplUserId()==userData.getUserid())
		{
			appContext.setHrplUserId((long) 0);
			freeDisabled = true;
			chargeDisabled = true;
			saveDisabled = true;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView");
		}*/
	}
	
	//******************************************************************

	
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
		public void to_search_staff() {
			try {
				String dynamicWhereClause = "";
				StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
				Long branch_id = null;
				if (tabindex==0)
				{
					branch_id=p_searchTable.getBranch_id();
				}
				else if(tabindex==1)
				{
					branch_id=p_searchTable2.getBranch_id();
				}
				
				if (searchStaff!=null && searchStaff.getStaff_id()!=null && searchStaff.getStaff_id()>0)
				{	 
					dynamicWhereClause = dynamicWhereClause + " and stf.staff_id = " + searchStaff.getStaff_id();			
					dynamicWhereClause = dynamicWhereClause + " and stf.position_id <> 1";
					p_staff_list = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
				}
				else if (searchStaff!=null && searchStaff.getIin_bin()!=null && searchStaff.getIin_bin().length()>0)
				{  
					dynamicWhereClause = dynamicWhereClause + " and stf.iin_bin = '" + searchStaff.getIin_bin()+"'";
					dynamicWhereClause = dynamicWhereClause + " and stf.position_id <> 1";
					p_staff_list = staffDao.dynamicFindStaffSalary(dynamicWhereClause);
				}
				else if  (searchStaff.getFirstname()!=null || searchStaff.getLastname()!=null || searchStaff.getMiddlename()!=null || branch_id!=null)
				{ 
					Salary searchSalary = new Salary();
					searchSalary.setBukrs(p_searchTable.getBukrs());
					searchSalary.setBranch_id(p_searchTable.getBranch_id());
					StaffService staffService = (StaffService) appContext.getContext()
							.getBean("staffService");
					p_staff_list = staffService.dynamicSearchStaffSalary2(searchStaff,
							searchSalary);

					if (p_staff_list.size() == 0) {
						p_staff_list = new ArrayList<Staff>();
						addMessage("Инфо", "Не найдено.");
					}
					/*
					String firstName = "";
					String middleName = "";
					String lastName = "";
					firstName = searchStaff.getFirstname().replaceAll("\\s", "");
					middleName = searchStaff.getMiddlename().replaceAll("\\s", "");
					lastName = searchStaff.getLastname().replaceAll("\\s", "");
					if (firstName.length() > 0) {
						dynamicWhereClause = dynamicWhereClause + " 1=1 ";
						dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(dynamicWhereClause, "and", "=", "stf.firstname", firstName);
					}
					if (middleName.length() > 0) {
						dynamicWhereClause = dynamicWhereClause + " ";
						dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(dynamicWhereClause, "and", "=", "stf.middlename", middleName);
					}
					if (lastName.length() > 0) {
						dynamicWhereClause = dynamicWhereClause + " ";
						dynamicWhereClause = Validation.sqlMatchPatternDynamicWhere(dynamicWhereClause, "and", "=", "stf.lastname", lastName);
					}
					
					if (branch_id!=null && branch_id>0)
					{
						dynamicWhereClause = dynamicWhereClause + "stf.branch_id ="+ branch_id;
					}
					if (dynamicWhereClause.length()==0)
					{
						throw new DAOException("Fill search criteria");
					}
					dynamicWhereClause = dynamicWhereClause + " and stf.position_id <> 1"; 
					p_staff_list = staffDao.dynamicFindStaffSalary2(dynamicWhereClause);*/
				}
				
				if (p_staff_list==null || p_staff_list.size()==0)
				{
					throw new DAOException("No employee found");
				}
					
				

				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:staffTable");

			} catch (DAOException ex) {
				p_staff_list = new ArrayList<Staff>();
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:staffTable");
				addMessage("Info", ex.getMessage());
			}
		}
		public void assignFoundEmployee() {
			String fio = "";
			if (selectedStaff != null && selectedStaff.getStaff_id() != null) {
				if (selectedStaff.getLastname() != null && selectedStaff.getLastname().length()>0 )
					fio = fio + selectedStaff.getLastname();
				if (selectedStaff.getFirstname() != null && selectedStaff.getFirstname().length()>0 )
					fio = fio + " " + selectedStaff.getFirstname();
				if (selectedStaff.getMiddlename() != null && selectedStaff.getMiddlename().length()>0 )
					fio = fio + " " + selectedStaff.getMiddlename(); 
				
				if (tabindex==0)
				{
					p_searchTable.setStaff_id(selectedStaff.getStaff_id());
					p_searchTable.setStaff_name(fio);
				}
				else if(tabindex==1)
				{
					p_searchTable2.setStaff_id(selectedStaff.getStaff_id());
					p_searchTable2.setStaff_name(fio);
				}
				
				
			} else {
				if (tabindex==0)
				{
					p_searchTable.setStaff_id(null);
					p_searchTable.setStaff_name("");
				}
				else if(tabindex==1)
				{
					p_searchTable2.setStaff_id(null);
					p_searchTable2.setStaff_name("");
				}
				
				
			}

			selectedStaff = new Staff();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			if (tabindex==0)
			{
				reqCtx.update("form:tabView:p_staff");
			}
			else if(tabindex==1)
			{
				reqCtx.update("form:tabView:p_staff2");
			}
		}
		// *********************************************************************
		
	public double getDob() {
			return dob;
		}

		public void setDob(double dob) {
			this.dob = dob;
		}
	private double dob;
	
	public void onTabChange()
	{
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('employeeWidget').hide();");
	}
	public void clearSearchStaffList()
	{
		p_staff_list = new ArrayList<Staff>();
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:staffTable");
	}
	public void goToFaha()
	{
		try {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
   	 	
			context.redirect(context.getRequestContextPath() + "/finance/accounting/employee/faha.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isThisMonth() {
		return thisMonth;
	}

	public void setThisMonth(boolean thisMonth) {
		this.thisMonth = thisMonth;
	}

	public List<OutputTable> getL_outputTableDetailed() {
		return l_outputTableDetailed;
	}

	public void setL_outputTableDetailed(List<OutputTable> l_outputTableDetailed) {
		this.l_outputTableDetailed = l_outputTableDetailed;
	}

	public OutputTable getSelectedStaffOut() {
		return selectedStaffOut;
	}

	public void setSelectedStaffOut(OutputTable selectedStaffOut) {
		this.selectedStaffOut = selectedStaffOut;
	}
	
	public void onSelectOutputTable(){
		try {
			l_outputTableDetailed.clear();
			for (OutputTable wa_out:l_outputTable3)
			{
				if (selectedStaffOut.getStaff_id().equals(wa_out.getStaff_id()))
				{
					l_outputTableDetailed.add(wa_out);
				}
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
		    reqCtx.update("form:outputTableDetailed");
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
		}
	}
}
