package general.services;

import general.dao.DAOException;
import general.output.tables.InvoiceListItemFkage;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Invoice;
import general.tables.Matnr;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import logistics.LogFinClass;

//GS dogovor
//GC service
//GW spisanie
//RG return
//PE peremeshenie
public interface FinanceServiceLogistics {

	//spisanie po poradazhe
	@Transactional
	public Long removeMatnrFromWerks (List<LogFinClass> l_mls,Long  GC_GS_AK_awkey, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException;
	
	
	//vozvrat posle prodazhi
	@Transactional
	public Long returnMatnrToWerks(List<LogFinClass> l_mls,Bkpf a_bkpf_GW, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException;
	

	//spsianie po garantii
	@Transactional
	public Long removeMatnrFromWerksGuarantee (List<LogFinClass> l_mls,Bkpf a_bkpf, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks, String a_barcode ) throws DAOException;
	
	//spsianie na remont
	@Transactional
	public Long removeMatnrFromWerksRemont (List<LogFinClass> l_mls,Matnr a_remont_matnr, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks, String a_barcode) throws DAOException;
	
	//spisnanie uterie
	@Transactional
	public Long removeMatnrFromWerksLost (List<LogFinClass> l_mls, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException;
	
	//otmenit spisanni tovar
	@Transactional
	public Long returnIncorrectRemovedMatnr (Bkpf a_bkpf_GW,String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException;
	
	@Transactional
	public List<InvoiceListItemFkage> getInvoiceListToFkage(Invoice a_invoice) throws DAOException;
	
	@Transactional
	public Long createFkage(Bkpf a_bkpf, List<Bseg> l_bseg, Branch a_branch) throws DAOException;
	
	//otpravka tovara
	@Transactional
	public Long moveFromWerks(List<LogFinClass> l_olc, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException;

	//poluchenie tovara
	@Transactional
	public Long movetoWerks(List<LogFinClass> l_orlc,String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException;
	
	@Transactional
	public Long addMatnrToWerksFound (List<LogFinClass> l_mls, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException;
	
	@Transactional
	public String get6010MatnrHkont(String a_bukrs,Long a_matnr,String a_method);
	
	@Transactional
	public Long tradeInGoodsIn (List<LogFinClass> l_mls, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks, Long a_customerId) throws DAOException;
}
