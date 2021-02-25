package general.dao;

import general.output.tables.ServFilterOutput;
import general.tables.ServFilter;

import java.util.Date;
import java.util.List;

public interface ServFilterDao extends GenericDao<ServFilter> {
	public List<ServFilterOutput> findAllBySN(String in_bukrs, Long in_con_number, String in_tovSN);
	public List<ServFilterOutput> findAllByBranch(String in_bukrs, Long in_branch, int in_cat);
	public List<ServFilterOutput> findAllCurrentByDate(String in_bukrs, Long in_branch, Date in_date);
	public List<ServFilterOutput> findAllOverdueByDate(String in_bukrs, Long in_branch, Date in_date);
	public List<ServFilter> findAllByCRMCategory(String in_bukrs, Long in_branch, int in_crmCat);
	public ServFilter findByTovarSN(String in_bukrs, String in_tovSN);
	public ServFilter findByContractID(Long in_conId);
	public List<ServFilter> findAllForCurrentPlanByDate(String in_bukrs,Long in_branch, Date in_date);
	public List<ServFilter> findAllForOverduePlanByDate(String in_bukrs,Long in_branch, Date in_date);
}

