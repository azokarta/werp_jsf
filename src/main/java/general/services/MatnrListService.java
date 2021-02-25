package general.services;

import general.dao.DAOException;
import general.tables.Contract;
import general.tables.Invoice;
import general.tables.MatnrList;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import user.User;

public interface MatnrListService {

	@Transactional
	public void createMlForFix(MatnrList ml) throws DAOException;

	/*****************************************************/
	@Transactional
	public void doPosting(Invoice in, User userData) throws DAOException;

	@Transactional
	public void doWriteoff(Invoice in, Long userId) throws DAOException;

	@Transactional
	public void doSend(Invoice in, User userData) throws DAOException;

	@Transactional
	public void doReceive(Invoice in, User userData) throws DAOException;

	@Transactional
	public void doAccountability(Invoice in, User userData) throws DAOException;

	@Transactional
	public void doReturn(Invoice in, User userData) throws DAOException;

	@Transactional
	public void doReturnAccountability(Invoice in, User userData) throws DAOException;
	
	@Transactional
	public void doReserve(Invoice in, User userData) throws DAOException;
	
	@Transactional
	public void doCancelReserve(Invoice in, Long userId) throws DAOException;
	
	@Transactional
	public void doCancelWriteoffDoc(Invoice in, Long userId) throws DAOException;

	@Transactional
	public void doWriteoffLoss(Invoice in, User userData) throws DAOException;

	@Transactional
	public void doResold(Contract contract, User userData) throws DAOException;
	
	@Transactional
	public void onCreateServiceContract(Contract c, User userData) throws DAOException;

	@Transactional
	public void restoreSoldMatnr(Long contractNumber, Long werks, User userData) throws DAOException;

	public List<MatnrList> findStaffMatnrList(Long staffId);

	/************** FIXES *****************/
	// @Transactional
	// public void doFix1() throws DAOException;
}