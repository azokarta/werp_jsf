package service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao2.MatnrLimitDao;
import general.springservice.MatnrLimitService;
import general.tables.Branch;
import general.tables2.MatnrLimit;
import user.User;

@ManagedBean(name = "matnrLimitListBean")
@ViewScoped
public class MatnrLimitList {

	private static final Long transactionId = 761L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			loadItems();
		}
	}

	private String pageHeader = "Ограничение выдачи материалов";
	private List<MatnrLimit> items = new ArrayList<>();
	private String bukrs;
	private Long branchId;
	private List<String> branchIds = new ArrayList<>();
	private Long positionId;
	private int totalCount;

	private List<MatnrLimit> selectedItems = new ArrayList<>();
	private boolean showViewUpdateBtns = false;

	public List<MatnrLimit> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<MatnrLimit> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public boolean isShowViewUpdateBtns() {
		return selectedItems != null && selectedItems.size() == 1;
	}

	public void setShowViewUpdateBtns(boolean showViewUpdateBtns) {
		this.showViewUpdateBtns = showViewUpdateBtns;
	}

	public Long getSelectedId() {
		if (Validation.isEmptyCollection(selectedItems)) {
			return null;
		}

		return selectedItems.get(0).getId();
	}

	private void prepareSearchModel() {
		if (userData.isSys_admin() || userData.isMain()) {

		} else {
			setBukrs(userData.getBukrs());
			List<Branch> userBranches = userData.getUserBranches();
			if (Validation.isEmptyCollection(userBranches)) {
				setBranchId(-1L);
			} else if (userBranches.size() == 1) {
				setBranchId(userBranches.get(0).getBranch_id());
			} else {
				for (Branch br : userBranches) {
					branchIds.add(br.getBranch_id().toString());
				}
			}
		}
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public List<MatnrLimit> getItems() {
		return items;
	}

	public void setItems(List<MatnrLimit> items) {
		this.items = items;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public void loadItems() {
		try {
			prepareSearchModel();
			MatnrLimitDao mlDao = (MatnrLimitDao) appContext.getContext().getBean("matnrLimitDao");
			if (branchId != null && branchId != 0) {
				items = mlDao.findAll(bukrs, branchId, positionId);
			} else if (!Validation.isEmptyCollection(branchIds)) {
				items = mlDao.findAll(bukrs, branchIds, positionId);
			} else if (userData.isSys_admin() || userData.isMain()) {
				items = mlDao.findAll(bukrs, branchId, positionId);
			}

			this.totalCount = items.size();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void delete() {
		try {
			if(!PermissionController.canAll(userData, transactionId)){
				throw new DAOException("Нет доступа!");
			}

			if (Validation.isEmptyCollection(selectedItems)) {
				throw new DAOException("Выберите элемент для удаления!");
			}
			
			
			
			MatnrLimitService service = (MatnrLimitService) appContext.getContext().getBean("matnrLimitService");
			List<Long> ids = new ArrayList<>();
			for (MatnrLimit ml : selectedItems) {
				ids.add(ml.getId());
			}
			service.delete(ids);
			loadItems();
			GeneralUtil.updateFormElement("DataListForm");
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

	@ManagedProperty("#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
