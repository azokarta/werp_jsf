package general.dao;

import java.util.Date;
import java.util.List;

import general.output.tables.ServFilterOutput;
import general.tables.ServFilter;
import general.tables.ServFilterVC;

public interface ServFilterVCDao extends GenericDao<ServFilterVC>{
	public List<ServFilterOutput> findAllBySN(String in_bukrs, Long in_con_number, String in_tovSN);
	public List<ServFilterOutput> findAllByBranch(String in_bukrs, Long in_branch, int in_cat);
	public List<ServFilterOutput> findAllCurrentByDate(String in_bukrs, Long in_branch, Date in_date, int warType);	
	
	public List<ServFilterOutput> findAllOverdueByDate(String in_bukrs, Long in_branch, Date in_date, int warType);
	
	public List<ServFilterVC> findAllByCRMCategory(String in_bukrs, Long in_branch, int in_crmCat);
	public ServFilterVC findByTovarSN(String in_bukrs, String in_tovSN);
	public ServFilterVC findByContractID(Long in_conId);
	public List<ServFilterVC> findAllForCurrentPlanByDate(String in_bukrs,Long in_branch, Date in_date);
	public List<ServFilterVC> findAllForOverduePlanByDate(String in_bukrs,Long in_branch, Date in_date);
}
