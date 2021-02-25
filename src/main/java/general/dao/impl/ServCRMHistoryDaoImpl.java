package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.ServCRMHistoryDao;
import general.tables.ServCRMHistory;

import org.springframework.stereotype.Component;

@Component("servCRMHistoryDao")
public class ServCRMHistoryDaoImpl extends GenericDaoImpl<ServCRMHistory> implements ServCRMHistoryDao {
	
	@Override
	public List<ServCRMHistory> findAll() {
		Query query = this.em
				.createQuery("select a FROM ServCRMHistory a");  
		List<ServCRMHistory> la = query.getResultList();
		return la;
	}
	
	@Override
	public List<ServCRMHistory> findAll(String condition) {
		String s = " SELECT a FROM ServCRMHistory a WHERE " + condition;
		Query query = this.em
				.createQuery(s); 
		List<ServCRMHistory> la = query.getResultList();
		return la;
	}	
	
	@Override
	public List<ServCRMHistory> findAllByContractID(Long conId) {
		String s = " SELECT a FROM ServCRMHistory a WHERE a.contractId = " + conId
				+ " order by a.actionDate DESC";
		Query query = this.em
				.createQuery(s); 
		List<ServCRMHistory> la = query.getResultList();
		return la;
	}
	
	@Override
	public ServCRMHistory findByServID(Long servId) {
		String s = " SELECT a FROM ServCRMHistory a WHERE a.serviceId = " + servId;
		Query query = this.em
				.createQuery(s); 
		List<ServCRMHistory> la = query.getResultList();
		if (la.size() > 0) return la.get(0);
		return null;		
	}
}
