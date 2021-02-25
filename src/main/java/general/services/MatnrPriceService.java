package general.services; 

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.MatnrPrice;

public interface MatnrPriceService{
	
	@Transactional
	public void create(MatnrPrice mp) throws DAOException;
	
	@Transactional
	public void update(MatnrPrice mp) throws DAOException;
}
