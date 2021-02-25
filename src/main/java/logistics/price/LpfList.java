package logistics.price;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrPriceDao;
import general.services.MatnrPriceService;
import general.tables.Customer;
import general.tables.Matnr;
import general.tables.MatnrPrice;
import general.tables.search.MatnrPriceSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.hibernate.annotations.common.util.StringHelper;

import user.User;

@ManagedBean(name = "lpfListBean")
@ViewScoped
public class LpfList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MatnrPrice selected;
	private Matnr selectedMatnr;

	@PostConstruct
	public void init() {
		// TODO PERMISSION
		if(GeneralUtil.isAjaxRequest()){
			return;
		}
		
		loadItems();
		loadCustomerList();
	}

	public MatnrPrice getSelected() {
		return selected;
	}

	public void setSelected(MatnrPrice selected) {
		this.selected = selected;
		if (selected == null) {
			this.selectedMatnr = null;
		} else if (this.selected.getMatnr() != null) {
			MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
			this.selectedMatnr = d.find(this.selected.getMatnr());
		}
	}

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	public void Save() {
		try {
			if (this.selected == null) {
				throw new DAOException("Элемент не выбран");
			}
			if (this.selectedMatnr != null) {
				this.selected.setMatnr(this.selectedMatnr.getMatnr());
			}
			this.selected.setUpdated_by(userData.getUserid());
			if (this.selected.getMp_id() == null) {
				this.selected.setCreated_by(userData.getUserid());
				this.Create();
			} else {
				this.Update();
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно");
			GeneralUtil.hideDialog("MatnrPriceCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public MatnrPrice prepareCreate() {
		this.selected = new MatnrPrice();
		this.selectedMatnr = null;
		return this.selected;
	}

	private void Update() {
		MatnrPriceService mpService = (MatnrPriceService) appContext
				.getContext().getBean("matnrPriceService");
		mpService.update(this.selected);
	}

	private void Create() {
		MatnrPriceService mpService = (MatnrPriceService) appContext
				.getContext().getBean("matnrPriceService");
		mpService.create(this.selected);
	}

	public void assignMatnr(Matnr m) {
		this.selectedMatnr = m;
	}

	public void Reset() {

	}

	
	/***********************/
	
	private MatnrPriceSearch searchModel = new MatnrPriceSearch();
	private List<MatnrPrice> items;
	public List<MatnrPrice> getItems() {
		return items;
	}
		
	public MatnrPriceSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(MatnrPriceSearch searchModel) {
		this.searchModel = searchModel;
	}

	private void loadItems(){
		MatnrPriceDao mpDao = (MatnrPriceDao)appContext.getContext().getBean("matnrPriceDao");
		this.items = mpDao.findAll(getPreparedCondition());
	}
	
	private String getPreparedCondition(){
		String cond = searchModel.getCondition();
		if(!Validation.isEmptyString(searchModel.getMatnrName())){
			String cond2 = "";
			MatnrDao d = (MatnrDao)appContext.getContext().getBean("matnrDao");
			List<Matnr> mList = d.findAll(String.format(" text45 LIKE '%s' ", searchModel.getMatnrName() + "%"));
			if(mList.size() > 0){
				String[] ids = new String[mList.size()];
				for(int i = 0; i < mList.size(); i++){
					ids[i] = mList.get(i).getMatnr().toString();
				}
				cond2 = String.format(" matnr IN(%s)", "'" + String.join("','", ids) + "'");
			}else{
				cond2 = " 1 = 0 ";
			}
			
			cond += (cond.length() > 0 ? " AND " : " ") + cond2 + " ";
		}
		return cond;
	}
	
	public void Search(){
		loadItems();
	}
	
	private List<Customer> customerList = new ArrayList<Customer>();
	public String getCustomerName(Long custId){
		if(custId != null && custId > 0){
			for(Customer c:customerList){
				if(c.getId().equals(custId)){
					return c.getName();
				}
			}
		}
		
		return "";
	}
	
	public List<Customer> getCustomerList() {
		return customerList;
	}

	private void loadCustomerList(){
		CustomerDao d = (CustomerDao)appContext.getContext().getBean("customerDao");
		customerList = d.findAll("fiz_yur = 1");
//		String[] ids = new String[items.size()];
//		int counter = 0;
//		for(MatnrPrice mp:items){
//			ids[counter] = mp.getCustomer_id().toString();
//			counter++;
//		}
//		if(ids.length > 0){
//			String condition = String.format(" customer_id IN (%s) ", "'" + StringHelper.join("','", ids) + "'");
//			customerList = d.findAll(condition);
//		}else{
//			customerList = new ArrayList<Customer>();
//		}
	}
	

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
