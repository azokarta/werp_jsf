package logistics.order;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.OrderDao;
import general.dao.OrderListDao;
import general.tables.Order;
import general.tables.OrderList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import user.User;

@ManagedBean(name = "loin02Bean")
@ViewScoped
public class Loin02 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long orderId;
	private Order order;
	private List<OrderList> orderList = new ArrayList<OrderList>();

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	public List<OrderList> getOrderList() {
		this.orderList = new ArrayList<OrderList>();
		if(this.order != null){
			OrderListDao d = (OrderListDao)appContext.getContext().getBean("orderListDao");
			this.orderList = d.dynamicFindAll("order_id = " + this.order.getId());
		}
		
		
		return orderList;
	}

	public void setOrderList(List<OrderList> orderList) {
		this.orderList = orderList;
	}

	@PostConstruct
	public void init() {
		// TODO PERMISSION
		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return;
		}
		if (this.orderId != null && this.orderId.longValue() > 0L) {
			this.loadOrder();
		}
	}
	
	private void loadOrder() {
		try {
			if (this.orderId != null && this.orderId.longValue() > 0L) {
				OrderDao d = (OrderDao) appContext.getContext().getBean(
						"orderDao");
				this.order = d.find(this.orderId);
				if (this.order == null) {
					throw new DAOException("Order Not Found!");
				}
			} else {
				throw new DAOException("Order Not Found!");
			}
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public void Update(){
		
	}
}
