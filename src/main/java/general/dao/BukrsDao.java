package general.dao; 

import java.util.List;
import general.tables.Bukrs;

public interface BukrsDao extends GenericDao<Bukrs> {
	List<Bukrs> findAll();
}