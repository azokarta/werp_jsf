package general.dao.impl;

import general.dao.RelatedDocsDao;
import general.tables.RelatedDocs;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("relatedDocsDao")
public class RelatedDocsDaoImpl extends GenericDaoImpl<RelatedDocs> implements
		RelatedDocsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RelatedDocs> findAll(String condition) {
		String s = " SELECT r FROM RelatedDocs r ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}

		Query q = em.createQuery(s);

		return q.getResultList();
	}

	@Override
	public void delete(Long parentId, String context, Long contextId) {
		em.createQuery(
				" DELETE FROM RelatedDocs WHERE parent_id = :parent_id AND context=:context AND context_id=:context_id")
				.setParameter("parent_id", parentId)
				.setParameter("context", context)
				.setParameter("context_id", contextId).executeUpdate();
	}

	@Override
	public RelatedDocs findParent(Long contextId, String context,
			String parentContext) {
		List<RelatedDocs> l = findAll(String.format(
				" context = '%s' AND context_id = %d ", context, contextId));
		if (l.size() > 0) {
			return find(l.get(0).getParent_id());
		}
		return null;
	}

	@Override
	public void delete(String cond) {
		if (cond.length() > 0) {
			Query q = em.createQuery("DELETE FROM RelatedDocs WHERE " + cond);
			q.executeUpdate();
		}
	}

	@Override
	public RelatedDocs find(Long contextId, String context) {
		List<RelatedDocs> l = findAll(String.format(
				" context = '%s' AND context_id = %d ", context, contextId));
		if(l.size() > 0){
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String[] findChildContextIds(Long contextId, String context, String childContext) {
		String sub = " SELECT id FROM RelatedDocs WHERE context_id = " + contextId + " AND context = '" + context + "' ";
		String s = " SELECT rd FROM RelatedDocs rd WHERE parent_id IN(" + sub + ") AND context = '" + childContext + "' ";
		Query q = em.createQuery(s);
		List<RelatedDocs> l = q.getResultList();
		String[] out = null;
		if(l.size() > 0){
			out = new String[l.size()];
			for(int k = 0; k < l.size(); k++){
				out[k] = l.get(k).getContext_id().toString();
			}
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String[] findRootAllChildContextIds(Long contextId, String context, String childContext) {
		String sub = " SELECT tree_id FROM RelatedDocs WHERE context_id = " + contextId + " AND context = '" + context + "' ";
		String s = " SELECT rd FROM RelatedDocs rd WHERE tree_id IN(" + sub + ") AND context = '" + childContext + "' ";
		Query q = em.createQuery(s);
		List<RelatedDocs> l = q.getResultList();
		String[] out = null;
		if(l.size() > 0){
			out = new String[l.size()];
			for(int k = 0; k < l.size(); k++){
				out[k] = l.get(k).getContext_id().toString();
			}
		}

		return out;
	}

}
