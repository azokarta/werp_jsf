package general.services;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import dms.contract.ContractFinOperations;
import general.universal.SaveFacmassinTable;
import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.Payroll;
import general.tables.Promotion;

public interface FinanceServiceDms {
	@Transactional
	boolean massContractPayments (List<SaveFacmassinTable> al_sft) throws DAOException;
	
	@Transactional
	public Long createContractRecievable (Contract a_contract, Branch a_branch, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, double a_dmbtr, double a_wrbtr, Long a_matnr, List<ContractFinOperations> contrFOList, List<Promotion> promoList) throws DAOException;
	
	@Transactional
	public void updateContractPayment(Bkpf p_bkpf,double p_dmbtr, double p_wrbtr, Long payment_schedule_id, Long matnr, String debitor_hkont, Calendar curDate) throws DAOException;
	
	@Transactional
	public void updateContract(Long a_awkey,String a_bukrs) throws DAOException;
	
	@Transactional
	public void counterUpdateContractPayment(Bkpf p_bkpf_SK,double p_dmbtr_SK, double p_wrbtr_SK,  Long a_userID,String a_tcode) throws DAOException;
	
	@Transactional
	public void discountToRecommender(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr, Long a_matnr) throws DAOException;
	
	@Transactional
	public void rewardToRecommender(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException;
	
	//@Transactional
	//public void discountFromRecommender(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
	//		String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
	//		Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException;
	
	@Transactional
	public void discountFromDealer(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException;
	
	@Transactional
	public void chargeDealer(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException;
	
	@Transactional
	public void bargainSaleDealer(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException;
	
	@Transactional
	public boolean addPromoMaterials(Bkpf a_bkpf_GC, List<Promotion> promoList, Long a_userID, String a_tcode, ContractFinOperations a_contrFO) throws DAOException;
	
	@Transactional
	public boolean addDiscountFromDealer(Bkpf a_bkpf_GC, Long a_userID,String a_tcode, ContractFinOperations a_contrFO) throws DAOException;
	
	//@Transactional
	//public boolean addDiscountFromRecommender(Bkpf a_bkpf_GC, Long a_userID,String a_tcode, ContractFinOperations a_contrFO) throws DAOException;
	
	@Transactional
	public boolean addRewardToRecommender(Bkpf a_bkpf_GC, Long a_userID,String a_tcode, ContractFinOperations a_contrFO) throws DAOException;
	
	@Transactional
	public boolean addDiscountToRecommender(Bkpf a_bkpf_GC, Long a_userID, String a_tcode, ContractFinOperations a_contrFO) throws DAOException;
	
	@Transactional
	public Long present(Contract a_contract, Branch a_branch, Long a_userID, String a_tcode, Long a_matnr, List<Promotion> promoList) throws DAOException;
	
	@Transactional
	public Long recreateContract(Contract a_contract,Bkpf old_bkpf_GC, Branch a_branch_new, Long a_userID_new, String a_tcode_new, 
			String a_waers_new, double a_kursf_new, double a_dmbtr_new, double a_wrbtr_new, Long a_matnr_new) throws DAOException;
	
	@Transactional
	public void cancelContract(Contract a_contract, Bkpf a_bkpf_GC,double a_kursf_vozvart, double a_dmbtr_vozvrat, double a_wrbtr_vozvrat, Long a_userID, String a_tcode)throws DAOException;
	
	@Transactional
	public void changeDealer(Contract a_con,Long a_old_dealer_staff_id,Long a_new_dealer_staff_id,Long a_userID, String a_tcode)throws DAOException;
	
	@Transactional
	public boolean universalContractCreatePayroll(List<Payroll> al_payroll,String a_tcode,Long a_userID) throws DAOException;	
	
	@Transactional
	public boolean universalContractSavePayroll(Contract a_contract, String a_opertype,String a_tcode,Long a_userID) throws DAOException;
	//@Transactional
	//public void conClosedFromDepositToDealer(Bkpf wa_bkpf_GC, Bkpf wa_bkpf_payment) throws DAOException;

}
