package general.dao;

import java.util.List;

import general.tables.BusinessArea;

public interface BusinessAreaDao extends GenericDao<BusinessArea> {
	public List<BusinessArea> findAll();
}
