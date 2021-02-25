package general.dao;

import java.util.List;
import general.tables.AddressType;

public interface AddressTypeDao extends GenericDao<AddressType> {
	public List<AddressType> findAll();
	public List<AddressType> findAll(String condition);

}
