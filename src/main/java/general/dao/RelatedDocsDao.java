package general.dao;

import java.util.List;

import general.tables.RelatedDocs;

public interface RelatedDocsDao extends GenericDao<RelatedDocs> {
	public List<RelatedDocs> findAll(String condition);
	
	public void delete(Long parentId,String context,Long contextId);
	
	public RelatedDocs findParent(Long contextId,String context,String parentContext);
	
	public void delete(String cond);
	
	public RelatedDocs find(Long contextId,String context);
	
	public String[] findChildContextIds(Long contextId,String context, String childContext);
	
	public String[] findRootAllChildContextIds(Long contextId,String context, String childContext);
}
