package finance.reports;

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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;

import user.User;
import f4.BukrsF4; 
import f4.HkontF4;
import general.tables.Bseg; 
import general.AppContext; 
import general.PermissionController;
import general.dao.DAOException; 
import general.dao.FmglflextDao; 
import general.tables.Bukrs; 
import general.tables.Fmglflext;
import general.tables.Hkont; 

@ManagedBean (name="faglb03DBBean", eager=true)
@ViewScoped
public class Faglb03DB implements Serializable{
	private static final long serialVersionUID = 1L; 
	private final static String transaction_code = "FAGLB03";
	private final static Long transaction_id = (long) 6;
	public static Long getTransactionId() {
		return transaction_id;
	}
	
	
	
	List<Faglb03> faglb03_list = new ArrayList<Faglb03>();
	
	private Bseg p_bseg = new Bseg(); 
	public Bseg getP_bseg() {
		return p_bseg;
	}	
	public void setP_bseg(Bseg p_bseg) {
		this.p_bseg = p_bseg;
	}	
		

	
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
	
	private String p_bukrs = "";	
	public String getP_bukrs() {
		return p_bukrs;
	}
	public void setP_bukrs(String p_bukrs) {
		this.p_bukrs = p_bukrs;
	}

	private int p_gjahr = 2015;
	public int getP_gjahr() {
		return p_gjahr;
	}
	public void setP_gjahr(int p_gjahr) {
		this.p_gjahr = p_gjahr;
	}



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
	
	List<Hkont> hkont_list2 = new ArrayList<Hkont>();	
	public List<Hkont> getHkont_list2(){
		return hkont_list2;
	}
	
	private Hkont selectedHkont;
	public Hkont getSelectedHkont() {
        return selectedHkont; 
    } 
    public void setSelectedHkont(Hkont p_selectedHkont) {
        this.selectedHkont = p_selectedHkont;
    }
    String hkont_filter = "";
	public String getHkont_filter() {
		return hkont_filter;
	}
	public void setHkont_filter(String hkont_filter) {
		this.hkont_filter = hkont_filter;
	}
    
    //******************************************************************		




