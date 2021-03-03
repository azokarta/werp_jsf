package general.dao;

import java.util.List;

import general.tables.MatnrList;

public interface MatnrListDao extends GenericDao<MatnrList> {
	public List<MatnrList> dynamicFind(String a_dynamicWhere);
	public List<MatnrList> dynamicFind2(String a_dynamicWhere);
	public List<MatnrList> dynamicFind3(String a_dynamicWhere);
	void updateStatus(String status,String condition) throws DAOException;
	void updateWerks(Long newWerks,String condition) throws DAOException;
	void updateMatnrs(String query) throws DAOException;
	
	public int findCount(Long matnr, Long werks);
	
	public List<MatnrList> findAll(String cond, int max);
	
	public List<MatnrList> findAll(String cond);
	
	public List<MatnrList> findAllWithStaff(String cond);
	
	public List<MatnrList> getGrouppedList(String condition);
	
	public List<MatnrList> getGrouppedListForService(String condition);
	
	public int updateMatnrListFkage(Long a_matnr,double a_summa, Long a_awkey, Long a_invoice_id, int a_rows) throws DAOException;
	
	
	public List<MatnrList> findAllAccountability(String cond);
	
	public List<MatnrList> findAllSold(String cond, int max);
	
	public List<MatnrList> findAllInWerks(Long werks,String status);
	
	public MatnrList findByBarcode(String barcode);
	
	public List<MatnrList> findStaffAllMatnrList(Long staffId);
	
	public List<MatnrList> findStaffMatnrListByMatnrWerksStatus(Long staffId, Long matnr, List<String> werksIds,String status);
	
	public MatnrList findOneByBarcodeWerksStatus(String barcode,List<String> werksIds,String status);

	List<MatnrList> batchInsert(List<MatnrList> matnrLists);
}
