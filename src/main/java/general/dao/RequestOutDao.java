package general.dao;

import java.util.List;

import general.tables.RequestOut;

public interface RequestOutDao extends GenericDao<RequestOut> {
	public List<RequestOut> findAll(String condition);
}
