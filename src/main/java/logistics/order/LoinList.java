package logistics.order;

import f4.WerksF4;
import general.AppContext;
import general.GeneralUtil;
import general.dao.OrderDao;
import general.dao.WerksBranchDao;
import general.tables.Order;
import general.tables.OrderStatus;
import general.tables.Werks;
import general.tables.WerksBranch;
import general.tables.search.OrderSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.TabChangeEvent;

import user.User;

@ManagedBean(name = "loinListBean")
@ViewScoped
public class LoinList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Integer, List<Order>> inOrders = new HashMap<Integer, List<Order>>();
	private Map<Integer, List<Order>> outOrders = new HashMap<Integer, List<Order>>();
	private Map<Integer, String> inStatuses = new HashMap<Integer, String>();
	private Map<Integer, String> outStatuses = new HashMap<Integer, String>();

	public Map<Integer, String> getInStatuses() {
		return inStatuses;
	}

	public Map<Integer, String> getOutStatuses() {
		return outStatuses;
	}

	public List<Order> getInOrders(Integer status) {
		return inOrders.get(status);
	}

	public List<Order> getOutOrders(Integer status) {
		return outOrders.get(status);
	}

	private String currentTab = "IN";
	public String getCurrentTab() {
		return currentTab;
	}

	private void prepareOrders() {
		inOrders.clear();
		outOrders.clear();
		OrderDao d = (OrderDao) appContext.getContext().getBean("orderDao");
		String condition = getPreparedCondition();
		Map<Integer, String> tempStatuses = new HashMap<Integer, String>();
		if (currentTab.equals("IN")) {
			
			String[] statusIds = new String[OrderStatus.getInStatuses().size()];
			int k = 0;
			for (Entry<Integer, String> e : OrderStatus.getInStatuses()
					.entrySet()) {
				statusIds[k] = e.getKey().toString();
				k++;
			}

			condition += (condition.length() > 0 ? " AND " : " ")
					+ String.format("in_status IN(%s) ",
							"'" + String.join("','", statusIds) + "'");
			List<Order> result = d.findAll(condition);
//			for (Order o : result) {
//				if (!inOrders.containsKey(o.getIn_status())) {
//					inOrders.put(o.getIn_status(), new ArrayList<Order>());
//				}
//
//				inOrders.get(o.getIn_status()).add(o);
//
//				if (!tempStatuses.containsKey(o.getIn_status())) {
//					tempStatuses.put(o.getIn_status(), OrderStatus
//							.getInStatuses().get(o.getIn_status()));
//				}
//			}

			inStatuses = tempStatuses;
		} else {
			
			String[] statusIds = new String[OrderStatus.getOutStatuses().size()];
			int k = 0;
			for (Entry<Integer, String> e : OrderStatus.getOutStatuses()
					.entrySet()) {
				statusIds[k] = e.getKey().toString();
				k++;
			}

			condition += (condition.length() > 0 ? " AND " : " ")
					+ String.format("out_status IN(%s) ",
							"'" + String.join("','", statusIds) + "'");

			List<Order> result = d.findAll(condition);

//			for (Order o : result) {
//				if (!outOrders.containsKey(o.getOut_status())) {
//					outOrders.put(o.getOut_status(), new ArrayList<Order>());
//				}
//
//				outOrders.get(o.getOut_status()).add(o);
//
//				if (!tempStatuses.containsKey(o.getOut_status())) {
//					tempStatuses.put(o.getOut_status(), OrderStatus
//							.getOutStatuses().get(o.getOut_status()));
//				}
//			}

			outStatuses = tempStatuses;
		}
	}

	private OrderStatus orderStatus = new OrderStatus();

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	private Order selected;

	public Order getSelected() {
		return selected;
	}

	public void setSelected(Order selected) {
		this.selected = selected;
	}

	private List<Werks> userWerksList = new ArrayList<Werks>();

	public List<Werks> getUserWerksList() {
		return userWerksList;
	}
	
	List<Werks> fromWerksList = new ArrayList<Werks>();
	List<Werks> toWerksList = new ArrayList<Werks>();
	
	public List<Werks> getFromWerksList() {
		if(currentTab.equals("IN")){
			if(userData.isSys_admin() || userData.isMain()){
				fromWerksList = werksF4Bean.getWerks_list();
			}else{
				fromWerksList = werksF4Bean.getByBukrs(userData.getBukrs());
			}
		}else{
			fromWerksList = userWerksList;
		}
		return fromWerksList;
	}

	public List<Werks> getToWerksList() {
		if(currentTab.equals("IN")){
			toWerksList = userWerksList;
		}else{
			if(userData.isSys_admin() || userData.isMain()){
				toWerksList = werksF4Bean.getWerks_list();;
			}else{
				toWerksList = werksF4Bean.getByBukrs(userData.getBukrs());;
			}
		}
		return toWerksList;
	}

	private String getPreparedCondition() {
		String condition = "";
		String[] werksIds = new String[userWerksList.size()];
		int i = 0;
		for (Werks w : userWerksList) {
			werksIds[i] = w.getWerks().toString();
			i++;
		}
		i = 0;
		List<Werks> allWerks;
		if(userData.isSys_admin() || userData.isMain()){
			allWerks = werksF4Bean.getWerks_list();
		}else{
			allWerks =werksF4Bean.getByBukrs(userData.getBukrs()); 
		}
		String[] allWerksIds = new String[allWerks.size()];
		for(Werks w:allWerks){
			allWerksIds[i] = w.getWerks().toString();
			i++;
		}
		

//		if (currentTab.equals("IN")) {
//			if (searchModel.getWerks_to() != null
//					&& searchModel.getWerks_to().longValue() > 0L) {
//				condition = " werks_to = " + searchModel.getWerks_to();
//			}else{
//				condition = String.format(" werks_to IN(%s) ",
//						"'" + String.join("','", werksIds) + "'");
//			}
//			
//			if (searchModel.getWerks_from() != null
//					&& searchModel.getWerks_from().longValue() > 0L) {
//				condition += " AND werks_from = " + searchModel.getWerks_from();
//			}else{
//				condition += String.format(" AND werks_from IN(%s) ",
//						"'" + String.join("','", allWerksIds) + "'");
//			}
//			
//		} else {
//			if (searchModel.getWerks_to() != null
//					&& searchModel.getWerks_to().longValue() > 0L) {
//				condition = " werks_to = " + searchModel.getWerks_to();
//			}else{
//				condition = String.format(" werks_to IN(%s) ",
//						"'" + String.join("','", allWerksIds) + "'");
//			}
//			//System.out.println("COND BEFORE: " + condition);
//			if (searchModel.getWerks_from() != null
//					&& searchModel.getWerks_from().longValue() > 0L) {
//				condition += " AND werks_from = " + searchModel.getWerks_from();
//			}else{
//				condition += String.format(" AND werks_from IN(%s) ",
//						"'" + String.join("','", werksIds) + "'");
//			}
		//}
		
		return "";//condition;
	}

	private void prepareUserWerks() {
		if (userData.isSys_admin() || userData.isMain()) {
			userWerksList = werksF4Bean.getWerks_list();
		} else {
			WerksBranchDao d = (WerksBranchDao) appContext.getContext()
					.getBean("werksBranchDao");
			for (WerksBranch wb : d.findAll("branch_id = "
					+ userData.getBranch_id())) {
				Werks temp = new Werks();
				temp.setBukrs(wb.getBukrs());
				temp.setText45(wb.getText45());
				temp.setWerks(wb.getWerks());
				userWerksList.add(temp);
			}
		}
	}
	
	private OrderSearch searchModel = new OrderSearch();
	public OrderSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(OrderSearch searchModel) {
		this.searchModel = searchModel;
	}
	
	public void Search(){
		prepareOrders();
	}

	@PostConstruct
	public void init() {
		// TODO PERMISSION
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}

		prepareUserWerks();
		prepareOrders();
		// loadItems();
	}

	public void onTabChange(TabChangeEvent event) {
		currentTab = event.getTab().getId();
		prepareOrders();
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

	public User getUserData() {
		return userData;
	}

	@ManagedProperty(value = "#{werksF4Bean}")
	private WerksF4 werksF4Bean;

	public void setWerksF4Bean(WerksF4 werksF4Bean) {
		this.werksF4Bean = werksF4Bean;
	}
}
