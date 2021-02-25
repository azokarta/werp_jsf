package general.dao;

import general.tables.ServiceTable;

import java.util.Date;
import java.util.List;

import service.ServiceTableOutput;

public interface ServiceDao extends GenericDao<ServiceTable> {
	public ServiceTable findByNumber(Long a_number);
	public ServiceTable findByAwkey(Long a_awkey, String a_bukrs);
	public List<ServiceTable> findAll();
	public List<ServiceTable> dynamicFindAll(String wcl);
	public ServiceTable dynamicFindSingle(String wcl);
	public List<ServiceTableOutput> findAll(String condition, int first, int max);
	public int getRowCount(String condition);
	public List<ServiceTable> findAllInCurrentMonthByType(Long inBranch, Date inDate, int inType);
}
