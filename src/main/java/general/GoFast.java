package general;

import general.dao.DAOException;
import general.dao.TransactionDao;
import general.services.TransactionService;
import general.tables.Transaction;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

@ManagedBean(name = "goFast")
@ViewScoped
public class GoFast implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2596126592006442594L;
	String transactionCode;

	public void setTransactionCode(String s) {
		transactionCode = s;
	}

	public void doRedirect()
	{
		try
		{
			TransactionService transactionService = (TransactionService) appContext
					.getContext().getBean("transactionService");
			Transaction t = transactionService.getByTransactionCode(transactionCode.trim().toUpperCase());
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			if (t.getUrl() != null) {
				
				try {
					context.redirect(context.getRequestContextPath() + t.getUrl());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					MessageController.getInstance().addError(e.getMessage());
				}
			}
			else
			{
				MessageController.getInstance().addError("Transaction Code Doesn't Exists");
			}
		}
		catch(DAOException e)
		{
			MessageController.getInstance().addError(e.getMessage());
		}
	}
	
	public String getTransactionCode() {
		return transactionCode;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	
	public String getTransactionName(String a_lang, String a_transaction_id)
	{
		String name="";
		TransactionDao transactionDao = (TransactionDao) appContext
				.getContext().getBean("transactionDao");
		Transaction t = transactionDao.find(Long.parseLong(a_transaction_id));
		if (t==null)
		{
			return "";
		}
		if (a_lang == null || a_lang.equals("ru"))
		{
			return t.getName_ru();
		}
		else if (a_lang.equals("en"))
		{
			return t.getName_en();
		}
		else if (a_lang.equals("tr"))
		{
			return t.getName_tr();
		}
		return name;
	}
	
}
