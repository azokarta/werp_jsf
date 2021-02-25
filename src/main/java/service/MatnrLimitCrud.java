package service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.springframework.beans.BeanUtils;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao2.MatnrLimitDao;
import general.springservice.MatnrLimitService;
import general.tables.Matnr;
import general.tables2.MatnrLimit;
import general.tables2.MatnrLimitItem;
import user.User;

@ManagedBean(name = "matnrLimitCrudBean")
@ViewScoped
public class MatnrLimitCrud implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Long transactionId = 761L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			try {
				this.id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				this.id = 0L;
			}
		}
	}

	private MatnrLimit selected;
	private String mode;
	private Long id;
	private String[] branchIds;

	public String[] getBranchIds() {
		return branchIds;
	}

	public void setBranchIds(String[] branchIds) {
		this.branchIds = branchIds;
	}

	public void initBean(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {
			if ("create".equals(this.mode)) {
				pageHeader = "Добавление лимит выдачи";
				selected = new MatnrLimit();
				loadMatnrs();
			} else {
				MatnrLimitDao mlDao = (MatnrLimitDao) appContext.getContext().getBean("matnrLimitDao");
				selected = mlDao.findWithDetail(id);
				if ("update".equals(this.mode)) {
					loadMatnrs();
					pageHeader = "Редактирование лимит выдачи №" + selected.getId();
				} else {
					pageHeader = "Просмотр лимит выдачи №" + selected.getId();
				}
			}
		}
	}

	private List<Matnr> matnrs;
	private Matnr selectedMatnr;
	private String pageHeader;

	public MatnrLimit getSelected() {
		return selected;
	}

	public void setSelected(MatnrLimit selected) {
		this.selected = selected;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public List<Matnr> getMatnrs() {
		return matnrs;
	}

	public void setMatnrs(List<Matnr> matnrs) {
		this.matnrs = matnrs;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	private void loadMatnrs() {
		MatnrDao md = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrs = md.findAll();
	}

	public void Save() {
		try {
			if(!PermissionController.canAll(userData, transactionId)){
				throw new DAOException("Нет доступа!");
			}
			MatnrLimitService mlService = (MatnrLimitService) appContext.getContext().getBean("matnrLimitService");
			selected.setUpdatedAt(Calendar.getInstance().getTime());
			selected.setUpdatedBy(userData.getUserid());
			if ("create".equals(mode)) {
				selected.setCreatedBy(userData.getUserid());
				selected.setCreatedAt(Calendar.getInstance().getTime());

				if (branchIds == null || branchIds.length == 0) {
					throw new DAOException("Выберите филиал!");
				}
				List<MatnrLimit> mlList = new ArrayList<>();
				for (String brId : branchIds) {
					MatnrLimit ml = new MatnrLimit();
					BeanUtils.copyProperties(selected, ml);
					ml.setBranchId(Long.valueOf(brId));
					ml.setId(null);
					ml.setMatnrLimitItems(new ArrayList<>());
					for(MatnrLimitItem mlItem:selected.getMatnrLimitItems()){
						MatnrLimitItem newMlItem = new MatnrLimitItem();
						newMlItem.setAccountLimit(mlItem.getAccountLimit());
						newMlItem.setId(null);
						newMlItem.setMatnr(mlItem.getMatnr());
						ml.addMatnrLimitItem(newMlItem);
					}
					mlList.add(ml);
				}
				mlService.create(mlList);
				GeneralUtil.doRedirect("/service/matnr-limit/List.xhtml");
			} else if ("update".equals(mode)) {
				mlService.update(selected);
				GeneralUtil.doRedirect("/service/matnr-limit/View.xhtml?id=" + selected.getId());
			}
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		} catch (Exception ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void onMatnrSelected() {
		if (selected != null && selectedMatnr != null) {
			MatnrLimitItem mlItem = new MatnrLimitItem();
			mlItem.setAccountLimit(selectedMatnr.getAccount_limit());
			mlItem.setMatnr(selectedMatnr.getMatnr());
			mlItem.setMatnrObject(selectedMatnr);
			mlItem.setMatnrLimit(selected);
			selected.addMatnrLimitItem(mlItem);

			GeneralUtil.hideDialog("MatnrListDialog");
		}
	}

	public void deleteMatnrsRow(MatnrLimitItem mlm) {
		selected.removeMatnrLimitItem(mlm);
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	@ManagedProperty("#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
