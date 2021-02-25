package general.dao.impl;

import general.Validation;
import general.dao.HrDocumentDao;
import general.dao.HrDocumentItemDao;
import general.dao.HrDocumentRouteDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.tables.HrDocument;
import general.tables.HrDocumentItem;
import general.tables.HrDocumentRoute;
import general.tables.Staff;
import general.tables.User;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("hrDocumentDao")
public class HrDocumentDaoImpl extends GenericDaoImpl<HrDocument> implements HrDocumentDao {

	@Autowired
	StaffDao stfDao;

	@Autowired
	HrDocumentItemDao hdItemDao;

	@Autowired
	HrDocumentRouteDao hdRouteDao;

	@Autowired
	UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocument> findAll(String cond) {
		String s = " SELECT h FROM HrDocument h ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public HrDocument findWithDetail(Long id) {
		HrDocument doc = super.find(id);
		if (doc != null) {
			Map<Long, Staff> stfMap = stfDao.getMappedList("");

			List<HrDocumentItem> items = hdItemDao.findAll(" hr_document_id = " + doc.getId());
			for (HrDocumentItem it : items) {
				it.setStaff(stfMap.get(it.getStaffId()));
				it.setManager(stfMap.get(it.getNewManagerId()));
				it.setHrDocument(doc);
			}
			doc.setItems(items);

			List<HrDocumentRoute> routes = hdRouteDao.findAll(" hr_document_id =  " + doc.getId());

			for (HrDocumentRoute r : routes) {
				r.setStaff(stfMap.get(r.getStaffId()));
				r.setHrDocument(doc);
			}
			doc.setRoutes(routes);

			User user = userDao.findWithStaff(doc.getCreatedBy());
			if (user != null) {
				doc.setCreator(user.getStaff());
			}

			User user2 = userDao.findWithStaff(doc.getResponsibleId());
			if (user2 != null) {
				doc.setResponsible(user2.getStaff());
			}
		}

		return doc;
	}

	@Override
	public int getRowCount(String condition) {
		String s = " SELECT COUNT(id) FROM HrDocument ";
		if (!Validation.isEmptyString(condition)) {
			s += " WHERE " + condition;
		}

		Query query = this.em.createQuery(s);
		return ((Long) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocument> findAllLazy(String cond, int first, int pageSize) {
		String s = " SELECT d FROM HrDocument d ";
		if (!Validation.isEmptyString(cond)) {
			s += " WHERE " + cond;
		}
		Query q = this.em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		List<HrDocument> l = q.getResultList();
		return l;
	}
}