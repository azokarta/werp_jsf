package general.beans;

import general.AppContext;
import general.dao.OrderDao;
import general.tables.Order;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class OrderBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Order> items;
	public List<Order> getItems() {
		OrderDao d = (OrderDao)appContext.getContext().getBean("orderDao");
		this.items = d.findAll();
		return items;
	}
	
	public List<Order> getItemsByStatus(int status){
		OrderDao d = (OrderDao)appContext.getContext().getBean("orderDao");
		this.items = d.findAll("status_id = " + status);
		return items;
	}

	private Order selected;
	
	public Order getSelected() {
		return selected;
	}

	public void setSelected(Order selected) {
		this.selected = selected;
	}
	
	public void resetSelected(){
		this.selected = null;
	}

	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
