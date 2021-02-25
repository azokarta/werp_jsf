package general.dao.impl;


import java.util.List;

import javax.persistence.Query;

import general.dao.InvoicePaymentTermDao;
import general.tables.InvoicePaymentTerm;

import org.springframework.stereotype.Component;

@Component("invoicePaymentTermDao")
public class InvoicePaymentTermDaoImpl extends GenericDaoImpl<InvoicePaymentTerm> implements InvoicePaymentTermDao{

	@Override
	public List<InvoicePaymentTerm> findAll(String condition) {
		String s = " SELECT pt FROM InvoicePaymentTerm pt ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}

	
}
