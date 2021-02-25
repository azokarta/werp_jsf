package general.services;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.Bseg;
import general.tables.Prebkpf;
import general.tables.Prebseg;

public interface PrebkpfService {
	
	@Transactional
	public void saveFinance(String a_bukrs,String a_prebkpf_id,Long a_userId) throws DAOException;
	
	@Transactional
	public void cancelStatus(String a_prebkpf_id,Long a_userId) throws DAOException;
	
	@Transactional
	public void save(Prebkpf a_prebkpf,List<Prebseg> al_prebseg) throws DAOException;
	

	@Transactional
	public boolean createPrebkpf(Bkpf a_bkpf, List<Bseg> wal_bseg) throws DAOException;
	
	//@Transactional
	//public void saveAEDocument(Prebkpf wa_pre,Map<String,ExchangeRate> l_er_map,Long a_userId) throws DAOException;
	
	//@Transactional
	//public void saveKPDocument(Prebkpf wa_pre,Map<String,ExchangeRate> l_er_map,Long a_userId) throws DAOException;
}
