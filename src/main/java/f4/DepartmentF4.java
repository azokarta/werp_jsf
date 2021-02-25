package f4;

import general.AppContext;
import general.dao.DAOException;
import general.dao.DepartmentDao;
import general.tables.Department;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "departmentF4Bean")
@ApplicationScoped
public class DepartmentF4 {
	List<Department> l_dep = new ArrayList<Department>();
	List<Department> l_dep_sorted_ru = new ArrayList<Department>();
	List<Department> l_dep_sorted_tr = new ArrayList<Department>();
	List<Department> l_dep_sorted_en = new ArrayList<Department>();
	public List<Department> getL_dep(){
		return l_dep;
	}
	public List<Department> getL_dep(String a_lang){
		if (a_lang.equals("ru"))
		{
			return l_dep_sorted_ru;
		}
		else if (a_lang.equals("en"))
		{
			return l_dep_sorted_en;
		}
		else if (a_lang.equals("tr"))
		{
			return l_dep_sorted_tr;
		}
		else
		return l_dep;
	}

	
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
			l_dep = new ArrayList<Department>();
			DepartmentDao departmentDao = (DepartmentDao) appContext.getContext().getBean("departmentDao");
			l_dep = departmentDao.findAll("");
			l_dep_sorted_ru = departmentDao.findAll(" 1=1 order by name_ru");
			l_dep_sorted_tr = departmentDao.findAll(" 1=1 order by name_tr");
			l_dep_sorted_en = departmentDao.findAll(" 1=1 order by name_en");
		}
	    catch(Exception ex)
		{
	    	System.out.println("Department F4 not loaded");
	    	throw new DAOException("Department F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			l_dep = new ArrayList<Department>();
			DepartmentDao departmentDao = (DepartmentDao) appContext.getContext().getBean("departmentDao");
			l_dep = departmentDao.findAll("");			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Department F4 not loaded");
	    	throw new DAOException("Department F4 not loaded");
		}
	}
	
	public String getName(Long depId,String lang){
		for(Department d:l_dep){
			if(d.getDep_id().equals(depId)){
				return d.getName(lang);
			}
		}
		return  null;
	}
}
