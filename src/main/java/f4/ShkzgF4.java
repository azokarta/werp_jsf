package f4;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean; 
 



import javax.faces.bean.ManagedProperty;

import general.AppContext;
import general.dao.ShkzgDao;
import general.tables.Shkzg;
@ManagedBean(name = "shkzgF4Bean")
@ApplicationScoped
public class ShkzgF4 {
	//private shkzg_list;
	List<Shkzg> shkzg_list = new ArrayList<Shkzg>();
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
		ShkzgDao shkzgDao = (ShkzgDao) appContext.getContext().getBean("shkzgDao");
		shkzg_list = shkzgDao.findAll();	
	}

	public List<Shkzg> getShkzg_list() {
		return shkzg_list;
	}
	
}
