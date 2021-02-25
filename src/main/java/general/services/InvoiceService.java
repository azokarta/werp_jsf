package general.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import user.User;
import general.dao.DAOException;
import general.tables.AccountMatnrState;
import general.tables.Bkpf;
import general.tables.Bseg;
import general.tables.Contract;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Promotion;
import general.tables.ServicePos;
import general.tables.ServiceTable;

public interface InvoiceService {

	public List<InvoiceItem> findStaffItems(Long staffId);

	@Transactional
	public void create(Invoice in, List<InvoiceItem> items, Map<String, List<Long>> parentDocs,
			Map<Long, List<String>> barcodesMap, Long userId) throws DAOException;

	@Transactional
	public void delete(Invoice in, User userData) throws DAOException;

	@Transactional
	public void update(Invoice in, List<InvoiceItem> items, Map<String, List<Long>> parentDocs,
			Map<Long, List<String>> barcodesMap, Long userId) throws DAOException;

	@Transactional
	public void createWriteoffDocFromContract(Contract contract, List<Promotion> promoList) throws DAOException;

	@Transactional
	public void updateWriteoffDocFromContract(Contract contract, List<Promotion> promoList) throws DAOException;

	@Transactional
	public void onChangeContractPromo(Contract contract, List<Promotion> promoList, Long currUserId)
			throws DAOException;

	@Transactional
	public void checkInvoicesStatus(Long awkey, List<Long> ids) throws DAOException;

	Map<Date, Map<Long, List<InvoiceItem>>> getGeneratedWerksLogData(Long werks, Date fromDate, Date toDate,
			Integer typeId, Long staffId);

	@Transactional
	public void createWriteoffDocFromService(ServiceTable st, List<ServicePos> spList) throws DAOException;

	@Transactional
	public void writeoffFromService(Long serviceAwkey, Long userId, String a_bukrs) throws DAOException;

	@Transactional
	public void createWriteoffDocFromBkpf(Bkpf bkpf, List<Bseg> bsegList, Long userId, Long dealerId, Long awkey)
			throws DAOException;

	@Transactional
	public void onCancelBkpf(Long awkey, User userData) throws DAOException;

	@Transactional
	public void onChangeContractAwkey(Long oldAwkey, Long newAwkey, User userData) throws DAOException;

	@Transactional
	public void returnWriteoffFromService(ServiceTable oldService, User userData) throws DAOException;

	@Transactional
	public void saveAms(Invoice in, List<AccountMatnrState> amsList, User userData) throws DAOException;

	@Transactional
	public void createReturnDocFromContract(Contract con, User userData) throws DAOException;

	public Double getWerksBalance(Long werks, Long matnr, Date toDate);

	// @Transactional
	// public void updateContractForFix(Contract c) throws DAOException;
	//
	@Transactional
	public void changedContractAwkey(Contract con);

	@Transactional
	public void updateInvoicesForFix2() throws DAOException;

	@Transactional
	public void updateInvoicesForFix() throws DAOException;

	public void checkStaffAccountabilityMatnr(Long staffId, Long werks, Map<Long, Double> matnrMengeMap)
			throws DAOException;
}