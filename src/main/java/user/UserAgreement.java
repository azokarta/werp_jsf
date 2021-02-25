package user;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.UserService;

@ManagedBean(name = "userAgreementBean")
@ViewScoped
public class UserAgreement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -56901251995308737L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			showAgreementDialog = userData.getIs_agree() == 0;
			if (showAgreementDialog) {
				GeneralUtil.showDialog("userAgreementDlg");
			}
		}
	}

	private boolean showAgreementDialog = false;

	public boolean isShowAgreementDialog() {
		return showAgreementDialog;
	}

	public void setShowAgreementDialog(boolean showAgreementDialog) {
		this.showAgreementDialog = showAgreementDialog;
	}

	private boolean agree;

	public boolean isAgree() {
		return agree;
	}

	public void setAgree(boolean agree) {
		this.agree = agree;
	}

	public void Save() {
		try {
			if (!agree) {
				throw new DAOException("Отметьте галочку \"Я согласен\" чтобы согласовать соглашение");
			}
			UserService uService = appContext.getContext().getBean("userService", UserService.class);
			uService.changeAgreement(userData.getUserid(), agree);
			userData.setIs_agree(1);
			GeneralUtil.doRedirect("/general/mainPage.xhtml");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void Refuse() {
		try {
//			UserService uService = appContext.getContext().getBean("userService", UserService.class);
//			uService.changeAgreement(userData.getUserid(), false);
			try {
				userData.logout();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GeneralUtil.doRedirect("/general/mainPage.xhtml");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

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
