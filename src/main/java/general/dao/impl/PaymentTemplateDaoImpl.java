package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.PaymentTemplateDao;
import general.tables.PaymentTemplate;

import org.springframework.stereotype.Component;
@Component("paymentTemplateDao")
public class PaymentTemplateDaoImpl extends GenericDaoImpl<PaymentTemplate> implements PaymentTemplateDao{
	
	@Override
	public List<PaymentTemplate> findAll(){ 
    	
    	Query query = this.em
                .createQuery("select b FROM PaymentTemplate b"); 
    	//query.setMaxResults(20);  
    	List<PaymentTemplate> pt =  query.getResultList();
    	return pt;
    }
	
	@Override
	public List<PaymentTemplate> findAll(String condition) { 
    	Query query = this.em
                .createQuery("select b FROM PaymentTemplate b Where " + condition); 
    	//query.setMaxResults(20);  
    	List<PaymentTemplate> pt =  query.getResultList();
    	return pt;
    }
	
}
