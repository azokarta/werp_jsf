package general.dao;

import general.tables.ServFilterArchive;

import java.util.Date;
import java.util.List;

public interface ServFilterArchiveDao extends GenericDao<ServFilterArchive> {
	public List<ServFilterArchive> findAllByMonth(String in_bukrs, Date in_date);
	public List<ServFilterArchive> findByTovarSN(String in_bukrs, String in_tovSN);
	public List<ServFilterArchive> findAllByServId(Long a_servId);
	public ServFilterArchive findPrevFNOByTovarSN(String in_bukrs, String in_tovSN, int a_fno, Long last_servId);
}
