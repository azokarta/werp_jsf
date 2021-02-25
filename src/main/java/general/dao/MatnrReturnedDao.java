package general.dao;

import java.util.List;

import general.tables.MatnrReturned;

public interface MatnrReturnedDao extends GenericDao<MatnrReturned> {
	public List<MatnrReturned> findAll(String condition);
}
