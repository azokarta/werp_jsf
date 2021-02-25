package general.dao.impl;

import general.dao.HrDocApproverDao;
import general.dao.StaffDao;
import general.tables.HrDocApprover;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("hrDocApproverDao")
public class HrDocApproverDaoImpl extends GenericDaoImpl<HrDocApprover>
		implements HrDocApproverDao {

	@Autowired
	StaffDao stfDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocApprover> findAll(String cond) {
		String s = "SELECT h FROM HrDocApprover h ";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}


}
