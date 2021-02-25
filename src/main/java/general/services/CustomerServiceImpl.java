package general.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.MessageProvider;
import general.Validation;
import general.dao.BankAccountDao;
import general.dao.CustomerDao;
import general.tables.BankAccount;
import general.tables.Customer;
import general.output.tables.Podotchet;
import general.services.CustomerService;
import general.dao.DAOException;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private CustomerDao dao;

	@Autowired
	private BankAccountDao baDao;

	@Override
	public void createCustomer(Customer c, List<BankAccount> baList)
			throws DAOException {
		String error = this.validateCustomer(c, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		dao.create(c);
		for (BankAccount ba : baList) {
			ba.setCustomer_id(c.getId());
			baDao.create(ba);
		}
	}

	@Override
	public void createCustomer(Customer c) throws DAOException {
		String error = this.validateCustomer(c, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		dao.create(c);
	}

	@Override
	public Customer searchByIinBin(String a_iin_bin) throws DAOException {
		try {
			int count = 0;
			count = dao.countCustomerbyIinBin(a_iin_bin).intValue();
			if (count == 0) {
				throw new DAOException("No customer found");
			} else if (count == 1) {
				return dao.findByIinBin(a_iin_bin);
			} else {
				throw new DAOException("Unexpected error");
			}

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}

	@Override
	public Customer searchById(Long a_customer_id) throws DAOException {
		Customer cus = new Customer();
		try {
			cus = dao.find(a_customer_id);
			return cus;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public void updateCustomer(Customer c) throws DAOException {
		String error = this.validateCustomer(c, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		dao.update(c);
	}

	@Override
	public List<Customer> dynamicSearch(Customer a_customer)
			throws DAOException {
		try {
			List<Customer> p_customer_list = new ArrayList<Customer>();
			String dynamicWhere = "";
			if (a_customer.getFiz_yur() > 0 && a_customer.getFiz_yur() < 3) {
				dynamicWhere = dynamicWhere + "fiz_yur = "
						+ a_customer.getFiz_yur();
				if (a_customer.getCountry_id() > 0) {
					if (dynamicWhere.length() > 0)
						dynamicWhere = dynamicWhere + " and ";
					dynamicWhere = dynamicWhere + "country_id = " + a_customer.getCountry_id();
				}

				a_customer.setIin_bin(a_customer.getIin_bin().replaceAll("\\s",
						""));
				if (a_customer.getIin_bin().length() > 0) {
					dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
							dynamicWhere, "and", "=", "iin_bin",
							a_customer.getIin_bin());
				}
				

				if (a_customer.getFiz_yur() == 1) {
					a_customer.setName(a_customer.getName().replaceAll("\\s",
							""));
					if (a_customer.getName().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "name",
								a_customer.getName());
					}
					if (a_customer.getIin_bin().length() == 0
							&& a_customer.getName().length() == 0
							&& a_customer.getCountry_id().equals(0L)) {
						dynamicWhere = " fiz_yur=1";
						// throw new
						// DAOException("Заполните данные для поиска");
					}
				} else if (a_customer.getFiz_yur() == 2) {
					a_customer.setFirstname(a_customer.getFirstname()
							.replaceAll("\\s", ""));
					if (a_customer.getFirstname().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "firstname",
								a_customer.getFirstname());
					}
					a_customer.setLastname(a_customer.getLastname().replaceAll(
							"\\s", ""));
					if (a_customer.getLastname().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "lastname",
								a_customer.getLastname());
					}
					a_customer.setMiddlename(a_customer.getMiddlename()
							.replaceAll("\\s", ""));
					if (a_customer.getMiddlename().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "middlename",
								a_customer.getMiddlename());
					}
					if (a_customer.getBirthday() != null) {
						if (dynamicWhere.length() > 0)
							dynamicWhere = dynamicWhere + " and ";
						dynamicWhere = dynamicWhere
								+ "birthday = '"
								+ new java.sql.Date(a_customer.getBirthday()
										.getTime()) + "'";
					}
					a_customer.setPassport_id(a_customer.getPassport_id()
							.replaceAll("\\s", ""));
					if (a_customer.getPassport_id().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "passport_id",
								a_customer.getPassport_id());
					}
					if (a_customer.getIin_bin().length() == 0
							&& a_customer.getFirstname().length() == 0
							&& a_customer.getLastname().length() == 0
							&& a_customer.getMiddlename().length() == 0
							&& a_customer.getBirthday() == null
							&& a_customer.getPassport_id().length() == 0
							&& a_customer.getCountry_id().equals(0L)) {
						dynamicWhere = " fiz_yur=2 ";
						// throw new
						// DAOException("Заполните данные для поиска");
					}

				}
			} else {
				dynamicWhere = " 1=1 ";
				// throw new DAOException("Выберите тип клиента");
			}
			String regx = ";,.";
			char[] ca = regx.toCharArray();
			for (char c : ca) {
				dynamicWhere = dynamicWhere.replace("" + c, "");
			}

			p_customer_list = dao.dynamicFindCustomers(dynamicWhere);
			return p_customer_list;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}
	@Override
	public List<Podotchet> dynamicSearchPodotchet(Customer a_customer)
			throws DAOException {
		try {
			List<Podotchet> p_customer_list = new ArrayList<Podotchet>();
			String dynamicWhere = "";
			if (a_customer.getFiz_yur() > 0 && a_customer.getFiz_yur() < 3) {
				dynamicWhere = dynamicWhere + "fiz_yur = "
						+ a_customer.getFiz_yur();
				if (a_customer.getCountry_id() > 0) {
					if (dynamicWhere.length() > 0)
						dynamicWhere = dynamicWhere + " and ";
					dynamicWhere = dynamicWhere + "country_id = "
							+ a_customer.getCountry_id();
				}

				a_customer.setIin_bin(a_customer.getIin_bin().replaceAll("\\s",
						""));
				if (a_customer.getIin_bin().length() > 0) {
					dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
							dynamicWhere, "and", "=", "iin_bin",
							a_customer.getIin_bin());
				}

				if (a_customer.getFiz_yur() == 1) {
					a_customer.setName(a_customer.getName().replaceAll("\\s",
							""));
					if (a_customer.getName().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "name",
								a_customer.getName());
					}
					if (a_customer.getIin_bin().length() == 0
							&& a_customer.getName().length() == 0) {
						dynamicWhere = " fiz_yur=1";
						// throw new
						// DAOException("Заполните данные для поиска");
					}
				} else if (a_customer.getFiz_yur() == 2) {
					a_customer.setFirstname(a_customer.getFirstname()
							.replaceAll("\\s", ""));
					if (a_customer.getFirstname().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "firstname",
								a_customer.getFirstname());
					}
					a_customer.setLastname(a_customer.getLastname().replaceAll(
							"\\s", ""));
					if (a_customer.getLastname().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "lastname",
								a_customer.getLastname());
					}
					a_customer.setMiddlename(a_customer.getMiddlename()
							.replaceAll("\\s", ""));
					if (a_customer.getMiddlename().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "middlename",
								a_customer.getMiddlename());
					}
					if (a_customer.getBirthday() != null) {
						if (dynamicWhere.length() > 0)
							dynamicWhere = dynamicWhere + " and ";
						dynamicWhere = dynamicWhere
								+ "birthday = '"
								+ new java.sql.Date(a_customer.getBirthday()
										.getTime()) + "'";
					}
					a_customer.setPassport_id(a_customer.getPassport_id()
							.replaceAll("\\s", ""));
					if (a_customer.getPassport_id().length() > 0) {
						dynamicWhere = Validation.sqlMatchPatternDynamicWhere(
								dynamicWhere, "and", "=", "passport_id",
								a_customer.getPassport_id());
					}
					if (a_customer.getIin_bin().length() == 0
							&& a_customer.getFirstname().length() == 0
							&& a_customer.getLastname().length() == 0
							&& a_customer.getMiddlename().length() == 0
							&& a_customer.getBirthday() == null
							&& a_customer.getPassport_id().length() == 0) {
						dynamicWhere = " fiz_yur=2 ";
						// throw new
						// DAOException("Заполните данные для поиска");
					}

				}
			} else {
				dynamicWhere = " 1=1 ";
				// throw new DAOException("Выберите тип клиента");
			}
			String regx = ";,.";
			char[] ca = regx.toCharArray();
			for (char c : ca) {
				dynamicWhere = dynamicWhere.replace("" + c, "");
			}

			p_customer_list = dao.dynamicFindCustomersPodotchet(dynamicWhere);
			return p_customer_list;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}
	@Override
	public List<Customer> searchInternalCompanies(String a_bukrs)
			throws DAOException {
		try {
			List<Customer> p_customer_list = new ArrayList<Customer>();
			String dynamicWhere = "";
			dynamicWhere = dynamicWhere + "fiz_yur = 1 and iin_bin <>'" + a_bukrs+"' and iin_bin in ('1000','2000','3000','5000','6000','7000','8000')";
			p_customer_list = dao.dynamicFindCustomers(dynamicWhere);
			return p_customer_list;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}
	@Override
	public List<Customer> searchCompaniesByIin(String al_iin) throws DAOException {
		try {
			List<Customer> p_customer_list = new ArrayList<Customer>();
			String dynamicWhere = "";
			dynamicWhere = dynamicWhere + "fiz_yur = 1 and iin_bin in ('"+al_iin+"')";
			p_customer_list = dao.dynamicFindCustomers(dynamicWhere);
			return p_customer_list;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}
	private String validateCustomer(Customer c, boolean isNew) {
		System.out.println(c.getId());
		MessageProvider messageProvider = new MessageProvider();
		String error = "";
		if (c.getFiz_yur() < 1 || c.getFiz_yur() > 2) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.customer.customer_type"))
					+ "\n";
		}

		if (c.getIin_bin() == null || c.getIin_bin().isEmpty()) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.customer.iin_bin"))
					+ "\n";
		} else {
			List<Customer> templList = dao.dynamicFindCustomers(String.format(
					" c.iin_bin = '%s'", c.getIin_bin()));

			if (isNew) {
				if (templList.size() > 0) {
					error += messageProvider.getErrorValue("same_iin_customer_exists") + "\n";
				}
			} else if (templList.size() > 0 && templList.get(0).getId().longValue() != c.getId()
					.longValue()) {
				error += messageProvider.getErrorValue("same_iin_customer_exists") + "\n";
			}
		}
		// Yur lico
		if (c.getFiz_yur() == 1) {
			if (c.getName().isEmpty()) {
				error += MessageFormat.format(
						messageProvider.getErrorValue("field_is_required"),
						messageProvider.getValue("hr.customer.name"))
						+ "\n";
			}
		} else {// Fiz lico
			if (c.getFirstname() == null || c.getFirstname().isEmpty()) {
				error += MessageFormat.format(
						messageProvider.getErrorValue("field_is_required"),
						messageProvider.getValue("hr.customer.firstname"))
						+ "\n";
			}

			if (c.getLastname() == null || c.getLastname().isEmpty()) {
				error += MessageFormat.format(
						messageProvider.getErrorValue("field_is_required"),
						messageProvider.getValue("hr.customer.lastname"))
						+ "\n";
			}

			if (c.getBirthday() != null) {
			} else {
				error += MessageFormat.format(
						messageProvider.getErrorValue("field_is_required"),
						messageProvider.getValue("hr.customer.birthday"))
						+ "\n";
			}

			if (c.getPassport_id() == null || c.getPassport_id().isEmpty()) {
				error += MessageFormat.format(
						messageProvider.getErrorValue("field_is_required"),
						messageProvider.getValue("hr.customer.passport_id"))
						+ "\n";
			}

			if (c.getPassport_given_by() == null
					|| c.getPassport_given_by().isEmpty()) {
				error += MessageFormat.format(
						messageProvider.getErrorValue("field_is_required"),
						messageProvider.getValue("hr.customer.passport_given_by"))
						+ "\n";
			}

			if (c.getPassport_given_date() != null) {
			} else {
				error += MessageFormat.format(
						messageProvider.getErrorValue("field_is_required"),
						messageProvider.getValue("hr.customer.passport_given_date"))
						+ "\n";
			}

		}

		if (c.getCountry_id() < 1) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.customer.country"))
					+ "\n";
		}
		if (isNew) {
			c.setCreated_date(Calendar.getInstance().getTime());
		}
		c.setUpdated_date(Calendar.getInstance().getTime());
		return error;
	}

	@Override
	public void updateCustomer(Customer c, List<BankAccount> baList)
			throws DAOException {
		String error = this.validateCustomer(c, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		dao.update(c);
		baDao.deleteCustomerAccount(c.getId());
		for (BankAccount ba : baList) {
			ba.setCustomer_id(c.getId());
			baDao.create(ba);
		}
	}
}
