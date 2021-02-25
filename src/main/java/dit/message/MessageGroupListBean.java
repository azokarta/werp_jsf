package dit.message;


import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao2.MessageGroupDao;
import general.springservice.MessageSpSer;
import general.tables2.MessageGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;


@ManagedBean(name = "messageGroupListBean", eager = true)
@ViewScoped
public class MessageGroupListBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private final static String transaction_code = "MESSGR";
	private final static Long transaction_id = (long) 598;
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
		PermissionController.canRead(userData, MessageGroupListBean.transaction_id);
		loadItems();
	}
	//******************************************************************
	//******************************************************************
	private void loadItems() {
		// System.out.println("LOADING...");
		MessageGroupDao d = (MessageGroupDao) appContext.getContext().getBean("messageGroupDao");
		items = d.findAll(searchModel.getCondition());
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
		private String group_name;
		public String getGroup_name() {
			return group_name;
		}

		public void setGroup_name(String group_name) {
			this.group_name = group_name;
		}



		public String getCondition() {
			String cond = "";
			if (!Validation.isEmptyString(group_name)) {
				cond = " group_name LIKE '%" + group_name + "%'";
			}
				
			return cond;
		}
	
	}
	//******************************************************************
	//******************************************************************
	List<MessageGroup> items = new ArrayList<MessageGroup>();	
	public List<MessageGroup> getItems() {
		return items;
	}
	public void setItems(List<MessageGroup> items) {
		this.items = items;
	}

	private MessageGroup selected = new MessageGroup();
	public MessageGroup getSelected() {
		return selected;
	}
	public void setSelected(MessageGroup selected) {
		this.selected = selected;
	} 
	public MessageGroup prepareCreate() {
		this.selected = new MessageGroup();
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
			if (selected.getGroup_id() != null) {
				PermissionController.canWrite(userData, MessageGroupListBean.transaction_id);
				MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
				messageService.updateMessageGroup(selected);
			} else {
				PermissionController.canWrite(userData, MessageGroupListBean.transaction_id);
				MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
				messageService.createMessageGroup(selected);
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("MessageGroupUpdateDialog");
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
			
			PermissionController.canWrite(userData, MessageGroupListBean.transaction_id);
			MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
			messageService.deleteMessageGroup(selected);
			loadItems();
			if (items.size()>0)
			{
				selected = items.get(0);
			}
			else
			{
				selected = new MessageGroup();
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("MessageGroupListForm");
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
	
	
	
	//*****************************************************************************************************

}
