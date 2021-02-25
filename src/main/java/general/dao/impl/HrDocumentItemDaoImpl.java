package general.dao.impl;

import general.dao.HrDocumentItemDao;
import general.dao.StaffDao;
import general.tables.HrDocumentItem;
import general.tables.Staff;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("hrDocumentItemDao")
public class HrDocumentItemDaoImpl extends GenericDaoImpl<HrDocumentItem>
		implements HrDocumentItemDao {

	@Autowired
	StaffDao stfDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocumentItem> findAll(String cond) {
		String s = "SELECT h FROM HrDocumentItem h ";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}


}
