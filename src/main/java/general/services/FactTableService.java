package general.services;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;

public interface FactTableService {
	@Transactional
	void incrementCounter(Long dealerId,Long fitterId, Long secretaryId) throws DAOException;
	
	@Transactional
	void incrementCounter(Long staffId, Double sum) throws DAOException; //Для взонсщиков
	
	@Transactional
	void decrementCounter(Long staffId) throws DAOException;

	public void setYear(int year) throws DAOException;
	
	public int getYear() throws DAOException;
	
	public void setMonth(int month) throws DAOException;
	
	public int getMonth() throws DAOException;
	
	
	/**
	 * NEW METHODS
	 */
	
	@Transactional
	void addSale(Long sellerId, Long secretaryId) throws DAOException; //Для продаж
	
	@Transactional
	void addService(Long staffId,Double sum) throws DAOException; // Для сервиса
	
	@Transactional
	void addPayment(Long staffId,Long branchId, Long positionId,Long businessAreaId, String waers, Double sum, boolean b) throws DAOException; //Для взнос.
}
