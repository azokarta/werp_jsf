package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.ServPacketDao;
import general.tables.ServPacket;

import org.springframework.stereotype.Component;

@Component("servPacketDao")
public class ServPacketDaoImpl extends GenericDaoImpl<ServPacket> implements
		ServPacketDao {

	@Override
	public List<ServPacket> findAllByTovarID(String a_bukrs, Long a_countryId,
			Long a_tovarId) {
		List<ServPacket> resultSP = null;
		Query q = this.em
				.createQuery(String
						.format("SELECT s FROM ServPacket s WHERE s.bukrs = '%s' and s.country_id = '%d' and s.tovar_id = '%d' and s.active=1",
								a_bukrs, a_countryId, a_tovarId));
		resultSP = q.getResultList();
		return resultSP;
	}

	@Override
	public List<ServPacket> dynamicFindAll(String wcl) {
		Query q = this.em.createQuery(String
				.format("SELECT s FROM ServPacket s WHERE " + wcl));
		List<ServPacket> results = q.getResultList();
		return results;
	}

}
