package general.dao;

import java.util.List;
import general.tables.MatnrPriceArc;

public interface MatnrPriceArcDao extends GenericDao<MatnrPriceArc>{
	public List<MatnrPriceArc> findAll(String condition);
}
