package finance.reports;
 
import java.io.Serializable;
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
import general.PermissionController;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.FmglflextDao;
import general.dao.UserDao;
import general.output.tables.RfcojResultTable;
import general.tables.Branch;
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
import javax.faces.event.ValueChangeEvent;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "rfcojBean", eager = true)
@ViewScoped
public class RfCoj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "RFCOJ";
	private final static Long transaction_id = (long) 32;
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
			PermissionController.canRead(userData,RfCoj.transaction_id);
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
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
		}
	}
	private double summa_total_prihod=0;
	private double summa_total_rashod=0;
	

	public double getSumma_total_prihod() {
		return summa_total_prihod;
	}

	public void setSumma_total_prihod(double summa_total_prihod) {
		this.summa_total_prihod = summa_total_prihod;
	}

	public double getSumma_total_rashod() {
		return summa_total_rashod;
	}

	public void setSumma_total_rashod(double summa_total_rashod) {
		this.summa_total_rashod = summa_total_rashod;
	}
	private Map<Long,String> l_staff_map = new HashMap<Long,String>();	
	public Map<Long, String> getL_staff_map() {
		return l_staff_map;
	}
	public void setL_staff_map(Map<Long, String> l_staff_map) {
		this.l_staff_map = l_staff_map;
	}

	public void show(){
		try {
			summa_total_prihod=0;
			summa_total_rashod=0;
			rt_list.clear();
			rt_list2.clear();
			l_staff_map.clear();
			Calendar cal_start = Calendar.getInstance();
			Calendar cal_end = Calendar.getInstance();
			String dynamicWhereClauseForBkpf = "";
			 
			if (s_date_from == null){
				throw new DAOException("Выберите дату с");
			} 
			else
			{
				cal_start.setTime(s_date_from);
			}
			
			if (s_date_to == null){
				throw new DAOException("Выберите дату по");
			} 
			else
			{
				cal_end.setTime(s_date_to);
			}
			
 
			if (p_bukrs.getBukrs().length()<4 || p_bukrs.getBukrs().equals("0000") || p_bukrs.getBukrs().equals("0"))
			{
				throw new DAOException("Выберите компанию");
			}
			if (selectedHkontForSearch==null || selectedHkontForSearch.length()==0)
			{
				throw new DAOException("Выберите кассу либо банк");
			}
			
			dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf+" b.bukrs = "+p_bukrs.getBukrs();
			dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf+" and b.blart in ('AE','BP','G2','IF','I2','SP','RP','PP','IA','NP','MT','ED','DP','KP','CP','CF','ST','OR','OV','ZZ','S2')";
			dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf+" and b.bldat between '"+GeneralUtil.getSQLDate(cal_start)+"' and '"+GeneralUtil.getSQLDate(cal_end)+"'";
			//System.out.println(dynamicWhereClauseForBkpf);
			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
			//BsegDao bsegDao = (BsegDao) appContext.getContext().getBean("bsegDao");
			//String l_hkont_where_clause = " bukrs = "+p_bukrs.getBukrs();
			
			/*for (int i = 0;i<l_as.size();i++)
			{
				AccountStatement wa_as = l_as.get(i);
				if (i==0)
				{
					l_hkont_where_clause = l_hkont_where_clause + " and hkont IN ('" +wa_as.getHkont()+"'";
				}
				else if (i==l_as.size()-1)
				{
					l_hkont_where_clause = l_hkont_where_clause + ",'" +wa_as.getHkont()+"')";
				}
				else
				{
					l_hkont_where_clause = l_hkont_where_clause + ",'" +wa_as.getHkont()+"'";
				}
				
			}
			
			if (l_as.size()==1)
			{
				l_hkont_where_clause = l_hkont_where_clause +')';
			}*/
			
			dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf + " and bs.hkont='"+selectedHkontForSearch+"'";
			if (selectedShkzg!=null && selectedShkzg.equals("S"))
			{
				dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf + " and bs.shkzg='S'";
			}
			else if (selectedShkzg!=null && selectedShkzg.equals("H"))
			{
				dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf + " and bs.shkzg='H'";
			}
			dynamicWhereClauseForBkpf = dynamicWhereClauseForBkpf + " order by bldat";
			rt_list = bkpfDao.findResultTableRfcoj(dynamicWhereClauseForBkpf,selectedWaers,selectedService);
			
			
			
			//System.out.println(l_bkpf.size());
			/*for(Bkpf wa_bkpf:l_bkpf){
				
				List<Bseg> bseg_list2 = new ArrayList<Bseg>();
				dynamicWhereClauseForBseg = "";
				//dynamicWhereClauseForBseg = l_hkont_where_clause;
				dynamicWhereClauseForBseg = dynamicWhereClauseForBseg+" belnr = "+wa_bkpf.getBelnr();
				
				dynamicWhereClauseForBseg = dynamicWhereClauseForBseg + " and hkont='"+selectedHkontForSearch+"'";
				if (selectedShkzg!=null && selectedShkzg.equals("S"))
				{
					dynamicWhereClauseForBseg = dynamicWhereClauseForBseg + " and shkzg='S'";
				}
				else if (selectedShkzg!=null && selectedShkzg.equals("H"))
				{
					dynamicWhereClauseForBseg = dynamicWhereClauseForBseg + " and shkzg='H'";
				}
				
				bseg_list2 = bsegDao.dynamicFindBseg(dynamicWhereClauseForBseg);
				l_bseg.addAll(bseg_list2);
				//System.out.println(l_bseg.size());
			}*/
			//System.out.println(l_bseg.size());
			UserDao userDao = (UserDao) appContext.getContext().getBean("userDao");
			
			for (RfcojResultTable rt:rt_list)
			{
				if (rt.getT_s_h().equals("Расход")){

					summa_total_rashod = summa_total_rashod + rt.getT_summa_rashod();
				}
				else if (rt.getT_s_h().equals("Приход")){
					rt.setT_s_h("Приход");
					summa_total_prihod = summa_total_prihod + rt.getT_summa_prihod();
				}
				
				if (rt.getUsnam()!=null && rt.getUsnam()>0)
				{
					String userFio = l_staff_map.get(rt.getUsnam());
					if (userFio==null)
					{	
						userFio = userDao.getUserFio(rt.getUsnam());
						rt.setUserFio(userFio);
						l_staff_map.put(rt.getUsnam(), userFio);
					}
					else
					{
						rt.setUserFio(userFio);
					}
					
				}
				
			}
			rt_list2.addAll(rt_list);
			
			/*for (Bkpf wa_bkpf:l_bkpf){
				RfcojResultTable rt = new RfcojResultTable();
				rt.setBelnr(wa_bkpf.getBelnr());
				rt.setBukrs(wa_bkpf.getBukrs());
				rt.setGjahr(wa_bkpf.getGjahr());
				rt.setT_blart(wa_bkpf.getBlart());
				rt.setT_waers(selectedWaers);
				rt.setT_contract_number(wa_bkpf.getContract_number());
				rt.setT_date(new SimpleDateFormat("dd.MM.yyyy").format(wa_bkpf.getBudat()));
				rt.setT_customer(wa_bkpf.getCustomer_id());
				rt.setT_bktxt(wa_bkpf.getBktxt());
				
				for (Bseg wa_bseg:l_bseg){ 
					if (wa_bkpf.getBelnr().equals(wa_bseg.getBelnr()) && wa_bkpf.getGjahr() == wa_bseg.getGjahr() && wa_bkpf.getBukrs().equals(wa_bseg.getBukrs())) {
						 
						if (wa_bseg.getShkzg().equals("H")){
							rt.setT_s_h("Расход");
							if (selectedWaers.equals("USD"))
							{
								rt.setT_summa_rashod(wa_bseg.getDmbtr());
							}
							else
							{
								rt.setT_summa_rashod(wa_bseg.getWrbtr());
							}
							summa_total_rashod = summa_total_rashod + rt.getT_summa_rashod();
						}
						else if (wa_bseg.getShkzg().equals("S")){
							rt.setT_s_h("Приход");
							if (selectedWaers.equals("USD"))
							{
								rt.setT_summa_prihod(wa_bseg.getDmbtr());
							}
							else
							{
								rt.setT_summa_prihod(wa_bseg.getWrbtr());
							}
							summa_total_prihod = summa_total_prihod + rt.getT_summa_prihod();
						}
						
					}
				}
				if (rt.getT_summa_prihod()>0 || rt.getT_summa_rashod()>0)
				{
					
					UserDao userDao = (UserDao) appContext.getContext().getBean("userDao");
					if (wa_bkpf.getUsnam()!=null && wa_bkpf.getUsnam()>0)
					{
						rt.setUserFio(userDao.getUserFio(wa_bkpf.getUsnam()));
					}
					rt_list.add(rt);
				}
				
			} */
			/**/
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:table1");
			reqCtx.update("form:userSel");
			
		} catch (DAOException ex) {
			addMessage("Инфо",ex.getMessage()); 
		}
	}
	public void handleChange(ValueChangeEvent event){  
	    System.out.println("New value: " + event.getNewValue());
	    summa_total_prihod=0;
		summa_total_rashod=0;
		rt_list.clear();
		Long new_val = Long.parseLong(event.getNewValue().toString());
		
		if (Integer.parseInt(event.getNewValue().toString())>0)
		{
			for (RfcojResultTable rt:rt_list2)
			{
				if (rt.getUsnam().equals(new_val))
				{
					if (rt.getT_s_h().equals("Расход")){

						summa_total_rashod = summa_total_rashod + rt.getT_summa_rashod();
					}
					else if (rt.getT_s_h().equals("Приход")){
						rt.setT_s_h("Приход");
						summa_total_prihod = summa_total_prihod + rt.getT_summa_prihod();
					}
					rt_list.add(rt);
				}
				
			
				
			}
		}
		else
		{
			for (RfcojResultTable rt:rt_list2)
			{
				if (rt.getT_s_h().equals("Расход")){

					summa_total_rashod = summa_total_rashod + rt.getT_summa_rashod();
				}
				else if (rt.getT_s_h().equals("Приход")){
					rt.setT_s_h("Приход");
					summa_total_prihod = summa_total_prihod + rt.getT_summa_prihod();
				}
				rt_list.add(rt);
				
			}
		}
		
	}
	public void show2(){
		try {
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:table1");
		} catch (DAOException ex) {
			addMessage("Инфо",ex.getMessage()); 
		}
	}

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
        
        HSSFCellStyle cellStyle2 = wb.createCellStyle();
        cellStyle2.setDataFormat((short)2); 
        
        for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
        for(int i=0; i < sheet.getPhysicalNumberOfRows();i++) {
        	if (i>0)
        	{
        		HSSFRow row = sheet.getRow(i);
                for(int i2=0; i2 < row.getPhysicalNumberOfCells();i2++) {
                	HSSFCell cell = row.getCell(i2);
                	if (i2==5||i2==6)
                	{
                		cell.setCellStyle(cellStyle2);
                		//System.out.println(cell.get);
                		String value = cell.getStringCellValue();
                		
                		value = value.replaceAll("\u00A0", "");
                		value = value.replace(",", ".");
                		double dvalue = Double.valueOf(value);
                		
                		value = value.replace("\\s","");
                		value = value.trim();
                		
                		cell.setCellValue(dvalue);
                	}
                	if  (i+1==sheet.getPhysicalNumberOfRows()&& i2==7)
                	{
                		cell.setCellStyle(cellStyle2);
                		//System.out.println(cell.get);
                		String value = cell.getStringCellValue();
                		
                		value = value.replaceAll("\u00A0", "");
                		value = value.replace(",", ".");
                		double dvalue = Double.valueOf(value);
                		
                		value = value.replace("\\s","");
                		value = value.trim();
                		
                		cell.setCellValue(dvalue);
                	}
                    
                }
        	}
            
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
	List<Hkont> l_hkont_1010 = new ArrayList<Hkont>();
	List<Hkont> l_hkont_1030 = new ArrayList<Hkont>();
	String selectedHkont_1010 = new String();
	String selectedHkont_1030 = new String();
	String selectedHkontForSearch = new String();
	String selectedShkzg = new String();
	
	int selectedService = 2;
	
	public int getSelectedService() {
		return selectedService;
	}

	public void setSelectedService(int selectedService) {
		this.selectedService = selectedService;
	}

	public String getSelectedShkzg() {
		return selectedShkzg;
	}

	public void setSelectedShkzg(String selectedShkzg) {
		this.selectedShkzg = selectedShkzg;
	}
	

	public String getSelectedHkont_1010() {
		return selectedHkont_1010;
	}

	public void setSelectedHkont_1010(String selectedHkont_1010) {
		this.selectedHkont_1010 = selectedHkont_1010;
	}

	public String getSelectedHkont_1030() {
		return selectedHkont_1030;
	}

	public void setSelectedHkont_1030(String selectedHkont_1030) {
		this.selectedHkont_1030 = selectedHkont_1030;
	}

	public List<Hkont> getL_hkont_1010() {
		return l_hkont_1010;
	}

	public void setL_hkont_1010(List<Hkont> l_hkont_1010) {
		this.l_hkont_1010 = l_hkont_1010;
	}

	public List<Hkont> getL_hkont_1030() {
		return l_hkont_1030;
	}

	public void setL_hkont_1030(List<Hkont> l_hkont_1030) {
		this.l_hkont_1030 = l_hkont_1030;
	}
	String selectedWaers = "";
	public void selectHkontCash(){
		selectedWaers = "";
		selectedHkontForSearch = selectedHkont_1010;
		for(Hkont wa_hkont:l_hkont_1010)
		{
			if (wa_hkont.getHkont().equals(selectedHkontForSearch))
			{
				selectedWaers = wa_hkont.getWaers();
			}
		}
		selectedHkont_1030 = "";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bank1030");
		
	}
	public void selectHkontBank(){
		selectedWaers = "";
		selectedHkontForSearch = selectedHkont_1030;
		for(Hkont wa_hkont:l_hkont_1030)
		{
			if (wa_hkont.getHkont().equals(selectedHkontForSearch))
			{
				selectedWaers = wa_hkont.getWaers();
			}
		}
		selectedHkont_1010 = "";
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:cash1010");
		
	}
	Map<String,AccountStatement> l_as_map = new HashMap<String,AccountStatement>();
	public void loadCashOffices()
	{
		try {
			l_as.clear();
			if (p_branch!=null && p_branch.getBranch_id()!=null && p_branch.getBranch_id()!=0)
			{	
				l_hkont_1010.clear();
				l_hkont_1030.clear();
				selectedWaers = "";
				FmglflextDao fmglflextDao = (FmglflextDao) appContext.getContext().getBean("fmglflextDao");
				Calendar curDate = Calendar.getInstance();	
				int curMonth = curDate.get(Calendar.MONTH) + 1;
				List<Fmglflext> l_fgl = new ArrayList<Fmglflext>();
				List<String> sl_hkont = new ArrayList<String>();
				l_as_map = new HashMap<String,AccountStatement>();
				//System.out.println(p_bukrs.getBukrs());
				for (Hkont wa_hkont:p_hkontF4Bean.getTypeHkontBranchWaers(p_bukrs.getBukrs(), p_branch.getBranch_id()))
				{
					//System.out.println(wa_hkont.getHkont());
					
					
					if (wa_hkont.getHkont().startsWith("1010"))
					{ 
						//System.out.println(wa_hkont.getHkont());
						AccountStatement wa_as = new AccountStatement();
						wa_as.setHkont(wa_hkont.getHkont());
						wa_as.setHkont_name(wa_hkont.getText45());
						wa_as.setWaers(wa_hkont.getWaers());
						l_as.add(wa_as);
						sl_hkont.add(wa_hkont.getHkont());
						l_as_map.put(wa_hkont.getHkont(), wa_as);
						l_hkont_1010.add(wa_hkont);
						//System.out.println(wa_hkont.getHkont());
					}
				
					//
					if (wa_hkont.getHkont().startsWith("1030"))
					{
						AccountStatement wa_as = new AccountStatement();
						wa_as.setHkont(wa_hkont.getHkont());
						wa_as.setHkont_name(wa_hkont.getText45());
						wa_as.setWaers(wa_hkont.getWaers());
						l_as.add(wa_as);
						sl_hkont.add(wa_hkont.getHkont());
						l_as_map.put(wa_hkont.getHkont(), wa_as);
						l_hkont_1030.add(wa_hkont);
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
			reqCtx.update("form:cash1010");
			reqCtx.update("form:bank1030");
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
	List<RfcojResultTable> rt_list2 = new ArrayList<RfcojResultTable>();
	List<RfcojResultTable> rt_list = new ArrayList<RfcojResultTable>();
	public List<RfcojResultTable> getRt_list() {
		return rt_list;
	}
	public void setRt_list(List<RfcojResultTable> rt_list) {
		this.rt_list = rt_list;
	}
	
	// ******************************************************************	
	public void changeBukrs()
	{
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:bukrs");
		reqCtx.update("form:branch");
		 
	}
	public List<Branch> getUserBranchList(String a_bukrs){
		BranchDao brDao = (BranchDao) appContext.getContext().getBean("branchDao");
		if (userData.isSys_admin()) return brDao.findUserBranchesOfficesAdmin(a_bukrs, userData.getUserid());
		else return brDao.findUserBranchesOffices(a_bukrs, userData.getUserid());		
		 
	}
}
