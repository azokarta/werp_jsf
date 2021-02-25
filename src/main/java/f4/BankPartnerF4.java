package f4;
import general.AppContext;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean; 
import javax.faces.bean.ManagedProperty;

import general.dao.BankPartnerDao;
import general.dao.DAOException;
import general.tables.BankPartner;

@ManagedBean(name = "bankPartnerF4Bean")
@ApplicationScoped
public class BankPartnerF4 {
	
	//***************************Application Context***************************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;  
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	//*************************************************************************
	
	
	List<BankPartner> bankPartner_list = new ArrayList<BankPartner>(); 
	@PostConstruct
	public void init(){ 
		try
		{
			bankPartner_list = new ArrayList<BankPartner>(); 
			BankPartnerDao bankPartnerDao = (BankPartnerDao) appContext.getContext().getBean("bankPartnerDao");
			bankPartner_list = bankPartnerDao.findAll();
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Bank partner F4 not loaded");
	    	throw new DAOException("Bank partner F4 not loaded");
		}
		
	}
	public List<BankPartner> getBankPartner_list(){
		return bankPartner_list;
	} 
	
	public void updateF4()
	{	
		try
		{
			bankPartner_list = new ArrayList<BankPartner>(); 
			BankPartnerDao bankPartnerDao = (BankPartnerDao) appContext.getContext().getBean("bankPartnerDao");
			bankPartner_list = bankPartnerDao.findAll();			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Bank partner F4 not loaded");
	    	throw new DAOException("Bank partner F4 not loaded");
		}
	}
} 
