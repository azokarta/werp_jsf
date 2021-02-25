package general.services;

import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Bsik;
import general.tables.Hkont;
import user.User;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;


public interface FinanceService { 	
	

	@Transactional
	void check_empty_fields(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException;

	@Transactional
	Long insertNewFiDoc(Bkpf a_bkpf, List<Bseg> l_bseg, List<Bsik> l_bsik) throws DAOException;
	
	@Transactional
	void updateFiDoc(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException;
	
	@Transactional
	Bkpf cancelFiDoc(Bkpf a_bkpf, List<Bseg> l_bseg,Long a_userID, String a_tcode) throws DAOException;
	
	
	@Transactional
	public Long createFAICF(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException;
	
	@Transactional
	public Long createFAES(Bkpf a_bkpf, List<Bseg> l_bseg, Long a_staff_id) throws DAOException;
	
	@Transactional
	public Long createFACO01(Bkpf a_bkpf, List<Bseg> l_bseg,Bkpf a_bkpf_VZ) throws DAOException;
	
	@Transactional
	public Long createFACI01(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException;
	
	@Transactional //tolko nachislenie
	public Long createAccountPayableDocs(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException;
	
	@Transactional //tolko nachislenie
	public Long createAccountReceivableDocs(Bkpf a_bkpf, List<Bseg> l_bseg, Branch a_branch) throws DAOException;
	
	@Transactional
	public void cashPaymentSingleEmployee(Bkpf a_bkpf) throws DAOException;
	
	@Transactional
	public Long EpxenseNoInvoice(Bkpf a_bkpf, List<Bseg> al_bseg) throws DAOException;
	
	@Transactional
	public Long IncomeNoInvoice(Bkpf a_bkpf, List<Bseg> al_bseg) throws DAOException;
	
	@Transactional //nachislenie i oplata
	public Long createAccountPayableDocsPayRA(Bkpf a_bkpf, List<Bseg> l_bseg, Branch a_branch, Hkont a_hkont) throws DAOException;
	
	@Transactional //nachislenie i oplata
	public Long createAccountReceivableDocsRA(Bkpf a_bkpf, List<Bseg> l_bseg, Branch a_branch, String a_hkont) throws DAOException;
	
	@Transactional
	public Bkpf cancelFA02(Bkpf a_bkpf,List<Bseg> al_bseg, User userData, String a_tcode) throws DAOException;
	
	@Transactional
	public Long IncomeNoInvoiceFKSG(Bkpf a_bkpf, List<Bseg> al_bseg, Long userId,Long dealerId) throws DAOException;
	
	

	
	@Transactional //tolko nachislenie
	public Long createFdint(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException;
	
	@Transactional
	public Long createFkint(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException;

	
}
