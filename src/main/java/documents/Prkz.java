package documents;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import dit.message.UserStaff;
import f4.BranchF4;
import general.AppContext;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.UserDao;
import general.dao2.AgreementDao;
import general.dao2.PrikazAttachDao;
import general.dao2.PrikazDao;
import general.springservice.PrikazSpSer;
import general.tables2.Agreement;
import general.tables2.MessageAttach;
import general.tables2.MessageHeader;
import general.tables2.Prikaz;
import general.tables2.PrikazAttach;
import user.User;

@ManagedBean(name = "prikazBean", eager = true)
@ViewScoped
public class Prkz implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "PRIKAZ";
	private final static Long transaction_id = (long) 698;
	//private final static Long read = (long) 1;
	//private final static Long write = (long) 2; 
	public static Long getTransactionId() {
		return transaction_id;
	}
	//******************************************************************
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
	//***************************User session***************************
	@ManagedProperty(value="#{branchF4Bean}")
	private BranchF4 branchF4Bean;	
	public BranchF4 getBranchF4Bean() {
		return branchF4Bean;
	}

	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}

	//******************************************************************
	private List<PrikazAttach> l_pa = new ArrayList<PrikazAttach>();
	private List<Agreement> l_a = new ArrayList<Agreement>();
	Map<Long,Outputtable> prikaz_map = new HashMap<Long,Outputtable>();	
	private Map<Long,List<Outputtable>> l_prikaz_map = new HashMap<Long,List<Outputtable>>();
	private List<Outputtable> l_prikaz_to_agree = new ArrayList<Outputtable>();
	private Map<Long,List<PrikazAttach>> l_prikaz_attach_map = new HashMap<Long,List<PrikazAttach>>();
	private Map<Long,List<Outputtable>> l_prikaz_child_map = new HashMap<Long,List<Outputtable>>();
	private Outputtable sel_prikaz = new Outputtable();
	private Map<Long,List<Agreement>> l_agreement_map = new HashMap<Long,List<Agreement>>();
	private String tabName="";
	
	

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public List<Outputtable> getL_prikaz_to_agree() {
		return l_prikaz_to_agree;
	}

	public void setL_prikaz_to_agree(List<Outputtable> l_prikaz_to_agree) {
		this.l_prikaz_to_agree = l_prikaz_to_agree;
	}

	//******************************************************************	
	@PostConstruct
	public void init() {		
		try {				
			
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Prkz.transaction_id);
			UserDao ud = (UserDao) appContext.getContext().getBean("userDao");
			List<Object[]> results = new ArrayList<Object[]>();
			results = ud.getUsernameFio(" and u.is_active=1");
			int count = 0;
			for (Object[] wa_result:results)
			{
				count++;
				//a2.text45 city,a2.waers,a2.hkont,a2.usd,a2.kzt,a2.uzs,a2.kgs,a2.azn,a2.rn,s2.text45 hkontname
				UserStaff wa_out = new UserStaff();
				if (wa_result[0]!=null) wa_out.setUser_id(Long.valueOf(String.valueOf(wa_result[0])));
				
				if (wa_result[1]!=null){ 
					wa_out.setUsername(String.valueOf(wa_result[1]));
				}
				if (wa_result[2]!=null){
					wa_out.setUserFio(String.valueOf(wa_result[2]));
				}
				wa_out.setFilteredname(wa_out.getUsername()+" "+wa_out.getUserFio());
				wa_out.setId(count);
				l_userstaff.add(wa_out);
				l_userstaff2.add(wa_out);
				l_userstaff_map.put(wa_out.getUser_id(), wa_out);
			}
			tabName="Просмотр";
			Long id_prikaz = sel_prikaz.getId_prikaz();
			try {
				loadList();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (id_prikaz!=null && id_prikaz>0L)
			{
				prepareLook(id_prikaz);
			}
			
		}
		catch (DAOException ex)
		{
			toMainPage();
		}
	}
	//******************************************************************
	public void loadList() throws ParseException
	{
		Calendar curDate = Calendar.getInstance();
		clearAll();
		
		sel_prikaz.setDate_prikaz(curDate.getTime());
		sel_prikaz.setCreator_id(userData.getUserid());
		
		
		PrikazDao prikazDao = (PrikazDao) appContext.getContext().getBean("prikazDao");
		List<Object[]> results = prikazDao.findListByUser(userData.getUserid(),"0,1,3","");
		List<Object[]> results2 = prikazDao.findListByUser(userData.getUserid(),"2",searchModel.getCondition());
		List<Object[]> results3 = prikazDao.findListByUserReadyToAgree(userData.getUserid());
		results.addAll(results2);
		if (results!=null && results.size()>0)
		{
			
			for (Object[] result : results) {
				Outputtable wa_out = new Outputtable();
//						p.id_prikaz,"
//						+" p.type_prikaz,"
//						+" p.date_prikaz,"
//						+" p.name_prikaz,"
//						+" p.bukrs,"
//						+" p.position_id,"
//						+" p.status_id,"
//						+" p.parent_id_prikaz,"
//						+" p.creator_id,"
//						+" p.version,"
//						+" p.code,"
//						+" p.branch_id,"
//						+" pa.prikaz_attach_id,"
//						+" pa.prikaz_attach,"
//						+" pa.name,"
//						pa.ext
				if (result[0]!=null) wa_out.setId_prikaz(Long.parseLong(String.valueOf(result[0])));
				if (result[1]!=null) wa_out.setType_prikaz(Integer.parseInt(String.valueOf(result[1])));
				if (result[2]!=null){
					wa_out.setDate_prikaz_str(String.valueOf(result[2]));
					wa_out.setDate_prikaz(new SimpleDateFormat("dd.MM.yyyy").parse(String.valueOf(result[2])));
				}
				
				
				if (result[3]!=null) wa_out.setName_prikaz(String.valueOf(result[3]));
				if (result[4]!=null) wa_out.setBukrs(String.valueOf(result[4]));
				if (result[5]!=null) wa_out.setPosition_id(Long.parseLong(String.valueOf(result[	5])));
				if (result[6]!=null) wa_out.setStatus_id(Integer.parseInt(String.valueOf(result[6])));
				if (result[7]!=null) wa_out.setParent_id_prikaz(Long.parseLong(String.valueOf(result[7])));
				if (result[8]!=null) wa_out.setCreator_id(Long.parseLong(String.valueOf(result[8])));
				if (result[9]!=null) wa_out.setVersion(Integer.parseInt(String.valueOf(result[9])));
				if (result[10]!=null) wa_out.setCode(String.valueOf(result[10]));
				if (result[11]!=null) wa_out.setBranch_id(Long.parseLong(String.valueOf(result[11])));
				if (result[12]!=null) wa_out.setDepartment_id(Integer.parseInt(String.valueOf(result[12])));
				
				if(wa_out.getBukrs()!=null && wa_out.getBukrs().equals("1000"))
				{
					wa_out.setBukrs_name("AURA");
				}
				else
					wa_out.setBukrs_name("GREENLIGHT");
				
				if (wa_out.getBranch_id()!=null && wa_out.getBranch_id()>0L)
				{
					wa_out.setBranch_name(branchF4Bean.getL_branch_map().get(wa_out.getBranch_id()).getText45());					
				}
				else if (wa_out.getBranch_id()!=null && wa_out.getBranch_id()==0L)
				{
					wa_out.setBranch_name("Все");					
				}
				prikaz_map.put(wa_out.id_prikaz, wa_out);
				
				if (wa_out.getParent_id_prikaz()!=null && wa_out.getParent_id_prikaz()>0L)
				{
					List<Outputtable> wal_prikaz = new ArrayList<Outputtable>();
					wal_prikaz = l_prikaz_child_map.get(wa_out.getParent_id_prikaz());
					if (wal_prikaz==null || wal_prikaz.size()==0)
					{
						wal_prikaz = new ArrayList<Outputtable>();
						wal_prikaz.add(wa_out);
						l_prikaz_child_map.put(wa_out.getParent_id_prikaz(), wal_prikaz);
					}
					else
					{
						wal_prikaz.add(wa_out);
					}
					
					continue;
				}
					
				//org.springframework.beans.BeanUtils.copyProperties(wa_prikaz, wa_out);
//				l_prikaz.add(wa_out);
				
				List<Outputtable> wal_prikaz = new ArrayList<Outputtable>();
				Long wa_status_id = (long) wa_out.getStatus_id();
				wal_prikaz = l_prikaz_map.get(wa_status_id);
				if (wal_prikaz==null || wal_prikaz.size()==0)
				{
					wal_prikaz = new ArrayList<Outputtable>();
					wal_prikaz.add(wa_out);
					l_prikaz_map.put(wa_status_id, wal_prikaz);
				}
				else
				{
					wal_prikaz.add(wa_out);
				}
				
				
	    	}
			
		}
		if (results3!=null && results3.size()>0)
		{
			
			for (Object[] result : results3) {
				Outputtable wa_out = new Outputtable();
//						p.id_prikaz,"
//						+" p.type_prikaz,"
//						+" p.date_prikaz,"
//						+" p.name_prikaz,"
//						+" p.bukrs,"
//						+" p.position_id,"
//						+" p.status_id,"
//						+" p.parent_id_prikaz,"
//						+" p.creator_id,"
//						+" p.version,"
//						+" p.code,"
//						+" p.branch_id,"
//						+" pa.prikaz_attach_id,"
//						+" pa.prikaz_attach,"
//						+" pa.name,"
//						pa.ext
				if (result[0]!=null) wa_out.setId_prikaz(Long.parseLong(String.valueOf(result[0])));
				if (result[1]!=null) wa_out.setType_prikaz(Integer.parseInt(String.valueOf(result[1])));
				if (result[2]!=null){
					wa_out.setDate_prikaz_str(String.valueOf(result[2]));
					wa_out.setDate_prikaz(new SimpleDateFormat("dd.MM.yyyy").parse(String.valueOf(result[2])));
				}
				
				
				if (result[3]!=null) wa_out.setName_prikaz(String.valueOf(result[3]));
				if (result[4]!=null) wa_out.setBukrs(String.valueOf(result[4]));
				if (result[5]!=null) wa_out.setPosition_id(Long.parseLong(String.valueOf(result[	5])));
				if (result[6]!=null) wa_out.setStatus_id(Integer.parseInt(String.valueOf(result[6])));
				if (result[7]!=null) wa_out.setParent_id_prikaz(Long.parseLong(String.valueOf(result[7])));
				if (result[8]!=null) wa_out.setCreator_id(Long.parseLong(String.valueOf(result[8])));
				if (result[9]!=null) wa_out.setVersion(Integer.parseInt(String.valueOf(result[9])));
				if (result[10]!=null) wa_out.setCode(String.valueOf(result[10]));
				if (result[11]!=null) wa_out.setBranch_id(Long.parseLong(String.valueOf(result[11])));
				if (result[12]!=null) wa_out.setDepartment_id(Integer.parseInt(String.valueOf(result[12])));
				
				if(wa_out.getBukrs()!=null && wa_out.getBukrs().equals("1000"))
				{
					wa_out.setBukrs_name("AURA");
				}
				else
					wa_out.setBukrs_name("GREENLIGHT");
				
				if (wa_out.getBranch_id()!=null && wa_out.getBranch_id()>0L)
				{
					wa_out.setBranch_name(branchF4Bean.getL_branch_map().get(wa_out.getBranch_id()).getText45());					
				}
				else if (wa_out.getBranch_id()!=null && wa_out.getBranch_id()==0L)
				{
					wa_out.setBranch_name("Все");					
				}
				prikaz_map.put(wa_out.id_prikaz, wa_out);
				l_prikaz_to_agree.add(wa_out);
			}	
		}	
		
		
		//prikazDao.find()
		for (Map.Entry<Long,List<Outputtable>> entry : l_prikaz_child_map.entrySet())
		{
			Outputtable wa_prikaz = new Outputtable();
			wa_prikaz = prikaz_map.get(entry.getKey());
			if (wa_prikaz!=null && wa_prikaz.getId_prikaz()!=null)
			{
				wa_prikaz.setCountChild(wa_prikaz.getCountChild()+1);
			}
			
		}
		
		
		
		PrikazAttachDao prikazAttachDao = (PrikazAttachDao) appContext.getContext().getBean("prikazAttachDao");
		List<PrikazAttach> l_pa = prikazAttachDao.findListByUser(userData.getUserid(),"0,1,2,3");
		for(PrikazAttach wa_pa:l_pa)
		{
			List<PrikazAttach> wal_pa = new ArrayList<PrikazAttach>();
			wal_pa = l_prikaz_attach_map.get(wa_pa.getId_prikaz());
			if (wal_pa==null || wal_pa.size()==0)
			{
				wal_pa = new ArrayList<PrikazAttach>();
				wal_pa.add(wa_pa);
				l_prikaz_attach_map.put(wa_pa.getId_prikaz(), wal_pa);
			}
			else
			{
				wal_pa.add(wa_pa);
			}
		}
		
		AgreementDao agreementDao = (AgreementDao) appContext.getContext().getBean("agreementDao");
		l_a = agreementDao.findListByUser(userData.getUserid(),"0,1,2,3");
		l_a.addAll(agreementDao.findListByUserReadyToAgree(userData.getUserid()));
		Map<Long,Agreement> agreement_map = new HashMap<Long,Agreement>();
		
		for(Agreement wa_a:l_a)
		{
			agreement_map.put(wa_a.getAgree_id(), wa_a);			
		}
		
		for (Map.Entry<Long,Agreement> entry : agreement_map.entrySet())
		{
			Agreement wa_a = (Agreement) entry.getValue();
			List<Agreement> wal_a = new ArrayList<Agreement>();
			wal_a = l_agreement_map.get( wa_a.getContext_id());
			if (wal_a==null || wal_a.size()==0)
			{
				wal_a = new ArrayList<Agreement>();
				wal_a.add(wa_a);
				l_agreement_map.put(wa_a.getContext_id(), wal_a);
			}
			else
			{
				wal_a.add(wa_a);
			}
		}
		l_a = new ArrayList<Agreement>();
		tabindex = 0;
	}
	
	public List<Outputtable> getList(Long a_status_id)
	{
		return l_prikaz_map.get(a_status_id);
	}
	//******************************************************************	
	public void toMainPage() {
		try {

			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//	   	 	context.redirect(context.getRequestContextPath() +  "/general/mainPage.xhtml");
		}
		catch (Exception ex)
		{
			 
			addMessage("Info",ex.getMessage());  
		}
	}
	//******************************************************************	
	//******************************************************************
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");
    }
	//******************************************************************
	//******************************************************************
	
	

	
