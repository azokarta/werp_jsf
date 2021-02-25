package finance.reports;


import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext; 
import general.GeneralUtil;
import general.Helper;
import general.PermissionController;
import general.dao.DAOException; 
import general.dao.RfcolDao;
import general.services.ZreportService;
import general.tables.Branch;
import general.tables.Zreport;

import java.io.InputStream;
import java.io.Serializable;

 






import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.primefaces.context.RequestContext;
 









import user.User;

@ManagedBean(name = "frep8Bean", eager = true)
@ViewScoped
public class Frep8 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FREP8";
	private final static Long transaction_id = (long) 456;
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
			PermissionController.canRead(userData,Frep8.transaction_id);
			

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
  			total = new OutputTable();
  			//List<Long> l_customer_id = new ArrayList<Long>();
  			String dynamicWhereClause = "";
  			dynamicWhereClause = "";
  			
  			
  			
  			
  			//date_to.setTime(GeneralUtil.getLastDayOfMonth(p_searchTable.getP_year(), p_searchTable.getP_month()).getTime());
  			
//  			p_searchTable.setBudat_from(date_from.getTime());
//  			p_searchTable.setBudat_to(date_to.getTime());
  			if (p_searchTable.getBukrs()!=null && !p_searchTable.getBukrs().equals("0"))
  			{
  				dynamicWhereClause = dynamicWhereClause + " and c.bukrs = '"+p_searchTable.getBukrs()+"' ";
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
  				dynamicWhereClause = dynamicWhereClause + " and c.branch_id in "+br_list;
  			}
  			else
  				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
  			
  			if (p_searchTable.budat_from==null)
	  		{
	  			throw new DAOException(Helper.getErrorMessage(13L, userData.getU_language()));
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and cp.budat between '"+GeneralUtil.getSQLDate(p_searchTable.budat_from)+"' ";
	  			
	  		
	  		if (p_searchTable.budat_to==null)
	  		{
	  			throw new DAOException(Helper.getErrorMessage(14L, userData.getU_language()));
	  		}
	  		else dynamicWhereClause = dynamicWhereClause + " and '"+GeneralUtil.getSQLDate(p_searchTable.budat_to)+"' ";
  			
	  		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
  			RfcolDao rfcolDao = (RfcolDao) appContext.getContext().getBean("rfcolDao");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = rfcolDao.dynamicFrep8Result(dynamicWhereClause);
  			for (Object[] wa_result:results)
  			{
  				OutputTable wa_out = new OutputTable();
//				cp.waers,summa,br.text45,c.contract_number,c.old_sn,fio,cus.name,cp.budat,c.price-c.paid remain, cp.belnr,cp.gjahr,cp.blart,c.bukrs
  				if (wa_result[0]!=null) wa_out.setWaers(String.valueOf(wa_result[0]));
  				if (wa_result[1]!=null) wa_out.setSumma(Double.parseDouble(String.valueOf(wa_result[1])));
  				if (wa_result[2]!=null) wa_out.setBranchName(String.valueOf(wa_result[2]));
  				
  				if (wa_result[3]!=null) wa_out.setSn(Long.parseLong(String.valueOf(wa_result[3])));
  				if (wa_result[4]!=null) wa_out.setOld_sn(Long.parseLong(String.valueOf(wa_result[4])));
  				if (wa_result[5]!=null) wa_out.setFio(String.valueOf(wa_result[5]));
  				if (wa_result[6]!=null)
					try {
						wa_out.setBudat(formatter.parse(String.valueOf(wa_result[6])));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
  				if (wa_result[7]!=null) wa_out.setRemain(Double.parseDouble(String.valueOf(wa_result[7])));
  				if (wa_result[8]!=null) wa_out.setBelnr(Long.parseLong(String.valueOf(wa_result[8])));
  				if (wa_result[9]!=null) wa_out.setGjahr(Integer.parseInt(String.valueOf(wa_result[9])));
  				if (wa_result[10]!=null) wa_out.setBlart(String.valueOf(wa_result[10]));
  				if (wa_result[11]!=null) wa_out.setBukrs(String.valueOf(wa_result[11]));
  				if (wa_result[12]!=null) wa_out.setCustomer_id(Long.parseLong(String.valueOf(wa_result[12])));
  				
  				l_outputTable.add(wa_out);
  				
  				total.setSumma(total.getSumma()+wa_out.getSumma());
  				total.setRemain(total.getRemain()+wa_out.getRemain());
  				
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
	
	private List<OutputTable> l_outputTable = new ArrayList<OutputTable>();
	public List<OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
	}

	private OutputTable total = new OutputTable();
	
	
	public OutputTable getTotal() {
		return total;
	}
	public void setTotal(OutputTable total) {
		this.total = total;
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
		private Long branch_id = 0L;
		private Date budat_from;
		private Date budat_to;
		
		
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
		
		
	}
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private String bukrs = "";
		private Long branch_id = 0L;
		private String waers;
		private double summa;
		private String branchName;
		private Long sn = 0L;
		private Long old_sn = 0L;
		private String fio;
		private String name;
		private Date budat;
		private double remain;
		private Long belnr;
		private int gjahr;
		private String blart;
		private String blart_name;
		private Long customer_id;
		
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
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}	
		public String getBranchName() {
			return branchName;
		}
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		public double getSumma() {
			return summa;
		}
		public void setSumma(double summa) {
			this.summa = summa;
		}
		public Long getSn() {
			return sn;
		}
		public void setSn(Long sn) {
			this.sn = sn;
		}
		public Long getOld_sn() {
			return old_sn;
		}
		public void setOld_sn(Long old_sn) {
			this.old_sn = old_sn;
		}
		public String getFio() {
			return fio;
		}
		public void setFio(String fio) {
			this.fio = fio;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Date getBudat() {
			return budat;
		}
		public void setBudat(Date budat) {
			this.budat = budat;
		}
		public double getRemain() {
			return remain;
		}
		public void setRemain(double remain) {
			this.remain = remain;
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
		public String getBlart() {
			return blart;
		}
		public void setBlart(String blart) {
			this.blart = blart;
		}
		public String getBlart_name() {
			return blart_name;
		}
		public void setBlart_name(String blart_name) {
			this.blart_name = blart_name;
		}
		public Long getCustomer_id() {
			return customer_id;
		}
		public void setCustomer_id(Long customer_id) {
			this.customer_id = customer_id;
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
				p_searchTable.getL_branch().add(wa);
//				if (wa.getBusiness_area_id()!=null && wa.getBusiness_area_id().equals(1L) || wa.getBusiness_area_id().equals(2L) || wa.getBusiness_area_id().equals(3L) || wa.getBusiness_area_id().equals(4L))
//				{
//					p_searchTable.getL_branch().add(wa);
//				}
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
	
	public void downloadExcelResult() {
        try {
        	Long a_rep_id=67L;
        	
        	
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_rep_id);
            //changing blob to inputstream
            InputStream in = wa_zr.getFileu().getBinaryStream();
            
            //changing inputstream to HSSFWorkbook
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
                     
  
            
            
            HSSFRow row2 = sheet.getRow(2);
            HSSFCell stringCell = row2.getCell(1); HSSFCellStyle stringStyle = stringCell.getCellStyle();
        	HSSFCell dateCell = row2.getCell(5); HSSFCellStyle dateStyle = dateCell.getCellStyle();
        	HSSFCell doubleCell = row2.getCell(7); HSSFCellStyle doubleStyle = doubleCell.getCellStyle();
        	
            int rowNum = 2;
            for (OutputTable wa_out:l_outputTable)
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
            	
            	cell1.setCellStyle(stringStyle);
            	cell2.setCellStyle(stringStyle);
            	cell3.setCellStyle(stringStyle);
            	cell4.setCellStyle(stringStyle);
            	cell5.setCellStyle(dateStyle); 
            	cell6.setCellStyle(stringStyle);
            	cell7.setCellStyle(doubleStyle);
            	cell8.setCellStyle(doubleStyle);
            		
            	
            	cell1.setCellValue(wa_out.getBranchName());
            	cell2.setCellValue(wa_out.getSn());
            	cell3.setCellValue(wa_out.getOld_sn());
            	cell4.setCellValue(wa_out.getFio());
            	cell5.setCellValue(wa_out.getBudat());
            	cell6.setCellValue(wa_out.getWaers());
            	cell7.setCellValue(wa_out.getSumma());
            	cell8.setCellValue(wa_out.getRemain());

            	
            	rowNum++;
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
//        	}
//        	else throw new DAOException("Нет записей");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
    
    
	}
	
	
	

	
	//*****************************************************************************************************
		
}
