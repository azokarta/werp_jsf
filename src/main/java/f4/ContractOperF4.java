package f4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.AppContext;
import general.dao.ContractOperDao;
import general.dao.DAOException;
import general.tables.ContractOper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "contractOperF4Bean")
@ApplicationScoped
public class ContractOperF4 {
	
	// ***************************Application Context***************************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	// *************************************************************************
	
	
	List<ContractOper> contractOper_list = new ArrayList<ContractOper>();
	Map<Long, ContractOper> co_map = new HashMap<Long, ContractOper>();
	
	@PostConstruct
	public void init() {
		try
		{
			contractOper_list = new ArrayList<ContractOper>();
			co_map = new HashMap<Long, ContractOper>();
			ContractOperDao coDao = (ContractOperDao) appContext.getContext().getBean("contractOperDao");
			contractOper_list = coDao.findAll();

			co_map = new HashMap<Long, ContractOper>();
			Long key;
			
			for (ContractOper co: contractOper_list) {
				//System.out.println("Inserting into Map: " + co.getOper_name_ru());
				key = co.getId();
				co_map.put(key, co);
			}		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Contract Oper F4 not loaded");
	    	throw new DAOException("Contract Oper F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			contractOper_list = new ArrayList<ContractOper>();
			co_map = new HashMap<Long, ContractOper>();
			ContractOperDao coDao = (ContractOperDao) appContext.getContext().getBean("contractOperDao");
			contractOper_list = coDao.findAll();

			co_map = new HashMap<Long, ContractOper>();
			Long key;
			
			for (ContractOper co: contractOper_list) {
				//System.out.println("Inserting into Map: " + co.getOper_name_ru());
				key = co.getId();
				co_map.put(key, co);
			}		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Contract Oper F4 not loaded");
	    	throw new DAOException("Contract Oper F4 not loaded");
		}
	}
	
	
	public List<ContractOper> getContractOper_list() {
		return contractOper_list;
	}

	public Map<Long, ContractOper> getCo_map() {
		return co_map;
	}
	
}