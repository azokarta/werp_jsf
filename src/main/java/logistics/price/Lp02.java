package logistics.price;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.PriceListDao;
import general.services.PriceListService;
import general.tables.Matnr;
import general.tables.PriceList;
import user.User;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "lp02Bean")
@ViewScoped
public class Lp02 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5444688342501043011L;
	private static final long transactionId = 100L;
	/**
	 * 
	 */

	@PostConstruct
	public void init() {
		if(GeneralUtil.isAjaxRequest()){
			return;
		}
		
		PermissionController.canWriteRedirect(userData, transactionId);
		loadMatnrList();
	}

	List<PriceList> priceList = new ArrayList<PriceList>();
	private String bukrs;
	private Long countryId;
	private Date fromDate;
	private String currency;
	private Long branch_id;
	private Long month_type;
	private Long prem_div;

	public Long getPrem_div() {
		return prem_div;
	}

	public void setPrem_div(Long prem_div) {
		this.prem_div = prem_div;
	}

	public List<PriceList> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<PriceList> priceList) {
		this.priceList = priceList;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Long getMonth_type() {
		return month_type;
	}

	public void setMonth_type(Long month_type) {
		this.month_type = month_type;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	private Matnr selectedMatnr;

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	public void assignMatnr() {
		boolean isExisted = false;
		for (PriceList pl : priceList) {
			if (pl.getMatnr().equals(selectedMatnr.getMatnr())) {
				isExisted = true;
				break;
			}
		}

		if (!isExisted) {
			PriceList pl = new PriceList();
			pl.setMatnr(selectedMatnr.getMatnr());
			pl.setMatnrObject(selectedMatnr);
			pl.setMonth(0);
			pl.setPrem_div(1L);
			priceList.add(pl);

			PriceList lastPL = getPrevPL(selectedMatnr.getMatnr());
			if (lastPL != null) {
				lastPricesMap.put(selectedMatnr.getMatnr(), lastPL);
			}
		}

		selectedMatnr = null;
	}

	private Map<Long, PriceList> lastPricesMap = new HashMap<Long, PriceList>();

	public String getLastPrice(Long matnrId) {
		if (lastPricesMap.get(matnrId) != null) {
			PriceList lp = lastPricesMap.get(matnrId);
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			return String.format("%s: %s %s", sdf.format(lp.getFrom_date()), lp.getPrice(), lp.getWaers());
		}

		return null;
	}

	private PriceList getPrevPL(Long matnrId) {
		PriceListDao plDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
		String cond = String.format(" bukrs = '%s' AND matnr = %d ORDER BY from_date DESC", getBukrs(), matnrId);
		List<PriceList> pList = plDao.findAll(cond);
		if (pList.size() > 0) {
			return pList.get(0);
		}
		return null;
	}

	public void removeRow(PriceList pl) {
		priceList.remove(pl);
	}

	public void Save() {
		try {
			PriceListService plService = (PriceListService) appContext.getContext().getBean("priceListService");
			preparePriceList();
			plService.createPriceList(priceList);
			GeneralUtil.doRedirect("/logistics/price/List.xhtml");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void preparePriceList() {
		for (PriceList pl : priceList) {
			pl.setBukrs(getBukrs());
			pl.setCountry_id(getCountryId());
			pl.setFrom_date(getFromDate());
			pl.setWaers(getCurrency());
			pl.setBranch_id(getBranch_id());
			pl.setMonth_type(getMonth_type());
			pl.setPrem_div(getPrem_div());
		}
	}

	private List<Matnr> matnrList = new ArrayList<>();

	public List<Matnr> getMatnrList() {
		return matnrList;
	}

	public void loadMatnrList() {
		MatnrDao mDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrList = mDao.findAll();
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

	public User getUserData() {
		return userData;
	}
}