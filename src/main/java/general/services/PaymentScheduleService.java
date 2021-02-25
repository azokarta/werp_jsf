package general.services;
 
import java.util.List;

import general.dao.DAOException;
import general.tables.PaymentSchedule;

import org.springframework.transaction.annotation.Transactional;

public interface PaymentScheduleService {
	
	@Transactional
	void create (List<PaymentSchedule> l) throws DAOException;
}
