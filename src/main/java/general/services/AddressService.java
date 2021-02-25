package general.services;

import general.dao.DAOException;
import general.tables.Address;

import org.springframework.transaction.annotation.Transactional;

public interface AddressService {
	@Transactional
	boolean createAddress(Address addr, Long a_userid, String a_trcode) throws DAOException;
	
	@Transactional
	boolean updateAddress(Address addr, Long a_userid, String a_trcode) throws DAOException;
}
