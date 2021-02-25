package general.services;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;
import general.Helper;
import general.MessageProvider;
import general.Validation;
import general.dao.BranchDao;
import general.dao.CountryDao;
import general.dao.PayrollDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.StaffFileDao;
import general.dao.UpdFileDao;
import general.tables.Address;
import general.tables.Branch;
import general.tables.EventLog;
import general.tables.Payroll;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.StaffFile;
import general.tables.UpdFile;
import general.dao.CustomerDao;
import general.tables.Customer;
import general.dao.DAOException;

@Service("staffService")
public class StaffServiceImpl implements StaffService {

	@Autowired
	private StaffDao stfDao;

	@Autowired
	private CustomerDao cusDao;

	@Autowired
	private SalaryDao salDao;

	@Autowired
	private PayrollDao prlDao;

	@Autowired
	private CountryDao countryDao;

	@Autowired
	private BranchDao branchDao;

	@Autowired
	private EventLogService elService;

	@Autowired
	UpdFileService fileService;

	@Autowired
	StaffFileDao sfDao;

	@Autowired
	AddressService addressService;

	@Autowired
	UpdFileDao fileDao;

	private List<Salary> p_salary_list = new ArrayList<Salary>();
	private List<Payroll> p_payroll_list = new ArrayList<Payroll>();

	@Override
	public void createStaff(Staff staff, Salary salary) throws DAOException {
		String error = validateStaff(staff, true);
		if (salary.getBukrs() != null && salary.getBukrs().length() > 0) {
			error += validateSalary(salary);
		}
		staff.setCreated_date(Calendar.getInstance().getTime());
		staff.setUpdated_date(Calendar.getInstance().getTime());
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		stfDao.create(staff);
		this.createOrUpdateCustomer(staff, new Customer());

		if (salary.getBukrs() != null && salary.getBukrs().length() > 0) {
			salary.setStaff_id(staff.getStaff_id());
			salary.setP_staff(staff);
			salDao.create(salary);
			staff.setDepartment_id(salary.getDepartment_id());
			staff.setPosition_id(salary.getPosition_id());
			staff.setBranch_id(salary.getBranch_id());
		}
		stfDao.update(staff);
	}

	private void createOrUpdateCustomer(Staff staff, Customer c) {
		c.setAddress(staff.getAddress());
		c.setBirthday(c.getBirthday());
		c.setCreated_by(staff.getCreated_by());
		c.setCreated_date(staff.getCreated_date());
		c.setEmail(staff.getEmail());
		c.setFirstname(staff.getFirstname());
		c.setFiz_yur(2);
		c.setIin_bin(staff.getIin_bin());
		c.setLastname(staff.getLastname());
		c.setMiddlename(staff.getMiddlename());
		c.setMobile(staff.getMobile());
		c.setPassport_given_by(staff.getPassport_given_by());
		c.setPassport_given_date(staff.getPassport_given_date());
		c.setPassport_id(staff.getPassport_id());
		c.setStaff_id(staff.getStaff_id());
		c.setUpdated_by(staff.getUpdated_by());
		c.setTelephone(staff.getHomephone());
		c.setUpdated_date(staff.getUpdated_date());

		if (c.getId() != null && c.getId() > 0L) {
			cusDao.update(c);
		} else {
			cusDao.create(c);
		}
		staff.setCustomer_id(c.getId());
	}

	private String validateSalary(Salary salary) {
		MessageProvider messageProvider = new MessageProvider();
		String error = "";
		if (Validation.isEmptyString(salary.getBukrs())) {
			MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("company"));
		}

