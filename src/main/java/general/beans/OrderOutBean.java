package general.beans;

import general.AppContext;
import general.dao.OrderOutDao;
import general.tables.OrderOut;
import general.tables.search.OrderOutSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class OrderOutBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<OrderOut> items;
	private OrderOut selected;
	private OrderOutSearch searchModel = new OrderOutSearch();
	public OrderOut getSelected() {
		return selected;
	}
	public void setSelected(OrderOut selected) {
		this.selected = selected;
	}
	public List<OrderOut> getItems() {
		OrderOutDao d = (OrderOutDao)appContext.getContext().getBean("orderOutDao");
		this.items = d.findAll(this.searchModel.getCondition());
		return items;
	}
	
	public List<OrderOut> getNullItems() {
		OrderOutDao d = (OrderOutDao)appContext.getContext().getBean("orderOutDao");
		String cond = this.searchModel.getCondition();
		cond += (cond.length() > 0 ? " AND " : " ") + " invoice_id IS NULL ";
		this.items = d.findAll(cond);
		return items;
	} 
	
	
	public OrderOutSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(OrderOutSearch searchModel) {
		this.searchModel = searchModel;
	}


	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
