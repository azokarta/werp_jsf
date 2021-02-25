package crm.beans.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import crm.constants.DemoConstants;
import crm.dao.CrmDocDemoDao;
import crm.tables.CrmDocDemo;
import crm.tables.search.CrmDocDemoSearch;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.tables.Branch;
import general.tables.Staff;
import user.User;

@ManagedBean(name = "soldDemoBean")
@ViewScoped
public class SoldDemoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3313830069313338901L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			//loadItems();
		}
	}

	private String pageHeader = "Список проданных демо";
	private List<CrmDocDemo> items;
	private List<Staff> dealers;
	private CrmDocDemoSearch searchModel = new CrmDocDemoSearch();

	private void prepareSearchModel() {
		searchModel.setResultId(DemoConstants.RESULT_SOLD);
//		if (userData.isSys_admin() || userData.isMain()) {
//			searchModel.setBukrs(userData.getBukrs());
//		} else {
//			searchModel.setBukrs(userData.getBukrs());
//			List<Branch> userBranches = userData.getUserBranches();
//			if (Validation.isEmptyLong(searchModel.getBranchId())) {
//				if (Validation.isEmptyCollection(userData.getUserBranches())) {
//					searchModel.setBranchId(-1L);
//				} else {
//					List<String> branchIds = new ArrayList<>();
//					for (Branch br : userBranches) {
//						branchIds.add(br.getBranch_id().toString());
//					}
//					searchModel.setBranchIds(branchIds);
//				}
//			}
//		}
		
		if(Validation.isEmptyString(searchModel.getBukrs())){
			searchModel.setBukrs(userData.getBukrs());
		}
		
		List<Branch> userBranches = userData.getUserBranches();
		if (Validation.isEmptyLong(searchModel.getBranchId())) {
			if(userData.isSys_admin() || userData.isMain()){
				
			} else if (Validation.isEmptyCollection(userData.getUserBranches())) {
				searchModel.setBranchId(-1L);
			} else {
				List<String> branchIds = new ArrayList<>();
				for (Branch br : userBranches) {
					branchIds.add(br.getBranch_id().toString());
				}
				searchModel.setBranchIds(branchIds);
			}
		} else {
			searchModel.setBranchIds(new ArrayList<>());
		}
	}

	public List<Staff> getDealers() {
		return dealers;
	}

	public void setDealers(List<Staff> dealers) {
		this.dealers = dealers;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public CrmDocDemoSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(CrmDocDemoSearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<CrmDocDemo> getItems() {
		return items;
	}

	public void setItems(List<CrmDocDemo> items) {
		this.items = items;
	}

	public void loadItems() {
		prepareSearchModel();
		CrmDocDemoDao dao = (CrmDocDemoDao) appContext.getContext().getBean("crmDocDemoDao");
		items = dao.findAllSoldDemos(searchModel);
		//System.out.println("SIZE: " + items.size());
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

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
