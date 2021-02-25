package general.beans;

import general.AppContext;
import general.dao.OrderListDao;
import general.tables.OrderList;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class OrderListBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<OrderList> items;
	private Long orderId;

	public List<OrderList> getItems() {
		if(this.orderId != null && this.orderId.longValue() > 0L){
			return this.getItemsByOrder(this.orderId);
		}
		OrderListDao d = (OrderListDao)appContext.getContext().getBean("orderListDao");
		this.items = d.findAll();
		return items;
	}
	
	public List<OrderList> getItemsByOrder(Long orderId){
		OrderListDao d = (OrderListDao)appContext.getContext().getBean("orderListDao");
		this.items = d.dynamicFindAll(" order_id = " + orderId);
		return items;
	}

	private OrderList selected;
	public OrderList getSelected() {
		return selected;
	}

	public void setSelected(OrderList selected) {
		this.selected = selected;
	}

	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
