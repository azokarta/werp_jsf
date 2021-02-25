package dit.transaction;

import java.util.List;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.MainPageFoldersDao;
import general.services.TransactionService;
import general.tables.MainPageFolders;
import general.tables.Transaction;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="ditTransaction01Bean",eager=true)
@ViewScoped
public class DitTransaction01 {

	private final static String transaction_code = "DITTRANSACTION01";
	private final static Long transaction_id = (long) 56;
	
	Transaction newTransaction = new Transaction();

	public Transaction getNewTransaction() {
		return newTransaction;
	}

	public void setNewTransaction(Transaction newTransaction) {
		this.newTransaction = newTransaction;
	}
	
	@PostConstruct
	public void init(){
		//TODO Permission
	}
	
	public void toSave()
	{
		try
		{
			//TODO Permission
			TransactionService tService = (TransactionService)appContext.getContext().getBean("transactionService");
			tService.createTransaction(newTransaction);
			GeneralUtil.addSuccessMessage("Transaction created successfully");
			newTransaction = new Transaction();
		}
		catch(DAOException e)
		{
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
	  return appContext;
	}

	public void setAppContext(AppContext appContext) {
	  this.appContext = appContext;
	}
	
	@ManagedProperty(value="#{userinfo}")
	private user.User userData;
	public user.User getUserData() {
	  return userData;
	}

	public void setUserData(user.User userData) {
	  this.userData = userData;
	}
	
	List<MainPageFolders> folderList = null;
	public List<MainPageFolders> getFolderList() {
		if(this.folderList == null){
			MainPageFoldersDao d = (MainPageFoldersDao)appContext.getContext().getBean("mainPageFoldersDao");
			this.folderList = d.findAll("");
		}
		return folderList;
	}
	
}
