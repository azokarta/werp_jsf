package general.services;

import general.dao.DAOException;
import general.services.FinanceServServiceImpl.ServiceItem;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface FinanceServService {
	@Transactional
	public long sellParts(List<ServiceItem> al_serviceItem, String a_bukrs,Long a_customer_id, String a_waers, Long a_branch_id, Long a_user_id, String a_tcode,double discount_summa,Date a_servDate) throws DAOException;
	@Transactional
	public void cancelService(Long a_awkey,Long a_userID, String a_tcode, Long a_master_staff_id, Long a_master_position_id, 
			double a_summa_otmena_master, String a_currency_otmena, Date a_service_date, Long a_service_id,  Long a_operator_staff_id, Long a_operator_position_id, 
			double a_summa_otmena_oparator, String a_bukrs) throws DAOException;
}
