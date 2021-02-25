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
import general.dao.MatnrSparePartDao;
import general.dao.MatnrWarDao;
import general.output.tables.ServPacketPosOutput;
import general.output.tables.ServPacketWarOutput;
import general.services.ServPacketService;
import general.tables.Bukrs;
import general.tables.Country;
import general.tables.Currency;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.MatnrSparePart;
import general.tables.MatnrWar;
import general.tables.PriceList;
import general.tables.ServPacket;
import general.tables.ServPacketPos;
import general.tables.ServPacketWar;
import general.tables.search.MatnrSearch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import logistics.order.MatnrPriceList;

import org.primefaces.context.RequestContext;

import service.OperTypeClass;
import user.User;

@ManagedBean(name = "spNewBean", eager = true)
@ViewScoped
public class ServPacketNew {
	/**
	 * 
	 */
	private static final long serialVersionUID = 213L;
	private final static String transaction_code = "SPNEW";
	private final static Long transaction_id = (long) 213;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;

	// ******************************************************************

	@PostConstruct
	public void init() {
		try {
			// disable_save_button();
			PermissionController.canWrite(userData, this.transaction_id);
			country_list = p_countryF4Bean.getCountry_list();

			p_countryF4Bean.getCountry_list();
			if (userData.isMain() || userData.isSys_admin()) {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					bukrs_list.add(wa_bukrs);
				}
				// p_service.setBukrs("3000");
			} else {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					if (wa_bukrs.getBukrs().equals(userData.getBukrs())) {
						bukrs_list.add(wa_bukrs);
						break;
					}
				}
				spTitle.setBukrs(userData.getBukrs());
			}

			Calendar cal = Calendar.getInstance();
			spTitle.setStart_date(cal.getTime());
			spTitle.setActive(1);

			spPosL.add(new ServPacketPosOutput(1));
			spWarL.add(new ServPacketWarOutput(1));

		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void assignCurrency() {
		Country c = p_countryF4Bean.getL_country_map().get(
				spTitle.getCountry_id());
		spTitle.setWaers(c.getCurrency());
		loadPriceList();
	}

	public void toMainPage() {
		GeneralUtil.doRedirect("/general/mainPage.xhtml");
	}

