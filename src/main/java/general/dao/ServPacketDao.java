package general.dao;

import java.util.List;

import general.tables.ServPacket;

public interface ServPacketDao extends GenericDao<ServPacket> {
	List<ServPacket> findAllByTovarID(String a_bukrs, Long a_countryId, Long a_tovarId);
	List<ServPacket> dynamicFindAll(String wcl);	
}
