package logistics.price;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.PriceListDao;
import general.tables.PriceList;
import general.tables.search.PriceListSearch;
import user.User;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "lpListBean")
@ViewScoped
public class LpList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long transactionId = 100L;

	private List<PriceList> items;
	private PriceListSearch searchModel = new PriceListSearch();
	private boolean canWrite = false;

	private PriceList selected;

	public PriceList getSelected() {
		return selected;
	}

	public void setSelected(PriceList selected) {
		this.selected = selected;
	}

	public boolean isCanWrite() {
		return canWrite;
	}

	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}

	public List<PriceList> getItems() {
		return items;
	}

	private void loadItems() {
		PriceListDao d = (PriceListDao) appContext.getContext().getBean("priceListDao");
		String cond = "";
		// if(!Validation.isEmptyString(searchModel.getBukrs())){
		// cond = " p.bukrs = '" + searchModel.getBukrs() + "' " ;
		// }
		items = d.findAllWithMatnr(cond); // d.dynamicFindPriceList(this.searchModel.getCondition());
	}

	public PriceListSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(PriceListSearch searchModel) {
		this.searchModel = searchModel;
	}

	@PostConstruct
	public void init() {
		// TODO PERMISSION
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}

		PermissionController.canRead(userData, transactionId);
		
		canWrite = PermissionController.canCreate(userData, transactionId);

		loadItems();
	}

	public void Search() {
		loadItems();
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public User getUserData() {
		return userData;
	}

	public void exportXLS() {

	}

	private String breadcrumb = "";

	public String getBreadcrumb() {
		return breadcrumb;
	}
}