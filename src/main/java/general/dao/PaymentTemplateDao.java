package general.dao;
 

import java.util.List;

import general.tables.PaymentTemplate;

public interface PaymentTemplateDao extends GenericDao<PaymentTemplate>{ 
	public List<PaymentTemplate> findAll();
	public List<PaymentTemplate> findAll(String con);
}
