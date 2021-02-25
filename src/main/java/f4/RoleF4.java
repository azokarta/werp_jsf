package f4;

import general.AppContext;
import general.dao.DAOException;
import general.dao.RoleDao;
import general.tables.Role;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "roleF4Bean")
@ApplicationScoped
public class RoleF4 {
	List<Role> role_list = new ArrayList<Role>();
	
	@PostConstruct
	public void init(){
		try
		{
			role_list = new ArrayList<Role>();
			RoleDao d = (RoleDao)appContext.getContext().getBean("roleDao");
			this.role_list = d.findAll("");			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Role F4 not loaded");
	    	throw new DAOException("Role F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			role_list = new ArrayList<Role>();
			RoleDao d = (RoleDao)appContext.getContext().getBean("roleDao");
			this.role_list = d.findAll("");			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Role F4 not loaded");
	    	throw new DAOException("Role F4 not loaded");
		}
	}
	
	public List<Role> getRole_list() {
		return role_list;
	}
	
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	public String getName(Long roleId){
		if(roleId != null){
			for(Role r:this.role_list){
				if(r.getRole_id() == roleId){
					return r.getText45();
				}
			}
		}
		return "";
	}
}
