package finance.reports;

import f4.BranchF4;
import f4.ExchangeRateF4;
import finance.reports.Frep5.DetailTable;
import general.AppContext; 
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BsegDao;
import general.dao.DAOException; 
import general.dao.RfcolDao;
import general.dao.ZreportDao;
import general.output.tables.Frep1OutputTable;
import general.output.tables.Frep2OutputTable;
import general.services.ZreportService;
import general.tables.ExchangeRate;
import general.tables.Zreport;

import java.io.InputStream;
import java.io.Serializable;

 






import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "frep2Bean", eager = true)
@ViewScoped
public class Frep2 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FREP2";
	private final static Long transaction_id = (long) 316;
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
			//Calendar curDate = Calendar.getInstance();
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Frep2.transaction_id);
			

			
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
  			l_outputTable.clear();
  			total = new Frep2OutputTable();
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			String dynamicWhereClause2 = "";
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and b.bukrs = '"+p_searchTable.getBukrs()+"' ";
  			}
  			else
  				throw new DAOException("Выберите компанию");
  			
  			if (p_searchTable.getConNum()!=null && p_searchTable.getConNum().length()>1)
  			{
  				dynamicWhereClause2 = dynamicWhereClause2 + " and con.contract_number = "+p_searchTable.getConNum();
  			}
  			else if (p_searchTable.getRefConNum()!=null && p_searchTable.getRefConNum().length()>1)
  			{
  				dynamicWhereClause2 = dynamicWhereClause2 + " and ref_con.contract_number = "+p_searchTable.getRefConNum();
  			}
  			else
  			{
  				if (p_searchTable.getBranch_id()!=null && !p_searchTable.getBranch_id().equals(0L))
  	  			{
  	  				if (!p_searchTable.getBranch_id().equals(-1L))
  	  				{
  	  				dynamicWhereClause2 = dynamicWhereClause2 + " and con.branch_id = "+p_searchTable.getBranch_id();
  	  				}
  	  				
  	  			}
  	  			else
  	  				throw new DAOException("Выберите филиал");
  	  			
  	  			if (p_searchTable.getWaers()!=null && !p_searchTable.getWaers().equals("0"))
  	  			{
  	  			dynamicWhereClause2 = dynamicWhereClause2 + " and b.waers = '"+p_searchTable.getWaers()+"' ";
  	  			}
  	  			
  	  			if (p_searchTable.getS_date_from()==null)
  	  			{
  	  				throw new DAOException("Выберите дату договора с");
  	  			}
  	  			else dynamicWhereClause2 = dynamicWhereClause2 + " and con.contract_date >= '"+GeneralUtil.getSQLDate(p_searchTable.getS_date_from())+"' ";
  	  				
  	  			
  	  			if (p_searchTable.getS_date_to()==null)
  	  			{
  	  				throw new DAOException("Выберите дату договора по");
  	  			}
  	  			else dynamicWhereClause2 = dynamicWhereClause2 + " and con.contract_date <= '"+GeneralUtil.getSQLDate(p_searchTable.getS_date_to())+"' ";
  			}
  			
  			dynamicWhereClause = dynamicWhereClause + dynamicWhereClause2;

  			//System.out.println(dynamicWhereClause);
  			BsegDao bsegDao = (BsegDao) appContext.getContext().getBean("bsegDao");
  			l_outputTable = bsegDao.dynamicFindFrep2(dynamicWhereClause);
  			
  			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = bsegDao.dynamicFindFrep2OldBaza(dynamicWhereClause2);
  			//staraya baza
  			for (Object[] result : results) {
				Frep2OutputTable wa_rt = new Frep2OutputTable();
				//wa_fot.setIndex(index);
				if (result[0]!=null) wa_rt.setBukrs(String.valueOf(result[0]));
				if (result[1]!=null) wa_rt.setWaers(String.valueOf(result[1]));
				if (result[2]!=null)
					try {
						wa_rt.setConDate(formatter.parse(String.valueOf(result[2])));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if (result[3]!=null)
					try {
						wa_rt.setVydachaDate(formatter.parse(String.valueOf(result[3])));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				if (result[4]!=null) wa_rt.setCusId(Long.parseLong(String.valueOf(result[4])));
				if (result[5]!=null && Integer.parseInt(String.valueOf(result[5]))==1) wa_rt.setCusFIO(String.valueOf(result[6]));
				else if (result[5]!=null && Integer.parseInt(String.valueOf(result[5]))==2) wa_rt.setCusFIO(Validation.returnFioInitials(String.valueOf(result[7]), String.valueOf(result[8]), String.valueOf(result[9])));
				if (result[10]!=null) wa_rt.setConNum(Long.parseLong(String.valueOf(result[10])));
				if (result[11]!=null) wa_rt.setConWaers(String.valueOf(result[11]));
				if (result[12]!=null) wa_rt.setConKursf(Double.parseDouble(String.valueOf(result[12]))); 
				
				
				

				
				
				if (result[13]!=null) wa_rt.setRefCusId(Long.parseLong(String.valueOf(result[13])));
				if (result[14]!=null && Integer.parseInt(String.valueOf(result[14]))==1) wa_rt.setRefCusFIO(String.valueOf(result[15]));
				else if (result[14]!=null && Integer.parseInt(String.valueOf(result[14]))==2) wa_rt.setRefCusFIO(Validation.returnFioInitials(String.valueOf(result[16]), String.valueOf(result[17]), String.valueOf(result[18])));
				if (result[19]!=null) wa_rt.setRefConNum(Long.parseLong(String.valueOf(result[19])));
				if (result[20]!=null) wa_rt.setRefConWaers(String.valueOf(result[20]));
				if (result[21]!=null) wa_rt.setRefConKursf(Double.parseDouble(String.valueOf(result[21])));
				if (result[22]!=null) wa_rt.setBranchName(String.valueOf(result[22]));
				if (result[23]!=null) wa_rt.setSumma(Double.parseDouble(String.valueOf(result[23])));
				wa_rt.setBktxt("Старая база");
				if (wa_rt.getWaers().equals("USD") && wa_rt.getRefConWaers().equals("USD"))
				{
					
					ExchangeRate wa_er = p_exchangeRateF4Bean.getL_er_map_internal().get("2"+String.valueOf(result[24])+"1000");
					if (wa_er!=null)
					{
						wa_rt.setWaers(wa_er.getSecondary_currency());
						wa_rt.setSumma(wa_rt.getSumma()*wa_er.getSc_value());
					}
					else  
					{
						wa_rt.setWaers("");
						wa_rt.setSumma(0);
						wa_rt.setBktxt("Внут. валюта не найдена.");
					}
					
				}
				else if (wa_rt.getWaers().equals("USD") && !wa_rt.getRefConWaers().equals("USD"))
				{
					wa_rt.setSumma(wa_rt.getSumma()*wa_rt.getRefConKursf());
					wa_rt.setWaers(wa_rt.getRefConWaers());
				}
				l_outputTable.add(wa_rt);
				

			}
	    	
  			
  			if (l_outputTable.size()==0)
  			{
  				throw new DAOException("Не найдено");
  			}
  			for(Frep2OutputTable wa_rt:l_outputTable)
  			{			
				total.setSumma(total.getSumma()+wa_rt.getSumma()); 				
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
	

	
	//private List<bsid> l_bsid2 = new ArrayList<bsid>();

	
	

	
	
	
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
		private Long branch_id = 0L;
		private String conNum;
		private String refConNum;
		private String waers;
		private Date s_date_from;
		private Date s_date_to;
		public Date getS_date_from() {
			return s_date_from;
		}
		public void setS_date_from(Date s_date_from) {
			this.s_date_from = s_date_from;
		}
		public Date getS_date_to() {
			return s_date_to;
		}
		public void setS_date_to(Date s_date_to) {
			this.s_date_to = s_date_to;
		}
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

		public String getConNum() {
			return conNum;
		}
		public void setConNum(String conNum) {
			this.conNum = conNum;
		}
		public String getRefConNum() {
			return refConNum;
		}
		public void setRefConNum(String refConNum) {
			this.refConNum = refConNum;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
	}	
	


	private Frep2OutputTable total = new Frep2OutputTable();
	
	
	public Frep2OutputTable getTotal() {
		return total;
	}
	public void setTotal(Frep2OutputTable total) {
		this.total = total;
	}



	private List<Frep2OutputTable> l_outputTable = new ArrayList<Frep2OutputTable>();
	
	public List<Frep2OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<Frep2OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
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
	
	public List<PaymentDetail> getL_pd() {
		return l_pd;
	}
	public void setL_pd(List<PaymentDetail> l_pd) {
		this.l_pd = l_pd;
	}

	public void getPaymentDetails(String a_cus_id) throws DAOException
	{
		try{
			l_pd.clear();
			String dynamicWhereClause = "";
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			BsegDao bsegDao = (BsegDao) appContext.getContext().getBean("bsegDao");
			dynamicWhereClause = dynamicWhereClause + " and bs.lifnr="+a_cus_id;
			List<Object[]> results = bsegDao.dynamicFindFrep2PaymentDetails(dynamicWhereClause);
			
			for (Object[] result : results) {
				PaymentDetail wa_rt = new PaymentDetail();
				if (result[0]!=null) wa_rt.setBukrs(String.valueOf(result[0]));
				if (result[1]!=null) wa_rt.setBelnr(Long.parseLong(String.valueOf(result[1])));
				if (result[2]!=null) wa_rt.setGjahr(Integer.parseInt(String.valueOf(result[2])));
				if (result[3]!=null) wa_rt.setBktxt(String.valueOf(result[3]));
				if (result[4]!=null) wa_rt.setBranchName(String.valueOf(result[4]));
				if (result[5]!=null) wa_rt.setDmbtr(Double.parseDouble(String.valueOf(result[5]))); 
				if (result[6]!=null) wa_rt.setWrbtr(Double.parseDouble(String.valueOf(result[6])));
				if (result[7]!=null)
					try {
						wa_rt.setBudat(formatter.parse(String.valueOf(result[7])));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if (result[8]!=null)
					try {
						wa_rt.setBldat(formatter.parse(String.valueOf(result[8])));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if (result[9]!=null) wa_rt.setWaers(String.valueOf(result[9]));
				//wa_fot.setIndex(index);
				

				
				
				if (wa_rt.getWaers().equals("USD")) wa_rt.setSumma(wa_rt.getDmbtr());
				else wa_rt.setSumma(wa_rt.getWrbtr());
				l_pd.add(wa_rt);
				

			}
			
			
			RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:detInfoTable");	
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}
	}

	private List<PaymentDetail> l_pd = new ArrayList<Frep2.PaymentDetail>();
	public class PaymentDetail {
		public PaymentDetail()
		{
			
		};
		
		private String bukrs = "";
		private String bktxt;
		private String branchName;
		private Long belnr;
		private int gjahr;
		private double dmbtr;
		private double wrbtr;
		private double summa;
		private Date budat;
		private Date bldat;
		private String waers;
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public String getBktxt() {
			return bktxt;
		}
		public void setBktxt(String bktxt) {
			this.bktxt = bktxt;
		}
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
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
		public double getSumma() {
			return summa;
		}
		public void setSumma(double summa) {
			this.summa = summa;
		}
		public Date getBudat() {
			return budat;
		}
		public void setBudat(Date budat) {
			this.budat = budat;
		}
		public Date getBldat() {
			return bldat;
		}
		public void setBldat(Date bldat) {
			this.bldat = bldat;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		
	}	

	//*****************************************************************************************************
	public void downloadAct(String a_bukrs,Long a_conNum,Long a_refConNum, double a_summa,String a_waers) {
        try {
        	Long a_rep_id=28L;
        	if (a_bukrs.equals("2000"))
        	{
        		a_rep_id=29L;
        	}
        	ZreportDao zreportDao = (ZreportDao) appContext.getContext().getBean("zreportDao");
        	List<Object[]> p_new = new ArrayList<Object[]>();
        	List<Object[]> p_rec = new ArrayList<Object[]>();
        	p_new = zreportDao.frep2Act(a_conNum);
        	p_rec = zreportDao.frep2Act(a_refConNum);
        	String ofis_from = "";
        	String condate_new = "";
        	String clientfio_new = "";
        	String dealerfio_new = "";
        	String ofis_to = "";
        	String condate_rec = "";
        	String clientfio_rec = "";
        	String dealerfio_rec = "";
        	Calendar dat = Calendar.getInstance();
        	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        	
        	Calendar curDate = Calendar.getInstance();
        	String paydate = "";
			String month1 = String.valueOf(curDate.get(Calendar.MONTH)+1);
			if (month1.length()==1)
			{
				month1 = "0"+month1;
			}
			paydate =  String.valueOf(curDate.get(Calendar.DAY_OF_MONTH))+"."+ month1+"."+String.valueOf(curDate.get(Calendar.YEAR));
//    		br1.text45 branchname"
//					+" ,con1.contract_number sn"
//					+" ,con1.contract_date condate"
//					+" ,cus1.lastname || ' ' || cus1.firstname  || ' ' || cus1.middlename clientfio"
//					+" ,cus1.name clientname"
//					+" ,cus1.fiz_yur clienttype"
//					+" ,st1.lastname || ' ' || st1.firstname  || ' ' || st1.middlename dealerfio"
        	for (Object[] wa_result:p_new)
        	{
        		
            	if (wa_result[0]!=null) ofis_from = String.valueOf(wa_result[0]);
            	if (wa_result[2]!=null)
					try {
						
						dat.setTime(GeneralUtil.removeTime(formatter.parse(String.valueOf(wa_result[2]))));
						String month = String.valueOf(dat.get(Calendar.MONTH)+1);
						if (month.length()==1)
						{
							month = "0"+month;
						}
						
						condate_new =  String.valueOf(dat.get(Calendar.DAY_OF_MONTH))+"."+ month +"."+String.valueOf(dat.get(Calendar.YEAR));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	
            	if (wa_result[6]!=null) dealerfio_new = String.valueOf(wa_result[6]);
            	
            	
            	if (wa_result[5]!=null)
            	{
            		if (Integer.parseInt(String.valueOf(wa_result[5]))==1)
            		{
            			if (wa_result[4]!=null) clientfio_new = String.valueOf(wa_result[4]);
            		}
            		else if (Integer.parseInt(String.valueOf(wa_result[5]))==2)
            		{
            			if (wa_result[3]!=null) clientfio_new = String.valueOf(wa_result[3]);
            		}
            			
            	}
  				
        	}
        	
        	
        	
        	for (Object[] wa_result:p_rec)
        	{
        		if (wa_result[0]!=null) ofis_to = String.valueOf(wa_result[0]);
            	if (wa_result[2]!=null)
					try {
						dat.setTime(GeneralUtil.removeTime(formatter.parse(String.valueOf(wa_result[2]))));
						String month = String.valueOf(dat.get(Calendar.MONTH)+1);
						if (month.length()==1)
						{
							month = "0"+month;
						}
						condate_rec =  String.valueOf(dat.get(Calendar.DAY_OF_MONTH))+"."+ month+"."+String.valueOf(dat.get(Calendar.YEAR));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	
            	if (wa_result[6]!=null) dealerfio_rec = String.valueOf(wa_result[6]);
            	
            	
            	if (wa_result[5]!=null)
            	{
            		if (Integer.parseInt(String.valueOf(wa_result[5]))==1)
            		{
            			if (wa_result[4]!=null) clientfio_rec = String.valueOf(wa_result[4]);
            		}
            		else if (Integer.parseInt(String.valueOf(wa_result[5]))==2)
            		{
            			if (wa_result[3]!=null) clientfio_rec = String.valueOf(wa_result[3]);
            		}
            			
            	}
        	}
        	
        	
        	
        	
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_rep_id);
            //changing blob to inputstream
            InputStream in = wa_zr.getFileu().getBinaryStream();
            
            //changing inputstream to HSSFWorkbook
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
                     
            HSSFCellStyle cellStyle = wb.createCellStyle();  
            cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                       
                    
            for(int i=0; i < 30;i++) 
            {
            	HSSFRow row = sheet.getRow(i);
            	if (row!=null)
            	{
            		for(int i2=0; i2 < 4;i2++) 
                	{
                		HSSFCell cell = row.getCell(i2);
                		if (cell!=null)
                		{
                			String val=cell.getStringCellValue();
                			if (val.length()>0)
                			{
//                				System.out.println("Row:"+i+" Cell:"+i2);
//                				val = val.replaceAll("\u00A0", "");                        		
                				val = val.replace("#ofis_from#",ofis_from);
                				val = val.replace("#sn_new#",String.valueOf(a_conNum));
                				val = val.replace("#condate_new#",String.valueOf(condate_new));
                				val = val.replace("#clientfio_new#",clientfio_new);
                				val = val.replace("#dealerfio_new#",dealerfio_new);
                				val = val.replace("#ofis_to#",ofis_to);
                				val = val.replace("#sn_rec#",String.valueOf(a_refConNum));
                				val = val.replace("#condate_rec#",String.valueOf(condate_rec));
                				val = val.replace("#clientfio_rec#",clientfio_rec);
                				val = val.replace("#dealerfio_rec#",dealerfio_rec);
                				val = val.replace("#paydate#",String.valueOf(paydate));
                				val = val.replace("#summa#",a_summa+" "+a_waers);
//                				System.out.println(val);
                				
                				cell.setCellValue(val);
                			}
                			

                		}
                    
                	}
            	}                	
                        
            }
                    
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
    
    
	}	
}
