package general.dao.impl;


import java.util.List;

import javax.persistence.Query;

import general.dao.InvoiceDeliveryTermDao;
import general.tables.InvoiceDeliveryTerm;

import org.springframework.stereotype.Component;

@Component("invoiceDeliveryTermDao")
public class InvoiceDeliveryTermDaoImpl extends GenericDaoImpl<InvoiceDeliveryTerm> implements InvoiceDeliveryTermDao{

	@Override
	public List<InvoiceDeliveryTerm> findAll(String condition) {
		String s = " SELECT pt FROM InvoiceDeliveryTerm pt ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}	
}
