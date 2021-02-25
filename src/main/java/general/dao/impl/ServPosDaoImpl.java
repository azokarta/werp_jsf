package general.dao.impl;

import general.dao.ServPosDao;
import general.dao.ServiceDao;
import general.tables.ServicePos;
import general.tables.ServiceTable;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("servPosDao")
public class ServPosDaoImpl extends GenericDaoImpl<ServicePos> implements ServPosDao {

	@Autowired
	ServiceDao sDao;
	
	@Override
	public List<ServicePos> findAllByServNumber(Long a_servNum) {
		ServiceTable p_service = sDao.findByNumber(a_servNum);
		Query q = this.em.createQuery(String.format("SELECT s FROM ServicePos s WHERE s.serv_id = '%d'", p_service.getId()));
		List<ServicePos> resultSP = q.getResultList();
		return resultSP;
	}
	
	public List<ServicePos> findAllByServID(Long a_servId) {
		List<ServicePos> resultSP = null;
		Query q = this.em.createQuery(String.format("SELECT s FROM ServicePos s WHERE s.serv_id = '%d'", a_servId));
		resultSP = q.getResultList();
		return resultSP;
	}
	
	
	@Override
	public List<ServicePos> dynamicFindAll(String wcl) {
		Query q = this.em.createQuery(String.format("SELECT s FROM ServicePos s WHERE " + wcl));
		List<ServicePos> results = q.getResultList();
		return results;
	}
}
