package general.dao;

import java.util.List;

import general.tables.Address;

public interface AddressDao extends GenericDao<Address> {
	public List<Address> findAll();
	public List<Address> findAll(String condition);
	public List<Address> findAllByCustomerId(Long cusId);
}