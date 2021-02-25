package logistics.werks;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.services.MatnrListService;
import user.User;

@ManagedBean(name = "restoreSoldMatnrBean")
@ViewScoped
public class RestoreSoldMatnrBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		GeneralUtil.doRedirect("/no_permission.xhtml");
		if (!GeneralUtil.isAjaxRequest()) {
			if (!userData.isSys_admin() && !userData.isMain()) {
				setBukrs(userData.getBukrs());
			}
		}
	}

	private String pageHeader = "Восстановление материалов проданных до миграции ";

	private String bukrs;

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	private Long contractNumber;
	private Long werks;

	public Long getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(Long contractNumber) {
		this.contractNumber = contractNumber;
	}

	public Long getWerks() {
		return werks;
	}

	public void setWerks(Long werks) {
		this.werks = werks;
	}

	public void Save() {
		try {
			if (Validation.isEmptyLong(contractNumber)) {
				throw new DAOException("Введите номер договора");
			}

			if (Validation.isEmptyLong(werks)) {
				throw new DAOException("Выберите склад");
			}

			MatnrListService mlService = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mlService.restoreSoldMatnr(contractNumber, werks, userData);
			setContractNumber(0L);
			setWerks(0L);
			//setBukrs(null);
			GeneralUtil.addSuccessMessage("Документы списания по данному контракту созданы успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	@ManagedProperty(value = "#{appContext}")
	protected AppContext appContext;

	@ManagedProperty(value = "#{userinfo}")
	protected User userData;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

}