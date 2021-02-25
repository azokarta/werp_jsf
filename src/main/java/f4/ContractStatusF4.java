package f4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import general.AppContext;
import general.dao.ContractStatusDao;
import general.dao.DAOException;
import general.tables.ContractStatus;
@ManagedBean(name="contractStatusF4Bean")
@ApplicationScoped 
public class ContractStatusF4 {
	List<ContractStatus> contractStatus_list = new ArrayList<ContractStatus>();
	Map<Long, ContractStatus> csMap = new HashMap<Long, ContractStatus>();
	
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
	 
	@PostConstruct
	public void init(){ 
		try
		{
			contractStatus_list = new ArrayList<ContractStatus>();
			csMap = new HashMap<Long, ContractStatus>();
			ContractStatusDao contractStatusDao = (ContractStatusDao) appContext.getContext().getBean("contractStatusDao");
			contractStatus_list = contractStatusDao.findAllActive();
			for (ContractStatus wa_cs:contractStatus_list)
			{
				csMap.put(wa_cs.getContract_status_id(), wa_cs);
			}			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Contract Status F4 not loaded");
	    	throw new DAOException("Contract Status F4 not loaded");
		}
		
	}
	
	public List<ContractStatus> getContractStatus_list(){
		return contractStatus_list;
	}
	
	public Map<Long, ContractStatus> getCsMap() {
		return csMap;
	}
	
}	