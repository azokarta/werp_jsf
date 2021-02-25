package general.dao;

import general.tables.ServCRMAction;

import java.util.List;

public interface ServCRMActionDao {
	public List<ServCRMAction> findAll();
	public List<ServCRMAction> findAll(String condition);
}
