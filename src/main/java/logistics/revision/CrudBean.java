package logistics.revision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.RevisionService;
import general.tables.RevItem;
import general.tables.RevItemTitle;
import general.tables.Revision;
import user.User;

@ManagedBean(name = "logRevCrudBean")
@ViewScoped
public class CrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4935422679672786783L;

	private Long id;
	private String mode;

	public void initBean(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {

			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				//
			}

			loadOrBlankRevision();
			if (!userData.isMain() && !userData.isSys_admin()) {
				revision.setBukrs(userData.getBukrs());
			}
			setPageHeader();
			if (mode.equals("create")) {

			} else if (mode.equals("update")) {
				prepareRevTitles();
			} else if (mode.equals("view")) {
				prepareRevTitles();
				prepareResultList();
				preparePostingItems();
				prepareWriteoffItems();
			}
		}
	}

	private void loadOrBlankRevision() {
		if (mode.equals("create")) {
			revision = new Revision();
		} else {
			RevisionService revService = appContext.getContext().getBean("revisionService", RevisionService.class);
			revision = revService.findWithDetail(id);
		}
	}

	public void Save() {
		try {
			RevisionService revService = appContext.getContext().getBean("revisionService", RevisionService.class);
			if (mode.equals("create")) {
				revService.create(revision, userData);
			} else {
				revService.update(revision, userData);
			}

			GeneralUtil.doRedirect("/logistics/revision/View.xhtml?id=" + revision.getId());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void closeDoc() {
		try {
			RevisionService revService = appContext.getContext().getBean("revisionService", RevisionService.class);
			revService.closeDoc(revision, userData.getUserid(),postingItems,writeoffItems);
			GeneralUtil.doRedirect("/logistics/revision/View.xhtml?id=" + revision.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private Revision revision;

	public Revision getRevision() {
		return revision;
	}

	public void setRevision(Revision revision) {
		this.revision = revision;
	}

	private String pageHeader;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		if (mode.equals("create")) {
			pageHeader = "Создание документа ревизии";
		} else if (mode.equals("update")) {
			pageHeader = "Редактирование документа ревизии №" + revision.getId();
		} else if (mode.equals("view")) {
			pageHeader = "Просмотр документа ревизии №" + revision.getId();
		}
	}

	List<RevItemTitle> revTitles;

	public List<RevItemTitle> getRevTitles() {
		return revTitles;
	}

	private void prepareRevTitles() {
		RevisionService revService = appContext.getContext().getBean("revisionService", RevisionService.class);
		revTitles = revService.findRevTitlesByRevId(revision.getId());
	}

	private Map<RevItemTitle, List<RevItem>> currentResultMap = new HashMap<>();

	public List<Map.Entry<RevItemTitle, List<RevItem>>> getCurrentResultList() {
		Set<Entry<RevItemTitle, List<RevItem>>> l = currentResultMap.entrySet();
		return new ArrayList<Entry<RevItemTitle, List<RevItem>>>(l);
	}

	private void prepareResultList() {
		RevisionService revService = appContext.getContext().getBean("revisionService", RevisionService.class);
		currentResultMap = revService.findCurrentResult(revision.getId());
	}

	public static class CurrentResult {
		private String revTitle;
		private String matnrName;
		private Double dbQty = 0D;
		private Double factQty = 0D;
		private Double defQty = 0D;
		private Double overQty = 0D;
		private Double overAmount = 0D;
		private Double defAmount = 0D;
		private String currencyCode;

		public String getCurrencyCode() {
			return currencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}

		public String getRevTitle() {
			return revTitle;
		}

		public void setRevTitle(String revTitle) {
			this.revTitle = revTitle;
		}

		public Double getDefAmount() {
			return defAmount;
		}

		public void setDefAmount(Double defAmount) {
			this.defAmount = defAmount;
		}

		public String getMatnrName() {
			return matnrName;
		}

		public void setMatnrName(String matnrName) {
			this.matnrName = matnrName;
		}

		public Double getDbQty() {
			return dbQty;
		}

		public void setDbQty(Double dbQty) {
			this.dbQty = dbQty;
		}

		public Double getFactQty() {
			return factQty;
		}

		public void setFactQty(Double factQty) {
			this.factQty = factQty;
		}

		public Double getDefQty() {
			return defQty;
		}

		public void setDefQty(Double defQty) {
			// if (defQty >= 0) {
			this.defQty = defQty;
			// }
		}

		public Double getOverQty() {
			return overQty;
		}

		public void setOverQty(Double overQty) {
			// if (overQty >= 0D) {
			this.overQty = overQty;
			// }
		}

		public Double getOverAmount() {
			return overAmount;
		}

		public void setOverAmount(Double overAmount) {
			this.overAmount = overAmount;
		}
	}

	List<RevItem> postingItems = new ArrayList<>();
	List<RevItem> writeoffItems = new ArrayList<>();

	public List<RevItem> getPostingItems() {
		return postingItems;
	}

	public void setPostingItems(List<RevItem> postingItems) {
		this.postingItems = postingItems;
	}

	public List<RevItem> getWriteoffItems() {
		return writeoffItems;
	}

	public void setWriteoffItems(List<RevItem> writeoffItems) {
		this.writeoffItems = writeoffItems;
	}

	private void preparePostingItems() {
		RevisionService s = (RevisionService) appContext.getContext().getBean("revisionService");
		postingItems = s.getPreparedPostingItems(revision.getId());
	}

	private void prepareWriteoffItems() {
		RevisionService s = (RevisionService) appContext.getContext().getBean("revisionService");
		writeoffItems = s.getPreparedWriteoffItems(revision.getId());
	}

	public static class MovingItem {
		private Long matnr;
		private String matnrName;
		private String matnrCode;
		private Double menge;
		private Double priceUnit = 0D;

		public Long getMatnr() {
			return matnr;
		}

		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}

		public String getMatnrName() {
			return matnrName;
		}

		public void setMatnrName(String matnrName) {
			this.matnrName = matnrName;
		}

		public String getMatnrCode() {
			return matnrCode;
		}

		public void setMatnrCode(String matnrCode) {
			this.matnrCode = matnrCode;
		}

		public Double getMenge() {
			return menge;
		}

		public void setMenge(Double menge) {
			this.menge = menge;
		}

		public Double getPriceUnit() {
			return priceUnit;
		}

		public void setPriceUnit(Double priceUnit) {
			this.priceUnit = priceUnit;
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

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
