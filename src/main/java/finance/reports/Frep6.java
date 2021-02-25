package finance.reports;


import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext;
import general.Helper;
import general.PermissionController;
import general.dao.DAOException;
import general.services.FmglflextService;
import general.services.ZreportService;
import general.tables.Branch;
import general.tables.Zreport;

import java.io.InputStream;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext;
 











import user.User;

@ManagedBean(name = "frep6Bean", eager = true)
@ViewScoped
public class Frep6 implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FREP6";
	private final static Long transaction_id = (long) 434;
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
			PermissionController.canRead(userData,Frep6.transaction_id);

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
  			Calendar curDate = Calendar.getInstance();
  			
  			
  			if (p_searchTable.getBukrs()==null || p_searchTable.getBukrs().equals("0"))
  			{
  				throw new DAOException(Helper.getErrorMessage(5L, userData.getU_language()));
  			}
  				
  			
  			if (p_searchTable.getSelectedBranches().size()==0)
  			{
  				throw new DAOException(Helper.getErrorMessage(7L, userData.getU_language()));
  			}
  			double exKzt = p_exchangeRateF4Bean.getL_er_map_national().get("1KZT").getSc_value();
  			double exKgs = p_exchangeRateF4Bean.getL_er_map_national().get("1KGS").getSc_value();
  			double exUzs = p_exchangeRateF4Bean.getL_er_map_national().get("1UZS").getSc_value();
  			double exAzn = p_exchangeRateF4Bean.getL_er_map_national().get("1AZN").getSc_value();
  			double exMyr = p_exchangeRateF4Bean.getL_er_map_national().get("1MYR").getSc_value();
	  		
	  		FmglflextService fmglflextService = (FmglflextService) appContext.getContext().getBean("fmglflextService");
  			List<Object[]> results = new ArrayList<Object[]>();
  			results = fmglflextService.getAccountsBalanceFrep6(p_searchTable.getBukrs(), curDate.get(Calendar.YEAR), p_searchTable.selectedBranches,p_searchTable.hkontCashBank);
  			for (Object[] wa_result:results)
  			{
  				//a2.text45 city,a2.waers,a2.hkont,a2.usd,a2.kzt,a2.uzs,a2.kgs,a2.azn,a2.myr,a2.rn,s2.text45 hkontname
  				OutputTable wa_out = new OutputTable();
  				if (wa_result[0]!=null) wa_out.setCityName(String.valueOf(wa_result[0]));
  				if (wa_result[1]!=null) wa_out.setWaers(String.valueOf(wa_result[1]));
  				if (wa_result[2]!=null) wa_out.setHkont(String.valueOf(wa_result[2]));
  				if (wa_result[3]!=null) wa_out.setUsd(Double.parseDouble(String.valueOf(wa_result[3])));
  				if (wa_result[4]!=null) wa_out.setKzt(Double.parseDouble(String.valueOf(wa_result[4])));
  				if (wa_result[5]!=null) wa_out.setUzs(Double.parseDouble(String.valueOf(wa_result[5])));
  				if (wa_result[6]!=null) wa_out.setKgs(Double.parseDouble(String.valueOf(wa_result[6])));
  				if (wa_result[7]!=null) wa_out.setAzn(Double.parseDouble(String.valueOf(wa_result[7])));
  				if (wa_result[8]!=null) wa_out.setMyr(Double.parseDouble(String.valueOf(wa_result[8])));
  				if (wa_result[10]!=null) wa_out.setHkontName(String.valueOf(wa_result[10]));
  				
  				wa_out.setTotal_usd(wa_out.getUsd()+(wa_out.getKzt()/exKzt)+(wa_out.getUzs()/exUzs)+(wa_out.getKgs()/exKgs)+(wa_out.getAzn()/exAzn)+(wa_out.getMyr()/exMyr));
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
  	//*******************************************************************************
	//******************************************************************
	

	
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
		private String hkontCashBank = "";
		
		
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
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
		public String getHkontCashBank() {
			return hkontCashBank;
		}
		public void setHkontCashBank(String hkontCashBank) {
			this.hkontCashBank = hkontCashBank;
		}
		
		
	}
	public class OutputTable {
		public OutputTable()
		{
			
		};
		public double getMyr() {
			return myr;
		}
		public void setMyr(double myr) {
			this.myr = myr;
		}
		private String waers;
		private double kzt;
		private double usd;
		private double uzs;
		private double kgs;
		private double azn;
		private double myr;
		private double total_usd;
		private String cityName;
		private String hkont;
		private String hkontName;
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getKzt() {
			return kzt;
		}
		public void setKzt(double kzt) {
			this.kzt = kzt;
		}
		public double getUsd() {
			return usd;
		}
		public void setUsd(double usd) {
			this.usd = usd;
		}
		public double getUzs() {
			return uzs;
		}
		public void setUzs(double uzs) {
			this.uzs = uzs;
		}
		public double getKgs() {
			return kgs;
		}
		public void setKgs(double kgs) {
			this.kgs = kgs;
		}
		public double getAzn() {
			return azn;
		}
		public void setAzn(double azn) {
			this.azn = azn;
		}
		public String getCityName() {
			return cityName;
		}
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}
		public String getHkont() {
			return hkont;
		}
		public void setHkont(String hkont) {
			this.hkont = hkont;
		}
		public String getHkontName() {
			return hkontName;
		}
		public void setHkontName(String hkontName) {
			this.hkontName = hkontName;
		}
		public double getTotal_usd() {
			return total_usd;
		}
		public void setTotal_usd(double total_usd) {
			this.total_usd = total_usd;
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
	
	public void downloadExcel() {
        try {
//        	if (detailedInfo.size()>0)
//        	{	
        	Long a_rep_id=49L;
        	
        	
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_rep_id);
            //changing blob to inputstream
            InputStream in = wa_zr.getFileu().getBinaryStream();
            
            //changing inputstream to HSSFWorkbook
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
                     
  
            
            
            HSSFRow row2 = sheet.getRow(2);
            HSSFCell stringCell = row2.getCell(1); HSSFCellStyle stringStyle = stringCell.getCellStyle();
        	HSSFCell doubleCell = row2.getCell(5); HSSFCellStyle doubleStyle = doubleCell.getCellStyle();
        	
        	HSSFRow row3 = sheet.getRow(3);        	
        	HSSFCell substringCell = row3.getCell(1); HSSFCellStyle substringStyle = substringCell.getCellStyle();
        	HSSFCell subdoubleCell = row3.getCell(5); HSSFCellStyle subdoubleStyle = subdoubleCell.getCellStyle();
        	
        	HSSFRow row4 = sheet.getRow(4);        	
        	HSSFCell totstringCell = row4.getCell(1); HSSFCellStyle totstringStyle = totstringCell.getCellStyle();
        	HSSFCell totdoubleCell = row4.getCell(5); HSSFCellStyle totdoubleStyle = totdoubleCell.getCellStyle();
        	
            int rowNum = 2;
            int count = 1;
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
            	HSSFCell cell9 = row.createCell(9); 
            	HSSFCell cell10 = row.createCell(10);

            	
            	if (count==l_outputTable.size())
            	{
            		cell1.setCellStyle(totstringStyle);
                	cell2.setCellStyle(totstringStyle);
                	cell3.setCellStyle(totstringStyle);
                	cell4.setCellStyle(totstringStyle);
                	cell5.setCellStyle(totdoubleStyle); 
                	cell6.setCellStyle(totdoubleStyle);
                	cell7.setCellStyle(totdoubleStyle);
                	cell8.setCellStyle(totdoubleStyle);
                	cell9.setCellStyle(totdoubleStyle);
                	cell10.setCellStyle(totdoubleStyle);
            	}
            	else if (wa_out.getHkontName()==null || wa_out.getHkontName().length()==0)
            	{
            		cell1.setCellStyle(substringStyle);
                	cell2.setCellStyle(substringStyle);
                	cell3.setCellStyle(substringStyle);
                	cell4.setCellStyle(substringStyle);
                	cell5.setCellStyle(subdoubleStyle); 
                	cell6.setCellStyle(subdoubleStyle);
                	cell7.setCellStyle(subdoubleStyle);
                	cell8.setCellStyle(subdoubleStyle);
                	cell9.setCellStyle(subdoubleStyle);
                	cell10.setCellStyle(subdoubleStyle);
                	
            		
            	}            	 
            	else
            	{
            		cell1.setCellStyle(stringStyle);
                	cell2.setCellStyle(stringStyle);
                	cell3.setCellStyle(stringStyle);
                	cell4.setCellStyle(stringStyle);
                	cell5.setCellStyle(doubleStyle); 
                	cell6.setCellStyle(doubleStyle);
                	cell7.setCellStyle(doubleStyle);
                	cell8.setCellStyle(doubleStyle);
                	cell9.setCellStyle(doubleStyle);
                	cell10.setCellStyle(doubleStyle);
            	}
            		
            	
            	if (wa_out.getCityName()!=null) cell1.setCellValue(wa_out.getCityName());
            	if (wa_out.getWaers()!=null) cell2.setCellValue(wa_out.getWaers());
            	if (wa_out.getHkont()!=null) cell3.setCellValue(wa_out.getHkont());
            	if (wa_out.getHkontName()!=null) cell4.setCellValue(wa_out.getHkontName());
            	cell5.setCellValue(wa_out.getUsd());
            	cell6.setCellValue(wa_out.getKzt());
            	cell7.setCellValue(wa_out.getUzs());
            	cell8.setCellValue(wa_out.getKgs());
            	cell9.setCellValue(wa_out.getAzn());
            	cell10.setCellValue(wa_out.getTotal_usd());
            	 

            	
            	rowNum++;
            	count++;
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
