package user;

import general.AppContext;
import general.GeneralUtil;
import general.PasswordHelper;
import general.Validation;
import general.dao.DAOException;
import general.dao.UserDao;
import general.services.UserService;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "userProfileBean")
@ViewScoped
public class UserProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7912631282926696473L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			loadUser();
		}

	}

	private void loadUser() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		user = d.find(userData.getUserid());
	}

	private String currentPassword;
	private String newPassword;
	private String confirmNewPassword;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}

	private general.tables.User user;

	public general.tables.User getUser() {
		return user;
	}

	public void setUser(general.tables.User user) {
		this.user = user;
	}

	public void Save() {
		try {
			String error = "";
			if (Validation.isEmptyString(currentPassword)) {
				error += "Введите текущий пароль \n";
			}

			if (Validation.isEmptyString(newPassword)) {
				error += "Введите новый пароль \n";
			}

			if (Validation.isEmptyString(confirmNewPassword)) {
				error += "Введите повторно новый пароль \n";
			}

			if (error.length() == 0) {
				try {
					if (!user.getHash().equals(
							PasswordHelper.getSha256(currentPassword
									+ user.getSalt()))) {
						error += "Не правильно введен текущий пароль \n";
					}
				} catch (NoSuchAlgorithmException
						| UnsupportedEncodingException e) {
					error += "ОШИБКА ПРОГРАММЫ \n";
				}
				
				if(!newPassword.equals(confirmNewPassword)){
					error += "Новый пароль не совпадает с повтором \n";
				}
			}

			if (error.length() > 0) {
				throw new DAOException(error);
			}
			user.setPassword(newPassword);
			UserService us = (UserService)appContext.getContext().getBean("userService");
			us.changePassword(user);
			GeneralUtil.addSuccessMessage("Пароль изменен успешно");

		} catch (DAOException e) {
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

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
