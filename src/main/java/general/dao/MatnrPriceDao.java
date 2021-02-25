package general.dao;

import java.util.List;
import general.tables.MatnrPrice;

public interface MatnrPriceDao extends GenericDao<MatnrPrice>{
	public List<MatnrPrice> findAll(String condition);
	
	public MatnrPrice findLastPrice(String bukrs, Long countryId, Long customerId,Long matnr);
}
