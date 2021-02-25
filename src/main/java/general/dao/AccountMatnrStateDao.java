package general.dao;

import java.util.List;

import general.tables.AccountMatnrState;

public interface AccountMatnrStateDao extends GenericDao<AccountMatnrState>{
	public List<AccountMatnrState> findAll(String condition);
}
