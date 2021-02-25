package general.dao;

import java.util.List;

import general.output.tables.ServAppReportOutput;
import general.tables.ServiceApplication;

public interface ServiceApplicationDao  extends GenericDao<ServiceApplication> {

	public ServiceApplication findByNumber(Long a_number);
	public List<ServiceApplication> findAll();
	public List<ServiceApplication> dynamicFindAll(String wcl);
	public List<ServiceApplication> findAll(String condition, int first, int max);
	public int getRowCount(String condition);
	public List<ServAppReportOutput> getSAReportBranch(String queryScript, String table) throws DAOException;
	public List<ServAppReportOutput> getSAReportStaff(String queryScript, String table) throws DAOException;
	
	public List<Object[]> dynamicSerrep1(String a_dynamicWhere) throws DAOException;
}
