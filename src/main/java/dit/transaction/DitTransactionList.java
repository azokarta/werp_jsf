package dit.transaction;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.MainPageFoldersDao;
import general.dao.TransactionDao;
import general.dao.UserDao;
import general.services.TransactionService;
import general.tables.MainPageFolders;
import general.tables.Transaction;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "ditTransactionListBean")
@ViewScoped
public class DitTransactionList implements Serializable {

	private static final long serialVersionUID = 1L;

	@PostConstruct
	public void init() {
		// TODO PERMISSION
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}
		
		if(!userData.isSys_admin()){
			GeneralUtil.doRedirect("/no_permission.xhtml");
		}

		loadItems();
		loadFolders();
	}

	private List<MainPageFolders> folders;

	public List<MainPageFolders> getFolders() {
		return folders;
	}

	private void loadFolders() {
		MainPageFoldersDao mpfd = (MainPageFoldersDao) appContext.getContext().getBean("mainPageFoldersDao");
		folders = mpfd.findAll("");
	}

	private TransactionSearch searchModel = new TransactionSearch();

	public TransactionSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(TransactionSearch searchModel) {
		this.searchModel = searchModel;
	}

	List<Transaction> items;
	private Transaction selected;

	public Transaction getSelected() {
		return selected;
	}

	private void loadItems() {
		// System.out.println("LOADING...");
		TransactionDao d = (TransactionDao) appContext.getContext().getBean("transactionDao");
		items = d.findAll(searchModel.getCondition());
	}

	public void setSelected(Transaction selected) {
		this.selected = selected;
	}

	public List<Transaction> getItems() {
		return items;
	}

	public Transaction prepareCreate() {
		this.selected = new Transaction();
		return this.selected;
	}

	public void Search() {
		loadItems();
	}

	private void Create() {
		TransactionService service = (TransactionService) appContext.getContext().getBean("transactionService");
		service.createTransaction(selected);
	}

	public void Save() {
		try {
			if (this.selected == null) {
				throw new DAOException("Select Transaction For Update");
			}
			if (selected.getTransaction_id() != null) {
				Update();
			} else {
				Create();
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("TransactionUpdateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Update() {
		TransactionService service = (TransactionService) appContext.getContext().getBean("transactionService");
		service.updateTransaction(this.selected);
	}

	// SEARCH MODEL CLASS
	public class TransactionSearch {
		private String name;
		private Long transactionId;
		private String transactionCode;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getTransactionId() {
			return transactionId;
		}

		public void setTransactionId(Long transactionId) {
			this.transactionId = transactionId;
		}

		public String getTransactionCode() {
			return transactionCode;
		}

		public void setTransactionCode(String transactionCode) {
			this.transactionCode = transactionCode;
		}

		public String getCondition() {
			String cond = "";
			if (!Validation.isEmptyString(name)) {
				cond = " name_ru LIKE '%" + name + "%'";
			}

			if (!Validation.isEmptyLong(transactionId)) {
				cond += (cond.length() > 0 ? " AND " : " ") + " transaction_id = " + transactionId;
			}

			if (!Validation.isEmptyString(transactionCode)) {
				cond += (cond.length() > 0 ? " AND " : " ") + " transaction_code = '" + transactionCode + "' ";
			}
			return cond;
		}
	}

	private String breadcrumb = "Дит > Все транзакции";

	public String getBreadcrumb() {
		return breadcrumb;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private user.User userData;

	public user.User getUserData() {
		return userData;
	}

	public void setUserData(user.User userData) {
		this.userData = userData;
	}

}