	@PostConstruct
	public void init(){
		try 
		{
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Faglb03DB.transaction_id);
			Calendar curDate = Calendar.getInstance();
			p_gjahr = curDate.get(Calendar.YEAR);

		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage()); 
			toMainPage();
		}
				
	}


	
	public void assignSelectedHkont(){ 
		p_bseg.setHkont(getSelectedHkont().getHkont()); 
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:hkont");
	}
	
	List<Fmglflext> fgl_list = new ArrayList<Fmglflext>();
	
	public void update(){
		try 
		{
			faglb03_list.clear();
			fgl_list.clear();
			FmglflextDao fglDao = (FmglflextDao) appContext.getContext().getBean("fmglflextDao");
			fgl_list = fglDao.findByBukrsGjahrHkont(p_bukrs, p_gjahr, selectedHkont.getHkont());
			if (fgl_list.size()==0)
			{
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:table1");
				throw new DAOException("Не найдено.");
			}
			
			for(int x = 0; x < 14; x = x+1) 
			{			
				Faglb03 wa_faglb03 = new Faglb03();
				if (x==0){ 
					wa_faglb03.setMonatName("На начало");
				}
				else if (x==13){ 
					wa_faglb03.setMonatName("Итого");
				}
				else{
					wa_faglb03.setMonatName(Integer.toString(x));
				}
				
				wa_faglb03.setMonat(x);
				faglb03_list.add(wa_faglb03);
		    }
			
			for (Fmglflext wa_fgl:fgl_list)
			{
				for (Faglb03 wa_faglb03:faglb03_list){
					if (wa_faglb03.getMonat()==0)
					{
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getBeg_amount());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getBeg_amount());}
					}
					else if (wa_faglb03.getMonat()==1){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth1());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth1());}
					}
					else if (wa_faglb03.getMonat()==2){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth2());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth2());}
					}
					else if (wa_faglb03.getMonat()==3){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth3());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth3());}
					}
					else if (wa_faglb03.getMonat()==4){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth4());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth4());}
					}
					else if (wa_faglb03.getMonat()==5){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth5());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth5());}
					}
					else if (wa_faglb03.getMonat()==6){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth6());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth6());}
					}
					else if (wa_faglb03.getMonat()==7){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth7());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth7());}
					}
					else if (wa_faglb03.getMonat()==8){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth8());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth8());}
					}
					else if (wa_faglb03.getMonat()==9){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth9());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth9());}
					}
					else if (wa_faglb03.getMonat()==10){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth10());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth10());}
					}
					else if (wa_faglb03.getMonat()==11){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth11());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth11());}
					}
					else if (wa_faglb03.getMonat()==12){
						if (wa_fgl.getDrcrk().equals("H")){wa_faglb03.setCredit(wa_fgl.getMonth12());}
						if (wa_fgl.getDrcrk().equals("S")){wa_faglb03.setDebet(wa_fgl.getMonth12());}
					} 
						
				}				
				 
			}
			double tmp_debet = 0;
			double tmp_credit = 0;
			for (Faglb03 wa_faglb03:faglb03_list){
				wa_faglb03.setBukrs(p_bukrs);
				if (selectedHkont.getWaers()== null || selectedHkont.getWaers().isEmpty() || selectedHkont.getWaers().equals("USD"))
				{
					wa_faglb03.setCurrency("USD");
				}
				else
				{
					wa_faglb03.setCurrency(selectedHkont.getWaers());
				}
				wa_faglb03.setGjahr(p_gjahr);
				wa_faglb03.setGlaccount(selectedHkont.getHkont()); 
				tmp_debet = tmp_debet + wa_faglb03.getDebet();
				tmp_credit = tmp_credit + wa_faglb03.getCredit();
			}
			for (Faglb03 wa_faglb03:faglb03_list){
				if (wa_faglb03.getMonat()==13)
				{
					if (tmp_debet>tmp_credit)
					{
						wa_faglb03.setDebet(tmp_debet-tmp_credit);
					}
					else if (tmp_credit>tmp_debet)
					{
						wa_faglb03.setCredit(tmp_credit-tmp_debet);
					}
				} 
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:table1");
		}
		catch (DAOException ex)
		{
			addMessage("Инфо",ex.getMessage());  
		}
		
	}

	public List<Faglb03> getFaglb03_list(){
		return faglb03_list;
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
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	//*****************************************************************	
	public class Faglb03 {
		String monatName;	
		private int monat;
		private int gjahr;
		private String bukrs;
		private String glaccount;
		private String currency;
		private double debet;
		private double credit;
		
		public String getMonatName() {
			return monatName;
		}
		public void setMonatName(String monatName) {
			this.monatName = monatName;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public String getGlaccount() {
			return glaccount;
		}
		public void setGlaccount(String glaccount) {
			this.glaccount = glaccount;
		}
		public double getDebet() {
			return debet;
		}
		public void setDebet(double debet) {
			this.debet = debet;
		}
		public double getCredit() {
			return credit;
		}
		public void setCredit(double credit) {
			this.credit = credit;
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
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
	}
	
	public void changeBukrs()
	{
		hkont_list2.clear();
		hkont_list.clear();
		hkont_filter = "";
		for(Hkont wa_hkont:p_hkontF4Bean.getHkont_list())
		{
			if (wa_hkont.getBukrs().equals(p_bukrs))
			{
				hkont_list.add(wa_hkont);
				hkont_list2.add(wa_hkont);
			}
		}
		

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:singleHkont");
		reqCtx.update("form:hkont_filter");
		 
	}
	public void filter_hkont()
	{
		hkont_list.clear();
		if (hkont_filter!=null && hkont_filter.length()>0)
		{
			for(Hkont wa_hkont:hkont_list2)
			{
				if (wa_hkont.getHkont().startsWith(hkont_filter))
				{
					hkont_list.add(wa_hkont);
				}
			}
		}
		else
		{
			for(Hkont wa_hkont:hkont_list2)
			{
				hkont_list.add(wa_hkont);
			}
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:singleHkont");
	}
}
