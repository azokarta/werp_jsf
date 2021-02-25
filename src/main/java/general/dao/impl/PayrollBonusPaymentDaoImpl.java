package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.PayrollBonusPaymentDao;
import general.tables.PayrollBonusPayment;
import general.tables.Staff;
@Component("payrollBonusPaymentDao")
public class PayrollBonusPaymentDaoImpl extends GenericDaoImpl<PayrollBonusPayment> implements PayrollBonusPaymentDao{
	public List<PayrollBonusPayment> findAll(){  
    	Query query = this.em
                .createQuery("select p FROM PayrollBonusPayment p"); 
    	 
    	List<PayrollBonusPayment> pbp =  query.getResultList();
    	return pbp;
    }
	
	public void deleteAll(){      	       
        Query query = em.createQuery ("DELETE FROM PayrollBonusPayment");
		int deleted = query.executeUpdate (); 
    }
}
