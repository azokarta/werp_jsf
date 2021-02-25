package reports.finance;
 
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import f4.BukrsF4;
import general.AppContext;
import general.dao.BkpfDao; 
import general.dao.BsegDao; 
import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.Blart;
import general.tables.Bseg;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.Customer;

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

@ManagedBean(name = "rfcimBean", eager = true)
@ViewScoped
public class Rfcim implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	
	
	@PostConstruct
	public void init(){
		try {
			for (Bukrs wa_bukrs: p_bukrsF4Bean.getBukrs_list()){
				if (!(wa_bukrs.getBukrs().equals("0001")))
				{
					bukrs_list.add(wa_bukrs);
				}
				
			}
		}
			catch (DAOException ex)
			{
				addMessage("Error",ex.getMessage()); 
			}
	}
	
	
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
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
			String lan_dir = "/en"; 
			if (userData.getU_language().equals("en"))
			{
				lan_dir = "/en"; 
			}
			else if (userData.getU_language().equals("ru"))
			{
				lan_dir = "/ru"; 
			}
			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath() + lan_dir + "/general/mainPage.xhtml");
		}
		catch (Exception ex)
		{
			 
			addMessage("Error",ex.getMessage());  
		}
	}
	
	public class ResultTable {
		private String bukrs;
		private int gjahr;
		private Long belnr;
		private String t_date;
		private String t_s_h;
		private Double t_dmbtr;
		private Double t_wrbtr;
		private Long t_customer;
		private String t_waers;
		private String t_blart;
		private Long t_contract_number;
		
		public String getT_date() {
			return t_date;
		}
		public void setT_date(String t_date) {
			this.t_date = t_date;
		}
		public String getT_s_h() {
			return t_s_h;
		}
		public void setT_s_h(String t_s_h) {
			this.t_s_h = t_s_h;
		}
		public Double getT_dmbtr() {
			return t_dmbtr;
		}
		public void setT_dmbtr(Double t_dmbtr) {
			this.t_dmbtr = t_dmbtr;
		}
		public Double getT_wrbtr() {
			return t_wrbtr;
		}
		public void setT_wrbtr(Double t_wrbtr) {
			this.t_wrbtr = t_wrbtr;
		}
		public Long getT_customer() {
			return t_customer;
		}
		public void setT_customer(Long t_customer) {
			this.t_customer = t_customer;
		}
		public String getT_waers() {
			return t_waers;
		}
		public void setT_waers(String t_waers) {
			this.t_waers = t_waers;
		}
		public String getT_blart() {
			return t_blart;
		}
		public void setT_blart(String t_blart) {
			this.t_blart = t_blart;
		}
		public Long getT_contract_number() {
			return t_contract_number;
		}
		public void setT_contract_number(Long t_contract_number) {
			this.t_contract_number = t_contract_number;
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
		public Long getBelnr() {
			return belnr;
		}
		public void setBelnr(Long belnr) {
			this.belnr = belnr;
		}		
	}
}
