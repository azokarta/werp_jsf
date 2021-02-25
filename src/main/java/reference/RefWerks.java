package reference;

import f4.BukrsF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.WerksDao;
import general.services.WerksService;
import general.tables.Werks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="refWerksBean")
@ViewScoped
public class RefWerks implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1973545998210964182L;
	
	@PostConstruct
	public void init(){
		if(GeneralUtil.isAjaxRequest()){
			return;
		}
		loadItems();
	}
	
	private List<Werks> items = new ArrayList<Werks>();
	public List<Werks> getItems() {
		return items;
	}
	private void loadItems(){
		WerksDao wd = (WerksDao)appContext.getContext().getBean("werksDao");
		items = wd.findAll(searchModel.getCondition());
	}
	
	private WerksSearch searchModel = new WerksSearch();
	public WerksSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(WerksSearch searchModel) {
		this.searchModel = searchModel;
	}
	public void Search(){
		loadItems();
	}
	
	private Werks selected;
	public Werks getSelected() {
		return selected;
	}
	public void setSelected(Werks selected) {
		this.selected = selected;
	}
	
	public Werks prepareCreate(){
		selected = new Werks();
		return selected;
	}
	
	public void Reset(){
		selected = null;
	}
	
	public void Save(){
		try{
			if(selected == null){
				throw new DAOException("SELECTED IS NULL");
			}
			
			if(Validation.isEmptyLong(selected.getWerks())){
				Create();
			}else{
				Update();
			}
			bukrsF4bean.updateF4();
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("WerksCreateDialog");
			
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	private void Create(){
		getService().create(selected);
	}
	
	private void Update(){
		getService().update(selected);
	}

	private WerksService getService(){
		return (WerksService)appContext.getContext().getBean("werksService");
	}
	
	
	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{bukrsF4Bean}")
	BukrsF4 bukrsF4bean;
	public BukrsF4 getBukrsF4bean() {
		return bukrsF4bean;
	}
	public void setBukrsF4bean(BukrsF4 bukrsF4bean) {
		this.bukrsF4bean = bukrsF4bean;
	}
	
	public class WerksSearch{
		private String text45;
		private String bukrs;
		public String getText45() {
			return text45;
		}
		public void setText45(String text45) {
			this.text45 = text45;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		
		public String getCondition(){
			String cond = " 1 = 1";
			if(!Validation.isEmptyString(bukrs)){
				cond = " bukrs = '" + bukrs + "' ";
			}
			
			if(!Validation.isEmptyString(text45)){
				cond += (cond.length() > 0 ? " AND " : " ") + " text45 LIKE '%" + text45 + "%' ";
			}
			return cond;
		}
	}
	
}
