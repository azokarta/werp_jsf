package general.dao;

import general.tables.ServFilterPremis;
import java.util.List;

public interface ServFilterPremisDao extends GenericDao<ServFilterPremis> {
	public List<ServFilterPremis> findAll();
	public List<ServFilterPremis> findAll(String condition);
	public List<ServFilterPremis> findAllByBukrsAndCountry(String in_bukrs, Long in_cId);
}
