package logistics.order;

import f4.WerksF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.OrderDao;
import general.dao.OrderListDao;
import general.dao.OrderOutDao;
import general.dao.OrderOutListDao;
import general.services.OrderOutService;
import general.tables.Customer;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Order;
import general.tables.OrderList;
import general.tables.OrderOut;
import general.tables.OrderOutList;
import general.tables.OrderStatus;
import general.tables.search.MatnrSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import logistics.LgBase;
import user.User;

@ManagedBean(name = "loout01Bean")
@ViewScoped
public class Loout01 extends LgBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long orderId;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	private String breadcrumb = " Логистика > Заказы > Новый заказ товаров (Внеш)";

	public String getBreadcrumb() {
		return breadcrumb;
	}

	private List<OrderOutList> orderList = new ArrayList<OrderOutList>();
	private OrderOut order = new OrderOut();

	public List<OrderOutList> getOrderList() {
		return orderList;
	}

	private List<Matnr> selectedMatnrs = new ArrayList<Matnr>();

	public List<Matnr> getSelectedMatnrs() {
		return selectedMatnrs;
	}

	public void setSelectedMatnrs(List<Matnr> selectedMatnrs) {
		this.selectedMatnrs = selectedMatnrs;
	}

	private List<Matnr> dialogMatnrs = new ArrayList<Matnr>();

	public List<Matnr> getDialogMatnrs() {
		return dialogMatnrs;
	}

	private MatnrSearch dialogSearchModel = new MatnrSearch();

	public MatnrSearch getDialogSearchModel() {
		return dialogSearchModel;
	}

	public void setDialogSearchModel(MatnrSearch dialogSearchModel) {
		this.dialogSearchModel = dialogSearchModel;
	}

	public void loadDialogMatnrs() {
		dialogMatnrs.clear();
		if (Validation.isEmptyString(order.getBukrs())) {
			GeneralUtil.addErrorMessage("Сначала выберите компанию");
		} else {
			dialogMatnrs.clear();
			MatnrDao d = (MatnrDao) getAppContext().getContext().getBean(
					"matnrDao");
			List<Matnr> temp = d.findAll(dialogSearchModel.getCondition());
			for (Matnr m : temp) {
				if (!selectedMatnrIds.contains(m.getMatnr())) {
					dialogMatnrs.add(m);
				}
			}
		}
	}

	private List<Long> selectedMatnrIds = new ArrayList<Long>();

	public void assignSelectedMatnrs() {
		for (Matnr m : selectedMatnrs) {
			if (!selectedMatnrIds.contains(m.getMatnr())) {
				OrderOutList temp = new OrderOutList();
				temp.setMatnr_id(m.getMatnr());
				temp.setQuantity_in(0);
				temp.setQuantity_out(0);
				temp.setMatnrCode(m.getCode());
				temp.setMatnrName(m.getText45());
				orderList.add(temp);
				selectedMatnrIds.add(m.getMatnr());
			}
		}
		selectedMatnrs.clear();
	}
	
	private void prepareOrderList(List<OrderOutList> l){
		String[] ids = new String[l.size()];
		for(int k = 0; k < l.size(); k++){
			ids[k] = l.get(k).getMatnr_id().toString();
		}
		
		if(ids.length > 0){
			String condition = String.format(" matnr IN(%s) ", "'" + String.join("','", ids) + "'");
			List<Matnr> mList = getMatnrDao().findAll(condition);
			Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();
			for(Matnr m:mList){
				matnrMap.put(m.getMatnr(),m);
			}
			Long matnrId;
			for(int i = 0; i < l.size(); i++){
				matnrId = l.get(i).getMatnr_id();
				if(matnrMap.containsKey(matnrId)){
					l.get(i).setMatnrName(matnrMap.get(matnrId).getText45());
					l.get(i).setMatnrCode(matnrMap.get(matnrId).getCode());
				}
			}
		}
	}

	public String removeMatnr(OrderOutList o) {
		selectedMatnrs.remove(o);
		orderList.remove(o);
		selectedMatnrIds.remove(o.getMatnr_id());
		return null;
	}

	public void resetOrderList() {
		orderList.clear();
		selectedMatnrIds.clear();
		//dialogSearchModel.setBukrs(order.getBukrs());
	}

	public void setOrderList(List<OrderOutList> orderList) {
		this.orderList = orderList;
	}

	private void loadOrderList() {
		if (this.orderId != null && this.orderId.longValue() > 0) {
			OrderOutListDao d = (OrderOutListDao) getAppContext().getContext()
					.getBean("orderOutListDao");
			this.orderList = d.findAll("order_id_out = " + this.orderId);
		}
	}

	public void resetOderList() {
		this.orderList = new ArrayList<OrderOutList>();
	}

	public OrderOut getOrder() {
		return order;
	}

	public void setOrder(OrderOut order) {
		this.order = order;
	}

	private OrderOutList selectedOrderList; //

	public OrderOutList getSelectedOrderList() {
		return selectedOrderList;
	}

	public void setSelectedOrderList(OrderOutList selectedOrderList) {
		this.selectedOrderList = selectedOrderList;
	}

	public void setOrderListOrderIn(Order o) {
		if (this.selectedOrderList != null) {
			if (o != null) {
				this.selectedOrderList.setOrder_id_in(o.getId());
			} else {
				this.selectedOrderList.setOrder_id_in(0L);
			}

		}
	}

	public void addRow() {
		this.orderList.add(new OrderOutList());
	}

	private OrderList selectedOrderIn;

	public OrderList getSelectedOrderIn() {
		return selectedOrderIn;
	}

	public void setSelectedOrderIn(OrderList selectedOrderIn) {
		this.selectedOrderIn = selectedOrderIn;
	}

	public void setOrderListMatnr(OrderList ol) {
		if (this.selectedOrderList != null) {
			if (ol != null) {
				this.selectedOrderList.setMatnr_id(ol.getMatnr_id());
				this.selectedOrderList.setQuantity_in(ol.getQuantity());
				this.selectedOrderList.setQuantity_out(ol.getQuantity());
			} else {
				this.selectedOrderList.setMatnr_id(0L);
				this.selectedOrderList.setQuantity_in(0);
				this.selectedOrderList.setQuantity_out(0);
			}
		}
	}

	@PostConstruct
	public void init() {
		// TODO Permission
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}
		prepareUserWerks();
		if (this.orderId != null && this.orderId.longValue() > 0L) {
			this.loadOrder();
			// System.out.println(this.order + " => " + this.order.getBukrs());
			this.loadOrderList();
			this.loadSelectedOrders();
			prepareOrderList(orderList);
		}
		loadInOrders();
	}

	public void Save() {
		try {
			if (this.order == null) {
				throw new DAOException("System error");
			}
			if (this.order.getId() == null) {
				this.Create();
			} else {
				this.Update();
			}
			GeneralUtil.doRedirect("/logistics/order/loout03.xhtml?orderId="
					+ this.order.getId());

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		if (this.order != null) {
			OrderOutService s = (OrderOutService) getAppContext().getContext()
					.getBean("orderOutService");

			this.order.setCreated_by(getUserData().getUserid());
			this.order.setUpdated_by(getUserData().getUserid());
			s.createOrder(this.order, this.orderList);
		}
	}

	private void loadOrder() {
		if (this.orderId != null && this.orderId.longValue() > 0) {
			OrderOutDao d = (OrderOutDao) getAppContext().getContext().getBean(
					"orderOutDao");
			this.order = d.find(this.orderId);
			if (this.order == null) {
				this.order = new OrderOut();
			}
		}

	}

	private void Update() {
		if (this.order != null) {
			OrderOutService s = (OrderOutService) getAppContext().getContext()
					.getBean("orderOutService");

			this.order.setUpdated_by(getUserData().getUserid());
			s.updateOrder(this.order, this.orderList);
		}
	}

	public void setOrderCustomer(Customer c) {
		if (this.order != null) {
			if (c != null) {
				order.setCustomer_id(c.getId());
			} else {
				order.setCustomer_id(0L);
			}
		}
	}

	public void deleteRow(OrderOutList ord) {
		List<OrderOutList> temp = new ArrayList<OrderOutList>();
		for (OrderOutList ol : this.orderList) {
			if (ord != ol) {
				temp.add(ol);
			}
		}

		this.orderList = temp;
	}

	private List<Long> selectedOrdersIds = new ArrayList<Long>();
	private List<Order> selectedOrders = new ArrayList<Order>();

	public List<Order> getSelectedOrders() {
		return selectedOrders;
	}

	public void setSelectedOrders(List<Order> selectedOrders) {
		this.selectedOrders = selectedOrders;
	}

	public void assignOrder(Order o) {
		if (o == null) {

		} else {
			if (this.selectedOrdersIds.contains(o.getId())) {
				return;
			}
			OrderListDao d = (OrderListDao) getAppContext().getContext()
					.getBean("orderListDao");
			for (OrderList ol : d.dynamicFindAll("order_id = " + o.getId())) {
				OrderOutList ool = new OrderOutList();
				ool.setMatnr_id(ol.getMatnr_id());
				ool.setOrder_id_in(o.getId());
				ool.setQuantity_in(ol.getQuantity());
				ool.setQuantity_out(ol.getQuantity());
				this.orderList.add(ool);
			}
			this.selectedOrdersIds.add(o.getId());
			this.selectedOrders.add(o);
		}
	}

	public Long getOrderWerksFrom(Long oId) {
		if (oId == null || oId == 0) {
			return null;
		}
		for (Order o : this.selectedOrders) {
			if (oId == o.getId()) {
				//return o.getWerks_from();
			}
		}
		return null;
	}

	private void loadSelectedOrders() {
		if (order != null) {

			this.selectedOrdersIds = new ArrayList<Long>();
			for (OrderOutList ol : this.orderList) {
				if (!this.selectedOrdersIds.contains(ol.getOrder_id_in())) {
					this.selectedOrdersIds.add(ol.getOrder_id_in());
				}
			}
			String[] ids = new String[this.selectedOrdersIds.size()];
			int i = 0;
			for (Long l : this.selectedOrdersIds) {
				if (l != null) {
					ids[i] = l.toString();
					i++;
				}
			}
			OrderDao d = (OrderDao) getAppContext().getContext().getBean(
					"orderDao");
			this.selectedOrders = d.findAll(String.format("id IN(%s)", "'"
					+ String.join("','", ids) + "'"));
		}
	}

	public void assignMatnr(Matnr mtnr) {
		if (this.selectedOrderList != null && mtnr != null) {
			this.selectedOrderList.setMatnr_id(mtnr.getMatnr());
		}
	}

	public void searchWerksMaterials() {
		MatnrListDao mlDao = (MatnrListDao) getAppContext().getContext()
				.getBean("matnrListDao");
		allWerksMaterials = mlDao.dynamicFind(" werks = " + werksId);
	}

	private Long werksId = 0L;

	public Long getWerksId() {
		return werksId;
	}

	public void setWerksId(Long werksId) {
		this.werksId = werksId;
	}

	private List<MatnrList> allWerksMaterials = null;

	public List<MatnrList> getAllWerksMaterials() {
		return allWerksMaterials;
	}

	private List<MatnrList> otherWerksMatnrs = new ArrayList<MatnrList>();

	public List<MatnrList> getOtherWerksMatnrs() {
		return otherWerksMatnrs;
	}
	
	private String searchingMatnrName;
	public String getSearchingMatnrName() {
		return searchingMatnrName;
	}

	public void loadOtherWerksMatnrs(Long matnr) {
		MatnrDao md = getMatnrDao();
		List<Matnr> mtnr = md.findAll("matnr = " + matnr);
		System.out.println(mtnr.size());
		if (mtnr.size() > 0) {
			searchingMatnrName = mtnr.get(0).getText45();
			MatnrListDao d = getMatnrListDao();
			otherWerksMatnrs = d.dynamicFind("matnr = " + matnr);
			for (int i = 0; i < otherWerksMatnrs.size(); i++) {
				otherWerksMatnrs.get(i).setWerksName(
						getWerksF4Bean().getName(
								otherWerksMatnrs.get(i).getWerks().toString()));
			}
		}
	}
	
	private String pageHeader = "Внешний заказ";
	public String getPageHeader() {
		if(order.getId() != null){
			pageHeader += " №" + order.getId(); 
		}
		return pageHeader;
	}

	/******* ORDERS IN ******/
	private List<Order> inOrders = new ArrayList<Order>();

	public List<Order> getInOrders() {
		return inOrders;
	}

	public void loadInOrders() {
		inOrders.clear();
		OrderDao d = (OrderDao) getAppContext().getContext()
				.getBean("orderDao");
		if (getUserWerksIds().length == 0) {
			return;
		}
		String condition = String.format("IN_STATUS = %d AND werks_to IN(%s)",
				OrderStatus.STATUS_IN_NEW,
				"'" + String.join("','", getUserWerksIds()) + "'");
//		for (Order o : d.findAll(condition)) {
//			o.setWerksFromName(werksF4Bean
//					.getName(o.getWerks_from().toString()));
//			inOrders.add(o);
//		}
	}

	private List<Order> selectedInOrders = new ArrayList<Order>();

	public List<Order> getSelectedInOrders() {
		return selectedInOrders;
	}

	public void setSelectedInOrders(List<Order> selectedInOrders) {
		this.selectedInOrders = selectedInOrders;
	}

	public void assignSelectedInOrders() {

	}

	/***** ORDERS IN END ******/

}
