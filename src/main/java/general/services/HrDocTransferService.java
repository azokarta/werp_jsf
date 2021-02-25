package general.services;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.HrDocTransfer;
import general.tables.Salary;
import user.User;

public interface HrDocTransferService {
	@Transactional
	public void create(HrDocTransfer hdt, User userData) throws DAOException;

	@Transactional
	public void update(HrDocTransfer hdt, User userData) throws DAOException;

	@Transactional
	public void send(HrDocTransfer hdt, User userData) throws DAOException;

	@Transactional
	public void refuse(HrDocTransfer hdt, User userData) throws DAOException;

	@Transactional
	public void approve(HrDocTransfer hdt, User userData) throws DAOException;

	@Transactional
	public void close(HrDocTransfer hdt, User userData) throws DAOException;

	@Transactional
	public void addApprover(HrDocTransfer doc, Salary salary, User userData) throws DAOException;

	@Transactional
	public void addAmount(HrDocTransfer doc, User userData) throws DAOException;
	
	@Transactional
	public void removeApprover(HrDocTransfer doc, User userData, Long approverId) throws DAOException;

	@Transactional
	public void addSalariesAndCloseDoc(HrDocTransfer doc, User userData) throws DAOException;
}
