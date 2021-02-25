package general.dao;

import java.util.List;

import general.tables.RevItem;

public interface RevItemDao extends GenericDao<RevItem>{
	public List<RevItem> findAll(String condition);
}
