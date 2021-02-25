package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.ServPacketWarDao;
import general.tables.ServPacketWar;

import org.springframework.stereotype.Component;

@Component("servPacketWarDao")
public class ServPacketWarDaoImpl extends GenericDaoImpl<ServPacketWar> implements ServPacketWarDao {

	public List<ServPacketWar> findAllByServPacketId(Long a_spId) {
		List<ServPacketWar> resultSP = null;
		Query q = this.em.createQuery(String.format("SELECT s FROM ServPacketWar s WHERE s.sp_id = '%d'", a_spId));
		resultSP = q.getResultList();
		return resultSP;
	}
	
	@Override
	public List<ServPacketWar> dynamicFindAll(String wcl) {
		Query q = this.em.createQuery(String.format("SELECT s FROM ServPacketWar s WHERE " + wcl));
		List<ServPacketWar> results = q.getResultList();
		return results;
	}
	
}
