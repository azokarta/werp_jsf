package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.AddressDao;
import general.tables.Address;

import org.springframework.stereotype.Component;

@Component("addressDao")
public class AddressDaoImpl extends GenericDaoImpl<Address> implements AddressDao{
	public List<Address> findAll() {
		Query query = this.em
				.createQuery("select a FROM Address a"); 
		List<Address> la = query.getResultList();
		return la;
	}
	
	@Override
	public List<Address> findAll(String condition) {
		String s = " SELECT a FROM Address a WHERE " + condition;
		Query query = this.em
				.createQuery(s); 
		List<Address> la = query.getResultList();
		return la;
	}		
	
	@Override
	public List<Address> findAllByCustomerId(Long cusId) {
		String s = " SELECT a FROM Address a WHERE a.customerId = " + cusId;
		Query query = this.em
				.createQuery(s); 
		List<Address> la = query.getResultList();
		return la;
	}
}
