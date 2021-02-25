package hr.customer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import f4.BlartF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.PermissionController;
import general.dao.BankAccountDao;
import general.dao.BankDao;
import general.dao.BkpfDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.services.CustomerService;
import general.tables.Bank;
import general.tables.BankAccount;
import general.tables.Bkpf;
import general.tables.Blart;
import general.tables.Customer;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import user.User;

@ManagedBean
abstract class HrcBase {

	@PostConstruct
	public void init() {
		if (FacesContext.getCurrentInstance().getPartialViewContext()
				.isAjaxRequest()) {
			return;
		}
		// TODO PERMISSION
		PermissionController.canRead(userData, this.getTransactionId());
		if (this.iinBinSearch != null && this.iinBinSearch.length() > 0) {
			this.search();
		} else if (this.customerId != null && this.customerId > 0) {
			this.searchById();
		}
	}

	Customer customer = new Customer();

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	String iinBinSearch = "";

	public String getIinBinSearch() {
		return iinBinSearch;
	}

	Long customerId = 0L;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setIinBinSearch(String iinBinSearch) {
		this.iinBinSearch = iinBinSearch;
	}

	public void search() {
		try {
			CustomerService cService = (CustomerService) appContext
					.getContext().getBean("customerService");
			customer = cService.searchByIinBin(iinBinSearch);
			this.loadBaList("customer_id = " + customer.getId());
		} catch (DAOException e) {
                    GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void searchById() {
		try {
			CustomerDao cDao = (CustomerDao) appContext.getContext().getBean(
					"customerDao");
			customer = cDao.find(this.customerId);
			if (customer == null) {
				throw new DAOException("Customer Not Found!");
			}
			this.iinBinSearch = customer.getIin_bin();
			this.loadBaList("customer_id = " + customer.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private List<BankAccount> baList = new ArrayList<BankAccount>();

	public List<BankAccount> getBaList() {
		return baList;
	}

	public void loadBaList(String condition) {
		BankAccountDao d = (BankAccountDao) appContext.getContext().getBean(
				"bankAccountDao");
		this.baList = d.findAll(condition);
		this.loadBankList();
	}

	public void addBankAccount() {
		// System.out.println(this.baList.size());
		this.loadBankList();
		this.baList.add(new BankAccount());
	}

	public void removeBankAccount(BankAccount bankAccount) {
		List<BankAccount> tempList = new ArrayList<BankAccount>();
		for (BankAccount ba : this.baList) {
			if (ba != bankAccount) {
				tempList.add(ba);
			}
		}
		this.baList = tempList;
	}

	public void setEmptyBaList() {
		this.baList = new ArrayList<BankAccount>();
	}

	private List<Bank> bankList = new ArrayList<Bank>();

	public List<Bank> getBankList() {
		return bankList;
	}

	public void loadBankList() {
		if (this.bankList.size() < 1) {
			BankDao d = (BankDao) appContext.getContext().getBean("bankDao");
			this.bankList = d.findAll("");
		}
	}

	String validateBaList(List<BankAccount> baList) {
		MessageProvider mp = new MessageProvider();
		String error = "";
		for (BankAccount ba : baList) {
			if (ba.getBank_id() == 0L) {
				error += MessageFormat.format(mp.getErrorValue("field_is_required"), mp.getValue("hr.bank_account.bank_id")) + "\n";
			}

			if (ba.getCurrency() == null || ba.getCurrency().isEmpty()) {
				error += MessageFormat.format(mp.getErrorValue("field_is_required"), mp.getValue("hr.bank_account.currency")) + "\n";
			}

			if (ba.getIban() == null || ba.getIban().isEmpty()) {
				error += MessageFormat.format(mp.getErrorValue("field_is_required"), mp.getValue("hr.bank_account.iban")) + "\n";
			}

			ba.setCreated_by(Long.valueOf(userData.getUserid()));
			ba.setCreated_date(Calendar.getInstance().getTime());
		}
		return error;
	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	abstract Long getTransactionId();

	/***** Дебитор & Кредитор ****/
	public String bkpfDialogTitle = "";
	List<Bkpf> bkpfList = new ArrayList<Bkpf>();

	public void loadBkpfList(int type) {
		switch (type) {
		case 2:
			this.bkpfDialogTitle = "Кредитор";
			break;

		default:
			this.bkpfDialogTitle = "Дебитор";
			break;
		}
		BkpfDao d = (BkpfDao) appContext.getContext().getBean("bkpfDao");
		this.bkpfList = d.dynamicFindBkpf("awtyp = " + type
				+ " AND awkey IS NULL AND customer_id = "
				+ this.customer.getId());
	}

	public String getBlartText(String s) {
		for (Blart b : blartF4Bean.getBlart_list()) {
			if (b.getBlart().equals(s)) {
				return b.getText45();
			}
		}
		return s;
	}

	public String getBkpfDialogTitle() {
		return bkpfDialogTitle;
	}

	public List<Bkpf> getBkpfList() {
		return bkpfList;
	}

	@ManagedProperty(value = "#{blartF4Bean}")
	private BlartF4 blartF4Bean;

	public BlartF4 getBlartF4Bean() {
		return blartF4Bean;
	}

	public void setBlartF4Bean(BlartF4 blartF4Bean) {
		this.blartF4Bean = blartF4Bean;
	}

	String breadcrumb = "Финанс > Контрагент ";

	public String getBreadcrumb() {
		return breadcrumb;
	}
}