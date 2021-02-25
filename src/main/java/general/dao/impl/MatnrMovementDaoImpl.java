package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.MatnrMovementDao;
import general.dao.StaffDao;
import general.tables.MatnrMovement;
import general.tables.Staff;

@Component("matnrMovementDao")
public class MatnrMovementDaoImpl extends GenericDaoImpl<MatnrMovement>
		implements MatnrMovementDao {

	@Autowired
	StaffDao stfDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<MatnrMovement> findAll(String condition) {
		String s = " SELECT m FROM MatnrMovement m ";
		if (condition.length() > 0) {
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}

	@Override
	public MatnrMovement find(Object id) {
		// TODO Auto-generated method stub
		MatnrMovement mm = super.find(id);
		if (mm != null) {
			if (Validation.isEmptyLong(mm.getStaff_id())) {
				mm.setResponsible(new Staff());
			} else {
				Staff stf1 = stfDao.find(mm.getStaff_id());
				if (stf1 == null) {
					mm.setResponsible(new Staff());
				} else {
					mm.setResponsible(stf1);
				}
			}

			if (Validation.isEmptyLong(mm.getReceived_by())) {
				mm.setReceiver(new Staff());
			} else {
				Staff stf2 = stfDao.find(mm.getReceived_by());
				if (stf2 == null) {
					mm.setReceiver(new Staff());
				} else {
					mm.setReceiver(stf2);
				}
			}
		}

		return mm;
	}
}
