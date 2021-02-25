package logistics.price;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.services.PriceListService;
import general.tables.Matnr;
import general.tables.PaymentTemplate;
import general.tables.PriceList;
import user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="lp01Bean")
@ViewScoped
public class Lp01 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long transactionId = 100L;
	private PriceList newPrice = new PriceList();
	private String bukrs;
	Matnr tempSelectedMatnr;
	Matnr selectedMatnr = new Matnr();
	PriceList priceList = new PriceList();
	List<PaymentTemplate> ptL = new ArrayList<PaymentTemplate>();
	
	public PriceList getPriceList() {
		return priceList;
	}
	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}
	
	public List<PaymentTemplate> getPtL() {
		return ptL;
	}
	public void setPtL(List<PaymentTemplate> ptL) {
		this.ptL = ptL;
	}
	public PriceList getNewPrice() {
		return newPrice;
	}
	public void setNewPrice(PriceList newPrice) {
		this.newPrice = newPrice;
	}
	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}
	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}
	
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	
	public Matnr getTempSelectedMatnr() {
		return tempSelectedMatnr;
	}
	public void setTempSelectedMatnr(Matnr tempSelectedMatnr) {
		this.tempSelectedMatnr = tempSelectedMatnr;
	}
	@PostConstruct
	public void init(){
		//TODO Permission
		if(GeneralUtil.isAjaxRequest()){
			return;
		}
		
		PermissionController.canWriteRedirect(userData, transactionId);
	}
	
	public void assignMatnr(){
		this.selectedMatnr = this.tempSelectedMatnr;
		if(this.selectedMatnr == null){
			this.newPrice.setMatnr(0L);
		}else{
			this.newPrice.setMatnr(this.selectedMatnr.getMatnr());
		}
	}
	
	public void addRow(){
		this.ptL.add(new PaymentTemplate(ptL.size()));
	}
	
	public void removeRow(PaymentTemplate pt){
		if(this.ptL.contains(pt)){
			this.ptL.remove(pt);
		}
	}
	
	public void Save(){
		try{
			PriceListService plService = (PriceListService)appContext.getContext().getBean("priceListService");
			if (!plService.checkPriceSum(newPrice, ptL)) {
				throw new DAOException("Ошибка в суммах графика платежей!");
			}
			this.preparePriceList();
			plService.createTovarPriceList(this.newPrice, this.ptL);
			GeneralUtil.doRedirect("/logistics/price/List.xhtml");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	private void preparePriceList(){
		newPrice.setPrice_list_id(null);
		for (PaymentTemplate pt:ptL)
			pt.setId(null);		
	}	
	
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public User getUserData() {
		return userData;
	}
}