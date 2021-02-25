package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.Customer;
import general.tables.Order;
import general.tables.OrderList;
import general.tables.PaymentSchedule;
import general.tables.ServiceApplication;
import general.tables.Staff;

public interface ServAppService {
	
	@Transactional
	void createServApp(ServiceApplication a_servApp, Long a_userid, String a_trcode) throws DAOException;
	
	@Transactional
	void updateServApp(ServiceApplication a_servApp, Long a_userid, String a_trcode) throws DAOException;
	
	@Transactional
	ServiceApplication getByID( Long a_servAppID) throws DAOException;
	
	@Transactional
	List<ServiceApplication> getListByBranchID(Long a_branch_id) throws DAOException;
	
}
