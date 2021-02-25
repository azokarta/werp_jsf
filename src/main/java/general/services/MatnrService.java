package general.services; 

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.MatnrWar;

public interface MatnrService{
	@Transactional
	public void create(Matnr m,List<Long> catIds, MatnrWar mw) throws DAOException;
	
	@Transactional
	public void update(Matnr m,List<Long> catIds, MatnrWar mw,MatnrWar oldMatnr, Long userId) throws DAOException;
	
	public List<MatnrList> getMatnrListWithMatnr(String condition, String bukrs);
	
	//public void prepareMatnrData()
}
