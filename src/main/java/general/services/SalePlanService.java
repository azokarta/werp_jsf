package general.services;

import general.dao.DAOException;
import general.tables.SalePlan;

import org.springframework.transaction.annotation.Transactional;

public interface SalePlanService {
	@Transactional
	void createSalePlan(SalePlan sp) throws DAOException;
	
	@Transactional
	void updateSalePlan(SalePlan sp) throws DAOException;
}
