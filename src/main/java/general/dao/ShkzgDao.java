package general.dao;

import general.tables.Shkzg;

import java.util.List;

public interface ShkzgDao extends GenericDao<Shkzg>{
	public List<Shkzg> findAll();
}
