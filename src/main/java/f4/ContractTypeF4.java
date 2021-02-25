package f4;

import general.AppContext;
import general.dao.ContractTypeDao;
import general.dao.DAOException;
import general.tables.ContractType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "contractTypeF4Bean")
@ApplicationScoped
public class ContractTypeF4 {

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

	List<ContractType> contractType_list = new ArrayList<ContractType>();
	Map<Long, ContractType> ct_map = new HashMap<Long, ContractType>();
	
	@PostConstruct
	public void init() {
		try
		{
			contractType_list = new ArrayList<ContractType>();
			ct_map = new HashMap<Long, ContractType>();
			ContractTypeDao ctDao = (ContractTypeDao) appContext.getContext().getBean("contractTypeDao");
			contractType_list = ctDao.findAll();
			ct_map = new HashMap<Long, ContractType>();
			
			Long key;
			for (ContractType ct: contractType_list) {
				//System.out.println("Inserting into Map: " + ct.getName());
				key = ct.getContract_type_id();
				ct_map.put(key, ct);
			}	
		}
	    catch(Exception ex)
		{
	    	System.out.println("Contract Type F4 not loaded");
	    	throw new DAOException("Contract Type F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			contractType_list = new ArrayList<ContractType>();
			ct_map = new HashMap<Long, ContractType>();
			ContractTypeDao ctDao = (ContractTypeDao) appContext.getContext().getBean("contractTypeDao");
			contractType_list = ctDao.findAll();
			ct_map = new HashMap<Long, ContractType>();
			
			Long key;
			for (ContractType ct: contractType_list) {
				//System.out.println("Inserting into Map: " + ct.getName());
				key = ct.getContract_type_id();
				ct_map.put(key, ct);
			}	
		}
	    catch(Exception ex)
		{
	    	System.out.println("Contract Type F4 not loaded");
	    	throw new DAOException("Contract Type F4 not loaded");
		}
	}

	public List<ContractType> getContractListByBukrs(String a_bukrs) {
		List<ContractType> ct_list = new ArrayList<ContractType>();
		
		for (ContractType ct : contractType_list) {
			if (ct.getBukrs().equals(a_bukrs)) {
				ct_list.add(ct);
			}
		}
		 				
		return ct_list;
	}
	
	public List<ContractType> getContractType_list() {
		return contractType_list;
	}

	public Map<Long, ContractType> getCt_map() {
		return ct_map;
	}

}
