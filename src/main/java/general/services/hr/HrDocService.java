package general.services.hr;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.HrDoc;
import general.tables.Salary;
import user.User;

public interface HrDocService {
	@Transactional
	public void create(HrDoc doc, User userData) throws DAOException;

	@Transactional
	public void update(HrDoc doc, User userData) throws DAOException;

	@Transactional
	public void send(HrDoc doc, User userData) throws DAOException;

	@Transactional
	public void refuse(HrDoc doc, User userData,String note) throws DAOException;

	@Transactional
	public void approve(HrDoc doc, User userData) throws DAOException;

	@Transactional
	public void close(HrDoc doc, User userData) throws DAOException;

	@Transactional
	public void addApprover(HrDoc doc, Salary salary, User userData) throws DAOException;

	@Transactional
	public void addAmount(HrDoc doc, User userData) throws DAOException;
	
	@Transactional
	public void removeApprover(HrDoc doc, User userData, Long approverId) throws DAOException;

	@Transactional
	public void addSalariesAndCloseDoc(HrDoc doc, User userData) throws DAOException;
	
	@Transactional
	public void dismissEmployeesAndCloseDoc(HrDoc doc, User userData) throws DAOException;
	
	@Transactional
	public void cancelDocument(HrDoc doc, User userData) throws DAOException;
	
	@Transactional
	public void completeDocument(HrDoc doc, User userData) throws DAOException;
}
