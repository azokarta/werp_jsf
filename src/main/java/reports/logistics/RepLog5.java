package reports.logistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.services.reports.LogisticsReportService;
import general.tables.report.LogReport5;
import user.User;

@ManagedBean(name = "repLog5Bean")
@ViewScoped
public class RepLog5 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {

		}
	}

	private String pageHeader = "Отттчеттт";
	private Long contractNumber;
	private List<LogReport5> items;

	public void generateData() {
		try {
			if (Validation.isEmptyLong(contractNumber)) {
				throw new DAOException("Введите номер договора");
			}
			items = new ArrayList<>();
			LogisticsReportService service = appContext.getContext().getBean("logisticsReportService",
					LogisticsReportService.class);
			items = service.getRep5Data(contractNumber);
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public Long getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(Long contractNumber) {
		this.contractNumber = contractNumber;
	}

	public List<LogReport5> getItems() {
		return items;
	}

	public void setItems(List<LogReport5> items) {
		this.items = items;
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
