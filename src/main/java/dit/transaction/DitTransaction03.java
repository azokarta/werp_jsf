package dit.transaction;

import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.dao.DAOException;
import general.services.TransactionService;
import general.tables.Transaction;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="ditTransaction03Bean",eager=true)
@ViewScoped
public class DitTransaction03 {

	private final static String transaction_code = "DITTRANSACTION01";
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
