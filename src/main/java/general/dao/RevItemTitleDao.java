package general.dao;

import java.util.List;

import general.tables.RevItemTitle;

public interface RevItemTitleDao extends GenericDao<RevItemTitle>{
	
	public List<RevItemTitle> findAll(String cond);
}
