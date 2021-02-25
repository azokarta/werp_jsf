package dit.message;


import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.UserDao;
import general.dao2.MessageGroupDao;
import general.dao2.MessageGroupUserDao;
import general.springservice.MessageSpSer;
import general.tables2.MessageGroup;
import general.tables2.MessageGroupUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;


@ManagedBean(name = "messGrUserListBean", eager = true)
@ViewScoped
public class MessGrUserListBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private final static String transaction_code = "MGRU";
	private final static Long transaction_id = (long) 617;
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
	//**********************Getter Setter for sessionData**********************
	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
		return userData;
	}
	public void setUserData(User userData) {
		this.userData = userData;
	}
	
	
	@PostConstruct
	public void init() {
		// TODO PERMISSION
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}
		
		if(!userData.isSys_admin()){
			GeneralUtil.doRedirect("/no_permission.xhtml");
		}
		PermissionController.canRead(userData, MessGrUserListBean.transaction_id);
		loadItems();
		
		MessageGroupDao d = (MessageGroupDao) appContext.getContext().getBean("messageGroupDao");
		l_mg = d.findAll("");
		for (MessageGroup wa_mg: l_mg)
		{
			l_mg_map.put(wa_mg.getGroup_id(), wa_mg.getGroup_name());
		}
		
		UserDao ud = (UserDao) appContext.getContext().getBean("userDao");
		List<Object[]> results = new ArrayList<Object[]>();
		results = ud.getUsernameFio(" and u.is_active=1");
		
		for (Object[] wa_result:results)
		{
			//a2.text45 city,a2.waers,a2.hkont,a2.usd,a2.kzt,a2.uzs,a2.kgs,a2.azn,a2.rn,s2.text45 hkontname
			UserStaff wa_out = new UserStaff();
			if (wa_result[0]!=null) wa_out.setUser_id(Long.valueOf(String.valueOf(wa_result[0])));
			
			if (wa_result[1]!=null){ 
				wa_out.setUsername(String.valueOf(wa_result[1]));
				wa_out.setFullName(wa_out.getUsername());
			}
			if (wa_result[2]!=null){
				wa_out.setUserFio(String.valueOf(wa_result[2]));
				wa_out.setFullName(wa_out.getFullName()+" - "+wa_out.getUserFio());
			}
			
			l_userstaff.add(wa_out);
			l_us_map.put(wa_out.getUser_id(), wa_out.getFullName());
			
		}
		
		
	}
	//******************************************************************
	//******************************************************************
	private void loadItems() {
		// System.out.println("LOADING...");
		MessageGroupUserDao d = (MessageGroupUserDao) appContext.getContext().getBean("messageGroupUserDao");
		items = d.findAll(searchModel.getCondition());
		selected = new MessageGroupUser();
		
	}
	
	
	
	public void Search() {
		loadItems();
	}
	
	
	private TransactionSearch searchModel = new TransactionSearch();

	public TransactionSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(TransactionSearch searchModel) {
		this.searchModel = searchModel;
	}
	
	// SEARCH MODEL CLASS
	public class TransactionSearch {
		private Long group_id;
		public Long getGroup_id() {
			return group_id;
		}

		public void setGroup_id(Long group_id) {
			this.group_id = group_id;
		}
		
		private Long user_id;
		public Long getUser_id() {
			return user_id;
		}

		public void setUser_id(Long user_id) {
			this.user_id = user_id;
		}

		

		public String getCondition() {
			String cond = "";
			if (!Validation.isEmptyLong(group_id)) {
				cond = " group_id = " + group_id;
			}
			if (!Validation.isEmptyLong(user_id)) {
				cond += (cond.length() > 0 ? " AND " : " ") + " user_id = " + user_id;
			}
//			if (!Validation.isEmptyString(lastname)) {
//				cond += (cond.length() > 0 ? " AND " : " ") + " lastname LIKE '%" + lastname + "%'";
//			}
//			if (!Validation.isEmptyString(firstname)) {
//				cond += (cond.length() > 0 ? " AND " : " ") + " firstname LIKE '%" + firstname + "%'";
//			}
//			if (!Validation.isEmptyString(middlename)) {
//				cond += (cond.length() > 0 ? " AND " : " ") + " middlename LIKE '%" + middlename + "%'";
//			}
//			private String lastname;
//			private String firstname;
//			private String middlename;
//			
//			public String getLastname() {
//				return lastname;
//			}
//
//			public void setLastname(String lastname) {
//				this.lastname = lastname;
//			}
//
//			public String getFirstname() {
//				return firstname;
//			}
//
//			public void setFirstname(String firstname) {
//				this.firstname = firstname;
//			}
//
//			public String getMiddlename() {
//				return middlename;
//			}
//
//			public void setMiddlename(String middlename) {
//				this.middlename = middlename;
//			}
			return cond;
		}
	
	}
	//******************************************************************
	//******************************************************************
	Map<Long, String> l_mg_map = new HashMap<Long, String>();	
	public Map<Long, String> getL_mg_map() {
		return l_mg_map;
	}
	public void setL_mg_map(Map<Long, String> l_mg_map) {
		this.l_mg_map = l_mg_map;
	}

	List<MessageGroup> l_mg = new ArrayList<MessageGroup>();	
	public List<MessageGroup> getL_mg() {
		return l_mg;
	}
	public void setL_mg(List<MessageGroup> l_mg) {
		this.l_mg = l_mg;
	}

	List<MessageGroupUser> items = new ArrayList<MessageGroupUser>();	
	public List<MessageGroupUser> getItems() {
		return items;
	}
	public void setItems(List<MessageGroupUser> items) {
		this.items = items;
	}

	private MessageGroupUser selected = new MessageGroupUser();
	public MessageGroupUser getSelected() {
		return selected;
	}
	public void setSelected(MessageGroupUser selected) {
		this.selected = selected;
	} 
	public MessageGroupUser prepareCreate() {
		this.selected = new MessageGroupUser();
		return this.selected;
	}
	//******************************************************************


	//******************************************************************
	//******************************************************************
	public void save() {
		try
		{
			
			if (this.selected == null) {
				throw new DAOException("Выберите группу для создния или изменения.");
			}
			if (selected.getGroup_id() != null && selected.getUser_id()!=null) {
				PermissionController.canWrite(userData, MessGrUserListBean.transaction_id);
				MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
				messageService.createMessageGroupUser(selected);
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("MessageGroupUpdateUserDialog");
			loadItems();
			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
		
	}
	public void delete() {
		try
		{
			
			if (this.selected == null) {
				throw new DAOException("Выберите группу для удаления.");
			}
//			
			PermissionController.canWrite(userData, MessGrUserListBean.transaction_id);
			MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
			messageService.deleteMessageGroupUser(selected);
			loadItems();
			if (items.size()>0)
			{
				selected = items.get(0);
			}
			else
			{
				selected = new MessageGroupUser();
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("MessageGroupUserListForm");
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
		
	}
	// *****************************************************************************	

	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");
    }

	List<UserStaff> l_userstaff = new ArrayList<UserStaff>();
	public List<UserStaff> getL_userstaff() {
		return l_userstaff;
	}
	public void setL_userstaff(List<UserStaff> l_userstaff) {
		this.l_userstaff = l_userstaff;
	}
	


	Map<Long, String> l_us_map = new HashMap<Long, String>();	
	public Map<Long, String> getL_us_map() {
		return l_us_map;
	}
	public void setL_us_map(Map<Long, String> l_us_map) {
		this.l_us_map = l_us_map;
	}
	
	public List<UserStaff> autocompleteUS(String a_text){
		
        List<UserStaff> filteredLus = new ArrayList<UserStaff>();
         
        for (UserStaff wa: l_userstaff) {
        	if (wa.getFullName().toLowerCase().contains(a_text.toLowerCase()))
        	{
        		filteredLus.add(wa);
        	}
        }
//		System.out.println(a_text);
		return filteredLus;
	}
	//*****************************************************************************************************

}
