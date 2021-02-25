package general.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.RelatedDocsDao;
import general.output.tables.RelatedDocsTree;
import general.tables.Invoice;
import general.tables.RelatedDocs;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("relatedDocsService")
public class RelatedDocsServiceImpl implements RelatedDocsService {

	@Autowired
	RelatedDocsDao rdDao;

	@Autowired
	InvoiceDao inDao;

	@Override
	public void create(RelatedDocs rd) throws DAOException {
		rdDao.create(rd);
	}

	@Override
	public void addChild(RelatedDocs parentDoc, Long childContextId, String childContext) {
		if (parentDoc == null) {
			throw new DAOException("Parent Related Doc Is NULL");
		}

		RelatedDocs rd = new RelatedDocs();
		rd.setContext(childContext);
		rd.setContext_id(childContextId);
		rd.setParent_id(parentDoc.getId());
		rd.setTree_id(parentDoc.getTree_id());
		rdDao.create(rd);
	}

	@Override
	public void addChildToParents(Long childContextId, String childContext, Map<String, List<Long>> parentDocs) {
		rdDao.delete(String.format(" context = '%s' AND context_id = %d ", childContext, childContextId));
		List<RelatedDocs> parentRelDocs = new ArrayList<RelatedDocs>();
		if (parentDocs != null && parentDocs.size() > 0) {
			for (Entry<String, List<Long>> e : parentDocs.entrySet()) {
				String tempContext = e.getKey();
				for (Long l : e.getValue()) {
					List<RelatedDocs> tempList = rdDao
							.findAll(String.format("context = '%s' AND context_id = %d ", tempContext, l));
					if (tempList.size() == 0) {
						RelatedDocs rd = new RelatedDocs(tempContext, l, 0L, l);
						rdDao.create(rd);
						parentRelDocs.add(rd);
					} else {
						parentRelDocs.addAll(tempList);
					}
				}
			}
		}

		for (RelatedDocs rd : parentRelDocs) {
			addChild(rd, childContextId, childContext);
		}
	}

	private void addChildsRecursively(Long parentId, TreeNode parentNode, Long currentId) {
		List<RelatedDocs> l = rdDao.findAll(" parent_id = " + parentId);
		for (RelatedDocs rd : l) {
			RelatedDocsTree rdt = new RelatedDocsTree();
			rdt.setActive(currentId.equals(rd.getId()));
			rdt.setContext(rd.getContext());
			rdt.setContextId(rd.getContext_id());
			rdt.setId(rd.getId());
			if (Invoice.CONTEXT.equals(rd.getContext())) {
				Invoice in = inDao.find(rd.getContext_id());
				if (in != null) {
					rdt.setDocumentName(in.getTypeName());
					rdt.setContextId(in.getReg_number());
				}
			}
			TreeNode t = new DefaultTreeNode(rdt, parentNode);
			addChildsRecursively(rd.getId(), t, currentId);
		}
	}

	@Override
	public TreeNode getRelatedTree(Long contextId, String context) {
		RelatedDocs rd = rdDao.find(contextId, context);
		if (rd != null) {
			List<RelatedDocs> tempList = rdDao
					.findAll(String.format(" tree_id = context_id AND tree_id = %d ", rd.getTree_id()));
			if (tempList.size() > 0) {
				RelatedDocs rdRoot = tempList.get(0);
				TreeNode root = new DefaultTreeNode("root", null);

				RelatedDocsTree rdt = new RelatedDocsTree();
				rdt.setContext(rdRoot.getContext());
				rdt.setId(rdRoot.getId());
				rdt.setContextId(rdRoot.getContext_id());
				rdt.setActive(rdRoot.getId().equals(rd.getId()));
				if (Invoice.CONTEXT.equals(rdRoot.getContext())) {
					Invoice in = inDao.find(rdRoot.getContext_id());
					if (in != null) {
						rdt.setDocumentName(in.getTypeName());
						rdt.setId(in.getReg_number());
					}
				}

				TreeNode tn = new DefaultTreeNode(rdt, root);
				addChildsRecursively(rdRoot.getId(), tn, rd.getId());
				root.getChildren().add(tn);

				return root;
			}
		}
		// TreeNode r = new DefaultTreeNode("Root",null);
		// RelatedDocsTree rdt = new RelatedDocsTree();
		// rdt.setContext("request");
		// new DefaultTreeNode(rdt,r);
		// return r;
		return null;
	}
}