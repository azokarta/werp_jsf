package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.Promotion;

public interface ContractService2 {

	 	@Transactional
	    public boolean onCreateContractHistoryPromo(Long a_contractId, List<Promotion> promoList, Long a_userId) throws DAOException;

	    @Transactional
	    public boolean onRemoveContractHistoryPromo(Long a_contractNumber, Long a_userId) throws DAOException;

	    @Transactional
	    public boolean onCreateContractHistoryMatnr(Long a_contractId, Long a_matnr, Long a_userId) throws DAOException;

	    @Transactional
	    public boolean onRemoveContractHistoryMatnr(Long a_contractNumber, Long a_userId) throws DAOException;

	    @Transactional
	    public boolean onCreateContractMatnr(Long a_contractNumber, String a_tovarSerial, Long a_matnrListId, Long a_userId) throws DAOException;

	    @Transactional
	    public boolean onCancelWriteOffAndReturnContractMatnr(Long a_contractNumber, Long a_userId) throws DAOException;

	    @Transactional
	    public boolean onCreateContractPromo(List<Promotion> a_promoList, Long a_contractId) throws DAOException;

	    @Transactional
	    public boolean onCancelWriteOffAndReturnContractPromo(Long a_contractNumber) throws DAOException;

}
