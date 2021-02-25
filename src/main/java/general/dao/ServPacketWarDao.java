package general.dao;

import general.tables.ServPacketWar;

import java.util.List;

public interface ServPacketWarDao extends GenericDao<ServPacketWar> {
	List<ServPacketWar> findAllByServPacketId(Long a_spId);
	List<ServPacketWar> dynamicFindAll(String wcl);
}
