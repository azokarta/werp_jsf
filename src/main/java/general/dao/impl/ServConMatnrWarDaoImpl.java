package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.ServConMatnrWarDao;
import general.tables.ServConMatnrWar;

@Component("servContrMatnrWar")
public class ServConMatnrWarDaoImpl extends GenericDaoImpl<ServConMatnrWar> implements ServConMatnrWarDao {
	
	@Override
	public List<ServConMatnrWar> findAll() {
		Query query = this.em
				.createQuery("select m FROM ServConMatnrWar m"); 
		List<ServConMatnrWar> l_mw = query.getResultList();
		return l_mw;
	}
	
	@Override
	public List<ServConMatnrWar> findAll(String condition) {
		String s = " SELECT m FROM ServConMatnrWar m WHERE " + condition;
		Query query = this.em
				.createQuery(s); 
		List<ServConMatnrWar> l_mw = query.getResultList();
		return l_mw;
	}
	
	@Override
	public List<ServConMatnrWar> findAllByContractId(Long conId) {
		String s = " SELECT m FROM ServConMatnrWar m WHERE m.contract_id = " + conId;
		Query query = this.em
				.createQuery(s); 
		List<ServConMatnrWar> l_mw = query.getResultList();
		return l_mw;
	}
	
	@Override
	public List<ServConMatnrWar> findAllByServId(Long servId) {
		String s = " SELECT m FROM ServConMatnrWar m WHERE m.serv_id = " + servId;
		Query query = this.em
				.createQuery(s); 
		List<ServConMatnrWar> l_mw = query.getResultList();
		return l_mw;
	}


}
