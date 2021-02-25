package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.BankAccountDao;
import general.tables.BankAccount;

import org.springframework.stereotype.Component;

@Component("bankAccountDao")
public class BankAccountDaoImpl extends GenericDaoImpl<BankAccount> implements BankAccountDao{
	
	@SuppressWarnings("unchecked")
	public List<BankAccount> findAll(String condition) {
		String q = " SELECT ba FROM BankAccount ba";
		if(condition.length() > 0){
			q += " WHERE " + condition;
		}
		Query query = this.em.createQuery(q);
		List<BankAccount> l = query.getResultList();
		return l;
	}

	@Override
	public void deleteCustomerAccount(Long customerId) {
		this.em.createQuery("DELETE FROM BankAccount ba WHERE ba.customer_id = " + customerId).executeUpdate();
	}
}
