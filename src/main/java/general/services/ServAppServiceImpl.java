package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedProperty;
import javax.persistence.Query;

import f4.BukrsF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.OrderDao;
import general.dao.OrderListDao;
import general.dao.PaymentScheduleDao;
import general.dao.ServiceApplicationDao;
import general.dao.StaffDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import user.UserRoleActions;
import dms.contract.Dmsc01;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.Contract;
import general.tables.Customer;
import general.tables.Order;
import general.tables.OrderList;
import general.tables.PaymentSchedule;
import general.tables.ServiceApplication;
import general.tables.Staff;

@Service("servAppService")
public class ServAppServiceImpl implements ServAppService {

	@Autowired
	ServiceApplicationDao saDao;
	
	@Override
	public void createServApp(ServiceApplication a_servApp,
			Long a_userid, String a_trcode) throws DAOException {
		try {
			ServiceApplication sa = new ServiceApplication();
			
			if (a_servApp != null) {
				sa = a_servApp;
				
				if (sa.getApp_type() == null || sa.getApp_type() == 0 ) {
					throw new DAOException("Please select the type of Service Application");					
				}
				
				saDao.create(sa);
				
				String oNumber = ""; 
		        
		        oNumber = Long.toString(sa.getBranch_id()) + Long.toString(sa.getApp_type()) + Long.toString(sa.getId());
		        sa.setApp_number(Long.parseLong(oNumber));
		        saDao.update(sa);
				
			}
			
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}

	@Override
	public void updateServApp(ServiceApplication a_servApp, Long a_userid,
			String a_trcode) throws DAOException {
		ServiceApplication sa = new ServiceApplication();
		try {
			saDao.update(a_servApp);
		} catch (Exception ex ) {
			throw new DAOException(ex.getMessage());
		}		
	}

	@Override
	public ServiceApplication getByID(Long a_servAppID) throws DAOException {
		ServiceApplication sa = new ServiceApplication();

		return sa;
	}

	@Override
	public List<ServiceApplication> getListByBranchID(Long a_branch_id)
			throws DAOException {
		List<ServiceApplication> sa_list = new ArrayList<ServiceApplication>();

		return sa_list;
	}
	

}