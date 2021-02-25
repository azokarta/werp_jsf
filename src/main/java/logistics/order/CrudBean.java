package logistics.order;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.MatnrDao;
import general.dao.MatnrPriceDao;
import general.dao.OrderDao;
import general.dao.OrderMatnrDao;
import general.dao.RelatedDocsDao;
import general.dao.RequestOutDao;
import general.dao.RequestOutMatnrDao;
import general.dao.UserDao;
import general.services.OrderService;
import general.services.RelatedDocsService;
import general.tables.Customer;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.MatnrPrice;
import general.tables.Order;
import general.tables.OrderMatnr;
import general.tables.RelatedDocs;
import general.tables.RequestOut;
import general.tables.RequestOutMatnr;
import general.tables.Staff;

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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;

import datamodels.CustomerModel;
import datamodels.MatnrModel;
import user.User;

@ManagedBean(name = "logOrderCrudBean")
@ViewScoped
public class CrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4150990380989994766L;
	static final Long transactionId = 109L;
	static final String transactionCode = "LG_OR";

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {
				id = 0L;
			}
			loadExRateMap();
			prepareMatnrModel();
			prepareCustomerModel();
			loadStaffMap();
			loadMatnrMap();
		}
	}

	Map<String, ExchangeRate> exRateMap = new HashMap<>();

	private void loadExRateMap() {
		ExchangeRateDao erDao = (ExchangeRateDao) appContext.getContext().getBean("exchangeRateDao");
		exRateMap = erDao.getLastCurrencyRatesDynamicMap(" type = 1 ");
	}

	private Long id;
	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
		if (!GeneralUtil.isAjaxRequest()) {
			initBean();
		}
	}

	private Map<Long, Matnr> matnrMap;

	private void loadMatnrMap() {
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = d.getMappedList("");
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();

	private void loadStaffMap() {
		UserDao d = (UserDao) appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for (general.tables.User u : l) {
			stfMap.put(u.getUser_id(), u.getStaff());
		}
	}

	private Order order;

	private void loadOrder() {
		if (Validation.isEmptyLong(id)) {
			order = new Order();
		} else {
			OrderDao d = (OrderDao) appContext.getContext().getBean("orderDao");
			order = d.find(id);
			if (order == null) {
				order = new Order();
			}
		}
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	private void initBean() {
		loadOrder();
		loadOmList();
		if (mode.equals("create")) {

			loadAllDocsForParent();

		} else if (mode.equals("update")) {
			loadParentDocs();
			loadAllDocsForParent();

		} else if (mode.equals("view")) {
			prepareRelatedDocsTree();
			loadParentDocs();
		}
	}

	public void deleteParentDocRow(RequestOut ro) {
		parentDocs.remove(ro);
	}

	public void Save() {
		try {
			if (!PermissionController.canCreate(userData, transactionId)) {
				throw new DAOException(PermissionController.NO_PERMISSION_MSG);
			}
			if (mode.equals("create")) {
				Create();
			} else {
				Update();
			}

			GeneralUtil.doRedirect("/logistics/order/View.xhtml?id=" + order.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void generateData() {
		for (OrderMatnr om : omList) {
			om.setAmount(om.getQuantity() * om.getUnit_price());
		}
	}

	public Double getTotalRow() {
		Double totalRow = 0D;
		for (OrderMatnr om : omList) {
			om.setAmount(om.getQuantity() * om.getUnit_price());
			totalRow += om.getAmount();

		}
		return totalRow;
	}

	private void Create() {
		OrderService s = (OrderService) appContext.getContext().getBean("orderService");
		s.create(order, omList, parentDocs, userData);
	}

	private void Update() {
		OrderService s = (OrderService) appContext.getContext().getBean("orderService");
		s.update(order, omList, parentDocs, userData);
	}

	private OrderMatnr currentOm;

	public void setCurrentOm(OrderMatnr om) {
		this.currentOm = om;
	}

	public void deleteOmRow(OrderMatnr om) {
		omList.remove(om);
		selectedMatnrMap.remove(om.getMatnr());
	}

	public void addOmRow() {
		omList.add(new OrderMatnr());
	}

	private List<OrderMatnr> omList = new ArrayList<OrderMatnr>();

	public List<OrderMatnr> getOmList() {
		return omList;
	}

	private void loadOmList() {
		if (order != null) {
			OrderMatnrDao d = (OrderMatnrDao) appContext.getContext().getBean("orderMatnrDao");
			omList = d.findAll(" order_id = " + order.getId());
			for (OrderMatnr om : omList) {
				Matnr m = matnrMap.get(om.getMatnr());
				if (m != null) {
					om.setMatnrObject(m);
					selectedMatnrMap.put(m.getMatnr(), m);
				}
			}
		}
	}

	private MatnrModel matnrModel;

	public MatnrModel getMatnrModel() {
		return matnrModel;
	}

	private void prepareMatnrModel() {
		MatnrDao mDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrModel = new MatnrModel(mDao);
	}

	private Matnr selectedMatnr;

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	public void assignMatnr() {
		try {
			if (selectedMatnr != null) {
				if (selectedMatnrMap.containsKey(selectedMatnr.getMatnr())) {
					throw new DAOException(String.format("Материал %s уже есть в списке", selectedMatnr.getText45()));
				} else {
					if (Validation.isEmptyString(order.getBukrs())) {
						throw new DAOException("Выберите компанию");
					}

					if (Validation.isEmptyLong(order.getCountry_id())) {
						throw new DAOException("Выберите страну");
					}

					if (Validation.isEmptyLong(order.getCustomer_id())) {
						throw new DAOException("Выберите поставщика");
					}

					if (Validation.isEmptyString(order.getCurrency())) {
						throw new DAOException("Выберите валюту");
					}

					MatnrPriceDao mpDao = (MatnrPriceDao) appContext.getContext().getBean("matnrPriceDao");
					MatnrPrice matnrPrice = mpDao.findLastPrice(order.getBukrs(), order.getCountry_id(),
							order.getCustomer_id(), selectedMatnr.getMatnr());

					currentOm.setMatnr(selectedMatnr.getMatnr());
					currentOm.setMatnrObject(selectedMatnr);
					if (matnrPrice != null) {
						if (matnrPrice.getWaers().equals(order.getCurrency())) {
							currentOm.setUnit_price(matnrPrice.getPrice());
						} else {
							double usdVal = getUsdVal(matnrPrice.getWaers(), matnrPrice.getPrice());
							if ("USD".equals(order.getCurrency())) {
								currentOm.setUnit_price(usdVal);
							} else {
								ExchangeRate er = exRateMap.get(order.getCurrency());
								if (er == null) {
									currentOm.setUnit_price(0D);
								} else {
									currentOm.setUnit_price(usdVal * er.getSc_value());
								}
							}
						}
					}
					selectedMatnrMap.put(selectedMatnr.getMatnr(), selectedMatnr);
					selectedMatnr = null;
				}
			}
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private double getUsdVal(String currency, double val) {
		ExchangeRate er = exRateMap.get(currency);
		if (er == null) {
			return val;
		}

		double exVal = er.getSc_value();
		if (exVal == 0) {
			return 0;
		}

		return val / exVal;
	}

	private Map<Long, Matnr> selectedMatnrMap = new HashMap<Long, Matnr>();

	public void onSelectCustomer(SelectEvent e) {
		Customer c = (Customer) e.getObject();
		order.setCustomer(c);
		order.setCustomer_id(c.getId());
		GeneralUtil.updateFormElement("form:customer_id");
		GeneralUtil.hideDialog("CustomerListDialog");
	}

	public void removeCustomer() {
		if (order != null) {
			order.setCustomer(new Customer());
			order.setCustomer_id(0L);
		}
	}

	private CustomerModel customerModel;

	public CustomerModel getCustomerModel() {
		return customerModel;
	}

	public void setCustomerModel(CustomerModel customerModel) {
		this.customerModel = customerModel;
	}

	private void prepareCustomerModel() {
		customerModel = new CustomerModel((CustomerDao) appContext.getContext().getBean("customerDao"));
		customerModel.getSearchModel().setFiz_yur(1);
	}

	private List<RequestOut> parentDocs = new ArrayList<RequestOut>();
	private List<RequestOut> loadedParentDocs;
	private RequestOut selectedParentDoc;
	private Map<Long, RequestOut> selectedParentDocMap = new HashMap<Long, RequestOut>();

	public List<RequestOut> getParentDocs() {
		return parentDocs;
	}

	public void setParentDocs(List<RequestOut> parentDocs) {
		this.parentDocs = parentDocs;
	}

	public RequestOut getSelectedParentDoc() {
		return selectedParentDoc;
	}

	public void setSelectedParentDoc(RequestOut selectedParentDoc) {
		this.selectedParentDoc = selectedParentDoc;
	}

	public List<RequestOut> getLoadedParentDocs() {
		return loadedParentDocs;
	}

	private void generateOmList() {
		if (selectedParentDoc != null) {
			RequestOutMatnrDao romDao = (RequestOutMatnrDao) appContext.getContext().getBean("requestOutMatnrDao");
			List<RequestOutMatnr> romList = romDao.findAll(" request_id = " + selectedParentDoc.getId());
			for (RequestOutMatnr rom : romList) {
				if (!selectedMatnrMap.containsKey(rom.getMatnr())) {
					Matnr m = matnrMap.get(rom.getMatnr());
					if (m != null) {
						OrderMatnr om = new OrderMatnr();
						om.setAmount(0D);
						om.setMatnr(m.getMatnr());
						om.setMatnrObject(m);
						om.setQuantity(0D);
						om.setUnit_price(0D);
						omList.add(om);
						selectedMatnrMap.put(m.getMatnr(), m);
					}
				}
			}
		}
	}

	public void assignParentDoc() {
		if (selectedParentDoc != null) {
			if (selectedParentDocMap.containsKey(selectedParentDoc.getId())) {
				GeneralUtil.addErrorMessage("Выбранный документ уже есть в списке");
			} else {
				parentDocs.add(selectedParentDoc);
				selectedParentDocMap.put(selectedParentDoc.getId(), selectedParentDoc);
				generateOmList();
				selectedParentDoc = null;

			}
		}
	}

	public void changeBukrs() {
		parentDocs = new ArrayList<RequestOut>();
		omList = new ArrayList<OrderMatnr>();
		selectedMatnrMap = new HashMap<Long, Matnr>();
	}

	Map<String, List<RequestOut>> loadedParentDocsMap;

	public void loadAllDocsForParent() {
		loadedParentDocs = new ArrayList<RequestOut>();
		RequestOutDao d = (RequestOutDao) appContext.getContext().getBean("requestOutDao");
		if (loadedParentDocsMap == null) {
			loadedParentDocsMap = new HashMap<String, List<RequestOut>>();
			loadedParentDocsMap.put("1000", new ArrayList<RequestOut>());
			loadedParentDocsMap.put("2000", new ArrayList<RequestOut>());
			List<RequestOut> l = d.findAll(" status_id = " + RequestOut.STATUS_OPENED + " ORDER BY id DESC");
			for (RequestOut r : l) {
				Staff stf = stfMap.get(r.getCreated_by());
				if (stf == null) {
					r.setCreator(new Staff());
				} else {
					r.setCreator(stf);
				}

				loadedParentDocsMap.get(r.getBukrs()).add(r);
			}
		}

		if (!Validation.isEmptyString(order.getBukrs())) {
			loadedParentDocs = loadedParentDocsMap.get(order.getBukrs());
		}
	}

	private void loadParentDocs() {
		RelatedDocsDao d = (RelatedDocsDao) appContext.getContext().getBean("relatedDocsDao");
		List<RelatedDocs> rList = d
				.findAll(String.format(" context = '%s' AND context_id = %d ", Order.CONTEXT, order.getId()));
		if (rList.size() > 0) {
			String[] ids = new String[rList.size()];
			for (int i = 0; i < rList.size(); i++) {
				ids[i] = rList.get(i).getParent_id().toString();
			}

			List<RelatedDocs> l = d.findAll(String.format(" id IN(%s) ", "'" + String.join("','", ids) + "'"));
			if (l.size() > 0) {

				String[] ids2 = new String[l.size()];
				for (int i = 0; i < l.size(); i++) {
					ids2[i] = l.get(i).getContext_id().toString();
				}

				RequestOutDao roDao = (RequestOutDao) appContext.getContext().getBean("requestOutDao");
				parentDocs = roDao.findAll(String.format(" id IN(%s) ", "'" + String.join("','", ids2) + "'"));
				for (RequestOut ro : parentDocs) {
					Staff stf = stfMap.get(ro.getCreated_by());
					if (stf == null) {
						ro.setCreator(new Staff());
					} else {
						ro.setCreator(stf);
					}
				}
			}
		}
	}

	private TreeNode relatedDocsTree;

	public TreeNode getRelatedDocsTree() {
		return relatedDocsTree;
	}

	public void setRelatedDocsTree(TreeNode relatedDocsTree) {
		this.relatedDocsTree = relatedDocsTree;
	}

	private void prepareRelatedDocsTree() {
		RelatedDocsService rds = (RelatedDocsService) appContext.getContext().getBean("relatedDocsService");
		relatedDocsTree = rds.getRelatedTree(order.getId(), Order.CONTEXT);
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
