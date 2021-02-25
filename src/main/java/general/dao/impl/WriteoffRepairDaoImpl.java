package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.Validation;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.WriteoffRepairDao;
import general.tables.WriteoffRepair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component("writeoffRepairDao")
public class WriteoffRepairDaoImpl extends GenericDaoImpl<WriteoffRepair> implements WriteoffRepairDao{

	@Autowired MatnrListDao mlDao;
	
	@Autowired MatnrDao mDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public WriteoffRepair findWithDetail(Long id) {
		String s = " SELECT w FROM WriteoffRepair w LEFT JOIN fetch w.writeoffRepairItems i LEFT JOIN w.matnrObject mo ";
		s += " WHERE w.id = " + id;
		Query q = em.createQuery(s);
		List<WriteoffRepair> l = q.getResultList();
		if (l != null && l.size() > 0) {
			WriteoffRepair wr = l.get(0);
			return wr;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WriteoffRepair> findAll(String cond) {
		String s = " SELECT w FROM WriteoffRepair w ";
		if(!Validation.isEmptyString(cond)){
			s += " WHERE " + cond;
		}
		
		Query q = em.createQuery(s);
		return q.getResultList();
	}
	
}
