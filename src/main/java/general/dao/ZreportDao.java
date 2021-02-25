package general.dao;

import general.tables.Zreport;

import java.util.List;

public interface ZreportDao extends GenericDao<Zreport>{
	public List<Zreport> findAll();
	public List<Object[]> frep2Act(Long a_conNum) throws DAOException;
}
