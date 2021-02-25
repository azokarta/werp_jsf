package general.services; 

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.MatnrPriceArc;

public interface MatnrPriceArcService{
	
	@Transactional
	public void create(MatnrPriceArc mp) throws DAOException;
}
