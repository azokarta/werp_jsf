package general.dao;

import java.util.List;

import general.tables.Matnr;
import general.tables.MatnrBukrs;

public interface MatnrBukrsDao extends GenericDao<MatnrBukrs> {
	List<MatnrBukrs> findAllByBukrs(String in_bukrs);
	
}
