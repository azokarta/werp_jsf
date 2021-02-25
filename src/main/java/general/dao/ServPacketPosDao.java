package general.dao;

import java.util.List;

import general.tables.ServPacketPos;

public interface ServPacketPosDao extends GenericDao<ServPacketPos>{
	List<ServPacketPos> findAllByServPacketID(Long a_spId);
	List<ServPacketPos> dynamicFindAll(String wcl);
}
