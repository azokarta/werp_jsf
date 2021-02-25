package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.CurrencyDao; 
import general.tables.Currency;
@Component("currencyDao")
public class CurrencyDaoImpl extends GenericDaoImpl<Currency> implements CurrencyDao{

	@Override
	public List<Currency> findAll(String s) {
		Query q = this.em.createQuery("SELECT c FROM Currency c WHERE c.currency LIKE :s");
		q.setParameter("s", "%" + s + "%");
		return q.getResultList();
	}

	@Override
	public List<Currency> findAll() {
		Query q = this.em.createQuery("SELECT c FROM Currency c");
		return q.getResultList();
	}

	@Override
	public Currency findByCurrency(String s) {
		Query q = this.em.createQuery("SELECT c FROM Currency c WHERE c.currency = :s");
		q.setParameter("s", s);
		List<Currency> l = q.getResultList();
		if(l.size() > 0){
			return l.get(0);
		}
		return null;
	}
	
	@Override
	public List<Currency> findAllWithCountries() {
		Query q = this.em.createQuery("SELECT cr FROM Currency cr, Country cn where cr.currency_id = cn.currency_id");
		return q.getResultList();
	}
	
}