//	
	
	
	
	public Map<Long, List<PrikazAttach>> getL_prikaz_attach_map() {
		return l_prikaz_attach_map;
	}

	
	public List<Agreement> getL_a() {
		return l_a;
	}

	public void setL_a(List<Agreement> l_a) {
		this.l_a = l_a;
	}

	public void setL_prikaz_attach_map(Map<Long, List<PrikazAttach>> l_prikaz_attach_map) {
		this.l_prikaz_attach_map = l_prikaz_attach_map;
	}
	
	
	public Map<Long, List<Outputtable>> getL_prikaz_child_map() {
		return l_prikaz_child_map;
	}

	public void setL_prikaz_child_map(Map<Long, List<Outputtable>> l_prikaz_child_map) {
		this.l_prikaz_child_map = l_prikaz_child_map;
	}
		
	public Outputtable getSel_prikaz() {
		return sel_prikaz;
	}

	public void setSel_prikaz(Outputtable sel_prikaz) {
		this.sel_prikaz = sel_prikaz;
	}	
	
	public List<PrikazAttach> getL_pa() {
		return l_pa;
	}

	public void setL_pa(List<PrikazAttach> l_pa) {
		this.l_pa = l_pa;
	}
	

	
	//******************************************************************
	public List<Outputtable> getChild (Long a_id)
	{
		return l_prikaz_child_map.get(a_id);
	}
	public List<Agreement> getAgreementList (Long a_id)
	{
		
		return l_agreement_map.get(a_id);
	}
	public List<PrikazAttach> getPrikazAttachList ()
	{
		return l_prikaz_attach_map.get(sel_prikaz.getId_prikaz());
	}
	
	
	
	
	public void prepareCreate ()
	{
		tabName="Создание";
		prosmotr = false;
		tabindex = 4;
		sel_prikaz = new Outputtable();
		l_pa = new ArrayList<PrikazAttach>();
		l_a = new ArrayList<Agreement>();
		sel_prikaz.setCreator_id(userData.getUserid());
		sel_prikaz.setCreatorName(getUserName(String.valueOf(sel_prikaz.getCreator_id())));
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView");
	}
	public void prepareChange (Long a_id_prikaz)
	{
		tabName="Изменение";
		prosmotr = false;
		sel_prikaz = prikaz_map.get(a_id_prikaz);
		sel_prikaz.setCreatorName(getUserName(String.valueOf(sel_prikaz.getCreator_id())));
		l_pa = l_prikaz_attach_map.get(a_id_prikaz);
		l_a = getAgreementList(a_id_prikaz);
		if (l_pa==null)
		{
			l_pa = new ArrayList<PrikazAttach>();
		}
		if (l_a==null)
		{
			l_a = new ArrayList<Agreement>();
		}
		tabindex = 4;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView");
		reqCtx.update("form:tabView:agreementDataList");
	}
	public void prepareLook (Long a_id_prikaz)
	{
		tabName="Просмотр";
		prosmotr = true;
		sel_prikaz = prikaz_map.get(a_id_prikaz);
		sel_prikaz.setCreatorName(getUserName(String.valueOf(sel_prikaz.getCreator_id())));
		l_pa = l_prikaz_attach_map.get(a_id_prikaz);
		l_a = getAgreementList(a_id_prikaz);
		if (l_pa==null)
		{
			l_pa = new ArrayList<PrikazAttach>();
		}
		if (l_a==null)
		{
			l_a = new ArrayList<Agreement>();
		}
		tabindex = 4;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView");
	}
	
	
	private int tabindex = 0;
  	public int getTabindex() {
		return tabindex;
	}
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	
	
	//******************************************************************
	public void save()
	{
		try {
			PrikazSpSer prikazService = (PrikazSpSer) appContext.getContext().getBean("prikazService");
			Prikaz new_prikaz = new Prikaz();
			org.springframework.beans.BeanUtils.copyProperties(sel_prikaz, new_prikaz);
			new_prikaz.setCreator_id(userData.getUserid());
			
			if (sel_prikaz.getId_prikaz()!=null && sel_prikaz.getId_prikaz()>0L)
			{
				prikazService.modifyPrikaz(new_prikaz,l_pa,l_a);
			}
			else
				prikazService.createPrikaz(new_prikaz,l_pa,l_a);
			
			
			sel_prikaz = new Outputtable();
			l_pa = new ArrayList<PrikazAttach>();
			loadList();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
		    reqCtx.update("form:tabView");
//		    
//		   
//		    reqCtx.update("form:tabView:datalist");
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
		}
		
	}
	
	public class Outputtable {
		private Long id_prikaz;
		private Long branch_id;
		private String branch_name;
		private Long position_id;
		private String position_name;
		private int status_id;
		private String status_name;
		private int version;
		private String bukrs;
		private String code;
		private Date date_prikaz;
		private String date_prikaz_str;
		private String name_prikaz;
		private Long creator_id;
		private Long parent_id_prikaz;
		private int type_prikaz;
		private int countChild = 0;
		private int department_id;
		private String creatorName;
		private List<String> selectedBranches = new ArrayList<String>();
		private String bukrs_name;
		
		public String getBukrs_name() {
			return bukrs_name;
		}
		public void setBukrs_name(String bukrs_name) {
			this.bukrs_name = bukrs_name;
		}
		public int getDepartment_id() {
			return department_id;
		}
		public void setDepartment_id(int department_id) {
			this.department_id = department_id;
		}



		public String getCondition() {
			String cond = "";
			
			if (!Validation.isEmptyString(bukrs)) {
				cond += " bukrs = '" + bukrs + "' ";
			}
			
			
			if (!Validation.isEmptyString(name_prikaz)) {
				cond += (cond.length() > 0 ? " AND " : " ") + " name_prikaz LIKE '%" + name_prikaz + "%'";
			}
			
			if (department_id!=0) {
				cond += (cond.length() > 0 ? " AND " : " ") + " department_id = " + department_id;
			}
			
			if (type_prikaz!=0) {
				cond += (cond.length() > 0 ? " AND " : " ") + " type_prikaz = " + type_prikaz;
			}
			
			if (cond.length() > 0)
			{
				cond = " and "+ cond;
			}
			
			String br = "";
			if (selectedBranches.size()>0)
			{
				int count = 0;
				for (String wa_br_id:selectedBranches)
				{
					count++;
					if (count==1)
					{
						br = br + " and p.branch_id in ("+wa_br_id;
					}
					else
					{
						br = br + ","+wa_br_id;
					}
					if(wa_br_id.equals(0L))
					{
						count = 0;
						br = "";
						break;
					}
					
				}
				br = br + ")";
				if (count>0)
				{
					cond = cond + br;
				}
			}
			
			return cond;

		}
		


		public int getCountChild() {
			return countChild;
		}


		public void setCountChild(int countChild) {
			this.countChild = countChild;
		}


		public String getDate_prikaz_str() {
			return date_prikaz_str;
		}


		public void setDate_prikaz_str(String date_prikaz_str) {
			this.date_prikaz_str = date_prikaz_str;
		}


		public Outputtable()
		{
			this.status_id=0;
			this.version=1;
		}
		
		
		public Long getCreator_id() {
			return creator_id;
		}
		public void setCreator_id(Long creator_id) {
			this.creator_id = creator_id;
		}
		public Long getParent_id_prikaz() {
			return parent_id_prikaz;
		}
		public void setParent_id_prikaz(Long parent_id_prikaz) {
			this.parent_id_prikaz = parent_id_prikaz;
		}
		public int getType_prikaz() {
			return type_prikaz;
		}
		public void setType_prikaz(int type_prikaz) {
			this.type_prikaz = type_prikaz;
		}
		public Date getDate_prikaz() {
			return date_prikaz;
		}
		public void setDate_prikaz(Date date_prikaz) {
			this.date_prikaz = date_prikaz;
		}
		public String getName_prikaz() {
			return name_prikaz;
		}
		public void setName_prikaz(String name_prikaz) {
			this.name_prikaz = name_prikaz;
		}
		public Long getId_prikaz() {
			return id_prikaz;
		}
		public void setId_prikaz(Long id_prikaz) {
			this.id_prikaz = id_prikaz;
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
		public Long getPosition_id() {
			return position_id;
		}
		public void setPosition_id(Long position_id) {
			this.position_id = position_id;
		}
		public String getPosition_name() {
			return position_name;
		}
		public void setPosition_name(String position_name) {
			this.position_name = position_name;
		}
		public int getStatus_id() {
			return status_id;
		}
		public void setStatus_id(int status_id) {
			this.status_id = status_id;
		}
		public String getStatus_name() {
			return status_name;
		}
		public void setStatus_name(String status_name) {
			this.status_name = status_name;
		}
	
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public int getVersion() {
			return version;
		}
		public void setVersion(int version) {
			this.version = version;
		}
		public List<String> getSelectedBranches() {
			return selectedBranches;
		}
		public void setSelectedBranches(List<String> selectedBranches) {
			this.selectedBranches = selectedBranches;
		}
		public String getCreatorName() {
			return creatorName;
		}
		public void setCreatorName(String creatorName) {
			this.creatorName = creatorName;
		}
		
		
		
	}
	
	public void changeBukrs()
	{
		sel_prikaz.setBranch_id(0L);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView:branch");
		
//		p_bkpf.setWaers(null);
//		p_bkpf.setKursf(0);
//		l_hkont.clear();
//		clearAndAddBseg();
//		RequestContext reqCtx = RequestContext.getCurrentInstance();
//		reqCtx.update("form");
	}
	public void searchChangeBukrs()
	{
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tabView:se_branch");
		
//		p_bkpf.setWaers(null);
//		p_bkpf.setKursf(0);
//		l_hkont.clear();
//		clearAndAddBseg();
//		RequestContext reqCtx = RequestContext.getCurrentInstance();
//		reqCtx.update("form");
	}


	public void handleFileUpload(FileUploadEvent event) throws IOException, SerialException, SQLException {
		if (l_pa==null || l_pa.size()<3)
		{
			PrikazAttach wa_pa = new PrikazAttach();
			InputStream a_in = event.getFile().getInputstream();
			byte[] bytes = IOUtils.toByteArray(a_in);
			wa_pa.setPrikaz_attach(new SerialBlob(bytes));
			wa_pa.setExt(event.getFile().getFileName());
			wa_pa.setName(wa_pa.getExt().substring(0,wa_pa.getExt().lastIndexOf(".")));
			wa_pa.setExt(wa_pa.getExt().substring(wa_pa.getExt().lastIndexOf(".")+1));
			l_pa.add(wa_pa);
		
			FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
	    	FacesContext.getCurrentInstance().addMessage(null, message);
	    	RequestContext reqCtx = RequestContext.getCurrentInstance();
	    	reqCtx.update("form:tabView:attachTable");
		}
    }
	public void removeFile(String a_name) {
		List<PrikazAttach> l_ma2 = new ArrayList<PrikazAttach>();
		
		for(PrikazAttach wa:l_pa)
		{
			if (!a_name.equals(wa.getName()))
			{
				l_ma2.add(wa);
			}
		}
		l_pa = new ArrayList<PrikazAttach>();
		l_pa.addAll(l_ma2);
	    RequestContext reqCtx = RequestContext.getCurrentInstance();
	    reqCtx.update("form:tabView:attachTable");
	        
	}
		
	public StreamedContent download(String a_filename) 
	{
	    try {
	           
	       	for(PrikazAttach wa:l_pa)
	   		{
	   			if (a_filename.equals(wa.getExt()))
	   			{
	   				
	   				String name = wa.getExt().substring(wa.getExt().lastIndexOf( '.' ));
	   				name = "temp"+name;
	   				InputStream in = wa.getPrikaz_attach().getBinaryStream();
	   		        StreamedContent file = new DefaultStreamedContent(in, "image/jpg", name);
	    		    
//	   		     try (Stream<String> lines = Files.lines(file)) {
//	   		      List<String> replaced = lines
//	   		          .map(line-> line.replaceAll(plainTextPattern, replaceWith))
//	   		          .collect(Collectors.toList());
//	   		      Files.write(targetFile, replaced);
//	   		   }
	   		        return file;
	   			}
	   		}
	          


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		return null;
	    
	    
	}
	
	private boolean prosmotr = false;
	
	public boolean isProsmotr() {
		return prosmotr;
	}

	public void setProsmotr(boolean prosmotr) {
		this.prosmotr = prosmotr;
	}

	public void downloadWord(Long a_prikaz_attach_id)
	{
		try{
//		            ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
//		            Zreport wa_zr = zreportService.getFile(a_rep_id);
//		            //changing blob to inputstream
//		            InputStream in = wa_zr.getFileu().getBinaryStream();
		            
//		            //changing inputstream to HSSFWorkbook
//		            HSSFWorkbook wb = new HSSFWorkbook(in);
//		            HSSFSheet sheet = wb.getSheetAt(0);
//		                     
//		            HSSFCellStyle cellStyle = wb.createCellStyle();  
//		            cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
//		            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//		            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//		                       
//		                    
//		            for(int i=0; i < 30;i++) 
//		            {
//		            	HSSFRow row = sheet.getRow(i);
//		            	if (row!=null)
//		            	{
//		            		for(int i2=0; i2 < 4;i2++) 
//		                	{
//		                		HSSFCell cell = row.getCell(i2);
//		                		if (cell!=null)
//		                		{
//		                			String val=cell.getStringCellValue();
//		                			if (val.length()>0)
//		                			{
////		                				System.out.println("Row:"+i+" Cell:"+i2);
////		                				val = val.replaceAll("\u00A0", "");                        		
//		                				val = val.replace("#ofis_from#",ofis_from);
//		                				val = val.replace("#sn_new#",String.valueOf(a_conNum));
//		                				val = val.replace("#condate_new#",String.valueOf(condate_new));
//		                				val = val.replace("#clientfio_new#",clientfio_new);
//		                				val = val.replace("#dealerfio_new#",dealerfio_new);
//		                				val = val.replace("#ofis_to#",ofis_to);
//		                				val = val.replace("#sn_rec#",String.valueOf(a_refConNum));
//		                				val = val.replace("#condate_rec#",String.valueOf(condate_rec));
//		                				val = val.replace("#clientfio_rec#",clientfio_rec);
//		                				val = val.replace("#dealerfio_rec#",dealerfio_rec);
//		                				val = val.replace("#paydate#",String.valueOf(paydate));
//		                				val = val.replace("#summa#",a_summa+" "+a_waers);
////		                				System.out.println(val);
//		                				
//		                				cell.setCellValue(val);
//		                			}
//		                			
//
//		                		}
//		                    
//		                	}
//		            	}                	
//		                        
//		            }
//		           
			PrikazAttach wa_temp = new PrikazAttach();
		    for(PrikazAttach wa_pa:l_pa)
		    {
		      	if (wa_pa.getPrikaz_attach_id().equals(a_prikaz_attach_id))
		       	{
		      		wa_temp = wa_pa;
		      		break;
		       	}
		    }
		    
		    InputStream in = wa_temp.getPrikaz_attach().getBinaryStream();
		    XWPFDocument doc = new XWPFDocument(in);
		   
		    for(XWPFHeader header:doc.getHeaderList())
		    {
		    	for (XWPFTable tbl : header.getTables()) {
		    		for (XWPFTableRow row : tbl.getRows()) {
			    		 for (XWPFTableCell cell : row.getTableCells()) {
			    			 for (XWPFParagraph p : cell.getParagraphs()) {
			    				 for (XWPFRun r : p.getRuns()) {
			    		             String text = r.getText(0);
			    		             if (text != null && text.contains("#date#")) {
			 		                	text = text.replace("#date#", sel_prikaz.getDate_prikaz_str());
			 		                    r.setText(text, 0);
			 		                }if (text != null && text.contains("#version#")) {
			 		                	text = text.replace("#version#", String.valueOf(sel_prikaz.getVersion()));
			 		                    r.setText(text, 0);
			 		                }
			                     }
			                 }
			             }
			         }
		    	}
		    }
		    
		    for(XWPFFooter footer:doc.getFooterList())
		    {
		    	for (XWPFTable tbl : footer.getTables()) {
		    		for (XWPFTableRow row : tbl.getRows()) {
			    		 for (XWPFTableCell cell : row.getTableCells()) {
			    			 for (XWPFParagraph p : cell.getParagraphs()) {
			    				 for (XWPFRun r : p.getRuns()) {
			    		             String text = r.getText(0);
			    		             if (text != null && text.contains("#zzz#")) {
				 		                	text = text.replace("#zzz#", "DONE");
				 		                    r.setText(text, 0);
				 		                }
			                     }
			                 }
			             }
			         }
		    	}
		    }
		            
		            
		     //calling servlet to download
		     String contentType = "application/vnd.ms-word";
		     FacesContext fc = FacesContext.getCurrentInstance();                     
		     HttpServletResponse response = (HttpServletResponse)fc.getExternalContext().getResponse();
		     HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
		     
		     
		     String userAgent = request.getHeader("user-agent");
		     boolean isInternetExplorer = (userAgent.indexOf("MSIE") > -1);		     
		     byte[] fileNameBytes = wa_temp.getName().getBytes((isInternetExplorer) ? ("windows-1250") : ("utf-8"));
		     String dispositionFileName = "";
		     for (byte b: fileNameBytes) dispositionFileName += (char)(b & 0xff);
		     
		     response.setHeader("Content-disposition", "attachment; filename=" + dispositionFileName+"."+wa_temp.getExt());
		     response.setContentType(contentType);
		                    
		     //writing excel to outputstream
		     ServletOutputStream out = response.getOutputStream(); 
		     doc.write(out); 
		            
		     //flushing and closing
		     out.flush(); 
		     out.close(); 
		     fc.responseComplete();

		}
		catch(Exception e)
		{
			addMessage("Info",e.getMessage());
		}
	}
	public void clearAll()
	{
		//File Handle
		l_pa = new ArrayList<PrikazAttach>();
//		l_prikaz = new ArrayList<Outputtable>();
		l_agreement_map = new HashMap<Long,List<Agreement>>();;
		l_a = new ArrayList<Agreement>();
		l_prikaz_map = new HashMap<Long,List<Outputtable>>();
		l_prikaz_to_agree = new ArrayList<Outputtable>();
		prikaz_map = new HashMap<Long,Outputtable>();
		l_prikaz_attach_map = new HashMap<Long,List<PrikazAttach>>();
		l_prikaz_child_map = new HashMap<Long,List<Outputtable>>();
		sel_prikaz = new Outputtable();
		tabindex = 0;
		
	}
	//******************************************************************
	// SEARCH MODEL CLASS
	private Outputtable searchModel = new Outputtable();

	public Outputtable getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(Outputtable searchModel) {
		this.searchModel = searchModel;
	}
	
	//*****************************************************************
	private List<UserStaff> l_userstaff = new ArrayList<UserStaff>();
	private Agreement temp_agree = new Agreement();
	
	
	public Agreement getTemp_agree() {
		return temp_agree;
	}

	public void setTemp_agree(Agreement temp_agree) {
		this.temp_agree = temp_agree;
	}

	public List<UserStaff> getL_userstaff() {
		return l_userstaff;
	}
	public void setL_userstaff(List<UserStaff> l_userstaff) {
		this.l_userstaff = l_userstaff;
	}
	private List<UserStaff> l_userstaff2 = new ArrayList<UserStaff>();
	public List<UserStaff> getL_userstaff2() {
		return l_userstaff2;
	}
	public void setL_userstaff2(List<UserStaff> l_userstaff2) {
		this.l_userstaff2 = l_userstaff2;
	}
	
	private UserStaff sel_userstaff = new UserStaff();
	public UserStaff getSel_userstaff() {
		return sel_userstaff;
	}

	public void setSel_userstaff(UserStaff sel_userstaff) {
		this.sel_userstaff = sel_userstaff;
	}
	Map<Long,UserStaff> l_userstaff_map = new HashMap<Long,UserStaff>();
	public String getUserName (String a_id)
	{
		if (a_id.length()==0)
		{
			return "";
		}
		UserStaff wa = l_userstaff_map.get(Long.parseLong(a_id));
		if (wa==null)
		{
			return "";
		}
		return wa.getUserFio();
	}
	
	

	
	public void addAgreeData(Long a_id)
	{
		int count = 0;		
		int current = 1;
		Agreement wa_a = new Agreement();		
		if (l_a==null || l_a.size()==0)
		{
			l_a = new ArrayList<Agreement>();
			wa_a.setStep(1);
			wa_a.setStatus_id(1);
			wa_a.setCurrent_a(current);
			current--;
			l_a.add(wa_a);
		}
		else
		{
			for(Agreement wa_a2:l_a)
			{
				count = wa_a2.getStep();
				if(wa_a.getStatus_id()==1 && current==1)
				{
					wa_a.setCurrent_a(current);
					current--;
				}
				else
				{
					wa_a.setCurrent_a(0);
				}
				
			}
			count++;
			wa_a.setStep(count);
			wa_a.setStatus_id(1);
			l_a.add(wa_a);
		}
			
		
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
    	reqCtx.update("form:tabView");
		
	}
	public void removeAgreeData(int a_step)
	{
		List<Agreement> l_a_temp = new ArrayList<Agreement>();
		int count = 0;
		int current = 1;
		for(Agreement wa_a:l_a)
		{
			if (wa_a.getStep()!=a_step)
			{
				count++;
				wa_a.setStep(count);
				if(wa_a.getStatus_id()==1 && current==1)
				{
					wa_a.setCurrent_a(current);
					current--;
				}
				else
				{
					wa_a.setCurrent_a(0);
				}
				l_a_temp.add(wa_a);				
			}
		}
		if (l_a_temp!=null)
		{
			l_a.clear();
			l_a.addAll(l_a_temp);
		}
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
    	reqCtx.update("form:tabView");
		
	}
	public void confirm(Long a_agree_id)
	{
		try{
			Agreement temp_a = new Agreement();
			Prikaz new_prikaz = new Prikaz();
			org.springframework.beans.BeanUtils.copyProperties(sel_prikaz, new_prikaz);
			for(Agreement wa_a:l_a)
			{
				if (wa_a.getAgree_id()==a_agree_id)
				{
					temp_a = wa_a; 				
				}
			}
			
			PrikazSpSer prikazService = (PrikazSpSer) appContext.getContext().getBean("prikazService");
			prikazService.confirmPrikaz(l_a,temp_a,new_prikaz);
			try {
				loadList();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:tabView");
			reqCtx.update("form:tabView:datalist");
			
		}
		catch (DAOException e)
		{
			addMessage("Info", e.getMessage());
		}
		
	}
	
	public void onRowReorder(ReorderEvent event) {
		List<Agreement> l_a_temp = new ArrayList<Agreement>();
		int count = 0;
		int current = 1;
		for(Agreement wa_a:l_a)
		{			
			count++;
			wa_a.setStep(count);
			if(wa_a.getStatus_id()==1 && current==1)
			{
				wa_a.setCurrent_a(current);
				current--;
			}
			else
			{
				wa_a.setCurrent_a(0);
			}
			
			l_a_temp.add(wa_a);	
		}
		if (l_a_temp.size()>0)
		{
			l_a.clear();
			l_a.addAll(l_a_temp);
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
    	reqCtx.update("form:tabView");
//        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Row Moved", "From: " + event.getFromIndex() + ", To:" + event.getToIndex());
//        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	public void onRowDblClckSelectUserStaff() {
		
		temp_agree.setUser_id(sel_userstaff.getUser_id());
		RequestContext reqCtx = RequestContext.getCurrentInstance();
    	reqCtx.execute("PF('agreeWidget').hide();");
    	reqCtx.update("form:tabView");
    }
	public void send()
	{
		try{
			
			
			PrikazSpSer prikazService = (PrikazSpSer) appContext.getContext().getBean("prikazService");
			Prikaz new_prikaz = new Prikaz();
			org.springframework.beans.BeanUtils.copyProperties(sel_prikaz, new_prikaz);
			
			
			Agreement wa_a = new Agreement();
			if (l_a!=null && l_a.size()>0)
			{
				wa_a = l_a.get(0);
			}
			
			
			if (sel_prikaz.getId_prikaz()!=null && sel_prikaz.getId_prikaz()>0L)
			{
				prikazService.sendPrikaz(new_prikaz,wa_a);
			}
			
			
			sel_prikaz = new Outputtable();
			l_pa = new ArrayList<PrikazAttach>();
			
	    
			loadList();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
		    reqCtx.update("form:tabView");
	    
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());
		}
	}

	
	
//	public void test()
//	{
//		System.out.println(1111);
//		
//		RequestContext reqCtx = RequestContext.getCurrentInstance();
//    	reqCtx.update("form:tabView:agreementDataList");
//		
//	}
	//******************************************************************
}
