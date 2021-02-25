package general.dao;

import java.util.List;

import general.tables.HrDocument;

public interface HrDocumentDao extends GenericDao<HrDocument>{
	public List<HrDocument> findAll(String cond);
	
	HrDocument findWithDetail(Long id);
	
	public int getRowCount(String condition);
	
	public List<HrDocument> findAllLazy(String cond,int first, int pageSize);
}
