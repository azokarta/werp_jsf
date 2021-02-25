package general.beans;

import general.AppContext;
import general.dao.RoleDao;
import general.tables.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class RoleBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Role> items;
	private String bukrs = "";

	public List<Role> getItems() {
		RoleDao d = (RoleDao)appContext.getContext().getBean("roleDao");
		this.items = d.findAll("");
		if(this.bukrs.length() > 0){
			return this.getItemsByBukrs(this.bukrs);
		}
		return items;
	}

	public List<Role> getItemsByBukrs(String bukrsId){
		List<Role> out = new ArrayList<Role>();
		for(Role r:this.getItems()){
			if(r.getBukrs().equals(bukrsId)){
				out.add(r);
			}
		}
		return out;
	}
	
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
