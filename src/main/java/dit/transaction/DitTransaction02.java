package dit.transaction;

import java.io.Serializable;
import java.util.ArrayList;
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

@ManagedBean(name="ditTransaction02Bean")
@ViewScoped
public class DitTransaction02 implements Serializable {

	private static final long serialVersionUID = 3162069901362517541L;
	private final static String transaction_code = "DITTRANSACTION02";
	private final static Long transaction_id = (long) 56;
	
	Transaction transaction = new Transaction();

	public Transaction gettransaction() {
		return transaction;
	}

	public void settransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
	@PostConstruct
	public void init(){
		//TODO Permission
		this.loadFolderList();
	}
	
	List<MainPageFolders> folderList = new ArrayList<MainPageFolders>();
	public List<MainPageFolders> getFolderList() {
		return folderList;
	}
	
	private void loadFolderList(){
		MainPageFoldersDao d = (MainPageFoldersDao)appContext.getContext().getBean("mainPageFoldersDao");
		this.folderList = d.findAll("");
	}

	private String transactionCode;
	
	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public void toSearch()
	{
		try
		{
			if(transactionCode.isEmpty())
			{
				throw new DAOException("Please, enter code");
			}
			TransactionService tService = (TransactionService)appContext.getContext().getBean("transactionService");
			transaction = tService.getByTransactionCode(transactionCode);
		}
		catch(DAOException e)
		{
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void toUpdate()
	{
		try
		{
			//TODO Permission
			TransactionService tService = (TransactionService)appContext.getContext().getBean("transactionService");
			tService.updateTransaction(transaction);
			GeneralUtil.addSuccessMessage("Transaction updated successfully");
			transaction = new Transaction();
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
}
