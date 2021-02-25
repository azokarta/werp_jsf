package general.dao;

import general.tables.ServicePos;
import general.tables.ServiceTable;

import java.util.List;

public interface ServPosDao extends GenericDao<ServicePos> {
	public List<ServicePos> findAllByServNumber(Long a_servNum);
	public List<ServicePos> findAllByServID(Long a_servId);
	public List<ServicePos> dynamicFindAll(String wcl);	
}