		if (salary.getBranch_id() == 0) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.salary.branch_id")) + "\n";
		} else {
			Branch brnch = branchDao.find(salary.getBranch_id());
			if (brnch != null) {
				salary.setCountry_id(brnch.getCountry_id());
			}
		}

		if (salary.getCountry_id() == null || salary.getCountry_id().longValue() == 0L) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.salary.country_id")) + "\n";
		}

		if (salary.getPosition_id() == null || salary.getPosition_id() == 0) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.salary.branch_id")) + "\n";
		}

		if (salary.getBusiness_area_id() == null || salary.getBusiness_area_id() == 0) {
			// error += "Please select business area" + "\n";
		}

		/*
		 * if (salary.getAmount() <= 0) { //error +=
		 * "Amount must be bigger than 0" + "\n"; }
		 */
		if (salary.getBeg_date() != null) {
			Calendar curDate = Calendar.getInstance();
			int curYear = curDate.get(Calendar.YEAR);
			int curMonth = curDate.get(Calendar.MONTH) + 1;

			Calendar salDate = Calendar.getInstance();
			salDate.setTime(salary.getBeg_date());
			int salYear = salDate.get(Calendar.YEAR);
			int salMonth = salDate.get(Calendar.MONTH) + 1;

			if (curMonth != salMonth || curYear != salYear) {
				// error +=
				// "The starting salary date must be in the current month" +
				// "\n";
			}
		} else {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.salary.beg_date")) + "\n";
		}

		if (salary.getDepartment_id() == 0L) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.salary.department_id")) + "\n";
		}
		return error;
	}

	private String validateStaff(Staff staff, boolean isNew) {
		MessageProvider messageProvider = new MessageProvider();
		int thisYear = Helper.getCurrentYear();
		String error = "";

		if (Validation.isEmptyString(staff.getFirstname())) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.firstname")) + "\n";
		}
		if (Validation.isEmptyString(staff.getLastname())) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.lastname")) + "\n";
		}

		if (Validation.isEmptyString(staff.getIin_bin())) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.iin_bin")) + "\n";
		} else {
			staff.getIin_bin().trim();
			if (staff.getIin_bin().length() != 12) {
				error += "ИИН/БИН должно быть 12 \n";
			}
		}

		if (staff.getDepartment_id() != null && staff.getDepartment_id() == 0L) {
			// error += "Выберите департамент" + "\n";
		}

		if (staff.getBirthday() != null) {
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(staff.getBirthday());
			int birthYear = cal2.get(Calendar.YEAR);
			if (birthYear < 1900 || birthYear >= thisYear) {
				// error += "Не правильно задан поле 'День рождения'" + "\n";
			}
		} else {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.birthdate")) + "\n";
		}

		if (Validation.isEmptyString(staff.getGender())) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.gender")) + "\n";
		}

		if (Validation.isEmptyString(staff.getPassport_id())) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.passport_number")) + "\n";
		}

		if (Validation.isEmptyString(staff.getPassport_given_by())) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.passport_given_by")) + "\n";
		}

		if (staff.getPassport_given_date() != null) {

			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(staff.getPassport_given_date());
			int passportYear = cal2.get(Calendar.YEAR);
			if (passportYear > 1900 && passportYear <= thisYear) {
				// error += "Не правильно заполнен поле Passport given year" +
				// "\n";
			}
		} else {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.passport_given_date")) + "\n";
		}

		if (staff.getPassport_validity() == null) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.passport_validity")) + "\n";
		}

		if (Validation.isEmptyString(staff.getMobile())) {
			error += MessageFormat.format(messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.staff.mobile")) + "\n";
		}

		// System.out.println("COUNTIIB: " +
		// stfDao.countStaffbyIinBin(staff.getIin_bin()) + " > " + isNew);
		if (!Validation.isEmptyString(staff.getIin_bin())) {
			List<Staff> temp = stfDao.findAll(" iin_bin = '" + staff.getIin_bin() + "' ");
			if (temp != null && temp.size() > 0) {
				if (isNew) {
					error += "Сотрудник с таким ИИН уже существует, ID сотрудника: " + temp.get(0).getStaff_id()
							+ " \n";
				} else {
					if (!temp.get(0).getStaff_id().equals(staff.getStaff_id())) {
						error += "Сотрудник с таким ИИН уже существует, ID сотрудника: " + temp.get(0).getStaff_id()
								+ " \n";
					}
				}
			}
			// if (isNew && (stfDao.countStaffbyIinBin(staff.getIin_bin()) > 0))
			// {
			// error +=
			// messageProvider.getValue("hr.staff.employee_with_same_iin_exists")
			// + "\n";
			// }
		}

		// if (staff.getCountry_id() == null || staff.getCountry_id() == 0) {
		// error += MessageFormat.format(
		// messageProvider.getErrorValue("field_is_required"),
		// messageProvider.getValue("hr.staff.country_id")) + "\n";
		// }
		//
		// if (staff.getState_id() == null || staff.getState_id() == 0) {
		// error += MessageFormat.format(
		// messageProvider.getErrorValue("field_is_required"),
		// messageProvider.getValue("hr.staff.state_id")) + "\n";
		// }
		//
		// if (staff.getCity_id() == null || staff.getCity_id() == 0) {
		// error += MessageFormat.format(
		// messageProvider.getErrorValue("field_is_required"),
		// messageProvider.getValue("hr.staff.city_id")) + "\n";
		// }

		// if (Validation.isEmptyString(staff.getAddress())) {
		// error += MessageFormat.format(
		// messageProvider.getErrorValue("field_is_required"),
		// messageProvider.getValue("hr.staff.address")) + "\n";
		// }
		//
		// if (staff.getFact_country_id() == null
		// || staff.getFact_country_id() == 0) {
		// error += MessageFormat.format(
		// messageProvider.getErrorValue("field_is_required"),
		// messageProvider.getValue("hr.staff.fact_country_id"))
		// + "\n";
		// }
		//
		// if (staff.getFact_city_id() == null || staff.getFact_city_id() == 0)
		// {
		// error += MessageFormat.format(
		// messageProvider.getErrorValue("field_is_required"),
		// messageProvider.getValue("hr.staff.fact_city_id")) + "\n";
		// }
		//
		// if (staff.getFact_state_id() == null || staff.getFact_state_id() ==
		// 0) {
		// error += MessageFormat.format(
		// messageProvider.getErrorValue("field_is_required"),
		// messageProvider.getValue("hr.staff.fact_state_id")) + "\n";
		// }
		//
		// if (Validation.isEmptyString(staff.getFact_address())) {
		// error += MessageFormat.format(
		// messageProvider.getErrorValue("field_is_required"),
		// messageProvider.getValue("hr.staff.fact_address")) + "\n";
		// }
		
		if(Validation.isEmptyLong(staff.getTsStaffId())){
			staff.setTsIsActive(0);
		}

		return error;
	}

	@Override
	public void updateStaff(Staff staff, Salary salary) throws DAOException {
		try {
			String error = validateStaff(staff, false);
			if (salary.getBukrs().length() > 0) {
				error += validateSalary(salary);
			}

			staff.setCreated_date(Calendar.getInstance().getTime());
			staff.setUpdated_date(Calendar.getInstance().getTime());

			if (error.length() > 0) {
				throw new DAOException(error);
			}
			stfDao.update(staff);
			Customer c = cusDao.findByStaffId(staff.getStaff_id());
			if (c == null) {
				c = new Customer();
			}
			this.createOrUpdateCustomer(staff, c);
			if (salary.getBukrs().length() > 0) {
				salary.setStaff_id(staff.getStaff_id());
				salDao.create(salary);
				staff.setPosition_id(salary.getPosition_id());
				stfDao.update(staff);
			}

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public void updateStaff(Staff staff, User userData, Address livingAddress, Address residenceAddress)
			throws DAOException {
		String error = validateStaff(staff, false);

		staff.setUpdated_date(Calendar.getInstance().getTime());

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		Customer c = cusDao.findByStaffId(staff.getStaff_id());
		if (c == null) {
			c = new Customer();
		}
		this.createOrUpdateCustomer(staff, c);
		stfDao.update(staff);

		residenceAddress.setCustomerId(staff.getCustomer_id());
		if (Validation.isEmptyLong(residenceAddress.getAddr_id())) {
			addressService.createAddress(residenceAddress, userData.getUserid(), "");
		} else {
			addressService.updateAddress(residenceAddress, userData.getUserid(), "");
		}

		livingAddress.setCustomerId(staff.getCustomer_id());
		if (Validation.isEmptyLong(livingAddress.getAddr_id())) {
			addressService.createAddress(livingAddress, userData.getUserid(), "");
		} else {
			addressService.updateAddress(livingAddress, userData.getUserid(), "");
		}

		elService.create(
				new EventLog(userData.getBukrs(), EventLog.TYPE_WARNING, "Редактирование сотрудника: " + staff.getLF(),
						userData.getStaff() == null ? 0 : userData.getStaff().getStaff_id()));
	}

	@Override
	public Staff searchByIinBin(String a_iin_bin) throws DAOException {
		Staff stf = new Staff();
		try {
			int count = 0;
			count = stfDao.countStaffbyIinBin(a_iin_bin).intValue();
			if (count == 0) {
				throw new DAOException("No employee found");
			} else if (count == 1) {
				stf = stfDao.findByIinBin(a_iin_bin);
				setP_salary_list(salDao.findByStaffId(stf.getStaff_id()));
				// setP_payroll_list(prlDao.findByStaffId(stf.getStaff_id()));
				return stf;
			} else {
				throw new DAOException("Unexpected error");
			}
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public Staff searchById(Long a_staff_id) throws DAOException {
		Staff stf = new Staff();
		try {
			stf = stfDao.find(a_staff_id);
			return stf;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public List<Payroll> getP_payroll_list() {
		return p_payroll_list;
	}

	public void setP_payroll_list(List<Payroll> p_payroll_list) {
		this.p_payroll_list = p_payroll_list;
	}

	@Override
	public List<Salary> getP_salary_list() {
		return p_salary_list;
	}

	public void setP_salary_list(List<Salary> p_salary_list) {
		this.p_salary_list = p_salary_list;
	}

	@Override
	public List<Staff> dynamicSearchStaffSalary(Staff a_staff, Salary a_salary) throws DAOException {
		try {
			List<Staff> p_staff_list = new ArrayList<Staff>();
			String dynamicWhere = "";
			a_salary.setBukrs(a_salary.getBukrs().replaceAll("\\s", ""));
			if (!(a_salary.getBukrs().equals(null)) && !(a_salary.getBukrs().equals("0"))) {
				dynamicWhere = dynamicWhere + " and sal.bukrs = '" + a_salary.getBukrs() + "'";
			} else {
				throw new DAOException("Выберите компанию");
			}
			if (a_salary.getBranch_id() != null && a_salary.getBranch_id() > 0) {
				dynamicWhere = dynamicWhere + " and sal.branch_id = " + a_salary.getBranch_id();
			} else {
				throw new DAOException("Выберите филиал");
			}

			if (a_salary.getPosition_id() != null && a_salary.getPosition_id() > 0) {
				dynamicWhere = dynamicWhere + " and sal.position_id = " + a_salary.getPosition_id();
			} else {
				throw new DAOException("Выберите должность");
			}

			a_staff.setFirstname(a_staff.getFirstname().replaceAll("\\s", ""));
			if (a_staff.getFirstname().length() > 0) {
				dynamicWhere = Validation.sqlMatchPatternDynamicWhere(dynamicWhere, "and", "=", "stf.firstname",
						a_staff.getFirstname());
			}
			a_staff.setLastname(a_staff.getLastname().replaceAll("\\s", ""));
			if (a_staff.getLastname().length() > 0) {
				dynamicWhere = Validation.sqlMatchPatternDynamicWhere(dynamicWhere, "and", "=", "stf.lastname",
						a_staff.getLastname());
			}
			a_staff.setMiddlename(a_staff.getMiddlename().replaceAll("\\s", ""));
			if (a_staff.getMiddlename().length() > 0) {
				dynamicWhere = Validation.sqlMatchPatternDynamicWhere(dynamicWhere, "and", "=", "stf.middlename",
						a_staff.getMiddlename());
			}

			String regx = ";";
			char[] ca = regx.toCharArray();
			for (char c : ca) {
				dynamicWhere = dynamicWhere.replace("" + c, "");
			}
			p_staff_list = stfDao.dynamicFindStaffSalary(dynamicWhere);
			return p_staff_list;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	public List<Staff> findByFIO(String f, String m, String l) {
		try {
			return stfDao.findByFIO(f, m, l);
		} catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
	}

	@Override
	public void createStaff(Staff s, User userData, Address livingAddress, Address residenceAddress) {
		String error = this.validateStaff(s, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		this.stfDao.create(s);

		this.createOrUpdateCustomer(s, new Customer());
		this.stfDao.update(s);

		livingAddress.setAddr_id(null);
		livingAddress.setCustomerId(s.getCustomer_id());
		residenceAddress.setCustomerId(s.getCustomer_id());
		residenceAddress.setAddr_id(null);
		addressService.createAddress(residenceAddress, userData.getUserid(), "");

		addressService.createAddress(livingAddress, userData.getUserid(), "");

		elService.create(new EventLog(userData.getBukrs(), EventLog.TYPE_WARNING, "Добавление сотрудника: " + s.getLF(),
				userData.getStaff() == null ? 0 : userData.getStaff().getStaff_id()));
	}

	@Override
	public List<Staff> dynamicSearchStaffSalary2(Staff a_staff, Salary a_salary) throws DAOException {
		try {
			List<Staff> p_staff_list = new ArrayList<Staff>();
			String dynamicWhere = "";
			a_salary.setBukrs(a_salary.getBukrs().replaceAll("\\s", ""));
			if (!(a_salary.getBukrs().equals(null)) && !(a_salary.getBukrs().equals("0"))) {
				dynamicWhere = dynamicWhere + " and sal.bukrs = '" + a_salary.getBukrs() + "'";
			} else {
				throw new DAOException("Выберите компанию");
			}
			if (a_salary.getBranch_id() != null && a_salary.getBranch_id() > 0) {
				dynamicWhere = dynamicWhere + " and sal.branch_id = " + a_salary.getBranch_id();
			} else {
				throw new DAOException("Выберите филиал");
			}

			if (a_salary.getPosition_id() != null && a_salary.getPosition_id() > 0) {
				dynamicWhere = dynamicWhere + " and sal.position_id = " + a_salary.getPosition_id();
			}

			a_staff.setFirstname(a_staff.getFirstname().replaceAll("\\s", ""));
			if (a_staff.getFirstname().length() > 0) {
				dynamicWhere = Validation.sqlMatchPatternDynamicWhere(dynamicWhere, "and", "=", "stf.firstname",
						a_staff.getFirstname());
			}
			a_staff.setLastname(a_staff.getLastname().replaceAll("\\s", ""));
			if (a_staff.getLastname().length() > 0) {
				dynamicWhere = Validation.sqlMatchPatternDynamicWhere(dynamicWhere, "and", "=", "stf.lastname",
						a_staff.getLastname());
			}
			a_staff.setMiddlename(a_staff.getMiddlename().replaceAll("\\s", ""));
			if (a_staff.getMiddlename().length() > 0) {
				dynamicWhere = Validation.sqlMatchPatternDynamicWhere(dynamicWhere, "and", "=", "stf.middlename",
						a_staff.getMiddlename());
			}

			String regx = ";";
			char[] ca = regx.toCharArray();
			for (char c : ca) {
				dynamicWhere = dynamicWhere.replace("" + c, "");
			}
			p_staff_list = stfDao.dynamicFindStaffSalary(dynamicWhere);
			return p_staff_list;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public void dismissStaff(Staff s) throws DAOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String cond = String.format(" staff_id = %d AND end_date >= '%s' ", s.getStaff_id(),
				sdf.format(Calendar.getInstance().getTime()));
		List<Salary> sList = salDao.findAll(cond);
		if (sList.size() > 0) {
			throw new DAOException("Невозможно уволнение. У сотрудника имеется должность");
		}

		s.setDismissed(1);
		stfDao.update(s);
	}

	@Override
	public void saveFile(Staff stf, Part p, User userData) throws DAOException {
		UpdFile file = new UpdFile();
		file.setCreated_by(userData.getUserid());
		file.setCreated_date(Calendar.getInstance().getTime());
		file.setFile_name(p.getSubmittedFileName());
		file.setFile_size(p.getSize());
		file.setMime_type(p.getContentType());

		fileService.create(p, file, userData);

		StaffFile sf = new StaffFile();
		sf.setStaff_id(stf.getStaff_id());
		sf.setFile_id(file.getId());
		sfDao.create(sf);
	}

	@Override
	public void saveStaffFile(Staff stf, UpdFile updFile, User userData) throws DAOException {
		StaffFile sf = new StaffFile();
		sf.setStaff_id(stf.getStaff_id());
		sf.setFile_id(updFile.getId());
		sfDao.create(sf);
	}

	@Override
	public List<UpdFile> findStaffAllFiles(Long staffId) {
		String subQuery = " SELECT s.file_id FROM StaffFile s WHERE staff_id = " + staffId;
		String cond = " id IN(" + subQuery + ")  ";
		return fileDao.findAll(cond);
	}

	@Override
	public void deleteStaffFile(Long staffId, Long fileId) throws DAOException {
		/**
		 * @TODO -- Надо еще удалить физически
		 */
		for (StaffFile sf : sfDao.findAllByStaffIdAndFileId(staffId, fileId)) {
			sfDao.delete(sf.getId());
			fileDao.delete(sf.getFile_id());
		}
	}

}
