package general.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import general.Validation;
import general.dao.DemoDao;
import general.tables.Demonstration;
import general.tables.search.DemoSearch;

import org.springframework.stereotype.Component;

@Component("demoDao")
public class DemoDaoImpl extends GenericDaoImpl<Demonstration>implements DemoDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Demonstration> findAll(String condition) {
		String q = " SELECT d FROM Demonstration d ";
		if (condition.length() > 0) {
			q += " WHERE " + condition;
		}
		Query query = this.em.createQuery(q);
		List<Demonstration> l = query.getResultList();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Demonstration> findAllLazy(String cond, int first, int pageSize) {
		String s = "SELECT d FROM Demonstration d JOIN FETCH d.dealerStaff s1 JOIN FETCH d.creatorStaff s2 "
				+ " JOIN FETCH d.demosecStaff s3 ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		return q.getResultList();
	}

	@Override
	public int getRowCount(String cond) {
		String s = "SELECT COUNT(d.id) FROM Demonstration d ";
		// if (!Validation.isEmptyString(cond)) {
		// s += " WHERE " + cond;
		// }
		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Demonstration> findAllByCriteriaLazy(DemoSearch searchModel, String orderByField, String orderByDir,
			int first, int pageSize) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Demonstration> criteriaQuery = criteriaBuilder.createQuery(Demonstration.class);
		Root<Demonstration> root = criteriaQuery.from(Demonstration.class);
		root.fetch("dealerStaff", JoinType.LEFT);
		root.fetch("demosecStaff", JoinType.LEFT);
		criteriaQuery.select(root).distinct(true);
		if ("DESC".equals(orderByDir)) {
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get(orderByField)));
		} else {
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get(orderByField)));
		}

		Predicate criteria = getPreparedPredicate(searchModel, criteriaBuilder, root);

		criteriaQuery.where(criteria);
		Query q = em.createQuery(criteriaQuery);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);

		return q.getResultList();
	}

	private Predicate getPreparedPredicate(DemoSearch searchModel, CriteriaBuilder criteriaBuilder,
			Root<Demonstration> root) {
		Predicate criteria = criteriaBuilder.conjunction();
		if (!Validation.isEmptyString(searchModel.getCustomerName())) {
			Predicate p = criteriaBuilder.like(criteriaBuilder.lower(root.get("customerName")),
					"%" + searchModel.getCustomerName().toLowerCase() + "%");
			criteria = criteriaBuilder.and(criteria, p);
		}

		if (!Validation.isEmptyString(searchModel.getCustomerMobile())) {
			Predicate p = criteriaBuilder.like(criteriaBuilder.lower(root.get("customerMobile")),
					searchModel.getCustomerMobile().toLowerCase() + "%");
			criteria = criteriaBuilder.and(criteria, p);
		}

		if (!Validation.isEmptyLong(searchModel.getBranchId())) {
			Predicate p = criteriaBuilder.equal(root.get("branchId"), searchModel.getBranchId());
			criteria = criteriaBuilder.and(criteria, p);
		}

		if (searchModel.getFromDate() != null) {
			Predicate p = criteriaBuilder.greaterThanOrEqualTo(root.get("dateTime"), searchModel.getFromDate());
			criteria = criteriaBuilder.and(criteria, p);
		}

		if (searchModel.getToDate() != null) {
			Predicate p = criteriaBuilder.lessThanOrEqualTo(root.get("dateTime"), searchModel.getToDate());
			criteria = criteriaBuilder.and(criteria, p);
		}

		if (searchModel.getStatusId() != null && searchModel.getStatusId() > 0) {
			Predicate p = criteriaBuilder.equal(root.get("statusId"), searchModel.getStatusId());
			criteria = criteriaBuilder.and(criteria, p);
		}

		if (!Validation.isEmptyCollection(searchModel.getBranchIds())) {
			Expression<String> expression = root.get("branchId");
			Predicate p = expression.in(searchModel.getBranchIds());
			criteria = criteriaBuilder.and(criteria, p);
		}

		return criteria;
	}

	@Override
	public int getRowCountByCriteria(DemoSearch searchModel) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Demonstration> root = criteriaQuery.from(Demonstration.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(root));

		Predicate criteria = getPreparedPredicate(searchModel, criteriaBuilder, root);
		criteriaQuery.where(criteria);
		Query q = em.createQuery(criteriaQuery);

		return ((Long) q.getSingleResult()).intValue();
	}
}
