package general.beans;

import general.AppContext;
import general.dao.MatnrTypeDao;
import general.tables.MatnrType;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MatnrTypeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MatnrType> items;
	public List<MatnrType> getItems() {
		MatnrTypeDao d = (MatnrTypeDao)appContext.getContext().getBean("matnrTypeDao");
		this.items = d.findAll("");
		return items;
	}
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
