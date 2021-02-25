package general.services;

import general.dao.DAOException;
import general.output.tables.ServFilterOutput;
import general.tables.ServCRMHistory;
import general.tables.ServCRMSchedule;
import general.tables.ServFilter;
import general.tables.ServFilterVC;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import user.User;

public interface ServFilterVCService {
	@Transactional
	List<ServFilterOutput> getZFList(String in_bukrs, Long in_branchID, Date in_date, 
			int in_list_type, int inListWar, Long in_con_num, String in_tovSN, int in_cat) throws DAOException;
	
	@Transactional
	public boolean createNewSCH(ServCRMHistory a_sch, User userData, Long trId) throws DAOException;
	
	@Transactional
	public boolean createNewSCS(ServCRMSchedule a_scs, User userData, Long trId) throws DAOException;
	
	public List<ServFilterVC> getZFListForPlan(String in_bukrs, Long in_branchID, Date in_date, boolean overdue) throws DAOException;
	
	public ServFilterVC getSFByContractId(Long inConId) throws DAOException;
	
	@Transactional
	public boolean savePlan(Long inBranchId, byte inType, Date inDate, double inSummPlan, String inWaers, User inUser, Long transactionId) throws DAOException;
	
	@Transactional
	public boolean saveDone(Long inBranchId, byte inType, Date inDate, double inSummDone, String inWaers, User inUser, Long transactionId) throws DAOException;
	
	@Transactional
	public boolean deleteSCHByServId(Long in_servId, User userData, Long trId) throws DAOException;
	
	@Transactional
	public boolean updateSF(ServFilterVC in_sf, User userData, Long trId) throws DAOException;
	
}