	public void toViewPage() {
		GeneralUtil.doRedirect("/service/packets/spview.xhtml?sp_id = "
				+ spTitle.getId());
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

	public void selectedBukrsDo() {
		System.out.println("Bukrs = " + spTitle.getBukrs());
	}

	public void prepareTable() {
		ot_l.clear();
		ot_l.add(new OperTypeClass(1L, "ПРОДАЖА_ЗАП"));
		ot_l.add(new OperTypeClass(2L, "УСЛУГА"));

		// reload matnr list
		loadMatnrPriceList();

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");
		// reqCtx.update("form:olTable");
		reqCtx.update("form:add_button");
		reqCtx.update("form:save_button");
	}

	public void loadMatnrPriceList() {
		p_matnr_list = new ArrayList<MatnrPriceList>();
		p_ml.clear();

		p_ml.addAll(loadMatnrByTovarID(spTitle.getTovar_id()));
		int count = 0;
		for (Matnr wa_matnr : p_ml) {
			count++;
			System.out.println(count);
			if (wa_matnr==null) continue;
			if (wa_matnr.getType() == 2) {
				MatnrPriceList mpl = new MatnrPriceList();
				mpl.setMatnr(wa_matnr);

				if (pl.get(wa_matnr.getMatnr()) != null) {
					mpl.setCurrency(pl.get(wa_matnr.getMatnr()).getWaers());
					mpl.setPrice(pl.get(wa_matnr.getMatnr()).getPrice());
					// System.out.println("New price from customer: " +
					// pl.get(wa_matnr.getMatnr()).getPrice() + " " +
					// pl.get(wa_matnr.getMatnr()).getWaers());
				} else {
					mpl.setCurrency(spTitle.getWaers());
					mpl.setPrice(0);
				}
				p_matnr_list.add(mpl);
			}
		}

		p_mpl = new ArrayList<MatnrPriceList>();
		p_mpl.addAll(p_matnr_list);

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("matnrForm:singleMatnr");
		reqCtx.update("form:matnrTableWidget");
	}

	public void refreshMatnrList() {
		p_matnr_list.clear();
		if (Validation.isEmptyString(matnrSearch.getCode())
				&& Validation.isEmptyString(matnrSearch.getText45())) {
			p_matnr_list.addAll(p_mpl);
		} else {
			// System.out.println(matnrSearch);
			// System.out.println(matnrSearch.getText45());
			for (MatnrPriceList mpl : p_mpl) {
				if (mpl.getMatnr()
						.getText45()
						.toLowerCase()
						.matches(
								"(?i).*"
										+ matnrSearch.getText45().toLowerCase()
										+ ".*")
						&& mpl.getMatnr()
								.getCode()
								.toLowerCase()
								.matches(
										"(?i).*"
												+ matnrSearch.getCode()
														.toLowerCase() + ".*")) {
					p_matnr_list.add(mpl);
				}
			}
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("matnrForm:singleMatnr");
	}

	public List<Matnr> loadMatnrByTovarID(Long tovID) {
		List<Matnr> lm = new ArrayList<Matnr>();
		MatnrSparePartDao mspDao = (MatnrSparePartDao) appContext.getContext()
				.getBean("matnrSparePartDao");
		List<MatnrSparePart> msp_l = mspDao.findAllByTovarID(tovID);
		System.out.println("Found sp for " + tovID + " total: " + msp_l.size());
		for (MatnrSparePart msp : msp_l) {
			lm.add(p_matnrF4Bean.getL_matnr_map().get(msp.getSparepart_id()));
		}
		return lm;
	}

	public Map<Long, PriceList> pl = new HashMap<Long, PriceList>();

	public void loadPriceList() {
		priceList_list = new ArrayList<PriceList>();

		for (PriceList wa_priceList2 : p_priceListF4Bean.getPriceListByCountry(
				spTitle.getBukrs(), spTitle.getCountry_id())) {

				priceList_list.add(wa_priceList2);
				pl.put(wa_priceList2.getMatnr(), wa_priceList2);

				System.out.println("Matnr_id: " + wa_priceList2.getMatnr()
						+ " - Price: " + wa_priceList2.getPrice() + " "
						+ wa_priceList2.getWaers());
		}
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:priceTable");
	}

	public void assignSelectedMatnr() {
		if (forWar) {
			System.out.println("Inside assign Matnr for War Method!");
			if (spWarRow != null) {
				spWarRow.getSpWar().setMatnr_id(
						selectedMatnr.getMatnr().getMatnr());
				spWarRow.getSpWar().setInfo(
						selectedMatnr.getMatnr().getText45());

				MatnrWarDao mwDao = (MatnrWarDao) appContext.getContext()
						.getBean("matnrWarDao");
				MatnrWar mw = mwDao.findByMatnr(selectedMatnr.getMatnr()
						.getMatnr());
				if (mw != null)
					spWarRow.getSpWar().setWar_months(mw.getWar_months());

				int pos = spWarRow.getIndex() - 1;
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form:spWarTable");
				reqCtx.update("form:spWarTable:" + pos + ":infoText");
				reqCtx.update("form:spWarTable:" + pos + ":war_mon");
			} else {
				System.out.println("SpWarRow is null!");
			}
		} else {
			System.out.println("Inside assign Matnr for Pos Method!");
			spPosRow.getSpPos()
					.setMatnr_id(selectedMatnr.getMatnr().getMatnr());
			spPosRow.getSpPos().setInfo(selectedMatnr.getMatnr().getText45());
			spPosRow.getSpPos().setQuantity(1L);
			PriceList pl2 = new PriceList();

			if (pl.get(selectedMatnr.getMatnr().getMatnr()) != null
					&& pl.get(selectedMatnr.getMatnr().getMatnr()).getMatnr() > 0) {
				pl2 = pl.get(selectedMatnr.getMatnr().getMatnr());
			}

			double rate = 1;
			
			if (pl2.getWaers() != null && pl2.getWaers().length() > 0) {
				rate = getCurrencyRate(pl2.getWaers(), spTitle.getWaers());
				System.out.println(pl2.getWaers() + " vs " + spTitle.getWaers() + " rate: "+ rate);
			}

			double price = pl2.getPrice() * rate;
			spPosRow.getSpPos().setPrice(price);
			spPosRow.getSpPos().setWaers(spTitle.getWaers());
			spPosRow.getSpPos().setQuantity(1L);
			spPosRow.getSpPos().setSumm(
					spPosRow.getSpPos().getQuantity()
							* spPosRow.getSpPos().getPrice());
			spPosRow.dis_qq = true;

			int pos = spPosRow.getIndex() - 1;
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:olTable");
			reqCtx.update("form:olTable:" + pos + ":infoText");
			reqCtx.update("form:olTable:" + pos + ":unitPrice");
			reqCtx.update("form:olTable:" + pos + ":b_menge");
			reqCtx.update("form:olTable:" + pos + ":b_wrbtr");

		}

		selectedMatnr = null;
	}
	
	// ***************** Get Currency & Rate from branch *******************

	public double getCurrencyRate(String main_currency, String sec_currency) {
		double out;
		out = 1;
		ExchangeRate wa_exrM = p_exchangeRateF4Bean.getL_er_map_national().get(
				"1" + main_currency);
		ExchangeRate wa_exrS = p_exchangeRateF4Bean.getL_er_map_national().get(
				"1" + sec_currency);

		if (wa_exrM != null && wa_exrM.getSc_value() > 0 && wa_exrS != null)
			out = wa_exrS.getSc_value() / wa_exrM.getSc_value();

		return out;
	}


	public void to_save() {
		ServPacketService spService = (ServPacketService) appContext
				.getContext().getBean("servPacketService");
		try {
			if (spTitle != null && spPosL != null && spWarL != null) {

				List<ServPacketPos> new_spPosL = new ArrayList<ServPacketPos>();
				for (ServPacketPosOutput spPosO : spPosL) {
					spPosO.getSpPos().setWaers(spTitle.getWaers());
					new_spPosL.add(spPosO.getSpPos());
				}

				List<ServPacketWar> new_spWarL = new ArrayList<ServPacketWar>();
				for (ServPacketWarOutput spWarO : spWarL)
					new_spWarL.add(spWarO.getSpWar());

				if (spService.createServPacket(spTitle, new_spPosL, new_spWarL,
						userData, transaction_code, transaction_id))
					GeneralUtil
							.addInfoMessage("Success! New Service Packet has been successfully created!");

				toViewPage();
			} else
				throw new DAOException(
						"Please fill up all the required fields before saving!");
		} catch (DAOException ex) {
			disable_save_button();
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	public void removeEmptyRows() {
		for (int i = 0; i < spPosL.size(); i++) {
			// System.out.println("Inspecting the Row: " + i);
			if (spPosL.get(i).getSpPos().getMatnr_id() == null) {
				if ((spPosL.get(i).getSpPos().getOperation() != null)
						&& ((spPosL.get(i).getSpPos().getOperation() == 1) || (spPosL
								.get(i).getSpPos().getOperation() == 2 && spPosL
								.get(i).getSpPos().getInfo().isEmpty()))) {
					deleteSPPosRow(i + 1);
					// System.out.println("Deleting the row: " + i);
					i--;
				}
			}
		}
		removeEmptyWarRows();
		enable_save_button();
		calcRow(spPosL.size());
	}

	public ServPacketPosOutput addPosRow() {
		spPosL.add(new ServPacketPosOutput(spPosL.size() + 1));
		// servPosListTable.get(servPosListTable.size()-1).getServPos().setOperation(1L);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");
		disable_save_button();
		return spPosL.get(spPosL.size() - 1);
	}

	public void updateByOperType(int row) {
		int pos = row - 1;
		switch (spPosL.get(pos).getSpPos().getOperation().intValue()) {
		case 2: {
			spPosL.get(pos).getSpPos().setMatnr_id(null);
			spPosL.get(pos).setDis_mat(true);
			spPosL.get(pos).getSpPos().setQuantity(1L);
			spPosL.get(pos).setDis_qq(true);
			break;
		}
		default: {
			spPosL.get(pos).setDis_mat(false);
			spPosL.get(pos).setDis_qq(false);
			break;
		}
		}
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olTable:" + pos + ":mat");
		reqCtx.update("form:olTable:" + pos + ":b_matnr");
		reqCtx.update("form:olTable:" + pos + ":b_menge");
		calcRow(row);
	}

	public void calcRow(int row) {
		if (row > 0) {
			int pos = row - 1;
			int numRow = 0;
			ServPacketPos sp2 = spPosL.get(pos).getSpPos();

			if (sp2 != null && sp2.getQuantity() != null) {
				spPosL.get(pos).getSpPos()
						.setSumm(sp2.getQuantity() * sp2.getPrice());
			}

			double sum = 0;
			for (int i = 0; i < spPosL.size(); i++) {
				sum += spPosL.get(i).getSpPos().getSumm();
				numRow += 1;
			}

			spTitle.setSumm(sum);
			spTitle.setPrice(sum - spTitle.getDiscount());

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:olTable:" + pos + ":b_wrbtr");
			reqCtx.update("form:olTable:" + (numRow - 1) + ":summarySC");
			reqCtx.update("form:totalSumm");
			reqCtx.update("form:paymentDue");
			reqCtx.update("form:discCurrency");
			reqCtx.update("form:paymnetCurrency");
			reqCtx.update("form:summCurrency");
			reqCtx.update("form:masterCurrency");
			reqCtx.update("form:operCurrency");

		}
	}

	public void deleteSPPosRow(int row) {
		int j = row - 1;
		spPosL.remove(j);

		while (j < spPosL.size()) {
			spPosL.get(j).setIndex(j + 1);
			j++;
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:olScrollPanel");
		disable_save_button();
	}

	public void removeEmptyWarRows() {
		for (int i = 0; i < spWarL.size(); i++) {
			if ((spWarL.get(i).getSpWar().getMatnr_id() == null)
					|| (spWarL.get(i).getSpWar().getWar_months() == 0)) {
				deleteSPWarRow(i + 1);
				i--;
			}
		}
	}

	public void deleteSPWarRow(int row) {
		int j = row - 1;
		spWarL.remove(j);

		while (j < spWarL.size()) {
			spWarL.get(j).setIndex(j + 1);
			j++;
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:spWarScrollPanel");
		disable_save_button();
	}

	public ServPacketWarOutput addWarRow() {
		spWarL.add(new ServPacketWarOutput(spWarL.size() + 1));
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:spWarScrollPanel");
		disable_save_button();
		return spWarL.get(spWarL.size() - 1);
	}

	public void disable_save_button() {
		dis_save_btn = true;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:save_button");
	}

	public void enable_save_button() {
		dis_save_btn = false;
		RequestContext reqCtx = RequestContext.getCurrentInstance();
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

	private MatnrSearch matnrSearch = new MatnrSearch();

	public MatnrSearch getMatnrSearch() {
		return matnrSearch;
	}

	public void setMatnrSearch(MatnrSearch matnrSearch) {
		this.matnrSearch = matnrSearch;
	}

	public List<MatnrPriceList> p_mpl = new ArrayList<MatnrPriceList>();
	public List<Matnr> p_ml = new ArrayList<Matnr>();
	
}
