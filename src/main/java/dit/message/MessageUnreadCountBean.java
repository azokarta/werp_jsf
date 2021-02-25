package dit.message;


import general.AppContext;
import general.GeneralUtil;
import general.dao2.MessageHeaderDao;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;


@ManagedBean(name = "messageUnreadCountBean", eager = true)
@ViewScoped
public class MessageUnreadCountBean implements Serializable{
	private static final long serialVersionUID = 1L;
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
		
		loadMessIn();
		
	}
	//******************************************************************
	//******************************************************************
	
	
	private String notReadMessNum="";
	
	
	public String getNotReadMessNum() {
		return notReadMessNum;
	}
	public void setNotReadMessNum(String notReadMessNum) {
		this.notReadMessNum = notReadMessNum;
	}
	
	
	
	
	public void loadMessIn() {
		MessageHeaderDao messageHeaderDao = (MessageHeaderDao) appContext.getContext().getBean("messageHeaderDao");
		countMess = messageHeaderDao.countUnreadMessageByUser(userData.getUserid());		

		String text = "";

		if (userData.getU_language().equals("en")) {text = "Inbox";}
		else if (userData.getU_language().equals("tr")) {text = "Gelen Kutusu";}
		else {text = "Входящие";}	
		
		
		notReadMessNum = text+" ("+countMess+")";

	}
	
	private int countMess=0;
	public int getCountMess() {
		return countMess;
	}
	public void setCountMess(int countMess) {
		this.countMess = countMess;
	}
}
