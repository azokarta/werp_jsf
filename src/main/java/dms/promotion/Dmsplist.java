package dms.promotion;

import f4.BranchF4;
import f4.BukrsF4;
import f4.ContractTypeF4;
import f4.CountryF4;
import f4.CurrencyF4;
import f4.ExchangeRateF4;
import f4.MatnrF4;
import general.AppContext;
import general.PermissionController;
import general.Validation;
import general.clone.Clone;
import general.dao.DAOException;
import general.dao.PromotionDao;
import general.services.PromotionService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.ContractType;
import general.tables.Country;
import general.tables.Currency;
import general.tables.ExchangeRate;
import general.tables.Matnr;
import general.tables.Promotion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "dmsplistBean", eager = true)
@ViewScoped
public class Dmsplist implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 89L;
	private final static String transaction_code = "DMSPLIST";
	private final static Long transaction_id = (long) 89;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;

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
	// ************************** BranchF4 ******************************
	// ******************************************************************
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 p_branchF4Bean;

	public BranchF4 getP_branchF4Bean() {
		return p_branchF4Bean;
	}

	public void setP_branchF4Bean(BranchF4 p_branchF4Bean) {
		this.p_branchF4Bean = p_branchF4Bean;
	}

	List<Branch> branch_list = new ArrayList<Branch>();

	public List<Branch> getBranch_list() {
		return branch_list;
	}

	List<Branch> region_list = new ArrayList<Branch>();

	public List<Branch> getRegion_list() {
		return region_list;
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

	// ******************************************************************
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
	// ***************************ContractTypeF4*******************************
	@ManagedProperty(value = "#{contractTypeF4Bean}")
	private ContractTypeF4 p_contractTypeF4Bean;

	public ContractTypeF4 getP_contractTypeF4Bean() {
		return p_contractTypeF4Bean;
	}

	public void setP_contractTypeF4Bean(ContractTypeF4 p_contractTypeF4Bean) {
		this.p_contractTypeF4Bean = p_contractTypeF4Bean;
	}

	List<ContractType> contractType_list = new ArrayList<ContractType>();

	public List<ContractType> getContractType_list() {
		return contractType_list;
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
	// ***************************MatnrF4*****************************************************
	@ManagedProperty(value = "#{matnrF4Bean}")
	private MatnrF4 p_matnrF4Bean;

	public MatnrF4 getP_matnrF4Bean() {
		return p_matnrF4Bean;
	}

	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
		this.p_matnrF4Bean = p_matnrF4Bean;
	}

	public List<Matnr> p_matnr_list = new ArrayList<Matnr>();

	public List<Matnr> getP_matnr_list() {
		return p_matnr_list;
	}

	// ******************************************************************
	// ***************************Promotion**********************************
	private Promotion search_promotion = new Promotion();

	public Promotion getP_promotion() {
		return search_promotion;
	}

	public void setP_promotion(Promotion s_promotion) {
		this.search_promotion = s_promotion;
	}

	// ***************** Get Currency & Rate from branch *******************

	public String getCurrencyName(Long wa_branch_id) {
		String out;
		out = "";
		for (Branch wa_branch : p_branchF4Bean.getBranch_list()) {
			if (wa_branch.getBranch_id().equals(wa_branch_id)) {
				for (Country wa_country : p_countryF4Bean.getCountry_list()) {
					if (wa_country.getCountry_id().equals(
							wa_branch.getCountry_id())) {

						for (Currency wa_currency : p_currencyF4Bean
								.getCurrency_list()) {
							if (wa_currency.getCurrency_id().equals(
									wa_country.getCurrency_id())) {
								out = wa_currency.getCurrency();
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return out;
	}

	public double getCurrencyRate(String main_currency, String sec_currency) {
		double out;
		out = 1;

		for (ExchangeRate wa_exr : p_exchangeRateF4Bean.getExchageRate_list()) {
			if (wa_exr.getMain_currency().equals(main_currency)
					&& wa_exr.getSecondary_currency().equals(sec_currency)) {
				out = wa_exr.getSc_value();
				break;
			}
		}

		return out;
	}

	public String p_currency;

	public String getP_currency() {
		return p_currency;
	}

	public void setP_currency(String p_currency) {
		this.p_currency = p_currency;
	}

	public double p_currate;

	public double getP_currate() {
		return p_currate;
	}

	public void setP_currate(double p_currate) {
		this.p_currate = p_currate;
	}

	// *********************************************************************
	// *********************************************************************
	@PostConstruct
	public void init() {

		search_promotion = new Promotion();
		try {

			p_currency = getCurrencyName(userData.getBranch_id());
			p_currate = getCurrencyRate("USD", p_currency);

			selectedPT = new PromoTable();
			
			PermissionController.canWrite(userData, transaction_id);
			
			if (userData.isMain()) {
				for (Bukrs wa_bukrs : p_bukrsF4Bean.getBukrs_list()) {
					bukrs_list.add(wa_bukrs);
				}
				search_promotion.setBukrs(bukrs_list.get(0).getBukrs());
			} else {
				bukrs_list.add(p_bukrsF4Bean.getBukrsByBukrs(userData.getBukrs()).get(0));
				search_promotion.setBukrs(userData.getBukrs());
				search_promotion.setBranch_id((long) userData.getBranch_id());
				selectedPT.getPromo().setBukrs(userData.getBukrs());
			}
			
			updateByNewBukrs();
			loadPromoTable(search_promotion);

		} catch (DAOException ex) {

			toMainPage();
			throw new DAOException("Error! " + ex.getMessage());
		}
	}

	// *****************************************************************************

	public void updateByNewBukrs() {
		String a_bukrs = userData.getBukrs(); 
		
		if (selectedPT != null) {
			a_bukrs = selectedPT.getPromo().getBukrs();
		}
		
		loadPromoMatnrListByBukrs(a_bukrs);
		loadBranchesByBukrs(a_bukrs);
		loadRegionsByBukrs(a_bukrs);
		loadContractTypeByBukrs(a_bukrs);		
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("PromotionCreateForm");
		reqCtx.update("PromotionEditForm");
	}

	public void loadPromoMatnrListByBukrs(String a_bukrs) {
		//System.out.println("BUKRS: " + a_bukrs);
		p_matnr_list = new ArrayList<Matnr>();
		p_matnrF4Bean.init();
		if (selectedPT != null) {
			for (Matnr m : p_matnrF4Bean.getMatnr_list()) {
				//System.out.println("Bukrs: " + m.getBukrs() + " - Matnr: " + m.getText45());
				if (m.getType() == 8) {
					p_matnr_list.add(m);
					//System.out.println(m.getText45());
				}
			}
		}
	}

	public void loadRegionsByBukrs(String a_bukrs) {
		region_list = new ArrayList<Branch>();
		for (Branch b : p_branchF4Bean.getBranch_list()) {
			if ((b.getType() == 2) && (b.getBukrs().equals(a_bukrs))) {
				region_list.add(b);
			}
		}
	}

	public void loadBranchesByBukrs(String a_bukrs) {
		branch_list = new ArrayList<Branch>();
		for (Branch b : p_branchF4Bean.getBranch_list()) {
			if ((b.getType() == 3) && (b.getBukrs().equals(a_bukrs))) {
				branch_list.add(b);
			}
		}
	}
	
	public void loadContractTypeByBukrs(String a_bukrs) {
		contractType_list = new ArrayList<ContractType>();
		contractType_list = p_contractTypeF4Bean.getContractListByBukrs(a_bukrs);
	}

	// *****************************************************************************

	public void prepareCreate() {

		if (selectedPT == null) {
			selectedPT = new PromoTable();
			selectedPT.getPromo().setFd_currency("USD");
			
			if (search_promotion.getBukrs() != null
					&& search_promotion.getBukrs().length() > 0) {
				selectedPT.getPromo().setBukrs(search_promotion.getBukrs());
			} else {
				selectedPT.getPromo().setBukrs(bukrs_list.get(0).getBukrs());
			}
		}
		updateByNewBukrs();
	}

	// *****************************************************************************

	public void prepareUpdate() {
		if (selectedPT != null) {
			
			loadContractTypeByBukrs(selectedPT.getPromo().getBukrs());
			
			selectedPTEdit = new PromoTable();
			
			selectedPTEdit.setIndex(selectedPT.getIndex());
			selectedPTEdit.setBranch(selectedPT.getBranch());
			selectedPTEdit.setBukr(selectedPT.getBukr());
			selectedPTEdit.setCountry(selectedPT.getCountry());
			selectedPTEdit.setMatnr(selectedPT.getMatnr());
			selectedPTEdit.setRegion(selectedPT.getRegion());
			
			Promotion psel = Clone.clonePromotion(selectedPT.getPromo());
			selectedPTEdit.setPromo(psel);
			
			updateEditZoneDisables();
			
			
		}		
	}
	public void loadPromoTable(Promotion s_promo) {

		List<Promotion> pm_list = new ArrayList<Promotion>();
		promoTable = new ArrayList<PromoTable>();

		PromotionDao promoDao = (PromotionDao) appContext.getContext().getBean(
				"promotionDao");

		pm_list = promoDao.findAll();
		int index = 0;
		for (Promotion p : pm_list) {
			index++;
			PromoTable pt = new PromoTable(index);

			pt.setPromo(p);

			pt.setBranch(p_branchF4Bean.getL_branch_map().get(p.getBranch_id()));

			for (Bukrs b : p_bukrsF4Bean.getBukrs_list()) {
				if (b.getBukrs().equals(p.getBukrs())) {
					pt.setBukr(b);
				}
			}

			for (Country c : p_countryF4Bean.getCountry_list()) {
				if (c.getCountry_id() == p.getCountry_id()) {
					pt.setCountry(c);
				}
			}

			pt.setMatnr(p_matnrF4Bean.getL_matnr_map().get(p.getMatnr()));

			promoTable.add(pt);
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:PromotionListForm");

	}

	// *****************************************************************************************************

	public void toMainPage() {
		try {
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			context.redirect(context.getRequestContextPath()
					+ "/general/mainPage.xhtml");
		} catch (Exception ex) {

			throw new DAOException("Error! " + ex.getMessage());
		}
	}

	// *****************************************************************************************************

	public PromoTable selectedPT;
	public List<PromoTable> promoTable;

	public PromoTable getSelectedPT() {
		return selectedPT;
	}

	public PromoTable selectedPTEdit;
	public PromoTable getSelectedPTEdit() {
		return selectedPTEdit;
	}

	public void setSelectedPTEdit(PromoTable selectedPTEdit) {
		this.selectedPTEdit = selectedPTEdit;
	}

	public void setSelectedPT(PromoTable selectedPT) {
		this.selectedPT = selectedPT;
	}

	public List<PromoTable> getPromoTable() {
		return promoTable;
	}

	public void setPromoTable(List<PromoTable> promoTable) {
		this.promoTable = promoTable;
	}

	// ******************************************************************************************************
	// ***************** LOAD BRANCH LIST *******************************
	// ******************************************************************

	public List<Branch> loadBranchList(String a_bukr) {
		List<Branch> wa_brlist = new ArrayList<Branch>();

		for (Branch wa_branch : p_branchF4Bean.getBranch_list()) {
			if (a_bukr.equals(wa_branch.getBukrs()) && wa_branch.getType() == 3) {
				wa_brlist.add(wa_branch);
			}
		}

		return wa_brlist;
	}

	public void loadBranch2() {
		branch_list = new ArrayList<Branch>();

		branch_list = loadBranchList(search_promotion.getBukrs());

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:branch");
		search_promotion.setPm_number(null);
		reqCtx.update("form:contr_num");

	}

	// *****************************************************************************
	
	public void create() {
		try {
			PromotionService promoService = (PromotionService) appContext
					.getContext().getBean("promotionService");
			Promotion p = new Promotion();
			p = selectedPT.getPromo();

			if (p != null) {
				p.setId(null);
				promoService.createPromo(p);
				
				//DAOException("New Promo #" + p.getPm_number() + " has been successfully created!");
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.execute("PF('PromotionCreateDialog').hide()");
				loadPromoTable(search_promotion);
			}
			else {
				throw new DAOException("Cannot save an Empty promotion!");
			}
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// ******************************************************************************************************

	public boolean dis_country_inp = true;
	public boolean dis_region_inp = true;
	public boolean dis_branch_inp = true;

	public boolean dis_discount_inp = true;
	public boolean dis_matnr_inp = false;
	public boolean dis_bonus_inp = true;

	public boolean isDis_discount_inp() {
		return dis_discount_inp;
	}

	public void setDis_discount_inp(boolean dis_discount_inp) {
		this.dis_discount_inp = dis_discount_inp;
	}

	public boolean isDis_country_inp() {
		return dis_country_inp;
	}

	public void setDis_country_inp(boolean dis_country_inp) {
		this.dis_country_inp = dis_country_inp;
	}

	public boolean isDis_region_inp() {
		return dis_region_inp;
	}

	public void setDis_region_inp(boolean dis_region_inp) {
		this.dis_region_inp = dis_region_inp;
	}

	public boolean isDis_branch_inp() {
		return dis_branch_inp;
	}

	public void setDis_branch_inp(boolean dis_branch_inp) {
		this.dis_branch_inp = dis_branch_inp;
	}

	public boolean isDis_matnr_inp() {
		return dis_matnr_inp;
	}

	public void setDis_matnr_inp(boolean dis_matnr_inp) {
		this.dis_matnr_inp = dis_matnr_inp;
	}

	public boolean isDis_bonus_inp() {
		return dis_bonus_inp;
	}

	public void setDis_bonus_inp(boolean dis_bonus_inp) {
		this.dis_bonus_inp = dis_bonus_inp;
	}

	public void updateZoneDisables() {
		dis_country_inp = true;
		dis_region_inp = true;
		dis_branch_inp = true;

		if (selectedPT.getPromo().getPm_scope().equals("GEN")) {
			dis_country_inp = false;
			dis_region_inp = true;
			dis_branch_inp = true;
			selectedPT.getPromo().setBranch_id(null);
			selectedPT.getPromo().setRegion_id(null);
		} else if (selectedPT.getPromo().getPm_scope().equals("REG")) {
			dis_country_inp = true;
			dis_region_inp = false;
			dis_branch_inp = true;
			selectedPT.getPromo().setBranch_id(null);
			selectedPT.getPromo().setCountry_id(null);
		} else if (selectedPT.getPromo().getPm_scope().equals("INT")) {
			dis_country_inp = true;
			dis_region_inp = true;
			dis_branch_inp = false;
			selectedPT.getPromo().setRegion_id(null);
			selectedPT.getPromo().setCountry_id(null);
		} else if (selectedPT.getPromo().getPm_scope().equals("COM")) {
			dis_country_inp = true;
			dis_region_inp = true;
			dis_branch_inp = true;
			selectedPT.getPromo().setBranch_id(null);
			selectedPT.getPromo().setRegion_id(null);
			selectedPT.getPromo().setCountry_id(null);
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("PromotionCreateForm:countryId");
		reqCtx.update("PromotionCreateForm:regId");
		reqCtx.update("PromotionCreateForm:branchId");
	}
	
	public void updateEditZoneDisables() {
		dis_country_inp = true;
		dis_region_inp = true;
		dis_branch_inp = true;

		if (selectedPTEdit.getPromo().getPm_scope().equals("GEN")) {
			dis_country_inp = false;
			dis_region_inp = true;
			dis_branch_inp = true;
			selectedPTEdit.getPromo().setBranch_id(null);
			selectedPTEdit.getPromo().setRegion_id(null);
		} else if (selectedPTEdit.getPromo().getPm_scope().equals("REG")) {
			dis_country_inp = true;
			dis_region_inp = false;
			dis_branch_inp = true;
			selectedPTEdit.getPromo().setBranch_id(null);
			selectedPTEdit.getPromo().setCountry_id(null);
		} else if (selectedPTEdit.getPromo().getPm_scope().equals("INT")) {
			dis_country_inp = true;
			dis_region_inp = true;
			dis_branch_inp = false;
			selectedPTEdit.getPromo().setRegion_id(null);
			selectedPTEdit.getPromo().setCountry_id(null);
		} else if (selectedPTEdit.getPromo().getPm_scope().equals("COM")) {
			dis_country_inp = true;
			dis_region_inp = true;
			dis_branch_inp = true;
			selectedPTEdit.getPromo().setBranch_id(null);
			selectedPTEdit.getPromo().setRegion_id(null);
			selectedPTEdit.getPromo().setCountry_id(null);
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("PromotionEditForm:countryId");
		reqCtx.update("PromotionEditForm:regId");
		reqCtx.update("PromotionEditForm:branchId");
	}

	public void updateTypeDisables() {

		if (selectedPT.getPromo().getPm_type() == 1) {
			dis_matnr_inp = false;
			dis_discount_inp = true;
			dis_bonus_inp = true;
			selectedPT.getPromo().setDiscount(0);
			selectedPT.getPromo().setBonus(null);
		} else if (selectedPT.getPromo().getPm_type() == 2) {
			dis_matnr_inp = true;
			dis_discount_inp = false;
			dis_bonus_inp = true;
			selectedPT.getPromo().setMatnr(null);
			;
			selectedPT.getPromo().setBonus(null);

		} else if (selectedPT.getPromo().getPm_type() == 3) {
			dis_discount_inp = true;
			dis_matnr_inp = true;
			dis_bonus_inp = false;
			selectedPT.getPromo().setMatnr(null);
			selectedPT.getPromo().setDiscount(0);
		} else {
			dis_discount_inp = true;
			dis_matnr_inp = true;
			dis_bonus_inp = true;
			selectedPT.getPromo().setMatnr(null);
			selectedPT.getPromo().setBonus(null);
			selectedPT.getPromo().setDiscount(0);
		}

		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("PromotionCreateForm");
		reqCtx.update("PromotionEditForm");
	}

	public void update() {
		try {

			PromotionService promoService = (PromotionService) appContext
					.getContext().getBean("promotionService");

			Promotion p = new Promotion();
			p = selectedPTEdit.getPromo();

			if (p != null) {
				promoService.updatePromo(p);
				
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.execute("PF('PromotionEditDialog').hide()");
				
				loadPromoTable(search_promotion);
			}
			else {
				throw new DAOException("Cannot update an Empty promotion!");
			}
			
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void destroy() {
		
	}
}
