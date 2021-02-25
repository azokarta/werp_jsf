package mydocs;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.MyDocsDao;
import general.tables.MyDocs;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import user.User;

@ManagedBean(name = "mydocsListBean")
@ViewScoped
public class ListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8921174744562329468L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try{
				statusId = new Integer(GeneralUtil.getRequestParameter("i"));
				statusId++;
			}catch(NumberFormatException e){
				statusId = 1;
			}
			
			try{
				context = GeneralUtil.getRequestParameter("context").trim();
			}catch(Exception e){
				context = "";
			}
			prepareMenuModel();
			prepareMdList();
		}
	}
	
	private String context;
	
	
	private MenuModel menuModel;
	public MenuModel getMenuModel() {
		return menuModel;
	}
	
	private void prepareMenuModel(){
		menuModel = new DefaultMenuModel();
		
		DefaultMenuItem dmi1 = new DefaultMenuItem("Заявки");
		dmi1.setUrl(String.format("/mydocs/List.xhtml?i=%d&context=%s", getStatusId()-1,"request"));
		if(!Validation.isEmptyString(context) && context.equals("request")){
			dmi1.setStyleClass("ui-state-active");
		}
		
		menuModel.addElement(dmi1);
	}

	private Integer statusId;
	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	List<MyDocs> mdList;

	public List<MyDocs> getMdList() {
		return mdList;
	}

	private void prepareMdList() {
		MyDocsDao mdDao = (MyDocsDao) appContext.getContext().getBean(
				"myDocsDao");
		String cond = String.format(
				" owner = %d AND status_id = %d ",
				(userData.getStaff() == null ? 0L : userData.getStaff()
						.getStaff_id()), statusId);
		if(!Validation.isEmptyString(context)){
			cond += " AND context = '" + context + "' ";
		}
		mdList = mdDao.findAll(cond);
	}

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
