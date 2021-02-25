package general.services;

import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.ContractHistory;
import general.tables.Customer;
import general.tables.MatnrList;
import general.tables.PaymentSchedule;
import general.tables.Payroll;
import general.tables.Promotion;
import general.tables.Staff;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import crm.tables.CrmDocDemo;
import user.User;

public interface ContractService {
	@Transactional
	boolean createContract( Contract a_contract, Branch a_branch, User userData, String a_trcode, String a_currency, double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId, PaymentSchedule a_ps[], Long a_promos[], double a_ref_skidka_vol, double refRate, String refWaers, String refDiscountWaers, String locale,CrmDocDemo demo) throws DAOException;
	
	@Transactional
	boolean updateContract( Contract a_new_con, PaymentSchedule a_ps[], User userData, String locale) throws DAOException;
	
	@Transactional
	boolean updateContractPrice( Contract a_contract, Bkpf a_old_bkpf, Branch a_branch, User userData, String a_tcode, 
			String a_waers, double a_kursf, double a_dmbtr, double a_wrbtr, Long a_matnr,  PaymentSchedule a_ps[], ContractHistory a_ch ) throws DAOException;

	@Transactional
	boolean updateContractPaymentGraphic(Contract a_contract,  
			PaymentSchedule a_ps[], ContractHistory a_ch, Long a_userId) throws DAOException;

	@Transactional
	boolean updateDealerDiscount(Contract a_contract,  
			PaymentSchedule a_ps[], ContractHistory a_ch, User userData, Long transactionId, String trCode) throws DAOException;
	
	@Transactional
	boolean CancelContract(Contract a_contract, Bkpf a_bkpf, double a_kursf, double a_dmbtr, 
			double a_wrbtr, User userData, String a_tcode, String info) throws DAOException;

	@Transactional
	boolean updateContractPromos(Contract a_contract, Bkpf a_bkpf, List<Promotion> promoList, Long a_userID, String a_tcode, String info, List<ContractHistory> a_chl) throws DAOException;

	@Transactional
	boolean SaveContractHistory(Long con_id, List<ContractHistory> a_chl, Long a_userId) throws DAOException;
	
	@Transactional
	boolean forContractReturnMatnr(Long a_con_number, List<Long> matnrId_list, String info, Long userId)
			throws DAOException;
	
	@Transactional 
	boolean AddRemoveRecommender(Contract a_con, Contract a_oldCon, double refRate, String refWaers, String refWaersDsicount, Bkpf a_bkpf_GC, Long a_userID, String a_tcode) throws DAOException;
	
	@Transactional 
	boolean changeContractTovar(Contract a_con, Contract a_oldCon, Bkpf a_bkpf_GC, Long a_userID, String a_tcode) throws DAOException;
		
	@Transactional
	boolean forWriteOffAssets(Contract contract, List<MatnrList> matnrList_L, Long userId)
			throws DAOException;
	
	@Transactional
	boolean changeDealer(Contract a_con,Long a_old_dealer_staff_id,Long a_new_dealer_staff_id,Long a_userID, String tCode)
			throws DAOException;	
	
	@Transactional
	Contract searchByContractNumber( Long a_contract_number) throws DAOException;
	
	@Transactional
	Customer getCustomerById( Long a_customer_id) throws DAOException;
	
	@Transactional
	Staff getStaffById( Long a_staff_id) throws DAOException;
	
	double countTotalPaid(Contract con) throws DAOException;
	
	double countTotalSkidka(Contract con) throws DAOException;
	
	@Transactional
	boolean createCutoffpriceContract(Contract a_contract, Branch a_branch, User userData, Long a_trId, String a_trcode, String a_currency, 
			double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId, PaymentSchedule a_ps[], String locale) throws DAOException;
	
	@Transactional
	boolean approveCutoffpriceContract(Contract a_contract, Branch a_branch, User userData, Long a_trId, String a_trCode, String a_currency, 
			double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId, PaymentSchedule a_ps[], String locale) throws DAOException;
	
	@Transactional
	boolean declineCutoffpriceContract(Contract a_contract, User userData, Long transactionId) throws DAOException;
	
	@Transactional
	boolean createServiceContract(Contract a_contract, String a_currency,
			Branch a_branch, User userData, Long transaction_id, String transaction_code) throws DAOException;
	
	@Transactional
	boolean createUniversalContract(Contract a_contract, Branch a_branch, List<Payroll> pl, User userData, Long a_trId, String a_trcode, String a_currency, 
			double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId, PaymentSchedule a_ps[], String locale) throws DAOException;
	
	@Transactional
	boolean approveUniversalContract(Contract a_contract, Branch a_branch, User userData, Long a_trId, String a_trCode, String a_currency, 
			double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId, PaymentSchedule a_ps[], String locale) throws DAOException;
	
	@Transactional
	public boolean createServFilterVCForNewContract(Contract a_con) throws Exception;
}

