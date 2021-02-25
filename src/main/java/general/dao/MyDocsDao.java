package general.dao;

import java.util.List;

import general.tables.MyDocs;

public interface MyDocsDao extends GenericDao<MyDocs> {
	public List<MyDocs> findAll(String condition);
	
	public List<MyDocs> findAllRequest(String condition);
}
