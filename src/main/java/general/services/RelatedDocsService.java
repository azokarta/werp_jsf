package general.services;



import java.util.List;
import java.util.Map;

import general.dao.DAOException;
import general.tables.RelatedDocs;

import org.primefaces.model.TreeNode;
import org.springframework.transaction.annotation.Transactional;

public interface RelatedDocsService {
	
	@Transactional
	void create(RelatedDocs rd) throws DAOException;
	
	
	@Transactional
	void addChild(RelatedDocs parentDoc,Long childContextId,String childContext);
	
	@Transactional
	void addChildToParents(Long childContextId,String childContext,Map<String, List<Long>> parentDocs);
	
	TreeNode getRelatedTree(Long contextId,String context);
}