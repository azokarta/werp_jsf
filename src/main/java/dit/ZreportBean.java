package dit;



import f4.BranchF4;
import f4.ExchangeRateF4;
import general.AppContext; 
import general.PermissionController;
import general.dao.DAOException; 
import general.dao.ZreportDao;
import general.services.ZreportService;
import general.tables.Zreport;

import java.io.InputStream;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;
 






import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import user.User;

@ManagedBean(name = "zreportBean", eager = true)
@ViewScoped
public class ZreportBean implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "ZREPORT";
	private final static Long transaction_id = (long) 379;
	public static Long getTransactionId() {
		return transaction_id;
	}
	
	private UploadedFile file;
	 
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
	 
	public void upload() {
	        if (file != null) {
	            try {
	                System.out.println(file.getFileName());
	                
	                ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
	                zreportService.uploadFile(file);
	                FacesMessage msg = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
	                FacesContext.getCurrentInstance().addMessage(null, msg);
	                findAll();
	            } catch (Exception e) {
	                System.out.println("Exception-File Upload." + e.getMessage());
	            }
	        }
	        else{
	        FacesMessage msg = new FacesMessage("Please select image!!");
	                FacesContext.getCurrentInstance().addMessage(null, msg);
	        }
	    }
	
	public StreamedContent download(Long a_id) {
            try {
                
                ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
                Zreport wa_zr = zreportService.getFile(a_id);
                InputStream in = wa_zr.getFileu().getBinaryStream();
                StreamedContent file = new DefaultStreamedContent(in, "image/jpg", wa_zr.getName());
                
                return file;

 
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
			return null;
        
        
    }
	public void change(Long a_id,String a_name) {
        try {
            
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            zreportService.changeFileName(a_id, a_name);
            findAll();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }   
    
	}
	public void delete(Long a_id) {
        try {
            
        	ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            zreportService.deleteFile(a_id);
            findAll();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }   
    
	}
	
	public void downloadNeed(Long a_id) {
        try {
            System.out.println(file.getFileName());
            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
            Zreport wa_zr = zreportService.getFile(a_id);
            InputStream in = wa_zr.getFileu().getBinaryStream();
            //StreamedContent file = new DefaultStreamedContent(in, "image/jpg", wa_zr.getName());
//            FileInputStream fis = new FileInputStream(
//            	      new File(wa_zr.getName()));
            	      HSSFWorkbook wb = new HSSFWorkbook(in);
                    
                    HSSFSheet sheet = wb.getSheetAt(0);
                    HSSFRow header = sheet.getRow(0);
                     
                    HSSFCellStyle cellStyle = wb.createCellStyle();  
                    cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
                    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                   
                    
                    
                    for(int i=0; i < sheet.getPhysicalNumberOfRows();i++) {
                    	if (i>0)
                    	{
                    		HSSFRow row = sheet.getRow(i);
                            for(int i2=0; i2 < row.getPhysicalNumberOfCells();i2++) {
                            	HSSFCell cell = row.getCell(i2);
                            	if (cell!=null)
                            	{
                            		System.out.println(cell.getStringCellValue());
                            		cell.setCellValue("255555555555");
                            	}
                            		
                            		
//                            	
                                
                            }
                    	}
                        
                    }
                    
                    
                   // cell.setCellValue("255555555555");

                    String contentType = "application/vnd.ms-excel";
                    FacesContext fc = FacesContext.getCurrentInstance();
                     
                    HttpServletResponse response = (HttpServletResponse)fc.getExternalContext().getResponse();
                    response.setHeader("Content-disposition", "attachment; filename=" + wa_zr.getName());
                    response.setContentType(contentType);
                     
                    
                    
                    ServletOutputStream out = response.getOutputStream(); 
                    wb.write(out); 
                    out.flush(); 
                    out.close(); 
                    fc.responseComplete();
                    //- See more at: http://www.icesoft.org/JForum/posts/list/9718.page#sthash.YCfSHer2.dpuf
//            	      fis.close();
            
            
            //IOUtils.copy(in, out);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
    
    
	}
	
	public void downloadNeed2() {
        try {
        	String filename = ""+System.currentTimeMillis() + ".xls";
            
            
            //Setup the output
            String contentType = "application/vnd.ms-excel";
            FacesContext fc = FacesContext.getCurrentInstance();
             
            HttpServletResponse response = (HttpServletResponse)fc.getExternalContext().getResponse();
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setContentType(contentType);
             
            fc.responseComplete();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
    
    
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
			PermissionController.canRead(userData,ZreportBean.transaction_id);
			
			
			
			findAll();
			
			
			
			//p_searchTable.setBudat_to(curDate.getTime());
			
//			List<String> items = new ArrayList<>();
//			items.add("A");
//			items.add("B");
//			items.add("C");
//			items.add("D");
//			items.add("E");
//
//			//lambda
//			//Output : A,B,C,D,E
//			items.forEach(item->System.out.println(item));
//
//			//Output : C
//			items.forEach(item->{
//				if("C".equals(item)){
//					System.out.println(item);
//				}
//			});
//
//			//method reference
//			//Output : A,B,C,D,E
//			items.forEach(System.out::println);
//
//			//Stream and filter
//			//Output : B
//			items.stream()
//				.filter(s->s.contains("B"))
//				.forEach(System.out::println);
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	public void findAll() {
		try {
			l_outputTable.clear();
			ZreportDao zreportDao = (ZreportDao) appContext.getContext().getBean("zreportDao");
            for (Zreport wa_zr:zreportDao.findAll())
            {
            	OutputTable wa_out =  new OutputTable();
            	wa_out.setId(wa_zr.getId());
            	wa_out.setName(wa_zr.getName());
            	l_outputTable.add(wa_out);
            }
            
            RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:l_outputTable");
		}
		catch (Exception ex)
		{
			 
			addMessage("Info",ex.getMessage());  
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

  	//*******************************************************************************
	//******************************************************************
	

	
	//private List<bsid> l_bsid2 = new ArrayList<bsid>();

	
	
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
		private String name = "";
		private Long id = 0L;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		
		
		
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
                	if (i2==3||i2==4 || i2==6||i2==7 || i2==9||i2==10)
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
                	if (i2==6)
                	{                		
                		cell.setCellValue("");
                	}
                    
                }
        	}
            
        }
    }
	
	
	

	
	//*****************************************************************************************************
		
}
