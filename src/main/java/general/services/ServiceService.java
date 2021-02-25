package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import user.User;
import general.dao.DAOException;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.ServPacketWar;
import general.tables.ServiceTable;
import general.tables.ServicePos;


public interface ServiceService {

	@Transactional
	boolean createService(ServiceTable a_service, Branch a_branch, Contract a_con, Long a_customer_id, User userData, String a_trCode, Long a_trId, List<ServicePos> a_sp) throws DAOException;
	
	@Transactional
	boolean createServiceFitting(ServiceTable a_service, Branch a_branch, Contract a_con, User userData, Long a_trId) throws DAOException;
	
	@Transactional
	boolean createServiceZamenFilter(ServiceTable a_service, Branch a_branch, Contract a_con, Long a_customer_id, User userData, String a_trCode, Long a_trId, List<ServicePos>  a_sp) throws DAOException;
	
	@Transactional
	boolean createServiceProphylaxis(ServiceTable a_service, Branch a_branch, Contract a_con, Long a_customer_id, User userData, String a_trCode, Long a_trId, List<ServicePos> a_sp, List<ServPacketWar> spWarL) throws DAOException;
	
	@Transactional
	boolean paymentStateUpdate(Long awkey, double sum, String a_bukrs) throws DAOException;
	
	@Transactional
	boolean updateService(ServiceTable a_service, ServiceTable a_service_old, Branch a_branch, Contract a_con, User userData, String a_trCode, Long a_trId ) throws DAOException;
	
	@Transactional
	boolean cancelService(ServiceTable a_service, User userData, String a_trCode) throws DAOException;
	
	@Transactional
	boolean cancelFitting(ServiceTable a_service, Branch a_branch, Contract a_con, User userData, String a_trCode, Long a_trId) throws DAOException;
	
	@Transactional
	boolean cancelFilter(ServiceTable a_service, Branch a_branch, User userData, String a_trCode, Long a_trId, List<ServicePos> a_sp) throws DAOException;
	
	@Transactional
	boolean cancelPacket(ServiceTable a_service, Branch a_branch, User userData, String a_trCode, Long a_trId) throws DAOException;
	
}