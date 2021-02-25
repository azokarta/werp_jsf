package general.services;

import org.springframework.transaction.annotation.Transactional;

import user.User;

import java.util.List;

import javax.servlet.http.Part;

import general.dao.DAOException;
import general.tables.Address;
import general.tables.Payroll;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.UpdFile;

public interface StaffService {
	@Transactional
	void createStaff(Staff staff, Salary salary) throws DAOException;

	@Transactional
	Staff searchByIinBin(String a_iin_bin) throws DAOException;

	@Transactional
	Staff searchById(Long a_staff_id) throws DAOException;

	@Transactional
	void updateStaff(Staff a_staff, Salary a_salary) throws DAOException;

	@Transactional
	void updateStaff(Staff a_staff, User userData, Address livingAddress, Address residenceAddress) throws DAOException;

	public List<Payroll> getP_payroll_list();

	public List<Salary> getP_salary_list();

	@Transactional
	List<Staff> dynamicSearchStaffSalary(Staff a_staff, Salary a_salary) throws DAOException;

	@Transactional
	public List<Staff> dynamicSearchStaffSalary2(Staff a_staff, Salary a_salary) throws DAOException;

	@Transactional
	List<Staff> findByFIO(String f, String m, String l) throws DAOException;

	@Transactional
	void createStaff(Staff s, User userData, Address livingAddress, Address residenceAddress) throws DAOException;

	@Transactional
	void dismissStaff(Staff s) throws DAOException;

	@Transactional
	void saveFile(Staff stf, Part p, User userData) throws DAOException;

	@Transactional
	public void saveStaffFile(Staff stf, UpdFile updFile, User userData) throws DAOException;

	List<UpdFile> findStaffAllFiles(Long staffId);

	@Transactional
	public void deleteStaffFile(Long staffId, Long fileId) throws DAOException;
}