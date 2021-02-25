package request;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.RequestService;
import general.tables.Request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "requestActionBean", eager = true)
@ViewScoped
public class ActionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2425787957376279257L;

	private Request request;

	public void initBean(Request r) throws Exception {
		this.request = r;
		prepareButtons();
	}

	private List<Button> buttonList = new ArrayList<ActionBean.Button>();

	public List<Button> getButtonList() {
		return buttonList;
	}

	private void prepareButtons() throws Exception {
		if(request == null){
			throw new Exception("Request is null");
		}
		
		buttonList = new ArrayList<ActionBean.Button>();
		if(userData.getUserid().equals(request.getCurrent_responsible())){
			if(request.getStatus_id().equals(Request.STATUS_1)){
				Button b1 = new Button("update", "Редактировать","");
				//Button b2 = new Button("send", "Отправить на исполнение","");
				buttonList.add(b1);
				//buttonList.add(b2);
			}
		}
		
		Button bToList = new Button("list", "В список","");
		buttonList.add(bToList);
	}

	public class Button {
		private String name;
		private String label;
		private String type;
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Button(String name, String label,String type) {
			super();
			this.name = name;
			this.label = label;
			this.type = type;
		}
		
		
		public void doAction(){
			if(getName().equals("update")){
				GeneralUtil.doRedirect("/request/Update.xhtml?id=" + request.getId());
			}else if(getName().equals("send")){
				//GeneralUtil.showDialog("ActionDialog");
				Send();
			}else if(getName().equals("list")){
				GeneralUtil.doRedirect("/request/List.xhtml");
			}
		}
		
	}
	
	private List<general.tables.User> userList = new ArrayList<general.tables.User>();
	public List<general.tables.User> getUserList() {
		return userList;
	}

	public void setUserList(List<general.tables.User> userList) {
		this.userList = userList;
	}

	public void Send(){
		try{
			RequestService rService = (RequestService)appContext.getContext().getBean("requestService");
			rService.nextStep(request, userList, userData);
			GeneralUtil.doRedirect("/request/View.xhtml?id=" + request.getId());
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;
	}
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	@ManagedProperty("#{userinfo}")
	User userData;
	public User getUserData() {
		return userData;
	}
	public void setUserData(User userData) {
		this.userData = userData;
	}
}