package general.services;

import general.dao.DAOException;
import general.tables.ServPacket;
import general.tables.ServPacketPos;
import general.tables.ServPacketWar;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import user.User;

public interface ServPacketService {
	@Transactional
	boolean createServPacket(ServPacket a_sp, List<ServPacketPos> a_spPosL, List<ServPacketWar> a_spWarL, User userData, String a_trCode, Long a_trId) throws DAOException;
	
	@Transactional
	boolean updateServPacket(ServPacket a_sp, List<ServPacketPos> a_spPosL, List<ServPacketWar> a_spWarL, User userData, String a_trCode, Long a_trId) throws DAOException;
}
