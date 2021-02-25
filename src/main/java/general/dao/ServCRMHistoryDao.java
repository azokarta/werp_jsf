package general.dao;

import general.tables.ServCRMHistory;

import java.util.List;

public interface ServCRMHistoryDao extends GenericDao<ServCRMHistory> {
	public List<ServCRMHistory> findAll();
	public List<ServCRMHistory> findAll(String condition);
	public List<ServCRMHistory> findAllByContractID(Long conId);
	public ServCRMHistory findByServID(Long servId);
}
