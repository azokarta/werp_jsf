package service.packets;

import f4.BukrsF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.ExchangeRateF4;
import f4.MatnrF4;
import f4.PriceListF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.ServPacketDao;
import general.dao.ServPacketPosDao;
import general.dao.ServPacketWarDao;
import general.output.tables.ServPacketPosOutput;
import general.output.tables.ServPacketWarOutput;
import general.tables.Bukrs;
import general.tables.Country;
import general.tables.Currency;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.PriceList;
import general.tables.ServPacket;
import general.tables.ServPacketPos;
import general.tables.ServPacketWar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import logistics.order.MatnrPriceList;
import service.OperTypeClass;
import user.User;

@ManagedBean(name = "spViewBean", eager = true)
@ViewScoped
public class ServPacketView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 215L;
	private final static String transaction_code = "SPVIEW";
	private final static Long transaction_id = (long) 215;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;

	// ******************************************************************

	@PostConstruct
	public void init() {
		try {
			// disable_save_button();
			PermissionController.canRead(userData, transaction_id);
			country_list = p_countryF4Bean.getCountry_list();
			bukrs_list = p_bukrsF4Bean.getBukrs_list();
						
			if (!Validation.isEmptyLong(sp_id)) {
				System.out.println("sp_id = " + sp_id);
				to_search(sp_id);
			}
				
			
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}
	
	public void to_search(Long a_spId) {
		ServPacketDao spDao = (ServPacketDao) appContext.getContext().getBean("servPacketDao");
		spTitle = spDao.find(a_spId);
		if (spTitle != null) {
			System.out.println("Found: " + spTitle.getName());
			
			ServPacketPosDao spPosDao = (ServPacketPosDao) appContext.getContext().getBean("servPacketPosDao");
			List<ServPacketPos> out_spPosL = spPosDao.findAllByServPacketID(a_spId);
			int i = 0;
			spPosL.clear();
			for (ServPacketPos spPos:out_spPosL) {
				i++;
				ServPacketPosOutput spPosO = new ServPacketPosOutput(i);
				spPosO.setSpPos(spPos);
				spPosO.setDis_mat(true);
				spPosO.setDis_qq(true);
				spPosL.add(spPosO);
			}
			
			ServPacketWarDao spWarDao = (ServPacketWarDao) appContext.getContext().getBean("servPacketWarDao");
			List<ServPacketWar> out_spWarL = spWarDao.findAllByServPacketId(a_spId);
			i = 0;
			spWarL.clear();
			for (ServPacketWar spWar:out_spWarL) {
				i++;
				ServPacketWarOutput spWarO = new ServPacketWarOutput(i);
				spWarO.setSpWar(spWar);
				spWarL.add(spWarO);
			}
			
			loadByCategory();
			prepareTable();
		} else {
			spTitle = new ServPacket();
			spPosL.clear();
			spWarL.clear();			
		}
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");		
	}
	
	public void searchById() {
		to_search(sp_id);
	}
	
	public void toEditPage() {
		GeneralUtil.doRedirect("/service/packets/spedit.xhtml?sp_id = " + sp_id);
	}
	
	public void loadByCategory() {
		tovar_list = new ArrayList<Matnr>();
		System.out.println("Bukrs = " + spTitle.getBukrs());
		System.out.println("In CAT = " + spTitle.getTovar_category());
		tovar_list = p_matnrF4Bean.getByBukrsAndCategory(spTitle.getBukrs(),
				spTitle.getTovar_category());
		System.out.println("Tovar list size: " + tovar_list.size());
		dis_selectTovar = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:tovar");
	}

	public void prepareTable() {
		ot_l.clear();
		ot_l.add(new OperTypeClass(1L, "ПРОДАЖА_ЗАП"));
		ot_l.add(new OperTypeClass(2L, "УСЛУГА"));

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");
		// reqCtx.update("form:olTable");
		reqCtx.update("form:add_button");
		reqCtx.update("form:save_button");
	}
	// ******************************************************************
	// ******************************************************************
	// ******************************************************************

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ******************************************************************
	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	// ******************************************************************
	// ***************************Country*******************************
	@ManagedProperty(value = "#{countryF4Bean}")
	private CountryF4 p_countryF4Bean;

	public CountryF4 getP_countryF4Bean() {
		return p_countryF4Bean;
	}

	public void setP_countryF4Bean(CountryF4 p_countryF4Bean) {
		this.p_countryF4Bean = p_countryF4Bean;
	}

	List<Country> country_list = new ArrayList<Country>();

	public List<Country> getCountry_list() {
		return country_list;
	}

	// ******************************************************************
	// ***************************BukrsF4*******************************
	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;

	public BukrsF4 getP_bukrsF4Bean() {
		return p_bukrsF4Bean;
	}

	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
		this.p_bukrsF4Bean = p_bukrsF4Bean;
	}

	List<Bukrs> bukrs_list = new ArrayList<Bukrs>();

	public List<Bukrs> getBukrs_list() {
		return bukrs_list;
	}

	// ******************************************************************
	// ***************************CurrencyF4*******************************
	@ManagedProperty(value = "#{currencyF4Bean}")
	private CurrencyF4 p_currencyF4Bean;

	public CurrencyF4 getP_currencyF4Bean() {
		return p_currencyF4Bean;
	}

	public void setP_currencyF4Bean(CurrencyF4 p_currencyF4Bean) {
		this.p_currencyF4Bean = p_currencyF4Bean;
	}

	List<Currency> currency_list = new ArrayList<Currency>();

	public List<Currency> getCurrency_list() {
		return currency_list;
	}

	// ************************************************************************
	// ***************************PriceListF4*******************************
	@ManagedProperty(value = "#{priceListF4Bean}")
	private PriceListF4 p_priceListF4Bean;

	public PriceListF4 getP_priceListF4Bean() {
		return p_priceListF4Bean;
	}

	public void setP_priceListF4Bean(PriceListF4 p_priceListF4Bean) {
		this.p_priceListF4Bean = p_priceListF4Bean;
	}

	List<PriceList> priceList_list = new ArrayList<PriceList>();

	public List<PriceList> getPriceList_list() {
		return priceList_list;
	}

	// ******************************************************************
	// ***************************MatnrF4*****************************************************
	@ManagedProperty(value = "#{matnrF4Bean}")
	private MatnrF4 p_matnrF4Bean;

	public MatnrF4 getP_matnrF4Bean() {
		return p_matnrF4Bean;
	}

	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
		this.p_matnrF4Bean = p_matnrF4Bean;
	}

	public List<MatnrPriceList> p_matnr_list = new ArrayList<MatnrPriceList>();

	public List<MatnrPriceList> getP_matnr_list() {
		return p_matnr_list;
	}

	public void setP_matnr_list(List<MatnrPriceList> p_matnr_list) {
		this.p_matnr_list = p_matnr_list;
	}

	private MatnrPriceList selectedMatnr = new MatnrPriceList();

	public MatnrPriceList getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(MatnrPriceList selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	// ***************************************************************************************
	// ***************************ExchangeRateF4*******************************
	@ManagedProperty(value = "#{exchangeRateF4Bean}")
	private ExchangeRateF4 p_exchangeRateF4Bean;

	public ExchangeRateF4 getP_exchangeRateF4Bean() {
		return p_exchangeRateF4Bean;
	}

	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
		this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
	}

	List<ExchangeRate> exchangeRate_list = new ArrayList<ExchangeRate>();

	public List<ExchangeRate> getExchangeRate_list() {
		return exchangeRate_list;
	}

	// ******************************************************************
	// ******************************************************************

	public ServPacket spTitle = new ServPacket();
	public List<ServPacketPosOutput> spPosL = new ArrayList<ServPacketPosOutput>();
	ServPacketPosOutput spPosRow;
	public List<ServPacketWarOutput> spWarL = new ArrayList<ServPacketWarOutput>();
	public ServPacketWarOutput spWarRow = new ServPacketWarOutput();
	public List<Matnr> tovar_list = new ArrayList<Matnr>();
	public boolean dis_selectTovar = true;
	public boolean dis_save_btn = true;
	public List<OperTypeClass> ot_l = new ArrayList<OperTypeClass>();
	public boolean forWar = false;
	public Long sp_id; 
	
	public Long getSp_id() {
		return sp_id;
	}

	public void setSp_id(Long sp_id) {
		this.sp_id = sp_id;
	}

	public boolean isForWar() {
		return forWar;
	}

	public void setForWar(boolean forWar) {
		this.forWar = forWar;
	}

	public ServPacketWarOutput getSpWarRow() {
		return spWarRow;
	}

	public void setSpWarRow(ServPacketWarOutput spWarRow) {
		this.spWarRow = spWarRow;
	}

	public boolean isDis_save_btn() {
		return dis_save_btn;
	}

	public void setDis_save_btn(boolean dis_save_btn) {
		this.dis_save_btn = dis_save_btn;
	}

	public List<OperTypeClass> getOt_l() {
		return ot_l;
	}

	public void setOt_l(List<OperTypeClass> ot_l) {
		this.ot_l = ot_l;
	}

	public void setPriceList_list(List<PriceList> priceList_list) {
		this.priceList_list = priceList_list;
	}

	public ServPacketPosOutput getSpPosRow() {
		return spPosRow;
	}

	public void setSpPosRow(ServPacketPosOutput spPosRow) {
		this.spPosRow = spPosRow;
	}

	public boolean isDis_selectTovar() {
		return dis_selectTovar;
	}

	public void setDis_selectTovar(boolean dis_selectTovar) {
		this.dis_selectTovar = dis_selectTovar;
	}

	public List<Matnr> getTovar_list() {
		return tovar_list;
	}

	public void setTovar_list(List<Matnr> tovar_list) {
		this.tovar_list = tovar_list;
	}

	public List<ServPacketPosOutput> getSpPosL() {
		return spPosL;
	}

	public void setSpPosL(List<ServPacketPosOutput> spPosL) {
		this.spPosL = spPosL;
	}

	public List<ServPacketWarOutput> getSpWarL() {
		return spWarL;
	}

	public void setSpWarL(List<ServPacketWarOutput> spWarL) {
		this.spWarL = spWarL;
	}

	public ServPacket getSpTitle() {
		return spTitle;
	}

	public void setSpTitle(ServPacket spTitle) {
		this.spTitle = spTitle;
	}

}
