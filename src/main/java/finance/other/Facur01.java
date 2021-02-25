package finance.other;

import f4.CurrencyF4;
import f4.ExchangeRateF4;
import f4.HkontF4;
import f4.UpdateF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.FmglflextDao;
import general.services.AccountingOtherService;
import general.services.FinanceService;
import general.tables.Bkpf;
import general.tables.Bseg;
import general.tables.Currency;
import general.tables.ExchangeRate;
import general.tables.Fmglflext;
import general.tables.Hkont;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "facur01Bean")
@ViewScoped
public class Facur01 implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "FACUR01";
	private final static Long transaction_id = (long) 42;
	public static Long getTransactionId() {
		return transaction_id;
	}
	//private final static Long read = (long) 1;
	//private final static Long write = (long) 2;

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
	// ***************************HkontF4***************************
	@ManagedProperty(value = "#{hkontF4Bean}")
	private HkontF4 p_hkontF4Bean;

	public HkontF4 getP_hkontF4Bean() {
		return p_hkontF4Bean;
	}

	public void setP_hkontF4Bean(HkontF4 p_hkontF4Bean) {
		this.p_hkontF4Bean = p_hkontF4Bean;
	}
	
	@ManagedProperty(value = "#{updateF4Bean}")
	private UpdateF4 updateF4Bean;
	
	
	public void setUpdateF4Bean(UpdateF4 updateF4Bean) {
		this.updateF4Bean = updateF4Bean;
	}

	// ******************************************************************
	// *************************** CurrencyF4 ***************************
	@ManagedProperty(value = "#{currencyF4Bean}")
	private CurrencyF4 p_curF4Bean;

	public CurrencyF4 getP_curF4Bean() {
		return p_curF4Bean;
	}

	public void setP_curF4Bean(CurrencyF4 p_curF4Bean) {
		this.p_curF4Bean = p_curF4Bean;
	}

	List<Currency> l_cur = new ArrayList<Currency>();

	public List<Currency> getL_cur() {
		return l_cur;
	}

	public void setL_cur(List<Currency> l_cur) {
		this.l_cur = l_cur;
	}

	// ******************************************************************
	// *************************** Exchange Rate F4 ***************************
	@ManagedProperty(value = "#{exchangeRateF4Bean}")
	private ExchangeRateF4 p_exchangeRateF4Bean;

	public ExchangeRateF4 getP_exchangeRateF4Bean() {
		return p_exchangeRateF4Bean;
	}

	public void setP_exchangeRateF4Bean(ExchangeRateF4 p_exchangeRateF4Bean) {
		this.p_exchangeRateF4Bean = p_exchangeRateF4Bean;
	}

	// ***************************Exchange Rate**************************
	ExchangeRate p_er = new ExchangeRate();

	public ExchangeRate getP_er() {
		return p_er;
	}

	public void setP_er(ExchangeRate p_er) {
		this.p_er = p_er;
	}

	List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();

	public List<ExchangeRate> getL_er() {
		return l_er;
	}

	public void setL_er(List<ExchangeRate> l_er) {
		this.l_er = l_er;
	}

	// ******************************************************************
	// **********************PostConstruct******************************
	@PostConstruct
	public void init() {
		try {
			if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) 
			{ 
			    return; // Skip ajax requests.
			}
			PermissionController.canRead(userData,Facur01.transaction_id);
			
			ExchangeRateDao erDao = (ExchangeRateDao) appContext.getContext()
					.getBean("exchangeRateDao");
			l_er = erDao.getLastCurrencyRates();
			p_er.setMain_currency("USD");
			p_er.setMc_value(1);
			for (Currency wa_cur : p_curF4Bean.getCurrency_list()) {
				if (!wa_cur.getCurrency().equals("USD")) {
					l_cur.add(wa_cur);
				}
			}

			// List<OutputTableClass> l_outputTable

		} catch (DAOException ex) {
			addMessage("Info", ex.getMessage());
			toMainPage();
		}

	}
	// *****************************************************************
	// ***********************Others************************************
	public void toMainPage() {
		try {

			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			context.redirect(context.getRequestContextPath()
					+ "/general/mainPage.xhtml");
		} catch (Exception ex) {

			addMessage("Info", ex.getMessage());
		}
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");
	}



	// *****************************************************************
	private SearchTableClass p_search = new SearchTableClass();

	public SearchTableClass getP_search() {
		return p_search;
	}

	public void setP_search(SearchTableClass p_search) {
		this.p_search = p_search;
	}

	public class SearchTableClass {
		public SearchTableClass() {

		}

		private Date s_date_from;
		private Date s_date_to;
		private String currency = "";
		private int type = 0;

		public Date getS_date_from() {
			return s_date_from;
		}

		public void setS_date_from(Date s_date_from) {
			this.s_date_from = s_date_from;
		}

		public Date getS_date_to() {
			return s_date_to;
		}

		public void setS_date_to(Date s_date_to) {
			this.s_date_to = s_date_to;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

	}

	private List<ExchangeRate> l_er_history = new ArrayList<ExchangeRate>();

	public List<ExchangeRate> getL_er_history() {
		return l_er_history;
	}

	public void setL_er_history(List<ExchangeRate> l_er_history) {
		this.l_er_history = l_er_history;
	}

	public void to_search() {
		try {
			Calendar curDate = Calendar
					.getInstance(TimeZone.getTimeZone("UTC"));
			Calendar s_date_from = Calendar.getInstance(TimeZone
					.getTimeZone("UTC"));
			Calendar s_date_to = Calendar.getInstance(TimeZone
					.getTimeZone("UTC"));
			if (p_search.getS_date_from() == null) {
				throw new DAOException("Заполните дата с");
			}

			if (p_search.getS_date_to() == null) {
				p_search.setS_date_to(curDate.getTime());
			}
			s_date_from.setTime(p_search.getS_date_from());
			s_date_to.setTime(p_search.getS_date_to());
			ExchangeRateDao erDao = (ExchangeRateDao) appContext.getContext()
					.getBean("exchangeRateDao");
			String dynamicWhereClause = "";
			if (p_search.getType() > 0) {
				dynamicWhereClause = dynamicWhereClause + " and type = "
						+ p_search.getType();
			}
			if (!p_search.getCurrency().equals("0")) {
				dynamicWhereClause = dynamicWhereClause
						+ " and secondary_currency = '"
						+ p_search.getCurrency() + "'";
			}
			dynamicWhereClause = dynamicWhereClause + " order by exrate_date DESC";
			l_er_history = erDao.getCurrencyHistory(dynamicWhereClause,
					new java.sql.Date(s_date_from.getTimeInMillis()),
					new java.sql.Date(s_date_to.getTimeInMillis()));

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:erHistoryTable");
		} catch (DAOException ex) {
			addMessage("Info", ex.getMessage());
		}
	}

	// ************************************************************************************
	// ************************************************************************************
	private List<AccountStatement> l_as = new ArrayList<AccountStatement>();

	public class AccountStatement {
		public AccountStatement() {

		}

		private String hkont = "";
		private String hkont_name = "";
		private String waers = "";
		private double amount = 0;

		public String getHkont() {
			return hkont;
		}

		public void setHkont(String hkont) {
			this.hkont = hkont;
		}

		public String getHkont_name() {
			return hkont_name;
		}

		public void setHkont_name(String hkont_name) {
			this.hkont_name = hkont_name;
		}

		public String getWaers() {
			return waers;
		}

		public void setWaers(String waers) {
			this.waers = waers;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

	}

	public void rateDifferences(String a_bukrs) {
		try {
			l_as.clear();
			
			if (p_er.getType() == 1 && !p_er.getSecondary_currency().equals("USD")) {
								
				FmglflextDao fmglflextDao = (FmglflextDao) appContext.getContext().getBean("fmglflextDao");
				FinanceService financeService = (FinanceService) appContext.getContext().getBean("financeService");
				Calendar curDate = Calendar.getInstance();
				//Time cputm = new Time(curDate.getTimeInMillis());
				int curMonth = curDate.get(Calendar.MONTH) + 1;
				List<Fmglflext> l_fgl = new ArrayList<Fmglflext>();
				List<String> sl_hkont = new ArrayList<String>();
				Map<String, AccountStatement> l_as_map = new HashMap<String, AccountStatement>();
				// System.out.println(p_bukrs.getBukrs());
				for (Hkont wa_hkont : p_hkontF4Bean.getHkont_list()) {
					// System.out.println(wa_hkont.getHkont());
					if ((wa_hkont.getHkont().startsWith("1030") || wa_hkont.getHkont().startsWith("1010") && wa_hkont.getWaers().equals(p_er.getSecondary_currency()))) 
					{
						// System.out.println(wa_hkont.getHkont());
						AccountStatement wa_as = new AccountStatement();
						wa_as.setHkont(wa_hkont.getHkont());
						wa_as.setHkont_name(wa_hkont.getText45());
						wa_as.setWaers(wa_hkont.getWaers());
						l_as.add(wa_as);
						sl_hkont.add(wa_hkont.getHkont());
						l_as_map.put(wa_hkont.getHkont(), wa_as);
						// System.out.println(wa_hkont.getHkont());
					}
				}
				// removing duplicates
				Set<String> hs = new HashSet<>();
				hs.addAll(sl_hkont);
				sl_hkont.clear();
				sl_hkont.addAll(hs);

				l_fgl = fmglflextDao.findByBukrsGjahrHkontList(a_bukrs, curDate.get(Calendar.YEAR), sl_hkont);
				for (Fmglflext wa_fmgl : l_fgl) {
					AccountStatement wa_as = l_as_map.get(wa_fmgl.getHkont());
					double amount = 0;
					amount = amount + wa_fmgl.getBeg_amount();
					for (int i = 1; i <= curMonth; i++) {
						switch (i) {
						case 1:
							amount = amount + wa_fmgl.getMonth1();
							break;
						case 2:
							amount = amount + wa_fmgl.getMonth2();
							break;
						case 3:
							amount = amount + wa_fmgl.getMonth3();
							break;
						case 4:
							amount = amount + wa_fmgl.getMonth4();
							break;
						case 5:
							amount = amount + wa_fmgl.getMonth5();
							break;
						case 6:
							amount = amount + wa_fmgl.getMonth6();
							break;
						case 7:
							amount = amount + wa_fmgl.getMonth7();
							break;
						case 8:
							amount = amount + wa_fmgl.getMonth8();
							break;
						case 9:
							amount = amount + wa_fmgl.getMonth9();
							break;
						case 10:
							amount = amount + wa_fmgl.getMonth10();
							break;
						case 11:
							amount = amount + wa_fmgl.getMonth11();
							break;
						case 12:
							amount = amount + wa_fmgl.getMonth12();
							break;

						}
					}

					if (wa_as != null) {
						if (wa_fmgl.getDrcrk().equals("S")) {
							wa_as.setAmount(wa_as.getAmount() + amount);
						} else if (wa_fmgl.getDrcrk().equals("H")) {
							wa_as.setAmount(wa_as.getAmount() - amount);
						}
					}
				}
				
				ExchangeRate old_er = p_exchangeRateF4Bean.getL_er_map_national().get("1" + p_er.getSecondary_currency());
				if (old_er != null && old_er.getSecondary_currency() != null && old_er.getSc_value() > 0) 
				{
					for (AccountStatement wa_as : l_as) 
					{
						if (wa_as.getAmount() != 0 && old_er.getSc_value() != p_er.getSc_value()) 
						{

							double wa_dmbtr = 0;
							wa_dmbtr = wa_as.getAmount() / p_er.getSc_value() - wa_as.getAmount() / old_er.getSc_value();
							//System.out.println(wa_dmbtr);
							
							wa_dmbtr=GeneralUtil.round(wa_dmbtr, 2);
						    //System.out.println(wa_as.getAmount() + " " + old_er.getSc_value() + " " + p_er.getSc_value() + " " + wa_dmbtr 
						    //		+ " " + wa_as.getAmount() / old_er.getSc_value() + " " + wa_as.getAmount() / p_er.getSc_value());
						    
						    //System.out.println(wa_dmbtr);
						     
							 Bkpf wa_bkpf4 = new Bkpf(); 
							 List<Bseg> wal_bseg4 = new ArrayList<Bseg>();
							 wa_bkpf4.setBukrs(a_bukrs);
							 wa_bkpf4.setGjahr(curDate.get(Calendar.YEAR));
							 wa_bkpf4.setBlart("ED");
							 wa_bkpf4.setBudat(curDate.getTime());
							 wa_bkpf4.setBldat(curDate.getTime());
							 wa_bkpf4.setMonat(curDate.get(Calendar.MONTH)+1);
							 wa_bkpf4.setCpudt(curDate.getTime());
							 wa_bkpf4.setUsnam(userData.getUserid());
							 wa_bkpf4.setTcode(Facur01.transaction_code);
							 wa_bkpf4.setWaers("USD"); wa_bkpf4.setKursf(1);
							 wa_bkpf4.setBrnch(userData.getBranch_id());
							 wa_bkpf4.setAwtyp(0);
							 
							 Bseg wa_bsegD = new Bseg(); 
							 Bseg wa_bsegK = new Bseg(); 
							 wa_bsegD.setBukrs(wa_bkpf4.getBukrs());
							 wa_bsegD.setGjahr(wa_bkpf4.getGjahr());
							 wa_bsegK.setBukrs(wa_bkpf4.getBukrs());
							 wa_bsegK.setGjahr(wa_bkpf4.getGjahr()); 
							 if (wa_dmbtr<0) 
							 {
								 wa_bkpf4.setDmbtr(Math.abs(wa_dmbtr));
								 wa_bsegD.setBuzei(1); 
								 wa_bsegD.setBschl("40");
								 wa_bsegD.setShkzg("S");
								 wa_bsegD.setHkont("74300001");
								 wa_bsegD.setDmbtr(Math.abs(wa_dmbtr));
								 wal_bseg4.add(wa_bsegD);
							 
								 wa_bsegK.setBuzei(2); 
								 wa_bsegK.setBschl("50");
								 wa_bsegK.setShkzg("H");
								 wa_bsegK.setHkont(wa_as.getHkont());
								 wa_bsegK.setDmbtr(Math.abs(wa_dmbtr));
								 wal_bseg4.add(wa_bsegK); 
							 } 
							 else if (wa_dmbtr>0) 
							 {
								 wa_bkpf4.setDmbtr(Math.abs(wa_dmbtr));
								 wa_bsegD.setBuzei(1); 
								 wa_bsegD.setBschl("40");
								 wa_bsegD.setShkzg("S");
								 wa_bsegD.setHkont(wa_as.getHkont());
								 wa_bsegD.setDmbtr(Math.abs(wa_dmbtr));
								 wal_bseg4.add(wa_bsegD);
							 
								 wa_bsegK.setBuzei(2); 
								 wa_bsegK.setBschl("50");
								 wa_bsegK.setShkzg("H");
								 wa_bsegK.setHkont("62500001");
								 wa_bsegK.setDmbtr(Math.abs(wa_dmbtr));
								 wal_bseg4.add(wa_bsegK); 
							 } 
							 //Create fin docs
							 financeService.createFAICF(wa_bkpf4, wal_bseg4);
							 
						}

					}
				}
			}
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	// *****************************************************************
	public void to_save() {
		try {
			PermissionController.canWrite(userData, Facur01.transaction_id);

			AccountingOtherService aOService = (AccountingOtherService) appContext
					.getContext().getBean("accountingOtherService");
			if (p_er.getSecondary_currency().equals("0")
					|| p_er.getSecondary_currency().equals(null)) {
				throw new DAOException("Выберите валюту");
			}
			
			if (p_er.getType()==2 && (p_er.getBukrs()==null || p_er.getBukrs().length()<1))
			{
				throw new DAOException("Выберите компанию.");
			}

			if (p_er.getSc_value() < 0 || p_er.getSc_value() == 0) {
				throw new DAOException("Значение не может быть 0 или меньше");
			}
			
			p_er.setSc_value(GeneralUtil.round(p_er.getSc_value(), 2));
			
			if (p_er.getType()==2 && p_er.getBukrs()!=null && p_er.getBukrs().length()>0)
			{
				ExchangeRate old_er = p_exchangeRateF4Bean.getL_er_map_internal().get("2" + p_er.getSecondary_currency()+p_er.getBukrs());
				if (old_er != null && old_er.getSecondary_currency() != null && old_er.getSc_value() > 0) 
				{
					if (p_er.getSc_value()==old_er.getSc_value())
					{
						throw new DAOException("Старый курс равен новому!");
					}
					
				}
			}
			else if (p_er.getType()==1)				
			{
				p_er.setBukrs(null);
				//ExchangeRate old_er = p_exchangeRateF4Bean.getL_er_map_national().get("1" + p_er.getSecondary_currency());
				//if (old_er != null && old_er.getSecondary_currency() != null && old_er.getSc_value() > 0) 
				//{
				//	if (p_er.getSc_value()==old_er.getSc_value())
				//	{
				//		throw new DAOException("Старый курс равен новому!");
				//	}
					
				//}
			}
			
			
			
			aOService.createNewExchangeRate(p_er);
			p_er = new ExchangeRate();
			p_er.setMain_currency("USD");
			p_er.setMc_value(1);
			p_er.setBukrs(null);

			ExchangeRateDao erDao = (ExchangeRateDao) appContext.getContext()
					.getBean("exchangeRateDao");
			l_er = erDao.getLastCurrencyRates();
			if (userData != null && userData.isLogged_in()
					&& userData.isSys_admin()) {
				p_exchangeRateF4Bean.clearAndAddAll(l_er);
			}

			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:new_secondary_currency");
			reqCtx.update("form:new_sc_value");
			reqCtx.update("form:new_sc_bukrs");
			reqCtx.update("form:exchangeRateListTable");

		} catch (DAOException ex) {
			addMessage("Info", ex.getMessage());
		}
	}
	public void updateF4()
	{
		if (userData.getUserid().equals(1L))
		{
			updateF4Bean.updateAllF4();
		}
		
	}
	
	public void closeYear()
	{
		if (userData.getUserid().equals(1L))
		{
//			FmglflextService fmglflextService = (FmglflextService) appContext.getContext().getBean("fmglflextService");
//			fmglflextService.closeYear("2000", 2016);
		}
		
	}
	
	// *****************************************************************
}
