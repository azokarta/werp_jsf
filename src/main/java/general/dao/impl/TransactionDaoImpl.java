package general.dao.impl;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.TransactionDao;
import general.tables.Transaction;
import general.tables.User;

@Component("transactionDao")
public class TransactionDaoImpl extends GenericDaoImpl<Transaction> implements TransactionDao {
	public List<Transaction> getAllTransaction(){
		Query query = this.em
				.createQuery("select t FROM Transaction t");
		List<Transaction> transaction =  query.getResultList();
		return transaction;
	}
	
	public Transaction getByTransactionCode(String transactionCode)
	{
		Query query = this.em
                .createQuery("select t FROM Transaction t where t.transaction_code= :t_code");
        query.setParameter("t_code", transactionCode);   
    	Transaction t = (Transaction) query.getSingleResult();
    	return t;
	}

	@Override
	public List<Transaction> findAll(String condition) {
		String s = "SELECT t FROM Transaction t ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);
		List<Transaction> l =  query.getResultList();
		return l;
	}
}
