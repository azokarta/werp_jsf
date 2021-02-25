package general.dao;

import java.util.List;

import general.tables.FactTable;

public interface FactTableDao extends GenericDao<FactTable> {
	List<FactTable> findAll(String condition);
}
