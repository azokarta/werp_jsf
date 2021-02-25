package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.ServPacketPosDao;
import general.tables.ServPacketPos;
import org.springframework.stereotype.Component;

@Component("servPacketPosDao")
public class ServPacketPosDaoImpl extends GenericDaoImpl<ServPacketPos> implements ServPacketPosDao {
	
	@Override
	public List<ServPacketPos> findAllByServPacketID(Long a_spId) {
		List<ServPacketPos> resultSP = null;
		Query q = this.em.createQuery(String.format("SELECT s FROM ServPacketPos s WHERE s.sp_id = '%d'", a_spId));
		resultSP = q.getResultList();
		return resultSP;
	}
	
	@Override
	public List<ServPacketPos> dynamicFindAll(String wcl) {
		Query q = this.em.createQuery(String.format("SELECT s FROM ServPacketPos s WHERE " + wcl));
		List<ServPacketPos> results = q.getResultList();
		return results;
	}

}
